package com.dl.tool;



import org.quartz.*;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.Pipeline;
import redis.clients.jedis.Response;
import redis.clients.jedis.exceptions.JedisConnectionException;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
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
	   

	@SuppressWarnings("unchecked")
	@Override
    public void execute(JobExecutionContext arg0) throws JobExecutionException {
		 Jedis jedis = null;
		 String patter=null;//".*ai.*";
		String savet = null;
		String dbname =null;
		 //HashMap<String,Object>  anamap =null;// new HashMap<String,Object>();
		 String sql = null;
		 String vv = null;
		 Map<String,Response<String>> responses = null;
		 int maxanaSaveno = 0,maxdnSaveno = 0;
		 long gno=0,vno=0;
		 long saveno =0;
		patter=".*ai.*";
		Connection con = null;
		PreparedStatement pstm = null;
		//System.out.println("saveHistyc start");
    	//DBConnection dbcon=new DBConnection();
		int t =0;
       // JSONObject anaobj =(JSONObject) (arg0.getJobDetail().getJobDataMap().get("taskdetial"));
		//JSONObject anaobj =AnaUtil.objana_v;
       // logger.warn(anaobj);
         maxanaSaveno = Integer.parseInt(AnaUtil.objana_v.get("maxsav").toString());
		 maxdnSaveno = Integer.parseInt(AnaUtil.objana_v.get("maxdnsav").toString());
		// logger.warn(maxdnSaveno);
		// Set<String> sinter_yc= AnaUtil.objana_v.keySet();
		 saveno = 0;
		 gno=0;
		 vno=0;

		 String[] collist = new String[((int)(maxanaSaveno/200)+1)];
		 String[] vallist = new String[((int)(maxanaSaveno/200)+1)];

		 
		 String[] dncollist = new String[((int)(maxdnSaveno/200)+1)];
		 String[] dnvallist = new String[((int)(maxdnSaveno/200)+1)];

		LocalDateTime rightnow = LocalDateTime.now();
		DateTimeFormatter df = DateTimeFormatter.ofPattern("MMddHHmm");
		DateTimeFormatter df2 = DateTimeFormatter.ofPattern("yyyyMMddHHmm");
		responses = new HashMap<String, Response<String>>();


         //logger.info(dzt);
    	try {
			con = FirstClass.getJdbcTemplate().getDataSource().getConnection();
			jedis= JedisUtil.getInstance().getJedis();
			//sinter_yc =  jedis.keys("*ai*");
			Pipeline p = jedis.pipelined();

			//SimpleDateFormat df = new SimpleDateFormat("MMddHHmm");//设置日期格式
			//SimpleDateFormat df2 = new SimpleDateFormat("yyyyMMddHHmm");//设置日期格式
			savet = rightnow.format(df);
			// Calendar c = Calendar.getInstance();//可以对每个时间域单独修改
			 dbname = "hyc" + (rightnow.getYear() % 10);
			double timechuo = System.currentTimeMillis();
			for (int i = 0; i < vallist.length; i++) {
				collist[i] = "";
				vallist[i] = "";
			}
			for (int i = 0; i < dnvallist.length; i++) {
				dncollist[i] = "";
				dnvallist[i] = "";
			}
			for (String key1 : AnaUtil.objana_v.keySet()) {
				if (Pattern.matches(patter, key1) )
				responses.put(key1 + "_.value", p.get(key1 + "_.value"));

			}
			try {
				if (p != null) p.sync();
			} catch (JedisConnectionException e) {
				System.out.println("lua err: " + e.toString());
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

							//anamap = (HashMap<String, Object>) AnaUtil.objana_v.get(key.replace("_.value",""));

							try {

								saveno = (long) ((HashMap<String, Object>) AnaUtil.objana_v.get(key.replace("_.value",""))).get("saveno");
							} catch (Exception e) {
								FirstClass.logger.error("err saveno:" + key + "  " + e.toString());
								saveno = -1;
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
										saveno = (long) (((HashMap<String, Object>) AnaUtil.objana_v.get(key.replace("_.value",""))).get("pulsaveno"));

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
										FirstClass.logger.error("pulsaveno:" + key + ":" + ((HashMap<String, Object>) AnaUtil.objana_v.get(key.replace("_.value",""))).toString());
										e.printStackTrace();
										//saveno=0;
									}
								}

							}      //pul对应的ana表的saveno必须要有

						} catch (Exception e) {
							FirstClass.logger.error(key + ":" + e.toString());
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

						pstm = con.prepareStatement(sql);
						 t = pstm.executeUpdate();



					} catch (Exception e) {
						FirstClass.logger.error( e.toString()+"---->"+sql);
					}
				}

			FirstClass.logger.warn("save histyc:" + savet);
				//logger.warn(dnvallist.length);
				jedis.set("delay_active", rightnow.format(df2));


				//存5分钟电量
				dbname = "hdn" + (rightnow.getYear() % 10);

				for (int i = 0; i < dnvallist.length; i++) {
					sql = "INSERT INTO " + dbname + " (SAVETIME,GROUPNO,CHGTIME" + dncollist[i] + ") VALUES (" + savet + "," + i + "," + timechuo + dnvallist[i] + ")";

					//logger.warn(sql);
					//if(rand.equalsIgnoreCase(imagerand)){
					try {

						pstm = con.prepareStatement(sql);
						t = pstm.executeUpdate();



					} catch (Exception e) {
						System.out.println( e.toString());
					}
				}
			FirstClass.logger.warn("save hdn:" + savet);

				try {
					p.close();
				} catch (IOException ee) {
				}
				//System.gc();
			pstm.close();
			pstm = null;
			con.close();
			con = null;
			JedisUtil.getInstance().returnJedis(jedis);
				responses.clear();
				responses=null;



		  }
	     	catch (JedisConnectionException ed) {
             ed.printStackTrace();
           } 
	    	 catch (Exception es) {
	            es.printStackTrace();
	        }
		savet = null;

		dbname = null;
    	collist= null;
	    vallist= null;
	    dncollist= null;
	    dnvallist= null;

	    patter=null;//".*ai.*";

		//anamap =null;// new HashMap<String,Object>();
		sql = null;
		vv = null;

		// logger.warn(maxdnSaveno);
		 //sinter_yc= null;

		 rightnow = null;
		 df = null;
		 df2 = null;


		Runtime.getRuntime().gc();
    }
    	
}
