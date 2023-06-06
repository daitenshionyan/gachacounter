package com.hanyans.gachacounter.core;

import java.net.URL;


public class AppUpdateMessage {
    public final String message;
    public final URL url;


    public AppUpdateMessage(String message, URL url) {
        this.message = message;
        this.url = url;
    }
}
