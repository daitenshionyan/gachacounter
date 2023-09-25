package com.hanyans.gachacounter.model;

import java.util.Objects;

import com.hanyans.gachacounter.mhy.Game;
import com.hanyans.gachacounter.model.rateup.BannerEventHistory;


/**
 * A data class that contains all banner history of a game.
 */
public class GameGachaData {
    /** The {@code Game} this history data is for. */
    public final Game game;
    /** UID name map. */
    public final UidNameMap nameMap;
    /** Standard banner history. */
    public final BannerHistory stndHist;
    /** Character banner history. */
    public final BannerHistory charHist;
    /** Weapon banner history. */
    public final BannerHistory weapHist;
    /** Character rate up event history. */
    public final BannerEventHistory charEvents;
    /** Weapon rate up event history. */
    public final BannerEventHistory weapEvents;


    /**
     * Constructs a {@code GameGacahHistory}.
     *
     * @param game - game this history is for.
     * @param nameMap - UID name map.
     * @param stndHist - standard banner history.
     * @param charHist - character banner history.
     * @param weapHist - weapon banner history.
     * @param charEvents - rate up events for character banner.
     * @param weapEvents - rate up events for weapon banner.
     */
    public GameGachaData(
                Game game,
                UidNameMap nameMap,
                BannerHistory stndHist,
                BannerHistory charHist,
                BannerHistory weapHist,
                BannerEventHistory charEvents,
                BannerEventHistory weapEvents) {
        this.game = Objects.requireNonNull(game);
        this.nameMap = Objects.requireNonNull(nameMap);
        this.stndHist = Objects.requireNonNull(stndHist);
        this.charHist = Objects.requireNonNull(charHist);
        this.weapHist = Objects.requireNonNull(weapHist);
        this.charEvents = Objects.requireNonNull(charEvents);
        this.weapEvents = Objects.requireNonNull(weapEvents);
    }
}
