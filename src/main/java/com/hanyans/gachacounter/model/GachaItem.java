package com.hanyans.gachacounter.model;

import java.util.Objects;

import com.hanyans.gachacounter.mhy.ItemType;

/**
 * Represents an item obtained from gacha. Unlike {@link GachaEntry},
 * {@code GachaItem} contains information about the item only and their
 * equality are evaluated based on their {@link #itemId}.
 */
public class GachaItem {
    /** ID of the item. */
    public final int itemId;
    /** Name of the item. */
    public final String name;
    /** Rank of the item. */
    public final int rank;
    /** Item type of the item. */
    public final ItemType itemType;


    public GachaItem(int itemId, String name, int rank, ItemType itemType) {
        this.itemId = itemId;
        this.name = name;
        this.rank = rank;
        this.itemType = itemType;
    }


    /**
     * Converts the given {@code GachaEntry} to a {@code GachaItem}.
     *
     * @param entry - the {@code GachaEntry} to convert.
     */
    public static GachaItem fromGachaEntry(GachaEntry entry) {
        return new GachaItem(entry.itemId, entry.name, entry.rank, entry.itemType);
    }


    @Override
    public String toString() {
        return name;
    }


    @Override
    public boolean equals(Object other) {
        if (other == null || !(other instanceof GachaItem)) {
            return false;
        }
        GachaItem casted = (GachaItem) other;
        return this.itemId == casted.itemId
                && this.name.equals(casted.name);
    }


    @Override
    public int hashCode() {
        return Objects.hash(itemId, name);
    }
}
