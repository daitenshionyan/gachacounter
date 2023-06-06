package com.hanyans.gachacounter.logic;

import java.util.Collection;
import java.util.HashMap;
import java.util.function.Consumer;

import com.hanyans.gachacounter.core.AppUpdateMessage;
import com.hanyans.gachacounter.core.ErrorMessage;
import com.hanyans.gachacounter.core.task.ConsumerTask;
import com.hanyans.gachacounter.model.UserPreference;
import com.hanyans.gachacounter.model.count.GachaReport;
import com.hanyans.gachacounter.wrapper.Game;

import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.ReadOnlyDoubleProperty;
import javafx.beans.property.ReadOnlyObjectProperty;


/**
 * Represents a component to handle the logical processes.
 */
public interface Logic {
    /**
     * Updates the player's gacha history by retrieving the gacha logs from the
     * given player URL.
     *
     * <p>A valid game should be set using {@link #setGame(Game)} prior to a
     * call to this method.
     *
     * @param playerUrl - the player's URL from data file.
     */
    public void updateGachaHistory(String playerUrl);


    /**
     * Grabs the player's URL for gacha log retrieval.
     *
     * <p>A valid game should be set using {@link #setGame(Game)} prior to a
     * call to this method.
     *
     * @param pathString - the path Stirng to the data file.
     * @param onComplete - {@code Consumer} to handle completion.
     */
    public void grabPlayerUrl(String pathString, Consumer<String> onComplete);


    /**
     * Returns the message property of this {@code Logic}.
     */
    public ReadOnlyObjectProperty<String> messageProperty();


    /**
     * Returns the progress property of this {@code Logic}.
     */
    public ReadOnlyDoubleProperty progressProperty();


    /**
     * Returns the running property of this {@code Logic}.
     */
    public ReadOnlyBooleanProperty runningProperty();


    /**
     * Returns the {@code UserPreference} that this {@code Logic} is using.
     */
    public UserPreference getUserPrefs();


    /**
     * Sets the game as specified.
     */
    public void setGame(Game game);


    /**
     * Returns the currently set game.
     */
    public Game getGame();


    /**
     * Sets the task to be executed when a gacha report has been created.
     *
     * @param task - the task to be executed.
     */
    public void setReportCompletionTask(ConsumerTask<GachaReport> task);


    /**
     * Sets the handler to handle error messages.
     *
     * @param errMsgHandler - the error message handler to set to.
     */
    public void setErrorMessageHandler(Consumer<ErrorMessage> errMsgHandler);


    /**
     * Sets the UID to include.
     *
     * @param uids - the uids to include.
     */
    public void setUidFilter(Collection<Long> uids);


    /**
     * Returns the current UID filter map.
     */
    public HashMap<Long, Boolean> getUidFilterMap();


    /**
     * Manually saves the current state of data.
     */
    public void manualSave();


    /**
     * Checks if there is a newer version of the application.
     */
    public void checkForAppUpdates();


    /**
     * Sets the handler to handle application update messages.
     *
     * @param handler - the update message handler to set to.
     */
    public void setAppUpdateMessageHandler(Consumer<AppUpdateMessage> handler);
}
