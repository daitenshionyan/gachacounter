package com.hanyans.gachacounter.gui;

import java.io.FileNotFoundException;
import java.io.IOException;

import com.hanyans.gachacounter.core.util.FileUtil;

import javafx.fxml.FXMLLoader;
import javafx.scene.Node;


public abstract class UiComponent<T extends Node> {
    public static final String FXML_DIR = "/view/";

    private final T root;


    public UiComponent(String path) {
        this.root = initializeRoot(path);
    }


    private T initializeRoot(String path) throws RuntimeException {
        FXMLLoader loader = new FXMLLoader();
        try {
            loader.setLocation(FileUtil.getResourceUrl(FXML_DIR + path));
        } catch (FileNotFoundException fnfEx) {
            throw new RuntimeException(fnfEx);
        }
        loader.setController(this);
        loader.setRoot(null);
        try {
            return loader.load();
        } catch (IOException ioEx) {
            throw new RuntimeException(ioEx);
        }
    }


    public T getRoot() {
        return root;
    }
}
