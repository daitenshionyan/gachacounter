package com.hanyans.gachacounter.mhy.response;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;


public class GachaResponseData {
    public final int page;
    public final int size;
    public final List<GachaEntryResponse> list;
    public final String region;
    public final int regionTimeZone;


    @JsonCreator
    public GachaResponseData(
                @JsonProperty("page") int page,
                @JsonProperty("size") int size,
                @JsonProperty("entries") List<GachaEntryResponse> listHsr,
                @JsonProperty("list") List<GachaEntryResponse> listGenshin,
                @JsonProperty("region") String region,
                @JsonProperty("regionTimeZone") int regionTimeZone) {
        this.page = page;
        this.size = size;
        this.list = getList(listHsr, listGenshin);
        this.region = region;
        this.regionTimeZone = regionTimeZone;
    }


    private static List<GachaEntryResponse> getList(List<GachaEntryResponse> l1, List<GachaEntryResponse> l2) {
        if (l1 != null && !l1.isEmpty()) {
            return l1;
        } else if (l2 != null && !l2.isEmpty()) {
            return l2;
        }
        return List.of();
    }
}
