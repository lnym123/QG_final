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

    int ForAgreement(String username,String groupid);

    List<User> selectAllUser();

    int OperateBanUser(String username,String action);

    //管理员同意添加群组后，修改群员所在群组及权限
    int AgreeCreateGroupMessage(String username,String groupid);
}
