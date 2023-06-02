package com.hanyans.gachacounter.core;

import java.util.HashMap;
import java.util.Map;


/**
 * A map to keep track of the frequencies of objects.
 *
 * @param <T> the type of object to keep track of.
 */
public class FrequencyMap<T> extends HashMap<T, Integer> {
    /**
     * Constructs an empty {@code FrequencyMap}.
     */
    public FrequencyMap() {}


    /**
     * Constructs a new {@code FrequencyMap} with the same frequency mapping as
     * the specified {@code Map}.
     *
     * @param map - the map whose frequency mappings are to be placed in this
     *      map.
     * @throws NullPointerException if the specified map is {@code null}.
     */
    public FrequencyMap(Map<? extends T, ? extends Integer> map) {
        super(map);
    }


    /**
     * Increments the frequency of the specified item by 1. If the item is not
     * yet in the map, its initial frequency is assumed to be 0.
     *
     * @param item - the item whose frequency to increment.
     * @return the new frequency of the specified item.
     */
    public int add(T item) {
        return add(item, 1);
    }


    /**
     * Increments the frequency of the specified item by the specified value.
     * If the item is not yet in the map, its initial frequency is assumed to
     * be 0.
     *
     * <p>Negative values are allowed. If the specified value is negative, the
     * requency will be reduced instead.
     *
     * @param item - the item whose frequency to increment.
     * @param value - the value to increment by.
     * @return the new frequency of the specified item.
     */
    public int add(T item, int value) {
        int freq = get(item) + value;
        put(item, freq);
        return freq;
    }


    /**
     * Adds the given frequency maping to this. The state of this map is
     * changed in the process. Use {@link #merge(Map)} if a state change of
     * this map is not desired.
     *
     * @param map - the map whose frequencies are to be added to this.
     * @return the change in the total frequency. (Can be negative)
     */
    public int addAll(Map<? extends T, ? extends Integer> map) {
        int sum = 0;
        for (Map.Entry<? extends T, ? extends Integer> entry : map.entrySet()) {
            sum += add(entry.getKey(), entry.getValue());
        }
        return sum;
    }


    /**
     * Decrements the frequency of the specified item by 1. If the item is not
     * yet in the map, its initial frequency is assumed to be 0.
     *
     * @param item - the item whose frequency to decrement.
     * @return the new frequency of the specified item.
     */
    public int reduce(T item) {
        return add(item, -1);
    }


    /**
     * Returns the frequency of the specified item. If the item is not yet in
     * the map, 0 is returned.
     *
     * @param item - the item whose frequency to return.
     */
    public Integer get(Object item) {
        return getOrDefault(item, 0);
    }


    /**
     * Returns the sum of all the stored frequencies of this map.
     */
    public int totalFreq() {
        int sum = 0;
        for (int freq : values()) {
            sum += freq;
        }
        return sum;
    }


    /**
     * Returns the largest frequency.
     */
    public int largestFreq() {
        if (isEmpty()) {
            return 0;
        }
        int max = Integer.MIN_VALUE;
        for (int freq : values()) {
            max = Math.max(freq, max);
        }
        return max;
    }


    /**
     * Adds the given frequency mapping to this. Unlike {@link #addAll(Map)},
     * the state of this map is not changed in the process. Instead, another
     * {@code FrequencyMap} is returned that represents the merged result.
     *
     * @param map - the map whose frequencies are to be added to this.
     * @return a {@code FrequencyMap} that represents the state after the
     *      operation.
     */
    public FrequencyMap<T> merge(Map<? extends T, ? extends Integer> map) {
        FrequencyMap<T> result = new FrequencyMap<>(this);
        result.addAll(map);
        return result;
    }
}
