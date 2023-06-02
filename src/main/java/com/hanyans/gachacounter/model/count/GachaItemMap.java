package com.hanyans.gachacounter.model.count;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;

import com.hanyans.gachacounter.model.GachaItem;


/**
 * A map mapping gacha items to gacha entries.
 */
public class GachaItemMap {
    private final HashMap<GachaItem, HashSet<ProcessedGachaEntry>> itemMap;

    private int size = 0;


    public GachaItemMap() {
        this.itemMap = new HashMap<>();
    }


    public GachaItemMap(HashMap<GachaItem, HashSet<ProcessedGachaEntry>> itemMap) {
        this.size = count(itemMap);
        this.itemMap = itemMap;
    }


    /**
     * Adds the specified {@code GachaEntry} to the map.
     *
     * @return the number of entries of the added item stored.
     */
    public int add(ProcessedGachaEntry entry) {
        Objects.requireNonNull(entry);
        GachaItem item = GachaItem.fromGachaEntry(entry);
        if (!(itemMap.containsKey(item))) {
            itemMap.put(item, new HashSet<>());
        }
        boolean isAdded = itemMap.get(item).add(entry);
        if (isAdded) {
            size++;
        }
        return itemMap.get(item).size();
    }


    /**
     * Returns a set view of the {@code GachaItem} in the map.
     */
    public HashSet<GachaItem> itemSet() {
        return new HashSet<>(itemMap.keySet());
    }


    /**
     * Returns the set of entries mapped to the specified item.
     * Changes to the returned set will not affect the map.
     *
     * @param item - the item whose entry set to retrieve.
     */
    public HashSet<ProcessedGachaEntry> get(GachaItem item) {
        return new HashSet<>(itemMap.get(item));
    }


    /**
     * Returns a set view of the mappings of this map. Changes to the set will
     * not affect the map.
     */
    public HashSet<Map.Entry<GachaItem, HashSet<ProcessedGachaEntry>>> entrySet() {
        return new HashSet<>(itemMap.entrySet());
    }


    /**
     * Returns a set view of the items of this map. Changes to this set will
     * not affect the map.
     */
    public HashSet<GachaItem> keySet() {
        return new HashSet<>(itemMap.keySet());
    }


    /**
     * Returns the number of entries contained in the map.
     */
    public int size() {
        return size;
    }


    /**
     * Merges this {@code GachaItemMap} with the other as specified.
     *
     * @return the merged result.
     */
    public GachaItemMap merge(GachaItemMap other) {
        HashMap<GachaItem, HashSet<ProcessedGachaEntry>> itemMap = copyMap(this.itemMap);
        for (Map.Entry<GachaItem, HashSet<ProcessedGachaEntry>> entry : other.itemMap.entrySet()) {
            if (itemMap.containsKey(entry.getKey())) {
                itemMap.get(entry.getKey()).addAll(entry.getValue());
            } else {
                itemMap.put(entry.getKey(), new HashSet<>(entry.getValue()));
            }
        }
        return new GachaItemMap(itemMap);
    }


    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        itemMap.keySet().stream()
                .sorted((i1, i2) -> i2.rank - i1.rank)
                .forEach(item -> builder.append(String.format("[%d] %s = %d\n",
                        item.rank, item.name, itemMap.get(item).size())));
        return builder.toString().strip();
    }


    private static HashMap<GachaItem, HashSet<ProcessedGachaEntry>> copyMap(
                HashMap<GachaItem, HashSet<ProcessedGachaEntry>> itemMap) {
        HashMap<GachaItem, HashSet<ProcessedGachaEntry>> map = new HashMap<>();
        for (Map.Entry<GachaItem, HashSet<ProcessedGachaEntry>> entry : itemMap.entrySet()) {
            map.put(entry.getKey(), new HashSet<>(entry.getValue()));
        }
        return map;
    }


    private static int count(Map<?, ? extends Collection<?>> map) {
        int count = 0;
        for (Collection<?> entries : map.values()) {
            count += entries.size();
        }
        return count;
    }
}
