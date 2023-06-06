package com.hanyans.gachacounter.logic.task;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.kohsuke.github.GHRelease;
import org.kohsuke.github.GitHub;

import com.hanyans.gachacounter.core.AppUpdateMessage;
import com.hanyans.gachacounter.core.Version;
import com.hanyans.gachacounter.core.task.RunnableTask;


public class AppUpdateCheckTask extends RunnableTask<AppUpdateMessage> {
    private static final String REPO_NAME = "daitenshionyan/gachacounter";

    private final Logger logger = LogManager.getFormatterLogger(AppUpdateCheckTask.class);

    private final Version appVer;


    public AppUpdateCheckTask(Version appVer) {
        this.appVer = appVer;
    }


    @Override
    public AppUpdateMessage performTask() throws Throwable {
        setMessage("Checking for updates...");
        setProgress(-1D);
        GHRelease release = GitHub.connectAnonymously().getRepository(REPO_NAME)
                .getLatestRelease();
        Version srcVer = Version.parse(release.getTagName());
        if (appVer.isBefore(srcVer)) {
            logger.info("New update available (%s -> %s)", appVer, srcVer);
            return formMessage(true, release);
        }
        logger.info("Application is up to date");
        return formMessage(false, release);
    }


    private AppUpdateMessage formMessage(boolean hasUpdate, GHRelease release) {
        return new AppUpdateMessage(
                hasUpdate,
                String.format("A new release is available (%s -> %s)",
                        appVer, release.getTagName()),
                release.getHtmlUrl());
    }
}
