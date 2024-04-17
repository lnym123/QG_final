package com.Controller.old;

import com.DAO.UserDAO;
import com.DAO.impl.UserDAOImpl;
import com.pojo.User;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.List;

@WebServlet("/login")
public class forlogintest  extends HttpServlet {
    private static final String SECRET_KEY = "MykEY"; // 替换为你的密钥
    private static final long EXPIRATION_TIME = 3600000; // JWT有效期，这里是1小时（毫秒）
    UserDAO userDao=new UserDAOImpl();
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {

        resp.setCharacterEncoding("UTF-8");
        String username = req.getParameter("username");
        String password = req.getParameter("password");
        List<User> users=userDao.selectAll(username,password);
        User matchedUser = users.stream().findFirst().orElse(null);
        // 验证用户名和密码（这里仅作简单示意，实际应使用更安全的方法）
        if (matchedUser!=null) { // 自定义的验证方法
            // 验证通过，生成JWT
                String token = Jwts.builder()
                        .setSubject(username) // 设置JWT的主题，可以存放用户名
                        .claim("avatar_url",matchedUser.getAvatar_url() )
                        .claim("username",username)// 尽管不建议直接存储明文密码，但这里仅作示例
                        .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME)) // 设置过期时间
                        .signWith(SignatureAlgorithm.HS256, SECRET_KEY.getBytes()) // 使用HS256算法签名
                        .compact();
                // 将JWT发送回客户端（例如放入响应体或Cookie中）
            resp.setContentType("application/json");
            PrintWriter out = resp.getWriter();
            out.println("{\"token\":\"" + token + "\"}");
            } else {
                // 用户名或密码错误处理...
                resp.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                resp.getWriter().write("Unauthorized");
            }
        }

    // 自定义的验证方法，真实场景中应该从数据库或其他身份验证源进行验证
    private boolean isValidUser(String username, String password) {
        // 这里只是一个模拟，实际会连接数据库进行查询
        return "valid_username".equals(username) && "valid_password".equals(password);
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        this.doPost(req, resp);
    }
}
