package com.hanyans.gachacounter.gui;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Objects;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.hanyans.gachacounter.core.util.FileUtil;
import com.hanyans.gachacounter.mhy.GachaType;
import com.hanyans.gachacounter.mhy.Game;
import com.hanyans.gachacounter.model.GachaItem;
import com.hanyans.gachacounter.model.UidNameMap;
import com.hanyans.gachacounter.model.count.ProcessedGachaEntry;

import javafx.beans.binding.Bindings;
import javafx.beans.property.SimpleObjectProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.util.Duration;


public class GachaItemCountBox extends UiComponent<VBox> {
    private static final String FXML_FILE = "GachaItemCountBox.fxml";

    private static final Path IMAGE_DIR_HSR = Path.of("image/HSR");
    private static final Path IMAGE_DIR_GENSHIN = Path.of("image/Genshin");

    // relative to image directory
    private static final Path CHARACTER_DIR_PATH = Path.of("character");
    private static final Path WEAPON_DIR_PATH = Path.of("weapon");

    private static final String STYLE_RANK_5 = "ssr-box-color";
    private static final String STYLE_RANK_4 = "sr-box-color";
    private static final String STYLE_RANK_3 = "r-box-color";

    private static final double ICON_HEIGHT = 84;
    private static final double ICON_WIDTH_CHAR = 72;

    private final Logger logger = LogManager.getFormatterLogger(GachaItemCountBox.class);

    private final Game game;
    private final Collection<ProcessedGachaEntry> entries;
    private final UidNameMap uidNameMap;

    @FXML private ImageView displayImage;

    @FXML private Label itemNameLabel;
    @FXML private Label itemCountLabel;
    @FXML private Label itemIdLabel;

    @FXML private Button hideShowButton;
    @FXML private VBox tableDisplayArea;

    private boolean isInitialized = false;
    private boolean isShowing = false;


    public GachaItemCountBox(
                Game game,
                GachaItem item,
                HashSet<ProcessedGachaEntry> entries,
                UidNameMap uidNameMap) {
        super(FXML_FILE);
        this.game = game;
        this.entries = entries.stream().sorted(Comparator.reverseOrder()).toList();
        this.uidNameMap = Objects.requireNonNull(uidNameMap);
        itemNameLabel.setText(item.name);
        itemCountLabel.setText(String.valueOf(entries.size()));
        itemIdLabel.setText(getItemIdText(item));
        switch (item.rank) {
            case 3:
                getRoot().getStyleClass().add(STYLE_RANK_3);
                break;
            case 4:
                getRoot().getStyleClass().add(STYLE_RANK_4);
                break;
            case 5:
                getRoot().getStyleClass().add(STYLE_RANK_5);
                break;
        }
        initialiseDisplayImage(item);
        setTableVisibility(isShowing);
    }


    private String getItemIdText(GachaItem item) {
        if (item.itemId == 0) {
            return String.format("%s.png", item.name);
        }
        return String.format("%d.png", item.itemId);
    }


    private Path getImagePath(GachaItem item) {
        Path path;
        switch (game) {
            case HSR:
                path = IMAGE_DIR_HSR;
                break;
            case Genshin:
                path = IMAGE_DIR_GENSHIN;
                break;
            default:
                throw new IllegalArgumentException(String.format("Unknown game type <%s>", game));
        }
        switch (item.itemType) {
            case CHARACTER:
                path = path.resolve(CHARACTER_DIR_PATH);
                break;
            case WEAPON:
                path = path.resolve(WEAPON_DIR_PATH);
                break;
            default:
                throw new IllegalArgumentException(String.format("Unknown item type <%s>", item.itemType));
        }
        return path.resolve(String.format("%s.png", item.name));
    }


    private void initialiseDisplayImage(GachaItem item) {
        Path imagePath = getImagePath(item);
        double iconHeight = ICON_HEIGHT;
        double iconWidth = ICON_WIDTH_CHAR;
        try (InputStream is = FileUtil.getInputStream(imagePath)) {
            Image image = new Image(is, iconWidth, iconHeight, true, true);
            displayImage.setImage(image);
            if (image.isError()) {
                logger.debug(
                        String.format("Unable to load icon image for <%s>", item.name),
                        image.exceptionProperty().get());
            } else {
                itemIdLabel.setVisible(false);
                itemIdLabel.setManaged(false);
            }
        } catch (IOException ioEx) {
            logger.debug(
                    String.format("Unable to load icon image for <%s>", item.name),
                    ioEx);
            return;
        }
    }


    private void initializeTableView(Collection<ProcessedGachaEntry> entries) {
        if (isInitialized) {
            return;
        }
        isInitialized = true;

        TableView<ProcessedGachaEntry> tableView = new TableView<>();
        tableView.getItems().setAll(entries);
        tableView.columnResizePolicyProperty().set(TableView.CONSTRAINED_RESIZE_POLICY);
        tableView.setFixedCellSize(30D);
        tableView.prefHeightProperty().bind(tableView
                .fixedCellSizeProperty()
                .multiply(Bindings.size(tableView.getItems()).add(1.01)));

        TableColumn<ProcessedGachaEntry, String> uidColumn = new TableColumn<>("UID");
        // careful: uidNameMap is not synchronized.
        uidColumn.setCellValueFactory(param ->
                new SimpleObjectProperty<>(uidNameMap.get(param.getValue().uid)));
        tableView.getColumns().add(uidColumn);

        TableColumn<ProcessedGachaEntry, LocalDateTime> timeColumn = new TableColumn<>("Time");
        timeColumn.setCellValueFactory(param ->
                new SimpleObjectProperty<>(param.getValue().time));
        timeColumn.setCellFactory(table -> new EntryTimeCell());
        tableView.getColumns().add(timeColumn);

        TableColumn<ProcessedGachaEntry, GachaType> bannerColumn = new TableColumn<>("Banner");
        bannerColumn.setCellValueFactory(param ->
                new SimpleObjectProperty<>(param.getValue().gachaType));
        tableView.getColumns().add(bannerColumn);

        TableColumn<ProcessedGachaEntry, ProcessedGachaEntry> lostColumn = new TableColumn<>("Lost to");
        lostColumn.setCellValueFactory(param ->
                new SimpleObjectProperty<>(getLostItem(param.getValue())));
        lostColumn.setCellFactory(table -> new LostEntryCell());
        tableView.getColumns().add(lostColumn);

        TableColumn<ProcessedGachaEntry, ProcessedGachaEntry> pityColumn = new TableColumn<>("Pity");
        pityColumn.setCellValueFactory(param ->
                new SimpleObjectProperty<>(param.getValue()));
        pityColumn.setCellFactory(table -> new EntryPityCell());
        pityColumn.setComparator((e1, e2) -> e1.pityCount - e2.pityCount);
        tableView.getColumns().add(pityColumn);

        tableDisplayArea.getChildren().setAll(tableView);
    }


    private ProcessedGachaEntry getLostItem(ProcessedGachaEntry entry) {
        if (!entry.isRateUp || entry.isRateUpWon) {
            return null;
        }
        return entry.prevRankEntry.orElse(null);
    }


    @FXML
    private void handleHideShow(ActionEvent event) {
        isShowing = !isShowing;
        setTableVisibility(isShowing);
    }


    private void setTableVisibility(boolean isShowing) {
        if (isShowing) {
            hideShowButton.setText("-");
            initializeTableView(entries);
        } else {
            hideShowButton.setText("+");
        }
        tableDisplayArea.setVisible(isShowing);
        tableDisplayArea.setManaged(isShowing);
    }





    private static class EntryTimeCell extends TableCell<ProcessedGachaEntry, LocalDateTime> {
        @Override
        protected void updateItem(LocalDateTime item, boolean empty) {
            super.updateItem(item, empty);
            if (item == null || empty) {
                return;
            }

            setText(item.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        }
    }





    private class LostEntryCell extends TableCell<ProcessedGachaEntry, ProcessedGachaEntry> {
        private static final double ICON_HEIGHT = 17;
        private static final double ICON_WIDTH_CHAR = 15;


        @Override
        protected void updateItem(ProcessedGachaEntry item, boolean empty) {
            super.updateItem(item, empty);
            if (item == null || empty) {
                return;
            }

            Path path = getImagePath(new GachaItem(item.itemId, item.name, item.rank, item.itemType));
            LostCountBox box = null;
            try (InputStream is = FileUtil.getInputStream(path)) {
                Image image = new Image(is, ICON_WIDTH_CHAR, ICON_HEIGHT, true, true);
                box = new LostCountBox(image, item.pityCount);
                setGraphic(box.getRoot());
                setTooltip(item);
            } catch (IOException ex) {
                // Ignore
            }
        }


        private void setTooltip(ProcessedGachaEntry entry) {
            Tooltip tooltip = new Tooltip();
            tooltip.setText(String.format("%s\nCount = %d\nTime = %s",
                    entry.name,
                    entry.pityCount,
                    entry.time.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))));
            tooltip.setShowDelay(Duration.millis(50));
            Tooltip.install(this, tooltip);
        }
    }





    private static class EntryPityCell extends TableCell<ProcessedGachaEntry, ProcessedGachaEntry> {
        @Override
        protected void updateItem(ProcessedGachaEntry item, boolean empty) {
            super.updateItem(item, empty);
            if (item == null || empty) {
                return;
            }

            switch (item.rank) {
                case 5:
                    setGraphic(new PityCountBox(
                            item.pityCount,
                            item.gachaType.getMax5Pity(),
                            item.isRateUp && !item.gachaType.equals(GachaType.STANDARD),
                            item.isRateUpWon
                        ).getRoot());
                    break;
                case 4:
                    setGraphic(new PityCountBox(
                            item.pityCount,
                            item.gachaType.getMax4Pity(),
                            item.isRateUp && !item.gachaType.equals(GachaType.STANDARD),
                            item.isRateUpWon
                        ).getRoot());
                    break;
                default:
                    setText("-");
                    break;
            }
        }
    }
}
