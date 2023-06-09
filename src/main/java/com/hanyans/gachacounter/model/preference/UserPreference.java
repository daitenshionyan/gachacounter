package com.hanyans.gachacounter.model.preference;

import java.nio.file.Path;
import java.util.Objects;

import org.apache.logging.log4j.Level;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;


/**
 * Represents the user's preference.
 */
public class UserPreference {
    private static final Level DEFAULT_LOG_LEVEL = Level.INFO;

    private Path dataFilePathHSR;
    private Path dataFilePathGenshin;
    private Level logLevel;
    private boolean checkUpdatesOnStart;

    private ChartPreference chartPrefs;


    public UserPreference() {
        this(null, null, null, null, null);
    }


    @JsonCreator
    public UserPreference(
                @JsonProperty("dataFilePathHSR") Path dataFilePathHSR,
                @JsonProperty("dataFilePathGenshin") Path dataFilePathGenshin,
                @JsonProperty("logLevel") Level logLevel,
                @JsonProperty("checkUpdateOnStart") Boolean checkUpdatesOnStart,
                @JsonProperty("chartPrefs") ChartPreference chartPrefs) {
        this.dataFilePathHSR = dataFilePathHSR;
        this.dataFilePathGenshin = dataFilePathGenshin;
        this.logLevel = Objects.requireNonNullElse(logLevel, DEFAULT_LOG_LEVEL);
        this.checkUpdatesOnStart = Objects.requireNonNullElse(checkUpdatesOnStart, true);
        this.chartPrefs = Objects.requireNonNullElse(chartPrefs, new ChartPreference());
    }


    /**
     * Replaces the data of this with the given {@code UserPreference}.
     *
     * @param other - the preference data to copy over.
     */
    public void resetTo(UserPreference other) {
        setDataFilePathHSR(other.getDataFilePathHsr());
        setDataFilePathGenshin(other.getDataFilePathGenshin());
        setLogLevel(other.getLogLevel());
        setCheckUpdateOnStart(other.isCheckUpdateOnStart());
        chartPrefs.resetTo(other.getChartPreference());
    }


    public synchronized void setDataFilePathHSR(Path dataFilePath) {
        this.dataFilePathHSR = dataFilePath;
    }


    /**
     * Returns the set data file path. Can be {@code null} if not set.
     */
    public synchronized Path getDataFilePathHsr() {
        return dataFilePathHSR;
    }


    public synchronized void setDataFilePathGenshin(Path dataFilePath) {
        this.dataFilePathGenshin = dataFilePath;
    }


    public synchronized Path getDataFilePathGenshin() {
        return dataFilePathGenshin;
    }


    public synchronized void setLogLevel(Level logLevel) {
        this.logLevel = Objects.requireNonNullElse(logLevel, DEFAULT_LOG_LEVEL);
    }


    public synchronized Level getLogLevel() {
        return logLevel;
    }


    public synchronized boolean isCheckUpdateOnStart() {
        return checkUpdatesOnStart;
    }


    public synchronized void setCheckUpdateOnStart(boolean shouldCheck) {
        checkUpdatesOnStart = shouldCheck;
    }


    public synchronized ChartPreference getChartPreference() {
        return chartPrefs;
    }


    public UserPreference getCopy() {
        UserPreference preference = new UserPreference();
        preference.resetTo(this);
        return preference;
    }
}
