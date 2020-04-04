package com.bjsxt.listener;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import java.util.Timer;

public class MessageListener implements ServletContextListener {
    private Timer timer=null;
    @Override
    public void contextInitialized(ServletContextEvent servletContextEvent) {
    timer=new Timer(true);
    servletContextEvent.getServletContext().log("start");
    timer.schedule(new MessageTask(servletContextEvent.getServletContext()),1000,5*1000);
    }

    @Override
    public void contextDestroyed(ServletContextEvent servletContextEvent) {

    }
}
