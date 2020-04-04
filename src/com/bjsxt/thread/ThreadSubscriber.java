/**  
* Title: ThreadSubscriber.java  
* Description: 
* @author：xx 
* @date 2019-6-18
* @version 1.0
* Company: www.zoutao.info
*/ 
package com.bjsxt.thread;

/**
 *@class_name：ThreadSubscriber
 *@comments:
 *@param:
 *@return: 
 *@author:xx
 *@createtime:2019-6-18
 */

import com.alibaba.fastjson.JSONObject;
import com.bjsxt.server.ChatSocket;
import com.dl.tool.FirstClass;
import com.dl.tool.MyTask;
import com.dl.tool.RedisUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.jdbc.core.JdbcTemplate;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPubSub;
import redis.clients.jedis.exceptions.JedisConnectionException;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class ThreadSubscriber  extends Thread {
	 private Thread t;
	 private boolean reconn=true;
	/* private boolean subbreak=true;*/
	 //RedisUtil jpool = new RedisUtil();
	  Jedis jedis = null;
	 //public Jedis jedis2 = null;
	// private Session thesesion;
	 private String threadName;
	 public ChatSocket ckt = new ChatSocket();
	 KeyExpiredListener myListener =null;  
	 //JedisPool pool = new JedisPool(new JedisPoolConfig(), "218.78.29.130", 6389, 10000,"Shcs123");
	 SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//设置日期格式
	 private static final Logger logger = LogManager.getLogger(ThreadSubscriber.class);
	 String pId = FirstClass.projectId;
	 public ThreadPoolExecutor executor = null;
	 public ThreadSubscriber(String name){
		// thesesion = sess;
		 threadName = name;
		 logger.warn(threadName+":   start the subscriber thread");
		 Thread.currentThread().setName(threadName);
		 executor = new ThreadPoolExecutor(5, 10, 200, TimeUnit.MILLISECONDS,
				 new LinkedBlockingQueue<Runnable>(), Executors.defaultThreadFactory(),new ThreadPoolExecutor.DiscardOldestPolicy());
		 //logger.warn("ThreadSubscriber.jpool:"+jpool.toString());
		// timer = new Timer();
		
	 }
	 private static void config(Jedis jediss){
	        String parameter = "notify-keyspace-events";
	        List<String> notify = jediss.configGet(parameter);
	        logger.warn(notify.toString());

	        if(notify.get(1).indexOf("$xE")<0){
	        	 logger.warn(notify.toString());
	        jediss.configSet(parameter, "$xE");
	        logger.warn(notify.toString());
	        }
	    }

	  public void run() {
		  logger.warn("Running thread_subscriber"  );
	     // Thread.currentThread().setName("thread_subscriber"+thesesion);
	    	//jedis2 = jpool.getJedis();
	        while(reconn){
	        	 try {
	        	 jedis = RedisUtil.getJedis();
	        	
	   	        config(jedis);  
	   	    
	   	        myListener= new KeyExpiredListener(ckt,executor);
	   	    
	   	      
	 	    /* timer.schedule(new TimerTask() 
	 	       {
	             public void run() {
	            	// if (subbreak)  myListener.punsubscribe("__key*__:*"); 
	             }
	            }, 60000, 60000);//  
*/
	   	     //jedis.set("subscriber.stat",df.format(new Date()));
	        	 try {
	             	jedis.psubscribe(myListener, "__keyevent@"+pId+"__:*");
	 			} 
	        	 catch (JedisConnectionException e){
	        		 e.printStackTrace();
	                logger.warn("Exception :", e.toString());

	                RedisUtil.close(jedis); 
	               jedis=null;

	            }
	        	 catch (ClassCastException e) {
	            	 logger.warn("Exception :", e.toString());
		            e.printStackTrace();
		        } 
	        	 catch(Exception e){
	        		 logger.error("Exception:", e.toString());
	            	e.printStackTrace();
	                

	            }
	        	
	        	 
	          /*  try{
	            	 logger.warn("waiting for 2s");
	                Thread.sleep(2000);

	            }catch(Exception unused){

	            }*/
	        	  }catch (Exception e) {
	    	    	  logger.warn("Thread " +  Thread.currentThread().getName() + " interrupted."+e.toString());
	    		  }
	        }

		 // executor.shutdown();
	      logger.warn("Thread " +  threadName + " exiting.");
	        
	  }
	   
	   public void start () {
		   logger.warn("Starting " +  threadName );
	      if (t == null) {
	         t = new Thread (this, threadName);
	         t.start ();
	      }
	   }
	   public void stoplisten () {
		   logger.warn("Stop " +  "thread_subscriber" );
		   reconn= false;
		   myListener.punsubscribe("__keyevent@"+pId+"__:*");
		   executor.shutdown();
		     /* try {
				  if (jedis != null) {

		              jedis.close();

			          }
				  } catch (JedisException e) {
						e.printStackTrace();
					}catch (Exception e) {
						e.printStackTrace();
					}*/
		   }
	   public  boolean killThreadByName(String name ) {
		    stoplisten ();
			ThreadGroup currentGroup = Thread.currentThread().getThreadGroup();
	 
			int noThreads = currentGroup.activeCount();
	 
			Thread[] lstThreads = new Thread[noThreads];
	 
			currentGroup.enumerate(lstThreads);
	 
			System.out.println("现有线程数" + noThreads);
	 
			for (int i = 0; i < noThreads; i++) {
	 
				String nm = lstThreads[i].getName();
	 
				System.out.println("线程号：" + i + " = " + nm);
	 
				if (nm.equals(name)) {
					logger.warn("关闭线程："  +  nm);
					lstThreads[i].interrupt();
	 
					return true;
	 
				}
	 
			}
	 
			return false;
	 
		}
	   /*
	   static class MyTask() extends TimerTask { 
		   @Override
		   public void run() {
			  // jedis.set("subscriber.stat",df.format(new Date()));
		   }
		   }*/
}


class KeyExpiredListener extends JedisPubSub {
	public ChatSocket skt;

	public ThreadPoolExecutor executor;
	// public JSONObject objana = new JSONObject();
	// public JSONObject msg_user = new JSONObject();
	// public JSONObject msg_author = new JSONObject();
	// public JSONObject objcondition = new JSONObject();
	 //public DBConnection dbcon=new DBConnection();
	 public Jedis tjedis = null;
	 public static JdbcTemplate jdbcTemplate;
	//public ScriptEngineManager scriptEngineManager = new ScriptEngineManager();
	//public ScriptEngine scriptEngine;// = scriptEngineManager.getEngineByName("nashorn");
	 //SendViaWs sendmsg = new SendViaWs();

	 private static final Logger logger = LogManager.getLogger(KeyExpiredListener.class);
	 public KeyExpiredListener(ChatSocket sskt,ThreadPoolExecutor texecutor) throws SQLException{
		 skt = sskt;
		 executor=texecutor;
		 jdbcTemplate=FirstClass.jdbcTemplate;
		// scriptEngine = scriptEngineManager.getEngineByName("nashorn");
		// logger.warn(msg_user);
		// logger.warn(msg_author);
	 }
	 
    @Override
    public void onPSubscribe(String pattern, int subscribedChannels) {
    	logger.warn("onPSubscribe " + pattern + " " + subscribedChannels);
    	tjedis = RedisUtil.getJedis();
    	//https://www.cnblogs.com/dafanjoy/p/9729358.html

    }
    @Override
    public void onPUnsubscribe(String pattern, int subscribedChannels) {
    	  RedisUtil.close(tjedis);
    	  tjedis=null;

    	/*try {
    		if (tjedis != null) {

                tjedis.close();

            }
		} catch (Exception e) {
			// TODO: handle exception
		}*/
    	 
    	//logger.warn("onPUnsubscribe close " + pattern + " " + subscribedChannels);
    }
   

    @Override
    public void onPMessage(String pattern, String channel, String message) {

		String val = "0",nval = "";
		String luaStr = null;

		channel = channel.split(":")[1];

		//if ( message.indexOf("di_0")>0)
		//	logger.warn("rec："+channel+" : "+message);
		{
			MyTask myTask = new MyTask(channel,message,jdbcTemplate,skt);
			//if (executor.getQueue().size()<100)
				executor.execute(myTask);
			//logger.warn("线程池中线程数目："+executor.getPoolSize()+"，队列中等待执行的任务数目："+executor.getQueue().size()+"，已执行完毕的任务数目："+executor.getCompletedTaskCount());
		}
		//logger.warn("rec："+channel+" : "+message);
		/*


        */



		/**
		 * 通知更新,目前采用修改redis中的anaupdate,authorupdate,conditionupdate 三个键值的方法
		 * redis消息队列？
		 */
		/*


    	*/
    }
//add other Unimplemented methods
}
