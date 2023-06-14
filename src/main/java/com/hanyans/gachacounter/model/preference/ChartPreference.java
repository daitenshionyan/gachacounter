package com.hanyans.gachacounter.model.preference;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.hanyans.gachacounter.core.Constants;


/**
 * Represents the preference settings for charts.
 */
public class ChartPreference {
    public static final int PITY_STEP_5_NORM_DEFAULT = 5;
    public static final int PITY_STEP_5_WEAP_DEFAULT = 5;
    public static final int PITY_STEP_4_DEFAULT = 1;

    public static final int FREQ_MARKING_STEP_FACTOR = 5;
    public static final int FREQ_MARKING_MAX_COUNT = 10;

    @JsonIgnore private int pityStep5Norm;
    @JsonIgnore private final Object pityStep5NormLock = new Object();
    @JsonIgnore private int pityStep5Weap;
    @JsonIgnore private final Object pityStep5WeapLock = new Object();
    @JsonIgnore private int pityStep4;
    @JsonIgnore private final Object pityStep4Lock = new Object();

    @JsonIgnore private int freqMarkingStepFactor;
    @JsonIgnore private final Object freqMarkingStepFactorLock = new Object();
    @JsonIgnore private int freqMarkingMaxCount;
    @JsonIgnore private final Object freqMarkingMaxCountLock = new Object();


    /**
     * Constructs a {@code ChartPreference} with its parameters set as its
     * defaults.
     */
    public ChartPreference() {
        this(
            PITY_STEP_5_NORM_DEFAULT,
            PITY_STEP_5_WEAP_DEFAULT,
            PITY_STEP_4_DEFAULT,
            FREQ_MARKING_STEP_FACTOR,
            FREQ_MARKING_MAX_COUNT
        );
    }


    @JsonCreator
    public ChartPreference(
            @JsonProperty("pityStep5Norm") int pityStep5Norm,
            @JsonProperty("pityStep5Weap") int pityStep5Weap,
            @JsonProperty("pityStep4") int pityStep4,
            @JsonProperty("freqMarkingStepFactor") int freqMarkingStepFactor,
            @JsonProperty("freqMarkingMaxCount") int freqMarkingMaxCount) {
        this.pityStep5Norm = validateStep(pityStep5Norm, Constants.MAX_PITY_5_NORM);
        this.pityStep5Weap = validateStep(pityStep5Weap, Constants.MAX_PITY_5_WEAP);
        this.pityStep4 = validateStep(pityStep4, Constants.MAX_PITY_4);
        this.freqMarkingStepFactor = validateFactor(freqMarkingStepFactor);
        this.freqMarkingMaxCount = validateCount(freqMarkingMaxCount);
    }


    public void resetTo(ChartPreference other) {
        setPityStep5Norm(other.getPityStep5Norm());
        setPityStep5Weap(other.getPityStep5Weap());
        setPityStep4(other.pityStep4);
        setFreqMarkingStepFactor(other.getFreqMarkingStepFactor());
        setFreqMarkingMaxCount(other.getFreqMarkingMaxCount());
    }


    public void setPityStep5Norm(int step) throws IllegalArgumentException {
        synchronized (pityStep5NormLock) {
            pityStep5Norm = validateStep(step, Constants.MAX_PITY_5_NORM);
        }
    }


    @JsonProperty("pityStep5Norm")
    public int getPityStep5Norm() {
        synchronized (pityStep5NormLock) {
            return pityStep5Norm;
        }
    }


    public void setPityStep5Weap(int step) throws IllegalArgumentException {
        synchronized (pityStep5WeapLock) {
            pityStep5Weap = validateStep(step, Constants.MAX_PITY_5_WEAP);
        }
    }


    @JsonProperty("pityStep5Weap")
    public int getPityStep5Weap() {
        synchronized (pityStep5WeapLock) {
            return pityStep5Weap;
        }
    }


    public void setPityStep4(int step) throws IllegalArgumentException {
        synchronized (pityStep4Lock) {
            this.pityStep4 = validateStep(step, Constants.MAX_PITY_4);
        }
    }


    @JsonProperty("pityStep4")
    public int getPityStep4() {
        synchronized (pityStep4Lock) {
            return pityStep4;
        }
    }


    public void setFreqMarkingStepFactor(int factor) throws IllegalArgumentException {
        synchronized (freqMarkingStepFactorLock) {
            this.freqMarkingStepFactor = validateFactor(factor);
        }
    }


    @JsonProperty("freqMarkingStepFactor")
    public int getFreqMarkingStepFactor() {
        synchronized (freqMarkingStepFactorLock) {
            return freqMarkingStepFactor;
        }
    }


    public void setFreqMarkingMaxCount(int count) throws IllegalArgumentException {
        synchronized (freqMarkingMaxCountLock) {
            this.freqMarkingMaxCount = validateCount(count);
        }
    }


    @JsonProperty("freqMarkingMaxCount")
    public int getFreqMarkingMaxCount() {
        synchronized (freqMarkingMaxCountLock) {
            return freqMarkingMaxCount;
        }
    }


    private int validateStep(int step, int max) throws IllegalArgumentException {
        if (step <= 0 || step > max) {
            throw new IllegalArgumentException(
                    String.format("Step (%d) not within 0 < x <= %d", step, max));
        }
        return step;
    }


    private int validateFactor(int factor) throws IllegalArgumentException {
        if (factor <= 0) {
            throw new IllegalArgumentException(
                    String.format("Zero or negative factor (%d)", factor));
        }
        return factor;
    }


    private int validateCount(int count) throws IllegalArgumentException {
        if (count <= 0) {
            throw new IllegalArgumentException(
                    String.format("Zero or negative factor (%d)", count));
        }
        return count;
    }
}
