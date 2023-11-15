package com.hanyans.gachacounter.model.count;

import java.util.Optional;

import com.hanyans.gachacounter.model.GachaEntry;

/**
 * Represents a {@code GachaEntry} that has been processed.
 */
public class ProcessedGachaEntry extends GachaEntry {
    /** Pity count of entry. */
    public final int pityCount;
    /** If the entry is a rate up pull. */
    public final boolean isRateUp;
    /** If this item won rate up. */
    public final boolean isRateUpWon;
    public final Optional<ProcessedGachaEntry> prevRankEntry;


    public ProcessedGachaEntry(
                GachaEntry gachaEntry,
                int pityCount,
                boolean isRateUp,
                boolean isRateUpWon,
                ProcessedGachaEntry prevEntry) {
        super(gachaEntry);
        this.pityCount = pityCount;
        this.isRateUp = isRateUp;
        this.isRateUpWon = isRateUpWon;
        this.prevRankEntry = Optional.ofNullable((prevEntry));
    }
}
