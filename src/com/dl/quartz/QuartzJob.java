package com.dl.quartz;

import com.dl.tool.DBConnection;
import com.dl.tool.RedisUtil;
import net.sf.json.JSONObject;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.quartz.*;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.exceptions.JedisConnectionException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * [任务类]
 * @author xx
 * @date 2019-8-20 
 * @copyright copyright (c) 2018
 */

@PersistJobDataAfterExecution
@DisallowConcurrentExecution
public  class QuartzJob implements Job {
	   
	    Jedis jedis = null;
	   private static final Logger logger = LogManager.getLogger(QuartzJob.class);
	   String s = null;
	      String vals = null;
	      String keys = null;
	      String luaStr = null;

	     // String pId = FirstClass.projectId;
	  //	DBConnection dbcon=null;//
	  //	PreparedStatement pstmt=null;
		//ResultSet rs;
		
		
		
		public QuartzJob() {
			//static
			//logger.warn("Myjob.jpool:"+jpool.toString());
				//logger.warn(taskarray);
			
		}
	/*
	 * 读取定时任务清单
	 *
	 */
	public Map<String, String> get_timetask_detial(String tid)
	{
		DBConnection dbcon=new DBConnection();
		List<Map<String, Object>> tasktype1 = new ArrayList<Map<String, Object>>();
		String sql = "SELECT a.kkey kkey,a.val val from timetask_detial a,do_info b where a.kkey=b.kkey and b.timeTaskLock=1 and  a.taskid="+tid;
		tasktype1= dbcon.queryforList(sql);
		HashMap<String, String> tmap = new HashMap<String, String>();

		int size=tasktype1.size() ;
		if (size> 0)
		{

			for (int i = 0; i<size; i++)
			{

				Map listData = (Map)tasktype1.get(i);
				tmap.put((listData.get("kkey").toString()),listData.get("val").toString());

			}


		}
		dbcon.getClose();
		return tmap;
	}
    @Override
    public void execute(JobExecutionContext arg0) throws JobExecutionException {
       //



    	HashMap<String, String> tidmap = (HashMap<String, String>) (arg0.getJobDetail().getJobDataMap().get("taskdetial"));
		//logger.warn(tidmap.get("vv").toString());
		HashMap<String, String> tmap = (HashMap<String, String>) get_timetask_detial(tidmap.get("vv").toString());
    	/*Map tmap=new HashMap<String, String>();
    	
    	tmap=(Map<String, String>) (arg0.getJobDetail().getJobDataMap().get("taskdetial"));
    	*/
    	String jobtype="1"; 
    	switch (jobtype) {
    	case "1":
		    		try {
		    			
						 jedis = RedisUtil.getJedis();
						// Pipeline p = jedis.pipelined(); 

						 //JSONObject jsonObject = JSONObject.fromObject(tmap.get("vv").toString());
						    
					       
					    	
					    	for (Object key:tmap.keySet()) {
					    		vals = tmap.get((String) key);
    			    			jedis.set(key+"_.value",vals);
				        	    jedis.set(key+"_.status","1"); 
					    		logger.warn("执行定时任务:  "+key+"  : "+vals);
    			    		}
				        	
				        	/*for(Map<String, Object> job:tmap)
				        	{
				        		key = job.get("kkey").toString();
				        		vals = job.get("val").toString();
				        		
				        	}*/
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
					/*String luaname =(String)taskdetial.get(0).get("luaname"); 
			    	logger.warn(luaname);
					try {
						 
						
							
						 jedis = RedisUtil.getJedis();
				        	Reader r = new InputStreamReader(QuartzJob.class.getResourceAsStream(luaname));
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
				        }*/
					logger.info("lua work ok");
			
			break;

		default:
			break;
		}
    	
    	//tmap.clear();
    	//tmap=null;
		logger.warn("执行定时任务:  QuartzJob done!");

    }
    	
}
