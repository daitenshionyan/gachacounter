package com.hanyans.gachacounter.model;

import java.util.Collection;
import java.util.HashSet;
import java.util.Objects;
import java.util.function.Predicate;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.hanyans.gachacounter.mhy.GachaType;


/**
 * Represents the gacha history of a banner containing data of the gacha
 * entries from a banner.
 */
public class BannerHistory {
    private final GachaType gachaType;
    private final HashSet<GachaEntry> entrySet = new HashSet<>();


    /**
     * Constructs an empty {@code BannerHistory}.
     *
     * @param gachaType - the gacha type of this history.
     */
    public BannerHistory(GachaType gachaType) {
        this.gachaType = gachaType;
    }


    /**
     * Constructs a {@code BannerHistory}.
     *
     * @param gachaType - the gacha type of the history.
     * @param entrySet - the set of entries to contain within the history.
     */
    @JsonCreator
    public BannerHistory(
                @JsonProperty("gachaType") GachaType gachaType,
                @JsonProperty("entrySet") Collection<GachaEntry> entrySet) {
        this.gachaType = gachaType;
        this.entrySet.addAll(entrySet);
    }


    /**
     * Adds the specified entry to the history. If the entry already exist, the
     * entry will be ignored and the state of the history will remain.
     *
     * @param entry - the {@code GachaEntry} to add.
     * @return {@code true} if the specified entry is added and {@code false}
     *      otherwise.
     */
    public boolean add(GachaEntry entry) {
        Objects.requireNonNull(entry);
        return entrySet.add(entry);
    }


    /**
     * Returns {@code true} if this history contains the specified entry and
     * {@code false} otherwise.
     *
     * @param entry - the entry to check.
     */
    public boolean contains(GachaEntry entry) {
        return entrySet.contains(entry);
    }


    /**
     * Adds all the elements from the given {@code BannerHistory} to this.
     *
     * @param history - the history to add in.
     * @return the number of new entries added.
     */
    public int addAll(BannerHistory history) {
        Objects.requireNonNull(history);
        int numAdded = 0;
        for (GachaEntry entry : history.entrySet) {
            if (entrySet.add(entry)) {
                numAdded++;
            }
        }
        return numAdded;
    }


    public int reset(BannerHistory history) {
        Objects.requireNonNull(history);
        entrySet.clear();
        return addAll(history);
    }


    public void reset() {
        entrySet.clear();
    }


    /**
     * Returns the number of gacha entries stored.
     */
    public int size() {
        return entrySet.size();
    }


    /**
     * Returns the gacha type this history corresponds to.
     */
    public GachaType getGachaType() {
        return gachaType;
    }


    /**
     * Returns a set view of the entries stored. Changes to the returned set
     * will not affect this {@code BannerHistory} and vice versa.
     */
    public HashSet<GachaEntry> getEntrySet() {
        return new HashSet<GachaEntry>(entrySet);
    }


    /**
     * Returns a {@code BannerHistory} that is the filtered view of the
     * specified filter.
     *
     * @param filter - the filter to apply.
     */
    public BannerHistory filter(Predicate<GachaEntry> filter) {
        return new BannerHistory(
                gachaType,
                entrySet.stream()
                        .filter(filter)
                        .toList());
    }
}
