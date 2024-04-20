package com.Controller.old.users;

import com.DAO.UserDAO;
import com.DAO.impl.UserDAOImpl;
import com.pojo.Message;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
@WebServlet("/sa")
public class Sendapplication extends HttpServlet {
    UserDAO userDao=new UserDAOImpl();
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String senter=req.getParameter("senter");
        String groupid = req.getParameter("groupid");
        String message1=req.getParameter("sendmessage");

        Message message=new Message(senter,message1,groupid);

        System.out.println(senter+message1+groupid);

        System.out.println(message.getSenter()+message.getMessage()+message.getGroupid());

        int i=userDao.sendapplication(message);
        resp.getWriter().write("登录成功");

    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        this.doGet(req,resp);
    }
}
