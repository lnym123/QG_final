package com.DAO;

import com.pojo.Message;

import java.util.List;

public interface MessageDAO {

    List<Message> AdminMessages(String groupId);

    List<Message> UserMessages(String groupId ,String name);

    int DeleteMessage(String senter,String message);

    int ForAgreement(String senter);

    int SendInvitation(String senter,String recipient, String groupid);

    int SendAgreementReply(String senter,String recipient,String groupid);

    int DeleteInvitationMessage(String senter,String recipient,String type) ;
}
