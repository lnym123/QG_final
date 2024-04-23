package com.Controller.servlet;

import com.Controller.BaseServlet;
import com.DAO.FundDAO;
import com.DAO.UserDAO;
import com.DAO.impl.FundDAOimpl;
import com.DAO.impl.UserDAOImpl;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.pojo.Funds;
import com.pojo.User;

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
 FundDAO fundDAO=new FundDAOimpl();
 UserDAO userDAO=new UserDAOImpl();
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
        System.out.println("username:"+username);

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



}
