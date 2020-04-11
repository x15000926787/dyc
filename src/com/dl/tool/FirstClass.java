package com.dl.tool;

import com.alibaba.fastjson.JSONObject;
import com.bjsxt.thread.ThreadSubscriber;
import com.dl.quartz.LuaJob;
import com.dl.quartz.QuartzJob;
import com.dl.quartz.QuartzManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;
import reactor.fn.timer.HashWheelTimer;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import java.util.*;


public class FirstClass  implements ServletContextListener {
	//定时任务统一放在本线程中添加，包含：定时开关机(myjob.class)，定时执行lua脚本(myjob.class)，5分钟存储历史遥测、历史电量数据(savehistyc),定期取得实时数据+发送给http客户端(UpdateDataJob)+判断遥信变位和遥测越限（以及恢复），并存入Mysql库
	//private ThreadTimer T1 = null; //原定时任务（cmd发送命令给周），现已停用
	//private ThreadLua T2 = null;    //定时执行lua脚本，现已停用。	
	//private ThreadDemo T3 = null;
	/**
	 * 监听redis库，取得变化遥测遥信数据，判断遥信变位和遥测越限（以及恢复）,发送短信和email,并发送实时值给http客户端
	 */
	private ThreadSubscriber T4 = null;
	/**
	 * private DelayWork T5 = null;
	 * 定期检查redis库中是否有延时任务
	 * 已弃用
	 * 延时任务实现方法：通过在redis中设置定时key-value键值对，然后在redis监听中接收expire来执行延时任务
	 *
	 * */


	
	
	public static String projectId;


	public String url;

	

	//private static  JSONObject taskarray = new JSONObject();
	private static final Logger logger = LogManager.getLogger(FirstClass.class);
	
	private WebApplicationContext springContext;  
	public static JdbcTemplate jdbcTemplate;

	public static JdbcTemplate getJdbcTemplate() {
			return jdbcTemplate;
		}
	public static void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
			FirstClass.jdbcTemplate = jdbcTemplate;
		}

	JSONObject objana = new JSONObject();
	public static AnaUtil myana=new AnaUtil();
	//public UpdateCkzJob upckz=new UpdateCkzJob();
    public FirstClass() {
    
    	 super(); 

   	 
    }
    
   
   
	/**
	 (non-Javadoc)
	 * <p>Title: contextDestroyed</p>  
	 * <p>Description: </p>  
	 * @param arg0  
	 * @see javax.servlet.ServletContextListener#contextDestroyed(javax.servlet.ServletContextEvent)  
	 */

	@Override
	public void contextDestroyed(ServletContextEvent arg0) {
		// TODO Auto-generated method stub
		QuartzManager.shutdownJobs();
		T4.stoplisten();
		try {
			Thread.sleep(5000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		//T4.killThreadByName("lisner_data");
		logger.warn("应用程序关闭!");
	}
	 
	/** (non-Javadoc)
	 * <p>Title: contextInitialized</p>  
	 * <p>Description: </p>  
	 * @param arg0  
	 * @see javax.servlet.ServletContextListener#contextInitialized(javax.servlet.ServletContextEvent)  
	 */
	@Override
	public void contextInitialized(ServletContextEvent arg0) {
		// TODO Auto-generated method stub
		logger.warn("初始化系统服务...");
		projectId = PropertyUtil.getProperty("project_id");
		
		/*logger.warn("项目ID："+name1);*/
		springContext = WebApplicationContextUtils.getWebApplicationContext(arg0.getServletContext());  
		  if(springContext != null){  
			  jdbcTemplate = (JdbcTemplate)springContext.getBean("jdbcTemplate");  			  
		  }else{  
		   logger.warn("获取应用程序上下文失败!");  
		   return;  
		  }  
		  //
		 
		// T1 = new ThreadTimer("timer");
				 //   T1.start();
		myana.loadAna_v(jdbcTemplate);
		myana.load_author(jdbcTemplate);

		myana.load_condition(jdbcTemplate);
		myana.load_msguser(jdbcTemplate);
		myana.load_onlinewarn(jdbcTemplate);
		//logger.warn(myana.msg_author);
		objana =myana.objana_v ;
       // logger.warn(objana);
		int i=0;

		
        /*
		String sql="select rtuno,sn,kkey,ttype,saveno,upperlimit,lowerlimit,pulsaveno,chgtime,ldz from prtuana_v ";

		
		

		i=0;j=0;
		Map<String,Object> map =null;

		
		

	    		try{
	    			List<Map<String, Object>> userData = getJdbcTemplate().queryForList(sql);
	    			 int size=userData.size() ;
	    			    if (size> 0)
	    			    {
	    					
	    			    	for ( i = 0; i<size; i++)
	    			    	{
	    						
	    			    		map = (HashMap) userData.get(i);




								maxsav=(Integer.parseInt(map.get("saveno").toString())>maxsav?Integer.parseInt(map.get("saveno").toString()):maxsav);
								 try
								 {
									 maxdnsav=(Integer.parseInt(map.get("pulsaveno").toString())>maxdnsav?Integer.parseInt(map.get("pulsaveno").toString()):maxdnsav);
								 }
								 catch(Exception e)
								 {}
								 objana.put(map.get("kkey").toString(),map);

	    			    	}
	    			    	 objana.put("maxsav",maxsav);
							 objana.put("maxdnsav",maxdnsav);
	    			    }
					
					

				}catch(Exception e){
					logger.warn("出错了"+e.toString());
				}
	    		logger.warn(objana);
				sql="select rtuno,sn,type ,kkey,chgtime from prtudig ";
			//if(rand.equalsIgnoreCase(imagerand)){
				try{
	    			List<Map<String, Object>> userData = getJdbcTemplate().queryForList(sql);
	    			 int size=userData.size() ;
	    			    if (size> 0)
	    			    {
	    					
	    			    	for ( i = 0; i<size; i++)
	    			    	{	
	    			    		map = (HashMap) userData.get(i);
								objana.put(map.get("kkey").toString(),map);
	    			    	}
	    			    }
				}catch(Exception e){
					logger.warn("出错了"+e.toString());
				}
      */
		/**
		 *  添加定时任务时，只携带任务ID作为参数，任务触发时再查询任务清单，2020-03-05修改
		 */
		/*
		String sql="select * from timetask_detial order by taskid,id";
		
		List<Map<String, Object>> tasktype1 = new ArrayList<Map<String, Object>>();
		//List<Map<String, Object>> tasktype2 = new ArrayList<Map<String, Object>>();
			try{

				
				tasktype1= getJdbcTemplate().queryForList(sql);
				for (Map<String, Object> tmap:tasktype1){
					if(!taskarray.containsKey(tmap.get("taskid").toString())){
						Map<String,Object> nmap = new HashMap<String,Object>();
						nmap.put(tmap.get("kkey").toString(), tmap.get("val"));
						taskarray.put(tmap.get("taskid").toString(), nmap);
					}else
					{
						Map<String,Object> fmap = (Map<String,Object>)taskarray.getJSONObject(tmap.get("taskid").toString());

						fmap.put(tmap.get("kkey").toString(), tmap.get("val"));
						taskarray.put(tmap.get("taskid").toString(), fmap);
					}
	    		}
	    

			}catch(Exception e){
				logger.warn("出错了"+e.toString());
			}

         */
		/**
		 *
		 */

		String sql="select * from timetask ";
		List<Map<String, Object>> tasktype1 = new ArrayList<Map<String, Object>>();
	//if(rand.equalsIgnoreCase(imagerand)){
		try{
			tasktype1= getJdbcTemplate().queryForList(sql);
			
			for (Map<String, Object> tmap:tasktype1){
				
				i=(int)tmap.get("id");


				switch ((int)tmap.get("type")) {
				case 1:
					Map<String, String> maps = new HashMap<String,String>();
					//maps.put("vv", taskarray.get(""+i).toString());
					maps.put("vv", ""+i);
					QuartzManager.addJob("qjob"+i, QuartzJob.class,tmap.get("cronstr").toString(),maps);
					logger.warn("定时ao/do任务:"+tmap.get("cronstr").toString()+maps.toString());
					//QuartzManager.addJob("job"+i,MyJob.class,rs.getString("cronstr"),taskstr); 	
					break;
				case 2:
					Map<String, Object> fmap = new HashMap<String,Object>();
					fmap.put("luaname",tmap.get("luaname").toString());	
					logger.warn("定时脚本任务:"+tmap.get("cronstr").toString()+fmap.toString());
					QuartzManager.addJob("ljob"+i, LuaJob.class,tmap.get("cronstr").toString(),fmap);
					//SchedulerUtil.hadleCronTrigger(rs.getString("id"), rs.getString("type"),""+i, ""+j,MyJob.class,rs.getString("cronstr"),taskstr); 	
					break;
				case 3:

					QuartzManager.addJob("saveHistyc",saveHistyc.class,tmap.get("cronstr").toString(),objana);
					logger.warn("存储5分钟历史数据任务:"+tmap.get("cronstr").toString());
					break;
				case 4:	
					QuartzManager.addJob("UpdateDataJob",UpdateDataJob.class,tmap.get("cronstr").toString(),objana);
					logger.warn("请求实时数据任务:"+tmap.get("cronstr").toString());
					break;
				case 5:
						QuartzManager.addJob("checkonlinetime",checkOnlineTime.class,tmap.get("cronstr").toString(),objana);
						logger.warn("check在线时长任务:"+tmap.get("cronstr").toString());
						break;
				default:
					break;
				}
				

				
				i++;

			}
      
		}catch(Exception e){
			logger.error("生成定时任务出错了"+e.toString());
		}
		Date tdate = new Date();


		    T4 = new ThreadSubscriber("lisner_data");
		    T4.start();
		/**
		 *
		 * 添加延时任务
		 * *
		 */
		/*QuartzManager.addJob("testJob", "testJobG", "testJobT", "testJobTG", MyJob.class, objana, 10,2, 0 );*/
        /*
		T5 = new DelayWork("DelayWork");
		    T5.start();
		*/
		/**
		 *
		 */
		/*
		try {
			upckz.execute();
		}catch (SQLException e)
		{
			logger.error("upckz error: " +  e.toString() );
			e.printStackTrace();
		}
           */
		
	}
     
   }