package com.hanyans.gachacounter.gui.popup;

import org.apache.logging.log4j.Level;

import com.hanyans.gachacounter.MainApp;
import com.hanyans.gachacounter.gui.UiComponent;
import com.hanyans.gachacounter.model.preference.UserPreference;

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

    @FXML private Pane chartPrefEntryBox;

    @FXML private CheckBox checkUpdatesOnStartCB;
    @FXML private ComboBox<Level> logLevelCmbBox;


    public PreferenceMenu(Stage displayStage, UserPreference preference) {
        super(FXML_FILE);
        this.displayStage = displayStage;

        initializeSystemPrefs(preference);
    }


    private void initializeSystemPrefs(UserPreference prefs) {
        logLevelCmbBox.getItems().addAll(Level.values());

        checkUpdatesOnStartCB.setSelected(prefs.isCheckUpdateOnStart());
        logLevelCmbBox.getSelectionModel().select(prefs.getLogLevel());
    }


    public static void displayAndWait(Stage parentStage, UserPreference preference) {
        Stage hostStage = new Stage();
        hostStage.setScene(new Scene(new PreferenceMenu(
                hostStage, preference).getRoot()));
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
        displayStage.close();
    }
}
