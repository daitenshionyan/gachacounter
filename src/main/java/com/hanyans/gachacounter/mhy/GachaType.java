package com.hanyans.gachacounter.mhy;

import java.util.HashSet;
import java.util.List;

/**
 * Enumberation representing the banner type of a gacha entry.
 *
 * <p>Enumberation also contains details of the banner that they
 * represent such as API gacha type number, 5 and 4 star pity.
 */
public enum GachaType {
    /** Standard (Regular) banner. */
    STANDARD(1, 200, 10, 90),
    /** Character banner. */
    CHARACTER(11, 301, 10, 90),
    /** Weapon (Light cone) banner. */
    WEAPON(12, 302, 10, 80);


    public static final HashSet<Integer> STANDARD_IDS = new HashSet<>(List.of(
        1, 200
    ));
    public static final HashSet<Integer> CHARACTER_IDS = new HashSet<>(List.of(
        11, 301, 400
    ));
    public static final HashSet<Integer> WEAPON_IDS = new HashSet<>(List.of(
        12, 302
    ));

    private final int typeIdHsr;
    private final int typeIdGenshin;
    private final int max5Pity;
    private final int max4Pity;


    GachaType(
                int typeIdHsr, int typeIdGenshin,
                int max4Pity, int max5Pity) {
        this.typeIdHsr = typeIdHsr;
        this.typeIdGenshin = typeIdGenshin;
        this.max4Pity = max4Pity;
        this.max5Pity = max5Pity;
    }


    /**
     * Returns the gacha type API number.
     */
    public int getTypeIdHsr() {
        return typeIdHsr;
    }


    public int getTypeIdGenshin() {
        return typeIdGenshin;
    }


    /**
     * Returns the maximum 4 star pity.
     */
    public int getMax4Pity() {
        return max4Pity;
    }


    /**
     * Returns the maximum 5 star pity.
     */
    public int getMax5Pity() {
        return max5Pity;
    }
}
