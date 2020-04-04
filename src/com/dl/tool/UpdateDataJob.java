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

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
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
public  class UpdateDataJob extends JdbcDaoSupport implements Job {
	   
	public static ChatSocket ckt = new ChatSocket();
	  // RedisUtil jpool = new RedisUtil();
	
	

	static String s = null;
	
	static   String luaStr = null;
	static 	 String kk = "";
	static   String dbname = null; 
	static  String dbname2 = null;  
	static  String savet=null;	
	//static   String up,down,rtuno,sn;
	//static  SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//设置日期格式
	static String pattern=".*_.value*";
	static String patternVtl=".*_.vtl.*";
	//static String patterndnVtl=".*dn.*";
	   public static Gson  gson=new Gson();
	  // SqliteHelper h = null;
	    Jedis jedis = null;
	    public static AnaUtil myana=new AnaUtil();
	    static HashMap<String,String>  map = new HashMap<String,String>();
	    public static final Logger logger = LogManager.getLogger(UpdateDataJob.class);
	    //String templatePath = UpdateDataJob.class.getClassLoader().getResource("/").getPath();
		public DBConnection dbcon=null;//new DBConnection();
	    static  Map<String,String> result = new HashMap<String,String>();
	    static Map umap=new HashMap<String, Map<String,String>>();
	    static  Map<String,Response<String>> responses = null;
	    JSONObject anaobj =null;
		//static ScriptEngineManager scriptEngineManager = new ScriptEngineManager();
		//static ScriptEngine scriptEngine ;//= scriptEngineManager.getEngineByName("nashorn");
	   //public static AnaUtil myana=new AnaUtil();
	    
	  // Set<String> sinter_yx =  null;
	  
	    
	 
		
		
		
		public UpdateDataJob()
		{
			dbcon=new DBConnection();
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

		  List<Map<String, Object>> tasktype1 = new ArrayList<Map<String, Object>>();
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


		  }

	  }
    @Override
    public void execute(JobExecutionContext arg0) throws JobExecutionException {


		anaobj =myana.objana_v;
    	  // DBConnection dbcon=new DBConnection();


		//JSONObject anaobj =myana.objana ;
		//logger.info(anaobj.toJSONString());
		   Set<String> sinter_yc= anaobj.keySet();

		HashMap<String,String>  map = new HashMap<String,String>();


		//logger.error("dddss");

		   responses = new HashMap<String,Response<String>>(sinter_yc.size());
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
		        for(String key1 : sinter_yc) {

		         responses.put(key1+"_.value", p.get(key1+"_.value"));

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
				//添加实时ai,di 到json字符串中
				//logger.warn(responses);
				for(String k : responses.keySet()) {
                    //logger.warn(k);
					if (Pattern.matches(pattern, k)||Pattern.matches(patternVtl, k))
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
							}
							//s = k.replace("_.value", "");
							myana.handleEvt(k.replace("_.value",""),luaStr,dbcon.jdbcTemplate,ckt);

							myana.handleCondition(luaStr,k.replace("_.value",""),jedis);

						} catch (Exception e) {
							//logger.warn("实时库中没有："+k);
							//e.printStackTrace();
						}
					}  //if ch_
				}
				/**
                //读取定时任务锁状态,谭小波项目不涉及do,ao,注销此过程
				//get_time_lock();
				*/


				//处理zjdy.txt
/*
				try{


					BufferedReader br = new BufferedReader(new FileReader(templatePath+"/zjdy.txt"));//构造一个BufferedReader类来读取文件
					String s = null;
					while((s = br.readLine())!=null){//使用readLine方法，一次读一行
						//result.append(System.lineSeparator()+s);
						logger.info(s);


						String[] vals=s.split("\\{|\\}");
						for (int i=0;i<vals.length;i++)
						{

							if(Pattern.matches(pattern, vals[i])) {

								try {
									//responses.get(k).get().toString()
									luaStr = responses.get(vals[i]+"_.value").get().toString();
									//logger.warn(vals[i]+":"+luaStr+":"+"val{"+vals[i]+"}");
									//s=s.replaceAll(".*val\\{"+vals[i]+"\\}.*",luaStr);
									s=s.replace("val{"+vals[i]+"}",luaStr);

								}catch (Exception e)
								{
									logger.warn("dot have: "+vals[i]);
								}

							}
						}

						//logger.info(s);

						try

						{


							ScriptEngine engine = (new ScriptEngineManager()).getEngineByName("JavaScript");


							File fName = new File(templatePath+"/myscript.js");
							Reader scriptReader = new FileReader(fName);

							engine.eval(scriptReader);

							Invocable invocable = (Invocable) engine;
							Pattern pp = Pattern.compile("Fun[^\\}]+");
							Matcher m = pp.matcher(s);//
							while (m.find()) {
								List<Float> list = new ArrayList<>();





                                String[] fs = m.group(0).split("\\{|,");
							    for (int i=2;i<fs.length;i++) {
							    	list.add(Float.parseFloat(fs[i]));
									//System.out.println("para:"+fs[i]);
								}
								//System.out.println("find: "+m.group(0));
                                //System.out.println("invocable:"+invocable.invokeFunction(fs[1], list));
								s=s.replace(m.group(0)+"}",invocable.invokeFunction(fs[1], list).toString());
                            }


							String[] calc=s.split("=");
							//logger.info(s);
							try
							{
								//实时库存储计算总加窗口值，并被SaveHistyc线程存储5分钟历史值，再根据mysql 定时事件算5分钟增量
								jedis.set(calc[0]+"_.value",engine.eval(calc[1]).toString());
								//直接发送总加遥测值到htttp客户端，计算总加电量增量发送到http客户端
								if (Pattern.matches(patterndnVtl, calc[0]))
								{
									map = (HashMap<String,String>)anaobj.get(calc[0]);


									result.put(calc[0],engine.eval(calc[1].toString()+"-"+String.valueOf(map.get("ldz"))).toString());
									logger.warn("calced: " + s.replace(calc[1], engine.eval(calc[1].toString()+"-"+String.valueOf(map.get("ldz"))).toString()));
								}
								else {
									result.put(calc[0], engine.eval(calc[1]).toString());
									logger.warn("calced: " + s.replace(calc[1], engine.eval(calc[1]).toString()));
								}

							}catch (ScriptException e) {
								logger.error("ScriptException: " +  e.toString() );
								e.printStackTrace();
							}catch (Exception e) {
								logger.error(" err: " +  e.toString() );
								e.printStackTrace();
							}


						}catch(NoSuchMethodException e){
							logger.error("NoSuchMethodException: " +  e.toString() );
						}
						catch(FileNotFoundException e){
							logger.error("System.getProperty(\"user.dir\")+\"/src/myscript.js\" err: " +  e.toString() );
						}
						catch (ScriptException e) {
							logger.error("ScriptException: " +  e.toString() );
							e.printStackTrace();
						}


					}
					br.close();
				}catch(Exception e){
					logger.error(e.toString());
					e.printStackTrace();
				}
*/

				if (!ckt.getSockets().isEmpty())    //
				{

					s = gson.toJson(umap);
					//logger.warn(s);
					try {
						ckt.broadcast(ckt.getSockets(), s,-1);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}





				try {
					p.close();
				}catch (IOException ee){}

		      

       	         RedisUtil.close(jedis);
       	         jedis=null;
				 dbcon.getClose();
		         responses.clear();
		         result.clear();
		         umap.clear();

			 } 
			
			catch (JedisConnectionException e) {
	            e.printStackTrace();
	        } 
			 catch (Exception e) {
		            e.printStackTrace();
			}

    	

    }
    	
}
