package com.avinash.clearwork.Chat;

import android.content.SharedPreferences;

import java.util.ArrayList;

public class MessageObject {

    String messageId;
    String senderId;
    String message;

    public String getAppUserID() {
        return appUserID;
    }



    String appUserID;

    ArrayList<String> mediaUrlList;

    public MessageObject(String messageId, String senderId, String message, ArrayList<String> mediaUrlList, String appUserID){

        this.messageId = messageId;
        this.senderId = senderId;
        this.message = message;
        this.mediaUrlList = mediaUrlList;
        this.appUserID = appUserID;


    }

    public String getMessageId() {
        return messageId;
    }

    public String getSenderId() {
        return senderId;
    }

    public String getMessage() {
        return message;
    }

    public ArrayList<String> getMediaUrlList() {
        return mediaUrlList;
    }
}
