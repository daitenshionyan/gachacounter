package com.hanyans.gachacounter.logic;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Objects;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.function.Consumer;
import java.util.function.Function;

import com.hanyans.gachacounter.core.task.RunnableTask;
import com.hanyans.gachacounter.logic.task.GachaCounterTask;
import com.hanyans.gachacounter.logic.task.HistoryRetrieverTask;
import com.hanyans.gachacounter.mhy.Game;
import com.hanyans.gachacounter.model.GameGachaData;
import com.hanyans.gachacounter.model.UidNameMap;
import com.hanyans.gachacounter.model.count.GachaReport;


/**
 * Manager to synchronize read and write access of game gacha data for
 * {@link LogicManager}.
 */
public class DataManager {
    private final ReadWriteLock lock = new ReentrantReadWriteLock();

    private final HashMap<Long, Boolean> uidFilterMap = new HashMap<>();

    private GameGachaData gameGachaData = null;


    /**
     * Returns the currently set game. {@code null} if the game is not yet set.
     */
    public Game getGame() {
        try {
            lock.readLock().lock();
            return gameGachaData != null ? gameGachaData.game : null;
        } finally {
            lock.readLock().unlock();
        }
    }


    /**
     * Performs the given read function on the current state of
     * {@code GameGachaData}. The given function will also have to handle the
     * case where {@code GameGachaData} is {@code null} (game not yet set).
     *
     * <p>The given function should not perform any write operation on the
     * {@code GameGachaData} although it is not restricted.
     *
     * @param <R> the return type of the read function.
     * @param readFunc - the read function to apply on the current state of
     *      {@code GameGachaData}.
     * @return the output of the function.
     */
    public <R> R readGachaData(Function<GameGachaData, R> readFunc) {
        try {
            lock.readLock().lock();
            return readFunc.apply(gameGachaData);
        } finally {
            lock.readLock().unlock();
        }
    }


    /**
     * Sets the UID filter to show only the UIDs in the given set.
     *
     * @param uids - the UIDs to show.
     * @throws NullPointerException if {@code uids} is {@code null}.
     */
    public void setUidFilter(HashSet<Long> uids) {
        Objects.requireNonNull(uids);
        try {
            lock.writeLock().lock();
            for (long uid : uidFilterMap.keySet()) {
                uidFilterMap.put(uid, uids.contains(uid));
            }
        } finally {
            lock.writeLock().unlock();
        }
    }


    /**
     * Returns a map view of the current state of the UID filter map. Changes
     * to the returned map or the encapsulated map of this {@code DataManager}
     * will not affect the other.
     */
    public HashMap<Long, Boolean> getUidFilterMap() {
        try {
            lock.readLock().lock();
            return new HashMap<>(uidFilterMap);
        } finally {
            lock.readLock().unlock();
        }
    }


    /**
     * Returns the current state of the UID name map.
     */
    public UidNameMap getUidNameMap() {
        try {
            lock.readLock().lock();
            return gameGachaData.nameMap;
        } finally {
            lock.readLock().unlock();
        }
    }


    /**
     * Forms a {@code RunnableTask} that resets the state of this
     * {@code DataManager} with reference to the given {@code GameGachaData}.
     *
     * <p>Given {@code GameGachaData} is copied by reference.
     *
     * @param gameGachaData - the new {@code GameGachaData} to reset to.
     * @param comHandler - a {@code Consumer} that accepts a
     *      {@code GachaReport} the reset procedure is completed.
     * @param exHandler a {@code Consumer} that accepts a {@code Throwable}
     *      whenever an exception while performing the task.
     */
    public RunnableTask<Void> formResetTask(
                GameGachaData gameGachaData,
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


    /**
     * Forms a {@code RunnableTask} that retrieves the player's gacha log
     * through the given player URL.
     *
     * @param playerUrl - URL to retrieve gacha log.
     * @param comHandler - a {@code Consumer} that accepts a
     *      {@code GachaReport} the reset procedure is completed.
     * @param exHandler a {@code Consumer} that accepts a {@code Throwable}
     *      whenever an exception while performing the task.
     */
    public RunnableTask<Void> formRetrieverTask(
                String playerUrl,
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


    /**
     * Forms a {@code RunnableTask} that generates a {@code GachaReport}. The
     * UIDs are filtered out based on the state of the UID filter map of when
     * the returned task is executed.
     *
     * @param comHandler - a {@code Consumer} that accepts a
     *      {@code GachaReport} the reset procedure is completed.
     * @param exHandler a {@code Consumer} that accepts a {@code Throwable}
     *      whenever an exception while performing task.
     */
    public RunnableTask<Void> formGachaReportTask(
                Consumer<GachaReport> comHandler,
                Consumer<Throwable> exHandler) {
        return new RunnableTask<>() {
            @Override
            public Void performTask() {
                try {
                    lock.readLock().lock();

                    GachaCounterTask task = new GachaCounterTask(gameGachaData, getUidFilters());
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


    /**
     * Returns a set of UIDs that should be filtered out.
     */
    private HashSet<Long> getUidFilters() {
        try {
            lock.readLock().lock();
            return new HashSet<>(uidFilterMap.entrySet().stream()
                    .filter(entry -> !entry.getValue())
                    .map(entry -> entry.getKey())
                    .toList());
        } finally {
            lock.readLock().unlock();
        }
    }
}
