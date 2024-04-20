package com.DAO;

import com.pojo.Message;
import com.pojo.User;

import java.util.List;

public interface UserDAO {

    int insert(User user);

    List<User> selectAll(String username,String password);

    User selectByname(String username);

    int updateData(User user);

    int sendapplication(Message message);

    int ExitGroup(String username);
}
