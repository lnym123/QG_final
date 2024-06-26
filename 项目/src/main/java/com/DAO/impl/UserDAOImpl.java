package com.dao.impl;

import com.dao.BaseDAO;
import com.dao.UserDAO;
import com.pojo.Message;
import com.pojo.User;

import java.util.List;

public class UserDAOImpl extends BaseDAO implements UserDAO {
    @Override
    public int insert(User user) {
        try {
            String sql = "INSERT INTO tb_user(username,password,location,PhoneNumber,authority,Personalfunds,Groupfunds,Locked) VALUES (?,?,?,?,?,?,?,?)";
            return executeUpdate(sql, user.getUsername(), user.getPassword(), user.getLocation()
                    , user.getPhoneNumber(),1,1000,0,"false");
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
            String sql = "SELECT username,password,location,PhoneNumber,authority,groupid,Personalfunds,Groupfunds,Locked FROM tb_user WHERE username = ?";
            return executeQueryBean(User.class,sql,username);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public int updateData(User user) {
        try {
            String sql = "UPDATE tb_user SET password = ?,location=?,PhoneNumber=? WHERE username = ?";
            return executeUpdate(sql,user.getPassword(),user.getLocation(),user.getPhoneNumber(),user.getUsername());
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
            String sql = "UPDATE tb_user SET groupid = NULL,authority=? WHERE username = ?";
            return executeUpdate(sql,1,username);
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
            String sql = "SELECT username,groupid,Personalfunds,Groupfunds FROM tb_user";
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

    @Override
    public List<User> selectGroupMumber(String groupid) {
        try {
            String sql = "SELECT username,Groupfunds,authority FROM tb_user WHERE groupid=?";
            return executeQuery(User.class,sql,groupid);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public int ForLogOutGroup(String groupid,String username,int amount) {
        try {
            String sql1 = "UPDATE tb_user SET Personalfunds=Personalfunds+Groupfunds WHERE groupid=?";
            String sql2 = "UPDATE tb_user SET Personalfunds=Personalfunds+? WHERE username=?";
            String sql5 = "UPDATE tb_user SET Groupfunds=0 WHERE groupid=?";
            String sql3 = "UPDATE tb_user SET groupid=NULL WHERE groupid=?";
            String sql4 = "UPDATE tb_user SET authority=1 WHERE username=?";
            String sql6 ="DELETE FROM tb_group WHERE groupname=?";
            return executeUpdate(sql2, amount,username)+executeUpdate(sql1, groupid)+executeUpdate(sql5,groupid)+
                    executeUpdate(sql3, groupid)+executeUpdate(sql4, username)+executeUpdate(sql6,groupid);
        } catch (Exception e) {

            throw new RuntimeException(e);
        }
    }

    @Override
    public User CheckResetPasswordAccount(String username, String PhoneNumber, String location) {
        try {
            String sql = "SELECT * FROM tb_user WHERE username=? AND PhoneNumber=? AND location=?";
            return executeQueryBean(User.class,sql,username,PhoneNumber,location);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public int ResetPassword(String username, String password) {
        try {
            String sql = "UPDATE tb_user SET password=? WHERE username=?";
            return executeUpdate(sql,password,username);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public int FreezePersonalFund(String username, int amount) {
        try {
            String sql = "UPDATE tb_user SET Personalfunds=Personalfunds-? WHERE username=?";
            return executeUpdate(sql,amount,username);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public int FreezeGroupFund(String username, int amount) {
        try {
            String sql = "UPDATE tb_user SET Groupfunds=Groupfunds-? WHERE username=?";
            return executeUpdate(sql,amount,username);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}

