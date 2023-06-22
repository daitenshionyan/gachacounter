package com.hanyans.gachacounter.model;

import java.time.LocalDateTime;
import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.hanyans.gachacounter.mhy.GachaType;
import com.hanyans.gachacounter.mhy.ItemType;
import com.hanyans.gachacounter.mhy.response.GachaEntryResponse;


/**
 * A data class that contains information about a gacha entry. Similar to
 * {@link GachaEntryResponse} but without the UID and language data.
 *
 * <p>{@code GachaEntry} equality are evaluated by both their {@link #id} and
 * {@link #name} and are ordered based on their {@link #time} followed by their
 * {@link #id} then {@link #name} from ealiest to latest, lowest to highest and
 * alphabetical order respectively.
 */
public class GachaEntry implements Comparable<GachaEntry> {
    /** UID of the player who owns the entry. */
    public final long uid;
    /** Gacha ID of the entry. */
    public final int gachaId;
    /** {@code GachaType} of the entry. */
    public final GachaType gachaType;
    /** ID of the item in the entry. */
    public final int itemId;
    /** Count of the item in the entry. */
    public final int count;
    /** Time the entry was made. */
    public final LocalDateTime time;
    /** Name of the item in the entry. */
    public final String name;
    /** Item type of the item in the entry. */
    public final ItemType itemType;
    /** Rank of the item in the entry. */
    public final int rank;
    /** ID of the entry. */
    public final long id;


    @JsonCreator
    public GachaEntry(
                @JsonProperty("uid") long uid,
                @JsonProperty("gachaId") int gachaId,
                @JsonProperty("gachaType") GachaType gachaType,
                @JsonProperty("itemId") int itemId,
                @JsonProperty("count") int count,
                @JsonProperty("time") LocalDateTime time,
                @JsonProperty("name") String name,
                @JsonProperty("itemType") ItemType itemType,
                @JsonProperty("rank") int rank,
                @JsonProperty("id") long id) {
        this.uid = uid;
        this.gachaId = gachaId;
        this.gachaType = Objects.requireNonNull(gachaType);
        this.itemId = itemId;
        this.count = count;
        this.time = Objects.requireNonNull(time);
        this.name = Objects.requireNonNull(name);
        this.itemType = Objects.requireNonNull(itemType);
        this.rank = rank;
        this.id = id;
    }


    /**
     * Constructs a {@code GachaEntry} with reference to another.
     *
     * @param other - the reference {@code GachaEntry}.
     * @throws NullPointerException if {@code other} is {@code null}.
     */
    public GachaEntry(GachaEntry other) {
        Objects.requireNonNull(other);
        this.uid = other.uid;
        this.gachaId = other.gachaId;
        this.gachaType = other.gachaType;
        this.itemId = other.itemId;
        this.count = other.count;
        this.time = other.time;
        this.name = other.name;
        this.itemType = other.itemType;
        this.rank = other.rank;
        this.id = other.id;
    }


    /**
     * Converts the given {@code GachaEntryResponse} to a {@code GachaEntry}.
     *
     * @param response - the response to convert.
     */
    public static GachaEntry fromGachaEntryResponse(GachaEntryResponse response, GachaType gachaType) {
        return new GachaEntry(
                response.uid,
                response.gacha_id,
                gachaType,
                response.item_id,
                response.count,
                response.time,
                response.name,
                ItemType.getItemType(response.item_type),
                response.rank_type,
                response.id);
    }


    @Override
    public boolean equals(Object other) {
        if (other == null || !(other instanceof GachaEntry)) {
            return false;
        }
        GachaEntry casted = (GachaEntry) other;
        return this.id == casted.id
                && this.name.equals(casted.name);
    }


    @Override
    public int hashCode() {
        return Objects.hash(id, name);
    }


    @Override
    public int compareTo(GachaEntry other) {
        int diff = this.time.compareTo(other.time);
        if (diff == 0) {
            diff = Long.valueOf(id).compareTo(other.id);
        }
        if (diff == 0) {
            diff = name.compareTo(other.name);
        }
        return diff;
    }
}
