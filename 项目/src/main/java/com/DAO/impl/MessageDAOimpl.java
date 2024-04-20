package com.DAO.impl;

import com.DAO.BaseDAO;
import com.DAO.MessageDAO;
import com.pojo.Message;
import java.util.List;

public  class MessageDAOimpl extends BaseDAO implements MessageDAO {

    @Override
    public List<Message> AdminMessages(String groupId) {
        try {
            String sql = "SELECT senter,message,groupid,type FROM tb_message WHERE groupid = ?AND type IN (?,?,?)";
            return executeQuery(Message.class, sql, groupId,"application","Exit","reply");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<Message> UserMessages(String groupId,String name) {
        try {
            String sql = "SELECT senter, message,groupid,type FROM tb_message WHERE recipient = ? AND type IN (?, ?)";
            return executeQuery(Message.class, sql, name, "invitation", "reply");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public int DeleteMessage(String senter, String message) {

        try {
            String sql ="DELETE FROM tb_message WHERE senter = ?AND message=?";
            return executeUpdate(sql,senter,message);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public int ForAgreement(String senter) {
        try {
            String sql = "DELETE FROM tb_message WHERE senter = ? AND type = ? AND recipient IS NULL";
            return executeUpdate(sql,senter,"application");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public int SendInvitation(String senter,String recipient, String groupid) {
        try {
            String sql = "INSERT INTO tb_message(senter,message,groupid,recipient,type) VALUES (?,?,?,?,?)";
            return executeUpdate(sql,senter,"邀请你加入"+groupid,groupid,recipient,"invitation");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public int SendAgreementReply(String senter,String recipient, String groupid) {
        try {
            String sql = "INSERT INTO tb_message(senter,message,recipient,type) VALUES (?,?,?,?)";
            return executeUpdate(sql,senter,"你已成功加入"+groupid,recipient,"reply");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    public int DeleteInvitationMessage(String senter,String recipient, String type) {
        try {
            String sql ="DELETE FROM tb_message WHERE senter = ?AND recipient=?AND type =?";
            return executeUpdate(sql,senter,recipient,type);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
