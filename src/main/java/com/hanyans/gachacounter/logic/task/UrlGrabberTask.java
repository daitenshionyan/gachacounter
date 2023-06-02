package com.hanyans.gachacounter.logic.task;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.hanyans.gachacounter.core.task.RunnableTask;
import com.hanyans.gachacounter.core.util.FileUtil;


/**
 * A {@code RunnableTask} to grab the player's URL from the specified path.
 */
public class UrlGrabberTask extends RunnableTask<String> {
    private static final String REQ_SEQ = "getGachaLog";

    private final Logger logger = LogManager.getFormatterLogger(UrlGrabberTask.class);

    private final String pathString;


    public UrlGrabberTask(String pathString) {
        this.pathString = pathString;
    }


    @Override
    public String performTask() throws Throwable {
        logger.debug("Started player URL grabbing task");

        String urlLine;
        logger.debug("Grabbing player URL from \"%s\"",
                pathString);
        try (BufferedReader reader = FileUtil.getFileReader(Path.of(pathString))) {
            List<String> lines = reader.lines()
                    .filter(line -> line.contains(REQ_SEQ))
                    .toList();
            if (lines.isEmpty()) {
                throw new IOException("Could not find URL");
            }
            urlLine = lines.get(lines.size() - 1);
        }
        for (String urlString : urlLine.split("\0+")) {
            if (urlString.contains(REQ_SEQ)) {
                logger.info("Completed player URL grabbing task in %d ms",
                        getRunTime());
                return urlString.substring(urlString.indexOf("http"));
            }
        }
        // should not happen
        throw new RuntimeException("Failed to parse URL");
    }
}
