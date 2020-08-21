package com.dl.tool;

import com.alibaba.fastjson.JSONObject;
import com.bjsxt.server.ChatSocket;
import com.google.gson.Gson;
import org.joda.time.DateTime;
import org.junit.Test;
import org.quartz.*;
import org.springframework.jdbc.core.support.JdbcDaoSupport;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.Pipeline;
import redis.clients.jedis.Response;
import redis.clients.jedis.exceptions.JedisConnectionException;

import javax.script.ScriptException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * [任务类]
 * @author xx
 * @date 2019-8-20 
 * @copyright copyright (c) 2018
 * 计算zjdy.txt
 * 计算电量增量
 * 发送 实时数据 and 定时锁状态 到前端
 */
@PersistJobDataAfterExecution
@DisallowConcurrentExecution
public  class devZF<autoreleasepool> extends JdbcDaoSupport implements Job {

	//public static ChatSocket ckt = new ChatSocket();
	  // RedisUtil jpool = new RedisUtil();




	   // public static final Logger logger = LogManager.getLogger(UpdateDataJob.class);







		public devZF()
		{

			//scriptEngine = scriptEngineManager.getEngineByName("nashorn");
		}
		 /**
	     * 检查遥测越限
	     * @param 
	     * @throws ScriptException
		  * 已经弃用
	     */  
	   /*
	   public int checkupdown(String val,String down,String up,int stat) {
		   int st=-2;
		  // logger.warn("check updown : " +  val+":"+  updown+":"+  stat );
		   String ud[]=new String[2];
		   ud[0] = down; 
		   ud[1] = up;
		   ud[1]="254";
		   ScriptEngineManager scriptEngineManager = new ScriptEngineManager();
	        ScriptEngine scriptEngine = scriptEngineManager.getEngineByName("nashorn");
	       try
	       {
	    	   if ((boolean) scriptEngine.eval(val+"<"+ud[0]) && stat>-1)
		    	   st = -1;
		       if ((boolean) scriptEngine.eval(val+">"+ud[1]) && stat<1)
		    	   st = 1; 
		       if ((boolean) scriptEngine.eval(val+">="+ud[0])&&(boolean) scriptEngine.eval(val+"<="+ud[1]) && stat!=0)
		    	   st = 0; 
   
	       }catch (ScriptException e) {
	        	logger.error("check updown err: " +  e.toString() );
	            e.printStackTrace();
	        }

		return st;
		
	} */
	   /**
	     * 检查遥信变位
	     * @param 
	     * @throws ScriptException
		* 已经弃用
	     */  
	   /*
	   public int checkevt(String val,int stat) {
		   int st = 0;
		   if (stat!=-1)
		   {
		   ScriptEngineManager scriptEngineManager = new ScriptEngineManager();
	        ScriptEngine scriptEngine = scriptEngineManager.getEngineByName("nashorn");
	       try
	       {
	    	  
		       if ((boolean) scriptEngine.eval(val+"!="+stat) )
		    	   st = 1; 
		      
  
	       }catch (ScriptException e) {
	        	logger.error("check updown err: " +  e.toString() );
	            e.printStackTrace();
	        }
		   }
		return st;
		
	} */
	    /*
	     * 插入记录
	     *

	  public void add_red(String sql) throws SQLException
	  {

		  DBConnection dbcon=new DBConnection();

		 // logger.warn(sql);
		  try{
			  dbcon.setPreparedStatement(sql);
			  int t=dbcon.getexecuteUpdate();
	          //dbcon.getClose();
			}catch(Exception e){
				logger.error("出错了"+e.toString());
			}
		  dbcon.getClose();
	  }*/
	    /*
	     * 读取定时锁状态
	     *
		 */
	  public void get_time_lock()
	  {

		 /* List<Map<String, Object>> tasktype1 = new ArrayList<Map<String, Object>>();
		  String sql = "SELECT * from do_info where timeTaskLock is not null";
		  tasktype1= dbcon.queryforList(sql);


		  int size=tasktype1.size() ;
		  if (size> 0)
		  {

			  for (int i = 0; i<size; i++)
			  {

				  Map listData = (Map)tasktype1.get(i);
				  result.put((listData.get("kkey").toString()+".lock"),listData.get("timeTaskLock").toString());
				  kk = listData.get("kkey").toString().split(",")[0];
				  if (umap.containsKey(kk))
				  {
				  	((Map)umap.get(kk)).put((listData.get("kkey").toString()+".lock"),listData.get("timeTaskLock").toString());
				  }
				  else {
					  Map<String,String> nvl = new HashMap<String,String>();
					  nvl.put((listData.get("kkey").toString()+".lock"),listData.get("timeTaskLock").toString());
				  	 umap.put(kk,nvl);
				  }
			  }


		  }*/

	  }

    @Override
    public void execute(JobExecutionContext arg0) throws JobExecutionException {
		AnaUtil.dev_array.clear();
		Jedis jedis = null;
		jedis= JedisUtil.getInstance().getJedis();
		for (Map.Entry<String, Object> entry : AnaUtil.dev_author.entrySet()) {
			//System.out.println("key值="+entry.getKey());
			//System.out.println("对应key值的value="+entry.getValue());
			 JSONObject tv = new JSONObject();
			Map<String,Object> tmap = (Map<String, Object>) entry.getValue();
			//FirstClass.logger.warn(tmap);
			tv.put("deviceId",tmap.get("deviceno"));
			tv.put("deviceName",tmap.get("name"));
			tv.put("updateTime",AnaUtil.getmiseconds(new DateTime()));
			if (tmap.containsKey("voltage_A"))
			{
				tv.put("voltage_A",jedis.get(tmap.get("voltage_A")+"_.value"));
			}
			if (tmap.containsKey("voltage_B"))
			{
				tv.put("voltage_B",jedis.get(tmap.get("voltage_B")+"_.value"));
			}
			if (tmap.containsKey("voltage_C"))
			{
				tv.put("voltage_C",jedis.get(tmap.get("voltage_C")+"_.value"));
			}
			if (tmap.containsKey("electricCurrent_A"))
			{
				tv.put("electricCurrent_A",jedis.get(tmap.get("electricCurrent_A")+"_.value"));
			}
			if (tmap.containsKey("electricCurrent_B"))
			{
				tv.put("electricCurrent_B",jedis.get(tmap.get("electricCurrent_B")+"_.value"));
			}
			if (tmap.containsKey("electricCurrent_C"))
			{
				tv.put("electricCurrent_C",jedis.get(tmap.get("electricCurrent_C")+"_.value"));
			}
			AnaUtil.dev_array.add(tv);
		}
		JedisUtil.getInstance().returnJedis(jedis);
		//FirstClass.logger.warn(AnaUtil.dev_array);
    }

}
