package com.dl.tool;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import redis.clients.jedis.Jedis;

import java.text.SimpleDateFormat;
import java.util.Set;

//import com.dl.impl.SqliteHelper;

/**
 * 用来处理redis库中的延时任务
 * 现已弃用
 */
public class DelayWork extends Thread {
	   private Thread t;
	   private String threadName; 
	   String vals[] = null;
	   String jobs[] = null;
	   

	  // private Session thesesion;


    /**
     * 设置日期格式
     * SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
     */




	   private  static Jedis jedis = null;
	   private static final Logger logger = LogManager.getLogger(DelayWork.class);
	  
	   int cnt = 30 ;
//	   private  String[] val_yc =new String[1000];
//	   Random rand = new Random();
	   public  DelayWork(String name) {
	      threadName = name;
	    //  thesesion = sess;

	      Thread.currentThread().setName(threadName);
	     // logger.warn("DelayWork.jpool:"+uname);
	    
	     
	   }
	  
	    /**
	     * 批量读取
	     * @param jedis
	     */
	   
	    
	    
	    
	    
	   @Override
       public void run() {
	      logger.warn("Running " +  threadName );
	      String s = null;
	     

	         while(true) {

					try {
						 
						 jedis = RedisUtil.getJedis();
						// Pipeline p = jedis.pipelined(); 
						
						 
						 
						 
						 try {
							 if (jedis.exists("delay_job"))
							 {
								 Set<String> ids = jedis.zrangeByScore("delay_job", 0,System.currentTimeMillis());
								 SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//设置日期格式
								
								 //logger.warn(df.format(new Date()),System.currentTimeMillis()); 
								 if(!ids.isEmpty()){
									
									 for(String id:ids){
										// logger.warn(id); 
													 Long count = jedis.zrem("delay_job", id);
				
													 if(count!=null&&count==1){
														jobs = id.split(",");
													    for(String job:jobs) {
													    	vals = job.split(":");
													    	jedis.set(vals[0]+"_.value",vals[1]);
													    	jedis.set(vals[0]+"_.status","1");
													    }
				
													 }
	
										 }
	
								 }
								 ids.clear();
								 ids=null;
							 }
							

							 } 
						 catch (Exception e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
							 
						 
						 
				       // Map<String,String> result = new HashMap<String,String>();
				      //  result.clear();
				       
				       /* //使用pipeline hgetall  
				        Map<String,Response<String>> responses = new HashMap<String,Response<String>>(sinter_yc.size());
				        
				        //start = System.currentTimeMillis();
				        for(String key : sinter_yc) {
				         responses.put(key, p.get(key));
				        }*/
				        
				       // p.sync();//同步
				        RedisUtil.close(jedis);
				        
				        jedis=null;
				       
				      

					     // logger.warn("Running " +  ckt.getSockets().toString() );

				       // end = System.currentTimeMillis();
				      //  System.out.println("result size:[" + result.size() + "] ..");
				      //  System.out.println("hgetAll with pipeline used [" + (end - start) / 1000 + "] seconds ..");
                      
						
				       
				   
						//if (cnt==10)
				       //  responses=null;
				        // result=null;
						
						cnt++;
						
						//}
					//	sList = xCell;	
			            
			           // 
						
						
					
					
				
				
//	            for (int i=0;i<1000;i++) 
//					val_yc[i] = ""+rand.nextInt(100); 
//				
				
				//map1.clear();
				//map1.put("data", val_yc);
		       // map1.put("res",message);  
		      //  map1.put("rdd",sList);
		       // map1.put("edd",eList);
		       //logger.warn(threadName+":"+cnt);
	         /*   try {
	            	if (thesesion.isOpen())
	            	//thesesion.getBasicRemote().sendText(gson.toJson(map1));
	            	synchronized(thesesion){

	            		//thesesion.getBasicRemote().sendText(gson.toJson(map1));
	            		thesesion.getBasicRemote().sendText(s);
	            	}
				} catch (IOException e) {
					e.printStackTrace();
				}*/
				
	            // 让线程睡眠一会
	            Thread.sleep(1000);
	          
	        //    sList.clear();
	  		//  xCell.clear();
	  		//  map1.clear();
	  		 
	         
	      }catch (InterruptedException e) {
	         logger.warn("Thread " +  Thread.currentThread().getName() + " interrupted.");
	      }
	     // h.close();
	     
	   //   sList = null;
        //  xCell = null;
		//  map1 = null;
		 
		  
	      
	   }           //while
	        // logger.warn("Thread " +  threadName + " exiting.");
	   }
	   @Override
       public void start () {
	      logger.warn("Starting " +  threadName );
	      if (t == null) {
	         t = new Thread (this, threadName);
	         t.start ();
	      }
	   }
	   public  boolean killThreadByName(String name) {
		   
		   
		   
		   try {
				 

		         // jedis.close();

				
				} catch (Exception e) {
					// TODO: handle exception
				}
		   
			ThreadGroup currentGroup = Thread.currentThread().getThreadGroup();
	 
			int noThreads = currentGroup.activeCount();
	 
			Thread[] lstThreads = new Thread[noThreads];
	 
			currentGroup.enumerate(lstThreads);
	 
			//logger.warn("现有线程数" + noThreads);
	 
			for (int i = 0; i < noThreads; i++) {
	 
				String nm = lstThreads[i].getName();
	 
				
	 
				if (nm.equals(name)) {
					logger.warn("关闭线程："  +  nm);
					lstThreads[i].interrupt();
	 
					return true;
	 
				}
	 
			}
	 
			return false;
	 
		}
	
	  
	}
	 
	/*public class TestThread {
	 
	   public static void main(String args[]) {
	      ThreadDemo T1 = new ThreadDemo( "Thread-1");
	      T1.start();
	      
	      ThreadDemo T2 = new ThreadDemo( "Thread-2");
	      T2.start();
	   }  
	    
	}
*/