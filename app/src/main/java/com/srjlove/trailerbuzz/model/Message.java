package com.srjlove.trailerbuzz.model;

/**
 * Created by Suraj on 12/5/2017.
 */

public class Message {

    private String author;
    private String msg;
   private String imgeUrl;

    public Message() {
    }

    public Message(String author, String msg, String imgeUrl) {
        this.author = author;
        this.msg = msg;
        this.imgeUrl = imgeUrl;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public String getImgeUrl() {
        return imgeUrl;
    }

    public void setImgeUrl(String imgeUrl) {
        this.imgeUrl = imgeUrl;
    }
}
