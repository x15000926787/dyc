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


public class treeDAOImpl 
extends JdbcDaoSupport{
	
	 public String get_tree(String pno)
	  {
		  String jsonString="";

		    String sql = "SELECT rtuno,name from prtu where domain="+pno;
		    log(sql);
		    List userData = getJdbcTemplate().queryForList(sql);
		    int size=userData.size() ;
		    if (size> 0)
		    {
				Object arr[] = new Object[size];
		    	for (int i = 0; i<size; i++)
		    	{
					ArrayList kv = new ArrayList();
		    		Map listData = (Map)userData.get(i);
		    		
		    		//kv.add(kdString);
		    		kv.add(listData.get("rtuno"));
		    		kv.add(listData.get("name"));
		    		arr[i]=kv;
		    		
		    		
		    	}
		    	  java.util.Map<String,Object> map1 = new HashMap<String,Object>();  
			      map1.put("result",1);  
		          map1.put("data",arr);  
				  jsonString = JSON.toJSONString(map1);
		    }
		  
		  return jsonString;
	  }
	 
	
	 
	 public String get_tree_2(int dd,String kk,String rtu)
	  {
		  String jsonString="";
		  String thetab = "prtuana"; 
		  if (dd>0) thetab = "prtupul";
		  java.util.Map<String,Object> map1 = new HashMap<String,Object>();  

		  Integer kind = 0;
		    try {
				kind = Integer.parseInt(kk);
			} catch (Exception e) {
				kind = 1;
				// TODO: handle exception
			}
		    // king = 1 小项目（三级） ,=2 大项目（四级）
		    String sql = "select distinct a.devicenm p_name,a.deviceno p_no from dev_author a,room b where a.roomno=b.roomno and b.rtuno="+rtu+" and a.type=0";
		    if (kind == 2)
		    {
		    sql = "select distinct roomname p_name,roomno p_no from room where rtuno="+rtu;
		    }else {
		    	// map1.put("result",0);  
			}
		    List userData = getJdbcTemplate().queryForList(sql);
		    int size=userData.size() ;
		    if (size> 0)
		    {
				Object arr[] = new Object[size];
		    	for (int i = 0; i<size; i++)
		    	{
					ArrayList kv = new ArrayList();
		    		Map listData = (Map)userData.get(i);
		    		kv.add(listData.get("p_no"));
		    		kv.add(listData.get("p_name"));
		    		arr[i]=kv;
		    		
		    		
		    	}
		    	  map1.put("result",1);  
		          map1.put("data",arr);  
				  
		      }  
		    
		    jsonString = JSON.toJSONString(map1);
		  return jsonString;
	  }
	 
	 public String get_tree_3(int dd ,String kk,String rtu,String len,int userId)
	  {
		  String jsonString="";
		  String  jsonStr = null;
		  String thetab = "prtuana"; 
		  if (dd>0) thetab = "prtupul";
		  java.util.Map<String,Object> map1 = new HashMap<String,Object>(); 
		  int asize = 5;
		 
		    try {
		    	asize = Integer.parseInt(len);
			} catch (Exception e) {
				asize = 5;
			}
		  String ntit = "",otit = "";
		  int i = 0,a = 0,b = 0;
		  String items[] = new String[asize*2+1];
		  for ( i=0 ;i<items.length;i++) items[i] = "";
		  i = 0;
		  int kind = 0;
		    try {
				kind = Integer.parseInt(kk);
			} catch (Exception e) {
				kind = 1;
				// TODO: handle exception
			}
		    // king = 1 小项目（三级） ,=2 大项目（四级）
		    String sql = "select b.devicenm s_name,a.name p_name,a.saveno p_no from  dev_author b,prtuana a,room c where (power(2,"+userId+"-1)&a.author_read)>0 and a.deviceno=b.deviceno and b.roomno=c.roomno and c.rtuno="+dd+" and a.deviceno ="+rtu+" order by saveno";
		    if (kind == 2)
		    {
		    	sql = "select distinct get_subs(name,1) s_name,get_subs(name,0) p_name,saveno p_no from "+thetab+" where (power(2,"+userId+"-1)&author_read)>0 and roomno="+rtu+" order by saveno";
		    }else {
		    	 //map1.put("result",0);  
			}
		    	log(sql);
		    	List userData = getJdbcTemplate().queryForList(sql);
		    	ArrayList myList = new ArrayList();
		    int size=userData.size() ;
		    if (size> 0)
		    {
				//Object arr[] = new Object[size];
		    	//for (int i = 0; i<size; i++)
		    	log(size);
				while (i<size)
		    	{
					//ArrayList kv = new ArrayList();
		    		Map listData = (Map)userData.get(i);
		    		ntit = listData.get("s_name").toString();
		    		
		    		if (i == 0 )
		    			{
		    			otit = ntit;
		    			items[0] = ntit;
		    			}
		    		if (!ntit.equals(otit))
		    		{
		    			otit = ntit;
		    			a = 0;
		    			 jsonStr = JSON.toJSONString(items);
		    		    
		    			myList.add(JSON.toJSON(jsonStr));
		    			items[0] = ntit;
		    		}
		    		items[a*2+1] = listData.get("p_no").toString();
		    		items[a*2+2] = listData.get("p_name").toString();
		    		a++;
		    		if (a == asize) 
		    		{
		    			//String items_[] = items;
		    			//log(items_[0]);
		    			// JSONArray jsonArray=new JSONArray(items);
		    			// JSONObject jsonObject = JSON.fromObject(items);
		    		      jsonStr = JSON.toJSONString(items);
		    		    
		    			myList.add(JSON.toJSON(jsonStr));
		    			
		    			a = 0;
		    			b = 1;
		    			while (b<asize*2+1) {
							 items[b] = "";	
							 b++;
						}
		    		}
		    		 
		    		
		    		
		    		i++;
		    	}
				if (a != 0) 
				{
					 jsonStr = JSON.toJSONString(items);
		    			myList.add(JSON.toJSON(jsonStr));
				}
				   
		    	  map1.put("result",1);  
		          map1.put("data",myList);  
				  
		      }else {
		    	  map1.put("result",0);  
			  }  
		    
		    jsonString = JSON.toJSONString(map1);
		  return jsonString;
	  }
	 
	 public String get_tree_4(String kk,String rtu,String nm)
	  {
		  String jsonString="";
		  java.util.Map<String,Object> map1 = new HashMap<String,Object>();  

		  Integer kind = 0;
		    try {
				kind = Integer.parseInt(kk);
			} catch (Exception e) {
				kind = 1;
				// TODO: handle exception
			}
		    // king = 1 小项目（三级） ,=2 大项目（四级）
		    String sql = "select get_subs(name,0) p_name,saveno p_no from prtuana where rtuno="+rtu+" and name like '"+nm+"%' order by saveno";
		    if (kind == 2)
		    {
		    	sql = "select get_subs(name,0) p_name,saveno p_no from prtuana where roomno="+rtu+" and name like '"+nm+"%' order by saveno";
		    }else {
		    	
			}
		    log("r4:"+sql);
		    	List userData = getJdbcTemplate().queryForList(sql);
		    int size=userData.size() ;
		    if (size> 0)
		    {
				Object arr[] = new Object[size];
		    	for (int i = 0; i<size; i++)
		    	{
					ArrayList kv = new ArrayList();
		    		Map listData = (Map)userData.get(i);
		    		kv.add(listData.get("p_no"));
		    		kv.add(listData.get("p_name"));
		    		arr[i]=kv;
		    		
		    		
		    	}
		    	  map1.put("result",1);  
		          map1.put("data",arr);  
				  
		      }  
		   
		    jsonString = JSON.toJSONString(map1);
		  return jsonString;
	  } 
	 
	 
	 public void log(Object msg)
	  {
	    System.out.println(msg);
	  }
}
