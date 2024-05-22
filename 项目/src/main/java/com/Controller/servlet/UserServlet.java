package com.controller.servlet;

import com.alibaba.fastjson.JSONObject;
import com.auth0.jwt.algorithms.Algorithm;
import com.controller.BaseServlet;
import com.dao.GroupDAO;
import com.dao.MessageDAO;
import com.dao.UserDAO;
import com.dao.impl.GroupDAOimpl;
import com.dao.impl.MessageDAOimpl;
import com.dao.impl.UserDAOImpl;
import com.alibaba.fastjson.JSON;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.pojo.Group;
import com.pojo.Message;
import com.pojo.User;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.aopalliance.intercept.Joinpoint;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;



import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.Objects;

import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import java.io.File;
import java.io.InputStream;
import java.io.PrintWriter;
import java.math.BigInteger;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.security.MessageDigest;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.*;



@WebServlet("/user/*")
@MultipartConfig // 启用文件上传功能
public class UserServlet  extends BaseServlet {
    private final UserDAO userDao = new UserDAOImpl();
    MessageDAO messageDAO = new MessageDAOimpl();
    GroupDAO groupDAO = new GroupDAOimpl();
    private static final String UPLOAD_DIR = "src/main/webapp/images";
    private static final int MAX_FILE_SIZE = 1024 * 1024 * 2; // 限制文件大小为2MB
    private static final String SECRET_KEY = "MykEY";
    private static final long EXPIRATION_TIME = 3600000; // JWT有效期，这里是1小时
    private static final Cache<String, User> userCache = Caffeine.newBuilder()
            .maximumSize(1000) // 最多缓存1000个用户
            .expireAfterWrite(10, TimeUnit.MINUTES) // 写入后10分钟过期
            .build();
    private static final Cache<String, User> userImfCache = Caffeine.newBuilder()  //用户个人信息缓存
            .maximumSize(1000)
            .expireAfterWrite(10, TimeUnit.MINUTES)
            .build();
    public void FileUpload(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        boolean isMultipart = ServletFileUpload.isMultipartContent(req);
        if (!isMultipart) {
            resp.getWriter().println("{ \"success\": false, \"message\": \"Request is not multipart.\" }");
            return;
        }

        DiskFileItemFactory factory = new DiskFileItemFactory();
        factory.setSizeThreshold(MAX_FILE_SIZE);
        factory.setRepository(new File(System.getProperty("java.io.tmpdir")));
        ServletFileUpload uploadHandler = new ServletFileUpload(factory);
        uploadHandler.setSizeMax(MAX_FILE_SIZE);

        try {
            List<FileItem> items = uploadHandler.parseRequest(req);
            String username = null;
            String userId = null; // 用于存储用户ID
            FileItem fileItem = null;

            for (FileItem item : items) {
                if (item.isFormField()) {
                    if ("username".equals(item.getFieldName())) {
                        username = item.getString();
                    } else if ("userId".equals(item.getFieldName())) { // 假设前端发送了用户ID
                        userId = item.getString();
                    }
                } else if ("avatar".equals(item.getFieldName())) {
                    fileItem = item; // 保存FileItem对象以便后续处理
                }
            }

            if (fileItem != null && username != null && userId != null) {
                InputStream inputStream = fileItem.getInputStream();
                String fileName = fileItem.getName();
                String contentType = fileItem.getContentType();
                long contentLength = fileItem.getSize();

                File uploadedFile = new File(UPLOAD_DIR, fileName);
                Files.copy(inputStream, uploadedFile.toPath(), StandardCopyOption.REPLACE_EXISTING);

                saveFileToDatabase(uploadedFile.getAbsolutePath(), contentType, contentLength, userId);
                resp.getWriter().println("{ \"success\": true }");
            } else {
                resp.getWriter().println("{ \"success\": false, \"message\": \"Missing fields or file.\" }");
            }

        } catch (FileUploadException | SQLException e) {
            resp.getWriter().println("{ \"success\": false, \"message\": \"" + e.getMessage() + "\" }");
            e.printStackTrace();
        }

    }

    public void ForChangeData(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException, NoSuchAlgorithmException {
        String username = req.getParameter("username");
        String password = req.getParameter("password");
        String codePassword=hashPasswordSHA256(password);
        String location = req.getParameter("location");
        String PhoneNumber = req.getParameter("PhoneNumber");
        resp.setCharacterEncoding("UTF-8");
        if (!ValidationHelper.isValidPhoneNumber(PhoneNumber)) {
            resp.getWriter().write("手机号格式错误");
            return;
        }
        if (!ValidationHelper.isValidLocation(location)) {
            resp.getWriter().write("地址格式错误");
            return;
        }
        User user = new User(username, codePassword, location, PhoneNumber);
        int i = userDao.updateData(user);
        if (i == 1) {
            resp.getWriter().write("修改完成");
        }

    }

    public void ForLogin(HttpServletRequest req, HttpServletResponse resp) throws IOException, NoSuchAlgorithmException {
            PrintWriter out = resp.getWriter();
            resp.setCharacterEncoding("UTF-8");
            String username = req.getParameter("username");
            String password = req.getParameter("password");
            String hashedPassword = hashPasswordSHA256(password);
            User matchedUser = userCache.getIfPresent(username);

            if (matchedUser == null) {
                // 缓存未命中，查询数据库
                List<User> users = userDao.selectAll(username, hashedPassword);
                matchedUser = users.stream().findFirst().orElse(null);
                if (matchedUser != null) {
                    userCache.put(username, matchedUser); // 缓存用户信息
                }
            }

            if (matchedUser != null && matchedUser.getLocked().equals("true")) {
                String jsonString = JSON.toJSONString("账号已被封禁");
            resp.setStatus(HttpServletResponse.SC_FORBIDDEN);
            resp.setContentType("application/json;charset=utf-8");
            resp.getWriter().write(jsonString);
            return;
            }

            if (matchedUser != null && matchedUser.getPassword().equals(hashedPassword)) {
                resp.setContentType("application/json");
                String token = Jwts.builder()
                        .signWith(SignatureAlgorithm.HS256, SECRET_KEY.getBytes()) // 使用HS256算法签名
                        .setSubject(username) // 设置JWT的主题，可以存放用户名
                        .claim("avatar_url", matchedUser.getAvatar_url())
                        .claim("authority", matchedUser.getAuthority())
                        .claim("PersonalFunds", matchedUser.getPersonalfunds())
                        .claim("username", username)
                        .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME)) // 设置过期时间
                        .compact();
                out.println("{\"token\":\"" + token + "\"}");

            } else {
                resp.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                resp.getWriter().write("Unauthorized");
            }
    }



    public void ForRegister(HttpServletRequest req, HttpServletResponse resp) throws IOException, NoSuchAlgorithmException {
        resp.setCharacterEncoding("UTF-8");
        String username = req.getParameter("username");
        String password = req.getParameter("password");
        String location = req.getParameter("location");
        String PhoneNumber = req.getParameter("PhoneNumber");
        String confirmPassword = req.getParameter("confirmPassword");
        if (!ValidationHelper.isValidPhoneNumber(PhoneNumber)) {
            resp.getWriter().write("手机号格式错误");
            return;
        }
        if (!ValidationHelper.isValidLocation(location)) {
            resp.getWriter().write("地址格式错误");
            return;
        }
        if (!ValidationHelper.isValidUsername(username)) {
            resp.getWriter().write("用户名格式错误");
            return;
        }

        String Codepassword=hashPasswordSHA256(password);
        List<User> users = userDao.selectAllUser();
        for (User user : users) {
            if (user.getUsername().equals(username)) {
                resp.getWriter().write("用户名已存在");
                return;
            }
        }
        User user = new User(username,Codepassword, location, PhoneNumber);
        int i = userDao.insert(user);

        if (i == 1) {
            resp.getWriter().write("注册成功");
        }

    }

    public void ShowImformation(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        User user;
        if(!userImfCache.asMap().isEmpty()){
            user = userImfCache.asMap().get(req.getParameter("username"));
        }else{
            user = userDao.selectByname(req.getParameter("username"));
            userImfCache.put(user.getUsername(), user);
        }

        ObjectMapper mapper = new ObjectMapper();
        Map<String, Object> response = new HashMap<>();

        if (user != null) {
            response.put("group", user.getGroupid());
            response.put("password", user.getPassword());
            response.put("number", user.getPhoneNumber());
            response.put("address", user.getLocation());

            String json = mapper.writeValueAsString(response);
            resp.setContentType("application/json");
            resp.setCharacterEncoding("UTF-8");
            resp.getWriter().write(json);

        }


    }

    public void Sendapplication(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String senter = req.getParameter("senter");
        String groupid = req.getParameter("groupid");
        String message1 = req.getParameter("sendmessage");
        User user = userDao.selectByname(senter);
        String groupname = user.getGroupid();
        if (groupname != null) {
            String jsonString = JSON.toJSONString("已处于一个群组，请先退出");
            resp.setContentType("text/json;charset=utf-8");
            resp.getWriter().write(jsonString);
            return;
        }
        List<Group> groups = groupDAO.selectAllForAdmin();
        for (Group group : groups) {
            if (Objects.equals(group.getGroupname(), groupid)) {
                Message message = new Message(senter, message1, groupid, "application");
                int i = userDao.sendapplication(message);
                String jsonString = JSON.toJSONString("发送申请完毕");
                resp.setContentType("text/json;charset=utf-8");
                resp.getWriter().write(jsonString);
                return;
            }
        }
        String jsonString = JSON.toJSONString("该群组不存在");
        resp.setContentType("text/json;charset=utf-8");
        resp.getWriter().write(jsonString);


    }

    public void ExitfromGroup(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String username = req.getParameter("username");
        User user = userDao.selectByname(username);
        String groupname = user.getGroupid();
        ObjectMapper mapper = new ObjectMapper();
        Map<String, Object> response = new HashMap<>();
        if (groupname == null) {
            response.put("success", false);
            String json = mapper.writeValueAsString(response);
            resp.setContentType("application/json");
            resp.setCharacterEncoding("UTF-8");
            resp.getWriter().write(json);
            return;
        }
        int i = userDao.ExitGroup(username);
        Message message = new Message(username, "退出群组", groupname, "Exit");
        int b = userDao.sendapplication(message);

        if (i == 1) {
            response.put("success", true);
            String json = mapper.writeValueAsString(response);
            resp.setContentType("application/json");
            resp.setCharacterEncoding("UTF-8");
            resp.getWriter().write(json);
        }
    }

    public void UserAcceptInvitation(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String username = req.getParameter("username");
        String groupname = req.getParameter("Thegroupname");
        String TheSenter = req.getParameter("TheSenter");
        int i = userDao.ForAgreement(username, groupname);
        messageDAO.DeleteInvitationMessage(TheSenter, username, "invitation");
        resp.setCharacterEncoding("UTF-8");
        resp.getWriter().write("已加入");

    }

    public void selectAllUserForAdmin(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        List<User> users = userDao.selectAllUser();
        String jsonString = JSON.toJSONString(users);
        resp.setContentType("text/json;charset=utf-8");
        resp.getWriter().write(jsonString);
    }

    public void ForAdminBanUser(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String id = req.getParameter("id");
        int i = userDao.OperateBanUser(id, "true");
        resp.setCharacterEncoding("UTF-8");
        resp.getWriter().write("已封禁");

    }

    public void ForAdminUnBanUser(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setCharacterEncoding("UTF-8");
        String id = req.getParameter("id");
        User user=userDao.selectByname(id);
        String locked=user.getLocked();
        if(locked.equals("false")){
            resp.getWriter().write("用户未被封禁");
            return;
        }

        int i = userDao.OperateBanUser(id, "false");

        resp.getWriter().write("已解禁");

    }

    public void UserDenyInvitation(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String id = req.getParameter("username");
        String thesenter = req.getParameter("TheSenter");
        String groupid = req.getParameter("groupid");
        messageDAO.DeleteInvitationMessage(thesenter, id, "invitation");
        messageDAO.SendUserDenyMessage(thesenter, id, groupid);
        resp.setCharacterEncoding("UTF-8");
        resp.getWriter().write("已拒绝");
    }

    //确认重置账号存在
    public void ResetPassword(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String username = req.getParameter("username");
        String location = req.getParameter("location");
        String phoneNumber = req.getParameter("PhoneNumber");
        User user = userDao.CheckResetPasswordAccount(username, phoneNumber, location);
        ObjectMapper mapper = new ObjectMapper();
        Map<String, Object> response = new HashMap<>();

        if (user != null) {
            response.put("success", true);
            String json = mapper.writeValueAsString(response);
            resp.setContentType("application/json");
            resp.setCharacterEncoding("UTF-8");
            resp.getWriter().write(json);
        } else {
            response.put("success", false);
            String json = mapper.writeValueAsString(response);
            resp.setContentType("application/json");
            resp.setCharacterEncoding("UTF-8");
            resp.getWriter().write(json);
        }

    }

    public void ForResetPassword(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String username = req.getParameter("object");
        String password = req.getParameter("password");
        userDao.ResetPassword(username, password);
        resp.setCharacterEncoding("UTF-8");
        resp.getWriter().write("已修改");
    }

    public void GetuserFund(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String username = req.getParameter("username");
        User user = userDao.selectByname(username);
        String jsonString = JSON.toJSONString(user);
        resp.setContentType("text/json;charset=utf-8");
        resp.getWriter().write(jsonString);

    }


    public static String hashPasswordSHA256(String plainPassword) throws NoSuchAlgorithmException {
        MessageDigest md = MessageDigest.getInstance("SHA-256");
        md.update(plainPassword.getBytes());
        byte[] digest = md.digest();

        BigInteger no = new BigInteger(1, digest);
        String hashtext = no.toString(16);

        while (hashtext.length() < 64) {
            hashtext = "0" + hashtext;
        }

        return hashtext;
    }

    private void saveFileToDatabase(String filePath, String contentType, long contentLength, String userId) throws SQLException {
        Connection conn = null;
        PreparedStatement stmt = null;
        try {
            try {
                Class.forName("com.mysql.cj.jdbc.Driver"); // 替换为实际的数据库驱动类名
            } catch (ClassNotFoundException e) {
                throw new RuntimeException(e);
            }
            conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/db1", "root", "1234"); // 替换为实际的数据库连接信息
            String sql = "UPDATE tb_user SET avatar_url = ? WHERE username = ?";
            stmt = conn.prepareStatement(sql);
            stmt.setString(1, filePath);
            stmt.setString(2, userId);
            stmt.executeUpdate();
        } finally {
            if (stmt != null) {
                stmt.close();
            }
            if (conn != null) {
                conn.close();
            }
        }
    }
}






















