package com.pojo;

public class Message {
     private String senter;
     private String message;
     private String groupid;
     private String recipient;

    public String getSenter() {
        return senter;
    }

    public Message() {
    }

    public Message(String senter, String message, String groupid) {
        this.senter = senter;
        this.message = message;
        this.groupid = groupid;
    }

    public void setSenter(String senter) {
        this.senter = senter;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getGroupid() {
        return groupid;
    }

    public void setGroupid(String groupid) {
        this.groupid = groupid;
    }

    public String getRecipient() {
        return recipient;
    }

    public void setRecipient(String recipient) {
        this.recipient = recipient;
    }
}
