package com.service;

import com.pojo.Message;

import java.util.List;

public interface MessageService {


    List<Message> ForMessage(String id,String type);

    void AdminAgreement(String id,String Thesenter);

    String SendInvitation(String senter,String recipient);

    void adminHandleGroupApplication(String theSenter,String thegroupid,String message, String theRecipient,String type);

}
