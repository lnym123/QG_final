package com.controller.servlet;
import com.controller.BaseServlet;
import com.dao.MessageDAO;
import com.dao.impl.MessageDAOimpl;
import com.alibaba.fastjson.JSON;
import com.pojo.Message;
import com.service.MessageService;
import com.service.impl.MessageServiceImpl;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

@WebServlet("/message/*")
public class MessageServlet extends BaseServlet {
    MessageService messageService = new MessageServiceImpl();
    MessageDAO messageDAO=new MessageDAOimpl();

    //获取管理员用户的消息
   public void ForAdminMessage(HttpServletRequest req, HttpServletResponse resp) throws IOException {
       String id = req.getParameter("id");
       List<Message> messages=messageService.ForMessage(id,"admin");
       String jsonString= JSON.toJSONString(messages);
       resp.setContentType("text/json;charset=utf-8");
       resp.getWriter().write(jsonString);
      }
//获取普通用户的消息
public void ForUserMessage(HttpServletRequest req, HttpServletResponse resp) throws IOException {
    String id = req.getParameter("id");
    List<Message> messages=messageService.ForMessage(id,"user");
    String jsonString= JSON.toJSONString(messages);
    resp.setContentType("text/json;charset=utf-8");
    resp.getWriter().write(jsonString);

            }
//删除信息
     public void DeleteMessage(HttpServletRequest req, HttpServletResponse resp) throws IOException {
         String senter = req.getParameter("TheSenter");
         String message = req.getParameter("TheMessage");
         messageDAO.DeleteMessage(senter,message);
         resp.setCharacterEncoding("UTF-8");
         resp.getWriter().write("删除完毕");

     }
     //管理员同意群组加入申请
     public  void Agreement(HttpServletRequest req, HttpServletResponse resp) throws IOException {
         String Thesenter = req.getParameter("TheSenter"); //zhangsan
         String id = req.getParameter("id");   //lisi
         messageService.AdminAgreement(id, Thesenter);
         resp.setCharacterEncoding("UTF-8");
         resp.getWriter().write("添加完毕");

     }
     //群组管理员邀请他人进入群组
     public void SendInvitation(HttpServletRequest req, HttpServletResponse resp) throws IOException {
         resp.setCharacterEncoding("UTF-8");
         String senter = req.getParameter("senter");
         String recipient= req.getParameter("recipient");
         String result=messageService.SendInvitation(senter,recipient);
         resp.getWriter().write(result);

     }
     //申请个人群组
    public  void ForApplyOwnGroup (HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setCharacterEncoding("UTF-8");
        String theSenter = req.getParameter("TheSenter");
        String theGroupId = req.getParameter("TheGroupId");
        if (!ValidationHelper.isValidLocation(theGroupId)) {
            resp.getWriter().write("群组名称格式错误,应为汉字");
            return;
        }
        messageDAO.ApplyOwnGroup(theSenter,theGroupId);

        resp.getWriter().write("申请已发送");
    }
    //获取网站管理员的消息
    public void ForHighAdminMessage(HttpServletRequest req, HttpServletResponse resp) throws IOException {
      List<Message> messages=messageDAO.GetHighAdminMessage();
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
             messageDAO.DeleteMessage(senter,message);
             resp.setCharacterEncoding("UTF-8");
             resp.getWriter().write("已经拒绝");
         }

 //网站管理员接受用户创立群组
   public void AgreeCreateGroupMessage(HttpServletRequest req, HttpServletResponse resp) throws IOException {
       String theSenter = req.getParameter("TheSenter");
       String thegroupid = req.getParameter("Thegroupid");
       messageService.adminHandleGroupApplication(theSenter,thegroupid,"","","accept");
       resp.setCharacterEncoding("UTF-8");
       resp.getWriter().write("已创立");
   }
    //网站管理员拒绝用户创立群组
   public void GroupApplicationCancel(HttpServletRequest req, HttpServletResponse resp) throws IOException {
       String theRecipient = req.getParameter("TheRecipient");
       String message = req.getParameter("TheMessage");
       String senter = req.getParameter("TheSenter");
       messageService.adminHandleGroupApplication(senter,"",message,theRecipient,"deny");
       resp.setCharacterEncoding("UTF-8");
       resp.getWriter().write("已经拒绝");
   }
   //用户发送解封请求
  public void sendUnBanReques(HttpServletRequest req, HttpServletResponse resp) throws IOException {
      String username = req.getParameter("username");
      messageDAO.sendUserUnBanReques(username);
      resp.setCharacterEncoding("UTF-8");
      resp.getWriter().write("已经发送");
            }

   }

