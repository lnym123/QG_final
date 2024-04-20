//package com.Controller.Filter;
//
//import javax.servlet.*;
//import javax.servlet.annotation.WebFilter;
//import java.io.IOException;
//import java.io.PrintWriter;
//import java.util.Objects;
//
//@WebFilter("/http://localhost:8080/jwt_test/user/ShowImformation")
//public class BaseFilter implements Filter {
//    @Override
//    public void init(FilterConfig filterConfig) throws ServletException {
//
//    }
//
//    @Override
//    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
//        String username = servletRequest.getParameter("username");
//        if(Objects.equals(username, "null")){
//            servletResponse.setContentType("application/json");
//            PrintWriter out = servletResponse.getWriter();
//
//        }
//    }
//
//    @Override
//    public void destroy() {
//
//    }
//}
