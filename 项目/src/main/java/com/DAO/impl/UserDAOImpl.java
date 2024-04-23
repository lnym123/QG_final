package com.DAO.impl;

import com.DAO.BaseDAO;
import com.DAO.UserDAO;
import com.pojo.Message;
import com.pojo.User;

import java.util.List;

public class UserDAOImpl extends BaseDAO implements UserDAO {
    @Override
    public int insert(User user) {
        try {
            String sql = "INSERT INTO tb_user(username,password,location,nickname,PhoneNumber,authority) VALUES (?,?,?,?,?,?)";
            return executeUpdate(sql, user.getUsername(), user.getPassword(), user.getLocation(), user.getNickname()
                    , user.getPhoneNumber(),1);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<User> selectAll(String username,String password) {
        try {
            String sql = "SELECT * FROM tb_user WHERE username = ? AND password = ?";
            return executeQuery(User.class,sql,username,password);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public User selectByname(String username) {
        try {
            String sql = "SELECT username,password,location,nickname,PhoneNumber,groupid FROM tb_user WHERE username = ?";
            return executeQueryBean(User.class,sql,username);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public int updateData(User user) {
        try {
            String sql = "UPDATE tb_user SET password = ?,location=?,nickname=?,PhoneNumber=? WHERE username = ?";
            return executeUpdate(sql,user.getPassword(),user.getLocation(),user.getNickname(),user.getPhoneNumber(),user.getUsername());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public int sendapplication(Message message) {
        try {
            String sql = "INSERT INTO tb_message(senter,message,groupid,type) VALUES (?,?,?,?)";
            return executeUpdate(sql, message.getSenter(),message.getMessage(),message.getGroupid(),message.getType());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public int ExitGroup(String username) {
        try {
            String sql = "UPDATE tb_user SET groupid = NULL WHERE username = ?";
            return executeUpdate(sql,username);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public int ForAgreement(String username, String groupid) {
        try {
            String sql = "UPDATE tb_user SET groupid = ? WHERE username = ?";
            return executeUpdate(sql,groupid,username);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<User> selectAllUser() {
        try {
            String sql = "SELECT username,groupid,Groupfunds FROM tb_user";
            return executeQuery(User.class,sql);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public int OperateBanUser(String username,String action) {
        try {
            String sql = "UPDATE tb_user SET Locked = ? WHERE username = ?";
            return executeUpdate(sql,action,username);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public int AgreeCreateGroupMessage(String username, String groupid) {
        try {
            String sql = "UPDATE tb_user SET groupid=?,authority=? WHERE username = ?";
            return executeUpdate(sql,groupid,2,username);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public int InsertAvatar(String filepath, String username) {
        try {
            String sql = "UPDATE tb_user SET avatar_url=? WHERE username=?";

            return executeUpdate(sql, filepath, username);
        } catch (Exception e) {

            throw new RuntimeException(e);
        }
    }
}

