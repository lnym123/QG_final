package com.dao;

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

    int ApplyOwnGroup(String senter,String groupid);

    List<Message> GetHighAdminMessage();
    //发生网站管理员拒接请求
    int SendDenyMessage(String recipient,String message);
    //发送用户拒绝企业请求
    int SendUserDenyMessage(String recipient,String groupid,String senter);

    int sendUserUnBanReques(String username);
}
