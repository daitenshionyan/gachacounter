package com.hanyans.gachacounter;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.config.Configuration;
import org.apache.logging.log4j.core.config.LoggerConfig;

import com.hanyans.gachacounter.core.Version;
import com.hanyans.gachacounter.core.util.FileUtil;
import com.hanyans.gachacounter.gui.CounterPanel;
import com.hanyans.gachacounter.logic.LogicManager;
import com.hanyans.gachacounter.model.UserPreference;
import com.hanyans.gachacounter.storage.LoadReport;
import com.hanyans.gachacounter.storage.StorageManager;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;


public class MainApp extends Application {
    public static final Version VERSION = new Version(0, 2, 0);

    private static final String ICON_FILE = "/view/img/icon.png";

    private static final Logger logger = LogManager.getFormatterLogger(MainApp.class);


    private StorageManager storage = null;
    private UserPreference preference= null;
    private LogicManager logic = null;


    @Override
    public void start(Stage mainStage) throws Exception {
        logger.info("=========< APPLICATION START %s >=========", VERSION);

        storage = new StorageManager();

        LoadReport<UserPreference> prefLoadReport = storage.loadPreference();
        preference = prefLoadReport.data;

        setLogLevel(preference.getLogLevel());

        logic = new LogicManager(VERSION, storage, preference);

        CounterPanel panel = new CounterPanel(logic, mainStage);
        mainStage.setScene(new Scene(panel.getRoot()));

        mainStage.setTitle("Gacha Counter");
        setStageIcon(mainStage);
        mainStage.show();

        if (preference.isCheckUpdateOnStart()) {
            logic.checkForAppUpdates(false);
        }
        logic.handleIoError(prefLoadReport.exList, "Error while loading preference");
    }


    @Override
    public void stop() {
        if (logic != null) {
            logic.shutdown();
        }
        logger.info("=========< APPLICATION CLOSE %s >=========", VERSION);
    }


    public static void setStageIcon(Stage stage) {
        try {
            Image icon = new Image(FileUtil.getResourceUrl(ICON_FILE).toString());
            if (icon.isError()) {
                throw icon.getException();
            }
            stage.getIcons().add(icon);
        } catch (Throwable ex) {
            ex.printStackTrace();
            return;
        }
    }


    public synchronized static void setLogLevel(Level logLevel) {
        // https://stackoverflow.com/a/23434603
        LoggerContext context = (LoggerContext) LogManager.getContext(false);
        Configuration config = context.getConfiguration();
        LoggerConfig loggerConfig = config.getLoggerConfig(LogManager.ROOT_LOGGER_NAME);
        loggerConfig.setLevel(logLevel);
        context.updateLoggers();
        logger.info("Log level set to <%s>", logLevel);
    }
}
