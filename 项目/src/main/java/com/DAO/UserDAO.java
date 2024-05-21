package com.dao;

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

    int InsertAvatar(String filepath,String username);

    List<User> selectGroupMumber(String groupid);

    int ForLogOutGroup(String groupid,String username,int amount);

    User CheckResetPasswordAccount(String username,String PhoneNumber,String location);
   //重置密码
    int ResetPassword(String username,String password);
    //交易发起，扣除用户资金
    int FreezePersonalFund(String username,int amount);

    int FreezeGroupFund(String username,int amount);



}
