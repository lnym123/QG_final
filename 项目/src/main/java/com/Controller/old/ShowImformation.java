package com.Controller.old;

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

@WebServlet("/showdata")
public class ShowImformation  extends HttpServlet {
    UserDAO userDao=new UserDAOImpl();
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        User user =userDao.selectByname(req.getParameter("username"));
        ObjectMapper mapper = new ObjectMapper();
        Map<String, Object> response = new HashMap<>();

    if(user!=null){
        response.put("group",user.getGroupid());
        response.put("password",user.getPassword());
        response.put("nickname",user.getNickname());
        response.put("number", user.getPhoneNumber());
        response.put("address", user.getLocation());

        System.out.println(response);
        String json = mapper.writeValueAsString(response);
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");
        resp.getWriter().write(json);

   }


    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        this.doGet(req, resp);
    }
}
