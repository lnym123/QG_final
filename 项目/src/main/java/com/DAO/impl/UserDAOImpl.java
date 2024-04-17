package com.DAO.impl;

import com.DAO.BaseDAO;
import com.DAO.UserDAO;
import com.pojo.User;

import java.util.List;

public class UserDAOImpl extends BaseDAO implements UserDAO {
    @Override
    public int insert(User user) {
        System.out.println(user.getUsername()+user.getPassword()+user.getNickname()+user.getLocation()+user.getPhoneNumber());
        try {
            String sql = "INSERT INTO tb_user(username,password,location,nickname,PhoneNumber) VALUES (?,?,?,?,?)";
            return executeUpdate(sql, user.getUsername(), user.getPassword(), user.getLocation(), user.getNickname()
                    , user.getPhoneNumber());
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


}

