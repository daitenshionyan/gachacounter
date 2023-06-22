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
import com.hanyans.gachacounter.core.LockedValue;
import com.hanyans.gachacounter.core.LockedVolatileValue;
import com.hanyans.gachacounter.core.PopupMessage;
import com.hanyans.gachacounter.core.Version;
import com.hanyans.gachacounter.core.task.ConsumerTask;
import com.hanyans.gachacounter.core.task.RunnableTask;
import com.hanyans.gachacounter.core.task.TrackableTask;
import com.hanyans.gachacounter.logic.task.AppUpdateCheckTask;
import com.hanyans.gachacounter.logic.task.UpdateDataTask;
import com.hanyans.gachacounter.logic.task.UrlGrabberTask;
import com.hanyans.gachacounter.mhy.Game;
import com.hanyans.gachacounter.mhy.exception.ResponseException;
import com.hanyans.gachacounter.model.GameGachaData;
import com.hanyans.gachacounter.model.count.GachaReport;
import com.hanyans.gachacounter.model.preference.UserPreference;
import com.hanyans.gachacounter.storage.LoadReport;
import com.hanyans.gachacounter.storage.Storage;

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

    private final LockedVolatileValue<Boolean> isRunning = new LockedVolatileValue<Boolean>(false);

    private final ThreadPoolExecutor executor = new ThreadPoolExecutor(
            1, 1, 5000, TimeUnit.MILLISECONDS, new LinkedBlockingDeque<>());

    private final LockedValue<ConsumerTask<GachaReport>> reportCompletionTask =
            new LockedValue<>(ConsumerTask.blankTask());
    private final LockedValue<Consumer<PopupMessage>> errMsgHandler =
            new LockedValue<>(msg -> {});
    private final LockedValue<Consumer<AppUpdateMessage>> updateHandler =
            new LockedValue<>(msg -> {});

    private final ObjectProperty<String> messageProperty = new SimpleObjectProperty<>();
    private final DoubleProperty progressProperty = new SimpleDoubleProperty(1.0);
    private final BooleanProperty runningProperty = new SimpleBooleanProperty(false);

    private final DataManager dataManager = new DataManager();

    private final Version version;
    private final Storage storage;
    private final UserPreference preference;


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
            ArrayList<Throwable> errList = saveState();
            handleIoError(errList, LOADING_ERROR_TITLE);
            setRunningState(false);
        });
        bindTaskProperty(task);
        executor.execute(task);
    }


    @Override
    public void setGame(Game game) {
        if (!canRun("SET GAME", false)) {
            return;
        }
        setRunningState(true);

        // check if should change game property
        Game oldGame = getGame();
        if (game == null || game.equals(oldGame)) {
            setRunningState(false);
            return;
        }

        // form load and then render task
        Runnable task = () -> {
            LoadReport<GameGachaData> loadReport = storage.loadGachaData(game);
            Consumer<GachaReport> comHandler = reportCompletionTask.get()
                    .bindProperties(messageProperty, progressProperty)
                    .andThen(report -> {
                        handleIoError(loadReport.exList, LOADING_ERROR_TITLE);
                        logger.info("Game set from <%s> to <%s>", oldGame, game);
                        setRunningState(false);
                    });
            dataManager.formResetTask(loadReport.data, comHandler, this::handleErrorMessage)
                    .run();
        };
        executor.execute(task);
    }


    @Override
    public void setUidFilter(Collection<Long> uids) {
        if (!canRun("SET UID FILTER", true)) {
            return;
        }
        setRunningState(true);
        updateUidFilterMap(new HashSet<>(uids));
        generateGachaReport(
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
        Consumer<GachaReport> comHandler = report -> {
            ArrayList<Throwable> exList = saveState();
            reportCompletionTask.get()
                    .bindProperties(messageProperty, progressProperty)
                    .accept(report);
            handleIoError(exList, SAVING_ERROR_TITLE);
            setRunningState(false);
        };
        RunnableTask<Void> task = dataManager.formRetrieverTask(playerUrl, comHandler, this::handleHistoryFailure);
        bindTaskProperty(task);
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
            generateGachaReport(report -> setRunningState(false));
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


    private ArrayList<Throwable> saveState() {
        ArrayList<Throwable> exList = new ArrayList<>();
        exList.addAll(storage.savePreference(preference));
        exList.addAll(dataManager.readGachaData(storage::saveGachaData));
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
    public void setReportCompletionTask(ConsumerTask<GachaReport> task) {
        task.setOnException(this::handleGachaReportFailure);
        reportCompletionTask.set(task);
    }


    @Override
    public void setPopupMessageHandler(Consumer<PopupMessage> handler) {
        errMsgHandler.set(handler);
    }


    private void handlePopupMessage(PopupMessage msg) {
        errMsgHandler.get().accept(msg);
    }


    private void handleErrorMessage(String title, String content) {
        handlePopupMessage(new PopupMessage(title, content, PopupMessage.MsgType.Error));
    }


    private void handleErrorMessage(Throwable ex) {
        handleErrorMessage(ex.getClass().getSimpleName(), ex.getMessage());
    }


    @Override
    public void setAppUpdateMessageHandler(Consumer<AppUpdateMessage> handler) {
        updateHandler.set(handler);
    }


    private void handleAppUpdateMessage(AppUpdateMessage msg, boolean isHandleUpToDate) {
        if (msg.hasUpdate || isHandleUpToDate) {
            updateHandler.get().accept(msg);
        }
    }


    /*
     * ========================================================================
     *      SYNC GETTER / SETTER
     * ========================================================================
     */


    private void setRunningState(boolean state) {
        isRunning.set(state);
        runningProperty.set(state);
    }


    public boolean isRunning() {
        return isRunning.get();
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
    public Game getGame() {
        return dataManager.getGame();
    }


    private void updateUidFilterMap(HashSet<Long> uids) {
        dataManager.setUidFilter(uids);
    }


    @Override
    public HashMap<Long, Boolean> getUidFilterMap() {
        return dataManager.getUidFilterMap();
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


    private void generateGachaReport(Consumer<GachaReport> finaliser) {
        Consumer<GachaReport> comHandler = reportCompletionTask.get()
                .bindProperties(messageProperty, progressProperty)
                .andThen(finaliser);
        executor.execute(dataManager.formGachaReportTask(comHandler, this::handleGachaReportFailure));
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
