package com.hanyans.gachacounter.core;

import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;


/**
 * A value that has read and write locks on it.
 *
 * @param <T> type of value being locked.
 */
public class LockedValue<T> {
    private final ReadWriteLock lock = new ReentrantReadWriteLock();

    private T value;


    /**
     * Constructs a {@code LockedValue} with its initial value set as
     * {@code null}.
     */
    public LockedValue() {
        this.value = null;
    }


    /**
     * Constructs a {@code LockedValue}.
     *
     * @param value - initial value.
     */
    public LockedValue(T value) {
        this.value = value;
    }


    /**
     * Sets the value as specified.
     *
     * @param value - value to set to.
     */
    public void set(T value) {
        try {
            lock.writeLock().lock();
            this.value = value;
        } finally {
            lock.writeLock().unlock();
        }
    }


    /**
     * Returns the current value.
     */
    public T get() {
        try {
            lock.readLock().lock();
            return value;
        } finally {
            lock.readLock().unlock();
        }
    }
}
