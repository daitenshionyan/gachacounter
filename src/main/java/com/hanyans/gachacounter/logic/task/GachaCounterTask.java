package com.hanyans.gachacounter.logic.task;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.hanyans.gachacounter.core.task.RunnableTask;
import com.hanyans.gachacounter.model.count.BannerReport;
import com.hanyans.gachacounter.model.count.GachaReport;
import com.hanyans.gachacounter.wrapper.Game;


/**
 * A {@code RunnableTask} to generate a {@code GachaReport}.
 */
public class GachaCounterTask extends RunnableTask<GachaReport> {
    private final Logger logger = LogManager.getFormatterLogger(GachaCounterTask.class);

    private final Game game;

    private final RunnableTask<BannerReport> stndCounter;
    private final RunnableTask<BannerReport> charCounter;
    private final RunnableTask<BannerReport> weapCounter;


    public GachaCounterTask (
                Game game,
                RunnableTask<BannerReport> stndCounter,
                RunnableTask<BannerReport> charCounter,
                RunnableTask<BannerReport> weapCounter) {
        this.game = game;
        this.stndCounter = stndCounter;
        this.charCounter = charCounter;
        this.weapCounter = weapCounter;
    }


    @Override
    public GachaReport performTask() throws Throwable {
        logger.debug("Started gacha counting task");

        // standard
        bindProgressProeprty(stndCounter.progressProperty());
        bindMessageProperty(stndCounter.messageProperty());
        BannerReport stndCount = stndCounter.performTask();

        // character
        bindProgressProeprty(charCounter.progressProperty());
        bindMessageProperty(charCounter.messageProperty());
        BannerReport charCount = charCounter.performTask();

        // weapon
        bindProgressProeprty(weapCounter.progressProperty());
        bindMessageProperty(weapCounter.messageProperty());
        BannerReport weapCount = weapCounter.performTask();

        logger.info("Completed gacha counting task in %d ms",
                getRunTime());
        return new GachaReport(game, stndCount, charCount, weapCount);
    }
}
