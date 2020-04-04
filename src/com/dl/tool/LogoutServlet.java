/**  
* Title: LogoutServlet.java  
* Description: 
* @author：xx 
* @date 2019-8-16
* @version 1.0
* Company: www.zoutao.info
*/ 
package com.dl.tool;

/**
 *@class_name：LogoutServlet
 *@comments:
 *@param:
 *@return: 
 *@author:xx
 *@createtime:2019-8-16
 */
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.PreparedStatement;

public class LogoutServlet extends HttpServlet {

	PreparedStatement pstmt=null;
	public LogoutServlet()
	{

		//System.out.print("LogoutServlet");
	}
    public void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
		DBConnection dbcon=new DBConnection();
        //1.记录事件
        try {
        	 String sess=null;
        	// System.out.println("1");
    		 sess =  String.valueOf(request.getSession().getAttribute("LOGIN_SUCCESS"));
    		// System.out.println("2");
    	  if (!"null".equals(sess) ){
    		 // System.out.println(dbcon.toString());
			 // System.out.print("ddddd");
			dbcon.setPreparedStatement("insert into loginred (uname,evt) values ('"+sess+"',2)");
			int t=dbcon.getexecuteUpdate();

    	  }
		} catch (Exception e) {
			// TODO: handle exception
		}
        //2.杀死session
        //request.getSession()底层的实现，如果发现没有session立即创建
        /*if(request.getSession(false)!=null //如果没有对应的session，返回null，不会创建session
                && request.getSession().getAttribute("LOGIN_SUCCESS")!=null){
    //  request.getSession(false)!=null && request.getSession().getAttribute("user")!=null  表示有sesson，并且有登录标记
            request.getSession().invalidate();
        }*/
        dbcon.getClose();
        //3.重定向到主页
        response.sendRedirect(request.getContextPath()+"/login.html");
    }

    public void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        doGet(request, response);
    }

}
