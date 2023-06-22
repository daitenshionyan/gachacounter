package com.hanyans.gachacounter.core;

import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;


/**
 * Similar to {@link LockedValue} except that values are volatile.
 */
public class LockedVolatileValue<T> {
    private final ReadWriteLock lock = new ReentrantReadWriteLock();

    private volatile T value;


    /**
     * Constructs a {@code LockedVolatileValue} with its initial value set as
     * {@code null}.
     */
    public LockedVolatileValue() {
        this.value = null;
    }


    /**
     * Constructs a {@code LockedVolatileValue}.
     *
     * @param value - initial value.
     */
    public LockedVolatileValue(T value) {
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
