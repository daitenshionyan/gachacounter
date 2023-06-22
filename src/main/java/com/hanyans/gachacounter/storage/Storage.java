package com.hanyans.gachacounter.storage;

import java.util.Collection;

import com.hanyans.gachacounter.mhy.Game;
import com.hanyans.gachacounter.model.BannerHistory;
import com.hanyans.gachacounter.model.GameGachaData;
import com.hanyans.gachacounter.model.preference.UserPreference;


/**
 * A class that handles I/O operations of data to and from the hard disk.
 */
public interface Storage {
    /**
     * Loads the gacha data of the specified game.
     *
     * @param game - the game whose data is to be loaded.
     * @return a {@code LoadReport} of the loaded data.
     */
    public LoadReport<GameGachaData> loadGachaData(Game game);


    /**
     * Saves the state of the given {@code GameGachaData}.
     *
     * @param data - {@code GameGachaData} to save.
     * @return a list of exceptions that occured while saving.
     */
    public Collection<Throwable> saveGachaData(GameGachaData data);


    /**
     * Saves the state of the given {@code BannerHistory}.
     *
     * @param game - the game of the given history.
     * @param history - the history to save.
     * @return a list of exceptions that occured while saving.
     */
    public Collection<Throwable> saveBannerHistory(Game game, BannerHistory history);


    /**
     * Loads user preference data.
     *
     * @return a {@code LoadReport} of the loaded data.
     */
    public LoadReport<UserPreference> loadPreference();


    /**
     * Saves the state of the given {@code UserPreference}.
     *
     * @param preference - the preference to save.
     * @return a list of exceptions that occured while saving.
     */
    public Collection<Throwable> savePreference(UserPreference preference);
}
