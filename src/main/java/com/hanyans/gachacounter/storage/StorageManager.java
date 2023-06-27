package com.hanyans.gachacounter.storage;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.fasterxml.jackson.databind.DatabindException;
import com.hanyans.gachacounter.core.util.FileUtil;
import com.hanyans.gachacounter.core.util.JsonUtil;
import com.hanyans.gachacounter.mhy.GachaType;
import com.hanyans.gachacounter.mhy.Game;
import com.hanyans.gachacounter.model.BannerHistory;
import com.hanyans.gachacounter.model.GameGachaData;
import com.hanyans.gachacounter.model.UidNameMap;
import com.hanyans.gachacounter.model.preference.UserPreference;
import com.hanyans.gachacounter.model.rateup.BannerEventHistory;


/**
 * Complete implementation of {@link Storage} to manage I/O operations of data
 * to and from hard disk.
 */
public class StorageManager implements Storage {
    /** User data directory path relative to the working directory. */
    public static final Path USER_DATA_DIR_PATH = Path.of("user_data");
    /** Event data directory path relative to the working directory. */
    public static final Path EVENT_DIR_PATH = Path.of("events");

    /**
     * HSR data directory path relative to either {@link #USER_DATA_DIR_PATH}
     * or {@link #EVENT_DIR_PATH}.
     */
    public static final Path HSR_DIR_PATH = Path.of("HSR");
    /**
     * Genshin data directory path relative to either
     * {@link #USER_DATA_DIR_PATH} or {@link #EVENT_DIR_PATH}.
     */
    public static final Path GENSHIN_DIR_PATH = Path.of("Genshin");

    /**
     * UID name map data file path relative to a game directory.
     */
    public static final Path NAME_MAP_PATH = Path.of("NameMap.json");
    /**
     * Standard history data file path relative to a game directory.
     */
    public static final Path STND_HIST_PATH = Path.of("StandardHistory.json");
    /**
     * Character history data file path relative to either
     * {@link #HSR_DIR_PATH} or {@link #GENSHIN_DIR_PATH}.
     */
    public static final Path CHAR_HIST_PATH = Path.of("CharacterHistory.json");
    /**
     * Weapon history data file path relative to either {@link #HSR_DIR_PATH}
     * or {@link #GENSHIN_DIR_PATH}.
     */
    public static final Path WEAP_HIST_PATH = Path.of("WeaponHistory.json");
    /**
     * Character events data file path relative to a game directory.
     */
    public static final Path CHAR_EVENTS_PATH = Path.of("CharacterEvents.json");
    /**
     * Weapon events data file path relative to a game directory.
     */
    public static final Path WEAP_EVENTS_PATH = Path.of("WeaponEvents.json");

    /** User preference data file path relative to the working directory. */
    public static final Path USER_PREF_PATH = USER_DATA_DIR_PATH.resolve("Preference.json");


    private final Logger logger = LogManager.getFormatterLogger(StorageManager.class);


    @Override
    public LoadReport<GameGachaData> loadGachaData(Game game) {
        ArrayList<Throwable> exList = new ArrayList<>();
        Path gamePath = getGamePath(game, USER_DATA_DIR_PATH);
        Path eventPath = getGamePath(game, EVENT_DIR_PATH);
        GameGachaData data = new GameGachaData(
                game,
                loadNameMap(gamePath.resolve(NAME_MAP_PATH), exList),
                loadHistory(GachaType.STANDARD, gamePath.resolve(STND_HIST_PATH), exList),
                loadHistory(GachaType.CHARACTER, gamePath.resolve(CHAR_HIST_PATH), exList),
                loadHistory(GachaType.WEAPON, gamePath.resolve(WEAP_HIST_PATH), exList),
                loadEvent(GachaType.CHARACTER, eventPath.resolve(CHAR_EVENTS_PATH), exList),
                loadEvent(GachaType.WEAPON, eventPath.resolve(WEAP_EVENTS_PATH), exList));
        return new LoadReport<>(data, exList);
    }


    private UidNameMap loadNameMap(Path path, ArrayList<Throwable> exList) {
        UidNameMap nameMap = loadData(path, UidNameMap.class, exList, "UID NAME MAP");
        if (nameMap == null) {
            logger.info("An empty name map will be used");
            return new UidNameMap();
        }
        logger.info("Successfully loaded name map");
        return nameMap;
    }


    private BannerHistory loadHistory(GachaType gachaType, Path path, ArrayList<Throwable> exList) {
        BannerHistory history = loadData(path, BannerHistory.class, exList, String.format("%s HISTORY", gachaType));
        if (history == null) {
            logger.info("An empty history data will be used for <%s HISTORY>",
                    gachaType);
            return new BannerHistory(gachaType);
        }
        logger.info("Successfully loaded %d <%s HISTORY>",
                history.size(), gachaType);
        return history;
    }


    private BannerEventHistory loadEvent(GachaType gachaType, Path path, ArrayList<Throwable> exList) {
        BannerEventHistory events = loadData(path, BannerEventHistory.class, exList, String.format("%s EVENT", gachaType));
        if (events == null) {
            logger.info("An empty event data will be used for <%s EVENT>",
                    gachaType);
            return new BannerEventHistory();
        }
        logger.info("Successfully loaded %d <%s EVENT>",
                events.size(), gachaType);
        return events;
    }


    /**
     * {@inheritDoc}
     *
     * <p>If given data is {@code null}, nothing will happen.
     */
    @Override
    public ArrayList<Throwable> saveGachaData(GameGachaData data) {
        ArrayList<Throwable> exList = new ArrayList<>();
        if (data == null) {
            return exList;
        }
        exList.addAll(saveNameMap(data.game, data.nameMap));
        exList.addAll(saveBannerHistory(data.game, data.stndHist));
        exList.addAll(saveBannerHistory(data.game, data.charHist));
        exList.addAll(saveBannerHistory(data.game, data.weapHist));
        return exList;
    }


    @Override
    public ArrayList<Throwable> saveNameMap(Game game, UidNameMap nameMap) {
        Path gamePath = getGamePath(game, USER_DATA_DIR_PATH).resolve(NAME_MAP_PATH);
        ArrayList<Throwable> exList = new ArrayList<>();
        try {
            FileUtil.createFile(gamePath);
            JsonUtil.serializeToFile(gamePath, nameMap);
            logger.info("Successfully save the state of <NAME MAP>");
        } catch (Throwable ex) {
            exList.add(new IOException(String.format("[NAME MAP]:\n%s",
                    ex.toString())));
            logger.error(
                    "Failed to save the state of <NAME MAP>", ex);
        }
        return exList;
    }


    @Override
    public ArrayList<Throwable> saveBannerHistory(Game game, BannerHistory history) {
        Path gamePath = getGamePath(game, USER_DATA_DIR_PATH);
        switch (history.getGachaType()) {
            case STANDARD:
                gamePath = gamePath.resolve(STND_HIST_PATH);
                break;
            case CHARACTER:
                gamePath = gamePath.resolve(CHAR_HIST_PATH);
                break;
            case WEAPON:
                gamePath = gamePath.resolve(WEAP_HIST_PATH);
                break;
            default:
                throw new IllegalArgumentException(
                        String.format("Unknown gacha type <%s>", history.getGachaType()));
        }

        ArrayList<Throwable> exList = new ArrayList<>();
        try {
            FileUtil.createFile(gamePath);
            JsonUtil.serializeToFile(gamePath, history);
            logger.info("Successfully save the state of <%s HISTORY>", history.getGachaType());
        } catch (Throwable ex) {
            exList.add(new IOException(String.format("[%s HISTORY]:\n%s",
                    history.getGachaType(), ex.toString())));
            logger.error(
                    String.format("Failed to save the state of <%s HISTORY>", history.getGachaType()),
                    ex);
        }
        return exList;
    }


    @Override
    public LoadReport<UserPreference> loadPreference() {
        ArrayList<Throwable> exList = new ArrayList<>();
        UserPreference prefs = loadData(USER_PREF_PATH, UserPreference.class, exList, "USER PREFERENCE");
        if (prefs == null) {
            logger.info("An empty event data will be used for <USER PREFERENCE>");
            prefs = new UserPreference();
        } else {
            logger.info("Successfully loaded <USER PREFERENCE>");
        }
        return new LoadReport<>(prefs, exList);
    }


    @Override
    public ArrayList<Throwable> savePreference(UserPreference preference) {
        ArrayList<Throwable> exList = new ArrayList<>();
        try {
            FileUtil.createFile(USER_PREF_PATH);
            JsonUtil.serializeToFile(USER_PREF_PATH, preference);
            logger.info("Successfully save the state of <USER PREFERENCE>");
        } catch (Throwable ex) {
            exList.add(new IOException(String.format("[USER PREFERENCE]:\n%s",
                    ex.toString())));
            logger.error("Failed to save the state of <USER PREFERENCE>", ex);
        }
        return exList;
    }


    private Path getGamePath(Game game, Path from) {
        switch (game) {
            case HSR:
                return from.resolve(HSR_DIR_PATH);
            case Genshin:
                return from.resolve(GENSHIN_DIR_PATH);
            default:
                throw new IllegalArgumentException(String.format("Unknown game type <%s>", game));
        }
    }


    private <T> T loadData(Path path, Class<T> valueType, ArrayList<Throwable> exList, String dataName) {
        T data = null;
        try {
            data = JsonUtil.deserialize(path, valueType);
        } catch (FileNotFoundException fnfEx) {
            // ignore file not found
            // exList.add(new FileNotFoundException(String.format("[%s]: %s",
            //         dataName,
            //         fnfEx.getMessage())));
            logger.warn("Could not find <%s> data file -- %s",
                    dataName, fnfEx.getMessage());
        } catch (DatabindException databindEx) {
            exList.add(new IOException(String.format("[%s]: %s (%s)",
                    dataName,
                    databindEx.getOriginalMessage(),
                    databindEx.getLocation().offsetDescription())));
            logger.warn("Error in <%s> data file -- %s (%s)",
                    dataName,
                    databindEx.getOriginalMessage(),
                    databindEx.getLocation().offsetDescription());
        } catch (Throwable ex) {
            exList.add(new IOException(String.format("[%s]: %s",
                    dataName,
                    ex)));
            logger.error(
                    String.format("Error occured while loading <%s> data file",
                            dataName),
                    ex);
        }
        return data;
    }
}
