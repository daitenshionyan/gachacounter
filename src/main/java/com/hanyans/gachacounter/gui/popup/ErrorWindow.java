package com.hanyans.gachacounter.gui.popup;

import com.hanyans.gachacounter.MainApp;
import com.hanyans.gachacounter.core.ErrorMessage;
import com.hanyans.gachacounter.gui.UiComponent;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.Region;
import javafx.stage.Modality;
import javafx.stage.Stage;


public class ErrorWindow extends Stage {
    public ErrorWindow(Stage parentStage, String title, String content) {
        ErrorPanel panel = new ErrorPanel(this, title, content);
        setScene(new Scene(panel.getRoot()));
        setResizable(false);
        initModality(Modality.WINDOW_MODAL);
        initOwner(parentStage);
        setTitle("Error");
        MainApp.setStageIcon(this);
    }


    public static void displayAndWait(Stage stage, ErrorMessage msg) {
        ErrorWindow window = new ErrorWindow(stage, msg.title, msg.content);
        window.showAndWait();
    }





    private static class ErrorPanel extends UiComponent<Region> {
        private static final String FXML_FILE = "ErrorPanel.fxml";

        private final Stage stage;

        @FXML private Label titleLabel;
        @FXML private Label contentLabel;


        ErrorPanel(Stage stage, String title, String content) {
            super(FXML_FILE);
            this.stage = stage;
            titleLabel.setText(title);
            contentLabel.setText(content);
        }


        @FXML
        private void handleOk(ActionEvent event) {
            stage.close();
        }
    }
}
