package com.hanyans.gachacounter.model.rateup;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.hanyans.gachacounter.model.GachaEntry;
import com.hanyans.gachacounter.model.GachaItem;


/**
 * A class that contains the rate up events that occured for a banner.
 */
public class BannerEventHistory {
    private final ArrayList<BannerEvent> events;


    /**
     * Constructs a {@code RateUpHistory} based on the given collection of
     * {@code RateUpEvent}.
     *
     * <p>The given collection is copied over as such, changes to the given
     * collection after the initialisation will not affect the state of this
     * {@code RateUpHistory}.
     *
     * @param events - the collection of events to be in the history.
     * @throws NullPointerException if {@code events} is {@code null}.
     */
    @JsonCreator
    public BannerEventHistory(@JsonProperty("events") Collection<BannerEvent> events) {
        this.events = new ArrayList<>(events
                .stream()
                .sorted()
                .toList());
    }


    /**
     * Constructs an empty {@code RateUpHistory}.
     */
    public BannerEventHistory() {
        this(List.of());
    }


    public void reset(BannerEventHistory other) {
        events.clear();
        events.addAll(other.events);
    }


    /**
     * Returns {@code true} if the given item is a rate up item at the
     * specified time and {@code false} otherwise.
     *
     * @param item - the item to check.
     * @param time - the time to check.
     */
    public boolean isRateUp(GachaItem item, LocalDateTime time) {
        if (events.isEmpty()) {
            return true;
        }
        for (BannerEvent event : events) {
            if (event.isWithin(time) && event.isRateUp(item)) {
                return true;
            }
        }
        return false;
    }


    /**
     * Returns {@code true} if the item of the entry is a rate up item at the
     * time that it was obtained and {@code false} otherwise.
     *
     * @param entry - the entry to check.
     */
    public boolean isRateUp(GachaEntry entry) {
        return isRateUp(GachaItem.fromGachaEntry(entry), entry.time);
    }


    /**
     * Returns the number of events in this history.
     */
    public int size() {
        return events.size();
    }
}
