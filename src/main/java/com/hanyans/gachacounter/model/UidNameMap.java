package com.hanyans.gachacounter.model;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.hanyans.gachacounter.core.LockedValue;


/**
 * A synchronized map mapping UIDs to a name.
 */
public class UidNameMap {
    @JsonIgnore private final LockedValue<HashMap<Long, String>> lockedNameMap =
            new LockedValue<>(new HashMap<>());


    /**
     * Constructs an empty {@code UidNameMap}.
     */
    public UidNameMap() {}


    /**
     * Constructs a {@code UidNameMap}.
     *
     * @param nameMap - initial name mappings.
     */
    @JsonCreator
    public UidNameMap(
                @JsonProperty("nameMap") HashMap<Long, String> nameMap) {
        putAll(Objects.requireNonNullElse(nameMap, new HashMap<>()));
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
        clearMap();
        putAll(ref.getNameMap());
    }
}
