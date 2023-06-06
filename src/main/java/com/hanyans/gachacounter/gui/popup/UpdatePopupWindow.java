package com.hanyans.gachacounter.gui.popup;

import java.awt.Desktop;
import java.net.URL;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.hanyans.gachacounter.MainApp;
import com.hanyans.gachacounter.core.AppUpdateMessage;
import com.hanyans.gachacounter.gui.UiComponent;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.Region;
import javafx.stage.Modality;
import javafx.stage.Stage;


public class UpdatePopupWindow extends Stage {
    public UpdatePopupWindow(Stage parentStage, boolean hasUpdate, String message, URL url) {
        UpdatePanel panel = new UpdatePanel(this, hasUpdate, message, url);
        setScene(new Scene(panel.getRoot()));
        setResizable(false);
        initModality(Modality.WINDOW_MODAL);
        initOwner(parentStage);
        setTitle("Update");
        MainApp.setStageIcon(this);
    }


    public static void displayAndWait(Stage stage, AppUpdateMessage msg) {
        UpdatePopupWindow window = new UpdatePopupWindow(
                stage,
                msg.hasUpdate,
                msg.message,
                msg.url);
        window.showAndWait();
    }





    private static class UpdatePanel extends UiComponent<Region> {
        private static final String FXML_FILE = "UpdatePanel.fxml";

        private final Logger logger = LogManager.getFormatterLogger(UpdatePopupWindow.class);

        private final Stage stage;
        private final URL url;

        @FXML private Label titleLabel;
        @FXML private TextArea contentArea;
        @FXML private Button goButton;


        UpdatePanel(Stage stage, boolean hasUpdate, String message, URL url) {
            super(FXML_FILE);
            this.stage = stage;
            this.url = url;
            titleLabel.setText(hasUpdate ? "Updates available" : "Up to date");
            contentArea.setText(message);

            // hide go button if up to date
            goButton.setVisible(hasUpdate);
            goButton.setManaged(hasUpdate);
        }


        @FXML
        private void handleOk(ActionEvent event) {
            stage.close();
        }


        @FXML
        private void handleGo(ActionEvent event) {
            try {
                Desktop.getDesktop().browse(url.toURI());
            } catch (Throwable ex) {
                logger.warn("Failed to browse to installation page", ex);
            }
            stage.close();
        }
    }
}
