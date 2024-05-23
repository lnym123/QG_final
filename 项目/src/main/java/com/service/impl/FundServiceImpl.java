package com.service.impl;

import com.dao.FundDAO;
import com.dao.GroupDAO;
import com.dao.UserDAO;
import com.dao.impl.FundDAOimpl;
import com.dao.impl.GroupDAOimpl;
import com.dao.impl.UserDAOImpl;
import com.pojo.Funds;
import com.pojo.Group;
import com.pojo.User;
import com.service.FundService;
import com.service.GroupService;
import com.service.UserService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FundServiceImpl implements FundService {
UserService userService = new UserServiceImpl();
GroupService groupService = new GroupServiceImpl();
    FundDAO fundDAO=new FundDAOimpl();
    UserDAO userDAO=new UserDAOImpl();
    GroupDAO groupDAO=new GroupDAOimpl();
    //展示个人流水
    @Override
    public List<Funds> ShowPersonalFlow(String username) {
        return fundDAO.SelectPersonalFunds(username);
    }

    @Override
    public List<Funds> fundsQueryService(String name, String content, String username) {
        return fundDAO.selectByCondition(username,name,content);
    }
    //获得各个群组的余额
    @Override
    public Map<String, Double> GetGroupBalance() {
        List<User> users = userService.getAllUsers();
        Map<String, Double> groupBalances = new HashMap<>();
        for (User user : users) {
            String groupName = user.getGroupid();
            // 跳过空群组ID
            if (groupName == null || groupName.isEmpty()) {
                continue;
            }
            double userBalance = user.getGroupfunds();
            // 如果群组已存在于 Map 中，取出当前累计余额
            double currentGroupBalance = groupBalances.getOrDefault(groupName, 0.0);
            // 更新群组累计余额
            double updatedGroupBalance = currentGroupBalance + userBalance;
            // 将更新后的余额存回 Map
            groupBalances.put(groupName, updatedGroupBalance);
        }
        List<Group> groups=groupService.GetAllGroups("notall");
        for (Group group : groups) {
            String groupName = group.getGroupname();
            // 跳过空群组ID
            if (groupName == null || groupName.isEmpty()) {
                continue;
            }
            double userBalance = group.getPublicfunds();
            // 如果群组已存在于 Map 中，取出当前累计余额
            double currentGroupBalance = groupBalances.getOrDefault(groupName, 0.0);
            // 更新群组累计余额
            double updatedGroupBalance = currentGroupBalance + userBalance;
            // 将更新后的余额存回 Map
            groupBalances.put(groupName, updatedGroupBalance);
        }
      return groupBalances;
    }
    //得到各个公共群组的资金
    @Override
    public List<User> GetBalanceOfEachMemberInGroup(String theAdminName) {
        User user =userDAO.selectByname(theAdminName);
        String groupname =user.getGroupid();
        return userDAO.selectGroupMumber(groupname);
    }
    //得到群组的公共资金
    @Override
    public Group GetGroupPublicFund(String theAdminName) {
        User user =userDAO.selectByname(theAdminName);
        String groupname =user.getGroupid();
        return groupDAO.SelectGroupPublicFund(groupname);
    }
    //分配资金
    @Override
    public String Allocatefunds(String theuser, int Useramount, int pubilicAmount,  String type) {

        User user =userDAO.selectByname(theuser);
        String groupname =user.getGroupid();
        fundDAO.Allocatefunds(theuser,Useramount,pubilicAmount,groupname);
        return "操作完毕";
    }
    //获得企业流水
    @Override
    public List<Funds> ShowGroupFlow(String username) {
        User user =userDAO.selectByname(username);
        String GroupName =user.getGroupid();
        return fundDAO.SelectGroupFunds(GroupName);
    }
}
