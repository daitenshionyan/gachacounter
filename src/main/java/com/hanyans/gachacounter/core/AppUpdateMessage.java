package com.hanyans.gachacounter.core;

import java.net.URL;


public class AppUpdateMessage {
    public final boolean hasUpdate;
    public final String message;
    public final URL url;


    public AppUpdateMessage(boolean hasUpdate, String message, URL url) {
        this.hasUpdate = hasUpdate;
        this.message = message;
        this.url = url;
    }
}
