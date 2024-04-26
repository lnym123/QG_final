package com.pojo;

public class Message {
     private String senter;
     private String message;
     private String groupid;
     private String recipient;
     private  String type;

    public String getType() {
        return type;
    }

    public Message(String senter, String message, String groupid, String recipient, String type) {
        this.senter = senter;
        this.message = message;
        this.groupid = groupid;
        this.recipient = recipient;
        this.type = type;
    }

    public Message(String senter, String message, String groupid, String type) {
        this.senter = senter;
        this.message = message;
        this.groupid = groupid;
        this.type = type;
    }

    @Override
    public String toString() {
        return "Message{" +
                "senter='" + senter + '\'' +
                ", message='" + message + '\'' +
                ", groupid='" + groupid + '\'' +
                ", recipient='" + recipient + '\'' +
                ", type='" + type + '\'' +
                '}';
    }

    public void setType(String type) {
        this.type = type;
    }

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
