package com.dl.tool;

import com.alibaba.fastjson.JSONObject;
import com.bjsxt.server.ChatSocket;
import com.google.gson.Gson;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.quartz.*;
import org.springframework.jdbc.core.support.JdbcDaoSupport;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.Pipeline;
import redis.clients.jedis.Response;
import redis.clients.jedis.exceptions.JedisConnectionException;

import javax.script.ScriptException;
import java.io.IOException;
import java.util.*;
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
public  class UpdateDataJob<autoreleasepool> extends JdbcDaoSupport implements Job {
	   
	//public static ChatSocket ckt = new ChatSocket();
	  // RedisUtil jpool = new RedisUtil();
	
	


	   // public static final Logger logger = LogManager.getLogger(UpdateDataJob.class);


	    
	 
		
		
		
		public UpdateDataJob()
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
		if (!ChatSocket.getSockets().isEmpty())    //
		{
		//logger.warn("start update_data...");
		 String s = null;

		  String luaStr = null;
		 	 String kk = null;


		 String pattern=null;


		 Gson  gson=null;

		Jedis jedis = null;

		// DBConnection dbcon=null;//new DBConnection();
		  Map<String,String> result = null;
		 Map umap=null;
		  Map<String,Response<String>> responses = null;
		Pipeline p = null;//jedis.pipelined();
		//Set<String> sinter_yc= null;//anaobj.keySet();

		kk = "";
		pattern=".*_.value*";


		gson=new Gson();






		result = new HashMap<String,String>();
		umap=new HashMap<String, Map<String,String>>();

		//logger.warn("updatedata ...");


		//dbcon=new DBConnection();


    	  // DBConnection dbcon=new DBConnection();


		//JSONObject anaobj =myana.objana ;
		//logger.info(anaobj.toJSONString());
		//sinter_yc= AnaUtil.objana_v.keySet();

		//HashMap<String,String>  map = new HashMap<String,String>();


		//logger.error("ask data...");

		   responses = new HashMap<String,Response<String>>(AnaUtil.objana_v.keySet().size());


	      
			try {
				 jedis= JedisUtil.getInstance().getJedis();
				
				 p = jedis.pipelined();
				
				// SimpleDateFormat df = new SimpleDateFormat("YYMMdd");//设置日期格式
				// SimpleDateFormat df2 = new SimpleDateFormat("HHmmss");//设置日期格式
				// Calendar c = Calendar.getInstance();//可以对每个时间域单独修改
				// savet = (df.format(new Date()));// new Date()为获取当前系统时间


				
		         //String up,down,rtuno,sn;
		        result.clear();
		       
		        //使用pipeline hgetall  
		       
		        
		        //start = System.currentTimeMillis();
		        for(String key1 : AnaUtil.objana_v.keySet()) {

		         responses.put(key1+"_.value", p.get(key1+"_.value"));

		        }
				//logger.error("ask data 1...");
		        try {
		         if (p!=null)  p.sync();
		        }
				catch (JedisConnectionException e) {
					logger.error("lua err: " +  e.toString() );
		            e.printStackTrace();
		        }
		        catch (Exception e) 
				 {
				 e.printStackTrace();
				 }
				//添加实时ai,di 到json字符串中
				//logger.warn(responses);
				for(String k : responses.keySet()) {
                    //logger.warn(k);
					if (Pattern.matches(pattern, k))
					{
						try {
							//logger.warn("lua err: "+(k) +  responses.get(k).get().toString() );
							luaStr = responses.get(k).get().toString();

							result.put(k, luaStr);



							kk = k.split("\\.")[0];
							//logger.warn(kk);
							if (umap.containsKey(kk))
							{
								((Map)umap.get(kk)).put(k, luaStr);
							}
							else {
								Map<String,String> nvl = new HashMap<String,String>();
								nvl.put(k, luaStr);
								umap.put(kk,nvl);
								nvl=null;
							}
							//s = k.replace("_.value", "");
							//if (k.contains("ai")) myana.handleMaxMin(k.replace("_.value",""),luaStr,dbcon.jdbcTemplate);
							//myana.handleEvt(k.replace("_.value",""),luaStr,dbcon.jdbcTemplate,ckt);

							//myana.handleCondition(luaStr,k.replace("_.value",""),jedis);

						} catch (Exception e) {
							//logger.warn("实时库中没有："+k);
							//e.printStackTrace();
						}
					}  //if ch_
				}
				//logger.error("ask data 2...");
				/**
                //读取定时任务锁状态,谭小波项目不涉及do,ao,注销此过程
				//get_time_lock();
				*/

					s = gson.toJson(umap);
					//logger.warn(s);
				//if(!ChatSocket.sockets.isEmpty()) {
					try {
						ChatSocket.broadcast( s, -1);
					} catch (Exception e) {
						e.printStackTrace();
					}
				//}






				try {
					p.close();
				}catch (IOException ee){}



				JedisUtil.getInstance().returnJedis(jedis);
				// dbcon.getClose();
		         responses.clear();
		         result.clear();
		         umap.clear();



				// RedisUtil jpool = new RedisUtil();



				 s = null;

				 luaStr = null;
				 kk = null;

				//static  String savet=null;
				//static   String up,down,rtuno,sn;
				//static  SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//设置日期格式
				 pattern=null;

				//static String patterndnVtl=".*dn.*";
				 gson=null;
				// SqliteHelper h = null;

				 //myana=null;


				//String templatePath = UpdateDataJob.class.getClassLoader().getResource("/").getPath();
				//dbcon.getClose();
				//dbcon=null;//new DBConnection();
				result = null;
				 umap=null;
				 responses = null;
				 p = null;//jedis.pipelined();
				 //anaobj =null;
				// sinter_yc= null;//anaobj.keySet();

			 } 
			
			catch (JedisConnectionException e) {
	            e.printStackTrace();
	        } 
			 catch (Exception e) {
		            e.printStackTrace();
			}
		}
		//logger.warn("end update_data.");

    }
    	
}
