package com.hanyans.gachacounter.wrapper.response;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;


public class GachaResponseResult {
    public final int retcode;
    public final String message;
    public final GachaResponseData data;


    @JsonCreator
    public GachaResponseResult(
                @JsonProperty("retcode") int retcode,
                @JsonProperty("message") String message,
                @JsonProperty("data") GachaResponseData data) {
        this.retcode = retcode;
        this.message = message;
        this.data = data;
    }
}
