package com.hanyans.gachacounter.model.preference;

import com.fasterxml.jackson.annotation.JsonCreator;
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


    public synchronized void setPityStep5Norm(int step) throws IllegalArgumentException {
        this.pityStep5Norm = validateStep(step, Constants.MAX_PITY_5_NORM);
    }


    public synchronized int getPityStep5Norm() {
        return pityStep5Norm;
    }


    public synchronized void setPityStep5Weap(int step) throws IllegalArgumentException {
        this.pityStep5Weap = validateStep(step, Constants.MAX_PITY_5_WEAP);
    }


    public synchronized int getPityStep5Weap() {
        return pityStep5Weap;
    }


    public synchronized void setPityStep4(int step) throws IllegalArgumentException {
        this.pityStep4 = validateStep(step, Constants.MAX_PITY_4);
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
