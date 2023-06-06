package com.hanyans.gachacounter.gui.popup;

import com.hanyans.gachacounter.MainApp;
import com.hanyans.gachacounter.core.PopupMessage;
import com.hanyans.gachacounter.gui.UiComponent;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.Region;
import javafx.stage.Modality;
import javafx.stage.Stage;


public class MessagePopupWindow extends Stage {
    public MessagePopupWindow(Stage parentStage, String title, String content) {
        ErrorPanel panel = new ErrorPanel(this, title, content);
        setScene(new Scene(panel.getRoot()));
        setResizable(false);
        initModality(Modality.WINDOW_MODAL);
        initOwner(parentStage);
        setTitle("Error");
        MainApp.setStageIcon(this);
    }


    public static void displayAndWait(Stage stage, PopupMessage msg) {
        MessagePopupWindow window = new MessagePopupWindow(stage, msg.title, msg.content);
        window.showAndWait();
    }





    private static class ErrorPanel extends UiComponent<Region> {
        private static final String FXML_FILE = "ErrorPanel.fxml";

        private final Stage stage;

        @FXML private Label titleLabel;
        @FXML private TextArea contentArea;


        ErrorPanel(Stage stage, String title, String content) {
            super(FXML_FILE);
            this.stage = stage;
            titleLabel.setText(title);
            contentArea.setText(content);
        }


        @FXML
        private void handleOk(ActionEvent event) {
            stage.close();
        }
    }
}
