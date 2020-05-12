package com.dl.tool;

import com.alibaba.fastjson.JSONObject;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.quartz.*;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.Pipeline;
import redis.clients.jedis.Response;
import redis.clients.jedis.exceptions.JedisConnectionException;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

/**
 * [任务类]
 * @author xx
 * @date 2019-8-20 
 * @copyright copyright (c) 2018
 */
@PersistJobDataAfterExecution
@DisallowConcurrentExecution
public  class saveHistyc implements Job {
	   
	   Jedis jedis = null;
	   private static final Logger logger = LogManager.getLogger(saveHistyc.class);

	   private String dzt = PropertyUtil.getProperty("dztime");
	   private String patter=".*ai.*";
	   private String patterdt=".*"+dzt+".*";
	      HashMap<String,Object>  anamap = new HashMap<String,Object>();
	   public static AnaUtil myana=new AnaUtil();
	  //	DBConnection dbcon=null;//
	  //	PreparedStatement pstmt=null;
		//ResultSet rs;
	  private   int maxanaSaveno = 0,maxdnSaveno = 0;
	  private    long gno=0,vno=0;
	  private   long saveno =0;
	  private String vv = null;
	  static Map<String,Response<String>> responses = null;
	/*public static boolean isNumber(String string) {
		if (string == null)
			return false;
		Pattern pattern = Pattern.compile("^-?\\d+(\\.\\d+)?$");
		return pattern.matcher(string).matches();
	}*/
		public saveHistyc() {

		}
		
   
	@SuppressWarnings("unchecked")
	@Override
    public void execute(JobExecutionContext arg0) throws JobExecutionException {
       //
		logger.warn("saveHistyc start");
    	DBConnection dbcon=new DBConnection();
		int t =0;
       // JSONObject anaobj =(JSONObject) (arg0.getJobDetail().getJobDataMap().get("taskdetial"));
		JSONObject anaobj =myana.objana_v;
       // logger.warn(anaobj);
         maxanaSaveno = Integer.parseInt(anaobj.get("maxsav").toString());
		 maxdnSaveno = Integer.parseInt(anaobj.get("maxdnsav").toString());
		// logger.warn(maxdnSaveno);
		 Set<String> sinter_yc= anaobj.keySet();
		 saveno = 0;
		 gno=0;
		 vno=0;
		String sql = "";
		 String[] collist = new String[((int)(maxanaSaveno/200)+1)];
		 String[] vallist = new String[((int)(maxanaSaveno/200)+1)];

		 
		 String[] dncollist = new String[((int)(maxdnSaveno/200)+1)];
		 String[] dnvallist = new String[((int)(maxdnSaveno/200)+1)];

		LocalDateTime rightnow = LocalDateTime.now();
		DateTimeFormatter df = DateTimeFormatter.ofPattern("MMddHHmm");
		DateTimeFormatter df2 = DateTimeFormatter.ofPattern("yyyyMMddHHmm");
		responses = new HashMap<String, Response<String>>(sinter_yc.size());


         //logger.info(dzt);
    	try {

			jedis = RedisUtil.getJedis();
			//sinter_yc =  jedis.keys("*ai*");
			Pipeline p = jedis.pipelined();

			//SimpleDateFormat df = new SimpleDateFormat("MMddHHmm");//设置日期格式
			//SimpleDateFormat df2 = new SimpleDateFormat("yyyyMMddHHmm");//设置日期格式
			String savet = rightnow.format(df);
			// Calendar c = Calendar.getInstance();//可以对每个时间域单独修改
			String dbname = "hyc" + (rightnow.getYear() % 10);
			double timechuo = System.currentTimeMillis();
			for (int i = 0; i < vallist.length; i++) {
				collist[i] = "";
				vallist[i] = "";
			}
			for (int i = 0; i < dnvallist.length; i++) {
				dncollist[i] = "";
				dnvallist[i] = "";
			}
			for (String key1 : sinter_yc) {
				if (Pattern.matches(patter, key1) )
				responses.put(key1 + "_.value", p.get(key1 + "_.value"));

			}
			try {
				if (p != null) p.sync();
			} catch (JedisConnectionException e) {
				logger.error("lua err: " + e.toString());
				e.printStackTrace();
			} catch (Exception e) {
				e.printStackTrace();
			}

			for (String key : responses.keySet())
				//for(String key : sinter_yc)
				{


					if (Pattern.matches(patter, key) ) //if (key.indexOf("ai")>=0)

					{
						//logger.warn(key);


						try {

							anamap = (HashMap<String, Object>) anaobj.get(key.replace("_.value",""));

							try {

								saveno = (long) (anamap.get("saveno"));
							} catch (Exception e) {
								logger.warn("err saveno:" + anamap + "  " + e.toString());
								saveno = 0;
							}
							if (saveno != -1) {
								gno = saveno / 200;                     //luaStr = responses.get(k).get().toString();
								vno = (saveno % 200);

								if (jedis.exists(key )) {
									vv = responses.get(key).get().toString(); //jedis.get();
									//logger.warn(vv);
									collist[(int) gno] = collist[(int) gno] + ",val" + vno;
									vallist[(int) gno] = vallist[(int) gno] + "," + vv;


									try {
										saveno = (long) (anamap.get("pulsaveno"));

										if (saveno != -1) {
											//logger.warn(saveno);
											//logger.warn(vv);
											gno = saveno / 200;
											vno = saveno % 200;
											dncollist[(int) gno] = dncollist[(int) gno] + ",val" + vno;
											dnvallist[(int) gno] = dnvallist[(int) gno] + "," + vv;
											//logger.warn(dnvallist[(int)gno]);
											//电量增量每5分钟计算一次，prtupul表的chgtime时标已不需要
										/*if (Pattern.matches(patterdt, savet))
										{
											//此处sql语句待来日优化
											sql="update prtupul set chgtime="+vv+" where saveno="+saveno;

											try{
												logger.info(sql);
												dbcon.setPreparedStatement(sql);
												int t=dbcon.getexecuteUpdate();;
											}catch(Exception e){
												System.out.println("出错了"+e.toString());
											}
										}*/
										}
									} catch (Exception e) {
										logger.error("pulsaveno:" + key + ":" + anamap.toString());
										e.printStackTrace();
										//saveno=0;
									}
								}

							}      //pul对应的ana表的saveno必须要有

						} catch (Exception e) {
							logger.error(key + ":" + e.toString());
							e.printStackTrace();
						}

					}


				}
				//存5分钟遥测
				for (int i = 0; i < vallist.length; i++) {
					sql = "INSERT INTO " + dbname + " (SAVETIME,GROUPNO,CHGTIME" + collist[i] + ") VALUES (" + savet + "," + i + "," + timechuo + vallist[i] + ")";

					//logger.warn(sql);
					//if(rand.equalsIgnoreCase(imagerand)){
					try {

						dbcon.setPreparedStatement(sql);
						 t = dbcon.getexecuteUpdate();
						if (t==0) logger.error(sql);


					} catch (Exception e) {
                        logger.warn( e.toString());
					}
				}

				logger.warn("save histyc:" + savet);
				//logger.warn(dnvallist.length);
				jedis.set("delay_active", rightnow.format(df2));


				//存5分钟电量
				dbname = "hdn" + (rightnow.getYear() % 10);

				for (int i = 0; i < dnvallist.length; i++) {
					sql = "INSERT INTO " + dbname + " (SAVETIME,GROUPNO,CHGTIME" + dncollist[i] + ") VALUES (" + savet + "," + i + "," + timechuo + dnvallist[i] + ")";

					//logger.warn(sql);
					//if(rand.equalsIgnoreCase(imagerand)){
					try {

						dbcon.setPreparedStatement(sql);
						 t = dbcon.getexecuteUpdate();
                        if (t==0) logger.error(sql);


					} catch (Exception e) {
                        logger.warn( e.toString());
					}
				}
				logger.warn("save hdn:" + savet);
				//logger.warn(sql);


				//结算点时间，重新读取anaobj
				if (Pattern.matches(patterdt, savet)) {
					AnaUtil.loadAna_v(dbcon.jdbcTemplate);
				}
				try {
					p.close();
				} catch (IOException ee) {
				}
				//System.gc();
				dbcon.getClose();
				dbcon = null;
				RedisUtil.close(jedis);
				jedis = null;
				responses.clear();



		  }
	     	catch (JedisConnectionException ed) {
             ed.printStackTrace();
           } 
	    	 catch (Exception es) {
	            es.printStackTrace();
	        }
    	//anaobj.clear();
    	//anaobj = null;
    	//sinter_yc.clear();
		//sinter_yc= null;
    	collist= null;
	    vallist= null;
	    dncollist= null;
	    dnvallist= null;
		Runtime.getRuntime().gc();
    }
    	
}
