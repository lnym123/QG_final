package com.Controller.servlet;

import com.Controller.BaseServlet;
import com.DAO.GroupDAO;
import com.DAO.UserDAO;
import com.DAO.impl.GroupDAOimpl;
import com.DAO.impl.UserDAOImpl;
import com.alibaba.fastjson.JSON;
import com.pojo.Group;
import com.pojo.User;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

@WebServlet("/group/*")
public class GroupServlet extends BaseServlet {
    GroupDAO groupDAO =new GroupDAOimpl();
    private final UserDAO userDao = new UserDAOImpl();

    public void GroupQueryService(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String groupName = req.getParameter("groupName");
        String direction = req.getParameter("direction");

        List<Group> groups;
        try {
            groups = groupDAO.selectBycondition(groupName,direction);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        String jsonString= JSON.toJSONString(groups);
        resp.setContentType("text/json;charset=utf-8");
        resp.getWriter().write(jsonString);
    }

    public void  ShowGroups(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        List<Group> groups=groupDAO.selectAll();
        String jsonString= JSON.toJSONString(groups);
        resp.setContentType("text/json;charset=utf-8");
        resp.getWriter().write(jsonString);

    }

    public void ShowPersonalGroup(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String username = req.getParameter("username");
        resp.setContentType("text/json;charset=utf-8");
        User user = userDao.selectByname(username);
        String groupname =user.getGroupid();
        Group group=groupDAO.selectById(groupname);
        String jsonString= JSON.toJSONString(group);
        resp.getWriter().write(jsonString);



    }

}
