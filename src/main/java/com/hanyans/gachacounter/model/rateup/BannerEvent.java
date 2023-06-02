package com.hanyans.gachacounter.model.rateup;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.hanyans.gachacounter.model.GachaItem;


/**
 * Represents a rate up event of a banner.
 */
public class BannerEvent implements Comparable<BannerEvent> {
    private final LocalDateTime begTime;
    private final LocalDateTime endTime;

    private final HashSet<String> rateUps;


    /**
     * Constructs a {@code RateUpEvent}.
     *
     * <p>If {@code startTime} or {@code endTime} are {@code null}, they will
     * be assumed to the the MIN and MAX time respectively. If {@code rateUps}
     * is {@code null}, it will be assumed to be empty.
     *
     * @param begTime - the start time of the event.
     * @param endTime - the end time of the event.
     * @param rateUps - a set of names corresponding to the item names that are
     *      rate ups.
     */
    @JsonCreator
    public BannerEvent(
                @JsonProperty("begTime") LocalDateTime begTime,
                @JsonProperty("endTime") LocalDateTime endTime,
                @JsonProperty("rateUps") HashSet<String> rateUps) {
        this.begTime = Objects.requireNonNullElse(begTime, LocalDateTime.MIN);
        this.endTime = Objects.requireNonNullElse(endTime, LocalDateTime.MAX);
        this.rateUps = Objects.requireNonNullElse(rateUps, new HashSet<>());
    }


    /**
     * Returns the start time of the event.
     */
    public LocalDateTime getStart() {
        return begTime;
    }


    /**
     * Returns the end time of the event.
     */
    public LocalDateTime getEnd() {
        return endTime;
    }


    /**
     * Returns {@code true} if the given time is within the start time and end
     * time of the event, both inclusive, and {@code false} otherwise.
     *
     * @param time - the time to check.
     * @throws NullPointerException if {@code time} is {@code null}.
     */
    public boolean isWithin(LocalDateTime time) {
        Objects.requireNonNull(time);
        return (begTime.isBefore(time) || begTime.isEqual(time))
                && (endTime.isAfter(time) || endTime.isEqual(time));
    }


    /**
     * Returns a set view of the item IDs of the rate up items. Changes to the
     * returned set will not affect the state of this {@code RateUpEvent}.
     */
    public HashSet<String> getRateUps() {
        return new HashSet<>(rateUps);
    }


    /**
     * Returns {@code true} if the given item is a rate up item and
     * {@code false} otherwise.
     *
     * @param item - the time to check for.
     */
    public boolean isRateUp(GachaItem item) {
        return rateUps.contains(item.name);
    }


    @Override
    public int compareTo(BannerEvent other) {
        int diff = begTime.compareTo(other.begTime);
        if (diff == 0) {
            diff = endTime.compareTo(other.endTime);
        }
        return diff;
    }
}
