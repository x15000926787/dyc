package com.dl.tool;

import com.alibaba.fastjson.JSONObject;
import org.apache.logging.log4j.LogManager;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.regex.Pattern;

/**
 * Desc:系统初始化读取
 * Created by xx on 2019/12/24.
 */
public  class UpdateCkzJob  {
	private static final org.apache.logging.log4j.Logger logger = LogManager.getLogger(UpdateCkzJob.class);

	 synchronized static public void execute() throws SQLException {
		 JSONObject anaobj =AnaUtil.objana_v ;
		 Set<String> sinter_yc= anaobj.keySet();
		 DBConnection dbcon=new DBConnection();
		 String dd = PropertyUtil.getProperty("dztime");
		 DateTimeFormatter df = DateTimeFormatter.ofPattern("MMdd");
		 DateTimeFormatter df2 = DateTimeFormatter.ofPattern("HHmm");
		 LocalDateTime time = LocalDateTime.now();
		 String localTime = df2.format(time);
		 String localDate = df.format(time);
		 int aa = Integer.parseInt(localTime);
		 int bb = Integer.parseInt(dd);
		 ResultSet rs=null;
		 HashMap<String,Object>  anamap = new HashMap<String,Object>();
		 List<Map<String, Object>> tasktype1 = new ArrayList<Map<String, Object>>();
		 Map<String, Object> tmap = new HashMap<String,Object>();
		 String patter=".*ai.*",vv="";
		 long saveno =0,dnsaveno=0;
		 long gno=0,vno=0;
		 if(aa>bb)
		 {
			 localDate = localDate+dd;
		 }
		 else
		 {
			 time = time.plusDays(-1);
			 localDate = df.format(time)+dd;
		 }
		 String sql="select max(savetime) mt from hyc"+(time.getYear()%10)+" where savetime<="+localDate;
		 dbcon.setPreparedStatement(sql);
		 rs = dbcon.getPrepatedResultSet();
		 while(rs.next())
		 {
		 	localDate = rs.getString("mt");
		 }
		 sql = "select * from hyc"+(time.getYear()%10)+" where savetime="+localDate+" order by groupno";
		 try{
			 tasktype1= dbcon.queryforList(sql);

			 for(String key : sinter_yc)
			 {


				 if (Pattern.matches(patter, key)) //if (key.indexOf("ai")>=0)

				 {
					 //anamap.clear();
					 try{

						 anamap = (HashMap<String,Object>)anaobj.get(key);

						 try{
							 dnsaveno = (long) (anamap.get("pulsaveno"));
						 }catch(Exception e)
						 {
							 logger.warn("err saveno:"+anamap+"  "+e.toString());
							 dnsaveno=0;
						 }
						 if (dnsaveno!=-1) {
							 saveno = (long) (anamap.get("saveno"));
							 gno = saveno / 200;
							 vno = (saveno % 200);
							 tmap = tasktype1.get((int)gno);
							 vv = tmap.get("val"+vno).toString();


							 //此处sql语句待来日优化
							 sql="update prtupul set chgtime="+vv+" where saveno="+dnsaveno;

							 try{
								 logger.info(sql);
								 dbcon.setPreparedStatement(sql);
								 int t=dbcon.getexecuteUpdate();;
							 }catch(Exception e){
								
								 logger.info("出错了"+e.toString()+":"+sql);
								 e.printStackTrace();
							 }



						 }      //pul对应的ana表的saveno必须要有

					 }catch(Exception e)
					 {
						 logger.error(key+":"+e.toString());
						 e.printStackTrace();
					 }

				 }



			 }

		 }catch(Exception e){
			 logger.warn("出错了"+e.toString());
			 e.printStackTrace();
		 }

    }
    	
}
