package com.dl.impl;
import com.alibaba.fastjson.JSON;
import org.springframework.jdbc.core.support.JdbcDaoSupport;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


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
		    // kind = 1 小项目（三级） ,=2 大项目（四级）
		    String sql = "select distinct a.devicenm p_name,a.deviceno p_no from dev_author a,room b where a.roomno=b.roomno and b.rtuno="+rtu+" and a.type=0";
		    if (kind == 2)
		    {
		    sql = "select distinct roomname p_name,roomno p_no,rtuno r_no from room where rtuno="+rtu;
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
					kv.add(listData.get("r_no"));
		    		arr[i]=kv;
		    		
		    		
		    	}
		    	  map1.put("result",1);  
		          map1.put("data",arr);  
				  
		      }  
		    
		    jsonString = JSON.toJSONString(map1);
		  return jsonString;
	  }
	 
	 public String get_tree_3(String kk,String rtu,String pno)
	  {
		  String jsonString="";
		  String  jsonStr = null;
		  String thetab = "prtuana"; 

		  java.util.Map<String,Object> map1 = new HashMap<String,Object>(); 

		  String ntit = "",otit = "";
		  int i = 0,a = 0,b = 0;


		  i = 0;
		  int kind = 0;
		    try {
				kind = Integer.parseInt(kk);
			} catch (Exception e) {
				kind = 1;
				// TODO: handle exception
			}
		    // king = 1 小项目（三级） ,=2 大项目（四级）
		  /**
		   * kind=1 sql有问题，以后再说，目前固定kk=2
		   */
		  String sql = "select b.devicenm s_name,a.name p_name,a.saveno p_no from  dev_author b,prtuana a,room c where  a.deviceno=b.deviceno and b.roomno=c.roomno and c.rtuno="+rtu+" and a.deviceno ="+rtu+" order by saveno";
		    if (kind == 2)
		    {
		    	sql = "select devicenm,deviceno,domain,roomno from dev_author where  domain="+rtu+" and roomno="+pno+" order by deviceno";
		    }else {
		    	 //map1.put("result",0);  
			}
		    	log(sql);
		    	List userData = getJdbcTemplate().queryForList(sql);
		  int size=userData.size() ;
		  if (size> 0)
		  {
			  Object arr[] = new Object[size];
			  for ( i = 0; i<size; i++)
			  {
				  ArrayList kv = new ArrayList();
				  Map listData = (Map)userData.get(i);

				  kv.add(listData.get("devicenm"));

				  kv.add(listData.get("domain"));
				  kv.add(listData.get("roomno"));
				  kv.add(listData.get("deviceno"));
				  arr[i]=kv;


			  }
			  map1.put("result",1);
			  map1.put("data",arr);

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
