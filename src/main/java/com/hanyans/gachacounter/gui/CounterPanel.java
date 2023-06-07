package com.hanyans.gachacounter.gui;

import java.io.File;
import java.io.FileNotFoundException;
import java.nio.file.Path;
import java.util.Objects;
import java.util.function.Consumer;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.hanyans.gachacounter.core.AppUpdateMessage;
import com.hanyans.gachacounter.core.PopupMessage;
import com.hanyans.gachacounter.core.util.FileUtil;
import com.hanyans.gachacounter.gui.popup.MessagePopupWindow;
import com.hanyans.gachacounter.gui.popup.PreferenceMenu;
import com.hanyans.gachacounter.gui.popup.UpdatePopupWindow;
import com.hanyans.gachacounter.gui.task.OverviewRenderTask;
import com.hanyans.gachacounter.gui.updater.BannerCardUpdater;
import com.hanyans.gachacounter.gui.updater.OverallCardUpdater;
import com.hanyans.gachacounter.gui.updater.StatisticsUpdater;
import com.hanyans.gachacounter.logic.Logic;
import com.hanyans.gachacounter.model.preference.UserPreference;
import com.hanyans.gachacounter.wrapper.Game;

import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.scene.shape.Circle;
import javafx.scene.chart.StackedBarChart;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.effect.BoxBlur;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;


public class CounterPanel extends UiComponent<VBox> {
    private static final String FXML_FILE = "CounterPanel.fxml";

    private static final String BG_MEDIA_FILE = "/view/img/bg.mp4";
    private static final double BG_WIDTH_HEIGHT_RATIO = 134D / 256D;

    private final Logger logger = LogManager.getFormatterLogger(CounterPanel.class);

    private final BooleanProperty isFilterShowingProperty = new SimpleBooleanProperty(false);

    private final Logic logic;
    private final UserPreference preference;
    private final Stage parentStage;

    private final CheckListPanel<Long> accFilterCheckList = new CheckListPanel<>();

    @FXML private AnchorPane bgPlaceholder;
    @FXML private MediaView bgMediaView;

    @FXML private Pane loadingPanel;
    @FXML private ProgressBar progressBar;
    @FXML private Label progressLabel;

    @FXML private ComboBox<Game> gameComboBox;

    @FXML private Pane mainDisplayBox;
    @FXML private TextField pathTextField;
    @FXML private TextField urlTextField;

    @FXML private Button addFilterButton;
    @FXML private Pane filterBox;
    @FXML private ScrollPane accFilterScrollPane;
    @FXML private Pane accFilterDisplayBox;

    @FXML private Pane bannerPityCountBox;

    @FXML private Label stndPityLabel4;
    @FXML private Label stndPityLabel5;
    @FXML private Label stndTotalLabel;

    @FXML private Label charPityLabel4;
    @FXML private Label charPityLabel5;
    @FXML private Label charTotalLabel;

    @FXML private Label weapPityLabel4;
    @FXML private Label weapPityLabel5;
    @FXML private Label weapTotalLabel;

    @FXML private Label overallTotalLabel;
    @FXML private ScrollPane itemListScrollPane;
    @FXML private VBox itemListBox;

    @FXML private ScrollPane statisticsScrollPane;
    @FXML private VBox statisticsBox;

    @FXML private StackedBarChart<String, Number> stats5NormChart;
    @FXML private StackedBarChart<String, Number> stats5WeapChart;
    @FXML private StackedBarChart<String, Number> stats4Chart;


    public CounterPanel(Logic logic, Stage parentStage) {
        super(FXML_FILE);
        this.logic = Objects.requireNonNull(logic);
        this.preference = logic.getUserPrefs();
        this.parentStage = parentStage;
        initializeLogic();
        initializeBg();
        initializeGameChoiceBox();
        initializeFilter();
        initializeItemList();
        initializeStatistics();
        setLoading(false);
    }


    /*
     * ========================================================================
     *      LOGIC INITIALIZATION
     * ========================================================================
     */


    private void initializeLogic() {
        logic.setReportCompletionTask(formRenderTask());
        logic.setPopupMessageHandler(formErrorMessageHandler());
        logic.setAppUpdateMessageHandler(formAppUpdateMessageHandler());
        setLogicPropertyListener();
    }


    private OverviewRenderTask formRenderTask() {
        return new OverviewRenderTask(
                bannerPityCountBox,
                new BannerCardUpdater(stndPityLabel4, stndPityLabel5, stndTotalLabel),
                new BannerCardUpdater(charPityLabel4, charPityLabel5, charTotalLabel),
                new BannerCardUpdater(weapPityLabel4, weapPityLabel5, weapTotalLabel),
                new OverallCardUpdater(overallTotalLabel, itemListBox),
                new StatisticsUpdater(stats5NormChart, stats5WeapChart, stats4Chart),
                preference.getChartPreference());
    }


    private void setLogicPropertyListener() {
        logic.progressProperty().addListener((ob, o, n) -> {
            Platform.runLater(() -> progressBar.progressProperty().setValue(n));
        });
        logic.messageProperty().addListener((ob, o, n) -> {
            Platform.runLater(() -> progressLabel.setText(n));
        });
        logic.runningProperty().addListener((ob, o, n) -> {
            Platform.runLater(() -> setLoading(n));
        });
    }


    private Consumer<PopupMessage> formErrorMessageHandler() {
        return msg -> Platform.runLater(() -> MessagePopupWindow.displayAndWait(parentStage, msg));
    }


    private Consumer<AppUpdateMessage> formAppUpdateMessageHandler() {
        return msg -> Platform.runLater(() -> UpdatePopupWindow.displayAndWait(parentStage, msg));
    }


    /*
     * ========================================================================
     *      BACKGROUD INITIALIZATION
     * ========================================================================
     */


    private void initializeBg() {
        addBgListener();
        setMediaPlayer();
    }


    private void setMediaPlayer() {
        try {
            MediaPlayer player = initializeBgMediaPlayer(BG_MEDIA_FILE);
            // I have no idea why it fails to load sometimes...
            // This just sets it to reload again until it works
            player.setOnError(() -> {
                logger.info("Failed to load background attepting to reload...");
                logger.debug("Error occured while loading background", player.getError());
                setMediaPlayer();
            });
            player.setCycleCount(MediaPlayer.INDEFINITE);
            player.setOnReady(() -> {
                logger.info("Successfully loaded background");
                player.play();
            });
            bgMediaView.setMediaPlayer(player);
        } catch (Throwable ex) {
            ex.printStackTrace();
            return;
        }
    }


    /**
     * Add listener to width and height properties of {@code bgPlaceholder} to
     * resize background to fill entire panel.
     */
    private void addBgListener() {
        bgPlaceholder.widthProperty().addListener((ob, o, n) -> {
            if (n == null) {
                return;
            }
            if (n.doubleValue() * BG_WIDTH_HEIGHT_RATIO > bgPlaceholder.getHeight()) {
                bgMediaView.setFitWidth(n.doubleValue());
                bgMediaView.setFitHeight(0);
            }
        });
        bgPlaceholder.heightProperty().addListener((ob, o, n) -> {
            if (n == null) {
                return;
            }
            if (n.doubleValue() / BG_WIDTH_HEIGHT_RATIO > bgPlaceholder.getWidth()) {
                bgMediaView.setFitHeight(n.doubleValue());
                bgMediaView.setFitWidth(0);
            }
        });
    }


    private MediaPlayer initializeBgMediaPlayer(String stringPath) {
        try {
            String uriString = FileUtil.getResourceUri(BG_MEDIA_FILE).toString();
            Media media = new Media(uriString);
            MediaPlayer player = new MediaPlayer(media);
            return player;
        } catch (FileNotFoundException fnfEx) {
            throw new RuntimeException(fnfEx);
        }
    }


    /*
     * ========================================================================
     *      GAME INITIALIZATION
     * ========================================================================
     */


    private void initializeGameChoiceBox() {
        gameComboBox.getItems().addAll(Game.values());
        gameComboBox.setCellFactory(lv -> new GameCell());
    }


    /*
     * ========================================================================
     *      FILTER INITIALIZATION
     * ========================================================================
     */


    private void initializeFilter() {
        bindScrollDisplaySize(accFilterScrollPane, accFilterDisplayBox);
        accFilterDisplayBox.getChildren().add(accFilterCheckList);
        initializeIsFilterShowingProperty();
        showFilterBox(false);
    }


    private void initializeIsFilterShowingProperty() {
        isFilterShowingProperty.addListener((ob, o, n) -> {
            if (n == null) {
                return;
            }
            showFilterBox(n);
        });
    }


    /*
     * ========================================================================
     *      ITEM LIST INITIALIZATION
     * ========================================================================
     */


    /**
     * Add listener to {@code itemListScrollPane} to bind its viewview port
     * size to its display pane.
     */
    private void initializeItemList() {
        bindScrollDisplaySize(itemListScrollPane, itemListBox);
    }


    /*
     * ========================================================================
     *      ITEM LIST INITIALIZATION
     * ========================================================================
     */


    private void initializeStatistics() {
        bindScrollDisplaySize(statisticsScrollPane, statisticsBox);
    }


    /*
     * ========================================================================
     *      BUTTON EVENT HANDLERS
     * ========================================================================
     */


    @FXML
    private void handleUpdateCount(ActionEvent event) {
        logger.debug("-{HANDLE UPDATE COUNT}- action fired");
        isFilterShowingProperty.set(false);
        logic.updateGachaHistory(urlTextField.getText());
    }


    @FXML
    private void handleBrowse(ActionEvent event) {
        logger.debug("-{HANDLE BROWSE}- action fired");
        isFilterShowingProperty.set(false);
        FileChooser fileChooser = new FileChooser();
        File file = fileChooser.showOpenDialog(parentStage);
        if (file != null) {
            logger.debug("File path set to \"%s\"", file.getAbsolutePath());
            pathTextField.setText(file.getAbsolutePath());
        } else {
            logger.debug("No file selected");
        }
    }


    @FXML
    private void handleGrab(ActionEvent event) {
        logger.debug("-{HANDLE GRAB}- action fired");
        isFilterShowingProperty.set(false);
        logic.grabPlayerUrl(
                pathTextField.getText(),
                urlString -> Platform.runLater(() -> urlTextField.setText(urlString)));
    }


    @FXML
    private void handleGameSet(ActionEvent event) {
        logger.debug("-{HANDLE GAME SET}- action fired");
        isFilterShowingProperty.set(false);
        urlTextField.setText("");
        Game selected = gameComboBox.getSelectionModel().getSelectedItem();
        switch (selected) {
            case HSR:
                setPathText(preference.getDataFilePathHsr());
                break;
            case Genshin:
                setPathText(preference.getDataFilePathGenshin());
                break;
            default:
                logger.info("Unknwon game type <%s>, ignoring", selected);
                return;
        }
        logic.setGame(selected);
    }


    @FXML
    private void handleAddFilter(ActionEvent event) {
        logger.debug("-{HANDLE ADD FILTER}- action fired");
        accFilterCheckList.clearCheckList();
        logic.getUidFilterMap().entrySet()
                .stream()
                .sorted((e1, e2) -> e1.getKey().compareTo(e2.getKey()))
                .forEachOrdered(entry -> accFilterCheckList.addCheckListItem(
                        entry.getKey(), entry.getValue()));
        isFilterShowingProperty.set(true);
    }


    @FXML
    private void handleCancelFilter(ActionEvent event) {
        logger.debug("-{AHNDLE CANCEL FILTER}- action fired");
        isFilterShowingProperty.set(false);
    }


    @FXML
    private void handleApplyFilter(ActionEvent event) {
        logger.debug("-{HANDLE APPLY FILTER}- action fired");
        logic.setUidFilter(accFilterCheckList.getCheckListMap()
                .entrySet()
                .stream()
                .filter(entry -> entry.getValue())
                .map(entry -> entry.getKey())
                .toList());
        isFilterShowingProperty.set(false);
    }


    @FXML
    private void handleSave(ActionEvent event) {
        logger.debug("-{HANDLE SAVE}- action fired");
        isFilterShowingProperty.set(false);
        logic.manualSave();
    }


    @FXML
    private void handleCheckForUpdates(ActionEvent event) {
        logger.debug("-{HANDLE CHECK FOR UPDATES}- action fired");
        isFilterShowingProperty.set(false);
        logic.checkForAppUpdates(true);
    }


    @FXML
    private void handleUpdateData(ActionEvent event) {
        logger.debug("-{HANDLE UPDATE DATA}- action fired");
        isFilterShowingProperty.set(false);
        logic.updateData();
    }


    @FXML
    private void handlePreferenceEdit(ActionEvent event) {
        logger.debug("-{HANDLE PREFERENCE EDIT}- action fired");
        isFilterShowingProperty.set(false);
        PreferenceMenu.displayAndWait(parentStage, logic, preference);
    }


    /*
     * ========================================================================
     *      UTILITY
     * ========================================================================
     */


    private void setPathText(Path path) {
        if (path == null) {
            pathTextField.setText("");
            return;
        }
        pathTextField.setText(path.toAbsolutePath().toString());
    }


    private void setLoading(Boolean isRunning) {
        if (isRunning == null) {
            isRunning = false;
        }
        loadingPanel.setVisible(isRunning);
        loadingPanel.setManaged(isRunning);
        mainDisplayBox.setFocusTraversable(!isRunning);
        if (isRunning) {
            mainDisplayBox.setEffect(new BoxBlur(5, 5, 3));
        } else {
            mainDisplayBox.setEffect(null);
        }
    }


    private void showFilterBox(boolean shouldShow) {
        addFilterButton.setVisible(!shouldShow);
        addFilterButton.setManaged(!shouldShow);
        filterBox.setVisible(shouldShow);
        filterBox.setManaged(shouldShow);
    }


    private void bindScrollDisplaySize(ScrollPane scrollPane, Pane pane) {
        scrollPane.viewportBoundsProperty().addListener((ob, o, n) -> {
            double width = n.getWidth();
            double height = n.getHeight();

            pane.setMinWidth(width);
            pane.setPrefWidth(width);
            pane.setMaxWidth(width);

            pane.setMinHeight(height);
        });
    }





    private class GameCell extends ListCell<Game> {
        @Override
        protected void updateItem(Game item, boolean isEmpty) {
            super.updateItem(item, isEmpty);
            if (item == null || isEmpty) {
                setGraphic(null);
                setText(null);
                return;
            }

            setGraphic(new GameListCell(item).getRoot());
        }
    }





    private class GameListCell extends UiComponent<Region> {
        private static final String FXML_FILE = "GameListCell.fxml";

        private static final double ICON_SIZE = 32D;

        @FXML private ImageView icon;
        @FXML private Label label;


        GameListCell(Game game) {
            super(FXML_FILE);

            try {
                switch (game) {
                    case HSR:
                        initializeIcon("/view/img/HSRIcon.png");
                        break;
                    case Genshin:
                        initializeIcon("/view/img/GenshinIcon.png");
                        break;
                    default:
                        throw new IllegalArgumentException(String.format("Unknown game type <%s>", game));
                }
            } catch (Throwable ex) {
                logger.error(String.format("Error occured while initializing icon for <%s> game", game), ex);
            }

            label.setText(String.valueOf(game));
        }


        private void initializeIcon(String pathString) throws Throwable {
            Image image = new Image(
                    FileUtil.getResourceUrl(pathString).toString(),
                    ICON_SIZE, ICON_SIZE,
                    true, true);
            if (image.isError()) {
                throw image.getException();
            }
            icon.setImage(image);
            icon.setClip(new Circle(
                    ICON_SIZE / 2,
                    ICON_SIZE / 2,
                    ICON_SIZE / 2));
        }
    }
}
