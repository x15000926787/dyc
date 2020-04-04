package com.dl.tool;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream; 
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.SocketException;
import java.util.Iterator;
import java.util.Properties; 

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.sqlite.JDBC;

import com.alibaba.druid.sql.visitor.functions.Now;
import com.alibaba.fastjson.JSON;
import com.mysql.jdbc.PreparedStatement;




import com.dl.tool.ToolSql;
import com.google.common.io.CharStreams;

import org.junit.After;
import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.Pipeline;
import redis.clients.jedis.Response;
import redis.clients.jedis.exceptions.JedisConnectionException;
import redis.clients.jedis.exceptions.JedisException;



//@Controller
public class ThreadLua extends Thread {
	   private Thread t;
	   private String threadName;
	   //private String url = "jdbc:mysql://localhost:3306/scada";
       // jdbc协议:数据库子协议:主机:端口/连接的数据库   //
	   static SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	   //private String user = "shcs";//用户名
	  // private String password = "@Shcs123";//密码
	   RedisUtil jpool = new RedisUtil();
	   private  static Jedis jedis = null;
	   private static final Logger logger = LogManager.getLogger(ThreadLua.class);
    
    
//	   private  String[] val_yc =new String[1000];
//	   Random rand = new Random();
      
	   public  ThreadLua(String name) {
	      threadName = name;
	      logger.info("Creating " +  threadName );
	      //logger.info(System.getProperty("user.dir"));//user.dir指定了当前的路径
	   
	   }
	 
	  
	    
	   
	  
	   public void run() {
		   logger.warn("Running " +  threadName );
		      String s = null;
		      String vals = null;
		      String luaStr = null;

		    
		      try {
		         while(true) {
		           

		        	
						try {
							 
							 jedis = jpool.getJedis();
								 try {
					        	
					        	Reader r = new InputStreamReader(ThreadLua.class.getResourceAsStream("jxkj.lua"));
					            luaStr = CharStreams.toString(r);
					            if (jedis!=null)   jedis.eval(luaStr);
					            r.close();
					            r=null;
					            //System.out.println(result);
					        } 
					        catch (SocketException e) {
					        	logger.error("lua err: " +  e.toString() );
					            e.printStackTrace();
					        }
					        catch (IOException e) {
					        	logger.error("lua err: " +  e.toString() );
					            e.printStackTrace();
					        }
					       
					        try {							 
					        	     
							          jedis.close();	
							     
								} catch (JedisException e) {
									e.printStackTrace();
								}catch (Exception e) {
									e.printStackTrace();
								}
					       
					        
			
							
						 } 
						
						catch (JedisConnectionException e) {
				            e.printStackTrace();
				        } 
						 catch (Exception e) {
					            e.printStackTrace();
					        }
						
					
					
//		            for (int i=0;i<1000;i++) 
//						val_yc[i] = ""+rand.nextInt(100); 
//			
					
		            // 让线程睡眠一会
		            Thread.sleep(10000);
		    
		  		 
		         }
		      }catch (InterruptedException e) {
		         logger.warn("Thread " +  Thread.currentThread().getName() + " interrupted.");
		      }finally {

		    	 
		      }
		     // h.close();
		
			 
			  
		      logger.warn("Thread " +  threadName + " exiting.");
		   }
	   
	   public void start () {
	      logger.info("Starting " +  threadName );
	      if (t == null) {
	         t = new Thread (this, threadName);
	         t.start ();
	      }
	   }
	   public  boolean killThreadByName(String name) {
		   
			ThreadGroup currentGroup = Thread.currentThread().getThreadGroup();
	 
			int noThreads = currentGroup.activeCount();
	 
			Thread[] lstThreads = new Thread[noThreads];
	 
			currentGroup.enumerate(lstThreads);
	 
			//logger.info("现有线程数" + noThreads);
	 
			for (int i = 0; i < noThreads; i++) {
	 
				String nm = lstThreads[i].getName();
	 
				//logger.info("线程号：" + i + " = " + nm);
	 
				if (nm.equals(name)) {
				
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