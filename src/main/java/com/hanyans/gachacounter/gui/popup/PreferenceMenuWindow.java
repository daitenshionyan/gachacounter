package com.hanyans.gachacounter.gui.popup;

import java.util.List;

import org.apache.logging.log4j.Level;

import com.hanyans.gachacounter.MainApp;
import com.hanyans.gachacounter.gui.UiComponent;
import com.hanyans.gachacounter.gui.task.PrefFormValidationTask;
import com.hanyans.gachacounter.logic.Logic;
import com.hanyans.gachacounter.model.preference.ChartPreference;
import com.hanyans.gachacounter.model.preference.UserPreference;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;


public class PreferenceMenuWindow extends UiComponent<VBox> {
    private static final String FXML_FILE = "PreferenceMenuPanel.fxml";

    private final FormInputBox pityStep5NormInput =
            new FormInputBox("5 \u2605 Pity Step (Normal)");
    private final FormInputBox pityStep5WeapInput =
            new FormInputBox("5 \u2605 Pity Step (Weapon)");
    private final FormInputBox pityStep4Input =
            new FormInputBox("4 \u2605 Pity Step");
    private final FormInputBox freqStepFactInput =
            new FormInputBox("Frequency Step Factor");
    private final FormInputBox freqMaxCountInput =
            new FormInputBox("Frequency Marking Max Count");

    private final Stage displayStage;
    private final Logic logic;
    private final UserPreference preference;

    @FXML private Pane chartPrefEntryBox;

    @FXML private CheckBox checkUpdatesOnStartCB;
    @FXML private ComboBox<Level> logLevelCmbBox;


    private PreferenceMenuWindow(Stage displayStage, Logic logic) {
        super(FXML_FILE);
        this.displayStage = displayStage;
        this.logic = logic;
        this.preference = logic.getUserPrefs();

        initializeChartPrefs(preference.getChartPreference());
        initializeSystemPrefs(preference);
    }


    private void initializeChartPrefs(ChartPreference pref) {
        pityStep5NormInput.setText(String.valueOf(pref.getPityStep5Norm()));
        chartPrefEntryBox.getChildren().add(pityStep5NormInput.getRoot());
        pityStep5WeapInput.setText(String.valueOf(pref.getPityStep5Weap()));
        chartPrefEntryBox.getChildren().add(pityStep5WeapInput.getRoot());
        pityStep4Input.setText(String.valueOf(pref.getPityStep4()));
        chartPrefEntryBox.getChildren().add(pityStep4Input.getRoot());
        freqStepFactInput.setText(String.valueOf(pref.getFreqMarkingStepFactor()));
        chartPrefEntryBox.getChildren().add(freqStepFactInput.getRoot());
        freqMaxCountInput.setText(String.valueOf(pref.getFreqMarkingMaxCount()));
        chartPrefEntryBox.getChildren().add(freqMaxCountInput.getRoot());
    }


    private void initializeSystemPrefs(UserPreference prefs) {
        List.of(Level.values()).stream()
                .sorted()
                .forEach(lvl -> logLevelCmbBox.getItems().add(lvl));

        checkUpdatesOnStartCB.setSelected(prefs.isCheckUpdateOnStart());
        logLevelCmbBox.getSelectionModel().select(prefs.getLogLevel());
    }


    public static void displayAndWait(Stage parentStage, Logic logic) {
        Stage hostStage = new Stage();
        hostStage.setScene(new Scene(new PreferenceMenuWindow(hostStage, logic)
                .getRoot()));
        hostStage.setResizable(false);
        hostStage.initModality(Modality.WINDOW_MODAL);
        hostStage.initOwner(parentStage);
        hostStage.setTitle("Preference Menu");
        MainApp.setStageIcon(hostStage);
        hostStage.showAndWait();
    }


    @FXML
    private void handleCancel(ActionEvent event) {
        displayStage.close();
    }


    @FXML
    private void handleOk(ActionEvent event) {
        displayStage.hide();
        PrefFormValidationTask task = new PrefFormValidationTask(
                preference,
                pityStep5NormInput,
                pityStep5WeapInput,
                pityStep4Input,
                freqStepFactInput,
                freqMaxCountInput,
                checkUpdatesOnStartCB,
                logLevelCmbBox);
        logic.updatePreference(task, this::handleTaskComplete, this::handleException);
    }


    private void handleTaskComplete(UserPreference pref) {
        Platform.runLater(() -> displayStage.close());
    }


    private void handleException(Throwable ex) {
        Platform.runLater(() -> displayStage.show());
    }
}
