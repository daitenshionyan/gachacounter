package com.hanyans.gachacounter.gui.task;

import org.apache.logging.log4j.Level;

import com.hanyans.gachacounter.core.task.RunnableTask;
import com.hanyans.gachacounter.model.preference.UserPreference;

import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;


public class PrefFormValidationTask extends RunnableTask<UserPreference> {
    private final UserPreference reference;
    private final CheckBox checkUpdatesOnStartCB;
    private final ComboBox<Level> logLevelCmbBox;


    public PrefFormValidationTask(
            UserPreference reference,
            CheckBox checkUpdatesOnStartCB,
            ComboBox<Level> logLevelCmbBox) {
        this.reference = reference.getCopy();
        this.checkUpdatesOnStartCB = checkUpdatesOnStartCB;
        this.logLevelCmbBox = logLevelCmbBox;
    }


    @Override
    public UserPreference performTask() throws Throwable {
        reference.setCheckUpdateOnStart(checkUpdatesOnStartCB.isSelected());
        reference.setLogLevel(logLevelCmbBox.getSelectionModel().getSelectedItem());
        return reference;
    }
}
