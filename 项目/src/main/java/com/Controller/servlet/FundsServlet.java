package com.controller.servlet;

import com.controller.BaseServlet;
import com.dao.FundDAO;
import com.dao.GroupDAO;
import com.dao.UserDAO;
import com.dao.impl.FundDAOimpl;
import com.dao.impl.GroupDAOimpl;
import com.dao.impl.UserDAOImpl;
import com.alibaba.fastjson.JSON;
import com.pojo.Funds;
import com.pojo.Group;
import com.pojo.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@WebServlet("/funds/*")
public class FundsServlet extends BaseServlet {
    private static final Logger logger = LoggerFactory.getLogger(FundsServlet.class);
 FundDAO fundDAO=new FundDAOimpl();
 UserDAO userDAO=new UserDAOImpl();
 GroupDAO groupDAO=new GroupDAOimpl();
    public void ShowPersonalFlow(HttpServletRequest request, HttpServletResponse resp) throws ServletException, IOException {
        String username =request.getParameter("username");
        List<Funds> funds=fundDAO.SelectPersonalFunds(username);
        String jsonString= JSON.toJSONString(funds);
        resp.setContentType("text/json;charset=utf-8");
        resp.getWriter().write(jsonString);

    }
    public void fundsQueryService(HttpServletRequest request,HttpServletResponse resp) throws ServletException, IOException {
        String name = request.getParameter("name");
        String content = request.getParameter("content");
        String username = request.getParameter("username");

        List<Funds> funds= fundDAO.selectByCondition(username,name,content);
        String jsonString= JSON.toJSONString(funds);
        resp.setContentType("text/json;charset=utf-8");
        resp.getWriter().write(jsonString);

    }
    public void GetGroupBalance(HttpServletRequest request, HttpServletResponse resp) throws ServletException, IOException {
        List<User> users = userDAO.selectAllUser();
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
        List<Group> groups=groupDAO.selectAll();
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


        // 将群组余额 Map 转换为数组对象列表
        List<Map<String, Object>> groupBalanceList = new ArrayList<>();
        for (Map.Entry<String, Double> entry : groupBalances.entrySet()) {
            Map<String, Object> groupBalanceItem = new HashMap<>();
            groupBalanceItem.put("groupname", entry.getKey());
            groupBalanceItem.put("number", entry.getValue());
            groupBalanceList.add(groupBalanceItem);
        }
        String jsonString= JSON.toJSONString(groupBalanceList);
        resp.setContentType("text/json;charset=utf-8");
        resp.getWriter().write(jsonString);
        }
        public void GetBalanceOfEachMemberInGroup(HttpServletRequest request, HttpServletResponse resp) throws ServletException, IOException {
            String theAdminName = request.getParameter("TheAdminName");
            User user =userDAO.selectByname(theAdminName);
            String groupname =user.getGroupid();
            List<User> users=userDAO.selectGroupMumber(groupname);
            String jsonString= JSON.toJSONString(users);
            resp.setContentType("text/json;charset=utf-8");
            resp.getWriter().write(jsonString);

        }
        public void GetGroupPublicFund(HttpServletRequest request, HttpServletResponse resp) throws ServletException, IOException {
        String theAdminName = request.getParameter("TheAdminName");
        User user =userDAO.selectByname(theAdminName);
        String groupname =user.getGroupid();
        Group group=groupDAO.SelectGroupPublicFund(groupname);
        String jsonString= JSON.toJSONString(group);
            resp.setContentType("text/json;charset=utf-8");
            resp.getWriter().write(jsonString);
        }
        public void Allocatefunds1(HttpServletRequest request, HttpServletResponse resp) throws ServletException, IOException {
            resp.setCharacterEncoding("UTF-8");
            String theuser = request.getParameter("Theuser");
            int amount = Integer.parseInt(request.getParameter("Theamount"));
            if(amount<1){
                resp.getWriter().write("请输入大于0的数字");
                return;
            }
            int Useramount = Integer.parseInt(request.getParameter("Useramount"))+amount;
            int pubilicAmount = Integer.parseInt(request.getParameter("pubilicAmount"))-amount;
            if(pubilicAmount<0||Useramount<0){
                resp.setCharacterEncoding("UTF-8");
                resp.getWriter().write("数额过大请重新填写");
                return;
            }
            User user =userDAO.selectByname(theuser);
            String groupname =user.getGroupid();
            fundDAO.Allocatefunds(theuser,Useramount,pubilicAmount,groupname);
            resp.getWriter().write("操作完毕");
        }
    public void Allocatefunds2(HttpServletRequest request, HttpServletResponse resp) throws ServletException, IOException {
        resp.setCharacterEncoding("UTF-8");
        String theuser = request.getParameter("Theuser");
        int amount = Integer.parseInt(request.getParameter("Theamount"));
        if(amount<1){
            resp.getWriter().write("请输入大于0的数字");
            return;
        }
        int Useramount =amount-Integer.parseInt(request.getParameter("Useramount"));
        int pubilicAmount = Integer.parseInt(request.getParameter("pubilicAmount"));
        if(pubilicAmount<0||Useramount<0){
            resp.setCharacterEncoding("UTF-8");
            resp.getWriter().write("数额过大请重新填写");
            return;
        }
        User user =userDAO.selectByname(theuser);
        String groupname =user.getGroupid();
        fundDAO.Allocatefunds(theuser,Useramount,pubilicAmount,groupname);
        resp.getWriter().write("操作完毕");
    }
        public void ShowGroupFlow(HttpServletRequest request, HttpServletResponse resp) throws ServletException, IOException {
            String username = request.getParameter("username");
            User user =userDAO.selectByname(username);
            String GroupName =user.getGroupid();
             List<Funds> funds=fundDAO.SelectGroupFunds(GroupName);
            String jsonString= JSON.toJSONString(funds);
            resp.setContentType("text/json;charset=utf-8");
            resp.getWriter().write(jsonString);
        }




    
}
