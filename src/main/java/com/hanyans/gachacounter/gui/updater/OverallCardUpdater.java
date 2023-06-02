package com.hanyans.gachacounter.gui.updater;

import java.util.List;

import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;


public class OverallCardUpdater {
    private final Label totalLabel;
    private final Pane itemListPane;


    public OverallCardUpdater(Label totalLabel, Pane itemListPane) {
        this.totalLabel = totalLabel;
        this.itemListPane = itemListPane;
    }


    public void update(int totalCount, List<Node> entryBoxes) {
        totalLabel.setText(String.valueOf(totalCount));
        itemListPane.getChildren().setAll(entryBoxes);
    }
}
