package com.hanyans.gachacounter.gui.popup;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.hanyans.gachacounter.MainApp;
import com.hanyans.gachacounter.core.PopupMessage;
import com.hanyans.gachacounter.core.util.FileUtil;
import com.hanyans.gachacounter.gui.UiComponent;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Region;
import javafx.stage.Modality;
import javafx.stage.Stage;


public class MessagePopupWindow extends Stage {
    public static String ERROR_TITLE_STYLECLASS = "error-title";
    public static String SUCCESS_TITLE_STYLECLASS = "success-title";

    public static String ERROR_ICON_PATH = "/view/img/kokomi_down.png";
    public static String SUCCESS_ICON_PATH = "/view/img/teriklee.png";


    private static final Logger logger = LogManager.getFormatterLogger(MessagePopupWindow.class);


    public MessagePopupWindow(Stage parentStage, PopupMessage msg) {
        MessagePopupPanel panel = new MessagePopupPanel(this, msg);
        setScene(new Scene(panel.getRoot()));
        setResizable(false);
        initModality(Modality.WINDOW_MODAL);
        initOwner(parentStage);
        setTitle(msg.msgType.name());
        MainApp.setStageIcon(this);
    }


    public static void displayAndWait(Stage stage, PopupMessage msg) {
        MessagePopupWindow window = new MessagePopupWindow(stage, msg);
        window.showAndWait();
    }





    private static class MessagePopupPanel extends UiComponent<Region> {
        private static final String FXML_FILE = "MessagePopupPanel.fxml";

        private final Stage stage;

        @FXML private ImageView imageIcon;
        @FXML private Label titleLabel;
        @FXML private TextArea contentArea;


        MessagePopupPanel(Stage stage, PopupMessage msg) {
            super(FXML_FILE);
            this.stage = stage;
            titleLabel.setText(msg.title);
            contentArea.setText(msg.content);
            switch (msg.msgType) {
                case Success:
                    setIcon(SUCCESS_ICON_PATH);
                    titleLabel.getStyleClass().add(SUCCESS_TITLE_STYLECLASS);
                    break;
                case Error:
                    setIcon(ERROR_ICON_PATH);
                    titleLabel.getStyleClass().add(ERROR_TITLE_STYLECLASS);
                    break;
            }
        }


        @FXML
        private void handleOk(ActionEvent event) {
            stage.close();
        }


        private void setIcon(String pathString) {
            try {
                Image image = new Image(FileUtil.getResourceUrl(pathString).toString());
                if (image.isError()) {
                    throw image.getException();
                }
                imageIcon.setImage(image);
            } catch (Throwable ex) {
                logger.warn("Error while loading image icon of popup", ex);
            }
        }
    }
}
