package com.hanyans.gachacounter.logic;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.hanyans.gachacounter.MainApp;
import com.hanyans.gachacounter.core.AppUpdateMessage;
import com.hanyans.gachacounter.core.PopupMessage;
import com.hanyans.gachacounter.core.Version;
import com.hanyans.gachacounter.core.task.ConsumerTask;
import com.hanyans.gachacounter.core.task.RunnableTask;
import com.hanyans.gachacounter.core.task.TrackableTask;
import com.hanyans.gachacounter.logic.task.AppUpdateCheckTask;
import com.hanyans.gachacounter.logic.task.CounterTask;
import com.hanyans.gachacounter.logic.task.GachaCounterTask;
import com.hanyans.gachacounter.logic.task.HistoryRetrieverTask;
import com.hanyans.gachacounter.logic.task.UpdateDataTask;
import com.hanyans.gachacounter.logic.task.UrlGrabberTask;
import com.hanyans.gachacounter.model.BannerHistory;
import com.hanyans.gachacounter.model.GameGachaData;
import com.hanyans.gachacounter.model.count.BannerReport;
import com.hanyans.gachacounter.model.count.GachaReport;
import com.hanyans.gachacounter.model.preference.UserPreference;
import com.hanyans.gachacounter.model.rateup.BannerEventHistory;
import com.hanyans.gachacounter.storage.LoadReport;
import com.hanyans.gachacounter.storage.Storage;
import com.hanyans.gachacounter.wrapper.GachaType;
import com.hanyans.gachacounter.wrapper.Game;
import com.hanyans.gachacounter.wrapper.exception.ResponseException;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleObjectProperty;


/**
 * Full implementation of {@code Logic} to handle logical procresses.
 */
public class LogicManager implements Logic {
    private static final String LOADING_ERROR_TITLE = "Error occured while loading";
    private static final String SAVING_ERROR_TITLE = "Error occured while saving";

    private final Logger logger = LogManager.getFormatterLogger(LogicManager.class);

    private final ThreadPoolExecutor executor = new ThreadPoolExecutor(
            1, 1, 5000, TimeUnit.MILLISECONDS, new LinkedBlockingDeque<>());

    private final BannerHistory stndHist = new BannerHistory(GachaType.STANDARD);
    private final BannerHistory charHist = new BannerHistory(GachaType.CHARACTER);
    private final BannerHistory weapHist = new BannerHistory(GachaType.WEAPON);
    private final BannerEventHistory charEvents = new BannerEventHistory();
    private final BannerEventHistory weapEvents = new BannerEventHistory();

    private final ObjectProperty<String> messageProperty = new SimpleObjectProperty<>();
    private final DoubleProperty progressProperty = new SimpleDoubleProperty(1.0);
    private final BooleanProperty runningProperty = new SimpleBooleanProperty(false);

    private final ObjectProperty<Game> gameProperty = new SimpleObjectProperty<>();

    private final Version version;
    private final Storage storage;
    private final UserPreference preference;

    private volatile boolean isRunning = false;

    private ConsumerTask<GachaReport> reportCompletionTask = ConsumerTask.blankTask();
    private Consumer<PopupMessage> errMsgHandler = msg -> {};
    private Consumer<AppUpdateMessage> updateHandler = msg -> {};

    private HashMap<Long, Boolean> uidFilterMap = new HashMap<>();


    public LogicManager(Version version, Storage storage, UserPreference preference) {
        this.version = version;
        this.storage = storage;
        this.preference = preference;
    }


    /*
     * ========================================================================
     *      TASKS
     * ========================================================================
     */


    @Override
    public void grabPlayerUrl(String pathString, Consumer<String> onComplete) {
        if (!canRun("GRAB PLAYER URL", true)) {
            return;
        }
        setRunningState(true);
        UrlGrabberTask task = new UrlGrabberTask(pathString);
        task.setOnException(ex -> {
            logger.error("Error occured while grabbing player URL", ex);
            handleErrorMessage(ex);
            setRunningState(false);
        });
        task.setOnComplete(urlString -> {
            onComplete.accept(urlString);
            updateDataPathPref(Path.of(pathString));
            handleIoError(saveState(), LOADING_ERROR_TITLE);
            setRunningState(false);
        });
        bindTaskProperty(task);
        executor.execute(task);
    }


    @Override
    public synchronized void setGame(Game game) {
        if (!canRun("SET GAME", false)) {
            return;
        }
        setRunningState(true);

        // check if should change game property
        Game oldGame = gameProperty.get();
        if (game == null || game.equals(oldGame)) {
            setRunningState(false);
            return;
        }

        // set game property and load new game data
        gameProperty.set(game);
        Collection<Throwable> exList = loadHistory(game);
        generateGachaReport(report -> {
            handleIoError(exList, LOADING_ERROR_TITLE);
            setUidFilterMap(report.uids);
            logger.info("Game set from <%s> to <%s>", oldGame, game);
            setRunningState(false);
        });
    }


    @Override
    public void setUidFilter(Collection<Long> uids) {
        if (!canRun("SET UID FILTER", true)) {
            return;
        }
        setRunningState(true);
        updateUidFilterMap(new HashSet<>(uids));
        generateGachaReport(
                getUidFilterSet(),
                report -> {
                    StringBuilder builder = new StringBuilder();
                    for (long uid : uids) {
                        builder.append(String.format("\t%d\n", uid));
                    }
                    String filterItemString = builder.toString().stripTrailing();
                    if (filterItemString.isBlank()) {
                        filterItemString = "\tNOTHING";
                    }
                    logger.info("Filter updated to show only:\n%s", filterItemString);
                    setRunningState(false);
                });
    }


    @Override
    public void updateGachaHistory(String playerUrl) {
        if (!canRun("UPDATE GACHA HISTORY", true)) {
            return;
        }

        // check if player url is valid
        logger.debug("Attempting to execute -{UPDATE GACHA HISTORY}-\n\t<PLAYER URL> = %s",
                String.valueOf(playerUrl));
        if (playerUrl == null || playerUrl.isBlank()) {
            logger.error("Unable to execute -{UPDATE GACHA HISTORY}- as <PLAYER URL> is invalid");
            handleErrorMessage("Invalid URL", "URL field is blank");
            return;
        }

        setRunningState(true);
        final long startTime = System.currentTimeMillis();
        HistoryRetrieverTask task = new HistoryRetrieverTask(
                playerUrl,
                getGame(),
                stndHist,
                charHist,
                weapHist);
        task.setOnComplete(num -> {
            ArrayList<Throwable> exList = saveState();
            generateGachaReport(report -> {
                setUidFilterMap(report.uids);
                handleIoError(exList, SAVING_ERROR_TITLE);
                long duration = System.currentTimeMillis() - startTime;
                logger.info("Completed -{UPDATE GACHA HISTORY}- in %d ms",
                        duration);
                setRunningState(false);
            });
        });
        task.setOnException(this::handleHistoryFailure);
        messageProperty.bind(task.messageProperty());
        progressProperty.bind(task.progressProperty());
        executor.execute(task);
    }


    @Override
    public void manualSave() {
        if (!canRun("SAVE DATA", false)) {
            return;
        }
        setRunningState(true);
        saveState();
        setRunningState(false);
    }


    @Override
    public void checkForAppUpdates(boolean isHandleUpToDate) {
        if (!canRun("CHECK FOR UPDATES", false)) {
            return;
        }
        setRunningState(true);
        AppUpdateCheckTask task = new AppUpdateCheckTask(version);
        task.setOnComplete(msg -> {
            handleAppUpdateMessage(msg, isHandleUpToDate);
            setRunningState(false);
        });
        task.setOnException(this::handleAppUpdateCheckFailure);
        bindTaskProperty(task);
        executor.execute(task);
    }


    @Override
    public void updateData() {
        if (!canRun("UPDATE DATA", false)) {
            return;
        }
        setRunningState(true);
        UpdateDataTask task = new UpdateDataTask();
        task.setOnComplete(msg -> {
            handlePopupMessage(msg);
            setRunningState(false);
        });
        task.setOnException(this::handleAppUpdateCheckFailure);
        bindTaskProperty(task);
        executor.execute(task);
    }


    @Override
    public void updatePreference(
            RunnableTask<UserPreference> task,
            Consumer<UserPreference> onComplete,
            Consumer<Throwable> onException) {
        if (!canRun("UPDATE PREFERENCE", false)) {
            return;
        }
        setRunningState(true);
        task.setOnComplete(onComplete.andThen(pref -> {
            preference.resetTo(pref);
            MainApp.setLogLevel(preference.getLogLevel());
            saveState();
            if (getGame() == null) {
                setRunningState(false);
                return;
            }
            generateGachaReport(
                    getUidFilterSet(),
                    report -> setRunningState(false));
        }));
        task.setOnException(onException.andThen(ex -> setRunningState(false)));
        bindTaskProperty(task);
        executor.execute(task);
    }


    /*
     * ========================================================================
     *      IO
     * ========================================================================
     */


    private Collection<Throwable> loadHistory(Game game) {
        LoadReport<GameGachaData> report = storage.loadGachaData(game);
        stndHist.reset(report.data.stndHist);
        charHist.reset(report.data.charHist);
        weapHist.reset(report.data.weapHist);
        charEvents.reset(report.data.charEvents);
        weapEvents.reset(report.data.weapEvents);
        return report.exList;
    }


    private ArrayList<Throwable> saveState() {
        ArrayList<Throwable> exList = new ArrayList<>();
        exList.addAll(storage.savePreference(preference));
        if (getGame() != null) {
            exList.addAll(storage.saveBannerHistory(getGame(), stndHist));
            exList.addAll(storage.saveBannerHistory(getGame(), charHist));
            exList.addAll(storage.saveBannerHistory(getGame(), weapHist));
        }
        return exList;
    }


    /*
     * ========================================================================
     *      EXCEPTION HANDLERS
     * ========================================================================
     */


    private void handleHistoryFailure(Throwable ex) {
        if (ex instanceof ResponseException) {
            logger.error("Failed to retrieve gacha log due to unexpected response -- %s",
                    ex.getMessage());
        } else {
            logger.error("Failed to retrieve gacha log due to unexpected exception", ex);
        }
        handleErrorMessage(ex);
        setRunningState(false);
    }


    public void handleIoError(Collection<Throwable> exList, String errorTitle) {
        if (exList.isEmpty()) {
            return;
        }
        StringBuilder builder = new StringBuilder();
        for (Throwable ex : exList) {
            builder.append(ex.getMessage()).append("\n\n");
        }
        handleErrorMessage(errorTitle, builder.toString().strip());
    }


    private void handleGachaReportFailure(Throwable ex) {
        logger.error("Error occured while generating gacha report", ex);
        handleErrorMessage(ex);
        setRunningState(false);
    }


    private void handleAppUpdateCheckFailure(Throwable ex) {
        logger.error("Error occured while checking for updates", ex);
        handleErrorMessage("Failed to check for updates", ex.toString());
        setRunningState(false);
    }


    /*
     * ========================================================================
     *      HANDLERS
     * ========================================================================
     */


    @Override
    public synchronized void setReportCompletionTask(ConsumerTask<GachaReport> task) {
        this.reportCompletionTask = task;
    }


    private synchronized ConsumerTask<GachaReport> getReportCompletionTask() {
        return reportCompletionTask;
    }


    @Override
    public synchronized void setPopupMessageHandler(Consumer<PopupMessage> errMsgHandler) {
        this.errMsgHandler = errMsgHandler;
    }


    private synchronized void handlePopupMessage(PopupMessage msg) {
        errMsgHandler.accept(msg);
    }


    private void handleErrorMessage(String title, String content) {
        handlePopupMessage(new PopupMessage(title, content, PopupMessage.MsgType.Error));
    }


    private void handleErrorMessage(Throwable ex) {
        handleErrorMessage(ex.getClass().getSimpleName(), ex.getMessage());
    }


    @Override
    public synchronized void setAppUpdateMessageHandler(Consumer<AppUpdateMessage> handler) {
        this.updateHandler = handler;
    }


    private synchronized void handleAppUpdateMessage(AppUpdateMessage msg, boolean isHandleUpToDate) {
        if (msg.hasUpdate || isHandleUpToDate) {
            updateHandler.accept(msg);
        }
    }


    /*
     * ========================================================================
     *      SYNC GETTER / SETTER
     * ========================================================================
     */


    private synchronized void setRunningState(boolean state) {
        isRunning = state;
        runningProperty.set(state);
    }


    public synchronized boolean isRunning() {
        return isRunning;
    }


    private void updateDataPathPref(Path path) {
        switch (getGame()) {
            case HSR:
                preference.setDataFilePathHsr(path);
                break;
            case Genshin:
                preference.setDataFilePathGenshin(path);
                break;
        }
    }


    @Override
    public UserPreference getUserPrefs() {
        return preference;
    }


    @Override
    public synchronized Game getGame() {
        return gameProperty.get();
    }


    private synchronized void updateUidFilterMap(HashSet<Long> uids) {
        for (long uid : uidFilterMap.keySet()) {
            if (uids.contains(uid)) {
                uidFilterMap.put(uid, true);
                continue;
            }
            uidFilterMap.put(uid, false);
        }
    }


    private synchronized void setUidFilterMap(Collection<Long> uids) {
        uidFilterMap.clear();
        for (long uid : uids) {
            uidFilterMap.put(uid, true);
        }
    }


    @Override
    public synchronized HashMap<Long, Boolean> getUidFilterMap() {
        return new HashMap<>(uidFilterMap);
    }


    public HashSet<Long> getUidFilterSet() {
        return new HashSet<>(getUidFilterMap().entrySet().stream()
                .filter(entry -> !entry.getValue())
                .map(entry -> entry.getKey())
                .toList());
    }


    /*
     * ========================================================================
     *      PROPERTIES
     * ========================================================================
     */


    @Override
    public ObjectProperty<String> messageProperty() {
        return messageProperty;
    }


    @Override
    public DoubleProperty progressProperty() {
        return progressProperty;
    }


    @Override
    public BooleanProperty runningProperty() {
        return runningProperty;
    }


    /*
     * ========================================================================
     *      MISC
     * ========================================================================
     */


    /**
     * Returns {@code true} if a task can run and {@code false} otherwise.
     *
     * @param taskName - the name of the task.
     * @param isRequireGame - if the task requires a game to be set.
     */
    private boolean canRun(String taskName, boolean isRequireGame) {
        if (isRunning()) {
            logger.error("Unable to execute -{%s}- as another task is already running",
                    taskName);
            return false;
        }
        if (isRequireGame && getGame() == null) {
            logger.error("Unable to execute -{%s}- as game is not yet set",
                    taskName);
            return false;
        }
        return true;
    }


    private void bindTaskProperty(TrackableTask task) {
        messageProperty.bind(task.messageProperty());
        progressProperty.bind(task.progressProperty());
    }


    public void generateGachaReport(Consumer<GachaReport> finaliser) {
        generateGachaReport(null, finaliser);
    }


    private void generateGachaReport(HashSet<Long> uidFilters, Consumer<GachaReport> finaliser) {
        // initialize individual counter tasks
        RunnableTask<BannerReport> weapCounter = new CounterTask(weapHist)
                .setRateUpMap(weapEvents)
                .setUidFilters(uidFilters);
        RunnableTask<BannerReport> charCounter = new CounterTask(charHist)
                .setRateUpMap(charEvents)
                .setUidFilters(uidFilters);
        RunnableTask<BannerReport> stndCounter = new CounterTask(stndHist)
                .setUidFilters(uidFilters);

        // initialize grouping task
        GachaCounterTask task = new GachaCounterTask(
                getGame(), stndCounter, charCounter, weapCounter);
        task.setOnException(this::handleGachaReportFailure);
        getReportCompletionTask().setOnException(this::handleGachaReportFailure);
        task.setOnComplete(getReportCompletionTask()
                .bindProperties(messageProperty, progressProperty)
                .andThen(finaliser));
        bindTaskProperty(task);

        executor.execute(task);
    }


    public void shutdown() {
        executor.shutdownNow();
        try {
            executor.awaitTermination(10000, TimeUnit.MILLISECONDS);
            if (!executor.isShutdown()) {
                logger.fatal("Failed to shutdown executor for some reason please close using task manager");
                return;
            }
        } catch (InterruptedException interEx) {
            logger.warn("Interrupted while shutting down executor", interEx);
            return;
        }
        logger.info("Successfully shutdown executor");
    }
}
