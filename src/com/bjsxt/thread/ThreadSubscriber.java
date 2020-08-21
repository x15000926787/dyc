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

import com.dl.tool.AnaUtil;
import com.dl.tool.FirstClass;
import com.dl.tool.JedisUtil;
import org.apache.logging.log4j.Logger;
import org.springframework.jdbc.core.JdbcTemplate;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPubSub;
import redis.clients.jedis.exceptions.JedisConnectionException;
import java.sql.SQLException;
import java.util.List;


public class ThreadSubscriber  extends Thread {
	 private Thread t;
	 private boolean reconn=true;
	/* private boolean subbreak=true;*/
	 //RedisUtil jpool = new RedisUtil();
	  Jedis jedis = null;
	 //public Jedis jedis2 = null;
	// private Session thesesion;
	 //public Jedis ttjedis = null;
	//public Jedis tmjedis = null;
	 private String threadName;
	 //public ChatSocket ckt = new ChatSocket();
	 KeyExpiredListener myListener =null;  
	 //JedisPool pool = new JedisPool(new JedisPoolConfig(), "218.78.29.130", 6389, 10000,"Shcs123");
	 //SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//设置日期格式
	 private static final Logger logger = FirstClass.logger;
	 String pId = FirstClass.projectId;
	 //public ThreadPoolExecutor executor = null;
	 public ThreadSubscriber(String name){
		// thesesion = sess;
		 threadName = name;
		 logger.warn(threadName+":   start the subscriber thread");
		 Thread.currentThread().setName(threadName);
		 //executor = new ThreadPoolExecutor(10, 20, 60000, TimeUnit.MILLISECONDS,
		//		 new LinkedBlockingQueue<Runnable>(), Executors.defaultThreadFactory(),new ThreadPoolExecutor.DiscardOldestPolicy());
		// executor.allowCoreThreadTimeOut(true);
		 //ttjedis = RedisUtil.getJedis();
		// tmjedis = RedisUtil.getJedis(1);
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
	        	 jedis = JedisUtil.getInstance().getJedis();
	        	
	   	        config(jedis);


                     myListener= new KeyExpiredListener();

	   	      
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

	               // RedisUtil.close(jedis);
	               //jedis=null;


	            }
	        	 catch (ClassCastException e) {
	            	 logger.warn("Exception :", e.toString());
		            e.printStackTrace();
		        } 
	        	 catch(Exception e){
	        		 logger.error("Exception:", e.toString());
	            	e.printStackTrace();
	                

	            }

	        	 JedisUtil.getInstance().returnJedis(jedis);
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
		     //RedisUtil.close(ttjedis);
		     //ttjedis=null;
		  // RedisUtil.close(tmjedis);
		   //  tmjedis=null;
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
	//public ChatSocket skt;

	//public ThreadPoolExecutor executor;
	// public JSONObject objana = new JSONObject();
	// public JSONObject msg_user = new JSONObject();
	// public JSONObject msg_author = new JSONObject();
	// public JSONObject objcondition = new JSONObject();
	 //public DBConnection dbcon=new DBConnection();
	//public Jedis tjedis;
	//public Jedis mjedis;
	 public  JdbcTemplate jdbcTemplate;
	// MyTask myTask = new MyTask();
	 public  String mess = null;
	//public ScriptEngineManager scriptEngineManager = new ScriptEngineManager();
	//public ScriptEngine scriptEngine;// = scriptEngineManager.getEngineByName("nashorn");
	 //SendViaWs sendmsg = new SendViaWs();
	//public static AnaUtil myana=new AnaUtil();
	 private static final Logger logger = FirstClass.logger;
	 public KeyExpiredListener() throws SQLException{

		 //myana=FirstClass.myana;
		 //executor=texecutor;
		// jdbcTemplate=FirstClass.jdbcTemplate;
		 //logger.warn(myana.tjedis.get("un_1_.ai_0_0_.value"));
		// myana.tjedis=tjedis;
		 //myana.mjedis=mjedis;
		// logger.warn(tjedis.info());
		// scriptEngine = scriptEngineManager.getEngineByName("nashorn");
		// logger.warn(msg_user);
		// logger.warn(msg_author);
	 }
	 
    @Override
    public void onPSubscribe(String pattern, int subscribedChannels) {
    	logger.warn("onPSubscribe " + pattern + " " + subscribedChannels);

    	//https://www.cnblogs.com/dafanjoy/p/9729358.html

    }
    @Override
    public void onPUnsubscribe(String pattern, int subscribedChannels) {
    	//  RedisUtil.close(tjedis);
    	//  tjedis=null;
		//RedisUtil.close(mjedis);
		//mjedis=null;
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



		//String chan = channel.split(":")[1];
		//mess = message;
		//if ( message.indexOf("di_0")>0)
		//	logger.warn("rec："+channel+" : "+message);//channel,message,jdbcTemplate,skt
		Runnable noArguments = () -> {
			try {




				{
					try {
						if (channel.contains("expired") ) {
							//logger.warn(message);
							AnaUtil.handleExpired(message);
						}
						else {


							//logger.warn(message+","+channel);
							AnaUtil.handleMessage( message);

						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				}

			} catch (Exception e) {
				e.printStackTrace();
			}

		};
		FirstClass.executor.execute( noArguments);
				/**/
		//channel=null;
		//message=null;
		//logger.warn("rec："+channel+" : "+message);
		////mmmess = null;
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
