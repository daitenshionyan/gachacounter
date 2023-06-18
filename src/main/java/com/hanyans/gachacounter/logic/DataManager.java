package com.hanyans.gachacounter.logic;

import java.util.HashMap;
import java.util.HashSet;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.function.Consumer;
import java.util.function.Function;

import com.hanyans.gachacounter.core.task.RunnableTask;
import com.hanyans.gachacounter.logic.task.GachaCounterTask;
import com.hanyans.gachacounter.logic.task.HistoryRetrieverTask;
import com.hanyans.gachacounter.model.GameGachaData;
import com.hanyans.gachacounter.model.count.GachaReport;
import com.hanyans.gachacounter.wrapper.Game;


/**
 * Manager to synchronize read and write access of game gacha data for
 * {@link LogicManager}.
 */
public class DataManager {
    private final ReadWriteLock lock = new ReentrantReadWriteLock();

    private final HashMap<Long, Boolean> uidFilterMap = new HashMap<>();

    private GameGachaData gameGachaData = null;


    public Game getGame() {
        try {
            lock.readLock().lock();
            return gameGachaData != null ? gameGachaData.game : null;
        } finally {
            lock.readLock().unlock();
        }
    }


    public <R> R readGachaData(Function<GameGachaData, R> readFunc) {
        try {
            lock.readLock().lock();
            return readFunc.apply(gameGachaData);
        } finally {
            lock.readLock().unlock();
        }
    }


    public void updateUidFilterMap(HashSet<Long> uids) {
        try {
            lock.writeLock().lock();
            for (long uid : uidFilterMap.keySet()) {
                uidFilterMap.put(uid, uids.contains(uid));
            }
        } finally {
            lock.writeLock().unlock();
        }
    }


    public HashMap<Long, Boolean> getUidFilterMap() {
        try {
            lock.readLock().lock();
            return new HashMap<>(uidFilterMap);
        } finally {
            lock.readLock().unlock();
        }
    }


    public RunnableTask<Void> formResetTask(GameGachaData gameGachaData,
                Consumer<GachaReport> comHandler,
                Consumer<Throwable> exHandler) {
        return new RunnableTask<>() {
            @Override
            public Void performTask() {
                try {
                    lock.writeLock().lock();

                    GachaCounterTask task = new GachaCounterTask(gameGachaData);
                    bindMessageProperty(task.messageProperty());
                    bindProgressProeprty(task.progressProperty());
                    GachaReport report = task.performTask();

                    resetData(gameGachaData, report);
                    // unlock to allow handler to read
                    lock.writeLock().unlock();
                    comHandler.accept(report);
                } catch (Throwable ex) {
                    lock.writeLock().unlock();
                    exHandler.accept(ex);
                }
                return null;
            }
        };
    }


    private void resetData(GameGachaData gameGachaData, GachaReport report) {
        this.gameGachaData = gameGachaData;
        uidFilterMap.clear();
        for (long uid : report.uids) {
            uidFilterMap.put(uid, true);
        }
    }


    private void resetUidFilterMap(GachaReport report) {
        uidFilterMap.clear();
        for (long uid : report.uids) {
            uidFilterMap.put(uid, true);
        }
    }


    public RunnableTask<Void> formRetrieverTask(String playerUrl,
                Consumer<GachaReport> comHandler,
                Consumer<Throwable> exHandler) {
        return new RunnableTask<>() {
            @Override
            public Void performTask() {
                try {
                    lock.writeLock().lock();

                    // retrieve history
                    HistoryRetrieverTask retTask = new HistoryRetrieverTask(playerUrl, gameGachaData);
                    bindMessageProperty(retTask.messageProperty());
                    bindProgressProeprty(retTask.progressProperty());
                    retTask.performTask();

                    // form gacha report
                    GachaCounterTask countTask = new GachaCounterTask(gameGachaData);
                    bindMessageProperty(countTask.messageProperty());
                    bindProgressProeprty(countTask.progressProperty());
                    GachaReport report = countTask.performTask();

                    // update UID map to add new UIDs if present
                    resetUidFilterMap(report);
                    // unlock to allow handler to read
                    lock.writeLock().unlock();
                    comHandler.accept(report);
                } catch (Throwable ex) {
                    lock.writeLock().unlock();
                    exHandler.accept(ex);
                }
                return null;
            }
        };
    }


    public RunnableTask<Void> formGachaReportTask(
                HashSet<Long> uidFilters,
                Consumer<GachaReport> comHandler,
                Consumer<Throwable> exHandler) {
        return new RunnableTask<>() {
            @Override
            public Void performTask() {
                try {
                    lock.readLock().lock();

                    GachaCounterTask task = new GachaCounterTask(gameGachaData, uidFilters);
                    task.setOnComplete(comHandler);
                    bindMessageProperty(task.messageProperty());
                    bindProgressProeprty(task.progressProperty());

                    GachaReport report = task.performTask();
                    comHandler.accept(report);
                } catch (Throwable ex) {
                    exHandler.accept(ex);
                } finally {
                    lock.readLock().unlock();
                }
                return null;
            }
        };
    }
}
