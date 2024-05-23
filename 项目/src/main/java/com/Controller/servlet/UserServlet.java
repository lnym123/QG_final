package com.controller.servlet;
import com.controller.BaseServlet;
import com.alibaba.fastjson.JSON;
import com.dao.UserDAO;
import com.dao.impl.UserDAOImpl;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.pojo.User;
import com.service.UserService;
import com.service.impl.UserServiceImpl;
import com.util.CheckCodeUtil;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpSession;
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
    private static final Logger logger = LoggerFactory.getLogger(UserServlet.class);
    UserService userService = new UserServiceImpl();
    private static final String UPLOAD_DIR = "src/main/webapp/images";
    private static final int MAX_FILE_SIZE = 1024 * 1024 * 2; // 限制文件大小为2MB
    private static final String SECRET_KEY = "MykEY";
    private static final long EXPIRATION_TIME = 3600000; // JWT有效期，这里是1小时
    UserDAO userDAO = new UserDAOImpl();
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
   //用户修改个人信息
    public void ForChangeData(HttpServletRequest req, HttpServletResponse resp) throws IOException, NoSuchAlgorithmException {
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
        userService.ChangePersonData(username, codePassword, location, PhoneNumber);
        resp.getWriter().write("修改完成");

    }
   //登录功能
    public void ForLogin(HttpServletRequest req, HttpServletResponse resp) throws IOException, NoSuchAlgorithmException {
            PrintWriter out = resp.getWriter();
            resp.setCharacterEncoding("UTF-8");
            String username = req.getParameter("username");
            String password = req.getParameter("password");
            String checkCode = req.getParameter("checkCode");
            HttpSession session = req.getSession();
            String checkCode1 = (String) session.getAttribute("checkCode");
        if(!checkCode1.equalsIgnoreCase(checkCode)){          //校验验证码
            resp.setStatus(HttpServletResponse.SC_METHOD_NOT_ALLOWED);
            return;
        }
        String hashedPassword = hashPasswordSHA256(password);
        User matchedUser = userService.Forlogin(username, hashedPassword);//service层中得到匹配用户

        if (matchedUser != null && matchedUser.getLocked().equals("true")) {
                    resp.setStatus(HttpServletResponse.SC_FORBIDDEN);
                    return;
            }
       logger.info("当前用户"+matchedUser);
            if (matchedUser != null && matchedUser.getPassword().equals(hashedPassword)) {
                resp.setContentType("application/json");
                String token = Jwts.builder()
                        .signWith(SignatureAlgorithm.HS256, SECRET_KEY.getBytes()) // 使用HS256算法签名
                        .setSubject(username)
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

    //注册用户
    public void ForRegister(HttpServletRequest req, HttpServletResponse resp) throws IOException, NoSuchAlgorithmException {
        resp.setCharacterEncoding("UTF-8");
        String username = req.getParameter("username");
        String password = req.getParameter("password");
        String location = req.getParameter("location");
        String PhoneNumber = req.getParameter("PhoneNumber");
        String confirmPassword = req.getParameter("confirmPassword");
        String checkCode = req.getParameter("checkCode");
        HttpSession session = req.getSession();
        String checkCode1 = (String) session.getAttribute("checkCode");
        System.out.println(checkCode);
        System.out.println(checkCode1);

        if(!checkCode1.equalsIgnoreCase(checkCode)){
            resp.getWriter().write("验证码错误");
            return;
        }
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
        if(!confirmPassword.equals(password)){
            resp.getWriter().write("请正确重复输入密码");
            return;
        }

        String Codepassword=hashPasswordSHA256(password);
        User user = new User(username, Codepassword, location, PhoneNumber);
        String string = userService.ForRegister(user);  //返回得到的注册成功与否情况
        resp.getWriter().write(string);

    }
   //展示用户个人信息
    public void ShowImformation(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        User user;
        String username = req.getParameter("username");
        user=userService.ShowImformation(username);
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
    //发送加入企业的请求
    public void Sendapplication(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String senter = req.getParameter("senter");
        String groupid = req.getParameter("groupid");
        String message1 = req.getParameter("sendmessage");
        resp.setContentType("text/json;charset=utf-8");
        String result=userService.SendApplication(senter,groupid,message1);  //获取发送请求的情况以及结果
        String jsonString = JSON.toJSONString(result);
        resp.getWriter().write(jsonString);


    }
    //用户退出群组
    public void ExitfromGroup(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");
        String username = req.getParameter("username");
        ObjectMapper mapper = new ObjectMapper();      //用以设置相应内容
        Map<String, Object> response = new HashMap<>();
        String result=userService.ExitGroup(username);
        if(result.equals("true")){
            response.put("success", true);
            String json = mapper.writeValueAsString(response);
            resp.getWriter().write(json);
        }else{
            response.put("success", false);
            String json = mapper.writeValueAsString(response);
            resp.getWriter().write(json);
        }



    }
    //用户接受企业管理员邀请加入群组
    public void UserAcceptInvitation(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String username = req.getParameter("username");
        String groupname = req.getParameter("Thegroupname");
        String TheSenter = req.getParameter("TheSenter");
        userService.UserHandleInvitation("yes",username,groupname,TheSenter);
        resp.setCharacterEncoding("UTF-8");
        resp.getWriter().write("已加入");

    }
    //用户拒绝企业管理员邀请加入群组
    public void UserDenyInvitation(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String id = req.getParameter("username");
        String thesenter = req.getParameter("TheSenter");
        String groupid = req.getParameter("groupid");
        userService.UserHandleInvitation("yes",id,groupid,thesenter);
        resp.setCharacterEncoding("UTF-8");
        resp.getWriter().write("已拒绝");
    }
    //为企业管理员获得所有用户
    public void selectAllUserForAdmin(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        List<User> users = userService.getAllUsers();
        String jsonString = JSON.toJSONString(users);
        resp.setContentType("text/json;charset=utf-8");
        resp.getWriter().write(jsonString);
    }
    //企业管理员封禁或解禁用户
    public void ForAdminBanUser(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setCharacterEncoding("UTF-8");
        String id = req.getParameter("id");
        String result=userService.AdminHandleUser(id,"ban");
        resp.getWriter().write("已封禁");

    }
    public void ForAdminUnBanUser(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setCharacterEncoding("UTF-8");
        String id = req.getParameter("id");
        String result=userService.AdminHandleUser(id,"unban");
        if(result.equals("done")){
            resp.getWriter().write("已解禁");
        }else{
            resp.getWriter().write(result);
        }
    }

    //确认重置账号存在
    public void ResetPassword(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");
        String username = req.getParameter("username");
        String location = req.getParameter("location");
        String phoneNumber = req.getParameter("PhoneNumber");
        ObjectMapper mapper = new ObjectMapper();
        Map<String, Object> response = new HashMap<>();
        String result=userService.ConfirmResetAccount(username,location,phoneNumber,"confirm");  //获取是否存在此账号的结果
        if (result.equals("true")) {
            response.put("success", true);
            String json = mapper.writeValueAsString(response);
            resp.getWriter().write(json);
        } else {
            response.put("success", false);
            String json = mapper.writeValueAsString(response);
            resp.getWriter().write(json);
        }
    }

    public void ForResetPassword(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String username = req.getParameter("object");
        String password = req.getParameter("password");
        String result=userService.ConfirmResetAccount(username,password," ","set");
        resp.setCharacterEncoding("UTF-8");
        resp.getWriter().write("已修改");
    }
    //获得个人资金数额
    public void GetuserFund(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String username = req.getParameter("username");
        User user = userDAO.selectByname(username);
        String jsonString = JSON.toJSONString(user);
        resp.setContentType("text/json;charset=utf-8");
        resp.getWriter().write(jsonString);

    }
    //获取验证码
    public void  CheckCode(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        //生成验证码
        HttpSession session = req.getSession(false); // 使用false避免在没有会话时创建新会话
        if (session == null) {
            // 如果确实需要在此处创建会话，确保之前没有进行过响应提交的操作
            session = req.getSession(true);
        }
        ServletOutputStream os = resp.getOutputStream();
        String string = CheckCodeUtil.outputVerifyImage(100, 50, os, 4);
         session = req.getSession();
        session.setAttribute("checkCode",string);
        resp.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
        resp.setHeader("Pragma", "no-cache");
        resp.setDateHeader("Expires", 0);
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






















