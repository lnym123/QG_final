package com.service;

import com.pojo.User;

import java.util.List;

public interface UserService {

    void ChangePersonData(String username,String  codePassword,String  location,String  PhoneNumber);

    User Forlogin(String username,String codePassword);

    String ForRegister(User Theuser);

    User ShowImformation (String username);

    String SendApplication(String senter, String groupid, String message1);

    String ExitGroup(String username);

    void UserHandleInvitation(String type, String username,String groupname,String TheSenter);

    List<User> getAllUsers();

    String AdminHandleUser(String id,String type);

    String ConfirmResetAccount(String username,String location, String PhoneNumber,String type);
}
