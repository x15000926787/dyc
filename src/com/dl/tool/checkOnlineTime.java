package com.dl.tool;

import com.alibaba.fastjson.JSONObject;
import com.bjsxt.server.ChatSocket;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.quartz.*;
import org.springframework.jdbc.core.JdbcTemplate;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.exceptions.JedisConnectionException;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.Calendar;
import java.util.regex.Pattern;

/**
 * [任务类]
 * @author xx
 * @date 2019-8-20 
 * @copyright copyright (c) 2018
 */
@PersistJobDataAfterExecution
@DisallowConcurrentExecution
public  class checkOnlineTime implements Job {


	  private static final Logger logger = LogManager.getLogger(checkOnlineTime.class);


	  public static AnaUtil myana=new AnaUtil();
	  private    long gno=0,vno=0;
	Jedis jedis = null;
	  private String vv = null;
	public DBConnection dbcon=null;//new DBConnection();
	  private HashMap<String,Object> onlinemap =null;
	//static ScriptEngineManager scriptEngineManager = new ScriptEngineManager()
	//static ScriptEngine scriptEngine ;//= scriptEngineManager.getEngineByName("nashorn");
	public static ChatSocket ckt = new ChatSocket();

		
   

	@Override
    public void execute(JobExecutionContext arg0) throws JobExecutionException {
       //
		//logger.warn("saveHistyc");
    	DBConnection dbcon=new DBConnection();

		jedis = RedisUtil.getJedis();
		JSONObject onlineobj =myana.online_warn;
       // logger.warn(anaobj);

		 Set<String> save_set= onlineobj.keySet();

		 gno=0;
		 vno=0;






    	try {
			

			
			 



			 for(String key : save_set)
			 {

                 vv = jedis.get(key+"_.value");
				 logger.info(key+":"+vv);
                 if (!vv.isEmpty())
				 {
					 myana.checkTime(key,vv,dbcon.jdbcTemplate,ckt);

				 }
				//


		        
		      
		        
		     }



			 //logger.warn(sql);





			 //System.gc();
		         dbcon.getClose();
		         dbcon=null;

	        	
	       
	        
           } 
		
	     	catch (JedisConnectionException e) {
             e.printStackTrace();
           } 
	    	 catch (Exception e) {
	            e.printStackTrace();
	        }
		RedisUtil.close(jedis);
		jedis=null;



    }
    	
}
