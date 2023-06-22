package com.hanyans.gachacounter.mhy.response;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;


/**
 * Represents a gacha entry that a response from API call defines. Also serves
 * as a convenience class to convert response data defining a gacha entry to an
 * object.
 */
public class GachaEntryResponse {
    public final long uid;
    public final int gacha_id;
    public final int gacha_type;
    public final int item_id;
    public final int count;
    public final LocalDateTime time;
    public final String name;
    public final String lang;
    public final String item_type;
    public final int rank_type;
    public final long id;


    @JsonCreator
    public GachaEntryResponse(
                @JsonProperty("uid") long uid,
                @JsonProperty("gacha_id") int gachaId,
                @JsonProperty("gacha_type") int gachaType,
                @JsonProperty("item_id") int itemId,
                @JsonProperty("count") int count,
                @JsonProperty("time") String time,
                @JsonProperty("name") String name,
                @JsonProperty("lang") String lang,
                @JsonProperty("item_type") String itemType,
                @JsonProperty("rank_type") int rank_type,
                @JsonProperty("id") long id) {
        this.uid = uid;
        this.gacha_id = gachaId;
        this.gacha_type = gachaType;
        this.item_id = itemId;
        this.count = count;
        this.time = LocalDateTime.parse(time, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        this.name = name;
        this.lang = lang;
        this.item_type = itemType;
        this.rank_type = rank_type;
        this.id = id;
    }
}
