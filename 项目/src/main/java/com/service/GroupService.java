package com.service;

import com.pojo.Group;

import java.util.List;

public interface GroupService {
    List<Group> GetAllGroups(String type);

    List<Group> GroupQuery(String groupName, String direction);

    Group ShowPersonalGroup(String username);

    void ChangeGroupInfo(String userID, String number, String scale, String direction, String visiable);

    void AdminOperateGroup(String id,String type);

    void LogOutGroup(String theAdmin,int PublicFunds);
}
