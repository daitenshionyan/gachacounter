package com.hanyans.gachacounter.model.count;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import com.hanyans.gachacounter.core.FrequencyMap;


/**
 * Encapsulation of a map mapping accounts as their UIDs to their pity count
 * frequency map.
 */
public class AccPityFreqMap {
    private final HashMap<Long, FrequencyMap<Integer>> accMap = new HashMap<>();


    /**
     * Constructs an empty {@code AccPityFreqMap}.
     */
    public AccPityFreqMap() {}


    /**
     * Constructs a {@code AccPityFreqMap} with the same mappings as the given
     * map.
     *
     * @param accMap - the mappings to put into this map.
     */
    public AccPityFreqMap(Map<Long, FrequencyMap<Integer>> accMap) {
        this.accMap.putAll(copyMap(accMap));
    }


    /**
     * Increments the frequency of the specified account's pity count frequency
     * by 1.
     *
     * <p>If the UID does not yet exist in this map, an empty
     * {@code FrequencyMap} is initialized for it.
     *
     * @param uid - the account's UID whose pity to increment.
     * @param pity - the pity count frequency to increment.
     * @return the new frequency of the incremented pity count.
     */
    public int add(long uid, int pity) {
        return get(uid).add(pity);
    }


    /**
     * Increments the frequency of the specified account's pity countfrequency
     * by the set number.
     *
     * <p>If the UID does not yet exist in this map, an empty
     * {@code FrequencyMap} is initialized for it.
     *
     * @param uid - the account's UID whose pity to increment.
     * @param pity - the pity count frequency to increment.
     * @param count - the count to increase the pity frequency by.
     * @return the new frequency of the incremented pity count.
     */
    public int add(long uid, int pity, int count) {
        return get(uid).add(pity, count);
    }


    /**
     * Returns the frequency map of the specified account.
     *
     * <p>If the UID does not yet exist in this map, an empty
     * {@code FrequencyMap} is initialized for it.
     *
     * @param uid - the account's UID whose {@code FrequencyMap} to return.
     */
    public FrequencyMap<Integer> get(long uid) {
        if (!accMap.containsKey(uid)) {
            accMap.put(uid, new FrequencyMap<>());
        }
        return accMap.get(uid);
    }


    /**
     * Returns the frequency of the specified account's pity count.
     *
     * <p>If the UID does not yet exist in this map, an empty
     * {@code FrequencyMap} is initialized for it. Thus 0 will be returned.
     *
     * @param uid - the account's UID whose {@code FrequencyMap} to return.
     * @param pity - the pity count whose frequency to return.
     */
    public int get(long uid, int pity) {
        return accMap.get(uid).get(pity);
    }


    /**
     * Returns the a set view of the keys (UIDs) encapsulated map.
     */
    public Set<Long> keySet() {
        return accMap.keySet();
    }


    /**
     * Returns a set view of the entries of the encapsulated map.
     */
    public Set<Map.Entry<Long, FrequencyMap<Integer>>> entrySet() {
        return accMap.entrySet();
    }


    /**
     * Merges this {@code AccPityFreqMap} with the other given map. This
     * operation does not change the state of either maps. Instead, a new map
     * is returned.
     *
     * @param other - the other {@code AccPityFreqMap} to merge with this.
     * @return a {@code AccPityFreqMap} that represents the result.
     */
    public AccPityFreqMap merge(AccPityFreqMap other) {
        AccPityFreqMap result = new AccPityFreqMap(accMap);
        for (Map.Entry<Long, FrequencyMap<Integer>> entry : other.entrySet()) {
            result.get(entry.getKey()).addAll(entry.getValue());
        }
        return result;
    }


    /**
     * Condenses the pity counts of all accounts by the given factor. The state
     * of this {@code AccFreqMap} is not changed in the process.
     *
     * @param factor - the factor to condense by.
     */
    public AccPityFreqMap condense(int factor) {
        AccPityFreqMap result = new AccPityFreqMap();
        for (Map.Entry<Long, FrequencyMap<Integer>> entry : accMap.entrySet()) {
            result.accMap.put(entry.getKey(), condense(entry.getValue(), factor));
        }
        return result;
    }


    private FrequencyMap<Integer> condense(FrequencyMap<Integer> freqMap, int factor) {
        FrequencyMap<Integer> result = new FrequencyMap<>(freqMap);
        for (Integer pity : freqMap.keySet()) {
            int value = pity / factor;
            if (pity % factor != 0 || value < 1) {
                value++;
            }
            result.add(value * factor, freqMap.get(pity));
        }
        return result;
    }


    /**
     * Combines all account's {@code FrequencyMap} into 1.
     *
     * @return the condensed {@code FrequencyMap}.
     */
    public FrequencyMap<Integer> combineAll() {
        return accMap.values().stream()
                .reduce(
                        new FrequencyMap<>(),
                        (sum, next) -> sum.merge(next),
                        (sum1, sum2) -> sum1.merge(sum2));
    }


    private static HashMap<Long, FrequencyMap<Integer>> copyMap(Map<Long, FrequencyMap<Integer>> map) {
        HashMap<Long, FrequencyMap<Integer>> result = new HashMap<>();
        for (Map.Entry<Long, FrequencyMap<Integer>> entry : map.entrySet()) {
            result.put(entry.getKey(), new FrequencyMap<>(entry.getValue()));
        }
        return result;
    }
}
