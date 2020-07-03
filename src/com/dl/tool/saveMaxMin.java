package com.dl.tool;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.quartz.*;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.Pipeline;
import redis.clients.jedis.Response;
import redis.clients.jedis.exceptions.JedisConnectionException;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
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


	   private static final Logger logger = FirstClass.logger;


	/*public static boolean isNumber(String string) {
		if (string == null)
			return false;
		Pattern pattern = Pattern.compile("^-?\\d+(\\.\\d+)?$");
		return pattern.matcher(string).matches();
	}*/
		public saveMaxMin() {

		}
		
   
	@SuppressWarnings("unchecked")
	@Override
    public void execute(JobExecutionContext arg0) throws JobExecutionException {




		//HashMap<String,Object>  anamap = new HashMap<String,Object>();
		 Map<String,Response<String>> responses = null;
		 String sql = null;
		Jedis mjedis = JedisUtil.getInstance().getJedis(1);


		LocalDateTime rightnow = LocalDateTime.now();
		//rightnow = rightnow.minusDays(1);

		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");


		Connection con = null;
		PreparedStatement pstm = null;
		//Statement stm = null;

		int hhmmop = 0;
		int hhmmcl = 0;
		try
		{
			con = FirstClass.getJdbcTemplate().getDataSource().getConnection();
			con.setAutoCommit(false);









		responses = new HashMap<String, Response<String>>();

		// logger.warn(anaobj);

		// logger.warn(maxdnSaveno);
		//Set<String> sinter_yc= AnaUtil.objana_v.keySet();
         //logger.info(dzt);
    	try {

			Pipeline p = mjedis.pipelined();
			for (String key1 : AnaUtil.objana_v.keySet()) {
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
			pstm = con.prepareStatement(sql);
			for (String key1 : AnaUtil.objana_v.keySet()) {
				if (key1.contains("ai") && mjedis.exists(key1+"_.maxv") && mjedis.exists(key1+"_.maxt")&& mjedis.exists(key1+"_.minv")&& mjedis.exists(key1+"_.mint")) {
					i++;
					//anamap = (HashMap<String,Object>)AnaUtil.objana_v.get(key1);

					//sql=sql+"REPLACE INTO everyday (tdate,saveno,maxv,maxt,minv,mint) VALUES ('"+rightnow.format(formatter)+"',"+anamap.get("saveno")+","+responses.get(key1+"_.maxv").get().toString()+",'"+responses.get(key1+"_.maxt").get().toString()+"',"+responses.get(key1+"_.minv").get().toString()+",'"+responses.get(key1+"_.mint").get().toString()+"');";
					//logger.warn(sql);
					pstm.setString(1, rightnow.format(formatter));
					pstm.setString(2, ((HashMap<String,Object>)AnaUtil.objana_v.get(key1)).get("saveno").toString());
					pstm.setString(3, responses.get(key1+"_.maxv").get());
					pstm.setString(4, responses.get(key1+"_.maxt").get());
					pstm.setString(5, responses.get(key1+"_.minv").get());
					pstm.setString(6, responses.get(key1+"_.mint").get());

					pstm.addBatch();

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
				pstm.executeBatch();
				con.commit();           //提交事务



			} catch (Exception e) {
				logger.error("出错了" + e.toString());
			}
			for (String key1 : AnaUtil.objana_v.keySet()) {
				mjedis.set(key1+"_.maxv","-99999999");
				mjedis.set(key1+"_.minv","99999999");
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

			//logger.warn("start save maxmin of day:"+rightnow.format(formatter));
			 //logger.warn(sql);

			/*sql="select * from everyday a where not exists(select 1 from everyday b where b.saveno=a.saveno and MONTH(a.tdate)=MONTH(b.tdate) and b.maxv>a.maxv and YEAR(a.tdate)=YEAR(b.tdate) ) and  a.tdate between STR_TO_DATE('"+rightnow.with(TemporalAdjusters.firstDayOfMonth()).format(formatter)+"','%Y-%m-%d') and STR_TO_DATE('"+rightnow.with(TemporalAdjusters.lastDayOfMonth()).format(formatter)+"','%Y-%m-%d')";
			//Map<String,Object> map =null;
			//if(rand.equalsIgnoreCase(imagerand)){
			try{
				List<Map<String, Object>> userData = FirstClass.getJdbcTemplate().queryForList(sql);
				int size=userData.size() ;
				if (size> 0)
				{

					for (  i = 0; i<size; i++)
					{

						//anamap = (HashMap) userData.get(i);
						sql="REPLACE INTO everymon (tdate,saveno,maxv,maxt) VALUES ('"+rightnow.with(TemporalAdjusters.firstDayOfMonth()).format(formatter)+"',"+ userData.get(i).get("saveno")+","+ userData.get(i).get("maxv")+",'"+userData.get(i).get("maxt")+"')";
						try{

							pstm.execute(sql);

							//con.setAutoCommit(true);


						}catch(Exception e){
							logger.error("出错了"+e.toString());
						}


					}
					con.commit();
				}

			}catch(Exception e){
				logger.warn("出错了"+e.toString());
			}

			sql="select * from everyday a where not exists(select 1 from everyday b where b.saveno=a.saveno and MONTH(a.tdate)=MONTH(b.tdate) and b.minv<a.minv and YEAR(a.tdate)=YEAR(b.tdate) ) and  a.tdate between STR_TO_DATE('"+rightnow.with(TemporalAdjusters.firstDayOfMonth()).format(formatter)+"','%Y-%m-%d') and STR_TO_DATE('"+rightnow.with(TemporalAdjusters.lastDayOfMonth()).format(formatter)+"','%Y-%m-%d')";
			//Map<String,Object> map =null;
			//if(rand.equalsIgnoreCase(imagerand)){
			try{
				List<Map<String, Object>> userData = FirstClass.getJdbcTemplate().queryForList(sql);
				int size=userData.size() ;
				if (size> 0)
				{

					for (  i = 0; i<size; i++)
					{

						//anamap = (HashMap) userData.get(i);
						sql="REPLACE INTO everymon (tdate,saveno,minv,mint) VALUES ('"+rightnow.with(TemporalAdjusters.firstDayOfMonth()).format(formatter)+"',"+ userData.get(i).get("saveno")+","+ userData.get(i).get("minv")+",'"+ userData.get(i).get("mint")+"')";
						try{

							pstm.execute(sql);



						}catch(Exception e){
							logger.error("出错了"+e.toString());
						}


					}
					con.commit();

				}

			}catch(Exception e){
				logger.warn("出错了"+e.toString());
			}
			 //logger.warn(sql);
*/

			logger.warn("ed save maxmin of day:"+rightnow.format(formatter));
	       
	        
           } 
		
	     	catch (JedisConnectionException e) {
             e.printStackTrace();
           } 
	    	 catch (Exception e) {
	            e.printStackTrace();
	        }


			con.setAutoCommit(true);
			pstm.close();
			pstm = null;
			con.close();
			//con = null;

		}
		catch (SQLException e)
		{
			e.printStackTrace();
			try {
				if(!con.isClosed()){
				}
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
		}


		JedisUtil.getInstance().returnJedis(mjedis);

		responses.clear();
		responses=null;



		sql = null;
		rightnow = null;

		formatter = null;




    }
    	
}
