package com.dl.tool;

import com.alibaba.fastjson.JSONObject;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.quartz.*;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.Pipeline;
import redis.clients.jedis.Response;
import redis.clients.jedis.exceptions.JedisConnectionException;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAdjusters;
import java.util.*;
import java.sql.PreparedStatement;

/**
 * [任务类]
 * @author xx
 * @date 2019-8-20 
 * @copyright copyright (c) 2018
 */
@PersistJobDataAfterExecution
@DisallowConcurrentExecution
public  class saveMaxMin implements Job {


	   private static final Logger logger = LogManager.getLogger(saveMaxMin.class);

	   private Jedis mjedis;
	   private String dzt = PropertyUtil.getProperty("dztime");
	   private String patter=".*ai.*";
	   private String patterdt=".*"+dzt+".*";
	      HashMap<String,Object>  anamap = new HashMap<String,Object>();
	   public static AnaUtil myana=new AnaUtil();
	  //	DBConnection dbcon=null;//
	  //	PreparedStatement pstmt=null;
		//ResultSet rs;
	  static Map<String,Response<String>> responses = null;
	  private String sql = null;
	/*public static boolean isNumber(String string) {
		if (string == null)
			return false;
		Pattern pattern = Pattern.compile("^-?\\d+(\\.\\d+)?$");
		return pattern.matcher(string).matches();
	}*/
		public saveMaxMin() {
			mjedis = RedisUtil.getJedis(1);
		}
		
   
	@SuppressWarnings("unchecked")
	@Override
    public void execute(JobExecutionContext arg0) throws JobExecutionException {
       //
		//logger.warn("saveHistyc");
    	DBConnection dbcon=new DBConnection();
    	
       // JSONObject anaobj =(JSONObject) (arg0.getJobDetail().getJobDataMap().get("taskdetial"));
		JSONObject anaobj =myana.objana_v;
       // logger.warn(anaobj);
		PreparedStatement ps = null;

		LocalDateTime rightnow = LocalDateTime.now();
		//rightnow = rightnow.minusDays(1);

		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

		responses = new HashMap<String, Response<String>>();

		// logger.warn(anaobj);

		// logger.warn(maxdnSaveno);
		Set<String> sinter_yc= anaobj.keySet();
         //logger.info(dzt);
    	try {

			Pipeline p = mjedis.pipelined();
			for (String key1 : sinter_yc) {
                if (key1.contains("ai")) {
					responses.put(key1 + "_.maxv", p.get(key1 + "_.maxv"));
					responses.put(key1 + "_.maxt", p.get(key1 + "_.maxt"));
					responses.put(key1 + "_.minv", p.get(key1 + "_.minv"));
					responses.put(key1 + "_.mint", p.get(key1 + "_.mint"));
				}

			}
			try {
				if (p != null) p.sync();
			} catch (JedisConnectionException e) {
				logger.error("lua err: " + e.toString());
				e.printStackTrace();
			} catch (Exception e) {
				e.printStackTrace();
			}
			int i=0;
			sql="";
			sql="REPLACE INTO everyday (tdate,saveno,maxv,maxt,minv,mint) VALUES (?,?,?,?,?,?);";
			ps=dbcon.setPreparedStatement(sql);
			for (String key1 : sinter_yc) {
				if (key1.contains("ai") && mjedis.exists(key1+"_.maxv") && mjedis.exists(key1+"_.maxt")&& mjedis.exists(key1+"_.minv")&& mjedis.exists(key1+"_.mint")) {
					i++;
					anamap = (HashMap<String,Object>)anaobj.get(key1);

					//sql=sql+"REPLACE INTO everyday (tdate,saveno,maxv,maxt,minv,mint) VALUES ('"+rightnow.format(formatter)+"',"+anamap.get("saveno")+","+responses.get(key1+"_.maxv").get().toString()+",'"+responses.get(key1+"_.maxt").get().toString()+"',"+responses.get(key1+"_.minv").get().toString()+",'"+responses.get(key1+"_.mint").get().toString()+"');";
					//logger.warn(sql);
					ps.setString(1, rightnow.format(formatter));
					ps.setString(2, anamap.get("saveno").toString());
					ps.setString(3, responses.get(key1+"_.maxv").get().toString());
					ps.setString(4, responses.get(key1+"_.maxt").get().toString());
					ps.setString(5, responses.get(key1+"_.minv").get().toString());
					ps.setString(6, responses.get(key1+"_.mint").get().toString());

					ps.addBatch();

					/*sql="REPLACE INTO everyday (tdate,saveno,maxv,maxt,minv,mint) VALUES ('"+rightnow.format(formatter)+"',"+anamap.get("saveno")+","+responses.get(key1+"_.maxv").get().toString()+",'"+responses.get(key1+"_.maxt").get().toString()+"',"+responses.get(key1+"_.minv").get().toString()+",'"+responses.get(key1+"_.mint").get().toString()+"');";
					//logger.warn(sql);

						try {

							dbcon.setPreparedStatement(sql);
							int t = dbcon.getexecuteUpdate();
							sql="";
							i=0;


						} catch (Exception e) {
							logger.error("出错了" + e.toString());
						}*/

				}

			}
			try {

				//dbcon.
				ps.executeBatch();
				            //提交事务



			} catch (Exception e) {
				logger.error("出错了" + e.toString());
			}
			/*Iterator iter = anaobj.entrySet().iterator();
			while (iter.hasNext()) {
				Map.Entry entry = (Map.Entry) iter.next();
				if (entry.getKey().toString().contains("ai"))
				{
					anamap = (HashMap<String,Object>)entry.getValue();
					sql="REPLACE INTO everyday (tdate,saveno,maxv,maxt,minv,mint) VALUES ('"+rightnow.format(formatter)+"',"+anamap.get("saveno")+","+responses.get(key1).get().toString()+",'"+anamap.get("maxt")+"',"+anamap.get("minv")+",'"+anamap.get("mint")+"')";

				}
				try{

					dbcon.setPreparedStatement(sql);
					int t=dbcon.getexecuteUpdate();;


				}catch(Exception e){
					System.out.println("出错了"+e.toString());
				}
			}*/

			 logger.warn("save maxmin of day:"+rightnow.format(formatter));
			 //logger.warn(sql);

			sql="select * from everyday a where not exists(select 1 from everyday b where b.saveno=a.saveno and MONTH(a.tdate)=MONTH(b.tdate) and b.maxv>a.maxv and YEAR(a.tdate)=YEAR(b.tdate) ) and  a.tdate between STR_TO_DATE('"+rightnow.with(TemporalAdjusters.firstDayOfMonth()).format(formatter)+"','%Y-%m-%d') and STR_TO_DATE('"+rightnow.with(TemporalAdjusters.lastDayOfMonth()).format(formatter)+"','%Y-%m-%d')";
			//Map<String,Object> map =null;
			//if(rand.equalsIgnoreCase(imagerand)){
			try{
				List<Map<String, Object>> userData = dbcon.queryforList(sql);
				int size=userData.size() ;
				if (size> 0)
				{

					for (  i = 0; i<size; i++)
					{

						anamap = (HashMap) userData.get(i);
						sql="REPLACE INTO everymon (tdate,saveno,maxv,maxt) VALUES ('"+rightnow.with(TemporalAdjusters.firstDayOfMonth()).format(formatter)+"',"+anamap.get("saveno")+","+anamap.get("maxv")+",'"+anamap.get("maxt")+"')";
						try{

							dbcon.setPreparedStatement(sql);
							int t=dbcon.getexecuteUpdate();;


						}catch(Exception e){
							logger.error("出错了"+e.toString());
						}


					}

				}

			}catch(Exception e){
				logger.warn("出错了"+e.toString());
			}

			sql="select * from everyday a where not exists(select 1 from everyday b where b.saveno=a.saveno and MONTH(a.tdate)=MONTH(b.tdate) and b.minv<a.minv and YEAR(a.tdate)=YEAR(b.tdate) ) and  a.tdate between STR_TO_DATE('"+rightnow.with(TemporalAdjusters.firstDayOfMonth()).format(formatter)+"','%Y-%m-%d') and STR_TO_DATE('"+rightnow.with(TemporalAdjusters.lastDayOfMonth()).format(formatter)+"','%Y-%m-%d')";
			//Map<String,Object> map =null;
			//if(rand.equalsIgnoreCase(imagerand)){
			try{
				List<Map<String, Object>> userData = dbcon.queryforList(sql);
				int size=userData.size() ;
				if (size> 0)
				{

					for (  i = 0; i<size; i++)
					{

						anamap = (HashMap) userData.get(i);
						sql="REPLACE INTO everymon (tdate,saveno,minv,mint) VALUES ('"+rightnow.with(TemporalAdjusters.firstDayOfMonth()).format(formatter)+"',"+anamap.get("saveno")+","+anamap.get("minv")+",'"+anamap.get("mint")+"')";
						try{

							dbcon.setPreparedStatement(sql);
							int t=dbcon.getexecuteUpdate();;


						}catch(Exception e){
							logger.error("出错了"+e.toString());
						}


					}

				}

			}catch(Exception e){
				logger.warn("出错了"+e.toString());
			}
			 //logger.warn(sql);




			 //System.gc();
		         dbcon.getClose();
		         dbcon=null;

	        	
	       
	        
           } 
		
	     	catch (JedisConnectionException e) {
             e.printStackTrace();
           } 
	    	 catch (Exception e) {
	            e.printStackTrace();
	        }
    	anaobj = null;

    }
    	
}
