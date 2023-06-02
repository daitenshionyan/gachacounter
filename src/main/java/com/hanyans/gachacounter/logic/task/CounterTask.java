package com.hanyans.gachacounter.logic.task;

import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.hanyans.gachacounter.core.FrequencyMap;
import com.hanyans.gachacounter.core.task.RunnableTask;
import com.hanyans.gachacounter.model.BannerHistory;
import com.hanyans.gachacounter.model.GachaEntry;
import com.hanyans.gachacounter.model.count.AccPityFreqMap;
import com.hanyans.gachacounter.model.count.BannerReport;
import com.hanyans.gachacounter.model.count.GachaItemMap;
import com.hanyans.gachacounter.model.count.ProcessedGachaEntry;
import com.hanyans.gachacounter.model.rateup.BannerEventHistory;
import com.hanyans.gachacounter.wrapper.GachaType;


/**
 * A {@code RunnableTask} that counts pity and generates a {@code BannerReport}
 * for a specified banner.
 */
public class CounterTask extends RunnableTask<BannerReport> {
    private final Logger logger = LogManager.getFormatterLogger(CounterTask.class);

    private final GachaType gachaType;
    private final HashSet<GachaEntry> entrySet;
    private final BannerEventHistory rateUpMap;
    private final HashSet<Long> uidFilters;


    /**
     * Constructs a {@code CounterTask}.
     *
     * @param bannerHistory - the banner history to count.
     */
    public CounterTask(BannerHistory bannerHistory) {
        this(bannerHistory, new BannerEventHistory());
    }


    public CounterTask(BannerHistory bannerHistory, BannerEventHistory rateUpMap) {
        this(bannerHistory.getGachaType(), bannerHistory.getEntrySet(), rateUpMap, null);
    }


    private CounterTask(
                GachaType gachaType,
                HashSet<GachaEntry> entrySet,
                BannerEventHistory rateUpMap,
                HashSet<Long> uidFilters) {
        this.gachaType = Objects.requireNonNull(gachaType);
        this.entrySet = Objects.requireNonNull(entrySet);
        this.rateUpMap = Objects.requireNonNullElse(rateUpMap, new BannerEventHistory());
        this.uidFilters = Objects.requireNonNullElse(uidFilters, new HashSet<>());
    }


    @Override
    public BannerReport performTask() {
        logger.debug("Started counting task for <%s>", gachaType.name());

        // sort entries by date
        List<GachaEntry> sortedEntries = entrySet.stream()
                .sorted(Comparator.naturalOrder())
                .collect(Collectors.toList());

        // initialize counters
        HashSet<Long> uids = new HashSet<>();
        GachaItemMap counter = new GachaItemMap();
        FrequencyMap<Long> pullSinceLast4 = new FrequencyMap<>();
        FrequencyMap<Long> pullSinceLast5 = new FrequencyMap<>();
        HashMap<Long, Boolean> isRateUp4 = new HashMap<>();
        HashMap<Long, Boolean> isRateUp5 = new HashMap<>();
        AccPityFreqMap freqMap5 = new AccPityFreqMap();
        AccPityFreqMap freqMap4 = new AccPityFreqMap();

        // iterate through entries
        for (int i = 0; i < sortedEntries.size(); i++) {
            GachaEntry entry = sortedEntries.get(i);
            setProgress(i, sortedEntries.size());
            setMessage(String.format("[%s] %s (%d of %d)",
                    gachaType.toString(),
                    entry.toString(),
                    i + 1, sortedEntries.size()));

            if (uidFilters.contains(entry.uid)) {
                continue;
            }

            int pityCount = 0;
            boolean isRateUp = rateUpMap.isRateUp(entry);
            boolean isRateUpWon = false;
            pullSinceLast4.add(entry.uid);
            pullSinceLast5.add(entry.uid);
            switch (entry.rank) {
                case 5:
                    pityCount = pullSinceLast5.get(entry.uid);
                    isRateUpWon = isRateUp5.getOrDefault(entry.uid, true);
                    pullSinceLast4.put(entry.uid, 0);
                    pullSinceLast5.put(entry.uid, 0);
                    isRateUp4.put(entry.uid, isRateUp);
                    isRateUp5.put(entry.uid, isRateUp);
                    freqMap5.add(entry.uid, pityCount);
                    break;
                case 4:
                    pityCount = pullSinceLast4.get(entry.uid);
                    isRateUpWon = isRateUp4.getOrDefault(entry.uid, true);
                    pullSinceLast4.put(entry.uid, 0);
                    isRateUp4.put(entry.uid, isRateUp);
                    freqMap4.add(entry.uid, pityCount);
                    break;
            }

            // add entry count
            uids.add(entry.uid);
            counter.add(new ProcessedGachaEntry(entry, pityCount, isRateUp, isRateUpWon));
        }

        logger.debug("Completed counting task for <%s> in %d ms",
                gachaType.name(), getRunTime());
        setProgress(1D);
        setMessage(String.format("[%s] DONE", gachaType.toString()));
        return new BannerReport(
                uids,
                gachaType,
                pullSinceLast4,
                pullSinceLast5,
                isRateUp4,
                isRateUp5,
                counter,
                freqMap5,
                freqMap4);
    }


    public CounterTask setRateUpMap(BannerEventHistory rateUpMap) {
        return new CounterTask(gachaType, entrySet, rateUpMap, uidFilters);
    }


    public CounterTask setUidFilters(HashSet<Long> uidFilters) {
        return new CounterTask(gachaType, entrySet, rateUpMap, uidFilters);
    }
}
