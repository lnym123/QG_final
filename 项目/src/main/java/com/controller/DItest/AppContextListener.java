package com.controller.DItest;

import com.service.impl.*;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

public class AppContextListener implements ServletContextListener {
     //监听器，初始化依赖注入容器
    @Override
    public void contextInitialized(ServletContextEvent sce) {
        //向DI容器注册各个service实现类
        SimpleDIContainer container = new SimpleDIContainer();
        container.registerBean(GroupServiceImpl.class, new GroupServiceImpl());
        container.registerBean(UserServiceImpl.class, new UserServiceImpl());
        container.registerBean(MessageServiceImpl.class, new MessageServiceImpl());
        container.registerBean(TransactionServiceImpl.class, new TransactionServiceImpl());
        container.registerBean(FundServiceImpl.class, new FundServiceImpl());
        //ServletContext是全局的应用上下文
        sce.getServletContext().setAttribute("diContainer", container);
    }
    //web关闭的时候
    @Override
    public void contextDestroyed(ServletContextEvent sce) {

    }
}