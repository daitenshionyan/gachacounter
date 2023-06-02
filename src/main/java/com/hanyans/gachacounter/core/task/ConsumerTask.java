package com.hanyans.gachacounter.core.task;

import java.util.Objects;
import java.util.function.Consumer;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ObjectProperty;


/**
 * A {@link TrackableTask} that requires an argument input to be performed.
 * Meant as a follow up completion task for {@link RunnableTask} as such, the
 * class will not have any completion handlers.
 *
 * <p>Task enforces a single execution only. Meaning a single instance of this
 * task can only be performed one at a time through the {@link #accept(Object)}
 * method. If the task is already running or is cancelled, the method will
 * return silently.
 *
 * @param <T> the type of the input argument.
 */
public abstract class ConsumerTask<T> extends TrackableTask implements Consumer<T> {
    /**
     * Convenience method to create a blank {@code ConsumerTask} that does
     * nothing.
     *
     * @param <T> the type of the input argument.
     */
    public static <T> ConsumerTask<T> blankTask() {
        return new ConsumerTask<T>() {
            @Override
            protected void performTask(T value) {}
        };
    }


    /**
     * Performs the task.
     *
     * @param value - the input argument.
     * @throws Throwable if an error occurs while executing the task.
     */
    protected abstract void performTask(T value) throws Throwable;


    /**
     * Performs the task while enforcing single task execution.
     *
     * <p>If the task is already running or is cancelled, this method will
     * return silently.
     *
     * @param value - the input argument.
     */
    @Override
    public void accept(T value) {
        if (hasStarted() || isCancelled()) {
            return;
        }
        setHasStarted(true);
        try {
            performTask(value);
        } catch (Throwable ex) {
            handleException(ex);
        }
        setHasStarted(false);
    }


    /**
     * Returns a {@code Consumer} that rebinds the given
     * {@code messageProperty} and {@code progressProperty} to this task's
     * properties before executing the task.
     *
     * @param messageProperty - the message property to bind.
     * @param progressProperty - the progress property to bind.
     * @throws NullPointerException if {@code messageProperty} or
     *      {@code progressProperty} is {@code null}.
     */
    public Consumer<T> bindProperties(ObjectProperty<String> messageProperty, DoubleProperty progressProperty) {
        Objects.requireNonNull(messageProperty);
        Objects.requireNonNull(progressProperty);
        return value -> {
            if (hasStarted() || isCancelled()) {
                return;
            }
            messageProperty.unbind();
            messageProperty.bind(messageProperty());
            progressProperty.unbind();
            progressProperty.bind(progressProperty());
            accept(value);
        };
    }
}
