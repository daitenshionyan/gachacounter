package com.hanyans.gachacounter.storage;

import java.util.Collection;
import java.util.Objects;


/**
 * A load report containing the loaded data and exceptions that occured while
 * loading the data.
 */
public class LoadReport<T> {
    /** The data laoded. */
    public final T data;
    /** List of exceptions that occured. */
    public final Collection<Throwable> exList;


    /**
     * Constructs a {@code LoadReport}.
     *
     * @param data - the data loaded.
     * @param exList - the list of exceptions that occured while loading.
     * @throws NullPointerException if any parameter is {@code null}.
     */
    public LoadReport(T data, Collection<Throwable> exList) {
        this.data = Objects.requireNonNull(data);
        this.exList = Objects.requireNonNull(exList);
    }
}
