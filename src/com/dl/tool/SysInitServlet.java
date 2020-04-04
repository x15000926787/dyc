package com.dl.tool;

import java.io.IOException;  
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;  

import javax.servlet.ServletContextEvent;  
import javax.servlet.ServletContextListener;  

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.context.WebApplicationContext;  
import org.springframework.web.context.support.WebApplicationContextUtils;  
  
/** 
 * 系统初始化 
 * @author xx
 * 
 */  
public class SysInitServlet implements ServletContextListener {  
 //获取spring注入的bean对象  
 private WebApplicationContext springContext;  
 public static JdbcTemplate jdbcTemplate;
	 public static JdbcTemplate getJdbcTemplate() {
		return jdbcTemplate;
	}
	public static void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
		SysInitServlet.jdbcTemplate = jdbcTemplate;
	}
  
 
   
 public SysInitServlet(){  
  super();  
 }  
 /** 
  *应用程序退出时调用 
  */  
 @Override  
 public void contextDestroyed(ServletContextEvent event) {  
  
  System.out.println("应用程序关闭!");  
 }  

/** 
  * 应用程序加载时调用 
  */  
 @Override  
 public void contextInitialized(ServletContextEvent event) {  
  springContext = WebApplicationContextUtils.getWebApplicationContext(event.getServletContext());  
  if(springContext != null){  
	  jdbcTemplate = (JdbcTemplate)springContext.getBean("jdbcTemplate");  
	 
	  
  }else{  
   System.out.println("获取应用程序上下文失败!");  
   return;  
  }  
  System.out.println("初始化系统服务!");  
  
 }  
   
 
}
