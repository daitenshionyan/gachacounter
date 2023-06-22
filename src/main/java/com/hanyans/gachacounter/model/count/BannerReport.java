package com.hanyans.gachacounter.model.count;

import java.util.HashMap;
import java.util.HashSet;

import com.hanyans.gachacounter.core.FrequencyMap;
import com.hanyans.gachacounter.mhy.GachaType;

/**
 * A data class containing pull data from a banner.
 */
public class BannerReport {
    /** A set of UIDs present in this report. */
    public final HashSet<Long> uids;
    /** {@code GachaType} this report is for. */
    public final GachaType gachaType;
    /** Number of pulls since last 4 star. */
    public final FrequencyMap<Long> pullSince4;
    /** Number of pulls since last 5 star. */
    public final FrequencyMap<Long> pullSince5;
    /** If the last 4 star obtained was a rate up. */
    public final HashMap<Long, Boolean> isRateUp4;
    /** If the last 5 star obtained was a rate up. */
    public final HashMap<Long, Boolean> isRateUp5;
    /** Number of pulls in banner. */
    public final int total;
    /** Item map of banner. */
    public final GachaItemMap counter;
    /** Frequency map of 5 star pity pulls. */
    public final AccPityFreqMap freqMap5;
    /** Frequency map of 4 star pity pulls. */
    public final AccPityFreqMap freqMap4;


    public BannerReport(
                HashSet<Long> uids,
                GachaType gachaType,
                FrequencyMap<Long> pullSince4,
                FrequencyMap<Long> pullSince5,
                HashMap<Long, Boolean> isRateUp4,
                HashMap<Long, Boolean> isRateUp5,
                GachaItemMap counter,
                AccPityFreqMap freqMap5,
                AccPityFreqMap freqMap4) {
        this.uids = uids;
        this.gachaType = gachaType;
        this.pullSince4 = pullSince4;
        this.pullSince5 = pullSince5;
        this.isRateUp4 = isRateUp4;
        this.isRateUp5 = isRateUp5;
        this.total = counter.size();
        this.counter = counter;
        this.freqMap5 = freqMap5;
        this.freqMap4 = freqMap4;
    }
}
