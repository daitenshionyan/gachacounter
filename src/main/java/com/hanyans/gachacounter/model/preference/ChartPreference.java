package com.hanyans.gachacounter.model.preference;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.hanyans.gachacounter.core.Constants;
import com.hanyans.gachacounter.core.LockedValue;


/**
 * Represents the preference settings for charts.
 */
public class ChartPreference {
    public static final int PITY_STEP_5_NORM_DEFAULT = 5;
    public static final int PITY_STEP_5_WEAP_DEFAULT = 5;
    public static final int PITY_STEP_4_DEFAULT = 1;

    public static final int FREQ_MARKING_STEP_FACTOR = 5;
    public static final int FREQ_MARKING_MAX_COUNT = 10;

    @JsonIgnore private LockedValue<Integer> pityStep5Norm;
    @JsonIgnore private LockedValue<Integer> pityStep5Weap;
    @JsonIgnore private LockedValue<Integer> pityStep4;

    @JsonIgnore private LockedValue<Integer> freqMarkingStepFactor;
    @JsonIgnore private LockedValue<Integer> freqMarkingMaxCount;


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
        this.pityStep5Norm = new LockedValue<>(validateStep(pityStep5Norm, Constants.MAX_PITY_5_NORM));
        this.pityStep5Weap = new LockedValue<>(validateStep(pityStep5Weap, Constants.MAX_PITY_5_WEAP));
        this.pityStep4 = new LockedValue<>(validateStep(pityStep4, Constants.MAX_PITY_4));
        this.freqMarkingStepFactor = new LockedValue<>(validateFactor(freqMarkingStepFactor));
        this.freqMarkingMaxCount = new LockedValue<>(validateCount(freqMarkingMaxCount));
    }


    public void resetTo(ChartPreference other) {
        setPityStep5Norm(other.getPityStep5Norm());
        setPityStep5Weap(other.getPityStep5Weap());
        setPityStep4(other.getPityStep4());
        setFreqMarkingStepFactor(other.getFreqMarkingStepFactor());
        setFreqMarkingMaxCount(other.getFreqMarkingMaxCount());
    }


    public void setPityStep5Norm(int step) throws IllegalArgumentException {
        pityStep5Norm.set(validateStep(step, Constants.MAX_PITY_5_NORM));
    }


    @JsonProperty("pityStep5Norm")
    public int getPityStep5Norm() {
        return pityStep5Norm.get();
    }


    public void setPityStep5Weap(int step) throws IllegalArgumentException {
        pityStep5Weap.set(validateStep(step, Constants.MAX_PITY_5_WEAP));
    }


    @JsonProperty("pityStep5Weap")
    public int getPityStep5Weap() {
        return pityStep5Weap.get();
    }


    public void setPityStep4(int step) throws IllegalArgumentException {
        pityStep4.set(validateStep(step, Constants.MAX_PITY_4));
    }


    @JsonProperty("pityStep4")
    public int getPityStep4() {
        return pityStep4.get();
    }


    public void setFreqMarkingStepFactor(int factor) throws IllegalArgumentException {
        freqMarkingStepFactor.set(validateFactor(factor));
    }


    @JsonProperty("freqMarkingStepFactor")
    public int getFreqMarkingStepFactor() {
        return freqMarkingStepFactor.get();
    }


    public void setFreqMarkingMaxCount(int count) throws IllegalArgumentException {
        freqMarkingMaxCount.set(validateCount(count));
    }


    @JsonProperty("freqMarkingMaxCount")
    public int getFreqMarkingMaxCount() {
        return freqMarkingMaxCount.get();
    }


    /*
     * ========================================================================
     *      UTILITY
     * ========================================================================
     */


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
