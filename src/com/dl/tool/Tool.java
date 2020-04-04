package com.dl.tool;

import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class Tool
{
	 static SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

	  public static void request(String msg, HttpServletResponse response)
	  {
	    try
	    {
	      response.setContentType("application/json;charset=UTF-8");
	      response.setHeader("Cache-Control", "no-cache");
	      response.setHeader("Pragma", "no-cache");
		  response.addHeader("Access-Control-Allow-Origin","*");
	      response.setDateHeader("Expires", 0L);
	      PrintWriter out = response.getWriter();
	      out.print(msg);
	    }
	    catch (IOException e)
	    {
	      e.printStackTrace();
	    }
	  }
	  
	  public static String rightTrim(String str)
	  {
	    if (str == null) {
	      return "";
	    }
	    int length = str.length();
	    for (int i = length - 1; i >= 0; i--)
	    {
	      if (str.charAt(i) != ' ') {
	        break;
	      }
	      length--;
	    }
	    return str.substring(0, length);
	  }
	  
	public static void log(Object msg)
	{
		  System.out.println(df.format(new Date())+":"+msg);
	}
	
	
	 public static String getIpAddr(HttpServletRequest request) {      

	        String ip = request.getHeader("x-forwarded-for");      

	            if(ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {     

	                ip = request.getHeader("Proxy-Client-IP");      

	            }     

	            if(ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {     

	                ip = request.getHeader("WL-Proxy-Client-IP");     

	            }     

	            if(ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {     

	                ip = request.getRemoteAddr();      

	            }   

	       return ip;     

	    }  
	
}
