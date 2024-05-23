package com.service.impl;

import com.dao.GroupDAO;
import com.dao.MessageDAO;
import com.dao.UserDAO;
import com.dao.impl.GroupDAOimpl;
import com.dao.impl.MessageDAOimpl;
import com.dao.impl.UserDAOImpl;
import com.pojo.Message;
import com.pojo.User;
import com.service.MessageService;
import com.service.UserService;

import java.util.List;

public class MessageServiceImpl implements MessageService {
    UserService userService = new UserServiceImpl();
    MessageDAO messageDAO=new MessageDAOimpl();
    UserDAO userDao = new UserDAOImpl();
    GroupDAO groupDAO=new GroupDAOimpl();

   //获取消息
    @Override
    public List<Message> ForMessage(String id,String type) {

        User user = userDao.selectByname(id);
        String groupname =user.getGroupid();
        if(type.equals("admin")) {
            return messageDAO.AdminMessages(groupname);
        }else{
            return messageDAO.UserMessages(groupname,id);
        }
    }
    //管理员同意群组加入申请
    @Override
    public void AdminAgreement(String id,String Thesenter) {
        User user=userDao.selectByname(id);
        String groupname=user.getGroupid();
        userDao.ForAgreement(Thesenter,groupname);
        messageDAO.ForAgreement(Thesenter);
        messageDAO.SendAgreementReply(id,Thesenter,groupname);
    }
    //群组管理员邀请他人进入群组
    @Override
    public String SendInvitation(String senter, String recipient) {
        User user1=userDao.selectByname(senter);
        String groupname1=user1.getGroupid();
        List<User> users=userService.getAllUsers();
        for (User user : users) {
            if(user.getUsername().equals(recipient)){
                User user2=userDao.selectByname(recipient);
                String groupname2=user2.getGroupid();
                if(groupname2!=null){
                    return "对方已有群组";
                }
                messageDAO.SendInvitation(senter,recipient,groupname1);
                return "成功发送邀请";
            }
        }
        return "对象不存在";
    }
    //网站管理员处理用户申请创立群组
    @Override
    public void adminHandleGroupApplication(String theSenter, String thegroupid, String message, String theRecipient,String type) {
        if(type.equals("accept")) {
            userDao.AgreeCreateGroupMessage(theSenter,thegroupid);
            groupDAO.AgreeCreateGroup(thegroupid);
        }else{
            messageDAO.SendDenyMessage(theRecipient,"拒绝了你的企业申请");
            messageDAO.DeleteMessage(theSenter,message);
        }
    }


}
