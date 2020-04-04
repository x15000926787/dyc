package com.dl.quartz;

import com.dl.tool.RedisUtil;
import com.google.common.io.CharStreams;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.exceptions.JedisConnectionException;

import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * [任务类]
 * @author xx
 * @date 2019-8-20 
 * @copyright copyright (c) 2018
 */
public  class MyJob implements Job {
	   
	    Jedis jedis = null;
	   private static final Logger logger = LogManager.getLogger(MyJob.class);
	   String s = null;
	      String vals = null;
	      String key = null;
	      String luaStr = null;
	  //	DBConnection dbcon=null;//
	  //	PreparedStatement pstmt=null;
		//ResultSet rs;
		
		
		
		public MyJob() {
			//static
			//logger.warn("Myjob.jpool:"+jpool.toString());
				//logger.warn(taskarray);
			
		}
		
    @Override
    public void execute(JobExecutionContext arg0) throws JobExecutionException {
       //

    	
   	 
    	List<Map<String, Object>> taskdetial = new ArrayList<Map<String, Object>>();
    	
    	taskdetial=(List<Map<String, Object>>) (arg0.getJobDetail().getJobDataMap().get("taskdetial"));
    	
    	String jobtype=arg0.getTrigger().getJobKey().getGroup(); 
    	switch (jobtype) {
    	case "1":
		    		try {
		    			logger.warn(taskdetial);
						 jedis = RedisUtil.getJedis();
						// Pipeline p = jedis.pipelined(); 
						
				        	
				        	for(Map<String, Object> job:taskdetial)
				        	{
				        		key = job.get("kkey").toString();
				        		vals = job.get("val").toString();
				        		jedis.set(key+"_.value",vals);
				        	    jedis.set(key+"_.status","1");
				        	}
				        	 //p.sync();//同步
				         // p.close(); 
				        	 RedisUtil.close(jedis);
				        	 jedis=null;
						// p.close();	
				         
				        
		               } 
					
				     	catch (JedisConnectionException e) {
			             e.printStackTrace();
			           } 
				    	 catch (Exception e) {
				            e.printStackTrace();
				        }
    		break;
		case "2":
					String luaname =(String)taskdetial.get(0).get("luaname"); 
			    	logger.warn(luaname);
					try {
						 
						
							
						 jedis = RedisUtil.getJedis();
				        	Reader r = new InputStreamReader(MyJob.class.getResourceAsStream(luaname));
				            luaStr = CharStreams.toString(r);
				             jedis.eval(luaStr);
				            r.close();
				            r=null;
				            RedisUtil.close(jedis);
				            jedis=null;
					  }
				     	catch (JedisConnectionException e) {
			             e.printStackTrace();
			           } 
				    	 catch (Exception e) {
				            e.printStackTrace();
				        }
					logger.info("lua work ok");
			
			break;

		default:
			break;
		}
    	
    	taskdetial.clear();
    	taskdetial=null;

    }
    	
}
