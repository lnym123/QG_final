package com.DAO.impl;

import com.DAO.BaseDAO;
import com.DAO.MessageDAO;
import com.pojo.Message;
import com.util.JDBCUtilV2;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public  class MessageDAOimpl extends BaseDAO implements MessageDAO {

    @Override
    public List<Message> AdminMessages(String groupId) {
        try {
            String sql = "SELECT senter,message,groupid FROM tb_message WHERE groupid = ?";
            return executeQuery(Message.class, sql, groupId);
        } catch (Exception e) {
            // 将底层异常包装为自定义的DAO层异常，便于上层处理
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<Message> UserMessages(String groupId,String name) {
        try {
            String sql = "SELECT senter,message FROM tb_message WHERE recipient= ? ";
            return  executeQuery(Message.class, sql, name);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public int DeleteMessage(String senter, String message) {

        try {
            String sql = "delete from tb_message where senter= ? AND message= ?";
            return executeUpdate(sql,senter,message);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}