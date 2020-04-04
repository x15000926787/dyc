package com.dl.tool;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.mail.Email;
import org.apache.commons.mail.EmailException;
import org.apache.commons.mail.SimpleEmail;
 
public class emailUtil {
	public static Email email = null;
	public emailUtil()
	{
		email = new SimpleEmail();
		email.setHostName("smtp.mxhichina.com");
		email.setAuthentication("service@windlinker.com", "Windlinker.com@");//设置的邮箱为你开启smtp服务和授权码
		email.setSmtpPort(25);//setSmtpPort("25");//设置访问smtp的端口
		email.setSSLOnConnect(true);//设置SSL链接
		email.setCharset("utf-8");
	}
	
	public String emailSend(String subject,String msg,HashMap<String, String> umap)
	{
		String rlt = "mail success sended";
		try {
			email.setFrom("service@windlinker.com");//这里的邮箱地址是你开启smtp服务的邮箱地址（必须一样）
			email.setSubject(subject);
			email.setMsg(msg);
			//email.addTo("158295783@qq.com","ddd");//这是目的的邮箱地址
			for (Map.Entry<String, String> entry : umap.entrySet()){
				String key = entry.getKey();
				String value = entry.getValue();
				email.addTo(value,key);
			}
			email.send();
			
			return rlt;
		} catch (EmailException e) {
			// TODO Auto-generated catch block
			rlt = "something wrong in sending mail";
			e.printStackTrace();
		}
		
		return rlt;
		
	}
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
		try {
			email.setFrom("service@windlinker.com");//这里的邮箱地址是你开启smtp服务的邮箱地址（必须一样）
			email.setSubject("TestMail");
			email.setMsg("This is a test mail ... :-)");
			email.addTo("158295783@qq.com","ddd");//这是目的的邮箱地址
			email.send();
		} catch (EmailException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
 
}