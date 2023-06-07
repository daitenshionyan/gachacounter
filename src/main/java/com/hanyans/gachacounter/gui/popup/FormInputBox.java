package com.hanyans.gachacounter.gui.popup;

import java.util.Optional;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.hanyans.gachacounter.gui.UiComponent;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;


public class FormInputBox extends UiComponent<Pane> {
    public static String ERROR_LABEL_STYLECLASS = "form-error-msg-label";
    public static String ERROR_TEXT_FIELD_STYLECLASS = "form-error-text-field";

    private static final String FXML_FILE = "FormInputBox.fxml";

    private final Logger logger = LogManager.getFormatterLogger(FormInputBox.class);

    private final String name;

    @FXML private Pane containerBox;

    @FXML private Pane nameBox;
    @FXML private Label nameLabel;

    @FXML private Pane valueBox;
    @FXML private TextField textField;
    @FXML private Label errorMsgLabel;


    public FormInputBox(String name) {
        super(FXML_FILE);
        this.name = name;
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


    public Optional<Throwable> processAsInteger(ProcessFunction<Integer> func) {
        disableError();
        try {
            int value = Integer.parseInt(getText());
            func.accept(value);
            logger.debug("Successfully processed \"%s\" field with value of %d",
                    name, value);
        } catch (NumberFormatException numEx) {
            logger.warn("Error in \"%s\" input field -- %s",
                    name, numEx.toString());
            setError("Not an integer");
            return Optional.ofNullable(numEx);
        } catch (Throwable ex) {
            logger.warn("Error in \"%s\" input field -- %s",
                    name, ex.toString());
            setError(ex.getMessage());
            return Optional.ofNullable(ex);
        }
        return Optional.empty();
    }





    @FunctionalInterface
    public static interface ProcessFunction<T> {
        public void accept(T value) throws Throwable;
    }
}
