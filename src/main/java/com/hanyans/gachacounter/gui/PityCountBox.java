package com.hanyans.gachacounter.gui;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;


public class PityCountBox extends UiComponent<HBox> {
    private static final String FXML_FILE = "PityCountBox.fxml";

    private static final String RATE_UP_WON_STYLECLASS = "rate-up-won";
    private static final String RATE_UP_LOSS_STYLECLASS = "rate-up-loss";

    @FXML private Label mainValueLabel;
    @FXML private Label subValueLabel;


    public PityCountBox(int count, int maxCount, boolean isRateUp, boolean isRateUpWon) {
        super(FXML_FILE);
        mainValueLabel.setText(String.valueOf(count));
        if (isRateUp) {
            if (isRateUpWon) {
                mainValueLabel.getStyleClass().add(RATE_UP_WON_STYLECLASS);
            } else {
                mainValueLabel.getStyleClass().add(RATE_UP_LOSS_STYLECLASS);
            }
        }
        subValueLabel.setText(String.format("of %d", maxCount));
    }
}
