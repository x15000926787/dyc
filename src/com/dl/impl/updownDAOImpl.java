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
import java.sql.DriverManager;
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


public class updownDAOImpl 
extends JdbcDaoSupport{
	 public String getName(int rtu, int sn) {
		    String jsonString = "";
		    String sql = "select concat(c.name,b.roomname,a.name) tname from prtuana a,room b,prtu c where a.rtuno=c.rtuno and a.roomno=b.roomno and a.sn=" + sn + " and a.rtuno=" + rtu;
		    List userData = getJdbcTemplate().queryForList(sql);
		    Map countMap = (Map)userData.get(0);
		    return countMap.get("tname").toString();
		  }
	public String testHelper(int uid) {
	    String jsonString = "{\"result\":0}";
	    String str = "";
	    try {
	      Class.forName("org.sqlite.JDBC");
	      Connection conn = DriverManager.getConnection("jdbc:sqlite:D:\\cstation\\cfg\\calc_exp.rdb");
	      
	      Statement stmt = conn.createStatement();


	      
	      ResultSet rs = stmt.executeQuery("SELECT * FROM calc_evt where uid=" + Math.floor((uid / 10000)) + " and pid=" + (uid % 10000));

	      
	      int size = 2;
	      int uuid = (int)Math.floor((uid / 10000));
	      int ppid = uid % 10000;
	      String name = "";
	      int i = 0;
	      
	      if (size > 0) {

	        
	        Object[] arr = new Object[size];
	        Object[] narr = new Object[1];
	        while (rs.next()) {
	          
	          log(rs.getString("sn"));
	          ArrayList kv = new ArrayList();
	          
	          kv.add(rs.getString("sn"));
	          kv.add(rs.getString("uid"));
	          kv.add(rs.getString("pid"));
	          kv.add(rs.getString("name"));
	          uuid = rs.getInt("uid");
	          ppid = rs.getInt("pid");
	          name = rs.getString("name");
	          str = rs.getString("close_exp");
	          if (str.indexOf(">") > 0) {
	            kv.add("上限");
	            kv.add(str.substring(str.indexOf(">") + 1).trim());
	          } else {
	            
	            kv.add("下限");
	            kv.add(str.substring(str.indexOf("<") + 1).trim());
	          } 
	          
	          kv.add("<a id='subBtn' href='javascript:void(0);'><span class='fa fa-save'></span></a>&nbsp;&nbsp;&nbsp;&nbsp;<a id='delBtn' href='javascript:void(0);'><span class='glyphicon glyphicon-trash'></span></a>");
	          
	          arr[i] = kv;
	          i++;
	        } 
	        
	        narr[0] = arr[0];
	        
	        Map<String, Object> map1 = new HashMap<String, Object>();
	        map1.put("result", Integer.valueOf(1));
	        map1.put("tname", getName((int)Math.floor((uid / 10000)), uid % 10000));
	        map1.put("uid", Integer.valueOf(uuid));
	        map1.put("pid", Integer.valueOf(ppid));
	        map1.put("i", Integer.valueOf(i));
	        
	        map1.put("name", name);
	        if (i > 1) {
	          map1.put("data", arr);
	        } else {
	          
	          map1.put("data", narr);
	        } 
	        jsonString = JSON.toJSONString(map1);
	      } 
	      
	      stmt.close();
	      conn.close();

	    
	    }
	    catch (ClassNotFoundException e) {
	      e.printStackTrace();
	    } catch (SQLException e) {
	      e.printStackTrace();
	    } 
	    log(jsonString);
	    return jsonString;
	  }
	  
  public String saveupdown(String jsonStr, Integer sn) throws SQLException, ParseException {
	    String jsonString = "{\"result\":0}";
	    JSONObject jsonObj = JSON.parseObject(jsonStr);
	    JSONArray addArr = jsonObj.getJSONArray("addArr");
	    JSONArray delArr = jsonObj.getJSONArray("delArr");
	    JSONArray editArr = jsonObj.getJSONArray("editArr");
	    Connection conn = null;
	    PreparedStatement pstm = null;
	    Statement stmt = null;
	    String sql = "";
	    
	    try {
	      Class.forName("org.sqlite.JDBC");
	      conn = DriverManager.getConnection("jdbc:sqlite:D:\\cstation\\cfg\\calc_exp.rdb");
	      

	       stmt = conn.createStatement();
	      for (int i = 0; i < addArr.size(); i++) {
	        String str2, str1;
	        JSONArray temp = addArr.getJSONArray(i);
	        int udtype = temp.getInteger(4).intValue();
	        
	        if (udtype == 0) {
	          
	          str1 = "get_r_f32(''ch_0'', ''mqtt_r_ai'', ''ai_" + temp.getString(1) + "_" + temp.getString(2) + "'', ''value'') > " + temp.getString(5);
	          str2 = "get_r_f32(''ch_0'', ''mqtt_r_ai'', ''ai_" + temp.getString(1) + "_" + temp.getString(2) + "'', ''value'') < " + temp.getString(5);
	        } else {
	          str1 = "get_r_f32(''ch_0'', ''mqtt_r_ai'', ''ai_" + temp.getString(1) + "_" + temp.getString(2) + "'', ''value'') < " + temp.getString(5);
	          str2 = "get_r_f32(''ch_0'', ''mqtt_r_ai'', ''ai_" + temp.getString(1) + "_" + temp.getString(2) + "'', ''value'') > " + temp.getString(5);
	        } 
	        
	        ResultSet rs = stmt.executeQuery("SELECT MIN(sn)+1 nsn  FROM calc_evt AS id1 WHERE NOT EXISTS(SELECT * FROM calc_evt AS id2 WHERE id1.sn+1=id2.sn)");
	        int nsn = rs.getInt("nsn");

	        
	        sql = "Insert into calc_evt (sn,key,name,enb_flg,uid,pid,value,quality,ref_time,chg_time,close_exp,close_delay_time,close_keep_time,open_exp,open_delay_time,open_keep_time,event_type,event_level,note) values (";
	        sql = String.valueOf(sql) + nsn + ",'evt_" + nsn + "','" + temp.getString(3) + "',1," + temp.getString(1) + "," + temp.getString(2) + ",0,0,0,0,'" + str1 + "',0,0,'" + str2 + "',0,0,'o_2_c',1,0)";
	        log("insert  " + sql);
	        stmt.addBatch(sql);
	      } 
	      
	      for (int d = 0; d < delArr.size(); d++) ;
	      /*
	      {
	        
	        sql = "delete from calc_evt where sn=" + delArr.getInteger(d);
	        stmt.addBatch(sql);
	        sql = "update calc_evt set sn=sn-1 where sn>" + delArr.getInteger(d);
	        stmt.addBatch(sql);
	        sql = "update calc_evt  set key='evt_'||sn where sn>=" + delArr.getInteger(d);
	        stmt.addBatch(sql);
	      } */
	      
	      for (int e = 0; e < editArr.size(); e++) {
	        String str2, str1;
	        JSONArray temp = editArr.getJSONArray(e);
	        int udtype = temp.getInteger(4).intValue();
	        
	        if (udtype == 0) {
	          
	          str1 = "get_r_f32(''ch_0'', ''mqtt_r_ai'', ''ai_" + temp.getString(1) + "_" + temp.getString(2) + "'', ''value'') > " + temp.getString(5);
	          str2 = "get_r_f32(''ch_0'', ''mqtt_r_ai'', ''ai_" + temp.getString(1) + "_" + temp.getString(2) + "'', ''value'') < " + temp.getString(5);
	        } else {
	          str1 = "get_r_f32(''ch_0'', ''mqtt_r_ai'', ''ai_" + temp.getString(1) + "_" + temp.getString(2) + "'', ''value'') < " + temp.getString(5);
	          str2 = "get_r_f32(''ch_0'', ''mqtt_r_ai'', ''ai_" + temp.getString(1) + "_" + temp.getString(2) + "'', ''value'') > " + temp.getString(5);
	        } 


	        
	        sql = "update calc_evt set close_exp='" + str1 + "' ,open_exp='" + str2 + "' where sn=" + temp.getInteger(0);
	        log("UPDATE  " + sql);
	        stmt.addBatch(sql);
	      } 
	      stmt.executeBatch();
	      
	      conn.setAutoCommit(true);
	      Map<String, Object> map1 = new HashMap<String, Object>();
	      map1.put("result", Integer.valueOf(1));
	      jsonString = JSON.toJSONString(map1);
	    }
	    catch (ClassNotFoundException e) {
	      e.printStackTrace();
	    } catch (SQLException e) {
	      e.printStackTrace();
	    } finally {
	      
	      stmt.close();
	      stmt = null;
	      conn.close();
	      conn = null;
	    } 

	    
	    try {
	      conn = getJdbcTemplate().getDataSource().getConnection();
	      conn.setAutoCommit(false);
	      stmt = conn.createStatement();
	      for (int i = 0; i < addArr.size(); i++) {
	        
	        JSONArray temp = addArr.getJSONArray(i);
	        int udtype = temp.getInteger(4).intValue();
	        
	        if (udtype == 0) {
	          
	          sql = " update prtuana set upperlimit=" + temp.getString(5) + " where rtuno=" + temp.getString(1) + " and sn=" + temp.getString(2);
	        } else {
	          sql = " update prtuana set lowerlimit=" + temp.getString(5) + " where rtuno=" + temp.getString(1) + " and sn=" + temp.getString(2);
	        } 
	        
	        stmt.addBatch(sql);
	      } 
	      
	      for (int d = 0; d < delArr.size(); d++);


	      
	      for (int e = 0; e < editArr.size(); e++) {
	        
	        JSONArray temp = editArr.getJSONArray(e);
	        int udtype = temp.getInteger(4).intValue();
	        
	        if (udtype == 0) {
	          
	          sql = " update prtuana set upperlimit=" + temp.getString(5) + " where rtuno=" + temp.getString(1) + " and sn=" + temp.getString(2);
	        } else {
	          sql = " update prtuana set lowerlimit=" + temp.getString(5) + " where rtuno=" + temp.getString(1) + " and sn=" + temp.getString(2);
	        } 

	        
	        log("UPDATE  " + sql);
	        stmt.addBatch(sql);
	      } 
	      stmt.executeBatch();
	      
	      conn.setAutoCommit(true);
	      Map<String, Object> map1 = new HashMap<String, Object>();
	      map1.put("result", Integer.valueOf(1));
	      jsonString = JSON.toJSONString(map1);
	    }
	    catch (SQLException e) {
	      e.printStackTrace();
	    } finally {
	      
	      stmt.close();
	      stmt = null;
	      conn.close();
	      conn = null;
	    } 
	    return jsonString;
	  }
  
  public String syncupdown()throws SQLException, ParseException {
	    String jsonString = "{\"result\":0}";
	    
	    Connection conn = null;
	    Statement stmt = null;
	    Connection conn_m = null;
	    Statement stmt_m = null;
	    String sql = "";
	    
	    try {
	      Class.forName("org.sqlite.JDBC");
	      conn = DriverManager.getConnection("jdbc:sqlite:D:\\cstation\\cfg\\calc_exp.rdb");

	      
	      stmt = conn.createStatement();
	      conn_m = getJdbcTemplate().getDataSource().getConnection();
	      conn_m.setAutoCommit(false);
	      stmt_m = conn_m.createStatement();
	      
	      ResultSet rs = stmt.executeQuery("SELECT *  FROM calc_evt order by uid,pid");
	      while (rs.next()) {
	        
	        String exp = rs.getString("close_exp");
	        
	        if (exp.indexOf(">") > 0) {
	          
	          sql = " update prtuana set upperlimit=" + exp.substring(exp.indexOf(">") + 1).trim() + " where rtuno=" + rs.getString("uid") + " and sn=" + rs.getString("pid");
	        } else {
	          sql = " update prtuana set lowerlimit=" + exp.substring(exp.indexOf("<") + 1).trim() + " where rtuno=" + rs.getString("uid") + " and sn=" + rs.getString("pid");
	        } 
	        
	        stmt_m.addBatch(sql);
	        stmt_m.executeBatch();
	      } 
	      
	      conn_m.commit();
	      conn_m.setAutoCommit(true);
	      
	      Map<String, Object> map1 = new HashMap<String, Object>();
	      map1.put("result", Integer.valueOf(1));
	      jsonString = JSON.toJSONString(map1);
	    }
	    catch (ClassNotFoundException e) {
	      e.printStackTrace();
	    } catch (SQLException e) {
	      e.printStackTrace();
	    } finally {
	      
	      stmt.close();
	      stmt = null;
	      conn.close();
	      conn = null;
	      stmt_m.close();
	      stmt_m = null;
	      conn_m.close();
	      conn_m = null;
	    } 
	    
	    return jsonString;
	  }
	  
  
  
	  
  public String loadconfig_updown(String dn, String nm, int lev) {
	  String jsonString = "";
	    String rtuString = "";
	    String sql = "";
	    if (lev == 1) {
	      
	      sql = "select roomname p_name,roomno rtunos,rtuno from room where rtuno=" + dn;
	    } else if (lev == 2) {
	      
	      sql = "select distinct get_subs(name,1) p_name,rtuno,roomno rtunos from prtuana where roomno=" + dn;
	    } else {
	      sql = "select get_subs(name,0) p_name,rtuno*10000+sn rtunos,rtuno from prtuana where roomno=" + dn + " and name like '" + nm + "%' order by saveno";
	    } 
	    log(sql);
	    List userData = getJdbcTemplate().queryForList(sql);
	    int size = userData.size();
	    if (size > 0) {
	      
	      Object[] arr = new Object[size];
	      for (int i = 0; i < size; i++) {

	        
	        Map listData = (Map)userData.get(i);
	        rtuString = "{\"text\":\"" + listData.get("p_name") + "\",\"a_attr\":{\"no\":\"" + listData.get("rtunos") + "\"}}";
	        if (i == 0) {
	          
	          jsonString = rtuString;
	        } else {
	          
	          jsonString = String.valueOf(jsonString) + "," + rtuString;
	        } 
	      } 
	    } 


	    
	    return "[" + jsonString + "]";
	  }
  public String loadconfig_updown_cn(String dn, String nm, int lev) {
	  String jsonString = "";
	    String rtuString = "";
	    String sql = "";
	    if (lev == 1) {
	      
	      sql = "select distinct get_subs(name,1) p_name,rtuno,roomno rtunos from prtuana where rtuno=" + dn;
	    } else {
	      sql = "select get_subs(name,0) p_name,rtuno*10000+sn rtunos,rtuno from prtuana where rtuno=" + dn + "-1 and name like '" + nm + "%' order by saveno";
	    } 
	    log(sql);
	    List userData = getJdbcTemplate().queryForList(sql);
	    int size = userData.size();
	    if (size > 0) {
	      
	      Object[] arr = new Object[size];
	      for (int i = 0; i < size; i++) {

	        
	        Map listData = (Map)userData.get(i);
	        rtuString = "{\"text\":\"" + listData.get("p_name") + "\",\"a_attr\":{\"no\":\"" + listData.get("rtunos") + "\"}}";
	        if (i == 0) {
	          
	          jsonString = rtuString;
	        } else {
	          
	          jsonString = String.valueOf(jsonString) + "," + rtuString;
	        } 
	      } 
	    } 


	    
	    return "[" + jsonString + "]";
	  }
  
	 
	 
	 public void log(Object msg)
	  {
	    System.out.println(msg);
	  }
}
