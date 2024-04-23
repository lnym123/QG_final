package com.Controller.servlet;

import com.Controller.BaseServlet;
import com.DAO.FundDAO;
import com.DAO.impl.FundDAOimpl;
import com.alibaba.fastjson.JSON;
import com.pojo.Funds;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

@WebServlet("/funds/*")
public class FundsServlet extends BaseServlet {
 FundDAO fundDAO=new FundDAOimpl();
    public void ShowPersonalFlow(HttpServletRequest request, HttpServletResponse resp) throws ServletException, IOException {
        String username = "zhangsan";
        List<Funds> funds=fundDAO.SelectPersonalFunds(username);
        String jsonString= JSON.toJSONString(funds);
        resp.setContentType("text/json;charset=utf-8");
        resp.getWriter().write(jsonString);

    }
}
