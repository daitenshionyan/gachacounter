package com.hanyans.gachacounter.logic.task;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collection;
import java.util.List;
import java.util.Random;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.hanyans.gachacounter.core.task.RunnableTask;
import com.hanyans.gachacounter.core.util.JsonUtil;
import com.hanyans.gachacounter.mhy.GachaType;
import com.hanyans.gachacounter.mhy.Game;
import com.hanyans.gachacounter.mhy.HistoryRetriever;
import com.hanyans.gachacounter.mhy.PlayerDetail;
import com.hanyans.gachacounter.mhy.exception.ResponseException;
import com.hanyans.gachacounter.mhy.response.GachaResponseResult;
import com.hanyans.gachacounter.model.BannerHistory;
import com.hanyans.gachacounter.model.GachaEntry;
import com.hanyans.gachacounter.model.GameGachaData;


/**
 * A {@code RunnableTask} to retrieve and add new gacha entries from all
 * banners.
 */
public class HistoryRetrieverTask extends RunnableTask<Integer> {
    private static final int PAGE_SIZE = 5;
    private static final long BASE_SLEEP_TIME = 1000;
    private static final long SLEEP_VARIATION = 2500;
    private static final long TIME_STEP = 100;

    private final Logger logger = LogManager.getFormatterLogger(HistoryRetrieverTask.class);

    private final Random rng = new Random();

    private final String playerUrl;
    private final Game game;

    private final BannerHistory stndHist;
    private final BannerHistory charHist;
    private final BannerHistory weapHist;


    public HistoryRetrieverTask(
                String playerUrl,
                GameGachaData data) {
        this.playerUrl = playerUrl;
        this.game = data.game;
        this.stndHist = data.stndHist;
        this.charHist = data.charHist;
        this.weapHist = data.weapHist;
    }


    @Override
    public Integer performTask() throws Throwable {
        logger.debug("Started history retrieval task");
        HistoryRetriever retriever = PlayerDetail.of(playerUrl).formHistoryRetriever();
        int totalAdded = 0;
        totalAdded += addEntries(GachaType.STANDARD, retriever, stndHist);
        totalAdded += addEntries(GachaType.CHARACTER, retriever, charHist);
        totalAdded += addEntries(GachaType.WEAPON, retriever, weapHist);
        logger.info("Completed history retrieval task, total of %d new entries added in %d ms",
                totalAdded, getRunTime());
        return totalAdded;
    }


    private int addEntries(GachaType gachaType, HistoryRetriever retriever, BannerHistory history)
                throws InterruptedException, ResponseException, IOException {
        logger.debug("Retrieving gacha history for <%s>", gachaType.name());
        int page = 1;
        long endId = 0;
        boolean isEnd = false;
        int totalAdded = 0;

        while (!isEnd) {
            if (isCancelled()) {
                throw new InterruptedException("Task cancelled");
            }
            List<GachaEntry> entries = retrieveGachaLog(retriever, gachaType, page, endId);
            int numAdded = addEntries(entries, history);
            totalAdded += numAdded;
            logger.info("Added %d of %d entries retrieved to <%s> (Total added = %d)",
                    numAdded, entries.size(), gachaType.name(), totalAdded);
            for (GachaEntry entry : entries) {
                logger.trace("Added <%s>", entry.name);
            }
            setMessage(String.format("[%s] Added %s entries",
                    gachaType.toString(), totalAdded));
            if (numAdded < PAGE_SIZE) {
                isEnd = true;
            } else {
                endId = entries.get(entries.size() - 1).id;
                page++;

                // delay next retrieval to avoid too frequent visit error
                long sleepTime = getSleepTime();
                logger.debug("Sleeping %d ms before next request for <%s>",
                        sleepTime, gachaType.name());
                setMessage(String.format("%s (Sleeping %dms)",
                        messageProperty().get(),
                        sleepTime));
                startSleepCycle(sleepTime);
            }
        }

        logger.info("Completed retrieval for <%s> added %d new entries",
                gachaType.name(), totalAdded);
        setMessage(String.format("[%s] Added %s entries (DONE)",
                gachaType.toString(), totalAdded));
        return totalAdded;
    }


    private int addEntries(Collection<GachaEntry> entries, BannerHistory history) {
        int numAdded = 0;
        for (GachaEntry entry : entries) {
            if (history.contains(entry)) {
                break;
            }
            history.add(entry);
            numAdded++;
        }
        return numAdded;
    }


    private List<GachaEntry> retrieveGachaLog(HistoryRetriever retriever, GachaType gachaType, int page, long endId)
                throws ResponseException, IOException {
        // form URL
        retriever = retriever
            .setLang("en") // force en
            .setPage(page)
            .setEndId(endId)
            .setSize(PAGE_SIZE)
            .setGachaType(gachaType, game);
        String urlString = getUrlString(retriever);
        logger.debug(String.join("\n\t",
                "Attempting -{RETRIEVE GACHA LOG}-",
                "page = %d",
                "endId = %d",
                "gachaType = <%s>",
                "URL = %s"),
                page, endId, gachaType.name(), urlString);
        URL url;
        try {
            url = new URL(urlString);
        } catch (MalformedURLException malEx) {
            throw new RuntimeException(malEx);
        }

        // get response
        GachaResponseResult result;
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(url.openStream(), "UTF-8"))) {
            result = JsonUtil.deserialize(reader, GachaResponseResult.class);
        }

        // process response
        if (result.retcode != 0) {
            throw new ResponseException(result.message);
        }
        return result.data.list.stream()
                .map(response -> GachaEntry.fromGachaEntryResponse(response, gachaType))
                .toList();
    }


    private String getUrlString(HistoryRetriever retriever) {
        switch (game) {
            case HSR:
                return retriever.formUrlStringHsr();
            case Genshin:
                return retriever.formUrlStringGenshin();
            default:
                throw new IllegalArgumentException(String.format("Unknown game type <%s>", game));
        }
    }


    private long getSleepTime() {
        return BASE_SLEEP_TIME + rng.nextLong(SLEEP_VARIATION);
    }


    private void startSleepCycle(long time) throws InterruptedException {
        long timeLeft = time;
        setProgress(0D);
        while (timeLeft > 0) {
            if (isCancelled()) {
                throw new InterruptedException("Task cancelled");
            }
            long sleepTime = Math.min(timeLeft, TIME_STEP);
            Thread.sleep(sleepTime);
            timeLeft -= sleepTime;
            setProgress(1 - (double) timeLeft / time);
        }
    }
}
