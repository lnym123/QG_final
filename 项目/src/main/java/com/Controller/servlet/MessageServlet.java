package com.Controller.servlet;

import com.Controller.BaseServlet;
import com.DAO.MessageDAO;
import com.DAO.UserDAO;
import com.DAO.impl.MessageDAOimpl;
import com.DAO.impl.UserDAOImpl;
import com.alibaba.fastjson.JSON;
import com.pojo.Message;
import com.pojo.User;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

@WebServlet("/message/*")
public class MessageServlet extends BaseServlet {
   MessageDAO messageDAO=new MessageDAOimpl();
    private final UserDAO userDao = new UserDAOImpl();
   public void ForAdminMessage(HttpServletRequest req, HttpServletResponse resp) throws IOException {
       String id = req.getParameter("id");
       User user = userDao.selectByname(id);
       String groupname =user.getGroupid();
       List<Message> messages=messageDAO.AdminMessages(groupname);
       System.out.println(messages);
       String jsonString= JSON.toJSONString(messages);
       resp.setContentType("text/json;charset=utf-8");
       resp.getWriter().write(jsonString);

      }

public void ForUserMessage(HttpServletRequest req, HttpServletResponse resp) throws IOException {
    String id = req.getParameter("id");
    User user = userDao.selectByname(id);
    String groupname =user.getGroupid();
    List<Message> messages=messageDAO.UserMessages(groupname,id);
    System.out.println(messages);
    String jsonString= JSON.toJSONString(messages);
    resp.setContentType("text/json;charset=utf-8");
    resp.getWriter().write(jsonString);

            }

     public void DeleteMessage(HttpServletRequest req, HttpServletResponse resp) throws IOException {
         String senter = req.getParameter("TheSenter");
         String message = req.getParameter("TheMessage");
         int i=messageDAO.DeleteMessage(senter,message);
         System.out.println("影响："+i);
         resp.setCharacterEncoding("UTF-8");
         resp.getWriter().write("删除完毕");



     }



   }

