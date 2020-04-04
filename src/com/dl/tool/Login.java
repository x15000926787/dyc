package com.dl.tool;

import java.sql.ResultSet;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import java.sql.PreparedStatement;
import java.sql.SQLException;

public class Login implements HandlerInterceptor {

    public void afterCompletion(HttpServletRequest httpRequest,HttpServletResponse httpResponse, Object arg2, Exception arg3)throws Exception {
         
    }

   
    public void postHandle(HttpServletRequest arg0, HttpServletResponse arg1,
            Object arg2, ModelAndView arg3) throws Exception {
        

    }
    
    public Login()
    {
    	
    	System.out.print("Login");
    }
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response,
            Object object) throws Exception {
        /*HttpServletRequest httpRequest = (HttpServletRequest) request;  
         HttpServletResponse httpResponse = (HttpServletResponse) response;*/
        String urlString = request.getRequestURI();
        Cookie[] cookies = request.getCookies();  
        HttpSession session=request.getSession();
		request.setCharacterEncoding("utf-8");
		DBConnection dbcon=new DBConnection();
		PreparedStatement pstmt=null;
		ResultSet rs;
        ///olForum/forumList.html模拟登录页
       // if(urlString.endsWith("forumList.html")){
        //    return true;
       // }
        //请求的路径
		 String contextPath=request.getContextPath();
        if (cookies!=null && cookies.length>0) {
            for (Cookie cookie : cookies) {
            	//System.out.print(cookie.getName());
               // if ("user_login_wxdt".equals(cookie.getName())) {
            	if (cookie.getName().toString().startsWith("user_login_")) {
            		
            		
            		String[] splituser=cookie.getName().toString().split("_"); 
            		String sql="select uname,pwd,id from user where uname=? ";
            		//if(rand.equalsIgnoreCase(imagerand)){
            			try{
            				pstmt=dbcon.setPreparedStatement(sql);
            				pstmt.setString(1,splituser[2]);
            				
            				rs=dbcon.getPrepatedResultSet();
            				if(rs.next()){
            					session.setAttribute("username", splituser[2]);
            					session.setAttribute("LOGIN_SUCCESS", rs.getString("id"));
            					/*try {
            						dbcon.setPreparedStatement("insert into loginred (uname,evt) values ('"+rs.getString("id")+"',1)");
            						t=dbcon.getexecuteUpdate();
            					} catch (Exception e) {
            						// TODO: handle exception
            					}*/
            					 String name = cookie.getValue();
                                 return true;
            				}
                        
            			}catch(SQLException e){
            				System.out.println("出错了"+e.toString());
            			}
            		
            		
            		
                   
                }
            }
            try{
           if (pstmt!=null) pstmt.close();
           if (dbcon!=null)  dbcon.getClose();
            }catch(SQLException e){
				
			}
        }
       
       
        /*httpRequest.getRequestDispatcher("/olForum/forumList").forward(httpRequest, httpResponse);*/
        /*response.sendRedirect(contextPath+"/olForum/forumList.html");*/
        response.sendRedirect(contextPath + "/relogin.html");  
        return false;
        /*httpResponse.sendRedirect(httpRequest.getContextPath()+"/olForum/forumList.html");
        return;*/
        

    }
   /* 
   @overwrite
    public boolean preHandle(HttpServletRequest request,
            HttpServletResponse response, Object handler) throws Exception {
        String requestUri = request.getRequestURI();
        String contextPath = request.getContextPath();
        String url = requestUri.substring(contextPath.length());
       
        HttpSession session = request.getSession();
       
       // if (null==u) 
        {
            Cookie[] cookies = request.getCookies();
            if (cookies!=null && cookies.length>0) {
                for (Cookie cookie : cookies) {
                    if ("user_login_wxdt".equals(cookie.getName())) {
                        String name = cookie.getValue();
                        ;
                    }
                }
            }
        }
 
}*/
}