package com.hanyans.gachacounter.model;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.nio.file.Path;
import java.util.HashMap;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import com.hanyans.gachacounter.core.util.JsonUtil;
import com.hanyans.gachacounter.mhy.Game;

public class UidNameMapTest {
    @TempDir private Path savePath;


    @Test
    public void jsonSaveTest() throws Throwable {
        UidNameMap expectedMap = new UidNameMap(Game.Genshin, getInitialMap());
        Path saveFile = savePath.resolve("NameMap.json");

        JsonUtil.serializeToFile(saveFile, expectedMap);
        UidNameMap actualMap = JsonUtil.deserialize(saveFile, UidNameMap.class);

        assertEquals(expectedMap.getGame(), actualMap.getGame());
        assertEquals(expectedMap.getNameMap(), actualMap.getNameMap());
    }


    private HashMap<Long, String> getInitialMap() {
        HashMap<Long, String> initialMap = new HashMap<>();
        initialMap.put(1L, "A");
        initialMap.put(2L, "B");
        initialMap.put(3L, "C");
        return initialMap;
    }
}
