package com.hanyans.gachacounter.gui.popup;

import com.hanyans.gachacounter.gui.UiComponent;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;

public class FormInputBox extends UiComponent<Pane> {
    public static String ERROR_LABEL_STYLECLASS = "form-error-msg-label";
    public static String ERROR_TEXT_FIELD_STYLECLASS = "form-error-text-field";

    private static final String FXML_FILE = "FormInputBox.fxml";

    @FXML private Pane containerBox;

    @FXML private Pane nameBox;
    @FXML private Label nameLabel;

    @FXML private Pane valueBox;
    @FXML private TextField textField;
    @FXML private Label errorMsgLabel;


    public FormInputBox(String name) {
        super(FXML_FILE);
        nameLabel.setText(name);

        disableError();
    }


    public String getText() {
        return textField.getText();
    }


    public void setText(String text) {
        textField.setText(text);
    }


    public void disableError() {
        errorMsgLabel.setText("");
        errorMsgLabel.setVisible(false);
        errorMsgLabel.setManaged(false);
        textField.getStyleClass().remove(ERROR_TEXT_FIELD_STYLECLASS);
    }


    public void setError(String errorMsg) {
        errorMsgLabel.setText(errorMsg);
        errorMsgLabel.setVisible(true);
        errorMsgLabel.setManaged(true);
        textField.getStyleClass().add(ERROR_TEXT_FIELD_STYLECLASS);
    }
}
