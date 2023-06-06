package com.hanyans.gachacounter.core;


public class PopupMessage {
    public final String title;
    public final String content;
    public final MsgType msgType;


    public PopupMessage(String title, String content, MsgType msgType) {
        this.title = title;
        this.content = content;
        this.msgType = msgType;
    }





    public static enum MsgType {
        Success, Error
    }
}
