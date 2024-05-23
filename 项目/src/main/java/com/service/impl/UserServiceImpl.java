package com.service.impl;
import com.dao.GroupDAO;
import com.dao.MessageDAO;
import com.dao.UserDAO;
import com.dao.impl.GroupDAOimpl;
import com.dao.impl.MessageDAOimpl;
import com.dao.impl.UserDAOImpl;
import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.pojo.Group;
import com.pojo.Message;
import com.pojo.User;
import com.service.GroupService;
import com.service.UserService;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

public class UserServiceImpl implements UserService {
    GroupService groupService=new GroupServiceImpl();
    private final UserDAO userDao = new UserDAOImpl();
    MessageDAO messageDAO = new MessageDAOimpl();
    GroupDAO groupDAO = new GroupDAOimpl();
    private static final String UPLOAD_DIR = "src/main/webapp/images";
    private static final int MAX_FILE_SIZE = 1024 * 1024 * 2; // 限制文件大小为2MB
    private static final String SECRET_KEY = "MykEY";
    private static final long EXPIRATION_TIME = 3600000; // JWT有效期，这里是1小时
    private static final Cache<String, User> userCache = Caffeine.newBuilder()  //储存已经登录的用户
            .maximumSize(1000) // 最多缓存1000个用户
            .expireAfterWrite(10, TimeUnit.MINUTES) // 写入后10分钟过期
            .build();
    private static final Cache<String, User> userImfCache = Caffeine.newBuilder()  //用户个人信息缓存
            .maximumSize(1000)
            .expireAfterWrite(10, TimeUnit.MINUTES)
            .build();
    private static final Cache<String, User> AlluserCache = Caffeine.newBuilder()  //储存所有用户
            .maximumSize(1000) // 最多缓存1000个用户
            .expireAfterWrite(10, TimeUnit.MINUTES)
            .build();


    @Override
    public void ChangePersonData(String username, String codePassword, String location, String PhoneNumber) {
        User user = new User(username, codePassword, location, PhoneNumber);
        userDao.updateData(user);
    }
  //用户登录时获取数据库相应的用户
    @Override
    public User Forlogin(String username, String codePassword) {
        List<User> users = userDao.selectAll(username, codePassword);
        return  users.stream().findFirst().orElse(null);
    }
    //用户注册，缓存用以保存所有的已注册用户名单。
    @Override
    public String ForRegister(User Theuser) {  //获取所有的用户
        List<User> users;
        if (!AlluserCache.asMap().isEmpty()) {
            // 缓存不为空，从缓存中获取数据
            users= new ArrayList<>(AlluserCache.asMap().values());
        } else {
            // 缓存为空，查询数据库
            users = userDao.selectAllUser();
            for (User user : users) {
                AlluserCache.put(user.getUsername(), user); // 缓存查询结果
            }
        }
        for (User user : users) {
            if (user.getUsername().equals(Theuser.getUsername())) {
                return "用户名已存在";
            }
        }
        userDao.insert(Theuser);
        return "注册成功";
    }

    @Override
    public User ShowImformation(String username) {
        return userDao.selectByname(username);
    }
    //处理发送企业群组加入请求
    @Override
    public String SendApplication(String senter, String groupid, String message1) {
        User user = userDao.selectByname(senter);
        String groupname = user.getGroupid();
        if (groupname != null) {
            return "已处于一个群组，请先退出";
        }
        List<Group> groups = groupService.GetAllGroups("all");//获得数据库已存在的群组
        for (Group group : groups) {
            if (Objects.equals(group.getGroupname(), groupid)) {
                Message message = new Message(senter, message1, groupid, "application");
                userDao.sendapplication(message);    //发送请求信息
                return "发送申请完毕";
            }
        }
        return "群组不存在";
    }
    /*
    * 用户退出群组
    * 返回是否成功
    * */
    @Override
    public String ExitGroup(String username) {
        User user = userDao.selectByname(username);
        String groupname = user.getGroupid();
        if (groupname == null) {
            return "false";     //用户不在群组当中
        }
        userDao.ExitGroup(username);
        Message message = new Message(username, "退出群组", groupname, "Exit");
        userDao.sendapplication(message);
        return "true";
    }
    //处理用户是否接受企业群组的邀请
    @Override
    public void UserHandleInvitation(String type, String username,String groupname,String TheSenter) {
        if(type.equals("yes")){
            userDao.ForAgreement(username, groupname);
            messageDAO.DeleteInvitationMessage(TheSenter, username, "invitation");
        }else{
            messageDAO.DeleteInvitationMessage(TheSenter, username, "invitation");
            messageDAO.SendUserDenyMessage(TheSenter, username, groupname);
        }
    }
    //为企业管理员获得所有用户信息
    public List<User> getAllUsers(){
        List<User> users;
        if (!AlluserCache.asMap().isEmpty()) {
            // 缓存不为空，从缓存中获取数据
            users= new ArrayList<>(AlluserCache.asMap().values());
        } else {
            // 缓存为空，查询数据库
            users = userDao.selectAllUser();
            for (User user : users) {
                AlluserCache.put(user.getUsername(), user); // 缓存查询结果
            }
        }
        return users;
    }
    //企业管理员封禁或解封用户
    @Override
    public String AdminHandleUser(String id,String type) {
        if(type.equals("ban")){
            userDao.OperateBanUser(id, "true");
        }else{
            User user=userDao.selectByname(id);
            String locked=user.getLocked();
            if(locked.equals("false")){
                return "用户未被封禁";
            }
            userDao.OperateBanUser(id, "false");
        }
        return "done";
    }
   //检查需要重置密码的账号是否存在
    @Override
    public String ConfirmResetAccount( String username,String location, String PhoneNumber,String type) {
        if (type.equals("confirm")) {
            User user = userDao.CheckResetPasswordAccount(username, PhoneNumber, location);
            if (user != null) {
                return "true";
            } else {
                return "false";
            }
        }else {
            userDao.ResetPassword(username,location);
            return "true";
        }
        }
    }





