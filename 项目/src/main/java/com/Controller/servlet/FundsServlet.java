package com.controller.servlet;

import com.controller.BaseServlet;
import com.alibaba.fastjson.JSON;
import com.pojo.Funds;
import com.pojo.Group;
import com.pojo.User;
import com.service.FundService;
import com.service.impl.FundServiceImpl;
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
    FundService fundService=new FundServiceImpl();
    public void ShowPersonalFlow(HttpServletRequest request, HttpServletResponse resp) throws ServletException, IOException {
        String username =request.getParameter("username");
        List<Funds> funds=fundService.ShowPersonalFlow(username);
        String jsonString= JSON.toJSONString(funds);
        resp.setContentType("text/json;charset=utf-8");
        resp.getWriter().write(jsonString);

    }
    //个人流水查询
    public void fundsQueryService(HttpServletRequest request,HttpServletResponse resp) throws ServletException, IOException {
        String name = request.getParameter("name");
        String content = request.getParameter("content");
        String username = request.getParameter("username");
        List<Funds> funds= fundService.fundsQueryService(name,content,username);
        String jsonString= JSON.toJSONString(funds);
        resp.setContentType("text/json;charset=utf-8");
        resp.getWriter().write(jsonString);
    }
    //得到所有群组的余额
    public void GetGroupBalance(HttpServletRequest request, HttpServletResponse resp) throws ServletException, IOException {
        Map<String, Double> groupBalances =fundService.GetGroupBalance(); //得到
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
        //得到企业内所有用户的余额
     public void GetBalanceOfEachMemberInGroup(HttpServletRequest request, HttpServletResponse resp) throws ServletException, IOException {
            String theAdminName = request.getParameter("TheAdminName");
            List<User> users=fundService.GetBalanceOfEachMemberInGroup(theAdminName);
            String jsonString= JSON.toJSONString(users);
            resp.setContentType("text/json;charset=utf-8");
            resp.getWriter().write(jsonString);

        }
        //得到企业公共资金
        public void GetGroupPublicFund(HttpServletRequest request, HttpServletResponse resp) throws ServletException, IOException {
        String theAdminName = request.getParameter("TheAdminName");
        Group group=fundService.GetGroupPublicFund(theAdminName);
        String jsonString= JSON.toJSONString(group);
            resp.setContentType("text/json;charset=utf-8");
            resp.getWriter().write(jsonString);
        }
        //管理员分配资金到各个企业内群组
     public void Allocatefunds1(HttpServletRequest request, HttpServletResponse resp) throws ServletException, IOException {
            resp.setCharacterEncoding("UTF-8");
            String theuser = request.getParameter("Theuser");
            int amount = Integer.parseInt(request.getParameter("Theamount"));
            int Useramount = Integer.parseInt(request.getParameter("Useramount"))+amount;
            int pubilicAmount = Integer.parseInt(request.getParameter("pubilicAmount"))-amount;
            if(amount<1){
                resp.getWriter().write("请输入大于0的数字");
                return;
            }
            if(pubilicAmount<0||Useramount<0){
                resp.getWriter().write("数额过大请重新填写");
                return;
            }
            String result=fundService.Allocatefunds(theuser, Useramount,pubilicAmount,"allocate");
            resp.getWriter().write(result);
        }
    public void Allocatefunds2(HttpServletRequest request, HttpServletResponse resp) throws ServletException, IOException {
        resp.setCharacterEncoding("UTF-8");
        String theuser = request.getParameter("Theuser");
        int amount = Integer.parseInt(request.getParameter("Theamount"));
        int Useramount =Integer.parseInt(request.getParameter("Useramount"))-amount;
        int pubilicAmount = amount+Integer.parseInt(request.getParameter("pubilicAmount"));
        if(amount<1){
            resp.getWriter().write("请输入大于0的数字");
            return;
        }
        if(pubilicAmount<0||Useramount<0){
            resp.getWriter().write("数额过大请重新填写");
            return;
        }
        String result=fundService.Allocatefunds(theuser, Useramount,pubilicAmount,"");
        resp.getWriter().write(result);
    }

        //展示群组流水
        public void ShowGroupFlow(HttpServletRequest request, HttpServletResponse resp) throws ServletException, IOException {
            String username = request.getParameter("username");
            List<Funds> funds=fundService.ShowGroupFlow(username);
            String jsonString= JSON.toJSONString(funds);
            resp.setContentType("text/json;charset=utf-8");
            resp.getWriter().write(jsonString);
        }



}
