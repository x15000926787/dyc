package com.dl.impl;
import com.alibaba.fastjson.JSON;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.dl.tool.Tool;

import java.awt.Desktop;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.sql.DataSource;

import org.joda.time.DateTime;
import org.joda.time.Period;
import org.joda.time.PeriodType;
import org.joda.time.format.DateTimeFormat;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.support.JdbcDaoSupport;
import org.apache.commons.lang3.*;


public class emailDAOImpl 
extends JdbcDaoSupport{
	 static SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");  
	public String getemailuser()
	  {
		  	String jsonString="{\"result\":0}";
		  	String sql ="SELECT * from msg_user order by id";
		  	// log(sql);  
		    List userData = getJdbcTemplate().queryForList(sql);
		    int size=userData.size() ;
		    if (size> 0)
		    {
				Object arr[] = new Object[size];
		    	for (int i = 0; i<size; i++)
		    	{
					ArrayList kv = new ArrayList();
		    		Map listData = (Map)userData.get(i);
		    		kv.add(listData.get("id"));
		    		kv.add(listData.get("name"));
		    		kv.add(listData.get("phone"));
		    		kv.add(listData.get("email"));
		    		kv.add("<a id='subBtn' href='javascript:void(0);' title='保存'><span class='fa fa-save'></span></a>&nbsp;&nbsp;&nbsp;&nbsp;<a id='delBtn' href='javascript:void(0);' title='删除'><span class='glyphicon glyphicon-trash'></span></a>&nbsp;&nbsp;&nbsp;&nbsp;<a id='infoBtn' href='javascript:void(0);' title='权限详情'><span class='fa fa-info-circle' onclick=javascript:getuserauthor("+listData.get("id")+",'"+listData.get("name")+"')></span></a>");
		    		arr[i]=kv;
		    		
		    		
		    	}
		    	  java.util.Map<String,Object> map1 = new HashMap<String,Object>();  
			      map1.put("result",1);  
		          map1.put("data",arr);  
				  jsonString = JSON.toJSONString(map1);
		    }
		  
		  
		  return jsonString;
	  }
	public String saveeuserEdit(String jsonStr,Integer sn) throws SQLException, ParseException
	{
		  String jsonString="{\"result\":0}";
		   JSONObject jsonObj=JSON.parseObject(jsonStr);
		   JSONArray addArr= jsonObj.getJSONArray("addArr");
		   JSONArray delArr= jsonObj.getJSONArray("delArr");
		   JSONArray editArr= jsonObj.getJSONArray("editArr");
		   Connection con = null;   
		   PreparedStatement pstm = null; 	    
		   Statement stm = null;   
		   String sql="";
		   try 
		   {   
			   con = getJdbcTemplate().getDataSource().getConnection();
			   con.setAutoCommit(false);
			   stm = con.createStatement();   
			   for (int i = 0; i <  addArr.size(); i++)
				{
					JSONArray  temp=addArr.getJSONArray(i);
					
					//String text2=temp.getString(2)+",以"+temp.getString(4)+"MW/MIN速率"+upDown+temp.getString(6)+"MW";
					
					sql="Insert into msg_user (NAME,PHONE,S_TYPE,EMAIL) values ('"+temp.getString(1)+"','"+temp.getString(2)+"',0,'"+temp.getString(3)+"')";
					
					stm.addBatch(sql); 
					
				}
			   for (int d = 0; d <  delArr.size(); d++)
				{
				   sql="delete from msg_user where id="+delArr.getInteger(d);
				   stm.addBatch(sql);
				   log("delete:  " + sql);
				}
			   for (int e = 0; e <  editArr.size(); e++)
				{
					JSONArray  temp=editArr.getJSONArray(e);
					
					//String text2=temp.getString(2)+",以"+temp.getString(4)+"MW/MIN速率"+temp.getString(5)+"MW";
					sql="update msg_user set NAME='"+temp.getString(1)+"' ,PHONE='"+temp.getString(2)+"',S_TYPE=0,EMAIL='"+temp.getString(3)+"' where ID="+temp.getInteger(0);
					log("UPDATE  " + sql);
					stm.addBatch(sql);
				}
			   stm.executeBatch();   
			   con.commit();
			   con.setAutoCommit(true);
	    	  Map<String,Object> map1 = new HashMap<String,Object>();  
	          map1.put("result",1);  
	          jsonString = JSON.toJSONString(map1);	  
		    }
		   catch (SQLException e)
		   {   
	           e.printStackTrace();   
	           try {   
	               if(!con.isClosed()){   

	               }   
	           	} catch (SQLException e1) {   
	           		e1.printStackTrace();   
	           	}   
	       }finally
	       {   
	    	   stm.close();
	    	   stm = null;
	    	    con.close();
	    	    con = null;
	       }   
		  return jsonString;
	  }
	 public void log(Object msg) 
	  {
		   System.out.println(df.format(new Date())+":"+msg);
		  
		 
	  }
}
