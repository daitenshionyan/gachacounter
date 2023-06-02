package com.hanyans.gachacounter.model.count;

import java.util.Collection;
import java.util.HashSet;

import com.hanyans.gachacounter.wrapper.Game;

/**
 * Data class containing gacha data from all banners.
 */
public class GachaReport {
    /** The game the report is for. */
    public final Game game;
    /** Set of UIDs present in the report. */
    public final HashSet<Long> uids;
    /** Standard banner {@code BannerReport} */
    public final BannerReport stndReport;
    /** Character banner {@code BannerReport} */
    public final BannerReport charReport;
    /** Weapon banner {@code BannerReport} */
    public final BannerReport weapReport;
    /** Total pulls from all banners. */
    public final int total;
    /** {@code GachaItemMap} of all banners. */
    public final GachaItemMap overallCount;
    /** Frequency map of 5 star pity pulls of all banner other than the weapon. */
    public final AccPityFreqMap freqMap5Norm;
    /** Frequency map of 5 star pity pulls of only the weapon banner. */
    public final AccPityFreqMap freqMap5Weap;
    /** Frequency map of 4 star pity pulls of all banners. */
    public final AccPityFreqMap freqMap4;


    /**
     * Constructs a {@code GachaReport}.
     *
     * @param game - game the report is for.
     * @param stndReport - standard banner report.
     * @param charReport - character banner report.
     * @param weapReport - weapon banner report.
     */
    public GachaReport(
                Game game,
                BannerReport stndReport,
                BannerReport charReport,
                BannerReport weapReport) {
        this.game = game;
        this.uids = mergeUidList(stndReport.uids, charReport.uids, weapReport.uids);
        this.stndReport = stndReport;
        this.charReport = charReport;
        this.weapReport = weapReport;
        this.total = stndReport.total + charReport.total + weapReport.total;
        this.overallCount = stndReport.counter
                .merge(charReport.counter)
                .merge(weapReport.counter);
        this.freqMap5Norm = stndReport.freqMap5
                .merge(charReport.freqMap5);
        this.freqMap5Weap = weapReport.freqMap5;
        this.freqMap4 = stndReport.freqMap4
                .merge(charReport.freqMap4)
                .merge(weapReport.freqMap4);
    }


    private HashSet<Long> mergeUidList(
                Collection<Long> stndList,
                Collection<Long> charList,
                Collection<Long> weapList) {
        HashSet<Long> res = new HashSet<>(stndList);
        res.addAll(charList);
        res.addAll(weapList);
        return res;
    }
}
