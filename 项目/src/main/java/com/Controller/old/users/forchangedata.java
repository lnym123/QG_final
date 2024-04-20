package com.Controller.old.users;

import com.DAO.UserDAO;
import com.DAO.impl.UserDAOImpl;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.pojo.User;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@WebServlet("/change")
public class forchangedata extends HttpServlet {
    UserDAO userDao=new UserDAOImpl();
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String username = req.getParameter("username");
        String  password = req.getParameter("password");
        String location = req.getParameter("location");
        String  nickname = req.getParameter("nickname");
        String    PhoneNumber = req.getParameter("PhoneNumber");
        User user=new User(username,password,location,nickname,PhoneNumber);
        int i=userDao.updateData(user);
        ObjectMapper mapper = new ObjectMapper();
        Map<String, Object> response = new HashMap<>();
        if (i==1) {
            response.put("success", true);
            String json = mapper.writeValueAsString(response);
            resp.setContentType("application/json");
            resp.setCharacterEncoding("UTF-8");
            resp.getWriter().write(json);
        }
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        this.doPost(req, resp);
    }
}
