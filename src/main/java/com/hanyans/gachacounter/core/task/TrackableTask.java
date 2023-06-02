package com.hanyans.gachacounter.core.task;

import java.util.function.Consumer;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyDoubleProperty;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ObservableValue;


/**
 * A task whose progress and status message properties can be tracked.
 */
public abstract class TrackableTask {
    private final ObjectProperty<String> messageProperty = new SimpleObjectProperty<>();
    private final DoubleProperty progressProperty = new SimpleDoubleProperty(1D);

    private Consumer<Throwable> onException = ex -> {};

    private long startTime = 0;

    private volatile boolean hasStarted = false;
    private volatile boolean isCancelled = false;


    /**
     * Sets the {@code started} state of the task as specified.
     *
     * @param hasStarted - the {@code started} state of the task to set to.
     */
    protected synchronized void setHasStarted(boolean hasStarted) {
        this.hasStarted = hasStarted;
        if (hasStarted) {
            startTime = System.currentTimeMillis();
        }
    }


    /**
     * Returns {@code true} if the task has started (is running).
     */
    protected synchronized boolean hasStarted() {
        return hasStarted;
    }


    /**
     * Returns the current run time of this task in milliseconds. If the task
     * is not currently running, {@code 0} is returned.
     */
    public synchronized long getRunTime() {
        if (hasStarted()) {
            return System.currentTimeMillis() - startTime;
        }
        return 0;
    }


    /**
     * Sets the state of the task to cancelled. This is irreversible.
     */
    public synchronized void cancelTask() {
        this.isCancelled = true;
    }


    /**
     * Returns {@code true} if the task is cancelled.
     */
    public synchronized boolean isCancelled() {
        return this.isCancelled;
    }


    /**
     * Sets the exception handler of the task. The set exception handler is
     * accessed through {@link #handleException(Throwable)}.
     *
     * @param onException - the {@code Consumer} to call to handle an uncaught
     *      exception.
     */
    public synchronized void setOnException(Consumer<Throwable> onException) {
        this.onException = onException;
    }


    /**
     * Handles the given exception with the last set exception handler from
     * {@link #setOnException(Consumer)}.
     *
     * @param ex - the exception to handle.
     */
    protected synchronized void handleException(Throwable ex) {
        onException.accept(ex);
    }


    /**
     * Returns a {@code ReadOnlyObjectProperty} of the message property.
     */
    public ReadOnlyObjectProperty<String> messageProperty() {
        return messageProperty;
    }


    /**
     * Binds the message property as specified.
     *
     * @param messageProperty - the message property to bind to.
     */
    protected void bindMessageProperty(ObservableValue<? extends String> messageProperty) {
        this.messageProperty.bind(messageProperty);
    }


    /**
     * Sets the status message of the task.
     *
     * @param message - the message to set.
     */
    protected void setMessage(String message) {
        messageProperty.unbind();
        messageProperty.set(message);
    }


    /**
     * Returns a {@code ReadOnlyDoubleProperty} of the progress property.
     */
    public ReadOnlyDoubleProperty progressProperty() {
        return progressProperty;
    }


    /**
     * Binds the task's progress property as specified.
     *
     * @param progressProperty - the progress property to bind to.
     */
    protected void bindProgressProeprty(ObservableValue<? extends Number> progressProperty) {
        this.progressProperty.bind(progressProperty);
    }


    /**
     * Sets the progress of the task.
     *
     * @param progress - the proportion of the task completed.
     */
    protected void setProgress(Double progress) {
        progressProperty.unbind();
        progressProperty.setValue(progress);
    }


    /**
     * Sets the progress of the task.
     *
     * @param workDone - the current work done.
     * @param totalWork - the total amount of work required to be done.
     */
    protected void setProgress(double workDone, double totalWork) {
        progressProperty.set(workDone / totalWork);
    }
}
