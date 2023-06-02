package com.hanyans.gachacounter.gui;

import java.util.HashMap;

import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;


public class CheckListPanel<T> extends VBox {
    private final HashMap<T, CheckListItemBox> checkBoxMap = new HashMap<>();


    public CheckListPanel() {
        setSpacing(5.0);
    }


    public void clearCheckList() {
        checkBoxMap.clear();
        getChildren().clear();
    }


    public void addCheckListItem(T item, boolean isSelected) {
        CheckListItemBox box = new CheckListItemBox(item.toString(), isSelected);
        checkBoxMap.put(item, box);
        getChildren().add(box.getRoot());
    }


    public HashMap<T, Boolean> getCheckListMap() {
        HashMap<T, Boolean> result = new HashMap<>();
        for (T item : checkBoxMap.keySet()) {
            boolean isSelected = checkBoxMap.get(item).isSelected();
            result.put(item, isSelected);
        }
        return result;
    }





    private class CheckListItemBox extends UiComponent<Pane> {
        private static final String FXML_FILE = "CheckListItemBox.fxml";

        @FXML private Label itemNameLabel;
        @FXML private CheckBox checkBox;


        CheckListItemBox(String name, boolean isSelected) {
            super(FXML_FILE);
            itemNameLabel.setText(name);
            checkBox.setSelected(isSelected);
        }


        boolean isSelected() {
            return checkBox.isSelected();
        }
    }
}
