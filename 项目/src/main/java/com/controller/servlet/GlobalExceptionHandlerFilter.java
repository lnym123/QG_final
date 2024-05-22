package com.controller.servlet;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@WebFilter("/*")
public class GlobalExceptionHandlerFilter implements Filter {

    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandlerFilter.class);

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        // 初始化Filter，可以加载配置、初始化资源等
        logger.info("Initializing GlobalExceptionHandlerFilter");
        // 例如，从filterConfig中获取初始化参数
        String someInitParam = filterConfig.getInitParameter("someParameter");
        logger.info("Init parameter value: {}", someInitParam);
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        try {
            chain.doFilter(request, response);
        } catch (Exception e) {
            // 设置HTTP状态码为500，表示服务器内部错误
            httpResponse.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);

            // 获取PrintWriter对象用于写入响应
            PrintWriter writer = httpResponse.getWriter();

            // 写入错误信息
            writer.write("Internal Server Error: " + e.getMessage());

            logger.error("Exception occurred while processing request for {}", httpRequest.getRequestURI(), e);

            // 如果需要，可以发送错误通知或者进行其他错误处理操作
        }
    }

    @Override
    public void destroy() {
        // 销毁Filter，释放资源
        logger.info("Destroying GlobalExceptionHandlerFilter");
        // 例如，关闭数据库连接、停止线程等资源清理操作
    }
}