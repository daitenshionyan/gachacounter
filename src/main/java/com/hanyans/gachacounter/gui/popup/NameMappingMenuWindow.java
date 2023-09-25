package com.hanyans.gachacounter.gui.popup;

import java.util.HashMap;
import java.util.List;

import com.hanyans.gachacounter.MainApp;
import com.hanyans.gachacounter.core.task.RunnableTask;
import com.hanyans.gachacounter.gui.UiComponent;
import com.hanyans.gachacounter.logic.Logic;
import com.hanyans.gachacounter.model.UidNameMap;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;


public class NameMappingMenuWindow extends UiComponent<VBox> {
    private static final String FXML_FILE = "NameMappingMenuPanel.fxml";

    private final HashMap<Long, FormInputBox> nameForms = new HashMap<>();

    private final Stage displayStage;
    private final Logic logic;
    private final UidNameMap nameMap;

    @FXML private Pane formArea;


    private NameMappingMenuWindow(Stage displayStage, Logic logic) {
        super(FXML_FILE);
        this.displayStage = displayStage;
        this.logic = logic;
        this.nameMap = logic.getUidNameMap();
        initializeNameMapForm(List.copyOf(logic.getUidFilterMap().keySet()), nameMap);
    }


    private void initializeNameMapForm(List<Long> uids, UidNameMap nameMap) {
        for (long uid : uids) {
            FormInputBox form = new FormInputBox(String.valueOf(uid));
            form.setText(nameMap.get(uid));
            nameForms.put(uid, form);
            formArea.getChildren().add(form.getRoot());
        }
    }


    public static void displayAndWait(Stage parentStage, Logic logic) {
        Stage hostStage = new Stage();
        hostStage.setScene(new Scene(new NameMappingMenuWindow(hostStage, logic)
                .getRoot()));
        hostStage.initModality(Modality.WINDOW_MODAL);
        hostStage.initOwner(parentStage);
        hostStage.setTitle("Name Mapping Menu");
        MainApp.setStageIcon(hostStage);
        hostStage.showAndWait();
    }


    @FXML
    private void handleCancel(ActionEvent event) {
        displayStage.close();
    }


    @FXML
    private void handleOk(ActionEvent event) {
        displayStage.hide();
        logic.updateUidNameMap(
                new RunnableTask<>() {
                    @Override
                    public UidNameMap performTask() throws Throwable {
                        for (long uid : nameForms.keySet()) {
                            String name = nameForms.get(uid).getText();
                            if (name.isBlank()) {
                                name = String.valueOf(uid);
                            }
                            nameMap.put(uid, name);
                        }
                        return nameMap;
                    }
                },
                this::handleTaskComplete,
                this::handleException);
    }


    private void handleTaskComplete(UidNameMap pref) {
        Platform.runLater(() -> displayStage.close());
    }


    private void handleException(Throwable ex) {
        Platform.runLater(() -> displayStage.show());
    }
}
