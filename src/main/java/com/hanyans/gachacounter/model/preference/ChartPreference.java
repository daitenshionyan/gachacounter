package com.hanyans.gachacounter.model.preference;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;


/**
 * Represents the preference settings for charts.
 */
public class ChartPreference {
    public static final int PITY_STEP_5_NORM_DEFAULT = 5;
    public static final int PITY_STEP_5_WEAP_DEFAULT = 5;
    public static final int PITY_STEP_4_DEFAULT = 1;

    public static final int FREQ_MARKING_STEP_FACTOR = 5;
    public static final int FREQ_MARKING_MAX_COUNT = 10;

    private int pityStep5Norm;
    private int pityStep5Weap;
    private int pityStep4;

    private int freqMarkingStepFactor;
    private int freqMarkingMaxCount;


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
        this.pityStep5Norm = validateStep(pityStep5Norm);
        this.pityStep5Weap = validateStep(pityStep5Weap);
        this.pityStep4 = validateStep(pityStep4);
        this.freqMarkingStepFactor = validateFactor(freqMarkingStepFactor);
        this.freqMarkingMaxCount = validateCount(freqMarkingMaxCount);
    }


    public synchronized void setPityStep5Norm(int step) throws IllegalArgumentException {
        this.pityStep5Norm = validateStep(step);
    }


    public synchronized int getPityStep5Norm() {
        return pityStep5Norm;
    }


    public synchronized void setPityStep5Weap(int step) throws IllegalArgumentException {
        this.pityStep5Weap = validateStep(step);
    }


    public synchronized int getPityStep5Weap() {
        return pityStep5Weap;
    }


    public synchronized void setPityStep4(int step) throws IllegalArgumentException {
        this.pityStep4 = validateStep(step);
    }


    public int getPityStep4() {
        return pityStep4;
    }


    public synchronized void setFreqMarkingStepFactor(int factor) throws IllegalArgumentException {
        this.freqMarkingStepFactor = validateFactor(factor);
    }


    public int getFreqMarkingStepFactor() {
        return freqMarkingStepFactor;
    }


    public synchronized void setFreqMarkingMaxCount(int count) throws IllegalArgumentException {
        this.freqMarkingMaxCount = validateCount(count);
    }


    public synchronized int getFreqMarkingMaxCount() {
        return freqMarkingMaxCount;
    }


    private int validateStep(int step) throws IllegalArgumentException {
        if (step <= 0) {
            throw new IllegalArgumentException(
                    String.format("Zero or negative step (%d)", step));
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
