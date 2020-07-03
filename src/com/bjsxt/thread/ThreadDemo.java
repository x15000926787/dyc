package com.bjsxt.thread;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.SocketException;

import java.rmi.server.SocketSecurityException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import oracle.net.aso.s;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import javax.websocket.OnClose;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.dl.impl.UserDAOImpl;
import com.dl.tool.*;
import com.alibaba.fastjson.JSONObject;
import com.bjsxt.server.ChatSocket;
import com.bjsxt.vo.RowMapper;
import com.google.common.collect.Lists;
import com.google.common.io.CharStreams;
import com.google.gson.Gson;
import com.sun.org.apache.bcel.internal.generic.NEW;
//import com.dl.impl.SqliteHelper;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.Pipeline;
import redis.clients.jedis.Response;
import redis.clients.jedis.exceptions.JedisConnectionException;
import redis.clients.jedis.exceptions.JedisException;
public class ThreadDemo extends Thread {
	public Thread t;
	public  String threadName; 
	  // private Session thesesion;
	public ChatSocket ckt = new ChatSocket();
	  // RedisUtil jpool = new RedisUtil();
	public  JSONObject objana = new JSONObject();
	public  JSONObject objdig = new JSONObject();
	   Map<String,String> updownstat = null;
	   Map<String,String> evtstat = null;
	   SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//设置日期格式
   
	   public Gson  gson=new Gson();
	  // SqliteHelper h = null;
	    Jedis jedis = null;
	    public static final Logger logger = LogManager.getLogger(ThreadDemo.class);
	   Set<String> sinter_yc =  null;
	  // Set<String> sinter_yx =  null;
	   int cnt = 0 ;
	   JSONObject anainfo = new JSONObject();
	   //Map<String,String> updown = null;//new HashMap<String,String>(sinter_yc.size());
	   //Map<String,String> anasavmap = null;
	   //Map<String,String> anachxhmap = null;
	   //Map<String,String> digchxhmap = null;
	  
//	   private  String[] val_yc =new String[1000];
//	   Random rand = new Random();
	   public  ThreadDemo(String name) {
	      threadName = name;
	    //  thesesion = sess;
	      logger.warn(threadName+":   start the  threaddemo");
	      Thread.currentThread().setName(threadName);
	      DBConnection dbcon=new DBConnection();
		   PreparedStatement pstmt=null;
		   ResultSet rs;
		   String key=null;
			 Object value=null;
	     // logger.warn("ThreadDemo.jpool:"+jpool.toString());
	     /* try
	        {
	         //连接SQLite的JDBC
	         Class.forName("org.sqlite.JDBC");       
	         //建立一个数据库名lincj.db的连接，如果不存在就在当前目录下创建之
	         Connection conn = DriverManager.getConnection("jdbc:sqlite:C:/cstation/w32/ch_0.rdb");  
	         //Connection conn = DriverManager.getConnection("jdbc:sqlite:path(路径)/lincj.db");
	         Statement stat = conn.createStatement(); 
	         stat.executeUpdate( "drop table if exists table1;" );//创建一个表，两列      
	         stat.executeUpdate( "create table table1(name varchar(64), age int);" );//创建一个表，两列      
	         stat.executeUpdate( "insert into table1 values('aa',12);" ); //插入数据
	         stat.executeUpdate( "insert into table1 values('bb',13);" );
	         stat.executeUpdate( "insert into table1 values('cc',20);" );
	 
	         ResultSet rs = stat.executeQuery("select * from table1;"); //查询数据 

	         while (rs.next()) { //将查询到的数据打印出来

	             System.out.print("name = " + rs.getString("name") + " "); //列属性一
	             logger.warn("age = " + rs.getString("age")); //列属性二
	         }
	         rs.close();
	         conn.close(); //结束数据库的连接 
	        }
	        catch( Exception e )
	        {
	         e.printStackTrace ( );
	        }*/
	      //为定期取实时数据初始化 sinter_yc集合
	      try {	            
	            cnt = 0; 
	            jedis = RedisUtil.getJedis();
				
				 sinter_yc =  jedis.keys("ch*i*");
	    		// sinter_yx =  jedis.keys("*.status");
	    		 logger.warn("size of yc:"+sinter_yc.size());
	    		 
	    		
	    		 //为存储历史数据初始化生成histycmap,hevtmap
	    		 
	    		 
	    		String sql="select rtuno,sn,kkey,saveno,upperlimit,lowerlimit from prtuana";
	    			//if(rand.equalsIgnoreCase(imagerand)){
			    		try{
							pstmt=dbcon.setPreparedStatement(sql);
							
							rs=dbcon.getPrepatedResultSet();
                             updownstat = new HashMap<String,String>(rs.getRow());
							 ResultSetMetaData rsmd = rs.getMetaData(); 
							 int colCount=rsmd.getColumnCount();
							 List<String> colNameList=new ArrayList<String>();
							 for(int i=0;i<colCount;i++){
								 colNameList.add(rsmd.getColumnName(i+1));
							 } 
							 while(rs.next()){ 
								 Map map=new HashMap<String, Object>();
								 for(int i=0;i<colCount;i++){
									 key=colNameList.get(i);
									 value=rs.getString(colNameList.get(i));
									 map.put(key, value);  
								 }
								 //results.add(map);
								 objana.put(map.get("kkey").toString(),map);
								 updownstat.put(map.get("kkey").toString(), "0");
							 }
							 
				        // pstmt.close();
				        // dbcon.getClose();
						}catch(SQLException e){
							System.out.println("出错了"+e.toString());
						}
			    		//logger.warn(objana);
	    				sql="select rtuno,sn,type,kkey from prtudig";
		    			//if(rand.equalsIgnoreCase(imagerand)){
		    				try{
		    					pstmt=dbcon.setPreparedStatement(sql);
		    					
		    					rs=dbcon.getPrepatedResultSet();
		    					evtstat = new HashMap<String,String>(rs.getRow());
		    					 ResultSetMetaData rsmd = rs.getMetaData(); 
								 int colCount=rsmd.getColumnCount();
								 List<String> colNameList=new ArrayList<String>();
								 for(int i=0;i<colCount;i++){
									 colNameList.add(rsmd.getColumnName(i+1));
								 } 
								 while(rs.next()){ 
									 Map map=new HashMap<String, Object>();
									 for(int i=0;i<colCount;i++){
										 key=colNameList.get(i);
										 value=rs.getString(colNameList.get(i));
										 map.put(key, value);  
									 }
									 //results.add(map);
									 objdig.put(map.get("kkey").toString(),map);
									 evtstat.put(map.get("kkey").toString(), "-1");
								
								 }
		    		         pstmt.close();
		    		         dbcon.getClose();
		    				}catch(SQLException e){
		    					System.out.println("出错了"+e.toString());
		    				}
		    				logger.warn(objdig);
				  RedisUtil.close(jedis);	
				  jedis=null;
	        } catch (Exception e) {
	        	 logger.warn(e.toString());
	            e.printStackTrace();
	        } 
	     
	     
	   }
	   /**
	     * 调用lua脚本
	     * @param jedis
	     */
	    public  void testCallLuaFile(){
	        String luaStr = null;
	        Jedis sjedis = null;
	        Object result = null;
	        //带反斜杠，路径为classPath，不带反斜杠，路径为类的同一目录
	        
	        try {
	        	 sjedis = RedisUtil.getJedis();
	        	Reader r = new InputStreamReader(ThreadDemo.class.getResourceAsStream("jxkj.lua"));
	            luaStr = CharStreams.toString(r);
	            if (sjedis!=null) result =  sjedis.eval(luaStr);

				  RedisUtil.close(sjedis);
				  sjedis=null;
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
	    }
	    /**
	     * 检查遥测越限
	     * @param 
	     * @throws ScriptException 
	     */  
	   
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
		
	} 
	   /**
	     * 检查遥信变位
	     * @param 
	     * @throws ScriptException 
	     */  
	   
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
		
	} 
	    /*
	     * 插入记录
	     * 
	     */
	  public void add_red(String sql)
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
	  }
	    
	   /**
	     * 批量读取
	     * @param jedis
	     */ 
	   public void run() {
	     // logger.warn("Running " +  threadName );
	      String s = null;
	      String vals = null;
	      String luaStr = null;
	      int vv = -2;
	      String dbname = null; 
		  String dbname2 = null;  
		  String savet=null;	
	      String up,down,rtuno,sn;
	      Map map=new HashMap<String, Object>();	
	      Map<String,String> result = new HashMap<String,String>();
	      Map<String,Response<String>> responses = new HashMap<String,Response<String>>(sinter_yc.size());
	      logger.warn(objdig);
	      try {
	         while(true) {
	        	// logger.warn("scanthread Running... "  );
	        	   map.clear();
	        	   result.clear();
	        	   responses.clear();
	    	      
					try {
						jedis = RedisUtil.getJedis();
						
						 Pipeline p = jedis.pipelined(); 
						
						 SimpleDateFormat df = new SimpleDateFormat("YYMMdd");//设置日期格式
						 SimpleDateFormat df2 = new SimpleDateFormat("HHmmss");//设置日期格式
						 savet = (df.format(new Date()));// new Date()为获取当前系统时间
						 Calendar c = Calendar.getInstance();//可以对每个时间域单独修改
						 dbname = "hevtyc"+(c.get(Calendar.YEAR)%10); 
						 dbname2 = "hevt"+(c.get(Calendar.YEAR)%10);  
						
				         //String up,down,rtuno,sn;
				        result.clear();
				       
				        //使用pipeline hgetall  
				       
				        
				        //start = System.currentTimeMillis();
				        for(String key : sinter_yc) {
				         responses.put(key, p.get(key));
				        }
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
				       /* keys = jedis.keys("*i*.status"); 
				        for(String key : keys) {
					         responses.put(key, p.get(key));
					        }
					        p.sync();
				        */
				       // Map<String,String> status = new HashMap<String,String>(sinter_yc.size());
				       
				        //status = jedis.hgetAll("updownstatus");
				        try {							 
				        	     
						        for(String k : responses.keySet()) {
						        	 luaStr = responses.get(k).get().toString();
							         result.put(k, luaStr);
							         s = k.replace("_.value", "");
							         if (s.indexOf("ai")>0){
								         
								         //logger.error(s);
								         
								        // logger.error(status.get(k.replace("_.value", "")) );
								        // logger.error(updown.get(k.replace("_.value", "")) );
							        	
							        	 map =(Map) objana.get(s);
							        	 if (!map.isEmpty())
							        	 {
							        	 down = map.get("LOWERLIMIT").toString();
							        	 up = map.get("UPPERLIMIT").toString();
							        	 rtuno = map.get("RTUNO").toString();
							        	 sn = map.get("SN").toString();
							        	 //logger.error(s);
								         //logger.error(luaStr);
								         //logger.error(map.get("LOWERLIMIT").toString());
								         //logger.error(updownstat.get(s).toString());
								         vv =  checkupdown(luaStr,down,up,Integer.parseInt(updownstat.get(s).toString())); 
								         if (vv!=-2) 
								        	 {
								        	   try
								        	   {
								        	   add_red("INSERT INTO "+dbname+" (ymd,hms,ch,xh,zt,val) values ("+df.format(new Date())+","+df2.format(new Date())+","+rtuno+","+sn+","+vv+","+luaStr+")");    
								        	   logger.warn("save hevtyc:"+df.format(new Date())+","+df2.format(new Date())+","+rtuno+","+sn+","+vv+","+luaStr);
								        	   updownstat.put(s, ""+vv);
								        	   } catch (Exception e) {
													e.printStackTrace();
												}
								        	 }
							        	 }else
							        	 {
							        		 logger.error("redis_key do not match mysql_kkey:"+s+",请确认！！！");
							        	 }
								         //map.clear();
								         //map=null;
							         }
							         if (s.indexOf("di")>0){
							        	    
								        	 map =(Map) objdig.get(s);
								        	//logger.warn(objdig);
								        	 //logger.warn(s);
								        	 //logger.warn(map);
								        	 if (!map.isEmpty())
								        	 {
								        	 rtuno = map.get("RTUNO").toString();
								        	 sn = map.get("SN").toString();
								        	 up = map.get("TYPE").toString();
									         vv =  checkevt(luaStr,Integer.parseInt(evtstat.get(s).toString())); 
									         if (evtstat.get(s).toString()!="-1" && vv!=0) 
									        	 {
									        	   try
									        	   {
									        	   add_red("INSERT INTO "+dbname2+" (ymd,hms,ch,xh,event,zt,ms) values ("+df.format(new Date())+","+df2.format(new Date())+","+rtuno+","+sn+","+up+","+luaStr+",'')");    
									        	   logger.warn("save hevt:"+df.format(new Date())+","+df2.format(new Date())+","+rtuno+","+sn+","+vv+","+luaStr);
									        	  
									        	   } catch (Exception e) {
														e.printStackTrace();
													}
									        	 }
									         evtstat.put(s, ""+luaStr);
							                }
								        	 else
								        	 {
								        		 //logger.error("redis_key do not match mysql_kkey:"+s+",请确认！！！");
								        		 //logger.warn(objdig);
								        	 }
									         //map.clear();
									         //map=null;
								         }
							        
						        }
							} catch (Exception e) {
								e.printStackTrace();
							}
				       
				        p.close();	
		        	      RedisUtil.close(jedis);	
		        	      jedis=null;
				      

					     // logger.warn("Running " +  ckt.getSockets().toString() );

				       // end = System.currentTimeMillis();
				      //  System.out.println("result size:[" + result.size() + "] ..");
				      //  System.out.println("hgetAll with pipeline used [" + (end - start) / 1000 + "] seconds ..");
                      
						
				       
				         s = gson.toJson(result);
				         try {
				        	 ckt.broadcast( s,-1);
							} catch (Exception e) {
								e.printStackTrace();
							}
						
						
						//if (cnt==10)
				         responses.clear();
				         result.clear();
				        
						cnt++;
						
						//}
						
			           // 
						
						
					 } 
					
					catch (JedisConnectionException e) {
			            e.printStackTrace();
			        } 
					 catch (Exception e) {
				            e.printStackTrace();
				        }
					
				
				
//	            for (int i=0;i<1000;i++) 
//					val_yc[i] = ""+rand.nextInt(100); 
//				
				
				
				//map1.put("data", val_yc);
		       // map1.put("res",message);  
		      
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
	          
	          
	  		 
	         }
	      }catch (InterruptedException e) {
	         logger.warn("Thread " +  Thread.currentThread().getName() + " interrupted.");
	      }finally {

	    	 
	      }
	     // h.close();
	     
	     
		 
		  
	      logger.warn("Thread " +  threadName + " exiting.");
	   }
	   
	   public void start () {
	      logger.warn("Starting " +  threadName );
	      if (t == null) {
	         t = new Thread (this, threadName);
	         t.start ();
	      }
	   }
	   public  boolean killThreadByName(String name) {
		   
		   
		   
		   try {
				 
			   // pstmt.close();
		        // dbcon.getClose();
			   RedisUtil.close(jedis);
			   jedis=null;
				  sinter_yc.clear();// =  null;
		  		   //sinter_yx.clear();// =  null;
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