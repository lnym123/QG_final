package com.Controller.servlet;

import com.Controller.BaseServlet;
import com.DAO.GroupDAO;
import com.DAO.MessageDAO;
import com.DAO.UserDAO;
import com.DAO.impl.GroupDAOimpl;
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
    UserDAO userDao = new UserDAOImpl();
    GroupDAO groupDAO=new GroupDAOimpl();

    //获取管理员用户的消息
   public void ForAdminMessage(HttpServletRequest req, HttpServletResponse resp) throws IOException {
       String id = req.getParameter("id");
       User user = userDao.selectByname(id);
       String groupname =user.getGroupid();
       List<Message> messages=messageDAO.AdminMessages(groupname);
       String jsonString= JSON.toJSONString(messages);
       resp.setContentType("text/json;charset=utf-8");
       resp.getWriter().write(jsonString);

      }
//获取普通用户的消息
public void ForUserMessage(HttpServletRequest req, HttpServletResponse resp) throws IOException {
    String id = req.getParameter("id");
    User user = userDao.selectByname(id);
    String groupname =user.getGroupid();
    List<Message> messages=messageDAO.UserMessages(groupname,id);
    String jsonString= JSON.toJSONString(messages);
    resp.setContentType("text/json;charset=utf-8");
    resp.getWriter().write(jsonString);

            }
//删除信息
     public void DeleteMessage(HttpServletRequest req, HttpServletResponse resp) throws IOException {
         String senter = req.getParameter("TheSenter");
         String message = req.getParameter("TheMessage");
         System.out.println("senter:"+senter+" message:"+message);
         int i=messageDAO.DeleteMessage(senter,message);
         resp.setCharacterEncoding("UTF-8");
         resp.getWriter().write("删除完毕");

     }
     //管理员同意群组加入申请
     public  void Agreement(HttpServletRequest req, HttpServletResponse resp) throws IOException {
         String Thesenter = req.getParameter("TheSenter"); //zhangsan
         String id = req.getParameter("id");   //lisi
         User user=userDao.selectByname(id);
         String groupname=user.getGroupid();
         int b=userDao.ForAgreement(Thesenter,groupname);
         int i= messageDAO.ForAgreement(Thesenter);
         int c= messageDAO.SendAgreementReply(id,Thesenter,groupname);
         resp.setCharacterEncoding("UTF-8");
         resp.getWriter().write("添加完毕");

     }
//发送加入群组申请
     public void SendInvitation(HttpServletRequest req, HttpServletResponse resp) throws IOException {

         String senter = req.getParameter("senter");
         String recipient= req.getParameter("recipient");
         User user=userDao.selectByname(senter);
         String groupname=user.getGroupid();
         int i= messageDAO.SendInvitation(senter,recipient,groupname);
         resp.setCharacterEncoding("UTF-8");
         resp.getWriter().write("成功发送邀请");

     }
     //申请个人群组
    public  void ForApplyOwnGroup (HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String theSenter = req.getParameter("TheSenter");
        String theGroupId = req.getParameter("TheGroupId");
        int i= messageDAO.ApplyOwnGroup(theSenter,theGroupId);
        resp.setCharacterEncoding("UTF-8");
        resp.getWriter().write("申请已发送");
    }
    //获取网站管理员的消息
    public void ForHighAdminMessage(HttpServletRequest req, HttpServletResponse resp) throws IOException {
      List<Message> messages=messageDAO.GetHighAdminMessage();
        for (Message message : messages) {
            System.out.println(message.toString());
        }
        String jsonString= JSON.toJSONString(messages);
        resp.setContentType("text/json;charset=utf-8");
        resp.getWriter().write(jsonString);
    }
    //
    //拒接用户解禁
         public void handleBanCancel(HttpServletRequest req, HttpServletResponse resp) throws IOException {
             String theRecipient = req.getParameter("TheRecipient");
             String message = req.getParameter("TheMessage");
             String senter = req.getParameter("TheSenter");
             messageDAO.SendDenyMessage(theRecipient,"拒绝了你的解禁申请");
             messageDAO.DeleteMessage(senter,message);
             resp.setCharacterEncoding("UTF-8");
             resp.getWriter().write("已经拒绝");
         }


   public void AgreeCreateGroupMessage(HttpServletRequest req, HttpServletResponse resp) throws IOException {
       String theSenter = req.getParameter("TheSenter");
       String thegroupid = req.getParameter("Thegroupid");
       System.out.println("thegroupid:"+thegroupid+"theSenter:"+theSenter);
       userDao.AgreeCreateGroupMessage(theSenter,thegroupid);
       groupDAO.AgreeCreateGroup(thegroupid);
       resp.setCharacterEncoding("UTF-8");
       resp.getWriter().write("已创立");
   }
   public void GroupApplicationCancel(HttpServletRequest req, HttpServletResponse resp) throws IOException {
       String theRecipient = req.getParameter("TheRecipient");
       String message = req.getParameter("TheMessage");
       String senter = req.getParameter("TheSenter");
       messageDAO.SendDenyMessage(theRecipient,"拒绝了你的企业申请");
       messageDAO.DeleteMessage(senter,message);
       resp.setCharacterEncoding("UTF-8");
       resp.getWriter().write("已经拒绝");
   }


   }

