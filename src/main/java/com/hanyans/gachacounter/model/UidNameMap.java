package com.hanyans.gachacounter.model;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.hanyans.gachacounter.core.LockedValue;
import com.hanyans.gachacounter.mhy.Game;


/**
 * A synchronized map mapping UIDs to a name.
 */
public class UidNameMap {
    @JsonIgnore private final LockedValue<Game> lockedGame =
            new LockedValue<>();
    @JsonIgnore private final LockedValue<HashMap<Long, String>> lockedNameMap =
            new LockedValue<>(new HashMap<>());


    /**
     * Constructs an empty {@code UidNameMap} without a game and any mappings.
     */
    public UidNameMap() {}


    /**
     * Constructs a {@code UidNameMap} with no mappings.
     *
     * @param game - game the map is for.
     */
    public UidNameMap(Game game) {
        setGame(game);
    }


    /**
     * Constructs a {@code UidNameMap}.
     *
     * @param game - game the map is for.
     * @param nameMap - initial name mappings.
     */
    @JsonCreator
    public UidNameMap(
                @JsonProperty("game") Game game,
                @JsonProperty("nameMap") HashMap<Long, String> nameMap) {
        setGame(game);
        putAll(Objects.requireNonNullElse(nameMap, new HashMap<>()));
    }


    /**
     * Returns the game this {@code UidNameMap} is for.
     */
    @JsonProperty("game")
    public Game getGame() {
        return lockedGame.get();
    }


    /**
     * Sets the game of this map.
     *
     * @param game - game to set to.
     */
    public void setGame(Game game) {
        lockedGame.set(game);
    }


    /**
     * Returns the name map. Returned map is copied, hences changes to the
     * returned or encapsulated map will not change the other.
     */
    @JsonProperty("nameMap")
    public HashMap<Long, String> getNameMap() {
        return new HashMap<>(lockedNameMap.get());
    }


    /**
     * Puts the specified UID and name mapping in.
     *
     * @param uid - UID to put in.
     * @param name - the name mapping of the UID.
     */
    public void put(long uid, String name) {
        lockedNameMap.performWrite(map -> map.put(uid, name));
    }


    /**
     * Puts all the mappings of the given map into the encapsulated name map.
     * If a mapping for a key already exists in the encapsulated map, its
     * mapping is replaced.
     *
     * @throws NullPointerException if {@code nameMap} is {@code null}.
     */
    public void putAll(Map<Long, String> nameMap) {
        lockedNameMap.performWrite(map -> map.putAll(nameMap));
    }


    /**
     * Clears the name map.
     */
    public void clearMap() {
        lockedNameMap.performWrite(map -> map.clear());
    }


    /**
     * Resets and copies the state of the given reference over to this
     * {@code UidNameMap}.
     *
     * @param ref - {@code UidNameMap} to copy over.
     */
    public void reset(UidNameMap ref) {
        setGame(ref.getGame());
        clearMap();
        putAll(ref.getNameMap());
    }
}
