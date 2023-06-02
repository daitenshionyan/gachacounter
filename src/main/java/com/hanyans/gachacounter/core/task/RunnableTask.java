package com.hanyans.gachacounter.core.task;

import java.util.Objects;
import java.util.function.Consumer;


/**
 * A {@link TrackableTask} that can be both be executed on the same or a
 * separate thread.
 *
 * <h3>Separate Thread Execution</h3>
 *
 * When executing on a separate thread a single task execution is enforced. If
 * an attempt is made to run the task (through the {@link #run()}) method if
 * the task is already running or is cancelled, the method will return
 * silently. To handle how the return result or an exception should be handle,
 * use {@link #setOnComplete(Consumer)} and {@link #setOnException(Consumer)}
 * methods to set the completion and exception handlers respectively. These
 * handlers are called when the task completes or runs into an exception
 * respectively.
 *
 * <h3>Same Thread Execution</h3>
 *
 * Execution through the {@link #performTask()} method bypasses the single task
 * execution and will not call the completion handler or exception handler when
 * the task is completed. Thus, callers should make neccessary implementations
 * to handle those.
 *
 * @param <T> the return type of the task.
 */
public abstract class RunnableTask<T> extends TrackableTask implements Runnable {
    private Consumer<T> onComplete = result -> {};


    /**
     * Performs the task.
     *
     * @return the result of the task.
     * @throws Throwable if an error occurs.
     */
    public abstract T performTask() throws Throwable;


    @Override
    public void run() {
        if (hasStarted() || isCancelled()) {
            return;
        }
        setHasStarted(true);
        try {
            handleCompletion(performTask());
        } catch (Throwable ex) {
            handleException(ex);
        }
        setHasStarted(false);
    }


    /**
     * Sets the completion handler of the task. This {@code Consumer} is called
     * when the task completes and is passed the result of the task.
     *
     * @param onComplete - the completion handler.
     */
    public synchronized void setOnComplete(Consumer<T> onComplete) {
        this.onComplete = Objects.requireNonNull(onComplete);
    }


    /**
     * Calls the completion handler to handle the completion of the task.
     *
     * @param result - the result of the task.
     */
    protected synchronized void handleCompletion(T result) {
        onComplete.accept(result);
    }
}
