package com.Controller.old.groups;

import com.DAO.GroupDAO;
import com.DAO.impl.GroupDAOimpl;
import com.alibaba.druid.util.StringUtils;
import com.alibaba.fastjson.JSON;
import com.pojo.Group;
import com.util.JDBCUtilV2;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;
@WebServlet("/GslBc")
public class GroupQueryService extends HttpServlet {
    GroupDAO groupDAO =new GroupDAOimpl();
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String groupName = req.getParameter("groupName");
        String direction = req.getParameter("direction");
        System.out.println(groupName+direction);

        List<Group> groups = null;
        try {
            groups = groupDAO.selectBycondition(groupName,direction);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        String jsonString= JSON.toJSONString(groups);
            resp.setContentType("text/json;charset=utf-8");
            resp.getWriter().write(jsonString);

    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        this.doPost(req, resp);
    }
}




