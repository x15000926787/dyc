package com.dl.quartz;

import com.dl.tool.RedisUtil;
import com.google.common.io.CharStreams;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.quartz.*;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.exceptions.JedisConnectionException;

import java.io.InputStreamReader;
import java.io.Reader;
import java.util.HashMap;
/**
 * [任务类]
 * @author xx
 * @date 2019-8-20 
 * @copyright copyright (c) 2018
 */
@PersistJobDataAfterExecution
@DisallowConcurrentExecution
public  class LuaJob implements Job {
	   
	    Jedis jedis = null;
	   private static final Logger logger = LogManager.getLogger(LuaJob.class);
	   String s = null;
	      String vals = null;
	      String key = null;
	      String luaStr = null;
	     // String pId = FirstClass.projectId;
	  //	DBConnection dbcon=null;//
	  //	PreparedStatement pstmt=null;
		//ResultSet rs;
		
		
		
		public LuaJob() {
			//static
			//logger.warn("Myjob.jpool:"+jpool.toString());
				//logger.warn(taskarray);
			
		}
		
    @Override
    public void execute(JobExecutionContext arg0) throws JobExecutionException {
       //

    	
   	 
    	HashMap<String, Object> taskdetial = (HashMap<String, Object>) (arg0.getJobDetail().getJobDataMap().get("taskdetial"));
    	
    	String jobtype="2"; 
    	switch (jobtype) {
    	case "1":

    		break;
		case "2":
					String luaname =(String)taskdetial.get("luaname"); 
			    	logger.info("执行定时脚本任务： "+luaname);
					try {
						 
						
							
						 jedis = RedisUtil.getJedis();
				        	Reader r = new InputStreamReader(LuaJob.class.getResourceAsStream(luaname));
				            luaStr = CharStreams.toString(r);
				            jedis.eval(luaStr);
				            r.close();
				            r=null;
				            RedisUtil.close(jedis);
				            jedis=null;
				            logger.info("定时脚本任务执行完成。");
					  }
				     	catch (JedisConnectionException e) {
			             e.printStackTrace();
			           } 
				    	 catch (Exception e) {
				            e.printStackTrace();
				        }
					
			
			break;

		default:
			break;
		}
    	
    	//taskdetial.clear();
    	//taskdetial=null;

    }
    	
}
