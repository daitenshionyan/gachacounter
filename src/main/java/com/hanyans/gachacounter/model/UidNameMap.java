package com.hanyans.gachacounter.model;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;


/**
 * An encapsulation of a map mapping UIDs to a name.
 */
public class UidNameMap {
    private final HashMap<Long, String> nameMap = new HashMap<>();


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
     * Returns the name that is mapped to the given UID. If there exist no
     * mappings, the String representation of the UID is returned.
     *
     * @param uid - UID whose mapped name to return.
     */
    public String get(long uid) {
        return nameMap.getOrDefault(uid, String.valueOf(uid));
    }


    /**
     * Puts the specified UID and name mapping in.
     *
     * @param uid - UID to put in.
     * @param name - the name mapping of the UID.
     */
    public void put(long uid, String name) {
        nameMap.put(uid, name);
    }


    /**
     * Puts all the mappings of the given map into the encapsulated name map.
     * If a mapping for a key already exists in the encapsulated map, its
     * mapping is replaced.
     *
     * @throws NullPointerException if {@code nameMap} is {@code null}.
     */
    public void putAll(Map<Long, String> map) {
        nameMap.putAll(map);
    }


    /**
     * Clears the name map.
     */
    public void clearMap() {
        nameMap.clear();
    }


    /**
     * Resets and copies the state of the given reference over to this
     * {@code UidNameMap}.
     *
     * @param ref - {@code UidNameMap} to copy over.
     */
    public void reset(UidNameMap ref) {
        clearMap();
        putAll(ref.nameMap);
    }


    @Override
    public boolean equals(Object other) {
        if (other == null || !(other instanceof UidNameMap)) {
            return false;
        }
        UidNameMap casted = (UidNameMap) other;
        return nameMap.equals(casted.nameMap);
    }


    @Override
    public int hashCode() {
        return Objects.hash(nameMap);
    }
}
