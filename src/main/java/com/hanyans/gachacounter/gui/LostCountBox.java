package com.hanyans.gachacounter.gui;

import java.io.InputStream;
import java.nio.file.Path;

import com.hanyans.gachacounter.core.util.FileUtil;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;


public class LostCountBox extends UiComponent<Pane> {
    private static final String FXML_FILE = "LostCountBox.fxml";

    private static final double ICON_HEIGHT = 17;
    private static final double ICON_WIDTH_CHAR = 15;

    @FXML private ImageView displayImage;
    @FXML private Label countLabel;


    public LostCountBox(Path imgPath, int count) {
        super(FXML_FILE);
        double iconHeight = ICON_HEIGHT;
        double iconWidth = ICON_WIDTH_CHAR;
        try (InputStream is = FileUtil.getInputStream(imgPath)) {
            Image image = new Image(is, iconWidth, iconHeight, true, true);
            displayImage.setImage(image);
        } catch (Throwable ex) {
            // Ignore
        }
        countLabel.setText(String.valueOf(count));
    }
}
