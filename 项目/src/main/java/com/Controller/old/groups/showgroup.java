package com.Controller.old.groups;

import com.DAO.GroupDAO;
import com.DAO.impl.GroupDAOimpl;
import com.alibaba.fastjson.JSON;
import com.pojo.Group;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

@WebServlet("/showG")
public class showgroup extends HttpServlet {
           GroupDAO groupDAO =new GroupDAOimpl();
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        List<Group> groups=groupDAO.selectAll();
        String jsonString= JSON.toJSONString(groups);
        resp.setContentType("text/json;charset=utf-8");
        resp.getWriter().write(jsonString);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        this.doGet(req, resp);
    }
}
