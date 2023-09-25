package com.hanyans.gachacounter.gui.task;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.hanyans.gachacounter.core.Constants;
import com.hanyans.gachacounter.core.FrequencyMap;
import com.hanyans.gachacounter.core.task.ConsumerTask;
import com.hanyans.gachacounter.gui.GachaItemCountBox;
import com.hanyans.gachacounter.gui.updater.BannerCardUpdater;
import com.hanyans.gachacounter.gui.updater.OverallCardUpdater;
import com.hanyans.gachacounter.gui.updater.StatisticsUpdater;
import com.hanyans.gachacounter.model.GachaItem;
import com.hanyans.gachacounter.model.UidNameMap;
import com.hanyans.gachacounter.model.count.AccPityFreqMap;
import com.hanyans.gachacounter.model.count.GachaReport;
import com.hanyans.gachacounter.model.count.ProcessedGachaEntry;
import com.hanyans.gachacounter.model.preference.ChartPreference;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.chart.XYChart;
import javafx.scene.layout.Pane;


public class OverviewRenderTask extends ConsumerTask<GachaReport> {
    private final Logger logger = LogManager.getFormatterLogger(OverviewRenderTask.class);

    private final Pane bannerBox;
    private final BannerCardUpdater stndUpdater;
    private final BannerCardUpdater charUpdater;
    private final BannerCardUpdater weapUpdater;
    private final OverallCardUpdater overallUpdater;
    private final StatisticsUpdater statisticsUpdater;
    private final ChartPreference chartPrefs;


    public OverviewRenderTask(
                Pane bannerBox,
                BannerCardUpdater stndUpdater,
                BannerCardUpdater charUpdater,
                BannerCardUpdater weapUpdater,
                OverallCardUpdater overallCardUpdater,
                StatisticsUpdater statisticsUpdater,
                ChartPreference chartPrefs) {
        this.bannerBox = bannerBox;
        this.stndUpdater = stndUpdater;
        this.charUpdater = charUpdater;
        this.weapUpdater = weapUpdater;
        this.overallUpdater = overallCardUpdater;
        this.statisticsUpdater = statisticsUpdater;
        this.chartPrefs = chartPrefs;
    }


    @Override
    protected void performTask(GachaReport report) throws Throwable {
        logger.debug("Started overview render task");

        ArrayList<Node> boxes = renderItemBoxes(report);
        StatisticsUpdater.Arguments statsArgs = formStatsArgs(report);

        // Displaying
        CountDownLatch latch = new CountDownLatch(1);
        Platform.runLater(() -> updatePanel(report, boxes, statsArgs, latch));
        latch.await();

        logger.info("Completed overview render task in %d ms",
                getRunTime());
    }


    private void updatePanel(
                GachaReport report,
                List<Node> entryBoxes,
                StatisticsUpdater.Arguments statsArgs,
                CountDownLatch latch) {
        if (report.uids.size() > 1) {
            bannerBox.setVisible(false);
            bannerBox.setManaged(false);
        } else {
            bannerBox.setVisible(true);
            bannerBox.setManaged(true);
            stndUpdater.update(report.stndReport);
            charUpdater.update(report.charReport);
            weapUpdater.update(report.weapReport);
        }
        overallUpdater.update(report.total, entryBoxes);
        statisticsUpdater.update(statsArgs);
        latch.countDown();
    }


    /*
     * ========================================================================
     *      GACHA ITEM BOX
     * ========================================================================
     */


    private ArrayList<Node> renderItemBoxes(GachaReport report) {
        long startTime = System.currentTimeMillis();
        List<GachaItem> items = report.overallCount.keySet().stream()
                .sorted((e1, e2) -> {
                    int diff = e2.rank - e1.rank;
                    if (diff == 0) {
                        diff = e1.itemType.compareTo(e2.itemType);
                    }
                    if (diff == 0) {
                        diff = e1.name.compareTo(e2.name);
                    }
                    return diff;
                })
                .toList();
        ArrayList<Node> boxes = new ArrayList<>();
        for (int i = 0; i < items.size(); i++) {
            GachaItem item = items.get(i);
            HashSet<ProcessedGachaEntry> entrySet = report.overallCount.get(item);
            boxes.add(new GachaItemCountBox(report.game, item, entrySet, report.uidNameMap).getRoot());
            setProgress(i + 1, items.size());
            setMessage(String.format("%.2f%%\n%s",
                    progressProperty().get() * 100D,
                    item.name));
        }

        long duration = System.currentTimeMillis() - startTime;
        logger.debug("Completed gacha item box rendering in %d ms", duration);
        return boxes;
    }


    /*
     * ========================================================================
     *      STATISTICS
     * ========================================================================
     */


    private StatisticsUpdater.Arguments formStatsArgs(GachaReport report) {
        long startTime = System.currentTimeMillis();
        StatisticsUpdater.PlotData data5Norm = formPlotData(
                report.uidNameMap, report.freqMap5Norm, chartPrefs.getPityStep5Norm(), Constants.MAX_PITY_5_NORM);
        StatisticsUpdater.PlotData data5Weap = formPlotData(
                report.uidNameMap, report.freqMap5Weap, chartPrefs.getPityStep5Weap(), Constants.MAX_PITY_5_WEAP);
        StatisticsUpdater.PlotData data4 = formPlotData(
                report.uidNameMap, report.freqMap4, chartPrefs.getPityStep4(), Constants.MAX_PITY_4);
        long duration = System.currentTimeMillis() - startTime;
        logger.debug("Completed graph data rendering in %d ms", duration);
        return new StatisticsUpdater.Arguments(data5Norm, data5Weap, data4);
    }


    private StatisticsUpdater.PlotData formPlotData(
                UidNameMap nameMap,
                AccPityFreqMap accFreqMap,
                int pityStep, int maxPity) {
        if (pityStep > 1) {
            accFreqMap = accFreqMap.condense(pityStep);
        }
        ArrayList<XYChart.Series<String, Number>> seriesList = new ArrayList<>();
        FrequencyMap<Integer> combFreqMap = accFreqMap.combineAll();
        for (Map.Entry<Long, FrequencyMap<Integer>> entry : accFreqMap.entrySet()) {
            seriesList.add(formSeries(
                    entry.getKey(), nameMap,
                    entry.getValue(), combFreqMap,
                    pityStep, maxPity));
        }
        int maxFreq = combFreqMap.largestFreq();
        int freqStep = getFreqStep(
                maxFreq,
                chartPrefs.getFreqMarkingStepFactor(),
                chartPrefs.getFreqMarkingMaxCount());
        int upperBound = freqStep;
        if (chartPrefs.getFreqMarkingMaxCount() > 1) {
            upperBound *= (maxFreq / freqStep + 1);
        }
        return new StatisticsUpdater.PlotData(seriesList, freqStep, upperBound);
    }


    private XYChart.Series<String, Number> formSeries(
                long uid, UidNameMap nameMap,
                FrequencyMap<Integer> freqMap, FrequencyMap<Integer> combFreqMap,
                int pityStep, int maxPity) {
        ObservableList<XYChart.Data<String, Number>> datas = FXCollections.observableArrayList();
        // iterate through all pity even if frequency of it is 0 to populate data
        // so that there will not be gaps when the graph is displayed.
        for (int pity = pityStep; pity - maxPity < pityStep; pity += pityStep) {
            datas.add(formData(
                    nameMap.get(uid),
                    pity < maxPity ? pity : maxPity,
                    pity < maxPity ? pityStep : maxPity - (pity - pityStep),
                    freqMap.get(pity),
                    combFreqMap.get(pity)));
        }
        return new XYChart.Series<>(
                nameMap.get(uid),
                datas);
    }


    private XYChart.Data<String, Number> formData(
                String name,
                int pity, int pityStep,
                int freq, int tot) {
        XYChart.Data<String, Number> data = new XYChart.Data<>(String.valueOf(pity), freq);
        if (pityStep > 1) {
            data.setExtraValue(String.format("%s\nPity %d ~ %d\nFreq: %d of %d",
                    name,
                    pity - pityStep + 1,
                    pity,
                    freq,
                    tot));
        } else {
            data.setExtraValue(String.format("%s\nPity %d\nFreq: %d of %d",
                    name, pity, freq, tot));
        }
        return data;
    }


    /**
     * Calculates the step of each major markings for the frequency axis.
     *
     * @param maxFreq - the highest frequency.
     * @param stepFactor - the factor the calculated step should be a factor
     *      of. This is ignored if the calculated step is lower than this
     *      value.
     * @param markingCount - the maximum marking counts not including the
     *      zero marking.
     */
    private int getFreqStep(int maxFreq, int stepFactor, int markingCount) {
        int freqStep = maxFreq;
        if (markingCount > 1) {
            // minus 1 so that there will be padding between maxFreq and graph upper bound
            freqStep = (int) Math.ceil((double) maxFreq / (markingCount - 1));
        }
        if (freqStep < stepFactor) {
            return Math.max(freqStep, 1);
        }
        return freqStep + stepFactor - (freqStep % stepFactor);
    }
}
