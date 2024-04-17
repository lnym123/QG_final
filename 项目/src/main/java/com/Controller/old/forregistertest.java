package com.Controller.old;

import com.DAO.UserDAO;
import com.DAO.impl.UserDAOImpl;
import com.pojo.User;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
@WebServlet("/register")
public class forregistertest extends HttpServlet {
      UserDAO userDao=new UserDAOImpl();
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
//        req.setCharacterEncoding("uft-8");
        String username = req.getParameter("username");
        String password = req.getParameter("password");
        String nickname = req.getParameter("nickname");
        String location = req.getParameter("location");
        String PhoneNumber = req.getParameter("PhoneNumber");
        String confirmPassword = req.getParameter("confirmPassword");
        User user=new User(username,password,location,nickname,PhoneNumber);
        int i=userDao.insert(user);
        if(i==1) {
            resp.getWriter().write("登录成功");
        }
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        this.doPost(req, resp);
    }
}
