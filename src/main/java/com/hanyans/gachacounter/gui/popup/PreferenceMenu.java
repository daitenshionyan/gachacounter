package com.hanyans.gachacounter.gui.popup;

import java.util.List;

import org.apache.logging.log4j.Level;

import com.hanyans.gachacounter.MainApp;
import com.hanyans.gachacounter.gui.UiComponent;
import com.hanyans.gachacounter.gui.task.PrefFormValidationTask;
import com.hanyans.gachacounter.logic.Logic;
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


public class PreferenceMenu extends UiComponent<VBox> {
    private static final String FXML_FILE = "PreferenceMenuPanel.fxml";

    private final Stage displayStage;
    private final Logic logic;
    private final UserPreference preference;

    @FXML private Pane chartPrefEntryBox;

    @FXML private CheckBox checkUpdatesOnStartCB;
    @FXML private ComboBox<Level> logLevelCmbBox;


    public PreferenceMenu(Stage displayStage, Logic logic, UserPreference preference) {
        super(FXML_FILE);
        this.displayStage = displayStage;
        this.logic = logic;
        this.preference = preference;

        initializeSystemPrefs(preference);
    }


    private void initializeSystemPrefs(UserPreference prefs) {
        List.of(Level.values()).stream()
                .sorted()
                .forEach(lvl -> logLevelCmbBox.getItems().add(lvl));

        checkUpdatesOnStartCB.setSelected(prefs.isCheckUpdateOnStart());
        logLevelCmbBox.getSelectionModel().select(prefs.getLogLevel());
    }


    public static void displayAndWait(Stage parentStage, Logic logic, UserPreference preference) {
        Stage hostStage = new Stage();
        hostStage.setScene(new Scene(new PreferenceMenu(hostStage, logic, preference)
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
            preference, checkUpdatesOnStartCB, logLevelCmbBox);
        logic.updatePreference(task, this::handleTaskComplete, ex -> {});
    }


    private void handleTaskComplete(UserPreference pref) {
        Platform.runLater(() -> displayStage.close());
    }
}
