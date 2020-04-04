/**  
* Title: SessionFilter.java  
* Description: 
* @author：xx 
* @date 2019-8-16
* @version 1.0
* Company: www.zoutao.info
*/ 
package com.dl.tool;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.PrintWriter;
/**
 *@class_name：SessionFilter
 *@comments:
 *@param:
 *@return: 
 *@author:xx
 *@createtime:2019-8-16
 */
public class SessionFilter implements Filter {
	private static final Logger logger = LogManager.getLogger(SessionFilter.class);
    public void destroy() {

        // TODO Auto-generated method stub

    }

    public void doFilter(ServletRequest request, ServletResponse response,FilterChain chain) throws IOException, ServletException {

        HttpServletRequest httpRequest = (HttpServletRequest) request;

        HttpServletResponse httpResponse = (HttpServletResponse) response;

        HttpSession session = httpRequest.getSession();

        // 登陆url

        String loginUrl = httpRequest.getContextPath() + "/login.html";

        String url = httpRequest.getRequestURI();
        
        
        String path = url.substring(url.lastIndexOf("/"));
        //logger.warn(path);
        
        // 超时处理，ajax请求超时设置超时状态，页面请求超时则返回提示并重定向

        if ((path.indexOf(".jsp") > 0 ||path.indexOf(".html")>0||path.indexOf(".htm") > 0) && (path.indexOf("login.html") < 0)&& session.getAttribute("LOGIN_SUCCESS") == null) {

            // 判断是否为ajax请求
            if (httpRequest.getHeader("x-requested-with") != null && httpRequest.getHeader("x-requested-with").equalsIgnoreCase("XMLHttpRequest")) {
            	logger.warn("1:"+session.getAttribute("LOGIN_SUCCESS"));
                httpResponse.addHeader("sessionstatus", "timeOut");

                httpResponse.addHeader("loginPath", loginUrl);

                chain.doFilter(request, response);// 不可少，否则请求会出错

            } else {
            	logger.warn("2:"+session.getAttribute("LOGIN_SUCCESS"));
                String str = "<script language='javascript'>alert('会话过期,请重新登录');"

                        + "window.top.location.href='"

                        + loginUrl

                        + "';</script>";

                response.setContentType("text/html;charset=UTF-8");// 解决中文乱码

                try {

                    PrintWriter writer = response.getWriter();

                    writer.write(str);

                    writer.flush();

                    writer.close();

                } catch (Exception e) {

                    e.printStackTrace();

                }

            }

        } else {
        	logger.warn("3:"+session.getAttribute("LOGIN_SUCCESS"));
            chain.doFilter(request, response);

        }

    }

    @Override
    public void init(FilterConfig arg0) throws ServletException {

        // TODO Auto-generated method stub

    }

	
	

}
