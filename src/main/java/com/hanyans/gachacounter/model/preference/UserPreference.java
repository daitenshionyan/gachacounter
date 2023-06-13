package com.hanyans.gachacounter.model.preference;

import java.nio.file.Path;
import java.util.Objects;

import org.apache.logging.log4j.Level;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;


/**
 * Represents the user's preference.
 */
public class UserPreference {
    private static final Level DEFAULT_LOG_LEVEL = Level.INFO;

    @JsonIgnore private Path dataFilePathHsr;
    @JsonIgnore private final Object dataFilePathHSRLock = new Object();
    @JsonIgnore private Path dataFilePathGenshin;
    @JsonIgnore private final Object dataFilePathGenshinLock = new Object();
    @JsonIgnore private Level logLevel;
    @JsonIgnore private final Object logLevelLock = new Object();
    @JsonIgnore private boolean checkUpdatesOnStart;
    @JsonIgnore private final Object checkUpdateOnStartLock = new Object();


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
        this.dataFilePathHsr = dataFilePathHSR;
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
        setDataFilePathHsr(other.getDataFilePathHsr());
        setDataFilePathGenshin(other.getDataFilePathGenshin());
        setLogLevel(other.getLogLevel());
        setCheckUpdateOnStart(other.isCheckUpdateOnStart());
        chartPrefs.resetTo(other.getChartPreference());
    }


    public void setDataFilePathHsr(Path path) {
        synchronized (dataFilePathHSRLock) {
            this.dataFilePathHsr = path;
        }
    }


    /**
     * Returns the set web cache data path for HSR. If path is not yet set,
     * {@code null} is returned.
     */
    @JsonProperty("dataFilePathHSR")
    public Path getDataFilePathHsr() {
        synchronized (dataFilePathHSRLock) {
            return dataFilePathHsr;
        }
    }


    public void setDataFilePathGenshin(Path path) {
        synchronized (dataFilePathGenshinLock) {
            this.dataFilePathGenshin = path;
        }
    }


    /**
     * Returns the set web cache data path for Genshin. If path is not yet set,
     * {@code null} is returned.
     */
    @JsonProperty("dataFilePathGenshin")
    public Path getDataFilePathGenshin() {
        synchronized (dataFilePathGenshinLock) {
            return dataFilePathGenshin;
        }
    }


    /**
     * Sets the log level as specified. If {@code null} is given, {@code INFO}
     * is assumed.
     */
    public void setLogLevel(Level logLevel) {
        synchronized (logLevelLock) {
            this.logLevel = Objects.requireNonNullElse(logLevel, DEFAULT_LOG_LEVEL);
        }
    }


    @JsonProperty("logLevel")
    public Level getLogLevel() {
        synchronized (logLevelLock) {
            return logLevel;
        }
    }


    public void setCheckUpdateOnStart(boolean shouldCheck) {
        synchronized (checkUpdateOnStartLock) {
            checkUpdatesOnStart = shouldCheck;
        }
    }


    @JsonProperty("checkUpdateOnStart")
    public boolean isCheckUpdateOnStart() {
        synchronized (checkUpdateOnStartLock) {
            return checkUpdatesOnStart;
        }
    }


    @JsonProperty("chartPrefs")
    public ChartPreference getChartPreference() {
        return chartPrefs;
    }


    public UserPreference getCopy() {
        UserPreference preference = new UserPreference();
        preference.resetTo(this);
        return preference;
    }
}
