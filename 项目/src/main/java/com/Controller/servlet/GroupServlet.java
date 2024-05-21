package com.controller.servlet;

import com.controller.BaseServlet;
import com.dao.GroupDAO;
import com.dao.UserDAO;
import com.dao.impl.GroupDAOimpl;
import com.dao.impl.UserDAOImpl;
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
    private final  GroupDAO groupDAO =new GroupDAOimpl();
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
    class ErrorResponse {
        private String errorMessage;

        public ErrorResponse(String errorMessage) {
            this.errorMessage = errorMessage;
        }

        // Getter for errorMessage
        public String getErrorMessage() {
            return errorMessage;
        }
    }
    public void ForAdminChangeGroup (HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String id = req.getParameter("id");
        resp.setCharacterEncoding("UTF-8");

        String number = req.getParameter("number");
        int TrueNum = Integer.parseInt(number);
        String scale = req.getParameter("scale");
        String direction = req.getParameter("direction");
        if(TrueNum<1){
            resp.getWriter().write("人数错误");
            return;}
            else if (!ValidationHelper.isValidLocation(scale)) {
                resp.getWriter().write("规模格式错误");
                return;
            } else if (!ValidationHelper.isValidLocation(direction)) {
            resp.getWriter().write("企业方向格式错误");
            return;}

        String visiable=req.getParameter("visiable");
        User user = userDao.selectByname(id);
        String groupname =user.getGroupid();
        groupDAO.ChangeGroupData(groupname,number,scale,direction,visiable);

        resp.getWriter().write("修改完毕");
    }
    public void selectAllGroupForAdmin(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        List<Group> groups=groupDAO.selectAllForAdmin();
        String jsonString= JSON.toJSONString(groups);
        resp.setContentType("text/json;charset=utf-8");
        resp.getWriter().write(jsonString);
    }

    public void ForAdminBanGroup(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String id = req.getParameter("id");
        int i=groupDAO.OperateBanGroup(id,"true");
        resp.setCharacterEncoding("UTF-8");
        resp.getWriter().write("已封禁");
    }

    public void ForAdminUnBanGroup(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String id = req.getParameter("id");
        int i=groupDAO.OperateBanGroup(id,"false");
        resp.setCharacterEncoding("UTF-8");
        resp.getWriter().write("已解封");
    }
    public void LogOutGroup(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String theAdmin = req.getParameter("TheAdmin");
        int PublicFunds = Integer.parseInt(req.getParameter("PublicFunds"));
        User user = userDao.selectByname(theAdmin);
        String groupname =user.getGroupid();
        userDao.ForLogOutGroup(groupname,theAdmin,PublicFunds);
        resp.setCharacterEncoding("UTF-8");
        resp.getWriter().write("已注销");
    }
}
