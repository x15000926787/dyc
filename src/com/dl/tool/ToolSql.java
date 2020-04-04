/**  
* Title: ToolSql.java  
* Description: 
* @author：xx 
* @date 2019-1-8
* @version 1.0
* Company: www.zoutao.info
*/ 
package com.dl.tool;

/**
 *@class_name：ToolSql
 *@comments:
 *@param:
 *@return: 
 *@author:xx
 *@createtime:2019-1-8
 */

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
import org.junit.Test;
public class ToolSql 
extends JdbcDaoSupport{
	 
	  /**
	   * @desc 查询最近一个定时任务
	   * @author xx
	   * @date 2019-1-8
	   */
@Test
	  public String find_task()throws Exception 
	  {
		  String jsonString="";
		  Date today = new Date();
	      Calendar c=Calendar.getInstance();
	      c.setTime(today);
	      int weekday=c.get(Calendar.DAY_OF_WEEK)-1;
	      Calendar cal = Calendar.getInstance();
	      cal.add(Calendar.DAY_OF_YEAR, 1);
	      cal.set(Calendar.HOUR_OF_DAY, 0);      
	      cal.set(Calendar.SECOND, 0);
	      cal.set(Calendar.MINUTE, 0);
	      cal.set(Calendar.MILLISECOND, 0);
	      long cur_min = (long) (System.currentTimeMillis()-cal.getTimeInMillis()) / 60 / 1000;

		    String sql = "SELECT * from prtu";
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
		    		kv.add(listData.get("rtuno"));
		    		kv.add(listData.get("name"));
		    		arr[i]=kv;
		    		
		    		
		    	}
		    	
		    	  java.util.Map<String,Object> map1 = new HashMap<String,Object>();  
			      map1.put("result",1);  
		          map1.put("data",arr);  
				  jsonString = JSON.toJSONString(map1);
				  log(jsonString);
		    }
		  
		  return jsonString;
	  }
	  
	 
	 public void log(Object msg)
	  {
	    System.out.println(msg);
	  }
}

