package com.Controller.servlet;

import com.Controller.BaseServlet;
import com.DAO.MessageDAO;
import com.DAO.UserDAO;
import com.DAO.impl.MessageDAOimpl;
import com.DAO.impl.UserDAOImpl;
import com.alibaba.fastjson.JSON;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.pojo.Message;
import com.pojo.User;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;

import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.*;

@WebServlet("/user/*")
@MultipartConfig // 启用文件上传功能
public class UserServlet  extends BaseServlet {
    private final UserDAO userDao = new UserDAOImpl();
    MessageDAO messageDAO= new MessageDAOimpl();
    private static final String UPLOAD_DIR = "src/main/webapp/images"; // 替换为实际的上传目录路径
    private static final int MAX_FILE_SIZE = 1024 * 1024 * 2; // 限制文件大小为2MB
    private static final String SECRET_KEY = "MykEY"; // 替换为你的密钥
    private static final long EXPIRATION_TIME = 3600000; // JWT有效期，这里是1小时（毫秒）

    public void FileUpload(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        boolean isMultipart = ServletFileUpload.isMultipartContent(req);
        if (!isMultipart) {
            // 处理非多部分请求或返回错误响应
            return;
        }

        DiskFileItemFactory factory = new DiskFileItemFactory();
        factory.setSizeThreshold(MAX_FILE_SIZE); // 设置临时文件阈值
        factory.setRepository(new File(System.getProperty("java.io.tmpdir")));
        ServletFileUpload uploadHandler = new ServletFileUpload(factory);
        uploadHandler.setSizeMax(MAX_FILE_SIZE); // 设置最大允许上传文件大小
        try {
            List<FileItem> items = uploadHandler.parseRequest(req);
            for (FileItem item : items) {
                if (!item.isFormField() && item.getFieldName().equals("avatar")) { // 检查是否为期望的文件字段
                    InputStream inputStream = item.getInputStream();
                    String fileName = item.getName();
                    String contentType = item.getContentType();
                    long contentLength = item.getSize();

                    // 保存文件到服务器
                    File uploadedFile = new File(UPLOAD_DIR, fileName);
                    Files.copy(inputStream, uploadedFile.toPath(), StandardCopyOption.REPLACE_EXISTING);

                    // 将文件信息（如路径或ID）保存到数据库
                    saveFileToDatabase(uploadedFile.getAbsolutePath(), contentType, contentLength, req.getParameter("userId")); // 假设从请求中获取用户ID
                    break;
                } else if (item.isFormField() && item.getFieldName().equals("username")) { // 处理非文件字段（如username）
                    String username = item.getString(); // 获取username字段的值
                    System.out.println(username);
                }
            }

            resp.getWriter().println("{ \"success\": true }");
        } catch (FileUploadException | SQLException e) {
            resp.getWriter().println("{ \"success\": false, \"message\": \"" + e.getMessage() + "\" }");
            e.printStackTrace();
        }
    }

    public void ForChangeData(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String username = req.getParameter("username");
        String password = req.getParameter("password");
        String location = req.getParameter("location");
        String nickname = req.getParameter("nickname");
        String PhoneNumber = req.getParameter("PhoneNumber");
        User user = new User(username, password, location, nickname, PhoneNumber);
        int i = userDao.updateData(user);
        ObjectMapper mapper = new ObjectMapper();
        Map<String, Object> response = new HashMap<>();
        if (i == 1) {
            response.put("success", true);
            String json = mapper.writeValueAsString(response);
            resp.setContentType("application/json");
            resp.setCharacterEncoding("UTF-8");
            resp.getWriter().write(json);
        }

    }

    public void ForLogin(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setCharacterEncoding("UTF-8");
        String username = req.getParameter("username");
        String password = req.getParameter("password");
        List<User> users = userDao.selectAll(username, password);
        User matchedUser = users.stream().findFirst().orElse(null);
        System.out.println("到达1");
        resp.setContentType("application/json;charset=utf-8");
        PrintWriter out = resp.getWriter();
        // 验证用户名和密码（这里仅作简单示意，实际应使用更安全的方法）
        if (!matchedUser.getUsername().equals("游客")) { // 自定义的验证方法
            // 验证通过，生成JWT
            System.out.println("正常账号登录");
            String token = Jwts.builder()
                    .signWith(SignatureAlgorithm.HS256, SECRET_KEY.getBytes()) // 使用HS256算法签名
                    .setSubject(username) // 设置JWT的主题，可以存放用户名
                    .claim("avatar_url", matchedUser.getAvatar_url())
                    .claim("authority", matchedUser.getAuthority())
                    .claim("username", username)
                    .claim("password", password)
                    .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME)) // 设置过期时间

                    .compact();
            // 将JWT发送回客户端（例如放入响应体或Cookie中）

            out.println("{\"token\":\"" + token + "\"}");
        } else if(Objects.equals(matchedUser.getUsername(), "游客")) {
            System.out.println("1");
            String token = Jwts.builder()
                    .signWith(SignatureAlgorithm.HS256, SECRET_KEY.getBytes())
                    .claim("username", "null")
                    .claim("password", password)
                    .claim("authority", matchedUser.getAuthority())
                    .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME)) // 设置过期时间
                    .compact();
            System.out.println(token);

            out.println("{\"token\":\"" + token + "\"}");


        }else{
            // 用户名或密码错误处理...
            resp.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            resp.getWriter().write("Unauthorized");
        }
    }

    public void ForRegister(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String username = req.getParameter("username");
        String password = req.getParameter("password");
        String nickname = req.getParameter("nickname");
        String location = req.getParameter("location");
        String PhoneNumber = req.getParameter("PhoneNumber");
        String confirmPassword = req.getParameter("confirmPassword");
        User user = new User(username, password, location, nickname, PhoneNumber);
        int i = userDao.insert(user);
        resp.setCharacterEncoding("UTF-8");
        if (i == 1) {
            resp.getWriter().write("注册成功");
        }

    }

    public void ShowImformation(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        User user = userDao.selectByname(req.getParameter("username"));
        ObjectMapper mapper = new ObjectMapper();
        Map<String, Object> response = new HashMap<>();

        if (user != null) {
            response.put("group", user.getGroupid());
            response.put("password", user.getPassword());
            response.put("nickname", user.getNickname());
            response.put("number", user.getPhoneNumber());
            response.put("address", user.getLocation());

            System.out.println(response);
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
        Message message = new Message(senter, message1, groupid,"application");
        int i = userDao.sendapplication(message);
        String jsonString= JSON.toJSONString("发送申请完毕");
        resp.setContentType("text/json;charset=utf-8");
        resp.getWriter().write(jsonString);

    }

    public void ExitfromGroup(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String username = req.getParameter("username");
        User user = userDao.selectByname(username);
        String groupname =user.getGroupid();
        int i = userDao.ExitGroup(username);
        Message message = new Message(username,"退出群组", groupname,"Exit");
        int b = userDao.sendapplication(message);
        ObjectMapper mapper = new ObjectMapper();
        Map<String, Object> response = new HashMap<>();
        if (i == 1) {
            response.put("success", true);
            String json = mapper.writeValueAsString(response);
            resp.setContentType("application/json");
            resp.setCharacterEncoding("UTF-8");
            resp.getWriter().write(json);
        }
    }

    public void  UserAcceptInvitation(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String username=req.getParameter("username");
        String groupname=req.getParameter("Thegroupname");
        String TheSenter=req.getParameter("TheSenter");
        int i=userDao.ForAgreement(username,groupname);
        messageDAO.DeleteInvitationMessage(TheSenter,username,"invitation");
        resp.setCharacterEncoding("UTF-8");
        resp.getWriter().write("已加入");

    }

    public void  selectAllUserForAdmin(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        List<User> users =userDao.selectAllUser();

        String jsonString= JSON.toJSONString(users);
        resp.setContentType("text/json;charset=utf-8");
        resp.getWriter().write(jsonString);
    }

    public void ForAdminBanUser(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String id = req.getParameter("id");
        int i=userDao.OperateBanUser(id,"true");
        resp.setCharacterEncoding("UTF-8");
        resp.getWriter().write("已封禁");

    }
    public void ForAdminUnBanUser(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String id = req.getParameter("id");
        int i=userDao.OperateBanUser(id,"false");
        resp.setCharacterEncoding("UTF-8");
        resp.getWriter().write("已解禁");

    }
    public void UserDenyInvitation(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String id = req.getParameter("username");
        String thesenter = req.getParameter("TheSenter");
        String groupid = req.getParameter("groupid");
        messageDAO.DeleteInvitationMessage(thesenter,id,"invitation");
        messageDAO.SendUserDenyMessage(thesenter,id,groupid);
        resp.setCharacterEncoding("UTF-8");
        resp.getWriter().write("已拒绝");
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
            System.out.println(filePath);
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





















