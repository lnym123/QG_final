package com.service.impl;


import com.dao.GroupDAO;
import com.dao.UserDAO;
import com.dao.impl.GroupDAOimpl;
import com.dao.impl.UserDAOImpl;
import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.pojo.Group;
import com.pojo.User;
import com.service.GroupService;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class GroupServiceImpl implements GroupService {
    private final GroupDAO groupDAO = new GroupDAOimpl();
    private final UserDAO userDao = new UserDAOImpl();
    private static final Cache<String, Group> GroupCache = Caffeine.newBuilder()
            .maximumSize(1000)
            .expireAfterWrite(10, TimeUnit.MINUTES)
            .build();

    @Override
    //获取所有公开群组
    public List<Group> GetAllGroups(String type) {
        List<Group> groups;
        if (type.equals("all")) {
            groups=groupDAO.selectAllForAdmin();
            return groups;
        }
        if (!GroupCache.asMap().isEmpty()) {
            // 缓存不为空，从缓存中获取数据
            groups = new ArrayList<>(GroupCache.asMap().values());
        } else {
            // 缓存为空，查询数据库
            groups = groupDAO.selectAll();
            for (Group group : groups) {
                GroupCache.put(group.getGroupname(), group); // 缓存查询结果
            }

        }
        return groups;
    }
  //条件查询群组
    @Override
    public List<Group> GroupQuery(String groupName, String direction) {

        List<Group> groups;
        try {
            groups = groupDAO.selectBycondition(groupName,direction);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return groups;
    }
   //获得个人群组信息
    @Override
    public Group ShowPersonalGroup(String username) {
        User user = userDao.selectByname(username);
        String GroupName =user.getGroupid();
        return groupDAO.selectById(GroupName);
    }
  //修改群组信息
    @Override
    public void ChangeGroupInfo(String userID, String number, String scale, String direction, String visiable) {
        User user = userDao.selectByname(userID);
        String groupname =user.getGroupid();
        groupDAO.ChangeGroupData(groupname,number,scale,direction,visiable);
    }

    @Override
    public void AdminOperateGroup(String id, String type) {
        if(type.equals("ban")){
            groupDAO.OperateBanGroup(id,"true");
        }else{
            groupDAO.OperateBanGroup(id,"false");
        }
    }

    @Override
    public void LogOutGroup(String theAdmin, int PublicFunds) {
        User user = userDao.selectByname(theAdmin);
        String GroupName =user.getGroupid();
        userDao.ForLogOutGroup(GroupName,theAdmin,PublicFunds);
    }


}
