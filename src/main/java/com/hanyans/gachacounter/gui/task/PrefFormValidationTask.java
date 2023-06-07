package com.hanyans.gachacounter.gui.task;

import java.util.ArrayList;

import org.apache.logging.log4j.Level;

import com.hanyans.gachacounter.core.task.RunnableTask;
import com.hanyans.gachacounter.gui.popup.FormInputBox;
import com.hanyans.gachacounter.model.preference.ChartPreference;
import com.hanyans.gachacounter.model.preference.UserPreference;

import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;


public class PrefFormValidationTask extends RunnableTask<UserPreference> {
    private final UserPreference reference;

    private final FormInputBox pityStep5NormInput;
    private final FormInputBox pityStep5WeapInput;
    private final FormInputBox pityStep4Input;
    private final FormInputBox freqStepFactInput;
    private final FormInputBox freqMaxCountInput;

    private final CheckBox checkUpdatesOnStartCB;
    private final ComboBox<Level> logLevelCmbBox;


    public PrefFormValidationTask(
            UserPreference reference,
            FormInputBox pityStep5NormInput,
            FormInputBox pityStep5WeapInput,
            FormInputBox pityStep4Input,
            FormInputBox freqStepFactInput,
            FormInputBox freqMaxCountInput,
            CheckBox checkUpdatesOnStartCB,
            ComboBox<Level> logLevelCmbBox) {
        this.reference = reference.getCopy();
        this.pityStep5NormInput = pityStep5NormInput;
        this.pityStep5WeapInput = pityStep5WeapInput;
        this.pityStep4Input = pityStep4Input;
        this.freqStepFactInput = freqStepFactInput;
        this.freqMaxCountInput = freqMaxCountInput;
        this.checkUpdatesOnStartCB = checkUpdatesOnStartCB;
        this.logLevelCmbBox = logLevelCmbBox;
    }


    @Override
    public UserPreference performTask() throws Throwable {
        final ArrayList<Throwable> exList = new ArrayList<>();

        ChartPreference chartPrefs = reference.getChartPreference();
        pityStep5NormInput.processAsInteger(chartPrefs::setPityStep5Norm)
                .ifPresent(exList::add);
        pityStep5WeapInput.processAsInteger(chartPrefs::setPityStep5Weap)
                .ifPresent(exList::add);
        pityStep4Input.processAsInteger(chartPrefs::setPityStep4)
                .ifPresent(exList::add);
        freqStepFactInput.processAsInteger(chartPrefs::setFreqMarkingStepFactor)
                .ifPresent(exList::add);
        freqMaxCountInput.processAsInteger(chartPrefs::setFreqMarkingMaxCount)
                .ifPresent(exList::add);

        reference.setCheckUpdateOnStart(checkUpdatesOnStartCB.isSelected());
        reference.setLogLevel(logLevelCmbBox.getSelectionModel().getSelectedItem());

        if (!exList.isEmpty()) {
            throw new IllegalArgumentException("Invalid values present in fields");
        }
        return reference;
    }
}
