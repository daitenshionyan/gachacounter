package com.hanyans.gachacounter.gui;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;


public class LostCountBox extends UiComponent<Pane> {
    private static final String FXML_FILE = "LostCountBox.fxml";

    @FXML private ImageView displayImage;
    @FXML private Label countLabel;


    public LostCountBox(Image image, int count) {
        super(FXML_FILE);
        displayImage.setImage(image);
        countLabel.setText(String.valueOf(count));
    }
}
