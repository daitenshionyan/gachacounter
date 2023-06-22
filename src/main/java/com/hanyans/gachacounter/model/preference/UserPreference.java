package com.hanyans.gachacounter.model.preference;

import java.nio.file.Path;
import java.util.Objects;

import org.apache.logging.log4j.Level;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.hanyans.gachacounter.core.LockedValue;


/**
 * Represents the user's preference.
 */
public class UserPreference {
    private static final Level DEFAULT_LOG_LEVEL = Level.INFO;

    @JsonIgnore private LockedValue<Path> dataFilePathHsr;
    @JsonIgnore private LockedValue<Path> dataFilePathGenshin;
    @JsonIgnore private LockedValue<Level> logLevel;
    @JsonIgnore private LockedValue<Boolean> checkUpdatesOnStart;

    @JsonIgnore private ChartPreference chartPrefs;


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
        this.dataFilePathHsr = new LockedValue<>(dataFilePathHSR);
        this.dataFilePathGenshin = new LockedValue<>(dataFilePathGenshin);
        this.logLevel = new LockedValue<>(Objects.requireNonNullElse(logLevel, DEFAULT_LOG_LEVEL));
        this.checkUpdatesOnStart = new LockedValue<>(Objects.requireNonNullElse(checkUpdatesOnStart, true));
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
        dataFilePathHsr.set(path);
    }


    /**
     * Returns the set web cache data path for HSR. If path is not yet set,
     * {@code null} is returned.
     */
    @JsonProperty("dataFilePathHSR")
    public Path getDataFilePathHsr() {
        return dataFilePathHsr.get();
    }


    public void setDataFilePathGenshin(Path path) {
        dataFilePathGenshin.set(path);
    }


    /**
     * Returns the set web cache data path for Genshin. If path is not yet set,
     * {@code null} is returned.
     */
    @JsonProperty("dataFilePathGenshin")
    public Path getDataFilePathGenshin() {
        return dataFilePathGenshin.get();
    }


    /**
     * Sets the log level as specified. If {@code null} is given, {@code INFO}
     * is assumed.
     */
    public void setLogLevel(Level logLevel) {
        this.logLevel.set(Objects.requireNonNullElse(logLevel, DEFAULT_LOG_LEVEL));
    }


    @JsonProperty("logLevel")
    public Level getLogLevel() {
        return logLevel.get();
    }


    public void setCheckUpdateOnStart(boolean shouldCheck) {
        checkUpdatesOnStart.set(shouldCheck);
    }


    @JsonProperty("checkUpdateOnStart")
    public boolean isCheckUpdateOnStart() {
        return checkUpdatesOnStart.get();
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
