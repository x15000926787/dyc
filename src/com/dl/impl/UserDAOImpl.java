package com.dl.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.bjsxt.thread.ThreadDemo;
import com.dl.tool.RedisUtil;
import com.google.common.io.CharStreams;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import org.joda.time.PeriodType;
import org.junit.Test;
import org.quartz.TriggerUtils;
import org.quartz.impl.triggers.CronTriggerImpl;
import org.springframework.jdbc.core.support.JdbcDaoSupport;
import redis.clients.jedis.Jedis;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import javax.servlet.http.HttpServletRequest;

import java.awt.*;
import java.io.*;
import java.math.BigDecimal;
import java.net.SocketException;
import java.sql.*;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;
import java.util.*;

public class UserDAOImpl
  extends JdbcDaoSupport 
{
	public String[] jzname={"合计",""};
	public String[] evt={"", ""};
	public String username = "";
	static public  Jedis  jedis = null; 
	  
	 static SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    public static ScriptEngineManager scriptEngineManager = new ScriptEngineManager();
    public static ScriptEngine scriptEngine =null;// scriptEngineManager.getEngineByName("nashorn");
	// RedisUtil jpool = new RedisUtil();
	 private static final Logger logger = LogManager.getLogger(UserDAOImpl.class);
	 private UserDAOImpl(){
         scriptEngine = scriptEngineManager.getEngineByName("nashorn");
		 //logger.warn("UserDAOImpl.jpool:"+jpool.toString());
		 //jedis = new Jedis("127.0.0.1",6379);
	    }
	 
	 


  
  public String login(String uEmail, String uPwd, boolean reg)
  {
 
    return "";
  }
  public String sound() throws IOException
  {
	  String jsonString="";
	  Calendar beforeTime = Calendar.getInstance();
	  beforeTime.add(Calendar.MINUTE, -1);// 1分钟之前的时间
	  
	  Date beforeD = beforeTime.getTime();
	  
	  String before5 = new SimpleDateFormat("yyyyMMddHHmmss").format(beforeD);  // 前1分钟时间
	  
	  before5 = before5.substring(2);
	  
	    String sql = "select b.ymd*1000000+b.hms qaData from etype_info a,hevt"+(beforeD.getYear()%10)+" b,prtudig c,project_info d where b.ch=c.rtuno and b.xh=c.sn and c.sound=1 and a.s_type = 1 and  a.e_type=b.EVENT and b.ymd*1000000+b.hms>d.curevttime and d.id=1  and a.e_zt=b.zt  order by ymd,hms desc limit 1 ";
	    log(sql);
	    List userData = getJdbcTemplate().queryForList(sql);
	    java.util.Map<String,Object> map1 = new HashMap<String,Object>();  
	    
         
		
	 
	    if (userData.size() > 0)
	    {
	    	ArrayList kv = new ArrayList();
    		Map listData = (Map)userData.get(0);
    		kv.add(1);
    		//listData.get("qaData").toString() 
    		 String sqlT1 = "update project_info set curevttime=?";
    		    getJdbcTemplate().update(sqlT1, new Object[] { listData.get("qaData").toString() });

	          map1.put("result",1); 
	          map1.put("data",kv);
	    }
	    else
	    	 map1.put("result",0); 
	    jsonString = JSON.toJSONString(map1);
	  return jsonString;
  }
  public String stopsound() throws IOException
  {
	  String jsonString="";
	  Calendar beforeTime = Calendar.getInstance();
	 // beforeTime.add(Calendar.MINUTE, -1);// 1分钟之前的时间
	  java.util.Map<String,Object> map1 = new HashMap<String,Object>();  
	  Date beforeD = beforeTime.getTime();
	  
	  String before5 = new SimpleDateFormat("yyyyMMddHHmmss").format(beforeD);  // 前1分钟时间
	  
	  before5 = before5.substring(2);
	  
	    String sqlT1 = "update project_info set curevttime=?";
	    try {
	    	 getJdbcTemplate().update(sqlT1, new Object[] { before5 });
	    	  map1.put("result",1); 
		} catch (Exception e) {
			  map1.put("result",0); 
		}
	   

	  
	    jsonString = JSON.toJSONString(map1);
	  return jsonString;
  }
  public String rollback(int uid, int aNum, String lv)
  {
    String sql = "select qaData from user_answer" + aNum + " where uID=" + uid;
    List qaData = getJdbcTemplate().queryForList(sql);
    if (qaData.size() > 0)
    {
      Map userMap = (Map)qaData.get(0);
      
      String[] oldArr = userMap.get("qaData").toString().split(",");
      int len = oldArr.length;
      
      String newData = "";
      //log("old arr" + len);
      for (int i = 0; i < len; i++)
      {
        String[] temp = oldArr[i].split("-");
        if (temp[2].equals(lv))
        {
          temp[1] = "0";
          temp[2] = "0";
        }
        if (i != len - 1) {
          newData = newData + temp[0] + "-" + temp[1] + "-" + temp[2] + ",";
        } else {
          newData = newData + temp[0] + "-" + temp[1] + "-" + temp[2];
        }
      }
     // log(newData);
      int backR = 0;
      String aBackSql = "update user_answer" + aNum + " set qaData='" + newData + "' where uID=\t" + uid;
      backR = getJdbcTemplate().update(aBackSql);
    }
    return "";
  }
  
  public String test()
  {
	  String jsonString="";
	    String sql = "SELECT * from prtu";
	    List userData = getJdbcTemplate().queryForList(sql);
	    if (userData.size() > 0)
	    {
	         Map userMap = (Map)userData.get(0);
	          jsonString = JSON.toJSONString(userMap);
	    }
	  
	  return jsonString;
  }
  
 
  /**
   * @desc 启动进程
   * @author zp
   * @date 2018-3-29
   */
  public  void startProgram(String programPath,String programName) throws IOException {  
	    log("启动应用程序：" + programPath);  
	    if (StringUtils.isNotBlank(programPath)) {  
	        try {  
	           // String programName = programPath.substring(programPath.lastIndexOf("/") + 1, programPath.lastIndexOf("."));  
	            List<String> list = new ArrayList<String>();  
	            list.add("cmd.exe");  
	            list.add("/c");  
	            list.add("start");  
	            list.add("\"" + programName + "\"");  
	            list.add("\"" + programPath + "\"");  
	            ProcessBuilder pBuilder = new ProcessBuilder(list);  
	            pBuilder.start();  
	        } catch (Exception e) {  
	            e.printStackTrace();  
	            log("应用程序：" + programPath + "不存在！");  
	        }  
	    }  
	}  
  public  void startProc(String processName,String para) throws IOException { 
       log("启动应用程序：" + processName);  
       try {  
           Runtime r = Runtime.getRuntime();  
           String[] cmd = new String[5];  
             
           cmd[0] = "cmd "; //命令行  
           cmd[1] = "/c "; //运行后关闭，  
           cmd[2] = "start "; //启动另一个窗口来运行指定的程序或命令(cmd 命令集里的)  
           //cmd[3] = "D:\\xampp\\htdocs\\cstation\\w32\\"; //要运行的.exe程序的目录  
           cmd[3] = "C:\\cstation\\w32\\"; 
           cmd[4] =processName+" "+para;// "svm-train -c 32 -g 0.0078125 -v 5 trainset ";//exe程序及其需要的参数  
          // String Naiveexe = "calc.exe";//windows自带计算器  
           String line;  
           String space=" ";  
           //Process p = Runtime.getRuntime().exec("cmd /c svm-train.exe -c 32 -g 0.0078125 -v 5 trainset",null,new File("C:\\libsvm\\windows")); 此时输出到控制台  
           //Process p = Runtime.getRuntime().exec("cmd /c start svm-train.exe -c 32 -g 0.0078125 -v 5 trainset",null,new File("C:\\libsvm\\windows"));此时弹出dos窗口运行  
           Process p = Runtime.getRuntime().exec((cmd[0]+cmd[1]+cmd[2]+cmd[4]),null,new File(cmd[3]));  
           //Process p = Runtime.getRuntime().exec("calc.exe"); //直接运行计算器  
           BufferedReader br = new BufferedReader(new InputStreamReader(p.getInputStream()));  
           while((line=br.readLine()) != null){  
               System.out.println(line);  
               //p.waitFor();  
           }  
       } catch (IOException e) {  
           // TODO Auto-generated catch block  
           e.printStackTrace();  
       } 
  }
  
  
  public static void startProc_n(String processName,String para) { 
	  System.out.println("启动dd应用程序：" + processName);  
      if (StringUtils.isNotBlank(processName)) {  
          try {  
              Desktop.getDesktop().open(new File(processName));  
          } catch (Exception e) {  
              e.printStackTrace();  
              System.out.println("应用程序：" + processName + "不存在！");  
          }  
      }   
 }
 
  /**
   * @desc 杀死进程
   * @author xx
   * @throws IOException 
 * @throws InterruptedException 
   * @date 2018-3-29
   */
  
  public  void killProc(String processName) throws IOException, InterruptedException {  
	  System.out.println("关闭应用程序：" + processName);  
      if (StringUtils.isNotBlank(processName)) {  
          executeCmd("taskkill /F /IM " + processName);  
      } 
  }
  @Test
  public  String runCMDShow(String strcmd) throws Exception 
  { 
	  String str = strcmd;
	  if (strcmd.length()==0) str = "";
      Process p = Runtime.getRuntime().exec("cmd /c start cmd.exe /c " + strcmd+" "); 
      log(strcmd);
      BufferedReader br = new BufferedReader(new InputStreamReader(p.getInputStream()));   
      String readLine = br.readLine();   
      while (readLine != null) { 
          readLine = br.readLine(); 
          System.out.println(readLine); 
      } 
      if(br!=null){ 
          br.close(); 
      } 
      p.destroy(); 
      p=null;
	return readLine; 
  } 
  
  
  
  /**
   * @desc 执行cmd命令 
   * @author xx
 * @throws InterruptedException 
   * @date 2018-3-29
   */
  
  public  String executeCmd(String command) throws IOException, InterruptedException {  
	  log("Execute command :" + command);  
	  int exitVal; 
      Runtime runtime = Runtime.getRuntime();  
      Process process = runtime.exec("cmd /c " + command); 
      exitVal = process.waitFor();
     // BufferedReader br = new BufferedReader(new InputStreamReader(process.getErrorStream(), "GBK"));  
     
      
      
     
      BufferedReader br = new BufferedReader(new InputStreamReader(process.getErrorStream(), "GBK"));  
      String line = null;  
      StringBuilder build = new StringBuilder();  
      while ((line = br.readLine()) != null) {  
    	  log(line);  
          build.append(line);  
      } 
      java.util.Map<String,Object> map1 = new HashMap<String,Object>(); 
      if (build.length()==0){
    	  map1.put("result",1);  
      }
      else {
    	  map1.put("result",0); 
	  }   
	  String jsonString = JSON.toJSONString(map1);
      return jsonString;  
  }  
  /**
   * @desc 判断进程是否开启
   * @author xx
   * @date 2018-3-29
   */
  public static boolean findProcess(String processName) {
      BufferedReader bufferedReader = null;
      try {
          Process proc = Runtime.getRuntime().exec("tasklist -fi " + '"' + "imagename eq " + processName +'"');
          bufferedReader = new BufferedReader(new InputStreamReader(proc.getInputStream()));
          String line = null;
          while ((line = bufferedReader.readLine()) != null) {
              if (line.contains(processName)) {
                  return true;
              }
          }
          return false;
      } catch (Exception ex) {
          ex.printStackTrace();
          return false;
      } finally {
          if (bufferedReader != null) {
              try {
                  bufferedReader.close();
              } catch (Exception ex) {}
          }
      }
  }
  public String rbt() throws IOException
  {
	  String jsonString="0";
	  String jsonString2="0";
	  String[][] arr = new String[3][2];
	  arr[0][0]="cs_main.exe";
	  arr[1][0]="cs_ctrlMbusTcp.exe";
	  arr[2][0]="cs_saveHisYjd.exe";
	  arr[0][1]="";
	  arr[1][1]="ch_0";
	  arr[2][1]="his_yjd";
	 // String url = "D:\\xampp\\htdocs\\cstation\\w32\\";   //
	 // String url = "C:\\cstation\\w32\\"; 
	  java.util.Map<String,Object> map1 = new HashMap<String,Object>();  
        
     // map1.put("data",arr);  
	  
      String procName = "";
      boolean exist=false;
      for (int i=0;i<3;i++)
      {  
    	  
    	  procName = arr[i][0];
    	  exist=findProcess(procName); 
      try {
          if (exist) { 
              // 存在，那么就先杀死该进程
              killProc(procName);
              //暂停10秒
              try {
            	      Thread.sleep(1000);
            	   } catch (Exception e) {
            	      e.printStackTrace();
                  }
             // jsonString=("杀死程序"+procName+"。。。");  
              // 在启动
              //startProc(url+procName+arr[i][1]);
          }else {
              //startProc(url+procName+arr[i][1]);
          }
          map1.put("result",1);
      } catch (Exception e) {
          // TODO: handle exception
    	  log("杀死程序"+procName+"失败。。。");  
    	  map1.put("result",0); 
      }
      }
      /*
      for (int i=0;i<3;i++)
      {  
    	  jsonString2=""+i;
    	  procName = arr[i][0];
    	  exist=findProcess(procName); 
      try {
          
              startProc_n(url+procName,arr[i][1]);
    	 // startProgram(url+procName,procName);
      } catch (Exception e) {
          // TODO: handle exception
    	   log("重启"+procName+"程序失败。。。"); 
    	  jsonString=("重启"+procName+"程序失败。。。");  
      }
      }
      */
      jsonString = JSON.toJSONString(map1);
	  return jsonString;
  }
  public String getTfList()
  {
	  	String jsonString="{\"result\":0}";
	  	String sql ="SELECT SN,P_YEAR||P_NAME p_name from PLAN_NAME order by SN asc";
	    List userData = getJdbcTemplate().queryForList(sql);
	    int size=userData.size() ;
	    if (size> 0)
	    {
			Object[] arr = new Object[size];
	    	for (int i = 0; i<size; i++)
	    	{
				ArrayList kv = new ArrayList();
	    		Map listData = (Map)userData.get(i);
	    		kv.add(listData.get("SN"));
	    		kv.add(listData.get("p_name"));
	    		arr[i]=kv;
	    		
	    		
	    	}
	    	  java.util.Map<String,Object> map1 = new HashMap<String,Object>();  
		      map1.put("result",1);  
	          map1.put("data",arr);  
			  jsonString = JSON.toJSONString(map1);
	    }
	  
	  
	  return jsonString;
  }
  public String getstatus()
  {
	  	String jsonString="{\"result\":0}";
	  	String sql ="SELECT * from prtudig where (sn=0) or rtuno=60 order by rtuno,SN ";
	    List userData = getJdbcTemplate().queryForList(sql);
	    int size=userData.size() ;
	    if (size> 0)
	    {
			Object[] arr = new Object[size];
	    	for (int i = 0; i<size; i++)
	    	{
				ArrayList kv = new ArrayList();
	    		Map listData = (Map)userData.get(i);
	    		kv.add(listData.get("rtuno"));
	    		kv.add(listData.get("SN"));
	    		kv.add(listData.get("val"));
	    		arr[i]=kv;
	    		
	    		
	    	}
	    	  java.util.Map<String,Object> map1 = new HashMap<String,Object>();  
		      map1.put("result",1);  
	          map1.put("data",arr);  
			  jsonString = JSON.toJSONString(map1);
	    }
	  
	  
	  return jsonString;
  }
  public String mysqltest(String pno)
  {
	  	String jsonString="{\"result\":0}";
	  	String sql ="SELECT rtuno,name p_name from prtu where domain="+pno+" order by rtuno asc";
	    List userData = getJdbcTemplate().queryForList(sql);
	    int size=userData.size() ;
	    if (size> 0)
	    {
			Object arr[] = new Object[size];
	    	for (int i = 0; i<size; i++)
	    	{
				ArrayList kv = new ArrayList();
	    		Map listData = (Map)userData.get(i);
	    		kv.add(listData.get("rtuno"));
	    		kv.add(listData.get("p_name"));
	    		arr[i]=kv;
	    		
	    		
	    	}
	    	  java.util.Map<String,Object> map1 = new HashMap<String,Object>();  
		      map1.put("result",1);  
	          map1.put("data",arr);  
			  jsonString = JSON.toJSONString(map1);
	    }
	  
	  
	  return jsonString;
  }

    /**
     * 请求遥测历史数据
     * @param keys   格式为：un_0_.ai_0,un_0_.ai_1,un_0_.ai_2
     * @param len    时间长度，天
     * @param type   1：起始时间为当日零点，2：起始时间为当前时间往前推len*24小时
     * @return       返回5分钟遥测值
     *
     * 注意：对于起止时间跨年的情况，目前还没有啥好办法，暂时先不考虑，等待以后解决。
     */
    public String ask4data(String keys,int len,int type)
    {
        String jsonString="{\"result\":0}";
        String[] savs = keys.split(",");
        String savenos="";
        java.util.Map<String,Object> map1 = new HashMap<String,Object>();
        for (int i=0;i<savs.length;i++)
        {
           savenos = savenos+",'"+savs[i]+"'";
        }
        if (savenos.length()>0) savenos=savenos.substring(1);

        String sql ="SELECT name,saveno,kkey  from prtuana where kkey in ("+savenos+") order by sn ";
        List userData = getJdbcTemplate().queryForList(sql);
        ArrayList arr = new ArrayList();
        ArrayList key = new ArrayList();
        int size=userData.size() ;
        savenos = "";
        if (size> 0)
        {

            for (int i = 0; i<size; i++)
            {
                //
                Map listData = (Map)userData.get(i);
                savenos = savenos + listData.get("saveno").toString()+",";
                //kv.add(listData.get("name"));
                arr.add(listData.get("name"));
                key.add(listData.get("kkey"));


            }
            map1.put("name",arr);
            map1.put("key",key);
        }
        savs = savenos.split(",");
        int maxgno=-1,mingno=99,saveno;
        for (int i=0;i<savs.length;i++)
        {
            int gno = (Integer.parseInt(savs[i])) / 200;
           if (gno>maxgno) maxgno= gno;
           if (gno<mingno) mingno= gno;
        }
        int gnos = maxgno-mingno+1;
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMddHHmm");
        LocalDateTime endtime = LocalDateTime.now();


        endtime = endtime.withMinute(endtime.getMinute()-(endtime.getMinute()%5));
        LocalDateTime starttime = endtime.plusDays(-1*len);
        if (endtime.getYear()>starttime.getYear())
        {
            starttime=starttime.withYear(endtime.getYear()).withMonth(1).withDayOfMonth(1).withHour(0).withMinute(0);

        }
        if (type==2)
        {
            starttime=endtime.withHour(0).withMinute(0);
        }
       // String startstr = starttime




                sql = "SELECT * from hyc"+(endtime.getYear()%10)+" where groupno between "+mingno+" and "+maxgno+" and  savetime between "+starttime.format(formatter)+" and "+endtime.format(formatter)+" order by savetime,groupno ";
logger.warn(sql);
                userData = getJdbcTemplate().queryForList(sql);
                size = userData.size();
                if (size>0) {

                    Object label[] = new Object[size/gnos];
                    for (int i = 0; i < savs.length; i++) {
                        saveno = Integer.parseInt(savs[i]);
                        int gno = (saveno) / 200;
                        int vno = (saveno) % 200;

                        Object val[] = new Object[size/gnos];
                        for (int j = 0; j < (size / gnos); j++) {
                            Map listData = (Map) userData.get(j * gnos + (gno - mingno));
                           // logger.warn(listData);
                            if (i==0) label[j] = listData.get("savetime");
                            val[j] =  listData.get("val"+vno);
                        }
                        if (i==0) map1.put("label",label);
                        map1.put(key.get(i).toString(),val);;

                    }
                    map1.put("result",1);
                    //map1.put("data",arr);
                    jsonString = JSON.toJSONString(map1);

                }

        return jsonString;
    }

    /**
     * 请求日电量结算数据
     * @param keys  格式为：un_0_.ai_0,un_0_.ai_1,un_0_.ai_2
     * @param len   结算周期个数
     * @param type  1：天，2：月
     * @return      电量结算值
     *
     * 注意：对于起止时间跨年的情况，目前还没有啥好办法，暂时先不考虑，等待以后解决。
     */
    public String ask4dndata(String keys,int len,int type)
    {
        String jsonString="{\"result\":0}";
        String[] savs = keys.split(",");
        String savenos="",cols="";
        JSONObject savmap = new JSONObject();
        java.util.Map<String,Object> map1 = new HashMap<String,Object>();
        for (int i=0;i<savs.length;i++)
        {
            savenos = savenos+",'"+savs[i]+"'";
        }
        if (savenos.length()>0) savenos=savenos.substring(1);

        String sql ="SELECT name,saveno,kkey  from prtuana where kkey in ("+savenos+") order by sn ";
        List userData = getJdbcTemplate().queryForList(sql);
        ArrayList arr = new ArrayList();
        ArrayList key = new ArrayList();
        int size=userData.size() ;
        int tsav = 0;
        savenos = "";
        int maxgno=-1,mingno=99,saveno;
        HashMap<String,String> sav2key = new HashMap<>();
        if (size> 0)
        {

            for (int i = 0; i<size; i++)
            {
                //
                Map listData = (Map)userData.get(i);
                savenos = savenos + listData.get("saveno").toString()+",";
                tsav = Integer.parseInt(listData.get("saveno").toString());
                int gno = (tsav) / 200;
                int vno = (tsav) % 200;
                if (gno>maxgno) maxgno= gno;
                if (gno<mingno) mingno= gno;
                if (savmap.containsKey(String.valueOf(gno)))
                {
                    ((ArrayList)savmap.get(String.valueOf(gno))).add(vno);
                }else
                {
                    ArrayList vals = new ArrayList();
                    vals.add(vno);
                    savmap.put(String.valueOf(gno),vals);
                }
                arr.add(listData.get("name"));
                key.add(listData.get("kkey"));
                sav2key.put(String.valueOf(tsav),listData.get("kkey").toString());


            }

        }
        savs = savenos.split(",");


        int gnos = maxgno-mingno+1;
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMddHHmm");
        LocalDateTime endtime = LocalDateTime.now();

        endtime =endtime.withMinute(endtime.getMinute()-(endtime.getMinute()%5));




        LocalDateTime starttime = endtime.plusDays(-1*(len-1)).withHour(0).withMinute(0);
        if (type==2)
        {
            starttime=endtime.plusMonths(-1*(len-1)).withDayOfMonth(1).withHour(0).withMinute(0);

        }
        if (endtime.getYear()>starttime.getYear())
        {
            starttime=starttime.withYear(endtime.getYear()).withMonth(1).withDayOfMonth(1).withHour(0).withMinute(0);

        }

        // String startstr = starttime



        Iterator iter = savmap.entrySet().iterator();
        while (iter.hasNext()) {
            Map.Entry entry = (Map.Entry) iter.next();
            //System.out.println(entry.getKey().toString());
            //System.out.println(entry.getValue().toString());
            int gno=Integer.parseInt(entry.getKey().toString());
            for (int s : (ArrayList<Integer>)entry.getValue()) {
                cols = cols+","+"sum(val"+s+")";
            }
            sql = "SELECT floor(savetime/10000) tt "+cols+" from hyc"+(endtime.getYear()%10)+" where groupno = "+entry.getKey().toString()+" and  savetime between "+starttime.format(formatter)+" and "+endtime.format(formatter)+" group by floor(savetime/10000) order by floor(savetime/10000) ";
            //logger.warn(sql);
            if (type==2)
            {
                sql = "SELECT floor(savetime/1000000) tt "+cols+" from hyc"+(endtime.getYear()%10)+" where groupno = "+entry.getKey().toString()+" and  savetime between "+starttime.format(formatter)+" and "+endtime.format(formatter)+" group by floor(savetime/1000000) order by floor(savetime/1000000) ";
            }
            logger.warn(sql);
            userData = getJdbcTemplate().queryForList(sql);
            size = userData.size();
            if (size>0) {


                for (int i = 0; i < size; i++) {
                    Map listData = (Map) userData.get(i);
                    Set<String> keySet = listData.keySet();
                    //遍历存放所有key的Set集合
                    Iterator<String> it =keySet.iterator();
                    while(it.hasNext()){                         //利用了Iterator迭代器**
                        //得到每一列
                        String col = it.next();
                        //通过key获取对应的value
                        String value = listData.get(col).toString();
                        if (i==0)
                        {
                             ArrayList stt = new ArrayList();
                             stt.clear();
                             stt.add(value);
                             if(col.matches("tt"))   {
                                 savmap.put("savetime",stt);
                             }else
                             {
                                 tsav = gno*200+Integer.parseInt(col.replace("sum(val","").replace(")",""));
                                 savmap.put(sav2key.get(String.valueOf(tsav)),stt);
                             }
                        }else
                        {
                            if(col.matches("tt"))   {
                                ((ArrayList)savmap.get("savetime")).add(value);
                            }else
                            {
                                tsav = gno*200+Integer.parseInt(col.replace("sum(val","").replace(")",""));
                                ((ArrayList)savmap.get(sav2key.get(String.valueOf(tsav)))).add(value);
                            }
                        }

                    }

                }
                savmap.put("name",arr);
                savmap.put("kkey",key);
                map1.put("result",1);
                map1.put("data",savmap);
                jsonString = JSON.toJSONString(map1);

            }

        }


        return jsonString;
    }

  public String getuser(String uid,String gkey)
  {
	  	String jsonString="{\"result\":0}";
        //String gkey = (String) httpSession.getAttribute("groupKey");
        //String uid = (String) httpSession.getAttribute("LOGIN_SUCCESS");
	  	String sql ="SELECT * from msg_user where gkey = '"+gkey+"' order by id";
	  	 log(uid);
	    List userData = getJdbcTemplate().queryForList(sql);
	    int size=userData.size() ;
	    if (size> 0)
	    {
			Object arr[] = new Object[size];
	    	for (int i = 0; i<size; i++)
	    	{
				ArrayList kv = new ArrayList();
	    		Map listData = (Map)userData.get(i);
	    		kv.add(listData.get("auth"));
	    		kv.add(listData.get("name"));
	    		kv.add(listData.get("phone"));
                kv.add(listData.get("phonevalid"));
                kv.add(listData.get("msg_st").toString()+','+listData.get("msg_et").toString());
               // kv.add(listData.get("msg_et"));
	    		kv.add(listData.get("email"));
                kv.add(listData.get("emailvalid"));
                /*if (uid.matches(listData.get("id").toString()))
	    		    kv.add("<a id='subBtn' href='javascript:void(0);' title='保存'><span class='fa fa-save'></span></a>&nbsp;&nbsp;&nbsp;&nbsp;<a id='delBtn' href='javascript:void(0);' title='删除'><span class='glyphicon glyphicon-trash'></span></a>&nbsp;&nbsp;&nbsp;&nbsp;<a id='infoBtn' href='javascript:void(0);' title='修改密码'><span class='fa fa-info-circle' onclick=javascript:getuserauthor("+listData.get("id").toString()+")></span></a>");
	    		else*/
                    kv.add("<a id='subBtn' href='javascript:void(0);' title='保存'><span class='fa fa-save'></span></a>&nbsp;&nbsp;&nbsp;&nbsp;<a id='delBtn' href='javascript:void(0);' title='删除'><span class='glyphicon glyphicon-trash'></span></a>");

                arr[i]=kv;
	    		
	    		
	    	}
	    	  java.util.Map<String,Object> map1 = new HashMap<String,Object>();  
		      map1.put("result",1);  
	          map1.put("data",arr);
            map1.put("uid",uid);
            jsonString = JSON.toJSONString(map1);
	    }
	  
	  
	  return jsonString;
  }
    public String loadUpdown(int rtu,int dev,int pno,int userId)
    {
        String jsonString="{\"result\":0}";

        String sql ="SELECT * from prtuana ";
        if (dev!=-1) sql = sql+" where deviceno="+dev;
        else sql=sql+" where rtuno in (select rtuno from prtu where domain="+pno+")";

        sql= sql +"  and (power(2,"+userId+"-1)&author_read)>0 order by sn";
       // log("aaa:"+sql);
        List userData = getJdbcTemplate().queryForList(sql);
        int size=userData.size() ;
        if (size> 0)
        {
            Object arr[] = new Object[size];
            for (int i = 0; i<size; i++)
            {
                ArrayList kv = new ArrayList();
                Map listData = (Map)userData.get(i);
                kv.add(listData.get("saveno"));
                kv.add(listData.get("name"));
                kv.add(listData.get("upperlimit"));
                kv.add(listData.get("lowerlimit"));
                kv.add(listData.get("author_alert"));

                kv.add(listData.get("author_msg"));
                kv.add(listData.get("author_email"));
                kv.add(listData.get("timevalid"));
                kv.add(listData.get("timecondition"));
                kv.add(listData.get("warnline"));
                kv.add(listData.get("online"));
                kv.add("<a id='subBtn' href='javascript:void(0);' onclick=\"resettime('"+listData.get("saveno")+"')\" title='计时重置'><span class='fa fa-history'></span></a>");
                arr[i]=kv;


            }
            java.util.Map<String,Object> map1 = new HashMap<String,Object>();
            map1.put("result",1);
            map1.put("data",arr);
            jsonString = JSON.toJSONString(map1);
        }


        return jsonString;
    }
    public String loadUpdown_dig(int rtu,int dev,int pno,int userId)
    {
        String jsonString="{\"result\":0}";

        String sql ="SELECT * from prtudig ";
        if (dev!=-1) sql = sql+" where deviceno="+dev;
        else sql=sql+" where rtuno in (select rtuno from prtu where domain="+pno+")";

        sql= sql +"  and (power(2,"+userId+"-1)&author_read)>0 order by sn";
        // log("aaa:"+sql);
        List userData = getJdbcTemplate().queryForList(sql);
        int size=userData.size() ;
        if (size> 0)
        {
            Object arr[] = new Object[size];
            for (int i = 0; i<size; i++)
            {
                ArrayList kv = new ArrayList();
                Map listData = (Map)userData.get(i);
                kv.add(listData.get("kkey"));
                kv.add(listData.get("name"));
               /* kv.add(listData.get("upperlimit"));
                kv.add(listData.get("lowerlimit"));*/
                kv.add(listData.get("author_alert"));

                kv.add(listData.get("author_msg"));
                kv.add(listData.get("author_email"));
                kv.add(listData.get("timevalid"));
                kv.add(listData.get("timecondition"));
                kv.add(listData.get("warnline"));
                kv.add(listData.get("online"));
                kv.add("<a id='subBtn' href='javascript:void(0);' onclick=\"resettime('"+listData.get("kkey")+"')\" title='计时重置'><span class='fa fa-history'></span></a>");

                arr[i]=kv;


            }
            java.util.Map<String,Object> map1 = new HashMap<String,Object>();
            map1.put("result",1);
            map1.put("data",arr);
            jsonString = JSON.toJSONString(map1);
        }


        return jsonString;
    }
  public String gettree(String pno)  
  {
	  
	  String jsonString="";
	  String rtuString="";
	  	String sql ="SELECT rtuno,name p_name from prtu where ananum>0 and domain="+pno+" order by rtuno asc";
	  	log("sql"+sql);	
	    List userData = getJdbcTemplate().queryForList(sql);
	    int size=userData.size() ;
	    if (size> 0)
	    {
			Object arr[] = new Object[size];
	    	for (int i = 0; i<size; i++)
	    	{
				
	    		Map listData = (Map)userData.get(i);
	    		rtuString = "{\"text\":\""+listData.get("p_name")+"\",\"a_attr\":{\"no\":\""+listData.get("rtuno")+"\"}}";
	    		if (i==0)
	    		{
	    		  jsonString = rtuString;
	    		}
	    		else {
					jsonString = jsonString + "," + rtuString;
				}
	
	    	}
	    	  
	    }
	  
	  	 jsonString="["+jsonString+"]"; 
	  return jsonString;
  }
  public String loadconfig(String dn,String nm,int lev)throws IOException
  {
	  //String jsonString="[{\"text\":\"childnode 1\",\"a_attr\":{\"no\":\"0\"}},{\"text\":\"childnode 2\",\"a_attr\":{\"no\":\"1\"}}]";
	  String jsonString="";
	  String rtuString="";
	  	String sql ="";
	  	if (lev==1)
	  	{
	  		sql = "select distinct get_subs(name,1) p_name,rtuno from prtuana where rtuno="+dn;
	  	}else {
	  		sql = "select get_subs(name,0) p_name,saveno rtuno from prtuana where rtuno="+dn+" and name like '"+nm+"%' order by saveno";
		}
	  	log(sql);		
	    List userData = getJdbcTemplate().queryForList(sql);
	    int size=userData.size() ;
	    if (size> 0)
	    {
			Object arr[] = new Object[size];
	    	for (int i = 0; i<size; i++)
	    	{
				
	    		Map listData = (Map)userData.get(i);
	    		rtuString = "{\"text\":\""+listData.get("p_name")+"\",\"a_attr\":{\"no\":\""+listData.get("rtuno")+"\"}}";
	    		if (i==0)
	    		{
	    		  jsonString = rtuString;
	    		}
	    		else {
					jsonString = jsonString + "," + rtuString;
				}
	
	    	}
	    	  
	    }
	  
	  	 jsonString="["+jsonString+"]"; 	
	  
	  
	  return jsonString;
  }
  public String loadconfig_xb(String dn,String nm,int lev)
  {
	  //String jsonString="[{\"text\":\"childnode 1\",\"a_attr\":{\"no\":\"0\"}},{\"text\":\"childnode 2\",\"a_attr\":{\"no\":\"1\"}}]";
	  String jsonString="";
	  String rtuString="";
	  String isav="";
	  String usav="";
	  	String sql ="";
	  	if (lev==1)
	  	{
	  		sql = "select distinct get_subs(name,1) p_name,rtuno from prtuana where rtuno="+dn+" and type=7";
	  	}else {
	  		sql = "select get_subs(name,0) p_name,saveno rtuno from prtuana where rtuno="+dn+" and name like '"+nm+"%电流%' and type=7 order by saveno";
		}
	  			
	    List userData = getJdbcTemplate().queryForList(sql);
	    int size=userData.size() ;
	    if (size> 0)
	    {
			Object arr[] = new Object[size];
	    	for (int i = 0; i<size; i++)
	    	{
				
	    		Map listData = (Map)userData.get(i);
	    		rtuString = "{\"text\":\""+listData.get("p_name")+"\",\"a_attr\":{\"no\":\""+listData.get("rtuno")+"\"}}";
	    		if (i==0)
	    		{
	    		  jsonString = rtuString;
	    		}
	    		else {
					jsonString = jsonString + "," + rtuString;
				}
	
	    	}
	    	  
	    }
	  
	  	 jsonString="["+jsonString+"]"; 	
	  
	  
	  return jsonString;
  }
  public String loadconfig_dl(String dn,String nm,int lev)
  {
	  //String jsonString="[{\"text\":\"childnode 1\",\"a_attr\":{\"no\":\"0\"}},{\"text\":\"childnode 2\",\"a_attr\":{\"no\":\"1\"}}]";
	  String jsonString="";
	  String rtuString="";
	  String isav="";
	  String usav="";
	  	String sql ="";
	  	if (lev==1)
	  	{
	  		sql = "select distinct get_subs(name,1) p_name,rtuno from prtuana where rtuno="+dn+" and type=2";
	  	}else {
	  		sql = "select get_subs(name,0) p_name,saveno rtuno from prtuana where rtuno="+dn+" and name like '"+nm+"%' and type=2 order by saveno";
		}
	  			
	    List userData = getJdbcTemplate().queryForList(sql);
	    int size=userData.size() ;
	    if (size> 0)
	    {
			Object arr[] = new Object[size];
	    	for (int i = 0; i<size; i++)
	    	{
				
	    		Map listData = (Map)userData.get(i);
	    		rtuString = "{\"text\":\""+listData.get("p_name")+"\",\"a_attr\":{\"no\":\""+listData.get("rtuno")+"\"}}";
	    		if (i==0)
	    		{
	    		  jsonString = rtuString;
	    		}
	    		else {
					jsonString = jsonString + "," + rtuString;
				}
	
	    	}
	    	  
	    }
	  
	  	 jsonString="["+jsonString+"]"; 	
	  
	  
	  return jsonString;
  }
  public String loadrtuno(String pno)
  {
	  //String jsonString="[{\"text\":\"childnode 1\",\"a_attr\":{\"no\":\"0\"}},{\"text\":\"childnode 2\",\"a_attr\":{\"no\":\"1\"}}]";
	  String jsonString="";
	  String rtuString="";
	  String isav="";
	  String usav="";
	  	String sql ="";
	  
	  		sql = "select name , rtuno from prtu where domain="+pno+" and  name is not null order by rtuno";
		
	  	logger.warn(sql);		
	    List userData = getJdbcTemplate().queryForList(sql);
	    int size=userData.size() ;
	    if (size> 0)
	    	//log(size);
	    {
			
	    	for (int i = 0; i<size; i++)
	    	{
				
	    		Map listData = (Map)userData.get(i);
	    		
	    		
	    		rtuString = "{\"value\":\""+listData.get("rtuno")+"\",\"name\":\""+listData.get("name")+"\"}";
	    		//log(rtuString);
	    		if (i==0)
	    		{
	    		  jsonString = rtuString;
	    		}
	    		else {
					jsonString = jsonString + "," + rtuString;
				}
	
	    	}
	    	  
	    }
	  
	  	 jsonString="["+jsonString+"]"; 	
	  
	  
	  return jsonString;
  }
  public String loaddsdev()
  {
	  //String jsonString="[{\"text\":\"childnode 1\",\"a_attr\":{\"no\":\"0\"}},{\"text\":\"childnode 2\",\"a_attr\":{\"no\":\"1\"}}]";
	  String jsonString="";
	  String rtuString="";
	  String isav="";
	  String usav="";
	  	String sql ="";
	  
	  		sql = "select distinct name , sn from time_plan_dev  order by sn";
		
	  			
	    List userData = getJdbcTemplate().queryForList(sql);
	    int size=userData.size() ;
	    if (size> 0)
	    	//log(size);
	    {
			
	    	for (int i = 0; i<size; i++)
	    	{
				
	    		Map listData = (Map)userData.get(i);
	    		
	    		
	    		rtuString = "{\"value\":\""+listData.get("sn")+"\",\"name\":\""+listData.get("name")+"\"}";
	    		//log(rtuString);
	    		if (i==0)
	    		{
	    		  jsonString = rtuString;
	    		}
	    		else {
					jsonString = jsonString + "," + rtuString;
				}
	
	    	}
	    	  
	    }
	  
	  	 jsonString="["+jsonString+"]"; 	
	  
	  
	  return jsonString;
  }
  public String loaddsdev_temp()
  {
	  //String jsonString="[{\"text\":\"childnode 1\",\"a_attr\":{\"no\":\"0\"}},{\"text\":\"childnode 2\",\"a_attr\":{\"no\":\"1\"}}]";
	  String jsonString="";
	  String rtuString="";
	  String isav="";
	  String usav="";
	  	String sql ="";
	  
	  		sql = "select distinct name , sn from time_plan_dev_temp  order by sn";
		
	  			
	    List userData = getJdbcTemplate().queryForList(sql);
	    int size=userData.size() ;
	    if (size> 0)
	    	//log(size);
	    {
			
	    	for (int i = 0; i<size; i++)
	    	{
				
	    		Map listData = (Map)userData.get(i);
	    		
	    		
	    		rtuString = "{\"value\":\""+listData.get("sn")+"\",\"name\":\""+listData.get("name")+"\"}";
	    		//log(rtuString);
	    		if (i==0)
	    		{
	    		  jsonString = rtuString;
	    		}
	    		else {
					jsonString = jsonString + "," + rtuString;
				}
	
	    	}
	    	  
	    }
	  
	  	 jsonString="["+jsonString+"]"; 	
	  
	  
	  return jsonString;
  }
  public String loaddsdevn()
  {
	  //String jsonString="[{\"text\":\"childnode 1\",\"a_attr\":{\"no\":\"0\"}},{\"text\":\"childnode 2\",\"a_attr\":{\"no\":\"1\"}}]";
	  String jsonString="";
	  String rtuString="";
	  String isav="";
	  String usav="";
	  	String sql ="";
	  
	  		sql = "select distinct name , sn from time_plan_dev order by sn";
		
	  			
	    List userData = getJdbcTemplate().queryForList(sql);
	    int size=userData.size() ;
	    if (size> 0)
	    	//log(size);
	    {
			
	    	for (int i = 0; i<size; i++)
	    	{
				
	    		Map listData = (Map)userData.get(i);
	    		
	    		
	    		rtuString = "{\"value\":\""+listData.get("sn")+"\",\"name\":\""+listData.get("name")+"\"}";
	    		//log(rtuString);
	    		if (i==0)
	    		{
	    		  jsonString = rtuString;
	    		}
	    		else {
					jsonString = jsonString + "," + rtuString;
				}
	
	    	}
	    	  
	    }
	  
	  	 jsonString="["+jsonString+"]"; 	
	  
	  
	  return jsonString;
  }
  public String loadconfig_dn(String dn,String nm,int lev) throws IOException
  {
	  //String jsonString="[{\"text\":\"childnode 1\",\"a_attr\":{\"no\":\"0\"}},{\"text\":\"childnode 2\",\"a_attr\":{\"no\":\"1\"}}]";
	  String jsonString="";
	  String rtuString="";
	  	String sql ="";
	  	if (lev==1)
	  	{
	  		sql = "select distinct get_subs(name,1) p_name,rtuno from prtupul where rtuno="+dn;
	  	}else {
	  		sql = "select get_subs(name,0) p_name,saveno rtuno  from prtupul where rtuno="+dn+" and name like '%"+nm+"%' order by saveno";
		}
	  	log(sql);		
	    List userData = getJdbcTemplate().queryForList(sql);
	    int size=userData.size() ;
	    if (size> 0)
	    {
			Object arr[] = new Object[size];
	    	for (int i = 0; i<size; i++)
	    	{
				
	    		Map listData = (Map)userData.get(i);
	    		rtuString = "{\"text\":\""+listData.get("p_name")+"\",\"a_attr\":{\"no\":\""+listData.get("rtuno")+"\"}}";
	    		if (i==0)
	    		{
	    		  jsonString = rtuString;
	    		}
	    		else {
					jsonString = jsonString + "," + rtuString;
				}
	
	    	}
	    	  
	    }
	  
	  	 jsonString="["+jsonString+"]"; 	
	  //log(jsonString);
	  
	  return jsonString;
  }
  public String loaddsplan(int sn)
  {
	  	String jsonString="{\"result\":0}";
	  	String sql ="SELECT week,t_open,t_close,on_off from TIME_PLAN WHERE SN="+sn+" order by week asc";
	    List userData = getJdbcTemplate().queryForList(sql);
	    int size=userData.size() ;
	    if (size> 0)
	    {
			Object arr[] = new Object[size];
	    	for (int i = 0; i<size; i++)
	    	{
				ArrayList kv = new ArrayList();
	    		Map listData = (Map)userData.get(i);
	    		kv.add("gp_name");
	    		kv.add("da_name");
	    		kv.add(listData.get("week"));
	    		kv.add(listData.get("t_open"));
	    		kv.add(listData.get("t_close"));
	    		kv.add(listData.get("on_off"));
	    		arr[i]=kv;
	    		
	    		
	    	}
	    	  java.util.Map<String,Object> map1 = new HashMap<String,Object>();  
		      map1.put("result",1);  
	          map1.put("data",arr);  
			  jsonString = JSON.toJSONString(map1);
	    }
	  
	  
	  return jsonString;
  }
  public String getyTfList()
  {
	  	String jsonString="{\"result\":0}";
	  	String sql ="SELECT SN,P_YEAR||P_NAME p_name from PLAN_NAME order by SN asc";
	    List userData = getJdbcTemplate().queryForList(sql);
	    int size=userData.size() ;
	    if (size> 0)
	    {
			Object arr[] = new Object[size];
	    	for (int i = 0; i<size; i++)
	    	{
				ArrayList kv = new ArrayList();
	    		Map listData = (Map)userData.get(i);
	    		kv.add(listData.get("SN"));
	    		kv.add(listData.get("p_name"));
	    		arr[i]=kv;
	    		
	    		
	    	}
	    	  java.util.Map<String,Object> map1 = new HashMap<String,Object>();  
		      map1.put("result",1);  
	          map1.put("data",arr);  
			  jsonString = JSON.toJSONString(map1);
	    }
	  
	  
	  return jsonString;
  }
  
  public Object[][] js(Date startDate,String startTime,String endTime) throws ParseException
  {
		 	SimpleDateFormat sd1=new SimpleDateFormat("yyyy-MM-dd");
		 	Date tempTime1=sd1.parse(startTime);
		 	SimpleDateFormat sd2=new SimpleDateFormat("yyyy-MM-dd");
		 	Date tempTime2=sd2.parse(endTime);
		 	
			 int gapDate= (int) (( tempTime2.getTime()-tempTime1.getTime())/(1000*60*60*24));
			 //log(gapDate);
			 Object[][] dateArr=new Object[ gapDate][2];
			 SimpleDateFormat xDate=new SimpleDateFormat("yy-MM-dd");
	    	for (int i = 0; i<gapDate; i++)
	    	{
	    		Date nextDate=new Date(tempTime1.getTime()+(1000*60*60*24*(i+1)));
	    		dateArr[i][0]=intervalTime(startDate,nextDate,30);
	    		dateArr[i][1]=xDate.format(nextDate);
	    	}
	  return dateArr;
  }
  
  public int intervalTime(Date startTime,Date endTime,int gap)
  {
	 return (int)( ( endTime.getTime()-startTime.getTime())/1000/60/gap);
  }
  
  
  public String getMainRealGl(String crew,int year)
  {
	  String jsonString="{\"result\":0}";
	  String sql = "select TAGNAME,SAVETIME,VAL,time4,pointinfo ,to_char(SAVETIME,'mm-dd') fd from REAL_GL left join (SELECT to_char(TB2.TIME1,'yyyymmdd') TIME4, LTRIM(MAX(SYS_CONNECT_BY_PATH(TB2.TEXT1, '<br>')), '<br>') pointInfo FROM (SELECT TB1.TEXT1, TB1.TIME1,ROW_NUMBER() OVER(PARTITION BY to_char(TB1.TIME1,'yyyymmdd') ORDER BY TB1.TEXT1 asc) RN FROM (SELECT POLY_POINT.M_NAME,POLY_POINT.TIME1,to_char(POLY_POINT.TIME1,'yyyy-mm-dd hh24:mi')||' '||POLY_POINT.M_NAME || ','||POLY_CASE.TEXT1 ||',' ||POLY_POINT.TEXT1 TEXT1 FROM POLY_POINT left join POLY_CASE on (POLY_POINT.CASE_SN = POLY_CASE.SN ) WHERE POLY_POINT.M_NAME='"+crew+"' ORDER BY POLY_POINT.TIME1 ASC) tb1) tb2 START WITH RN = 1 CONNECT BY RN - 1 = PRIOR RN　　　 AND to_char(TB2.TIME1,'yyyymmdd') = PRIOR to_char(TB2.TIME1,'yyyymmdd')GROUP BY to_char(TB2.TIME1,'yyyymmdd') ORDER BY to_char(TB2.TIME1,'yyyymmdd') asc) TTT on (to_char(REAL_GL.SAVETIME,'yyyymmdd') =TTT.TIME4) where TAGNAME='"+crew+"' and (to_char(SAVETIME,'yyyy') = '"+year+"') and (to_char(SAVETIME,'hh24miss') = '000000' or to_char(SAVETIME,'hh24miss') = '080000' or to_char(SAVETIME,'hh24miss') = '160000') ORDER BY SAVETIME asc";
	    List userData = getJdbcTemplate().queryForList(sql);
	    if (userData.size() > 0)
	    {
	    	int size=userData.size();
			Object arr[] = new Object[size];
			ArrayList labels = new ArrayList();
			ArrayList pointInfos = new ArrayList();
			ArrayList vals = new ArrayList();
	    	for (int i = 0; i<size; i++)
	    	{
	    		Map userMap = (Map)userData.get(i);
				ArrayList xCell = new ArrayList();
				double val = Double.parseDouble(userMap.get("VAL").toString());
				String time = userMap.get("SAVETIME").toString();
				String pointInfo = (String) userMap.get("POINTINFO");
	    		time=time.substring(0, time.length()-11);
				if(pointInfo==null)
				{
					pointInfo="";
				}
				vals.add(val);
				labels.add(time);
				pointInfos.add(pointInfo.split("<br>"));
				arr[i] = xCell;
	    	}
			Map<String, Object> map1 = new HashMap<String, Object>();
	        map1.put("result",1);  
	        map1.put("labels",labels);  
			map1.put("data", vals);
	        map1.put("pointInfos",pointInfos);  
			jsonString = JSON.toJSONString(map1);
	    }
	  
	  return jsonString;
  }
  public String get_vals(String dt,String nm,int devno,String pno,int userId) throws IOException, ScriptException {
	  	String jsonString="{\"result\":0}";
	  	int rows=0;
	  	long c1=0;
	  	long c2=0;


      //System.out.println(dt);
	  	List<String> disno = new ArrayList<String>();
	  	List<String> disnm = new ArrayList<String>();
	  	//FileOutputStream os = new FileOutputStream("C://exp/exp_"+devno+"_"+dt+".xlsx");
	  	//FileOutputStream os = new FileOutputStream("./exp_"+devno+"_"+dt+".xlsx");
		//XSSFWorkbook wb = new XSSFWorkbook();
		
		//XSSFSheet sheet = wb.createSheet("data_exp");
      //

	  	
		 // 创建两种单元格格式
	       // CellStyle cs = wb.createCellStyle();
	      //  CellStyle cs2 = wb.createCellStyle();

	        // 创建两种字体
	      //  XSSFFont f = wb.createFont();
	       // XSSFFont f2 = wb.createFont();

	      //  // 创建第一种字体样式（用于列名）
	      //  f.setFontHeightInPoints((short) 10);
	      //  f.setColor(IndexedColors.BLACK.getIndex());
	      //  f.setBoldweight(XSSFFont.BOLDWEIGHT_BOLD);

	        // 创建第二种字体样式（用于值）
	       // f2.setFontHeightInPoints((short) 10);
	       // f2.setColor(IndexedColors.BLACK.getIndex());

	        // 设置第一种单元格的样式（用于列名）
	       // cs.setFont(f);
	       // cs.setBorderLeft(CellStyle.BORDER_THIN);
	      //  cs.setBorderRight(CellStyle.BORDER_THIN);
	      //  cs.setBorderTop(CellStyle.BORDER_THIN);
	       // cs.setBorderBottom(CellStyle.BORDER_THIN);
	      //  cs.setAlignment(CellStyle.ALIGN_CENTER);

	        // 设置第二种单元格的样式（用于值）
	       // cs2.setFont(f2);
	     //   cs2.setBorderLeft(CellStyle.BORDER_THIN);
	     //   cs2.setBorderRight(CellStyle.BORDER_THIN);
	     //   cs2.setBorderTop(CellStyle.BORDER_THIN);
	     //   cs2.setBorderBottom(CellStyle.BORDER_THIN);
	     //   cs2.setAlignment(CellStyle.ALIGN_CENTER);



		
		//XSSFRow row1 = sheet.createRow(0);
		
	
		//XSSFCell cell = row1.createCell(0);
		
		// cell.setCellStyle(cs);
       // cell.setCellValue(nm);
		
		
       //  row1 = sheet.createRow(1);
        
    	
		// cell = row1.createCell(0);
		// cell.setCellStyle(cs);

       // cell.setCellValue(dt.substring(0,4)+"-"+dt.substring(4,6)+"-"+dt.substring(6,8)+" "+dt.substring(8,10)+":"+dt.substring(10,12));
           //  FileOutputStream os = new FileOutputStream("C:\\temp.xlsx");
              

	  	int saveno = 0;
	  	String devnm=null;
	  	/*
	  	if (lev==1)
	  	{
	  		sql = "select distinct get_subs(name,1) p_name,rtuno from prtuana where rtuno="+dn;
	  	}else {
	  		sql = "select get_subs(name,0) p_name,saveno rtuno from prtuana where rtuno="+dn+" and name like '%"+nm+"%' order by saveno";
		}*/
	  	String sql ="SELECT distinct b.devicenm dis from  prtuana a,dev_author b,room c where (power(2,"+userId+"-1)&a.author_read)>0 and  a.deviceno=b.deviceno and c.roomno =b.roomno and c.rtuno="+devno+" order by   b.devicenm ";
      log(sql);
	  	List userData = getJdbcTemplate().queryForList(sql);
	    int size=userData.size() ;
	    Object arrdis[] = new Object[size];
      Object arrdis2[] = new Object[size];
	    long len[] = new long[size];
	    if (size> 0)
	    {
			
	    	for (int i = 0; i<size; i++)
	    	{	
	    		Map listData = (Map)userData.get(i);
	    		disnm.add(listData.get("dis").toString());
	    		devnm=listData.get("dis").toString().replace("#", "").replace("(", "").replace(")", "");
	    		disno.add(devnm);	
	    		arrdis[i]=disnm.get(i);
	    		arrdis2[i]=disno.get(i);
	    		
	    		
	    		
	    		
	    	}	 
	     }
	    sql ="SELECT count(*) cnt, b.devicenm  from prtuana a,dev_author b,room c where (power(2,"+userId+"-1)&a.author_read)>0 and a.deviceno=b.deviceno and c.roomno =b.roomno and c.rtuno="+devno+" group by   b.devicenm ";
      log(sql);
	     userData = getJdbcTemplate().queryForList(sql);
	     size=userData.size() ;
	  
	    if (size> 0)
	    {
			
	    	for (int i = 0; i<size; i++)
	    	{	
	    		Map listData = (Map)userData.get(i);
	    		len[i] =  (long) listData.get("cnt");
	    		//log(len[i]);
	    		//dev.add(listData.get("deviceno").toString());
	    		
	    	}	 
	     }
      List<String> dev = new ArrayList<String>();
	  	List<String> val = new ArrayList<String>();
	  	List<String> nms = new ArrayList<String>();
	  	List<String> sav = new ArrayList<String>();
      List<String> up = new ArrayList<String>();
      List<String> lower = new ArrayList<String>();
	  	//List<String> dev = new ArrayList<String>();
	    saveno = 0;
	  	 sql ="SELECT  a.name,a.saveno, b.devicenm,a.upperlimit,a.lowerlimit from prtuana a,dev_author b,room c where (power(2,"+userId+"-1)&a.author_read)>0 and a.deviceno=b.deviceno and c.roomno =b.roomno and c.rtuno="+devno+" order by   b.devicenm ";
      log(sql);
	  	 userData = getJdbcTemplate().queryForList(sql);
	     size=userData.size() ;
	    Object arr[] = new Object[size];
	    Object arr2[] = new Object[size];
	    Object arr3[] = new Object[size];
      Object color[] = new Object[size];
	    if (size> 0)
	    {
			
	    	for (int i = 0; i<size; i++)
	    	{	
	    		Map listData = (Map)userData.get(i);
	    		nms.add(listData.get("name").toString());
	    		sav.add(listData.get("saveno").toString());
                up.add(listData.get("upperlimit").toString());
                lower.add(listData.get("lowerlimit").toString());
	    		devnm=listData.get("devicenm").toString().replace("#", "").replace("(", "").replace(")", "");
	    		dev.add(devnm);
	    		//dev.add(listData.get("deviceno").toString());
	    		
	    	}	 
	     }
      //System.out.println(dt);
	    sql ="SELECT * FROM hyc"+dt.substring(3,4)+" where savetime = "+dt.substring(4,12)+"  and chgtime is not null order by groupno";
      log(sql);
	    userData = getJdbcTemplate().queryForList(sql);
	    size=userData.size() ;
	    if (size> 0)
	    {
			
	    	for (int i = 0; i<nms.size(); i++)
	    	{	
	    		saveno = Integer.parseInt(sav.get(i));
	    		int gno=(int)(saveno)/200;
	 	  	    int vno=(saveno)%200;
	    		Map listData = (Map)userData.get(gno);
	    		val.add(listData.get("val"+vno).toString());
               // System.out.println(listData.get("val"+vno).toString()+">"+lower.get(i)+" && "+listData.get("val"+vno).toString()+"<"+up.get(i));
                if ((boolean)scriptEngine.eval(listData.get("val"+vno).toString()+">"+lower.get(i)+" && "+listData.get("val"+vno).toString()+"<"+up.get(i)))
                    color[i]=0;
                else
                    color[i]=1;
                //System.out.println(listData.get("val"+vno).toString()+">"+lower.get(i)+" && "+listData.get("val"+vno).toString()+"<"+up.get(i));
	    		arr[i]=nms.get(i);
	    		arr2[i]=val.get(i);
	    		arr3[i]=dev.get(i);
	    		
	    	}	
	    	 java.util.Map<String,Object> map1 = new HashMap<String,Object>();  
		      map1.put("result",1);  
	          map1.put("data",arr); 
	          map1.put("data2",arr2);
	          map1.put("data3",arr3);
	          map1.put("data4",arrdis);
	          map1.put("data5",arrdis2);
            map1.put("data6",color);
	          rows=1;
	          /*for (int i=0;i< arrdis.length;i++)
	          {
	        	    rows++;
	        	    row1 = sheet.createRow(rows);
		    		cell = row1.createCell(0);
		            cell.setCellValue((String)arrdis[i]);
		            c2=c2+len[i];
		            rows++;
		            row1 = sheet.createRow(rows);
		            for (long j = c1; j < c2; j++) {
		            	
		            	row1 = sheet.getRow(rows);
			    		cell = row1.createCell((int) (j-c1)*2);
			    		 cell.setCellStyle(cs2);
			            cell.setCellValue((String)arr[(int)j]);
			            cell = row1.createCell((int) (j-c1)*2+1);
			            cell.setCellStyle(cs2);
			            cell.setCellValue((String)arr2[(int)j]);
					}
		            c1=c2;
		            
		            
		            
		            
	        	  
	          }*/
	         
	          
	          
	        /*    row1 = sheet.getRow(2);
	    		cell = row1.createCell(1);
	    		

	            cell.setCellValue("fssssdas");*/
	          
	          
	          
			  jsonString = JSON.toJSONString(map1);	
	    	
	     }
	   // sheet.autoSizeColumn((short)0); //调整第一列宽度
       // sheet.autoSizeColumn((short)1); //调整第二列宽度
       // sheet.autoSizeColumn((short)2); //调整第三列宽度
       // sheet.autoSizeColumn((short)3); //调整第四列宽度
	  //  wb.write(os);
     //   os.close();
	  
	  return jsonString;
  }
  public String savedsplan(int all,String id,String oof,String op,String cl ) throws SQLException, ParseException
  {
	  String jsonString="{\"result\":0}";
	  
	   String[] oofstr = oof.split(",");
	   String[] opstr = op.split(",");
	   String[] clstr = cl.split(",");
	   Connection con = null;   
	   PreparedStatement pstm = null; 	    
	   Statement stm = null;   
	   String sql="";
	   int hhmmop = 0;
	   int hhmmcl = 0;
	   try 
	   {   
		   con = getJdbcTemplate().getDataSource().getConnection();
		   con.setAutoCommit(false);
		   stm = con.createStatement();   
		  
		   for (int e = 1; e <  oofstr.length; e++)//00 : 00
			{
				hhmmop = Integer.parseInt(opstr[e].substring(0, 2))*60+Integer.parseInt(opstr[e].substring(5, 7));
				hhmmcl = Integer.parseInt(clstr[e].substring(0, 2))*60+Integer.parseInt(clstr[e].substring(5, 7));
				sql="UPDATE TIME_PLAN set ON_OFF = "+oofstr[e]+" , t_open ="+hhmmop+", t_close="+hhmmcl+" where week="+(e-1);
				if (all==0) sql=sql+" AND SN="+id;
				log(sql);
				stm.addBatch(sql);
			}
		   stm.executeBatch();   
		   con.commit();
		   con.setAutoCommit(true);
	    	  Map<String,Object> map1 = new HashMap<String,Object>();  
	          map1.put("result",1);  
	          jsonString = JSON.toJSONString(map1);	 
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
       }finally
       {   
    	   stm.close();
    	   stm = null;
    	    con.close();
    	    con = null;
       }   
	  return jsonString;
  }
  public String savedsplann(int all,String id,String oof,String op,String cl,String wk ) throws SQLException, ParseException
  {
	  String jsonString="{\"result\":0}";
	  
	   String[] oofstr = oof.split(",");
	   String[] opstr = op.split(",");
	   String[] clstr = cl.split(",");
	   String[] wkstr = wk.split(",");
	   Connection con = null;   
	   PreparedStatement pstm = null; 	    
	   Statement stm = null;   
	   String sql="";
	   int hhmmop = 0;
	   int hhmmcl = 0;
	   if (all==0){
		   try 
		   {   
			   con = getJdbcTemplate().getDataSource().getConnection();
			   con.setAutoCommit(false);
			   stm = con.createStatement();  
			   sql = " delete from time_plan where sn="+id;
			   stm.addBatch(sql);
			   for (int e = 1; e <  oofstr.length; e++)//00:00
				{  // if (Integer.parseInt(oofstr[e])>0){
						hhmmop = Integer.parseInt(opstr[e].substring(0, 2))*60+Integer.parseInt(opstr[e].substring(3, 5));
						hhmmcl = Integer.parseInt(clstr[e].substring(0, 2))*60+Integer.parseInt(clstr[e].substring(3, 5));
						sql="INSERT INTO TIME_PLAN (SN,ON_OFF,T_OPEN,T_CLOSE,WEEK) VALUES ("+id+", "+oofstr[e]+" , "+hhmmop+", "+hhmmcl+","+wkstr[e]+")";
						
						log(sql);
						stm.addBatch(sql);
				   // }
				}
			   stm.executeBatch();   
			   con.commit();
			   con.setAutoCommit(true);
		    	  Map<String,Object> map1 = new HashMap<String,Object>();  
		          map1.put("result",1);  
		          jsonString = JSON.toJSONString(map1);	 
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
	       }finally
	       {   
	    	   stm.close();
	    	   stm = null;
	    	    con.close();
	    	    con = null;
	       }  
	   }else{
		   try 
		   {   
			   con = getJdbcTemplate().getDataSource().getConnection();
			   con.setAutoCommit(false);
			   stm = con.createStatement();  
			   sql = "truncate table TIME_PLAN";
			   stm.addBatch(sql);
			   for (int i = 0; i <=  Integer.parseInt(id); i++)
			   {//00 : 00
				   for (int e = 1; e <  oofstr.length; e++)//00 : 00
					{
					   // if (Integer.parseInt(oofstr[e])>0)
					    {
						hhmmop = Integer.parseInt(opstr[e].substring(0, 2))*60+Integer.parseInt(opstr[e].substring(3, 5));
						hhmmcl = Integer.parseInt(clstr[e].substring(0, 2))*60+Integer.parseInt(clstr[e].substring(3, 5));
						sql="INSERT INTO TIME_PLAN (SN,ON_OFF,T_OPEN,T_CLOSE,WEEK) VALUES ("+i+", "+oofstr[e]+" , "+hhmmop+", "+hhmmcl+","+wkstr[e]+")";
						
						log(sql);
						stm.addBatch(sql);
					    }
					}
		       }
			   stm.executeBatch();   
			   con.commit();
			   con.setAutoCommit(true);
		    	  Map<String,Object> map1 = new HashMap<String,Object>();  
		          map1.put("result",1);  
		          jsonString = JSON.toJSONString(map1);	 
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
	       }finally
	       {   
	    	   stm.close();
	    	   stm = null;
	    	    con.close();
	    	    con = null;
	       }   		   
	   }
	  return jsonString;
  }
  public String savedsplann_temp(int all,String id,String oof,String op,String cl,String wk ) throws SQLException, ParseException
  {
	  String jsonString="{\"result\":0}";
	  
	   String[] oofstr = oof.split(",");
	   String[] opstr = op.split(",");
	   String[] clstr = cl.split(",");
	   String[] wkstr = wk.split(",");
	   Connection con = null;   
	   PreparedStatement pstm = null; 	    
	   Statement stm = null;   
	   String sql="";
	   int hhmmop = 0;
	   int hhmmcl = 0;
	   if (all==0){
		   try 
		   {   
			   con = getJdbcTemplate().getDataSource().getConnection();
			   con.setAutoCommit(false);
			   stm = con.createStatement();  
			   sql = " delete from time_plan_temp where sn="+id;
			   log(sql);
			   stm.addBatch(sql);
			   for (int e = 1; e <  oofstr.length; e++)//00:00
				{  // if (Integer.parseInt(oofstr[e])>0){
						hhmmop = Integer.parseInt(opstr[e].substring(0, 2))*60+Integer.parseInt(opstr[e].substring(3, 5));
						//hhmmcl = wkstr[e];
						sql="INSERT INTO time_plan_temp (SN,ON_OFF,TEMP,T_SET,week) VALUES ("+id+", "+oofstr[e]+" , "+clstr[e]+" , "+hhmmop+", "+wkstr[e]+")";
						
						log(sql);
						stm.addBatch(sql);
				   // }
				}
			   stm.executeBatch();   
			   con.commit();
			   con.setAutoCommit(true);
		    	  Map<String,Object> map1 = new HashMap<String,Object>();  
		          map1.put("result",1);  
		          jsonString = JSON.toJSONString(map1);	 
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
	       }finally
	       {   
	    	   stm.close();
	    	   stm = null;
	    	    con.close();
	    	    con = null;
	       }  
	   }else{
		   try 
		   {   
			   con = getJdbcTemplate().getDataSource().getConnection();
			   con.setAutoCommit(false);
			   stm = con.createStatement();  
			   sql = "truncate table TIME_PLAN_TEMP";
			   stm.addBatch(sql);
			   for (int i = 0; i <=  Integer.parseInt(id); i++)
			   {//00 : 00
				   for (int e = 1; e <  oofstr.length; e++)//00 : 00
					{
					   // if (Integer.parseInt(oofstr[e])>0)
					    {
						hhmmop = Integer.parseInt(opstr[e].substring(0, 2))*60+Integer.parseInt(opstr[e].substring(3, 5));
						//hhmmcl = Integer.parseInt(clstr[e].substring(0, 2))*60+Integer.parseInt(clstr[e].substring(3, 5));
						sql="INSERT INTO TIME_PLAN_TEMP (SN,ON_OFF,T_SET,TEMP) VALUES ("+i+", "+oofstr[e]+" , "+hhmmop+", "+wkstr[e]+")";
						
						log(sql);
						stm.addBatch(sql);
					    }
					}
		       }
			   stm.executeBatch();   
			   con.commit();
			   con.setAutoCommit(true);
		    	  Map<String,Object> map1 = new HashMap<String,Object>();  
		          map1.put("result",1);  
		          jsonString = JSON.toJSONString(map1);	 
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
	       }finally
	       {   
	    	   stm.close();
	    	   stm = null;
	    	    con.close();
	    	    con = null;
	       }   		   
	   }
	  return jsonString;
  }
  public String deldsplann(String id,String op,String wk ) throws SQLException, ParseException
  {
	  String jsonString="{\"result\":0}";
	  
	  
	   Connection con = null;   
	   PreparedStatement pstm = null; 	    
	   Statement stm = null;   
	   String sql="";
	   int hhmmop = 0;
	   int hhmmcl = 0;
	   {
		   try 
		   {   
			   con = getJdbcTemplate().getDataSource().getConnection();
			   con.setAutoCommit(false);
			   stm = con.createStatement();  

			  // for (int i = 0; i <=  Integer.parseInt(id); i++)
			   {//00 : 00
				  // for (int e = 1; e <  oofstr.length; e++)//00 : 00
					{
					   // if (Integer.parseInt(oofstr[e])>0){
						//hhmmop = Integer.parseInt(opstr[e].substring(0, 2))*60+Integer.parseInt(opstr[e].substring(3, 5));
						//hhmmcl = Integer.parseInt(clstr[e].substring(0, 2))*60+Integer.parseInt(clstr[e].substring(3, 5));
						sql="delete from TIME_PLAN where SN="+id+" AND WEEK="+wk+" AND  T_OPEN="+op;
						
						log(sql);
						stm.addBatch(sql);
					   // }
					}
		       }
			   stm.executeBatch();   
			   con.commit();
			   con.setAutoCommit(true);
		    	  Map<String,Object> map1 = new HashMap<String,Object>();  
		          map1.put("result",1);  
		          jsonString = JSON.toJSONString(map1);	 
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
	       }finally
	       {   
	    	   stm.close();
	    	   stm = null;
	    	    con.close();
	    	    con = null;
	       }   		   
	   }
	  return jsonString;
  }
  public String deldsplann_temp(String id,String op,String st,String wk ) throws SQLException, ParseException
  {
	  String jsonString="{\"result\":0}";
	  
	  
	   Connection con = null;   
	   PreparedStatement pstm = null; 	    
	   Statement stm = null;   
	   String sql="";
	   int hhmmop = 0;
	   int hhmmcl = 0;
	   {
		   try 
		   {   
			   con = getJdbcTemplate().getDataSource().getConnection();
			   con.setAutoCommit(false);
			   stm = con.createStatement();  

			  // for (int i = 0; i <=  Integer.parseInt(id); i++)
			   {//00 : 00
				  // for (int e = 1; e <  oofstr.length; e++)//00 : 00
					{
					   // if (Integer.parseInt(oofstr[e])>0){
						//hhmmop = Integer.parseInt(opstr[e].substring(0, 2))*60+Integer.parseInt(opstr[e].substring(3, 5));
						//hhmmcl = Integer.parseInt(clstr[e].substring(0, 2))*60+Integer.parseInt(clstr[e].substring(3, 5));
						sql="delete from time_plan_temp where SN="+id+"  AND  T_SET="+st+" AND TEMP="+op+" AND week="+wk;
						
						log(sql);
						stm.addBatch(sql);
					   // }
					}
		       }
			   stm.executeBatch();   
			   con.commit();
			   con.setAutoCommit(true);
		    	  Map<String,Object> map1 = new HashMap<String,Object>();  
		          map1.put("result",1);  
		          jsonString = JSON.toJSONString(map1);	 
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
	       }finally
	       {   
	    	   stm.close();
	    	   stm = null;
	    	    con.close();
	    	    con = null;
	       }   		   
	   }
	  return jsonString;
  }
 
  public String savedlssCaseEdit(String jsonStr ) throws SQLException, ParseException
  {
	  String jsonString="{\"result\":0}";
	   JSONObject jsonObj=JSON.parseObject(jsonStr);
	   JSONArray addArr= jsonObj.getJSONArray("addArr");
	   JSONArray delArr= jsonObj.getJSONArray("delArr");
	   JSONArray editArr= jsonObj.getJSONArray("editArr");
	   Connection con = null;   
	   PreparedStatement pstm = null; 	    
	   Statement stm = null;   
	   String sql="";
	   try 
	   {   
		   con = getJdbcTemplate().getDataSource().getConnection();
		   con.setAutoCommit(false);
		   stm = con.createStatement();   
		   for (int i = 0; i <  addArr.size(); i++)
			{
				JSONArray  temp=addArr.getJSONArray(i);
				sql="INSERT INTO POLY_CASE (SN, PLAN_L,ACTUAL_L,TYPE1,IN_OUT,RESION,TEXT1)  values(poly_case_sn.nextval,"+temp.getIntValue(1)+", "+temp.getIntValue(2)+","+temp.getIntValue(3)+","+temp.getIntValue(4)+","+temp.getIntValue(5)+",'"+temp.getString(7)+"')";
				stm.addBatch(sql);   
			}
		   for (int d = 0; d <  delArr.size(); d++)
			{
			   sql="delete from POLY_CASE where sn="+delArr.getInteger(d);
			   stm.addBatch(sql);
			}
		   for (int e = 0; e <  editArr.size(); e++)
			{
				JSONArray  temp=editArr.getJSONArray(e);
				sql="update POLY_CASE set PLAN_L= "+temp.getDoubleValue(1)+" , ACTUAL_L="+temp.getDoubleValue(2)+", TYPE1="+temp.getInteger(3)+" ,IN_OUT="+temp.getInteger(4)+",RESION="+temp.getInteger(5)+",  TEXT1='"+temp.getString(7)+"' where SN="+temp.getInteger(0);
				stm.addBatch(sql);
			}
	    	  Map<String,Object> map1 = new HashMap<String,Object>();  
	          map1.put("result",1);  
	          jsonString = JSON.toJSONString(map1);	 
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
       }finally
       {   
    	   stm.close();
    	   stm = null;
    	    con.close();
    	    con = null;
       }   
	  return jsonString;
  }
  
  public String saveTfEdit(String jsonStr,Integer sn) throws SQLException, ParseException
  {
	  String jsonString="{\"result\":0}";
	   JSONObject jsonObj=JSON.parseObject(jsonStr);
	   JSONArray addArr= jsonObj.getJSONArray("addArr");
	   JSONArray delArr= jsonObj.getJSONArray("delArr");
	   JSONArray editArr= jsonObj.getJSONArray("editArr");
	   Connection con = null;   
	   PreparedStatement pstm = null; 	    
	   Statement stm = null;   
	   String sql="";
	   try 
	   {   
		   con = getJdbcTemplate().getDataSource().getConnection();
		   con.setAutoCommit(false);
		   stm = con.createStatement();   
		   for (int i = 0; i <  addArr.size(); i++)
			{
				JSONArray  temp=addArr.getJSONArray(i);
				String upDown="升至";
				if(temp.getIntValue(5)==0)
				{
					upDown="降至";
				}
				String text2=temp.getString(2)+",以"+temp.getString(4)+"MW/MIN速率"+upDown+temp.getString(6)+"MW";
				String sT=temp.getString(2)+":00";
				String eT=temp.getString(7)+":00";
				sql="Insert into PLAN (SN,M_NAME,PLAN_SN,START_TIME,END_TIME,START_P,RATE_P,UP_DOWN,END_P,TEXT2) values (Plan_sn.nextval,'"+temp.getString(1)+"',"+sn+",to_date('"+sT+"','yyyy-MM-dd HH24:mi:ss'),to_date ('"+eT+"','yyyy-MM-dd HH24:mi:ss'),"+temp.getString(3)+","+temp.getString(4)+","+temp.getString(5)+","+temp.getString(6)+",'"+text2+"') ";
				stm.addBatch(sql); 
				//log("insert:  " + sql);
			}
		   for (int d = 0; d <  delArr.size(); d++)
			{
			   sql="delete from PLAN where sn="+delArr.getInteger(d);
			   stm.addBatch(sql);
			  // log("delete:  " + sql);
			}
		   for (int e = 0; e <  editArr.size(); e++)
			{
				JSONArray  temp=editArr.getJSONArray(e);
				String upDown="升至";
				if(temp.getIntValue(5)==0)
				{
					upDown="降至";
				}
				String text2=temp.getString(2)+",以"+temp.getString(4)+"MW/MIN速率"+upDown+temp.getString(6)+"MW";
				sql="update PLAN set M_NAME= '"+temp.getString(1)+"' , START_TIME=TO_DATE ('"+temp.getString(2)+":00"+"', 'yyyy-MM-dd hh24:mi:ss') , END_TIME=TO_DATE ('"+temp.getString(7)+":00"+"', 'yyyy-MM-dd hh24:mi:ss') ,RATE_P="+temp.getString(4)+" ,  UP_DOWN="+temp.getString(5)+" ,  START_P="+temp.getString(3)+" ,  END_P="+temp.getString(6)+" ,TEXT2='"+text2+"' where SN="+temp.getInteger(0);
				//log("insert  " + sql);
				stm.addBatch(sql);
			}
		   stm.executeBatch();   
		   con.commit();
		   con.setAutoCommit(true);
    	  Map<String,Object> map1 = new HashMap<String,Object>();  
          map1.put("result",1);  
          jsonString = JSON.toJSONString(map1);	  
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
       }finally
       {   
    	   stm.close();
    	   stm = null;
    	    con.close();
    	    con = null;
       }   
	  return jsonString;
  }
  public String savegdEdit(String jsonStr,int mk,int evt) throws SQLException, ParseException
  {
	  String jsonString="{\"result\":0}";
	   JSONObject jsonObj=JSON.parseObject(jsonStr);
	   JSONArray addArr= jsonObj.getJSONArray("addArr");
	   JSONArray delArr= jsonObj.getJSONArray("delArr");
	   JSONArray editArr= jsonObj.getJSONArray("editArr");
	   int flag = 0 ;
	   String sql="";
	   try 
	   {   
		    
		   for (int i = 0; i <  addArr.size(); i++)
			{
				JSONArray  temp=addArr.getJSONArray(i);
				
				//String text2=temp.getString(2)+",以"+temp.getString(4)+"MW/MIN速率"+upDown+temp.getString(6)+"MW";
				flag = 3;
				sql="Insert into PLAN_POINT (ID,EVT_ID,JZ_ID,PLAN_ID,INFO,S_DATETIME,E_DATETIME) values (Plan_sn.nextval,get_evtid('"+temp.getString(2)+"'),get_jzid('"+temp.getString(1)+"'),"+mk+",'"+temp.getString(3)+"',to_date('"+temp.getString(4)+"','yyyy-mm-dd'),to_date('"+temp.getString(5)+"','yyyy-mm-dd'))";
				saveGd(temp.getInteger(1),temp.getInteger(0),temp.getString(2),temp.getFloat(3),temp.getFloat(5),temp.getFloat(6),temp.getString(7),temp.getFloat(8),flag);

				
				
			}
		   for (int d = 0; d <  delArr.size(); d++)
			{
			   sql="delete from PLAN_POINT where id="+delArr.getInteger(d);
			  
			   //log("delete:  " + sql);
			}
		   for (int e = 0; e <  editArr.size(); e++)
			{
				JSONArray  temp=editArr.getJSONArray(e);
				
				//String text2=temp.getString(2)+",以"+temp.getString(4)+"MW/MIN速率"+temp.getString(5)+"MW";
				if (evt == 1)
				{
					if (mk == -1)
					{
						flag =1;
						//sql="update PLAN_GD set S_DATETIME=TO_DATE('"+temp.getString(2)+"','YYYY-MM-DD HH24:MI'),E_POWER="+temp.getString(6)+" where ID="+temp.getInteger(0)+" AND POINT_ID="+temp.getInteger(1);
					}
					if (mk == -99)
					{
						sql="update PLAN_GD set S_DATETIME=TO_DATE('"+temp.getString(2)+"','YYYY-MM-DD HH24:MI') where ID="+temp.getInteger(0)+" AND POINT_ID="+temp.getInteger(1);
					    flag = 2;
					}	
				}
				//log("UPDATE  " + sql);
				//log((temp.getInteger(1)+","+temp.getInteger(0)+","+temp.getString(2)+","+temp.getFloat(3)+","+temp.getFloat(5)+"---"+temp.getFloat(6)+","+temp.getString(7)+","+temp.getFloat(8)+","+flag));
				saveGd(temp.getInteger(1),temp.getInteger(0),temp.getString(2),temp.getFloat(3),temp.getFloat(5),temp.getFloat(6),temp.getString(7),temp.getFloat(8),flag);
			}
		 
    	  Map<String,Object> map1 = new HashMap<String,Object>();  
          map1.put("result",1);  
          jsonString = JSON.toJSONString(map1);	  
	    }
	   catch (SQLException e)
	   {   
           e.printStackTrace();   
          
       }finally
       {   
    	   
       }   
	  return jsonString;
  }
  public String getauthor() {
	    String jsonString = "";
	    String rtuString = "";
	    String sql = "SELECT deviceno,devicenm from dev_author order by deviceno asc";
	    List userData = getJdbcTemplate().queryForList(sql);
	    int size = userData.size();
	    
	    if (size > 0) {
	      
	      Object[] arr = new Object[size];
	      Object[] brr = new Object[size];
	      Object[] crr = new Object[size];
	      for (int i = 0; i < size; i++) {

	        
	        Map listData = (Map)userData.get(i);
	        arr[i] = 0;
	        brr[i] = listData.get("deviceno");
	        crr[i] = listData.get("devicenm");
	      } 
	      
	      Map<String, Object> map1 = new HashMap<String, Object>();
	      map1.put("result", Integer.valueOf(1));
	      map1.put("roomno", arr);
	      map1.put("deviceno", brr);
	      map1.put("devicenm", crr);
	      jsonString = JSON.toJSONString(map1);
	    } 

	    
	    return jsonString;
	  }
  public String getName(int rtu, int sn) {
	    String jsonString = "";
	    String sql = "select concat(c.name,b.roomname,a.name) tname from prtuana a,room b,prtu c where a.rtuno=c.rtuno and a.roomno=b.roomno and a.sn=" + sn + " and a.rtuno=" + rtu;
	    List userData = getJdbcTemplate().queryForList(sql);
	    Map countMap = (Map)userData.get(0);
	    return countMap.get("tname").toString();
	  }

  public String testHelper(int uid) {
	    String jsonString = "{\"result\":0}";
	    String str = "";
	    try {
	      Class.forName("org.sqlite.JDBC");
	      Connection conn = DriverManager.getConnection("jdbc:sqlite:D:\\cstation\\cfg\\calc_exp.rdb");
	      
	      Statement stmt = conn.createStatement();


	      
	      ResultSet rs = stmt.executeQuery("SELECT * FROM calc_evt where uid=" + Math.floor((uid / 10000)) + " and pid=" + (uid % 10000));

	      
	      int size = 2;
	      int uuid = (int)Math.floor((uid / 10000));
	      int ppid = uid % 10000;
	      String name = "";
	      int i = 0;
	      
	      if (size > 0) {

	        
	        Object[] arr = new Object[size];
	        Object[] narr = new Object[1];
	        while (rs.next()) {
	          
	          log(rs.getString("sn"));
	          ArrayList kv = new ArrayList();
	          
	          kv.add(rs.getString("sn"));
	          kv.add(rs.getString("uid"));
	          kv.add(rs.getString("pid"));
	          kv.add(rs.getString("name"));
	          uuid = rs.getInt("uid");
	          ppid = rs.getInt("pid");
	          name = rs.getString("name");
	          str = rs.getString("close_exp");
	          if (str.indexOf(">") > 0) {
	            kv.add("上限");
	            kv.add(str.substring(str.indexOf(">") + 1).trim());
	          } else {
	            
	            kv.add("下限");
	            kv.add(str.substring(str.indexOf("<") + 1).trim());
	          } 
	          
	          kv.add("<a id='subBtn' href='javascript:void(0);'><span class='fa fa-save'></span></a>&nbsp;&nbsp;&nbsp;&nbsp;<a id='delBtn' href='javascript:void(0);'><span class='glyphicon glyphicon-trash'></span></a>");
	          
	          arr[i] = kv;
	          i++;
	        } 
	        
	        narr[0] = arr[0];
	        
	        Map<String, Object> map1 = new HashMap<String, Object>();
	        map1.put("result", Integer.valueOf(1));
	        map1.put("tname", getName((int)Math.floor((uid / 10000)), uid % 10000));
	        map1.put("uid", Integer.valueOf(uuid));
	        map1.put("pid", Integer.valueOf(ppid));
	        map1.put("i", Integer.valueOf(i));
	        
	        map1.put("name", name);
	        if (i > 1) {
	          map1.put("data", arr);
	        } else {
	          
	          map1.put("data", narr);
	        } 
	        jsonString = JSON.toJSONString(map1);
	      } 
	      
	      stmt.close();
	      conn.close();

	    
	    }
	    catch (ClassNotFoundException e) {
	      e.printStackTrace();
	    } catch (SQLException e) {
	      e.printStackTrace();
	    } 
	    log(jsonString);
	    return jsonString;
	  }
	  
  public String saveupdown(String jsonStr, Integer sn) throws SQLException, ParseException {
	    String jsonString = "{\"result\":0}";
	    JSONObject jsonObj = JSON.parseObject(jsonStr);
	    JSONArray addArr = jsonObj.getJSONArray("addArr");
	    JSONArray delArr = jsonObj.getJSONArray("delArr");
	    JSONArray editArr = jsonObj.getJSONArray("editArr");
	    Connection conn = null;
	    PreparedStatement pstm = null;
	    Statement stmt = null;
	    String sql = "";
	    
	    try {
	      Class.forName("org.sqlite.JDBC");
	      conn = DriverManager.getConnection("jdbc:sqlite:D:\\cstation\\cfg\\calc_exp.rdb");
	      

	       stmt = conn.createStatement();
	      for (int i = 0; i < addArr.size(); i++) {
	        String str2, str1;
	        JSONArray temp = addArr.getJSONArray(i);
	        int udtype = temp.getInteger(4).intValue();
	        
	        if (udtype == 0) {
	          
	          str1 = "get_r_f32(''ch_0'', ''mqtt_r_ai'', ''ai_" + temp.getString(1) + "_" + temp.getString(2) + "'', ''value'') > " + temp.getString(5);
	          str2 = "get_r_f32(''ch_0'', ''mqtt_r_ai'', ''ai_" + temp.getString(1) + "_" + temp.getString(2) + "'', ''value'') < " + temp.getString(5);
	        } else {
	          str1 = "get_r_f32(''ch_0'', ''mqtt_r_ai'', ''ai_" + temp.getString(1) + "_" + temp.getString(2) + "'', ''value'') < " + temp.getString(5);
	          str2 = "get_r_f32(''ch_0'', ''mqtt_r_ai'', ''ai_" + temp.getString(1) + "_" + temp.getString(2) + "'', ''value'') > " + temp.getString(5);
	        } 
	        
	        ResultSet rs = stmt.executeQuery("SELECT MIN(sn)+1 nsn  FROM calc_evt AS id1 WHERE NOT EXISTS(SELECT * FROM calc_evt AS id2 WHERE id1.sn+1=id2.sn)");
	        int nsn = rs.getInt("nsn");

	        
	        sql = "Insert into calc_evt (sn,key,name,enb_flg,uid,pid,value,quality,ref_time,chg_time,close_exp,close_delay_time,close_keep_time,open_exp,open_delay_time,open_keep_time,event_type,event_level,note) values (";
	        sql = String.valueOf(sql) + nsn + ",'evt_" + nsn + "','" + temp.getString(3) + "',1," + temp.getString(1) + "," + temp.getString(2) + ",0,0,0,0,'" + str1 + "',0,0,'" + str2 + "',0,0,'o_2_c',1,0)";
	        log("insert  " + sql);
	        stmt.addBatch(sql);
	      } 
	      
	      for (int d = 0; d < delArr.size(); d++) ;
	      /*
	      {
	        
	        sql = "delete from calc_evt where sn=" + delArr.getInteger(d);
	        stmt.addBatch(sql);
	        sql = "update calc_evt set sn=sn-1 where sn>" + delArr.getInteger(d);
	        stmt.addBatch(sql);
	        sql = "update calc_evt  set key='evt_'||sn where sn>=" + delArr.getInteger(d);
	        stmt.addBatch(sql);
	      } */
	      
	      for (int e = 0; e < editArr.size(); e++) {
	        String str2, str1;
	        JSONArray temp = editArr.getJSONArray(e);
	        int udtype = temp.getInteger(4).intValue();
	        
	        if (udtype == 0) {
	          
	          str1 = "get_r_f32(''ch_0'', ''mqtt_r_ai'', ''ai_" + temp.getString(1) + "_" + temp.getString(2) + "'', ''value'') > " + temp.getString(5);
	          str2 = "get_r_f32(''ch_0'', ''mqtt_r_ai'', ''ai_" + temp.getString(1) + "_" + temp.getString(2) + "'', ''value'') < " + temp.getString(5);
	        } else {
	          str1 = "get_r_f32(''ch_0'', ''mqtt_r_ai'', ''ai_" + temp.getString(1) + "_" + temp.getString(2) + "'', ''value'') < " + temp.getString(5);
	          str2 = "get_r_f32(''ch_0'', ''mqtt_r_ai'', ''ai_" + temp.getString(1) + "_" + temp.getString(2) + "'', ''value'') > " + temp.getString(5);
	        } 


	        
	        sql = "update calc_evt set close_exp='" + str1 + "' ,open_exp='" + str2 + "' where sn=" + temp.getInteger(0);
	        log("UPDATE  " + sql);
	        stmt.addBatch(sql);
	      } 
	      stmt.executeBatch();
	      
	      conn.setAutoCommit(true);
	      Map<String, Object> map1 = new HashMap<String, Object>();
	      map1.put("result", Integer.valueOf(1));
	      jsonString = JSON.toJSONString(map1);
	    }
	    catch (ClassNotFoundException e) {
	      e.printStackTrace();
	    } catch (SQLException e) {
	      e.printStackTrace();
	    } finally {
	      
	      stmt.close();
	      stmt = null;
	      conn.close();
	      conn = null;
	    } 

	    
	    try {
	      conn = getJdbcTemplate().getDataSource().getConnection();
	      conn.setAutoCommit(false);
	      stmt = conn.createStatement();
	      for (int i = 0; i < addArr.size(); i++) {
	        
	        JSONArray temp = addArr.getJSONArray(i);
	        int udtype = temp.getInteger(4).intValue();
	        
	        if (udtype == 0) {
	          
	          sql = " update prtuana set upperlimit=" + temp.getString(5) + " where rtuno=" + temp.getString(1) + " and sn=" + temp.getString(2);
	        } else {
	          sql = " update prtuana set lowerlimit=" + temp.getString(5) + " where rtuno=" + temp.getString(1) + " and sn=" + temp.getString(2);
	        } 
	        
	        stmt.addBatch(sql);
	      } 
	      
	      for (int d = 0; d < delArr.size(); d++);


	      
	      for (int e = 0; e < editArr.size(); e++) {
	        
	        JSONArray temp = editArr.getJSONArray(e);
	        int udtype = temp.getInteger(4).intValue();
	        
	        if (udtype == 0) {
	          
	          sql = " update prtuana set upperlimit=" + temp.getString(5) + " where rtuno=" + temp.getString(1) + " and sn=" + temp.getString(2);
	        } else {
	          sql = " update prtuana set lowerlimit=" + temp.getString(5) + " where rtuno=" + temp.getString(1) + " and sn=" + temp.getString(2);
	        } 

	        
	        log("UPDATE  " + sql);
	        stmt.addBatch(sql);
	      } 
	      stmt.executeBatch();
	      
	      conn.setAutoCommit(true);
	      Map<String, Object> map1 = new HashMap<String, Object>();
	      map1.put("result", Integer.valueOf(1));
	      jsonString = JSON.toJSONString(map1);
	    }
	    catch (SQLException e) {
	      e.printStackTrace();
	    } finally {
	      
	      stmt.close();
	      stmt = null;
	      conn.close();
	      conn = null;
	    } 
	    return jsonString;
	  }
  
  public String syncupdown()throws SQLException, ParseException {
	    String jsonString = "{\"result\":0}";
	    
	    Connection conn = null;
	    Statement stmt = null;
	    Connection conn_m = null;
	    Statement stmt_m = null;
	    String sql = "";
	    
	    try {
	      Class.forName("org.sqlite.JDBC");
	      conn = DriverManager.getConnection("jdbc:sqlite:D:\\cstation\\cfg\\calc_exp.rdb");

	      
	      stmt = conn.createStatement();
	      conn_m = getJdbcTemplate().getDataSource().getConnection();
	      conn_m.setAutoCommit(false);
	      stmt_m = conn_m.createStatement();
	      
	      ResultSet rs = stmt.executeQuery("SELECT *  FROM calc_evt order by uid,pid");
	      while (rs.next()) {
	        
	        String exp = rs.getString("close_exp");
	        
	        if (exp.indexOf(">") > 0) {
	          
	          sql = " update prtuana set upperlimit=" + exp.substring(exp.indexOf(">") + 1).trim() + " where rtuno=" + rs.getString("uid") + " and sn=" + rs.getString("pid");
	        } else {
	          sql = " update prtuana set lowerlimit=" + exp.substring(exp.indexOf("<") + 1).trim() + " where rtuno=" + rs.getString("uid") + " and sn=" + rs.getString("pid");
	        } 
	        
	        stmt_m.addBatch(sql);
	        stmt_m.executeBatch();
	      } 
	      
	      conn_m.commit();
	      conn_m.setAutoCommit(true);
	      
	      Map<String, Object> map1 = new HashMap<String, Object>();
	      map1.put("result", Integer.valueOf(1));
	      jsonString = JSON.toJSONString(map1);
	    }
	    catch (ClassNotFoundException e) {
	      e.printStackTrace();
	    } catch (SQLException e) {
	      e.printStackTrace();
	    } finally {
	      
	      stmt.close();
	      stmt = null;
	      conn.close();
	      conn = null;
	      stmt_m.close();
	      stmt_m = null;
	      conn_m.close();
	      conn_m = null;
	    } 
	    
	    return jsonString;
	  }
	  
  
  
	  
  public String loadconfig_updown(String dn, String nm, int lev) {
	  String jsonString = "";
	    String rtuString = "";
	    String sql = "";
	    if (lev == 1) {
	      
	      sql = "select roomname p_name,roomno rtunos,rtuno from room where rtuno=" + dn;
	    } else if (lev == 2) {
	      
	      sql = "select distinct get_subs(name,1) p_name,rtuno,roomno rtunos from prtuana where roomno=" + dn;
	    } else {
	      sql = "select get_subs(name,0) p_name,rtuno*10000+sn rtunos,rtuno from prtuana where roomno=" + dn + " and name like '" + nm + "%' order by saveno";
	    } 
	    log(sql);
	    List userData = getJdbcTemplate().queryForList(sql);
	    int size = userData.size();
	    if (size > 0) {
	      
	      Object[] arr = new Object[size];
	      for (int i = 0; i < size; i++) {

	        
	        Map listData = (Map)userData.get(i);
	        rtuString = "{\"text\":\"" + listData.get("p_name") + "\",\"a_attr\":{\"no\":\"" + listData.get("rtunos") + "\"}}";
	        if (i == 0) {
	          
	          jsonString = rtuString;
	        } else {
	          
	          jsonString = String.valueOf(jsonString) + "," + rtuString;
	        } 
	      } 
	    } 


	    
	    return "[" + jsonString + "]";
	  }
  public String loadconfig_updown_cn(String dn, String nm, int lev) {
	  String jsonString = "";
	    String rtuString = "";
	    String sql = "";
	    if (lev == 1) {
	      
	      sql = "select distinct get_subs(name,1) p_name,rtuno,roomno rtunos from prtuana where rtuno=" + dn;
	    } else {
	      sql = "select get_subs(name,0) p_name,rtuno*10000+sn rtunos,rtuno from prtuana where rtuno=" + dn + "-1 and name like '" + nm + "%' order by saveno";
	    } 
	    log(sql);
	    List userData = getJdbcTemplate().queryForList(sql);
	    int size = userData.size();
	    if (size > 0) {
	      
	      Object[] arr = new Object[size];
	      for (int i = 0; i < size; i++) {

	        
	        Map listData = (Map)userData.get(i);
	        rtuString = "{\"text\":\"" + listData.get("p_name") + "\",\"a_attr\":{\"no\":\"" + listData.get("rtunos") + "\"}}";
	        if (i == 0) {
	          
	          jsonString = rtuString;
	        } else {
	          
	          jsonString = String.valueOf(jsonString) + "," + rtuString;
	        } 
	      } 
	    } 


	    
	    return "[" + jsonString + "]";
	  }
  
  public String set_author(String devs, int uid) {
	  String jsonString = "";
    Map<String, Object> map1 = new HashMap<String, Object>();
    String sql = "update dev_author set author=bitnot(author ,power(2," + uid + "-1)) ";
    log(sql);
    
    int re = getJdbcTemplate().update(sql);
    log(Integer.valueOf(re));
    if (re > 0) {
      sql = "update dev_author set author=(author | power(2," + uid + "-1)) where deviceno in (" + devs + ")";
      log(sql);
      re = getJdbcTemplate().update(sql);
      log(Integer.valueOf(re));
      if (re > 0) { map1.put("result", Integer.valueOf(1)); }
      else { map1.put("result", Integer.valueOf(0)); }
    
    } 


    
    return JSON.toJSONString(map1);
  }

  public String getUserAuthor(int uId) {
	   
	    String rtuString = "";
	    String sql = "SELECT deviceno,devicenm from dev_author where (author & power(2," + (uId - 1) + "))>0 order by deviceno asc";
	    List userData = getJdbcTemplate().queryForList(sql);
	    int size = userData.size();
	    Map<String, Object> map1 = new HashMap<String, Object>();
	    if (size > 0) {

	      
	      Object[] brr = new Object[size];
	      
	      for (int i = 0; i < size; i++) {

	        
	        Map listData = (Map)userData.get(i);
	        
	        brr[i] = listData.get("deviceno");
	      } 


	      map1.put("result", Integer.valueOf(1));
	      
	      map1.put("deviceno", brr);
	    }
	    else {
	      
	      map1.put("result", Integer.valueOf(0));
	    } 
	    
	    return JSON.toJSONString(map1);
	  }
    public String loadEvt(String gkey,int sn,int ck) {

        String rtuString = "";
        String cka = " ";
        if (ck==1) cka=" and  a.readstatus=0 ";
        String sql = "select concat(get_datestr(a.ymd,a.hms),' ',b.name,' ',d.e_info) text,concat('1,',a.ymd,',',a.hms,',',a.ch,',',a.xh) id,a.readstatus readStatus,d.e_color ecl from hevt0 a,prtudig b,prtu c,etype_info d where b.author_read>0 "+cka+" and  b.type=d.e_type and a.zt=d.e_zt and  a.ch=c.rtuno and a.xh=b.sn and a.ch=b.rtuno   and c.un_x='"+gkey+"' union select concat(get_datestr(a.ymd,a.hms),' ',b.name,' ',concat(CASE WHEN a.zt = -1 THEN 'yxx' WHEN a.zt = 0 THEN 'hf' ELSE 'ysx' END ,'(',a.val,')')) text,concat('2,',a.ymd,',',a.hms,',',a.ch,',',a.xh) id,a.readstatus readStatus,abs(a.zt) ecl from hevtyc0 a,prtuana b,prtu c  where   b.author_read>0 "+cka+" and  a.ch=c.rtuno and a.xh=b.sn and a.ch=b.rtuno  and c.un_x='"+gkey+"' order by text desc LIMIT "+(sn*10)+", 10";
        log(sql);
        List userData = getJdbcTemplate().queryForList(sql);
        int size = userData.size();
        Map<String, Object> map1 = new HashMap<String, Object>();
        if (size > 0) {


            Object[] brr = new Object[size];

            for (int i = 0; i < size; i++) {


                Map listData = (Map)userData.get(i);
                if (listData.get("id").toString().startsWith("2,"))
                {
                    rtuString = listData.get("text").toString().replace("ysx","越上限").replace("yxx","越下限").replace("hf","恢复");
                    listData.put("text",rtuString);
                }

                brr[i] = listData;
            }


            map1.put("result", Integer.valueOf(1));

            map1.put("data", brr);
        }
        else {

            map1.put("result", Integer.valueOf(0));
        }

        return JSON.toJSONString(map1);
    }
  public String saveyTfEdit(String jsonStr,Integer sn,String gkey) throws SQLException, ParseException
  {
	  String jsonString="{\"result\":0}";
      //String gkey = (String) httpSession.getAttribute("groupKey");
	   JSONObject jsonObj=JSON.parseObject(jsonStr);
	   JSONArray addArr= jsonObj.getJSONArray("addArr");
	   JSONArray delArr= jsonObj.getJSONArray("delArr");
	   JSONArray editArr= jsonObj.getJSONArray("editArr");
	   Connection con = null;   
	   PreparedStatement pstm = null; 	    
	   Statement stm = null;   
	   String sql="";
	   String tid = "";
	   try 
	   {   
		   con = getJdbcTemplate().getDataSource().getConnection();
		   con.setAutoCommit(false);
		   stm = con.createStatement();   
		   for (int i = 0; i <  addArr.size(); i++)
			{
				JSONArray  temp=addArr.getJSONArray(i);
                String[] keys=temp.getString(4).split(",");
				//String text2=temp.getString(2)+",以"+temp.getString(4)+"MW/MIN速率"+upDown+temp.getString(6)+"MW";
                sql = "SELECT CASE  WHEN NOT EXISTS(SELECT * FROM msg_user WHERE gkey='"+gkey+"'   and auth=2 )  THEN 2  ELSE  (SELECT MIN(auth)+1   FROM msg_user A  WHERE gkey='"+gkey+"'   and NOT EXISTS(   SELECT * FROM msg_user B WHERE A.auth+1=B.auth and B.gkey='"+gkey+"'  )) END as tid";
                List userData = getJdbcTemplate().queryForList(sql);
                if (userData.size() > 0) {
                    Map listData = (Map)userData.get(0);

                    tid = listData.get("tid").toString();
                }
				sql="Insert into msg_user (auth,NAME,PHONE,PHONEVALID,MSG_ST,MSG_ET,EMAIL,EMAILVALID,valid,leval,gkey,passwd) values ("+tid+",'"+temp.getString(1)+"','"+temp.getString(2)+"',"+temp.getString(3)+","+keys[0]+","+keys[1]+",'"+temp.getString(5)+"',"+temp.getString(6)+",1,2,'"+gkey+"','111111')";
                //log("insert:  " + sql);
				stm.addBatch(sql); 
				
			}
		   for (int d = 0; d <  delArr.size(); d++)
			{
			   sql="delete from msg_user where id="+delArr.getInteger(d);
			   stm.addBatch(sql);
			   //log("delete:  " + sql);
			}
		   for (int e = 0; e <  editArr.size(); e++)
			{
				JSONArray  temp=editArr.getJSONArray(e);
                String[] keys=temp.getString(4).split(",");
				//2,weswsw,13900000000,false,0/1440,111@111.com,false
				sql="update msg_user set NAME='"+temp.getString(1)+"' ,PHONE='"+temp.getString(2)+"',phonevalid="+temp.getString(3)+",msg_st="+keys[0]+",msg_et="+keys[1]+",EMAIL='"+temp.getString(5)+"',emailvalid="+temp.getString(6)+" where ID="+temp.getInteger(0);
				log("UPDATE  " + sql);
				stm.addBatch(sql);
			}
		   stm.executeBatch();   
		   con.commit();
		   con.setAutoCommit(true);
    	  Map<String,Object> map1 = new HashMap<String,Object>();  
          map1.put("result",1);  
          jsonString = JSON.toJSONString(map1);	  
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
       }finally
       {   
    	   stm.close();
    	   stm = null;
    	    con.close();
    	    con = null;
       }   
	  return jsonString;
  }
    public String saveUpDown(String jsonStr,Integer sn) throws SQLException, ParseException
    {
        String jsonString="{\"result\":0}";
        JSONObject jsonObj=JSON.parseObject(jsonStr);
        JSONArray addArr= jsonObj.getJSONArray("addArr");
        JSONArray delArr= jsonObj.getJSONArray("delArr");
        JSONArray editArr= jsonObj.getJSONArray("editArr");
        Connection con = null;
        PreparedStatement pstm = null;
        Statement stm = null;
        String sql="";
        try
        {
            con = getJdbcTemplate().getDataSource().getConnection();
            con.setAutoCommit(false);
            stm = con.createStatement();
            /*for (int i = 0; i <  addArr.size(); i++)
            {
                JSONArray  temp=addArr.getJSONArray(i);
                String[] keys=temp.getString(4).split(",");
                //String text2=temp.getString(2)+",以"+temp.getString(4)+"MW/MIN速率"+upDown+temp.getString(6)+"MW";

                sql="Insert into msg_user (NAME,PHONE,PHONEVALID,MSG_ST,MSG_ET,EMAIL,EMAILVALID) values ('"+temp.getString(1)+"','"+temp.getString(2)+"',"+temp.getString(3)+","+keys[0]+","+keys[1]+",'"+temp.getString(5)+"',"+temp.getString(6)+")";
                log("insert:  " + sql);
                stm.addBatch(sql);

            }*/
            /*for (int d = 0; d <  delArr.size(); d++)
            {
                sql="delete from msg_user where id="+delArr.getInteger(d);
                stm.addBatch(sql);
                //log("delete:  " + sql);
            }*/
            for (int e = 0; e <  editArr.size(); e++)
            {
                JSONArray  temp=editArr.getJSONArray(e);
                String[] keys=temp.getString(4).split(",");
                //2,weswsw,13900000000,false,0/1440,111@111.com,false
                if (temp.getString(9)==null) temp.set(9,"1000");
                if (temp.getString(8)==null) temp.set(8," ");
                sql="update prtuana set upperlimit="+temp.getString(2)+" ,lowerlimit="+temp.getString(3)+",author_alert="+temp.getString(4)+",author_msg="+temp.getString(5)+",author_email="+temp.getString(6)+",timevalid="+temp.getString(7)+",timecondition='"+temp.getString(8)+"',warnline="+temp.getString(9)+" where saveno="+temp.getInteger(0);
                //log("UPDATE ana " + sql);
                try {
                    if ((boolean)scriptEngine.eval("1"+temp.getString(6))) ;
                    stm.addBatch(sql);
                    stm.executeBatch();
                    con.commit();
                    con.setAutoCommit(true);
                    Map<String,Object> map1 = new HashMap<String,Object>();
                    map1.put("result",1);
                    jsonString = JSON.toJSONString(map1);
                } catch (ScriptException e1) {
                    //e1.printStackTrace();
                }catch (Exception ee)
                {}
            }

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
        }finally
        {
            stm.close();
            stm = null;
            con.close();
            con = null;
        }
        return jsonString;
    }
    public String saveUpDown_dig(String jsonStr,String sn) throws SQLException, ParseException
    {
        String jsonString="{\"result\":0}";
        JSONObject jsonObj=JSON.parseObject(jsonStr);
        JSONArray addArr= jsonObj.getJSONArray("addArr");
        JSONArray delArr= jsonObj.getJSONArray("delArr");
        JSONArray editArr= jsonObj.getJSONArray("editArr");
        Connection con = null;
        PreparedStatement pstm = null;
        Statement stm = null;
        String sql="";

        try
        {
            con = getJdbcTemplate().getDataSource().getConnection();
            con.setAutoCommit(false);
            stm = con.createStatement();
            /*for (int i = 0; i <  addArr.size(); i++)
            {
                JSONArray  temp=addArr.getJSONArray(i);
                String[] keys=temp.getString(4).split(",");
                //String text2=temp.getString(2)+",以"+temp.getString(4)+"MW/MIN速率"+upDown+temp.getString(6)+"MW";

                sql="Insert into msg_user (NAME,PHONE,PHONEVALID,MSG_ST,MSG_ET,EMAIL,EMAILVALID) values ('"+temp.getString(1)+"','"+temp.getString(2)+"',"+temp.getString(3)+","+keys[0]+","+keys[1]+",'"+temp.getString(5)+"',"+temp.getString(6)+")";
                log("insert:  " + sql);
                stm.addBatch(sql);

            }*/
            /*for (int d = 0; d <  delArr.size(); d++)
            {
                sql="delete from msg_user where id="+delArr.getInteger(d);
                stm.addBatch(sql);
                //log("delete:  " + sql);
            }*/
            for (int e = 0; e <  editArr.size(); e++)
            {

                JSONArray  temp=editArr.getJSONArray(e);

                //2,weswsw,13900000000,false,0/1440,111@111.com,false
                if (temp.getString(7)==null) temp.set(7,"1000");
                if (temp.getString(6)==null) temp.set(6," ");

                //2,weswsw,13900000000,fal temp.getString(6)
                sql="update prtudig set author_alert="+temp.getString(2)+",author_msg="+temp.getString(3)+",author_email="+temp.getString(4)+",timevalid="+temp.getString(5)+",timecondition='"+temp.getString(6)+"',warnline="+temp.getString(7)+" where kkey='"+temp.getString(0)+"'";
                System.out.println("UPDATE dig " + sql);
                try {
                   if ((boolean)scriptEngine.eval("1"+temp.getString(6))) ;
                    stm.addBatch(sql);
                    stm.executeBatch();
                    con.commit();
                    con.setAutoCommit(true);
                    Map<String,Object> map1 = new HashMap<String,Object>();
                    map1.put("result",1);
                    jsonString = JSON.toJSONString(map1);
                } catch (ScriptException e1) {
                    //e1.printStackTrace();
                }catch (Exception ee)
                {}

            }

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
        }finally
        {
            stm.close();
            stm = null;
            con.close();
            con = null;
        }
        return jsonString;
    }
  public String savedlssPointEdit(String jsonStr ) throws SQLException, ParseException
  {
	  String jsonString="{\"result\":0}";
	   JSONObject jsonObj=JSON.parseObject(jsonStr);
	   JSONArray addArr= jsonObj.getJSONArray("addArr");
	   JSONArray delArr= jsonObj.getJSONArray("delArr");
	   JSONArray editArr= jsonObj.getJSONArray("editArr");
	   Connection con = null;   
	   PreparedStatement pstm = null; 	    
	   Statement stm = null;   
	   String sql="";
	   try 
	   {   
		   con = getJdbcTemplate().getDataSource().getConnection();
		   con.setAutoCommit(false);
		   stm = con.createStatement();   
		   for (int i = 0; i <  addArr.size(); i++)
			{
				JSONArray  temp=addArr.getJSONArray(i);
				sql="INSERT INTO POLY_POINT (SN, M_NAME,TIME1,case_sn,text1)  values(poly_point_sn.nextval,'"+temp.getString(2)+"',TO_DATE('"+temp.getString(1)+"', 'yyyy-MM-dd hh24:mi:ss'),"+temp.getIntValue(3)+",'"+temp.getString(4)+"')";
				stm.addBatch(sql);   
			}
		   for (int d = 0; d <  delArr.size(); d++)
			{
			   sql="delete from POLY_POINT where sn="+delArr.getInteger(d);
			   stm.addBatch(sql);
			}
		   for (int e = 0; e <  editArr.size(); e++)
			{
				JSONArray  temp=editArr.getJSONArray(e);
				sql="update POLY_POINT set M_NAME= '"+temp.getString(2)+"' , TIME1=TO_DATE ('"+temp.getString(1)+"', 'yyyy-mm-dd hh24:mi:ss') ,CASE_SN="+temp.getInteger(3)+" ,  TEXT1='"+temp.getString(4)+"' where SN="+temp.getInteger(0);
				stm.addBatch(sql);
			}
    	  Map<String,Object> map1 = new HashMap<String,Object>();  
          map1.put("result",1);  
          jsonString = JSON.toJSONString(map1);	  
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
       }finally
       {   
    	   stm.close();
    	   stm = null;
    	    con.close();
    	    con = null;
       }   
	  return jsonString;
  }
 

  public String doinfo(String key,int mode) 
  {  
	  String jsonString="{\"result\":0}";
	  if (mode==1)
  {
	  //String jsonString="{\"result\":0}";
	  String sql = "select * from PLAN_NAME where SN = "+key;
		List userData = getJdbcTemplate().queryForList(sql);
		if (userData.size() > 0)
		{
			int size = userData.size();
			Object arr[] = new Object[size];
			for (int i = 0; i < size; i++)
			{
				//ArrayList xCell = new ArrayList();
				Map userMap = (Map) userData.get(i);
				//int sn = Integer.valueOf(userMap.get("SN").toString());
				
				
				String P_EXPLAIN ="";
				 try {   
					 P_EXPLAIN = userMap.get("P_EXPLAIN").toString();
		           	} catch (NullPointerException e1) {   
		           		e1.printStackTrace();   
		           	}   
				
				//xCell.add(sn);
				
				//xCell.add(P_EXPLAIN);
				//xCell.add("<a id='infoBtn' href='javascript:showInfo("+sn+","+i+");'><span class='fa fa-edit'></span></a>&nbsp;&nbsp;<a id='infohide' href='javascript:hideInfo();'><span class='fa fa-stop'></span></a>");
				//xCell.add("<a id='infoBtn' href='javascript:showInfo("+sn+","+i+");'><span class='fa fa-edit'></span></a>");

				arr[i] = P_EXPLAIN;
			}
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("data", arr);
			map.put("result",1);  
			jsonString = JSON.toJSONString(map);
		}
  }
	  if (mode==2)
	  {
		  //String jsonString="{\"result\":0}";
		  String sql = "delete from PLAN_NAME where SN = "+key;
			int i=getJdbcTemplate().update(sql);
			//log("delete:"+i);
				Map<String, Object> map = new HashMap<String, Object>();
				map.put("data", i);
				map.put("result",1);  
				jsonString = JSON.toJSONString(map);
			
	  }
	  return jsonString;
  }
    public String resettime(String key,int tp)
    {
        String jsonString="{\"result\":0}";
        String[] tb = {""," prtuana"," prtudig"};
        String[] keys = {""," saveno="+key," kkey='"+key+"'"};
        LocalDateTime rightnow = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String dtstr = rightnow.format(formatter);

        {
            //String jsonString="{\"result\":0}";
            String sql = "update "+tb[tp]+"  set online=0,checktime='"+dtstr+"',timestat=0,warntimes=0 where "+keys[tp];
            int i=getJdbcTemplate().update(sql);
            //log("delete:"+i);
            Map<String, Object> map = new HashMap<String, Object>();
            map.put("data", i);
            map.put("result",1);
            jsonString = JSON.toJSONString(map);

        }
        return jsonString;
    }
  public String delgd(int key,int mode) 
  {  
	  String jsonString="{\"result\":0}";
	  
	  
	  {
		  //String jsonString="{\"result\":0}";
		  String sql = "insert into del_gd values ( "+key+","+mode+")";
			int i=getJdbcTemplate().update(sql);
			//log("delete:"+i);
				Map<String, Object> map = new HashMap<String, Object>();
				map.put("data", i);
				map.put("result",1);  
				jsonString = JSON.toJSONString(map);
			
	  }
	  return jsonString;
  }
    public String setEvtStat(String info)
    {
        String jsonString="{\"result\":0}";
        String[] ifo = info.split(",");
        //log(ifo[0]);
        String dbname=(ifo[0].matches("1")?"hevt":"hevtyc")+ifo[1].substring(1,2);

        {
            //String jsonString="{\"result\":0}";
            String sql = "update "+dbname+" set readstatus=1 where ymd="+ifo[1]+" and hms="+ifo[2]+" and ch="+ifo[3]+" and xh="+ifo[4];
            //log(sql);
            try {
                int i=getJdbcTemplate().update(sql);

                Map<String, Object> map = new HashMap<String, Object>();
                map.put("data", i);
                map.put("result",1);
                jsonString = JSON.toJSONString(map);
            }catch (Exception e)
            {

            }


        }
        return jsonString;
    }
  public String doTfSearch(String key) 
  {
	  String[] keys=key.split("\\|");
	  keys[0]=keys[0].trim();
	  keys[1]=keys[1].trim();
	  String jsonString="{\"result\":0}";
	  String sql =null;
	  if (keys[0].length()>0 && keys[1].length()>0)
	  {sql="select * from PLAN_NAME where P_NAME like '%"+keys[1]+"%' and P_YEAR like '%"+keys[0]+"%'";}
	  if (keys[0].length()>0 && keys[1].length()==0)
	  {sql="select * from PLAN_NAME where  P_YEAR like '%"+keys[0]+"%'";}
	  if (keys[0].length()==0 && keys[1].length()>0)
	  {sql="select * from PLAN_NAME where P_NAME like '%"+keys[1]+"%' ";}
      //log("sql:"+sql);
	  List userData = getJdbcTemplate().queryForList(sql);
		if (userData.size() > 0)
		{
			int size = userData.size();
			Object arr[] = new Object[size];
			for (int i = 0; i < size; i++)
			{
				ArrayList xCell = new ArrayList();
				Map userMap = (Map) userData.get(i);
				int sn = Integer.valueOf(userMap.get("SN").toString());
				String P_NAME = userMap.get("P_NAME").toString();
				String P_YEAR = userMap.get("P_YEAR").toString();

				xCell.add(sn);
				xCell.add(P_YEAR);
				xCell.add(P_NAME);
				xCell.add("<a id='infoBtn' href='javascript:showInfo("+sn+","+i+");'><span class='fa fa-edit'></span></a>&nbsp;&nbsp;<a id='infohide' href='javascript:hideInfo("+sn+","+i+");'><span class='glyphicon glyphicon-trash'></span></a>");
				//xCell.add("<a id='infoBtn' href='javascript:showInfo("+sn+","+i+");'><span class='fa fa-edit'></span></a>");

				arr[i] = xCell;
			}
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("data", arr);
			map.put("result",1);  
			jsonString = JSON.toJSONString(map);
		}
	  return jsonString;
  }
  
	public String getdlssCaseEditCell(int resion, int type, int inOut)
	{
		String jsonString="{\"result\":0}";
		String sql = "SELECT POLY_CASE.SN ,POLY_CASE.PLAN_L,POLY_CASE.ACTUAL_L,POLY_CASE.TYPE1,POLY_CASE.IN_OUT,POLY_CASE.RESION,POLY_CASE.TEXT1,PLAN_NAME.P_NAME,PLAN_NAME.P_YEAR from POLY_CASE  LEFT JOIN PLAN_NAME ON (PLAN_NAME.SN = POLY_CASE.PLAN_SN) where POLY_CASE.TYPE1="+type+" and POLY_CASE.IN_OUT="+inOut+" and POLY_CASE.RESION="+resion+" ORDER BY POLY_CASE.sn desc";
		if(type==6)
		{
			sql = "SELECT POLY_CASE.SN ,POLY_CASE.PLAN_L,POLY_CASE.ACTUAL_L,POLY_CASE.TYPE1,POLY_CASE.IN_OUT,POLY_CASE.RESION,POLY_CASE.TEXT1,PLAN_NAME.P_NAME,PLAN_NAME.P_YEAR from POLY_CASE  LEFT JOIN PLAN_NAME ON (PLAN_NAME.SN = POLY_CASE.PLAN_SN)  ORDER BY POLY_CASE.sn desc";
		}
		List userData = getJdbcTemplate().queryForList(sql);
		if (userData.size() > 0)
		{
			int size = userData.size();
			Object arr[] = new Object[size];
			for (int i = 0; i < size; i++)
			{
				ArrayList xCell = new ArrayList();
				Map userMap = (Map) userData.get(i);
				int sn = Integer.valueOf(userMap.get("SN").toString());
	    		double plan=Double.parseDouble(userMap.get("PLAN_L").toString());
	    		double actual=Double.parseDouble(userMap.get("ACTUAL_L").toString());
				int type1 =Integer.valueOf(userMap.get("TYPE1").toString());
				int inOut1 =Integer.valueOf(userMap.get("IN_OUT").toString());
				int resion1 =Integer.valueOf(userMap.get("RESION").toString());
				String P_NAME = userMap.get("P_NAME").toString();
				String P_YEAR = userMap.get("P_YEAR").toString();
				String text = userMap.get("TEXT1").toString();
	    		String typeStr="计划";
	    		if(type1==1)
	    		{
	    			typeStr="非计划";
	    		}
	    		String in_oUTStr="内部";
	    		if(inOut1==1)
	    		{
	    			in_oUTStr="外部";
	    		}
				xCell.add(sn);
				xCell.add(plan);
				xCell.add(actual);
				xCell.add(typeStr);
				xCell.add(in_oUTStr);
				xCell.add(resion1);
				xCell.add(P_YEAR+P_NAME);
				xCell.add(text);
				arr[i] = xCell;
			}
			Map<String, Object> map1 = new HashMap<String, Object>();
			map1.put("data", arr);
	        map1.put("result",1);  
			jsonString = JSON.toJSONString(map1);
		}
		return jsonString;
	}
  
  public String getdlssShowCell(String startT ,int resion, int type,int inOut)
  {
	  String jsonString="{\"result\":0}";
	  String sql ="SELECT POLY_POINT.SN,POLY_POINT.M_NAME,POLY_POINT.TIME1,POLY_CASE.PLAN_L,POLY_CASE.ACTUAL_L,POLY_CASE.TYPE1,POLY_CASE.IN_OUT,POLY_CASE.RESION,POLY_POINT.TEXT1,POLY_CASE.TEXT1 TEXT2 , PLAN_NAME.P_YEAR|| PLAN_NAME.P_NAME P_NAME from POLY_POINT left join POLY_CASE on (POLY_POINT.CASE_SN = POLY_CASE.SN ) LEFT JOIN PLAN_NAME ON (PLAN_NAME.SN = POLY_CASE.PLAN_SN) where POLY_CASE.TYPE1="+type+" and POLY_CASE.IN_OUT="+inOut+" and POLY_CASE.RESION="+resion+" and to_char(POLY_POINT.time1,'yyyymm')='"+startT+"' ORDER BY POLY_POINT.TIME1 ASC";
	  if(startT.endsWith("all"))
	  {
		  sql="SELECT POLY_POINT.SN,POLY_POINT.M_NAME,POLY_POINT.TIME1,POLY_CASE.PLAN_L,POLY_CASE.ACTUAL_L,POLY_CASE.TYPE1,POLY_CASE.IN_OUT,POLY_CASE.RESION,POLY_POINT.TEXT1,POLY_CASE.TEXT1 TEXT2 , PLAN_NAME.P_YEAR|| PLAN_NAME.P_NAME P_NAME from POLY_POINT left join POLY_CASE on (POLY_POINT.CASE_SN = POLY_CASE.SN ) LEFT JOIN PLAN_NAME ON (PLAN_NAME.SN = POLY_CASE.PLAN_SN) ORDER BY POLY_POINT.TIME1 ASC";
	  }
	  List userData = getJdbcTemplate().queryForList(sql);
	    if (userData.size() > 0)
	    {
	    	 int size=userData.size();
	    	 Object[] arr=new Object[size];
	    	for (int i = 0; i<size; i++)
	    	{
		    	 ArrayList xCell=new ArrayList();  
	    		Map userMap = (Map)userData.get(i);
	    		int sn=Integer.valueOf(userMap.get("SN").toString());
	    		String name=userMap.get("M_NAME").toString();
	    		String time=userMap.get("TIME1").toString();
	    		time=time.substring(0, time.length()-2);
	    		double planL=Double.parseDouble(userMap.get("PLAN_L").toString());
	    		double actual=Double.parseDouble(userMap.get("ACTUAL_L").toString());
	    		int type1=Integer.valueOf(userMap.get("TYPE1").toString());
	    		String typeStr="计划";
	    		if(type1==1)
	    		{
	    			typeStr="非计划";
	    		}
	    		int in_oUT=Integer.valueOf(userMap.get("IN_OUT").toString());
	    		String in_oUTStr="内部";
	    		if(in_oUT==1)
	    		{
	    			in_oUTStr="外部";
	    		}
	    		int resion1=Integer.valueOf(userMap.get("RESION").toString());
	    		String PName=userMap.get("P_NAME").toString();
	    		String text=userMap.get("TEXT1").toString()+"，"+userMap.get("TEXT2").toString();
	    		xCell.add(sn);
	    		xCell.add(time);
	    		xCell.add(name);
	    		xCell.add(planL);
	    		xCell.add(actual);
	    		xCell.add(typeStr);
	    		xCell.add(in_oUTStr);
	    		xCell.add(resion1);
	    		xCell.add(PName);
	    		xCell.add(text);
	    		arr[i]=xCell;
	    	}
	    	  Map<String,Object> map1 = new HashMap<String,Object>();  
	          map1.put("data",arr);  
	          map1.put("result",1);  
	          jsonString = JSON.toJSONString(map1);
	    }
	  return jsonString;
  }
	  
	  
  public String getdlssPointEditCell(String startT)
  {
	  String jsonString="{\"result\":0}";
	  String sql ="SELECT * from POLY_POINT where(to_char(TIME1,'yyyy-mm')) = '"+startT+"' ORDER BY SN ASC";
	  if(startT.endsWith("all"))
	  {
		  sql="SELECT * from POLY_POINT ORDER BY SN ASC";
	  }
	  List userData = getJdbcTemplate().queryForList(sql);
	    if (userData.size() > 0)
	    {
	    	 int size=userData.size();
	    	 Object[] arr=new Object[size];
	    	for (int i = 0; i<size; i++)
	    	{
		    	 ArrayList xCell=new ArrayList();  
	    		Map userMap = (Map)userData.get(i);
	    		int sn=Integer.valueOf(userMap.get("SN").toString());
	    		String name=userMap.get("M_NAME").toString();
	    		String time=userMap.get("TIME1").toString();
	    		time=time.substring(0, time.length()-2);
	    		int caseSn=Integer.valueOf(userMap.get("CASE_SN").toString());
	    		String text=userMap.get("TEXT1").toString();
	    		xCell.add(sn);
	    		xCell.add(time);
	    		xCell.add(name);
	    		xCell.add(caseSn);
	    		xCell.add(text);
	    		arr[i]=xCell;
	    	}
	    	  Map<String,Object> map1 = new HashMap<String,Object>();  
	          map1.put("data",arr);  
	          map1.put("result",1);  
	          jsonString = JSON.toJSONString(map1);
	    }
	  
	  return jsonString;
  }
  public String getdlssCaseEditCell(String startT,String resion, String type,String inOut)
  {
	  String jsonString="";
	  String sql ="SELECT POLY_POINT.SN,TIME1,M_NAME,TYPE1,IN_OUT,RESION,PLAN_L,ACTUAL_L,POLY_POINT.TEXT1 from POLY_POINT left join POLY_CASE on (POLY_POINT.CASE_SN = POLY_CASE.SN ) where (to_char(POLY_POINT.TIME1,'yyyy-mm') = '"+startT+"') and POLY_CASE.RESION="+resion+" and POLY_CASE.TYPE1="+type+" and POLY_CASE.in_out="+inOut+" ORDER BY POLY_POINT.SN ASC";
	    List userData = getJdbcTemplate().queryForList(sql);
	    if (userData.size() > 0)
	    {
	    	 int size=userData.size();
	    	 Object[] arr=new Object[size];
	    	for (int i = 0; i<size; i++)
	    	{
		    	 ArrayList xCell=new ArrayList();  
	    		Map userMap = (Map)userData.get(i);
	    		int sn=Integer.valueOf(userMap.get("SN").toString());
	    		String name=userMap.get("M_NAME").toString();
	    		String time=userMap.get("TIME1").toString();
	    		double planL=Double.parseDouble(userMap.get("PLAN_L").toString());
	    		double actual=Double.parseDouble(userMap.get("ACTUAL_L").toString());
	    		int type1=Integer.valueOf(userMap.get("TYPE1").toString());
	    		int in_oUT=Integer.valueOf(userMap.get("IN_OUT").toString());
	    		int resion1=Integer.valueOf(userMap.get("RESION").toString());
	    		String text=userMap.get("TEXT1").toString();
	    		xCell.add(sn);
	    		xCell.add(time);
	    		xCell.add(name);
	    		xCell.add(type1);
	    		xCell.add(in_oUT);
	    		xCell.add(resion1);
	    		xCell.add(planL);
	    		xCell.add(actual);
	    		xCell.add(text);
	    		arr[i]=xCell;
	    	}
	    	  java.util.Map<String,Object> map1 = new HashMap<String,Object>();  
	          map1.put("data",arr);  
	            jsonString = JSON.toJSONString(map1);
	    }
	  
	  return jsonString;
  }
  

  //p_id number,sid number,dt string,sp number,rate number,ep number,et string,pd number,flag number
  public String saveGd(int aid , int pid,String sdt,float sp ,float rate, float ep,String et, float pd, int flag ) throws ParseException, SQLException
  {
	String jsonString="";
	Connection conn = null;
    conn = getJdbcTemplate().getDataSource().getConnection();
   
    PreparedStatement ps = conn.prepareStatement("call savegd(?,?,?,?,?,?,?,?,?) ", new String[] { "ID", "POINT_ID", "S_DATETIME","s_power","rate","e_power","e_datetime","pday","flag"});
    ps.setInt(1, aid);
    ps.setInt(2, pid);
    ps.setString(3, sdt);
    ps.setFloat(4,sp);
    ps.setFloat(5,rate);
    ps.setFloat(6,ep);
    ps.setString(7,et);
    ps.setFloat(8,pd);
    ps.setInt(9, flag);
    ps.executeUpdate();
    
    ps.close();
    ps = null;
  
    conn.close();
    conn = null;
	  java.util.Map<String,Object> map1 = new HashMap<String,Object>();  
      map1.put("result",1);  
      //map1.put("sn",newID);  
      jsonString = JSON.toJSONString(map1);
    return jsonString;
  }
  public String newTfPlan(int year , String pName) throws ParseException, SQLException
  {
	String jsonString="";
	Connection conn = null;
    conn = getJdbcTemplate().getDataSource().getConnection();
    int newID = 0;
    PreparedStatement ps = conn.prepareStatement("Insert into PLAN_NAME(SN,P_YEAR,P_NAME,P_EXPLAIN)values(Plan_name_sn.nextval,?,?,' ') ", new String[] { "SN", "P_YEAR", "P_NAME"});
    ps.setInt(1, year);
    ps.setString(2, pName);
    ps.executeUpdate();
    ResultSet rs = ps.getGeneratedKeys();
    if (rs.next())
    {
      newID = rs.getInt(1);
     // log(newID+":newid");
    }
    ps.close();
    ps = null;
    rs.close();
    rs = null;
    conn.close();
    conn = null;
	  java.util.Map<String,Object> map1 = new HashMap<String,Object>();  
      map1.put("result",1);  
      map1.put("sn",newID);  
      jsonString = JSON.toJSONString(map1);
    return jsonString;
  }
  public String updateTfPlan(int dsn , String p_exp) throws ParseException, SQLException
  {
	String jsonString="";
	Connection conn = null;
    conn = getJdbcTemplate().getDataSource().getConnection();
    int newID = 0;
    PreparedStatement ps = conn.prepareStatement("update PLAN_NAME set P_EXPLAIN=? where sn=? ", new String[] { "P_EXPLAIN","SN"});
    
    ps.setString(1, p_exp);
    ps.setInt(2, dsn);
    ps.executeUpdate();
    /*
    ResultSet rs = ps.getGeneratedKeys();
    if (rs.next())
    {
      newID = rs.getInt(1);
    }
    */
    ps.close();
    ps = null;
   // rs.close();
    //rs = null;
    conn.close();
    conn = null;
	  java.util.Map<String,Object> map1 = new HashMap<String,Object>();  
      map1.put("result",1);  
      //map1.put("sn",newID);  
      jsonString = JSON.toJSONString(map1);
    return jsonString;
  }
  
  public String tfExport(Integer sn,int tab,int mode,String from,String to) 
  {
	  String jsonString="{\"result\":0}";
	  String sql ="SELECT * from DL_TJ where  PLAN_SN="+sn+"  ORDER BY M_NAME ASC";
	  if(mode==2)
	  {
		  sql="SELECT M_NAME,sum(YJH_FDL) YJH_FDL ,sum(YJH_SSDL) YJH_SSDL ,sum(PLAN_FDL) PLAN_FDL ,sum(PLAN_SSDL) PLAN_SSDL ,sum(REAL_FDL) REAL_FDL ,sum(REAL_SSDL) REAL_SSDL  from DL_TJ_DAY where (to_char(DID,'yyyymmdd')) BETWEEN '"+from+"' and '"+to+"' and M_NAME!='p_all' and M_NAME!='p_1' and M_NAME!='p_2' and M_NAME!='p_3' and M_NAME!='p_4' GROUP by M_NAME ORDER BY M_NAME ASC";  
	  }
	    List userData = getJdbcTemplate().queryForList(sql);
	    int size=userData.size() ;
		Object arr[] = new Object[size+1];

		String[] head={ "","年计划发电量" ,"年计划损失电量" ,"实际计划发电量" ,"实际计划损失电量"};
		String[] head2={ "","年计划发电量" ,"年计划损失电量" ,"实际计划发电量" ,"实际计划损失电量" ,"实际发电量" ,"实际损失电量"};
	    if (size> 0)
	    {
			if(tab==1)
			{
				arr[0]=head;
			}
			else
			{
				arr[0]=head2;
			}
			
	    	for (int i = 0; i<size; i++)
	    	{
				ArrayList xCell = new ArrayList();
	    		Map planData = (Map)userData.get(i);
				String mName = (String )planData.get("M_NAME").toString();
				mName=getJzName(mName);
				String YJH_FDL = (String )planData.get("YJH_FDL").toString();
				String YJH_SSDL = (String )planData.get("YJH_SSDL").toString();
				String PLAN_FDL = (String )planData.get("PLAN_FDL").toString();
				String PLAN_SSDL = (String )planData.get("PLAN_SSDL").toString();
				String REAL_FDL = (String )planData.get("REAL_FDL").toString();
				String REAL_SSDL = (String )planData.get("REAL_SSDL").toString();
				xCell.add(mName);
				xCell.add(YJH_FDL);
				xCell.add(YJH_SSDL);
				xCell.add(PLAN_FDL);
				xCell.add(PLAN_SSDL);
				if(tab==2)
				{
					xCell.add(REAL_FDL);
					xCell.add(REAL_SSDL);	
				}
				arr[i+1] = xCell;
	    	}
			Map<String, Object> map1 = new HashMap<String, Object>();
			map1.put("data", arr);
	        map1.put("result",1);  
		    jsonString = JSON.toJSONString(map1);
	    }
	   return jsonString;
  }
  

	public String getTfPlanAll(String crew , String tab,int pId,int userId) throws ParseException
	  {
		    int gno=0;
		  	int vno=0;
		  	int sav=0;
		  	String dbnameString = "";
		  	String dbnameString2 = "";
		  	String jsonString="";
		  	String jzName="";
		  	String dateString ="";
		  	//log(crewLong);
            dbnameString= "hyc";
            dbnameString2 = "prtuana";
            String sql="";


          dbnameString= "hyc";
          dbnameString2 = "prtuana";
          List userData = null;
          Map jznm = null;
          sav=pId;
          if (sav==-1)
          {
              sql = "select min(a.saveno) sav from prtuana a,prtu b  where (power(2,"+userId+"-1)&a.author_read)>0 and  a.rtuno=b.rtuno and b.domain="+tab;
              //log(sql);
              userData = getJdbcTemplate().queryForList(sql);
              jznm = (Map)userData.get(0);
              sav = Integer.parseInt(jznm.get("sav").toString());
          }


          gno = (int)sav/200;
          vno = sav % 200;
		  	dateString = crew.replace('/', '-');
          DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

          LocalDateTime dateTime2 = LocalDateTime.parse(dateString, formatter);
		  	 dateString = ""+(dateTime2.getMonthValue()*100+dateTime2.getDayOfMonth());

		  	java.util.Map<String,Object> map1 = new HashMap<String,Object>() ;
		  	  sql = "select a.name anm,b.name bnm from prtu a,"+dbnameString2+" b where a.rtuno=b.rtuno and b.saveno="+sav;

			  
			     userData = getJdbcTemplate().queryForList(sql);
			     jznm = (Map)userData.get(0);
			    jzName=jznm.get("anm").toString()+jznm.get("bnm").toString();
			   // log(jzName);
		     sql = "select IFNULL(val"+vno+",0) val,savetime from "+dbnameString+(dateTime2.getYear()%10)+" where  groupno="+gno+"  and savetime>="+dateString+"0000 and savetime<"+dateString+"9999 order by savetime"; 
		     userData.clear();
		    
		    userData = getJdbcTemplate().queryForList(sql);
		   
		  
		    int size=userData.size() ;
		    //size = 288;
		    String infoArr[ ]=new String[288];
		    java.util.Map<String,Object> map2 = new HashMap<String,Object>();
		    Object arr[] = new Object[24];
		    for (int i = 0; i<288; i++)
	    	{
		    	infoArr[i] ="";
	    	}
		   
		    
		   
			

		    if (size> 0)
		    {
		    	for (int i = 0; i<size; i++)
		    	{
		    		Map planData = (Map)userData.get(i);
			    	   
		    	    {
						
						String mName = planData.get("val").toString();
						BigDecimal bd = new BigDecimal(planData.get("savetime").toString());  
				       
						String st	=	bd.toPlainString();
						if (st.indexOf(".")>0)
							st = st.substring(0,st.indexOf("."));		
			    		int    idx   = Integer.parseInt(st.substring(st.length()-4));
			    		idx = ((int)(idx/100))*12+(idx%100)/5;
			    		infoArr[idx]=mName;

		    	     }
		    	}
		    }
		    for (int i = 0; i<24; i++)
	    	{
		    	
	    		ArrayList xCell = new ArrayList();
	    		xCell.add(""+i+":00");
	    		for (int j = 0; j<12; j++)
	    		{
	    			xCell.add(infoArr[i*12+j]);
	    		
	    		}
				arr[i] = xCell;
	    	}
			map1.put("data", arr);
	        map1.put("result",1);  
	        map1.put("jzName",jzName);
		    jsonString = JSON.toJSONString(map1);
		    //log(jsonString);
		    return jsonString;
	  }
	public String getTfPlanAll_kb(String crew , String tab,int pId) throws ParseException
	  {
		    int gno=0;
		  	int vno=0;
		  	String dbnameString = "";
		  	String dbnameString2 = "";
		  	String jsonString="";
		  	String jzName="";
		  	String dateString ="",dateString2 ="";
		  	//log(crewLong);
		  	if (Integer.parseInt(tab) ==1) 
		  		{
		  		dbnameString= "hyc";
		  		dbnameString2 = "prtuana";
		  		}
		  	else {
		  		dbnameString= "hdn";
		  		dbnameString2 = "prtupul";
			}
		  	gno = (int)pId/200;
		  	vno = pId % 200;
		  	dateString = crew.replace('/', '-');
		  	dateString2 = dateString;

          DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

          LocalDateTime dateTime2 = LocalDateTime.parse(dateString, formatter);
          dateString = ""+(dateTime2.getMonthValue()*100+dateTime2.getDayOfMonth());
			  //dateTime2 =dateTime2.plusDays(1);
		  	java.util.Map<String,Object> map1 = new HashMap<String,Object>() ;
		  	String  sql = "select a.name anm,b.name bnm from prtu a,"+dbnameString2+" b where a.rtuno=b.rtuno and b.saveno="+pId; 

			  
			    List userData = getJdbcTemplate().queryForList(sql);
			    Map jznm = (Map)userData.get(0);
			    jzName=jznm.get("anm").toString()+jznm.get("bnm").toString();
			   // log(jzName);
		     sql = "select IFNULL(val"+vno+",0) val,savetime from "+dbnameString+(dateTime2.getYear()%10)+" where  groupno="+gno+"  and savetime>="+dateString+"0000 and savetime<"+dateString+"9999 order by savetime"; 
		     userData.clear();
		    
		    userData = getJdbcTemplate().queryForList(sql);
		   
		  
		    int size=userData.size() ;
		    //size = 288;
		    String infoArr[]=new String[288];
		    java.util.Map<String,Object> map2 = new HashMap<String,Object>();
		    Object arr[] = new Object[24];
		    for (int i = 0; i<288; i++)
	    	{
		    	infoArr[i] ="";
	    	}
		   
		    
		   
			

		    if (size> 0)
		    {
		    	for (int i = 0; i<size; i++)
		    	{
		    		Map planData = (Map)userData.get(i);
			    	   
		    	    {
						
						String mName = planData.get("val").toString();
						BigDecimal bd = new BigDecimal(planData.get("savetime").toString());  
				       
						String st	=	bd.toPlainString();
						if (st.indexOf(".")>0)
							st = st.substring(0,st.indexOf("."));		
			    		int    idx   = Integer.parseInt(st.substring(st.length()-4));
			    		idx = ((int)(idx/100))*12+(idx%100)/5;
			    		infoArr[idx]=mName;

		    	     }
		    	}
		    }
		    for (int i = 0; i<24; i++)
	    	{
		    	
	    		ArrayList xCell = new ArrayList();
	    		xCell.add(dateString2);
	    		xCell.add(""+i+":00");
	    		for (int j = 0; j<12; j++)
	    		{
	    			xCell.add(infoArr[i*12+j]);
	    		
	    		}
				arr[i] = xCell;
	    	}
			map1.put("data", arr);
	        map1.put("result",1);  
	        map1.put("jzName",jzName);
		    jsonString = JSON.toJSONString(map1);
		    //log(jsonString);
		    return jsonString;
	  }
	public String getTfPlanAll_evt(String crew , String tab,int pId,int ob,String pno,int userId) throws ParseException
	  {
		    int gno=0;
		  	int vno=0;
		  	String dbnameString = "";
		  	String dbnameString2 = "";
		  	String jsonString="";
		  	String jzName="";
		  	int zt =0 ;

		  	String dateString ="";
		  	String dateString2 ="";
		  	//log(crewLong);
		    String[] evt_info ={" "," ","保护动作","数据越限","遥测越限","电流越限","预告警","故障告警","备用","备用","通讯中断","漏水报警","烟雾报警","照明开/关"};
		    String[] zt_info ={"恢复","告警","告警","数据越限","数据越限","电流越限","预告警","故障告警","备用","备用"};
		  		dbnameString= "hevt";
		  		dbnameString2 = "prtupul";
          java.util.Map<String,Object> map1 = new HashMap<String,Object>() ;
		  

		  	dateString = crew.replace('/', '-');
		  	dateString2 = tab.replace('/', '-');
			
		  	 //DateTime dateTime2 = new DateTime(new SimpleDateFormat("yyyy-MM-dd").parse(dateString));
          DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

          LocalDateTime dateTime2 = LocalDateTime.parse(dateString, formatter);
          dateString = ""+(dateTime2.getMonthValue()*100+dateTime2.getDayOfMonth());
		  	 //dateString = ""+(dateTime2.getMonthOfYear()*100+dateTime2.getDayOfMonth());
		  	dateString = dateString.replace("-","");
		  	dateString2 =dateString2.replace("-","");
		  	dateString = dateString.substring(2);
		  	dateString2 = dateString2.substring(2);

		  	String  sql = "select a.ymd yd,a.hms hs,a.event evt,' ' sm,a.zt tz,d.e_info ifo,b.name bm,c.name cm from hevt"+(dateTime2.getYear()%10)+" a,prtudig b,prtu c,etype_info d where (power(2,"+userId+"-1) & b.author_read)>0 and b.type=d.e_type and a.zt=d.e_zt and  a.ch=c.rtuno and a.xh=b.sn and a.ch=b.rtuno and a.ymd>="+dateString+" and a.ymd<="+dateString2+" ";
          if (pId>=0) sql= sql+" and a.ch="+pId;
          else {
        	  sql= sql+" and c.domain="+pno;
		}
          sql = sql + " union select a.ymd yd,a.hms hs,'yc' evt,a.val sm,a.zt tz,a.tlimit ifo,b.name bm,c.name cm from hevtyc"+(dateTime2.getYear()%10)+" a,prtuana b,prtu c  where  (power(2,"+userId+"-1) & b.author_read)>0 and  a.ch=c.rtuno and a.xh=b.sn and a.ch=b.rtuno and a.ymd>="+dateString+" and a.ymd<="+dateString2;
          if (pId>=0) sql= sql+" and a.ch="+pId;
          else {
        	  sql= sql+" and c.domain="+pno;
		}
          if (ob==0) sql = sql + " order by yd desc,hs desc";
			else sql= sql+ " order by yd desc,hs desc";

log(sql);
			    List userData = getJdbcTemplate().queryForList(sql);
			   
		    
		    int size=userData.size() ;

			Object arr[] = new Object[size];

		    if (size> 0)
		    {
               // log(sql);
		    	for (int i = 0; i<size; i++)
		    	{
		    		ArrayList xCell = new ArrayList();
		    		Map planData = (Map)userData.get(i);
		    		String ymd = planData.get("yd").toString();
		    		String hms = planData.get("hs").toString();
		    		ymd = "20"+ymd;
		    		if (hms.length()<2) hms="0"+hms;
		    		if (hms.length()<3) hms="0"+hms;
		    		if (hms.length()<4) hms="0"+hms;
		    		if (hms.length()<5) hms="0"+hms;
		    		if (hms.length()<6) hms="0"+hms;

		    		int vx = 0;
		    		if (planData.get("evt").toString().indexOf("yc")>=0) vx=1;
		    		else vx = 0;
		    		try
		    		{
		    		ymd = ymd.substring(0,4)+"-"+ymd.substring(4,6)+"-"+ymd.substring(6,8);
		    		hms = hms.substring(0,2)+":"+hms.substring(2,4)+":"+hms.substring(4,6);
		    		}catch(Exception e) 
		    		{
		    			
		    		}
		    		xCell.add(""+(i+1));
			    	xCell.add(""+ymd+" "+hms);
			    	xCell.add(""+planData.get("cm").toString());
			    	xCell.add(""+planData.get("bm").toString());

			    	
			    	zt = Integer.parseInt(planData.get("tz").toString());
			    	if (Math.abs(zt)>=2) zt=0;

			    	if (vx==1)
                    {
                        switch(zt){
                            case -1 :

                                xCell.add("越下线");
                                try{
                                    xCell.add(""+planData.get("sm").toString()+"("+planData.get("ifo").toString()+")");
                                }
                                catch(Exception e)
                                {

                                    try{
                                        xCell.add(""+planData.get("sm").toString());
                                    }
                                    catch(Exception ee)
                                    {
                                        xCell.add("   ");
                                    }
                                }
                                break;
                            case 0 :
                                xCell.add("恢复");
                                try{
                                    xCell.add(""+planData.get("sm").toString());
                                }
                                catch(Exception e)
                                {
                                    xCell.add("   ");
                                }
                                break;
                            case 1 :
                                xCell.add("越上线");
                                try{
                                    xCell.add(""+planData.get("sm").toString()+"("+planData.get("ifo").toString()+")");
                                }
                                catch(Exception e)
                                {

                                    try{
                                        xCell.add(""+planData.get("sm").toString());
                                    }
                                    catch(Exception ee)
                                    {
                                        xCell.add("   ");
                                    }
                                }
                                break;

                            default :
                                break;

                        }

                    }else
                    {
                        try{
                            xCell.add(""+planData.get("ifo").toString());
                        }
                        catch(Exception e)
                        {
                            xCell.add("   ");
                        }

                        try{
                            xCell.add(""+planData.get("sm").toString());
                        }
                        catch(Exception e)
                        {
                            xCell.add("   ");
                        }
                    }

					arr[i] = xCell;

		    	}
		    }
          //log(sql);
			map1.put("data", arr);
	        map1.put("result",1);  
	        map1.put("jzName",jzName);
		    jsonString = JSON.toJSONString(map1);
		    //log(jsonString);
		    return jsonString;
	  }
  public String getTfPlanAll_xb(String crew , String tab,String pId,int nmd) throws ParseException
  {
	    int gno=0;
	  	int vno=0;
	  	 String abc[ ]=new String[3];
	  	float[][] hgl = new float[3][32];
	  	String dbnameString = "";
	  	String dbnameString2 = "";
	  	String jsonString="";
	  	String jzName="";
	  	String dateString ="";
	  	String savstr = "-99";
	  	//log(crewLong);
	  	abc[0]="A";
	  	abc[1]="B";
	  	abc[2]="C";
	  	for (int i=0;i<3;i++)
	  	{
	  		for (int j=0 ;j<32;j++)
	  		{
	  			hgl[i][j] = 0;
	  		}
	  	}
	  
	  		dbnameString= "xiebo_hgl";
	  		dbnameString2 = "prtuana";
	  		String  sql = "select a.name anm,get_subs(b.name,1) bnm,b.saveno sav from prtu a,"+dbnameString2+" b where a.rtuno=b.rtuno and b.type=7 and b.name like '%"+pId+"%' order by saveno"; 

	       //log(sql);
		    List userData = getJdbcTemplate().queryForList(sql);
		    int size=userData.size() ;
		    String savArr[ ]=new String[size];
		    if (userData.size() > 0)
		    {
		    	for (int i = 0; i<size; i++)
		    	{
		    		Map userMap = (Map)userData.get(i);
		    		
		    		savArr[i] = userMap.get("sav").toString();
		    		savstr = savstr+","+userMap.get("sav").toString();
		    		jzName=userMap.get("anm").toString()+userMap.get("bnm").toString();
		    	}
		    }	
	  	
	  	dateString = crew.replace('/', '-');
	  	// DateTime dateTime2 = new DateTime(new SimpleDateFormat("yyyy-MM-dd").parse(dateString));
	  	// dateString = ""+(dateTime2.getYear()*100+dateTime2.getMonthOfYear());

      DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

      LocalDateTime dateTime2 = LocalDateTime.parse(dateString, formatter);
      dateString = ""+(dateTime2.getYear()*100+dateTime2.getMonthValue());
		  //dateTime2 =dateTime2.plusDays(1);
	  	java.util.Map<String,Object> map1 = new HashMap<String,Object>() ;
	  	  sql = "select hegelv,did,saveno from xiebo_hgl where  did>"+dateString+"00 and did<"+dateString+"99 and saveno in ("+savstr+") order by did,saveno"; 
	     userData.clear();
	    
	    userData = getJdbcTemplate().queryForList(sql);
	    //log(sql);
	    //log(userData.size());
	     size=userData.size() ;
	   
	    
	   
		Object arr[] = new Object[7];

	    if (size> 0)
	    {
	    	for (int i = 0; i<size; i++)
	    	{
	    		Map planData = (Map)userData.get(i);
	    		int dd = (Integer.parseInt(planData.get("did").toString())%100)-1;
	    		hgl[i%3][dd] = Float.parseFloat(planData.get("hegelv").toString());
	    	}
	    }
		for (int i=0;i<3;i++)
		{
			ArrayList xCell = new ArrayList();
			xCell.add(abc[i]+"相");
			for (int j=0;j<16;j++)
			{
				xCell.add(""+hgl[i][j]);
			}
			arr[i] = xCell;
		}
		
        ArrayList xCell = new ArrayList();
        xCell.add("日期");
        for (int i=17 ;i<32;i++)
		xCell.add(""+i);
        arr[3] = xCell;
        for (int i=0;i<3;i++)
		{
			ArrayList xCell2 = new ArrayList();
			xCell2.add(abc[i]+"相");
			for (int j=16;j<31;j++)
			{
				xCell2.add(""+hgl[i][j]);
			}
			arr[i+4] = xCell2;
		}
		

		map1.put("data", arr);
        map1.put("result",1);  
        map1.put("jzName",jzName);
	    jsonString = JSON.toJSONString(map1);
	    //log(jsonString);
	    return jsonString;
  }
  public String getTfPlanAll_dl(String crew , String tab,String pId,int nmd) throws ParseException
  {
	    int gno=0;
	  	int vno=0;
	  	 String abc[ ]=new String[3];
	  	float[][] max = new float[3][24];
	  	String[][] maxt = new String[3][24];
	  	String dbnameString = "";
	  	String dbnameString2 = "";
	  	String jsonString="";
	  	String jzName="";
	  	String dateString ="";
	  	String savstr = "-99";
	  	//log(crewLong);
	  	abc[0]="A";
	  	abc[1]="B";
	  	abc[2]="C";
	  	for (int i=0;i<3;i++)
	  	{
	  		for (int j=0 ;j<24;j++)
	  		{
	  			max[i][j] = 0;
	  		}
	  	}
	  	for (int i=0;i<3;i++)
	  	{
	  		for (int j=0 ;j<24;j++)
	  		{
	  			maxt[i][j] ="";
	  		}
	  	}
	  		dbnameString= "dl_max";
	  		dbnameString2 = "prtuana";
	  		String  sql = "select a.name anm,get_subs(b.name,1) bnm,b.saveno sav from prtu a,"+dbnameString2+" b where a.rtuno=b.rtuno and b.type=2 and b.name like '%"+pId+"%' order by saveno"; 

	      // log(sql);
		    List userData = getJdbcTemplate().queryForList(sql);
		    int size=userData.size() ;
		    String savArr[ ]=new String[size];
		    if (userData.size() > 0)
		    {
		    	for (int i = 0; i<size; i++)
		    	{
		    		Map userMap = (Map)userData.get(i);
		    		
		    		savArr[i] = userMap.get("sav").toString();
		    		savstr = savstr+","+userMap.get("sav").toString();
		    		jzName=userMap.get("anm").toString()+userMap.get("bnm").toString();
		    	}
		    }	
	  	
	  	dateString = crew.replace('/', '-');
	  	// DateTime dateTime2 = new DateTime(new SimpleDateFormat("yyyy-MM-dd").parse(dateString));
	  	// dateString = ""+(dateTime2.getYear());
      DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

      LocalDateTime dateTime2 = LocalDateTime.parse(dateString, formatter);
      dateString = String.valueOf(dateTime2.getMonthValue());
		  //dateTime2 =dateTime2.plusDays(1);
	  	java.util.Map<String,Object> map1 = new HashMap<String,Object>() ;
	  	  sql = "select val,valtime,did,saveno from dl_max where  did>"+dateString+"0000 and did<"+dateString+"9999 and saveno in ("+savstr+") order by did,saveno"; 
	     userData.clear();
	    
	    userData = getJdbcTemplate().queryForList(sql);
	    //log(sql);
	   // log(userData.size());
	     size=userData.size() ;
	   
	    
	   
		Object arr[] = new Object[6];

	    if (size> 0)
	    {
	    	for (int i = 0; i<size; i++)
	    	{
	    		Map planData = (Map)userData.get(i);
	    		int dd =((int) (Integer.parseInt(planData.get("did").toString())%10000)/100)-1;
	    		max[i%3][dd] = Float.parseFloat(planData.get("val").toString());
	    		dateString =planData.get("valtime").toString();
	    	    if (dateString.length()<8) dateString="0"+dateString;
	    	    dateString=dateString.substring(0,2)+"-"+dateString.substring(2,4)+" "+dateString.substring(4,6)+":"+dateString.substring(6,8);
	    		maxt[i%3][dd] = dateString;
	    	}
	    }
		for (int i=0;i<3;i++)
		{
			ArrayList xCell = new ArrayList();
			xCell.add(abc[i]+"相");
			for (int j=0;j<12;j++)
			{
				xCell.add(""+max[i][j]);
			}
			arr[i*2] = xCell;
		}
		
      
        for (int i=0;i<3;i++)
		{
			ArrayList xCell2 = new ArrayList();
			xCell2.add("最大时间");
			for (int j=0;j<12;j++)
			{
				xCell2.add(""+maxt[i][j]);
			}
			arr[i*2+1] = xCell2;
		}
		

		map1.put("data", arr);
        map1.put("result",1);  
        map1.put("jzName",jzName);
	    jsonString = JSON.toJSONString(map1);
	    //log(jsonString);
	    return jsonString;
  }
  public String showgd(int aids , int pids) throws ParseException
  {
	  String jsonString="";
	    String sql ="";
	    sql ="select a.id aid,a.p_day ap_day,a.point_id apoint_id,a.s_power as_power,a.e_power ae_power,a.rate arate,a.s_datetime as_datetime,a.e_datetime ae_datetime,a.mark amark,b.evt_id bevt_id from plan_gd a,plan_point_hd b,plan_point c where a.point_id=c.id and c.evt_id=b.evt_id and a.id=b.evt_sn  and b.jz_id=c.jz_id  and   a.POINT_ID="+pids +" and a.id="+aids;

	   //log(sql);
	    
	    List userData = getJdbcTemplate().queryForList(sql);
	    int size=userData.size() ;
	    String infoArr[ ]=new String[size];
		Object arr[] = new Object[size];
		String info="";
	    if (size> 0)
	    {
	    	for (int i = 0; i<size; i++)
	    	{
				ArrayList xCell = new ArrayList();
	    		Map planData = (Map)userData.get(i);
	    	   // if(tab==1)
	    	    {
	    	    	float pd =0;
	    	    	int id = Integer.valueOf(planData.get("aid").toString()); 
	    	    	try
	    	    	{
					pd = Float.valueOf(planData.get("ap_day").toString());
	    	    	}catch(Exception e) 
		    		{
		    			pd=0;	
		    		}
					int pid = Integer.valueOf(planData.get("apoint_id").toString());
					int amk = Integer.valueOf(planData.get("amark").toString());
					int beid = Integer.valueOf(planData.get("bevt_id").toString());
					float sp = Float.valueOf(planData.get("as_power").toString());
					float ep = Float.valueOf(planData.get("ae_power").toString());
					float ra = Float.valueOf(planData.get("arate").toString());
		    		String startT=planData.get("as_datetime").toString().substring(0,planData.get("as_datetime").toString().length()-5);
		    		String endT=planData.get("ae_datetime").toString().substring(0,planData.get("ae_datetime").toString().length()-5);
		    		String updown="升";
		    		if (ra<0) {updown="降";}
		    		else if (ra==0) {updown="解列";}
                    
		    		//double startP = Double.parseDouble(planData.get("S_POWER").toString());
					//double rate =Double.parseDouble(planData.get("RATE").toString());
					//String upDown =planData.get("UPDOWN").toString().replace("0", "降至").replace("1", "升至");
					//double endP =Double.parseDouble(planData.get("E_POWER").toString());
					xCell.add(id);
					xCell.add(pid);
					xCell.add(startT);
					xCell.add(sp);
					xCell.add(updown);
					xCell.add(Math.abs(ra));
					xCell.add(ep);
					xCell.add(endT);
					xCell.add(pd);
					
					//xCell.add(startP);
					//xCell.add(rate);
					//xCell.add(upDown);
					//xCell.add(endP);
					//xCell.add(endT);    	
					xCell.add("<a id='savBtn' href='javascript:void(0);'><span class='fa fa-save'></span></a>");
					xCell.add(amk);
					xCell.add(beid);
					/*if(jz.endsWith("all"))
					{
						xCell.add("");
					}
					else
					{
						xCell.add("<a id='subBtn' href='javascript:void(0);'><span class='fa fa-save'></span></a>&nbsp;&nbsp;&nbsp;&nbsp;<a id='delBtn' href='javascript:void(0);'><span class='glyphicon glyphicon-trash'></span></a>");
					}*/
	    	    }
	    	   
				arr[i] = xCell;
	    	}
	    }
		Map<String, Object> map1 = new HashMap<String, Object>();
		map1.put("data", arr);
        map1.put("result",1);  
	    jsonString = JSON.toJSONString(map1);
	    //log(jsonString);
	    return jsonString;
  }
  public String getyTfPlanAll(Integer sn , String jz,int tab) throws ParseException
  {
	  String jsonString="";
	    String sql ="";
	    String selMName="jz_id='"+jz+"' ";
	    if(tab==1)
	    {
		    if(jz.endsWith("all"))
		    {
		    	 sql ="SELECT * from PLAN_POINT where  PLAN_ID="+sn +" order by M_NAME asc ,START_TIME asc";
		    }
		    else
		    {
		    	
		    	if(jz.endsWith("p_1"))
		    	{
		    		selMName="M_NAME='Q1-1' ";
		    	}
		    	else if(jz.endsWith("p_2"))
		    	{
		    		selMName="(M_NAME='Q2-1' or M_NAME='Q2-2' or  M_NAME='Q2-3' or  M_NAME='Q2-4')";
		    	}
		    	else if(jz.endsWith("p_3"))
		    	{
		    		selMName="(M_NAME='Q3-1' or M_NAME='Q3-2' )";
		    	}
		    	else if(jz.endsWith("p_4"))
		    	{
		    		selMName="(M_NAME='F1-1' or  M_NAME='F1-2') ";
		    	}
		    	
		    	 sql ="SELECT * from PLAN where "+selMName+" and PLAN_SN="+sn+" order by M_NAME asc ,START_TIME asc";
		    }
		    sql ="SELECT * from PLAN_POINT where  PLAN_ID="+sn +" and jz_id=get_jzid2('"+jz+"') order by EVT_ID asc ,S_DATETIME asc";
           //log(sql);
           //log(jz);
	    }
	    else
	    {
		    if(jz.endsWith("all"))
		    {
		    	 sql ="SELECT * from DL_TJ where  PLAN_SN="+sn+" order by M_NAME asc";
		    }
		    else
		    {
		    	if(jz.endsWith("p_1"))
		    	{
		    		selMName="M_NAME='Q1-1' ";
		    	}
		    	else if(jz.endsWith("p_2"))
		    	{
		    		selMName="(M_NAME='Q2-1' or M_NAME='Q2-2' or  M_NAME='Q2-3' or  M_NAME='Q2-4')";
		    	}
		    	else if(jz.endsWith("p_3"))
		    	{
		    		selMName="(M_NAME='Q3-1' or M_NAME='Q3-2' )";
		    	}
		    	else if(jz.endsWith("p_4"))
		    	{
		    		selMName="(M_NAME='F1-1' or  M_NAME='F1-2' )";
		    	}
		    	 sql ="SELECT * from DL_TJ where "+selMName+" and PLAN_SN="+sn;
		    }
	    }
	    
	    List userData = getJdbcTemplate().queryForList(sql);
	    int size=userData.size() ;
	    String infoArr[ ]=new String[size];
		Object arr[] = new Object[size];
		String info="";
	    if (size> 0)
	    {
	    	for (int i = 0; i<size; i++)
	    	{
				ArrayList xCell = new ArrayList();
	    		Map planData = (Map)userData.get(i);
	    	    if(tab==1)
	    	    {
	    	    	int id = Integer.valueOf(planData.get("id").toString()); 
					//int snNum = Integer.valueOf(planData.get("plan_id").toString());
					int jzid = Integer.valueOf(planData.get("jz_id").toString());
					int evtid = Integer.valueOf(planData.get("evt_id").toString());
		    		String startT=planData.get("S_DATETIME").toString().substring(0,planData.get("S_DATETIME").toString().length()-11);
		    		String endT=planData.get("E_DATETIME").toString().substring(0,planData.get("E_DATETIME").toString().length()-11);
		    		try
		    		{
		    		    info=planData.get("info").toString();
		    		}catch(Exception e) 
		    		{
		    			info="";	
		    		}

		    		//double startP = Double.parseDouble(planData.get("S_POWER").toString());
					//double rate =Double.parseDouble(planData.get("RATE").toString());
					//String upDown =planData.get("UPDOWN").toString().replace("0", "降至").replace("1", "升至");
					//double endP =Double.parseDouble(planData.get("E_POWER").toString());
					xCell.add(id);
					//xCell.add(snNum);
					xCell.add(jzname[jzid]);
					xCell.add(evt[evtid]);
					xCell.add(info);
					xCell.add(startT);
					//xCell.add(startP);
					//xCell.add(rate);
					//xCell.add(upDown);
					//xCell.add(endP);
					xCell.add(endT);    	
					xCell.add("<a id='subBtn' href='javascript:void(0);'><span class='fa fa-save'></span></a>&nbsp;&nbsp;&nbsp;&nbsp;<a id='delBtn' href='javascript:void(0);'><span class='glyphicon glyphicon-trash'></span></a>");

					/*if(jz.endsWith("all"))
					{
						xCell.add("");
					}
					else
					{
						xCell.add("<a id='subBtn' href='javascript:void(0);'><span class='fa fa-save'></span></a>&nbsp;&nbsp;&nbsp;&nbsp;<a id='delBtn' href='javascript:void(0);'><span class='glyphicon glyphicon-trash'></span></a>");
					}*/
	    	    }
	    	    else
	    	    {
	    	    	int snNum = Integer.valueOf(planData.get("PLAN_SN").toString());
					String mName = (String )planData.get("M_NAME").toString();
					String YJH_FDL = (String )planData.get("YJH_FDL").toString();
					String YJH_SSDL = (String )planData.get("YJH_SSDL").toString();
					String PLAN_FDL = (String )planData.get("PLAN_FDL").toString();
					String PLAN_SSDL = (String )planData.get("PLAN_SSDL").toString();
					String REAL_FDL = (String )planData.get("REAL_FDL").toString();
					String REAL_SSDL = (String )planData.get("REAL_SSDL").toString();
					xCell.add(snNum);
					xCell.add(mName);
					xCell.add(YJH_FDL);
					xCell.add(YJH_SSDL);
					xCell.add(PLAN_FDL);
					xCell.add(PLAN_SSDL);
					xCell.add(REAL_FDL);
					xCell.add(REAL_SSDL);
	    	    }
				arr[i] = xCell;
	    	}
	    }
		Map<String, Object> map1 = new HashMap<String, Object>();
		map1.put("data", arr);
        map1.put("result",1);  
	    jsonString = JSON.toJSONString(map1);
	    //log(jsonString);
	    return jsonString;
  }
  public String getyTfPlanAll11(Integer sn , String jz,int tab) throws ParseException
  {
	  String jsonString="";
	    String sql ="";
	    String selMName="jz_id='"+jz+"' ";
	    if(tab==1)
	    {
		   /*
	    	if(jz.endsWith("all"))
		    {
		    	 sql ="SELECT * from PLAN_POINT where  PLAN_ID="+sn +" order by M_NAME asc ,START_TIME asc";
		    }
		    else
		    {
		    	
		    	if(jz.endsWith("p_1"))
		    	{
		    		selMName="M_NAME='Q1-1' ";
		    	}
		    	else if(jz.endsWith("p_2"))
		    	{
		    		selMName="(M_NAME='Q2-1' or M_NAME='Q2-2' or  M_NAME='Q2-3' or  M_NAME='Q2-4')";
		    	}
		    	else if(jz.endsWith("p_3"))
		    	{
		    		selMName="(M_NAME='Q3-1' or M_NAME='Q3-2' )";
		    	}
		    	else if(jz.endsWith("p_4"))
		    	{
		    		selMName="(M_NAME='F1-1' or  M_NAME='F1-2') ";
		    	}
		    	
		    	 sql ="SELECT * from PLAN where "+selMName+" and PLAN_SN="+sn+" order by M_NAME asc ,START_TIME asc";
		    }*/
		    //sql ="SELECT * FROM PLAN_GD WHERE POINT_ID IN (SELECT id from PLAN_POINT where  PLAN_ID="+sn +" and jz_id=get_jzid2('"+jz+"')) ORDER BY POINT_ID,S_DATETIME";
            sql ="select a.id aid,a.point_id p_id,c.jz_name jz,b.evt_id evtid,b.info evtinfo,a.s_datetime sdate,a.e_datetime edate,a.s_power sp,a.e_power ep,a.rate ra ,a.mark mk,a.p_day pday,p_datetime pdate from plan_gd a,plan_point b,web_jz c where b.jz_id=c.sn and  a.point_id=b.id and b.PLAN_ID="+sn +" and jz_id=get_jzid2('"+jz+"') order by b.evt_id,a.s_datetime";
		    //log("sql:"+sql);
           //log(jz);
	    }
	    else
	    {
		    if(jz.endsWith("all"))
		    {
		    	 sql ="SELECT * from DL_TJ where  PLAN_SN="+sn+" order by M_NAME asc";
		    }
		    else
		    {
		    	if(jz.endsWith("p_1"))
		    	{
		    		selMName="M_NAME='Q1-1' ";
		    	}
		    	else if(jz.endsWith("p_2"))
		    	{
		    		selMName="(M_NAME='Q2-1' or M_NAME='Q2-2' or  M_NAME='Q2-3' or  M_NAME='Q2-4')";
		    	}
		    	else if(jz.endsWith("p_3"))
		    	{
		    		selMName="(M_NAME='Q3-1' or M_NAME='Q3-2' )";
		    	}
		    	else if(jz.endsWith("p_4"))
		    	{
		    		selMName="(M_NAME='F1-1' or  M_NAME='F1-2' )";
		    	}
		    	 sql ="SELECT * from DL_TJ where "+selMName+" and PLAN_SN="+sn;
		    }
	    }
	    
	    List userData = getJdbcTemplate().queryForList(sql);
	    int size=userData.size() ;
	    String infoArr[ ]=new String[size];
		Object arr[] = new Object[size];
		String info="";
		String pd="";
	    if (size> 0)
	    {
	    	for (int i = 0; i<size; i++)
	    	{
				ArrayList xCell = new ArrayList();
	    		Map planData = (Map)userData.get(i);
	    	    if(tab==1)
	    	    {
	    	    	int id = Integer.valueOf(planData.get("aid").toString()); 
					int pid = Integer.valueOf(planData.get("p_id").toString());
					String jznm = (planData.get("jz").toString());
					int evtid = Integer.valueOf(planData.get("evtid").toString());
					int mk = Integer.valueOf(planData.get("mk").toString());
					float pday=0;
					try
					{
					 pday = Float.valueOf(planData.get("pday").toString());
					 pd="停留"+pday+"小时";
					}catch(Exception e) 
		    		{
		    			pday=0;	
		    			pd="满功率运行";
		    		}
		    		String startT=planData.get("SDATE").toString().substring(0,planData.get("SDATE").toString().length()-5);
		    		String endT=planData.get("EDATE").toString().substring(0,planData.get("EDATE").toString().length()-5);
		    		
		    		String pingT="";
		    		try
		    		{
		    			pingT=planData.get("PDATE").toString().substring(0,planData.get("PDATE").toString().length()-5);
		    		}catch(Exception e) 
		    		{
		    			pingT="";	
		    		}
		    		try
		    		{
		    		    info=planData.get("evtinfo").toString();
		    		}catch(Exception e) 
		    		{
		    			info="";	
		    		}
		    		 
		    		String startP = (planData.get("SP").toString());
					double rate =Double.parseDouble(planData.get("RA").toString());
					String upDown ="降至";
					if (rate>0) upDown = "升至";
					String endP =(planData.get("EP").toString());
					xCell.add(id);
					xCell.add(pid);
					
					xCell.add(jznm);
					xCell.add(info+evt[evtid]);
					
					// string.Format(%.2f ,pday%24)
					//if (pday>24) 
					//	{pd=""+(int)pday/24+"天"+String.format("%.2f" ,pday%24)+"小时";}
					//else 
						
						
					if (mk==-1)
					  {xCell.add(startT+"开始  从"+startP+"(MW) "+upDown+" "+endP+"(MW),在该平台"+pd);}
					else if (mk==-99)
			    	{
					xCell.add(startT+"解列 停机"); 
				    }
					else if (mk==-98)
			    	{
					xCell.add(startT+"大修工期跨年"); 
				    }
					else if (mk==1)
				    	{
						xCell.add(startT+"并网"+" 从"+startP+"(MW) "+upDown+"  "+endP+"(MW),在该平台"+pd); 
					    }
					else if (mk==2)
			    	{
					xCell.add(startT+"开始"+" 从"+startP+"(MW) "+upDown+"  满功率"+endP+"(MW)运行"); 
				    }
					else
						xCell.add(startT+"开始  从"+startP+"(MW) "+upDown+" "+endP+"(MW), 在该平台"+pd);
					xCell.add("<a id='delBtn' href='javascript:void(0);'><span class='glyphicon glyphicon-trash'></span></a>");

					//xCell.add(startP);
					//xCell.add(rate);
					//xCell.add(upDown);
					//xCell.add(endP);
					//xCell.add(endT);    	
					//xCell.add("<a id='subBtn' href='javascript:void(0);'><span class='fa fa-save'></span></a>&nbsp;&nbsp;&nbsp;&nbsp;<a id='delBtn' href='javascript:void(0);'><span class='glyphicon glyphicon-trash'></span></a>");

					/*if(jz.endsWith("all"))
					{
						xCell.add("");
					}
					else
					{
						xCell.add("<a id='subBtn' href='javascript:void(0);'><span class='fa fa-save'></span></a>&nbsp;&nbsp;&nbsp;&nbsp;<a id='delBtn' href='javascript:void(0);'><span class='glyphicon glyphicon-trash'></span></a>");
					}*/
	    	    }
	    	    else
	    	    {
	    	    	int snNum = Integer.valueOf(planData.get("PLAN_SN").toString());
					String mName = (String )planData.get("M_NAME").toString();
					String YJH_FDL = (String )planData.get("YJH_FDL").toString();
					String YJH_SSDL = (String )planData.get("YJH_SSDL").toString();
					String PLAN_FDL = (String )planData.get("PLAN_FDL").toString();
					String PLAN_SSDL = (String )planData.get("PLAN_SSDL").toString();
					String REAL_FDL = (String )planData.get("REAL_FDL").toString();
					String REAL_SSDL = (String )planData.get("REAL_SSDL").toString();
					xCell.add(snNum);
					xCell.add(mName);
					xCell.add(YJH_FDL);
					xCell.add(YJH_SSDL);
					xCell.add(PLAN_FDL);
					xCell.add(PLAN_SSDL);
					xCell.add(REAL_FDL);
					xCell.add(REAL_SSDL);
	    	    }
				arr[i] = xCell;
	    	}
	    }
	    sql="select count(*) jz_cnt,jz_id from plan_gd a,plan_point b,web_jz c where b.jz_id=c.sn and  a.point_id=b.id and b.PLAN_ID="+sn +" and jz_id=get_jzid2('"+jz+"') group by jz_id order by jz_id";
	    //log(sql);
	    userData = getJdbcTemplate().queryForList(sql);
	     size=userData.size() ;
	    int jz_cnt[ ]=new int[size];
	    for (int i = 0; i<size; i++)
    	{
			
    		Map planData = (Map)userData.get(i);
    		int cnt = Integer.valueOf(planData.get("jz_cnt").toString()); 
    		jz_cnt[i]=cnt;
    	}
	    sql="select count(*) evt_cnt,b.evt_id,a.point_id  from plan_gd a,plan_point b,web_jz c where b.jz_id=c.sn and  a.point_id=b.id and b.PLAN_ID="+sn +" and jz_id=get_jzid2('"+jz+"') group by b.evt_id,a.point_id order by b.evt_id,a.point_id";
	    //log(sql);
	    userData = getJdbcTemplate().queryForList(sql);
	     size=userData.size() ;
	    int evt_cnt[ ]=new int[size];
	    for (int i = 0; i<size; i++)
    	{
			
    		Map planData = (Map)userData.get(i);
    		int cnt = Integer.valueOf(planData.get("evt_cnt").toString()); 
    		evt_cnt[i]=cnt;
    	}
	    Map<String, Object> map1 = new HashMap<String, Object>();
		map1.put("data", arr);
        map1.put("result",1);
        //map1.put("cnts",(jz_cnt[0]));
        map1.put("jz_cnt",jz_cnt);
        map1.put("evt_cnt",evt_cnt);
	    jsonString = JSON.toJSONString(map1);
	    //log(jsonString);
	    return jsonString;
  }
  
  public String getyPlanAll(Integer sn , String jz,int tab) throws ParseException
  {
	  String jsonString="";
	    String sql ="";
	    String selMName="jz_id='"+jz+"' ";
	    if(tab==1)
	    {
		    
		    	
		    	 sql ="SELECT * from PLAN_POINT where "+selMName+" and PLAN_id="+sn+" order by EVT_ID asc ,S_DATETIME asc";
		 // log(sql);
		    	 // }
	    }
	    else
	    {
		    if(jz.endsWith("all"))
		    {
		    	 sql ="SELECT * from DL_TJ where  PLAN_SN="+sn+" order by M_NAME asc";
		    }
		    else
		    {
		    	if(jz.endsWith("p_1"))
		    	{
		    		selMName="M_NAME='Q1-1' ";
		    	}
		    	else if(jz.endsWith("p_2"))
		    	{
		    		selMName="(M_NAME='Q2-1' or M_NAME='Q2-2' or  M_NAME='Q2-3' or  M_NAME='Q2-4')";
		    	}
		    	else if(jz.endsWith("p_3"))
		    	{
		    		selMName="(M_NAME='Q3-1' or M_NAME='Q3-2' )";
		    	}
		    	else if(jz.endsWith("p_4"))
		    	{
		    		selMName="(M_NAME='F1-1' or  M_NAME='F1-2' )";
		    	}
		    	 sql ="SELECT * from DL_TJ where "+selMName+" and PLAN_SN="+sn;
		    }
	    }
	    
	    List userData = getJdbcTemplate().queryForList(sql);
	    int size=userData.size() ;
	    String infoArr[ ]=new String[size];
		Object arr[] = new Object[size];

	    if (size> 0)
	    {
	    	for (int i = 0; i<size; i++)
	    	{
				ArrayList xCell = new ArrayList();
	    		Map planData = (Map)userData.get(i);
	    	    if(tab==1)
	    	    {
	    	    	int id = Integer.valueOf(planData.get("id").toString()); 
					//int snNum = Integer.valueOf(planData.get("plan_id").toString());
					int jzid = Integer.valueOf(planData.get("jz_id").toString());
					int evtid = Integer.valueOf(planData.get("evt_id").toString());
		    		String startT=planData.get("S_DATETIME").toString().substring(0,planData.get("START_TIME").toString().length()-5);
		    		String endT=planData.get("E_DATETIME").toString().substring(0,planData.get("END_TIME").toString().length()-5);
					double startP = Double.parseDouble(planData.get("S_POWER").toString());
					double rate =Double.parseDouble(planData.get("RATE").toString());
					String upDown =planData.get("UP_DOWN").toString().replace("0", "降至").replace("1", "升至");
					double endP =Double.parseDouble(planData.get("E_POWER").toString());
					xCell.add(id);
					//xCell.add(snNum);
					xCell.add(jzid);
					xCell.add(evtid);
					xCell.add(startT);
					//xCell.add(startP);
					//xCell.add(rate);
					//xCell.add(upDown);
					//xCell.add(endP);
					xCell.add(endT);   	
					
					if(jz.endsWith("all"))
					{
						xCell.add("");
					}
					else
					{
						xCell.add("<a id='subBtn' href='javascript:void(0);'><span class='fa fa-save'></span></a>&nbsp;&nbsp;&nbsp;&nbsp;<a id='delBtn' href='javascript:void(0);'><span class='glyphicon glyphicon-trash'></span></a>");
					}
	    	    }
	    	    else
	    	    {
	    	    	int snNum = Integer.valueOf(planData.get("PLAN_SN").toString());
					String mName = (String )planData.get("M_NAME").toString();
					String YJH_FDL = (String )planData.get("YJH_FDL").toString();
					String YJH_SSDL = (String )planData.get("YJH_SSDL").toString();
					String PLAN_FDL = (String )planData.get("PLAN_FDL").toString();
					String PLAN_SSDL = (String )planData.get("PLAN_SSDL").toString();
					String REAL_FDL = (String )planData.get("REAL_FDL").toString();
					String REAL_SSDL = (String )planData.get("REAL_SSDL").toString();
					xCell.add(snNum);
					xCell.add(mName);
					xCell.add(YJH_FDL);
					xCell.add(YJH_SSDL);
					xCell.add(PLAN_FDL);
					xCell.add(PLAN_SSDL);
					xCell.add(REAL_FDL);
					xCell.add(REAL_SSDL);
	    	    }
				arr[i] = xCell;
	    	}
	    }
		Map<String, Object> map1 = new HashMap<String, Object>();
		map1.put("data", arr);
        map1.put("result",1);  
	    jsonString = JSON.toJSONString(map1);
	    return jsonString;
  }
  public String getplandl(Integer sn , String jz,int tab) throws ParseException
  {
	  String jsonString="";
	    String sql ="";
	    String sql2 ="";
	    String p_1="1";
	    String p_2="2,3,4,5";
	    String p_3="6,7";
	    String p_4="8,9";
	    String[] dlty ={"发电量","上网电量","损失电量"};
	    DecimalFormat df = new DecimalFormat("#.000");
	    int sizes=3 ;
	    if(jz.endsWith("all") || jz.endsWith("p_1")|| jz.endsWith("p_2")|| jz.endsWith("p_3")|| jz.endsWith("p_4"))
	    
	    {
	    	if(jz.endsWith("all")) 
	    	{
	    		sizes = 30;
	    		sql=p_1+","+p_2+","+p_3+","+p_4;
	    	}
	    	if(jz.endsWith("p_1")) 
	    		{
	    		sizes = 3;
	    		sql=p_1;
	    		}
	    	if(jz.endsWith("p_2")) 
	    		{
	    		sizes = 15;
	    		sql=p_2;
	    		}
	    	if(jz.endsWith("p_3")) 
	    	{
	    		sizes = 9;
	    		sql=p_3;
	    		}
	    	if(jz.endsWith("p_4")) 
	    	{
	    		sizes = 9;
	    		sql=p_4;
	    		}
	    	    sql2="select nvl(sum(fdl),0) fd,nvl(sum(swdl),0) sw,nvl(sum(lost),0) ls,to_char(savetime,'mm') mm from plan_points_all a,web_jz b where plan_sn="+sn+" and jz_sn in ("+sql+") and a.jz_sn=b.sn group by to_char(savetime,'mm') order by to_char(savetime,'mm')";
            
		         sql="select nvl(sum(fdl),0) fd,nvl(sum(swdl),0) sw,nvl(sum(lost),0) ls,to_char(savetime,'mm') mm,jz_name,sn from plan_points_all a,web_jz b where plan_sn="+sn+" and jz_sn in ("+sql+") and a.jz_sn=b.sn group by sn,jz_name,to_char(savetime,'mm') order by sn,jz_name,to_char(savetime,'mm')";
		         
	    }
	    else
	    {
	    	//sql="select sum(fdl) fd,sum(swdl) sw,sum(lost) ls,to_char(savetime,'mm') mm ,jz_sn from plan_points_all where plan_sn="+sn+" and jz_sn=get_jzid2('"+jz+"') group by jz_sn,to_char(savetime,'mm') order by jz_sn,to_char(savetime,'mm')";
	    	 sql="select nvl(sum(fdl),0) fd,nvl(sum(swdl),0) sw,nvl(sum(lost),0) ls,to_char(savetime,'mm') mm,jz_name,sn from plan_points_all a,web_jz b where plan_sn="+sn+" and jz_sn =get_jzid2('"+jz+"') and a.jz_sn=b.sn group by sn,jz_name,to_char(savetime,'mm') order by sn,jz_name,to_char(savetime,'mm')";
	    	 
	    }
	    //log(jz+"---"+sql);
	    //log(jz+"---"+sql2);
	    List userData = getJdbcTemplate().queryForList(sql);
	    int size = userData.size();
	    String infoArr[ ]=new String[size];
		Object arr[] = new Object[sizes];
		double tot_fd =0;
		double tot_sw =0;
		double tot_ls =0;
		int jzs = 0;
		int jzso = -1;
		double [][] data = new double[sizes][14];
		
	    if (size> 0)
	    {
	    	for (int i = 0; i<size; i++)
	    	{
	    		
	    		Map planData = (Map)userData.get(i);
	    	    if(tab==1)
	    	    {
	    	    	jzso = ((int)(i/12))*3;
	    	    	jzs = Integer.parseInt(planData.get("sn").toString());
	    	    	if (i%12==0)
	    	    	{
	    	    		tot_fd = 0;
	    	    		tot_sw = 0;
	    	    		tot_ls = 0;
	    	    		data[jzso][0] = jzs;
		    	    	data[jzso+1][0] = jzs;
		    	    	data[jzso+2][0] = jzs;
	    	    	}
	    	    	String jz_nm = planData.get("jz_name").toString();
	    	    	int mm = Integer.parseInt(planData.get("mm").toString());
	    	    	
	    	    	
	    	    	double fdl =0; //Double.parseDouble(planData.get("fd").toString())/100000;
	    	    	
	    	    	double swdl =0; //Double.parseDouble(planData.get("sw").toString())/100000;
	    	    	double ls = 0;//Double.parseDouble(planData.get("ls").toString())/100000;
	    	    	try
	    	    	{
	    	    		 fdl = Double.parseDouble(planData.get("fd").toString())/100000;
		    	    	
		    	    	 swdl = Double.parseDouble(planData.get("sw").toString())/100000;
		    	    	 ls = Double.parseDouble(planData.get("ls").toString())/100000;

	    	    	}
	    	    catch(Exception e) 
	    		{
                    fdl =0; //Double.parseDouble(planData.get("fd").toString())/100000;
	    	    	
	    	    	 swdl =0; //Double.parseDouble(planData.get("sw").toString())/100000;
	    	    	 ls = 0;//Double.parseDouble(planData.get("ls").toString())/100000;
	
	    		}
	    	    	tot_fd = tot_fd+fdl;
	    	    	tot_sw = tot_sw+swdl;
	    	    	tot_ls = tot_ls+ls;
	    	    	data[jzso][mm] = fdl;
	    	    	data[jzso+1][mm] = swdl;
	    	    	data[jzso+2][mm] = ls;
	    	    	if (mm==12)
	    	    	{
	    	    		
	    	    		data[jzso][13] = tot_fd;
		    	    	data[jzso+1][13] = tot_sw;
		    	    	data[jzso+2][13] = tot_ls;
	    	    	}
	    	    	
	    	    	/*
	    	    	if (i==0)
					{
						xCell.add((jz_nm));
						xCell2.add((jz_nm));
						xCell3.add((jz_nm));
						xCell.add("发电量");
						xCell2.add("上网电量");
						xCell3.add("损失电量");
					}
					*/
					//xCell.add(df.format(fdl));
					//xCell2.add(df.format(swdl));
					//xCell3.add(df.format(ls));
					//log(data);
	    	    }
	    	    else
	    	    {
	    	    	int snNum = Integer.valueOf(planData.get("PLAN_SN").toString());
					String mName = (String )planData.get("M_NAME").toString();
					String YJH_FDL = (String )planData.get("YJH_FDL").toString();
					String YJH_SSDL = (String )planData.get("YJH_SSDL").toString();
					String PLAN_FDL = (String )planData.get("PLAN_FDL").toString();
					String PLAN_SSDL = (String )planData.get("PLAN_SSDL").toString();
					String REAL_FDL = (String )planData.get("REAL_FDL").toString();
					String REAL_SSDL = (String )planData.get("REAL_SSDL").toString();
					/*
					xCell.add(snNum);
					xCell.add(mName);
					xCell.add(YJH_FDL);
					xCell.add(YJH_SSDL);
					xCell.add(PLAN_FDL);
					xCell.add(PLAN_SSDL);
					xCell.add(REAL_FDL);
					xCell.add(REAL_SSDL);
					*/
	    	    }
				//arr[i] = xCell;
	    	}
	    }
	    
	    tot_fd = 0;
		tot_sw = 0;
		tot_ls = 0;
		
	    if(jz.endsWith("all") || jz.endsWith("p_2")|| jz.endsWith("p_3")|| jz.endsWith("p_4"))
	    {
	    	  userData = getJdbcTemplate().queryForList(sql2);
	 	      size = userData.size();
	 	     jzso = jzso+3;
	 	     if (size> 0)
	 	    {
	 	    	  
	 	    	for (int i = 0; i<size; i++)
	 	    	{
	 	    		
	 	    		Map planData = (Map)userData.get(i);
	 	    	    
	 	    	    {
	 	    	    	
	 	    	    	int mm = Integer.parseInt(planData.get("mm").toString());
	 	    	    	
	 	    	    	
	 	    	    	double fdl =0; //Double.parseDouble(planData.get("fd").toString())/100000;
		    	    	
		    	    	double swdl =0; //Double.parseDouble(planData.get("sw").toString())/100000;
		    	    	double ls = 0;//Double.parseDouble(planData.get("ls").toString())/100000;
		    	    	try
		    	    	{
		    	    		 fdl = Double.parseDouble(planData.get("fd").toString())/100000;
			    	    	
			    	    	 swdl = Double.parseDouble(planData.get("sw").toString())/100000;
			    	    	 ls = Double.parseDouble(planData.get("ls").toString())/100000;

		    	    	}
		    	    catch(Exception e) 
		    		{
	                    fdl =0; //Double.parseDouble(planData.get("fd").toString())/100000;
		    	    	
		    	    	 swdl =0; //Double.parseDouble(planData.get("sw").toString())/100000;
		    	    	 ls = 0;//Double.parseDouble(planData.get("ls").toString())/100000;
		
		    		}
	 	    	    	tot_fd = tot_fd+fdl;
	 	    	    	tot_sw = tot_sw+swdl;
	 	    	    	tot_ls = tot_ls+ls;
	 	    	    	data[jzso][mm] = fdl;
	 	    	    	data[jzso+1][mm] = swdl;
	 	    	    	data[jzso+2][mm] = ls;
	 	    	    	if (mm==12)
	 	    	    	{
	 	    	    		
	 	    	    		data[jzso][13] = tot_fd;
	 		    	    	data[jzso+1][13] = tot_sw;
	 		    	    	data[jzso+2][13] = tot_ls;
	 	    	    	}
	 	    	    	
	 	    	    	/*
	 	    	    	if (i==0)
	 					{
	 						xCell.add((jz_nm));
	 						xCell2.add((jz_nm));
	 						xCell3.add((jz_nm));
	 						xCell.add("发电量");
	 						xCell2.add("上网电量");
	 						xCell3.add("损失电量");
	 					}
	 					*/
	 					//xCell.add(df.format(fdl));
	 					//xCell2.add(df.format(swdl));
	 					//xCell3.add(df.format(ls));
	 					
	 	    	    }
	 	    	    
	 				//arr[i] = xCell;
	 	    	}
	 	    	
	 	    }
	    }
	 	    for (int i = 0; i<sizes; i++)
		    {
		    	
		    	ArrayList xCell = new ArrayList();
		    	
		    	xCell.add(jzname[(int)data[i][0]]);
		    	xCell.add(dlty[(int)(i%3)]);
		    	  for (int j = 1; j<14; j++)
		    	  {
		    		  xCell.add(df.format(data[i][j])); 
		    	  }
		    	  arr[i] = xCell;
		    	
		    }
	    
	   // arr[0] = xCell;
	   // arr[1] = xCell2;
	   // arr[2] = xCell3;
		Map<String, Object> map1 = new HashMap<String, Object>();
		map1.put("data", arr);
		//log(arr);
        map1.put("result",1);  
	    jsonString = JSON.toJSONString(map1);
	    return jsonString;
  }
  public String getJzName(String crew)
  {
	  String jzName="";
	    if(crew.endsWith("all"))
	    {
	    	jzName="秦山核电";
	    }
	    else if (crew.endsWith("p_1"))
		{
	    	jzName="秦一厂";
		}
	    else if (crew.endsWith("p_2"))
		{
	    	jzName="秦二厂";
		}
	    else if (crew.endsWith("p_3"))
		{
	    	jzName="秦三厂";
		}
	    else if (crew.endsWith("p_4"))
		{
	    	jzName="方家山";
		}
	    else if (crew.endsWith("Q1-1"))
		{
	    	jzName="秦山一厂一号机组";
		}
	    else if (crew.endsWith("Q2-1"))
		{
	    	jzName="秦山二厂一号机组";
		}
	    else if (crew.endsWith("Q2-2"))
		{
	    	jzName="秦山二厂二号机组";
		}
	    else if (crew.endsWith("Q2-3"))
		{
	    	jzName="秦山二厂三号机组";
		}	    
	    else if (crew.endsWith("Q2-4"))
		{
	    	jzName="秦山二厂四号机组";
		}
	    else if (crew.endsWith("Q3-1"))
		{
	    	jzName="秦山三厂一号机组";
		}
	    else if (crew.endsWith("Q3-2"))
		{
	    	jzName="秦山三厂二号机组";
		}
	    else if (crew.endsWith("F1-1"))
		{
	    	jzName="方家山长一号机组";
		}	
	    else if (crew.endsWith("F1-2"))
		{
	    	jzName="方家山长二号机组";
		}	
	  return jzName;
  }
  
  
  public String getTfData(String crew,String crewLong, int pId,int userId) throws ParseException
  {
	  	int gno=0;
	  	int vno=0;
        int sav=0;
	  	String dbnameString = "";
	  	String dbnameString2 = "";
	  	String jsonString="";
	  	String jzName="";
	  	String dateString ="";
	  	float vv,tvmx,tvmn;
	  	String mxt,mnt;
	  	tvmx = -9999999;
	  	tvmn = 9999999;
	  	mxt = "";
	  	mnt = "";

      String  sql ="";
        dbnameString= "hyc";
        dbnameString2 = "prtuana";
        List userData = null;
        Map jznm = null;
	  	sav=pId;
	  	if (sav==-1)
        {
             sql = "select min(a.saveno) sav from prtuana a,prtu b  where (power(2,"+userId+"-1)&a.author_read)>0 and  a.rtuno=b.rtuno and b.domain="+crewLong;
            //(sql);
             userData = getJdbcTemplate().queryForList(sql);
            jznm = (Map)userData.get(0);
            sav = Integer.parseInt(jznm.get("sav").toString());
        }
//log("ddd:"+sav);
	  	gno = (int)sav/200;
	  	vno = sav % 200;
	  	dateString = crew.replace('/', '-');
	  	// DateTime dateTime2 = new DateTime(new SimpleDateFormat("yyyy-MM-dd").parse(dateString));
	  	// dateString = ""+(dateTime2.getMonthOfYear()*100+dateTime2.getDayOfMonth());
      DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

      LocalDateTime dateTime2 = LocalDateTime.parse(dateString, formatter);
      dateString = ""+(dateTime2.getMonthValue()*100+dateTime2.getDayOfMonth());
		  //dateTime2 =dateTime2.plusDays(1);
	  	java.util.Map<String,Object> map1 = new HashMap<String,Object>() ;
	  	 sql = "select a.name anm,b.name bnm from prtu a,"+dbnameString2+" b where a.rtuno=b.rtuno and b.saveno="+sav;

		   // log("ddd2:"+sql);
		     userData = getJdbcTemplate().queryForList(sql);
		     jznm = (Map)userData.get(0);
		    jzName=jznm.get("anm").toString()+jznm.get("bnm").toString();
	     sql = "select IFNULL(val"+vno+",0) val,savetime from "+dbnameString+(dateTime2.getYear()%10)+" where  groupno="+gno+" and savetime>="+dateString+"0000 and savetime<"+dateString+"9999 order by savetime"; 
		   // sql = "select IFNULL(val"+vno+",0) val,savetime from "+dbnameString+(dateTime2.getYear()%10)+" where  groupno="+gno+" and round(savetime/10000)="+dateString+" order by savetime"; 
        // if (ttp==0)  sql = "select IFNULL(val"+vno+",0) val,savetime from "+dbnameString+(dateTime2.getYear()%10)+" where  groupno="+gno+" and savetime>="+dateString+"0800 and savetime<"+dateString+"2005 order by savetime"; 

		    userData.clear();
	     //log(userData.size());
	    userData = getJdbcTemplate().queryForList(sql);
	   // log("ddd3:"+sql);
	   // log(userData.size());
	    int size=userData.size() ;
	    String infoArr[ ]=new String[288];
	    java.util.Map<String,Object> map2 = new HashMap<String,Object>();  
	    int maxPoint=  288;
	    maxPoint=maxPoint;
    	
	    Object xArr[ ]=new Object[maxPoint];
		String realS="";
		String realE="";
		   Object yArr[ ]=new Object[maxPoint];
		    String tooltipArr[ ]=new String[maxPoint];
    	int yAddS=0;
    	int yAddE=0;
    	for (int i = 0; i<288; i++)
    	{
    		yArr[i]="";
    		xArr[i]= " ";
    		
    	}
	    if (userData.size() > 0)
	    {
	    	for (int i = 0; i<size; i++)
	    	{
	    		Map userMap = (Map)userData.get(i);
	    		
	    		
	    		BigDecimal bd = new BigDecimal(userMap.get("savetime").toString());  
			       
				String st	=	bd.toPlainString();
				if (st.indexOf(".")>0)
					st = st.substring(0,st.indexOf("."));		
	    		int    idx   = Integer.parseInt(st.substring(st.length()-4));
	    		idx = ((int)(idx/100))*12+(idx%100)/5;
	    		yArr[idx]=userMap.get("VAL").toString();
	    		
	    	    
	    	    vv = Float.parseFloat(userMap.get("VAL").toString());
	    	   
	            String dou_str = st;
	            if (dou_str.length()<8) dou_str= "0"+dou_str;
	            try {
	            	 dou_str = dou_str.substring(4,6)+":"+dou_str.substring(6,8);
				} catch (Exception e) {
					// TODO: handle exception
				}
	    	    if (vv>tvmx) 
	    	    {
	    	    		tvmx = vv;
	    	    		mxt = dou_str;
	    	    }
	    	    if (vv<tvmn) 
	    	    {
	    	    		tvmn = vv;
	    	    		mnt = dou_str;
	    	    }
	    		
	           if (idx%12 == 0)
	           {
	    		xArr[idx]=dou_str;
	           }else
	           {
	        	   xArr[idx]= " ";
	           }
	           
	    	//xArr[i]=dateString;
	    		tooltipArr[idx]=" 时间:"+userMap.get("VAL").toString();
   			 	
	    	}
	    	 map2.put("mx","当日最大值: "+tvmx+" 最大时间: "+mxt);
	    	 map2.put("mn","当日最小值: "+tvmn+" 最小时间: "+mnt);
	    	 map2.put("result",1);  
	    	  map2.put("data",yArr);  
	    	  map2.put("tooltipArr",tooltipArr);  
	    	  map2.put("infoArr",infoArr);  
	    	  map2.put("labels",xArr);  
	    	  map2.put("jzName",jzName);  
	          jsonString = JSON.toJSONString(map2);
				
	          map1 = getTfReal(crew,crewLong,sav);
	          map1.put("planJson", JSON.parse(jsonString)); 
	          //log(xArr[0]);
	    }
  
        jsonString = JSON.toJSONString(map1);
	    return jsonString;
  }
  public String getTfData_gzr(String crew,String crewLong, int pId) throws ParseException
  {
	  	int gno=0;
	  	int vno=0;
	  	String dbnameString = "";
	  	String dbnameString2 = "";
	  	String jsonString="";
	  	String jzName="";
	  	String dateString ="";
	  	float vv,tvmx,tvmn;
	  	String mxt,mnt;
	  	tvmx = -9999999;
	  	tvmn = 9999999;
	  	mxt = "";
	  	mnt = "";
	  	//log(crewLong);
	  	if (Integer.parseInt(crewLong) ==1) 
	  		{
	  		dbnameString= "hyc";
	  		dbnameString2 = "prtuana";
	  		}
	  	else {
	  		dbnameString= "hdn";
	  		dbnameString2 = "prtupul";
		}
	  	gno = (int)pId/200;
	  	vno = pId % 200;
	  	dateString = crew.replace('/', '-');
	  	// DateTime dateTime2 = new DateTime(new SimpleDateFormat("yyyy-MM-dd").parse(dateString));
	  	// dateString = ""+(dateTime2.getMonthOfYear()*100+dateTime2.getDayOfMonth());
      DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

      LocalDateTime dateTime2 = LocalDateTime.parse(dateString, formatter);
      dateString = ""+(dateTime2.getMonthValue()*100+dateTime2.getDayOfMonth());
		  //dateTime2 =dateTime2.plusDays(1);
	  	java.util.Map<String,Object> map1 = new HashMap<String,Object>() ;
	  	String  sql = "select a.name anm,b.name bnm from prtu a,"+dbnameString2+" b where a.rtuno=b.rtuno and b.saveno="+pId; 

		    //log(sql);
		    List userData = getJdbcTemplate().queryForList(sql);
		    Map jznm = (Map)userData.get(0);
		    jzName=jznm.get("anm").toString()+jznm.get("bnm").toString();
	     sql = "select IFNULL(val"+vno+",0) val,savetime from "+dbnameString+(dateTime2.getYear()%10)+" where  groupno="+gno+" and savetime>="+dateString+"0000 and savetime<"+dateString+"9999 order by savetime"; 
		   // sql = "select IFNULL(val"+vno+",0) val,savetime from "+dbnameString+(dateTime2.getYear()%10)+" where  groupno="+gno+" and round(savetime/10000)="+dateString+" order by savetime"; 
          sql = "select IFNULL(val"+vno+",0) val,savetime from "+dbnameString+(dateTime2.getYear()%10)+" where  groupno="+gno+" and savetime>="+dateString+"0800 and savetime<"+dateString+"2005 order by savetime"; 

		    userData.clear();
	     //log(userData.size());
	    userData = getJdbcTemplate().queryForList(sql);
	    //log(sql);
	    //log(userData.size());
	    int size=userData.size() ;
	    String infoArr[ ]=new String[144];
	    java.util.Map<String,Object> map2 = new HashMap<String,Object>();  
	    int maxPoint=  	144+1;
	    maxPoint=maxPoint;
    	
	    Object xArr[ ]=new Object[maxPoint];
		String realS="";
		String realE="";
		   Object yArr[ ]=new Object[maxPoint];
		    String tooltipArr[ ]=new String[maxPoint];
    	int yAddS=0;
    	int yAddE=0;
    	for (int i = 0; i<145; i++)
    	{
    		yArr[i]="";
    		xArr[i]= " ";
    		
    	}
	    if (userData.size() > 0)
	    {
	    	for (int i = 0; i<size; i++)
	    	{
	    		Map userMap = (Map)userData.get(i);
	    		
	    		
	    		BigDecimal bd = new BigDecimal(userMap.get("savetime").toString());  
			       
				String st	=	bd.toPlainString();
				if (st.indexOf(".")>0)
					st = st.substring(0,st.indexOf("."));		
	    		int    idx   = Integer.parseInt(st.substring(st.length()-4));
	    		idx = ((int)(idx/100))*12+(idx%100)/5-96;
	    		yArr[idx]=userMap.get("VAL").toString();
	    		
	    	    
	    	    vv = Float.parseFloat(userMap.get("VAL").toString());
	    	   
	            String dou_str = st;
	            if (dou_str.length()<8) dou_str= "0"+dou_str;
	            try {
	            	 dou_str = dou_str.substring(4,6)+":"+dou_str.substring(6,8);
				} catch (Exception e) {
					// TODO: handle exception
				}
	    	    if (vv>tvmx) 
	    	    {
	    	    		tvmx = vv;
	    	    		mxt = dou_str;
	    	    }
	    	    if (vv<tvmn) 
	    	    {
	    	    		tvmn = vv;
	    	    		mnt = dou_str;
	    	    }
	    		
	           if (idx%12 == 0)
	           {
	    		xArr[idx]=dou_str;
	           }else
	           {
	        	   xArr[idx]= " ";
	           }
	           
	    	//xArr[i]=dateString;
	    		tooltipArr[idx]=" 时间:"+userMap.get("VAL").toString();
   			 	
	    	}
	    	 map2.put("mx","当日最大值: "+tvmx+" 最大时间: "+mxt);
	    	 map2.put("mn","当日最小值: "+tvmn+" 最小时间: "+mnt);
	    	 map2.put("result",1);  
	    	  map2.put("data",yArr);  
	    	  map2.put("tooltipArr",tooltipArr);  
	    	  map2.put("infoArr",infoArr);  
	    	  map2.put("labels",xArr);  
	    	  map2.put("jzName",jzName);  
	          jsonString = JSON.toJSONString(map2);
				
	          map1 = getTfReal_gzr(crew,crewLong,pId);
	          map1.put("planJson", JSON.parse(jsonString)); 
	          //log(xArr[0]);
	    }
  
        jsonString = JSON.toJSONString(map1);
	    return jsonString;
  }
  
  public String getdlData(String crew,String crewLong, int pId) throws ParseException
  {
	  	int gno=0;
	  	int vno=0;
	  	String dbnameString = "";
	  	String dbnameString2 = "";
	  	String jsonString="";
	  	String jzName="";
	  	String dateString ="";
	  	float vv,tvmx,tvmn;
	  	String mxt,mnt;
	  	tvmx = 0;
	  	tvmn = 9999999;
	  	mxt = "";
	  	mnt = "";
	  	//log(crewLong);
	  	if (Integer.parseInt(crewLong) ==1) 
	  		{
	  		dbnameString= "hyc";
	  		dbnameString2 = "prtuana";
	  		}
	  	else {
	  		dbnameString= "hdn";
	  		dbnameString2 = "prtupul";
		}
	  	gno = (int)pId/200;
	  	vno = pId % 200;
	  	dateString = crew.replace('/', '-');
	  	// DateTime dateTime2 = new DateTime(new SimpleDateFormat("yyyy-MM-dd").parse(dateString));
	  	// dateString = ""+(dateTime2.getMonthOfYear()*100+dateTime2.getDayOfMonth());
      DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

      LocalDateTime dateTime2 = LocalDateTime.parse(dateString, formatter);
      dateString = ""+(dateTime2.getMonthValue()*100+dateTime2.getDayOfMonth());
		  //dateTime2 =dateTime2.plusDays(1);
	  	java.util.Map<String,Object> map1 = new HashMap<String,Object>() ;
	  	String  sql = "select a.name anm,b.name bnm from prtu a,"+dbnameString2+" b where a.rtuno=b.rtuno and b.saveno="+pId; 

		    //log(sql);
		    List userData = getJdbcTemplate().queryForList(sql);
		    Map jznm = (Map)userData.get(0);
		    jzName=jznm.get("anm").toString()+jznm.get("bnm").toString();
	     //sql = "select IFNULL(val"+vno+",0) val,savetime from "+dbnameString+(dateTime2.getYear()%10)+" where  groupno="+gno+" and savetime>="+dateString+"0000 and savetime<"+dateString+"9999 order by savetime"; 
		    sql = "select a.val"+vno+"-b.val"+vno+" val ,b.savetime savetime from "+dbnameString+(dateTime2.getYear()%10)+" a,"+dbnameString+(dateTime2.getYear()%10)+" b where a.groupno=b.groupno and a.groupno="+gno+" and TIMESTAMPDIFF(MINUTE,STR_TO_DATE(concat('',"+dateTime2.getYear()+"*100000000+b.savetime"+"),'%Y%m%d%H%i'),STR_TO_DATE(concat('',"+dateTime2.getYear()+"*100000000+a.savetime)"+",'%Y%m%d%H%i'))=60 and b.savetime>="+dateString+"0000 and b.savetime<"+dateString+"9999 and mod(a.savetime,100)=0"; 

		    userData.clear();
	     
	    userData = getJdbcTemplate().queryForList(sql);
	    //log(sql);
	    //log(userData.size());
	    int size=24 ;
	    String infoArr[ ]=new String[24];
	    float dlval[ ]=new float[24];
	    java.util.Map<String,Object> map2 = new HashMap<String,Object>();  
	    int maxPoint=  24;
	    maxPoint=maxPoint;
    	
	    Object xArr[ ]=new Object[maxPoint];
		String realS="";
		String realE="";
		   Object yArr[ ]=new Object[maxPoint];
		    String tooltipArr[ ]=new String[maxPoint];
    	int yAddS=0;
    	int yAddE=0;
    	for (int i = 0; i<24; i++)
    	{
    		yArr[i]="";
    		xArr[i]= " ";
    		dlval[i]=0;
    		
    	}
	    if (userData.size() > 0)
	    {
	    	for (int i = 0; i<userData.size(); i++)
	    	{
	    		Map userMap = (Map)userData.get(i);
	    		
	    		
	    		BigDecimal bd = new BigDecimal(userMap.get("savetime").toString());  
			       
				String st	=	bd.toPlainString();
				if (st.indexOf(".")>0)
					st = st.substring(0,st.indexOf("."));		
	    		int    idx   = Integer.parseInt(st.substring(st.length()-4));
	    		idx = ((int)(idx/100));
	    		yArr[idx]=userMap.get("VAL").toString();
	    		
	    	    
	    	    vv = Float.parseFloat(userMap.get("VAL").toString());
	    	    tvmx = tvmx+vv;
	            String dou_str = st;
	            if (dou_str.length()<8) dou_str= "0"+dou_str;
	            try {
	            	 dou_str = dou_str.substring(4,6)+":"+dou_str.substring(6,8);
				} catch (Exception e) {
					// TODO: handle exception
				}
	    	    if (vv>tvmx) 
	    	    {
	    	    		tvmx = vv;
	    	    		mxt = dou_str;
	    	    }
	    	    if (vv<tvmn) 
	    	    {
	    	    		tvmn = vv;
	    	    		mnt = dou_str;
	    	    }
	    		
	           if (idx%12 == 0)
	           {
	    		xArr[idx]=dou_str;
	           }else
	           {
	        	   xArr[idx]= " ";
	           }
	           
	    	//xArr[i]=dateString;
	    		tooltipArr[idx]=" 时间:"+userMap.get("VAL").toString();
   			 	
	    	}
	    	 map2.put("mx","当日电量: "+tvmx+"(kWh)");
	    	 //map2.put("mn","当日最小值: "+tvmn+" 最小时间: "+mnt);
	    	 map2.put("result",1);  
	    	  map2.put("data",yArr);  
	    	  map2.put("tooltipArr",tooltipArr);  
	    	  map2.put("infoArr",infoArr);  
	    	  map2.put("labels",xArr);  
	    	  map2.put("jzName",jzName);  
	          jsonString = JSON.toJSONString(map2);
				
	          map1 = getdlinc(crew,crewLong,pId);
	          map1.put("planJson", JSON.parse(jsonString));
	          map1.put("result",1);
	          //log(xArr[0]);
	    }
  
        jsonString = JSON.toJSONString(map1);
	    return jsonString;
  }
  
  
  public String getTfData_xb(String crew,String crewLong, String pId) throws ParseException
  {
	  	int gno=0;
	  	int vno=0;
	  	int[] monthDay = {31,28,31,30,31,30,31,31,30,31,30,31};
	  	
	  	String timestring ="";
	  	String dbnameString = "";
	  	String dbnameString2 = "";
	  	String jsonString="";
	  	String jzName="";
	  	String dateString ="";
	  	float vv,tvmx,tvmn,pvmx,pvmn,qvmx,qvmn;
	  	String mxt,mnt,mxt2,mnt2,mxt3,mnt3;
	  	tvmx = -9999999;
	  	tvmn = 9999999;
	  	pvmx = -9999999;
	  	pvmn = 9999999;
	  	qvmx = -9999999;
	  	qvmn = 9999999;
	  	mxt = "";
	  	mnt = "";
		mxt2 = "";
	  	mnt2 = "";
		mxt3 = "";
	  	mnt3 = "";
	  	dbnameString= "hyc";
  		dbnameString2 = "prtuana";
  		int savno = 0;
  		int year = 0 ;
  		int mode = 0;
	  	//
	  	
	   try
	   {
		   mode = Integer.parseInt(crewLong);
	   }
	   catch (Exception e) 
	   {
		   mode = 1;
	   }
	   //log("mode:"+mode);
	  	dateString = crew.replace('/', '-');
	  	// DateTime dateTime2 = new DateTime(new SimpleDateFormat("yyyy-MM-dd").parse(dateString));
	  	// dateString = ""+(dateTime2.getMonthOfYear()*100+dateTime2.getDayOfMonth());
      DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

      LocalDateTime dateTime2 = LocalDateTime.parse(dateString, formatter);
      dateString = ""+(dateTime2.getMonthValue()*100+dateTime2.getDayOfMonth());
	  	 year = dateTime2.getYear(); 
	  	if((year%4==0 && year%100 != 0)|| year%400==0 ) 
		  monthDay[1]++;
	  	 if (mode==0)
	  	 {
	  		 timestring = " and savetime>="+dateString+"0000 and savetime<"+dateString+"9999 ";
	  	 }
	  	 else
	  	 {
	  		dateString = ""+(dateTime2.getMonthValue()*100+1);
	  		timestring = " and savetime>="+dateString+"0000 and savetime<"+(dateTime2.getMonthValue()*100+monthDay[dateTime2.getMonthValue()-1])+"9999 ";
	  	 }
		  //dateTime2 =dateTime2.plusDays(1);
	  	java.util.Map<String,Object> map1 = new HashMap<String,Object>() ;
	  	String  sql = "select a.name anm,get_subs(b.name,1) bnm,b.saveno sav from prtu a,"+dbnameString2+" b where a.rtuno=b.rtuno and b.type=7  and b.name like '%"+pId+"%电流%' order by saveno"; 

		   
		    List userData = getJdbcTemplate().queryForList(sql);
		    int size=userData.size() ;
		    String savArr[ ]=new String[size];
		    if (userData.size() > 0)
		    {
		    	for (int i = 0; i<size; i++)
		    	{
		    		Map userMap = (Map)userData.get(i);
		    		
		    		savArr[i] = userMap.get("sav").toString();
		    		jzName=userMap.get("anm").toString()+userMap.get("bnm").toString();
		    	}
		    }
		    //A相
		 savno = Integer.parseInt(savArr[0]);   
			gno = (int)savno/200;
		  	vno = savno % 200;   
	     sql = "select IFNULL(val"+vno+",0) val,savetime from "+dbnameString+(dateTime2.getYear()%10)+" where  groupno="+gno+timestring+" order by savetime"; 
		
		 userData.clear();
	    
	    userData = getJdbcTemplate().queryForList(sql);
	   //("A:"+sql);
	   
	    size=userData.size() ;
	    String infoArr[ ]=new String[size];
	    java.util.Map<String,Object> map2 = new HashMap<String,Object>();  
	    int maxPoint=  size;
	  
    	//log("ss:"+size);
	    Object xArr[ ]=new Object[maxPoint];
		   Object yArr[ ]=new Object[maxPoint];
		   Object yArr2[ ]=new Object[maxPoint];
		   Object yArr3[ ]=new Object[maxPoint];
		   Object yArr4[ ]=new Object[maxPoint];
		    String tooltipArr[ ]=new String[maxPoint];
	    if (userData.size() > 0)
	    {
	    	for (int i = 0; i<size; i++)
	    	{
	    		Map userMap = (Map)userData.get(i);
	    		yArr[i]=userMap.get("VAL").toString();
	    		
	    		yArr4[i]=5;
	    	    //dateString2= userMap.get("savetime").toString();
	    	    vv = Float.parseFloat(userMap.get("VAL").toString());
	    	    Double dou_obj = new Double(userMap.get("savetime").toString());
	            NumberFormat nf = NumberFormat.getInstance();
	            nf.setGroupingUsed(false);
	            String dou_str = nf.format(dou_obj);
	            if (dou_str.length()<8) dou_str= "0"+dou_str;
	            
	            try {
	            	 if (mode==0)
	        	  	 {
	            	 dou_str = dou_str.substring(4,6)+":"+dou_str.substring(6,8);
	        	  	 }
	            	 else
	        	  	 {
	        	  	 dou_str =dou_str.substring(0,2)+"月"+dou_str.substring(2,4)+"日  "+dou_str.substring(4,6)+":"+dou_str.substring(6,8);	 
	        	  	 }
				} catch (Exception e) {
					// TODO: handle exception
				}
	    	    if (vv>tvmx) 
	    	    {
	    	    		tvmx = vv;
	    	    		mxt = dou_str;
	    	    }
	    	    if (vv<tvmn) 
	    	    {
	    	    		tvmn = vv;
	    	    		mnt = dou_str;
	    	    }
	    	    if (mode==0)
       	  	 {	
	           if (i%12 == 0)
	           {
	    		xArr[i]=dou_str;
	           }else
	           {
	        	   xArr[i]= " ";
	           }
       	    }else
            {
       	    	xArr[i]=dou_str;
            }
	    		tooltipArr[i]=" A相:"+userMap.get("VAL").toString();
   			 	
	    	}
	    	  
	         
	          
	          //log(xArr[0]);
	    }
	    
	    
	    //B相
			 savno = Integer.parseInt(savArr[1]);   
				gno = (int)savno/200;
			  	vno = savno % 200;   
		     sql = "select IFNULL(val"+vno+",0) val,savetime from "+dbnameString+(dateTime2.getYear()%10)+" where  groupno="+gno+timestring+"  order by savetime"; 
		     //log("B:"+sql);
			   
			 userData.clear();
		    
		    userData = getJdbcTemplate().queryForList(sql);
		    
		   
		    size=userData.size() ;
		   
		    if (userData.size() > 0)
		    {
		    	for (int i = 0; i<size; i++)
		    	{
		    		Map userMap = (Map)userData.get(i);
		    		yArr2[i]=userMap.get("VAL").toString();

		    	   
		    	    vv = Float.parseFloat(userMap.get("VAL").toString());
		    	    Double dou_obj = new Double(userMap.get("savetime").toString());
		            NumberFormat nf = NumberFormat.getInstance();
		            nf.setGroupingUsed(false);
		            String dou_str = nf.format(dou_obj);
		            if (dou_str.length()<8) dou_str= "0"+dou_str;
		            try {
		            	 dou_str = dou_str.substring(4,6)+":"+dou_str.substring(6,8);
					} catch (Exception e) {
						// TODO: handle exception
					}
		    	    if (vv>pvmx) 
		    	    {
		    	    		pvmx = vv;
		    	    		mxt2 = dou_str;
		    	    }
		    	    if (vv<pvmn) 
		    	    {
		    	    		pvmn = vv;
		    	    		mnt2 = dou_str;
		    	    }
		    		
		          
		    		tooltipArr[i]=tooltipArr[i]+" B相:"+userMap.get("VAL").toString();
	   			 	
		    	}
		    }
	    
		    //C相
			 savno = Integer.parseInt(savArr[2]);   
				gno = (int)savno/200;
			  	vno = savno % 200;   
		     sql = "select IFNULL(val"+vno+",0) val,savetime from "+dbnameString+(dateTime2.getYear()%10)+" where  groupno="+gno+timestring+"  order by savetime";  
		     //log("C:"+sql);
			 userData.clear();
		    
		    userData = getJdbcTemplate().queryForList(sql);
		   
		   
		    size=userData.size() ;
		   
		    if (userData.size() > 0)
		    {
		    	for (int i = 0; i<size; i++)
		    	{
		    		Map userMap = (Map)userData.get(i);
		    		yArr3[i]=userMap.get("VAL").toString();

		    	   
		    	    vv = Float.parseFloat(userMap.get("VAL").toString());
		    	    Double dou_obj = new Double(userMap.get("savetime").toString());
		            NumberFormat nf = NumberFormat.getInstance();
		            nf.setGroupingUsed(false);
		            String dou_str = nf.format(dou_obj);
		            if (dou_str.length()<8) dou_str= "0"+dou_str;
		            try {
		            	 dou_str = dou_str.substring(4,6)+":"+dou_str.substring(6,8);
					} catch (Exception e) {
						// TODO: handle exception
					}
		    	    if (vv>qvmx) 
		    	    {
		    	    		qvmx = vv;
		    	    		mxt3= dou_str;
		    	    }
		    	    if (vv<qvmn) 
		    	    {
		    	    		qvmn = vv;
		    	    		mnt3 = dou_str;
		    	    }
		    		
		          
		    		tooltipArr[i]=tooltipArr[i]+" C相:"+userMap.get("VAL").toString();
	   			 	
		    	}
		    }
	    
		    
	    map1.put("mx","A相最大值: "+tvmx+" 最大时间: "+mxt+" "+"B相最大值: "+pvmx+" 最大时间: "+mxt2+" "+"C相最大值: "+qvmx+" 最大时间: "+mxt3);
	    map1.put("mn","A相最小值: "+tvmn+" 最小时间: "+mnt+" "+"B相最小值: "+pvmn+" 最小时间: "+mnt2+" "+"C相最小值: "+qvmn+" 最小时间: "+mnt3);

  	  
  	
  	 
  	    map1.put("infoArr",infoArr);  
  	    map1.put("labels",xArr);  
  	    map1.put("jzName",jzName);  
       
			
        map1.put("data",yArr); 
        map1.put("data2",yArr2); 
        map1.put("data3",yArr3);
        map1.put("data4",yArr4);
        map1.put("tooltipArr",tooltipArr);  
        map1.put("result",1);  
        jsonString = JSON.toJSONString(map1);
	    return jsonString;
  }
  public String getTfData_dl(String crew,String crewLong, String pId) throws ParseException
  {
	  	int gno=0;
	  	int vno=0;
	  	int[] monthDay = {31,28,31,30,31,30,31,31,30,31,30,31};
	  	
	  	String timestring ="";
	  	String dbnameString = "";
	  	String dbnameString2 = "";
	  	String jsonString="";
	  	String jzName="";
	  	String dateString ="";
	  	float vv,tvmx,tvmn,pvmx,pvmn,qvmx,qvmn;
	  	String mxt,mnt,mxt2,mnt2,mxt3,mnt3;
	  	tvmx = -9999999;
	  	tvmn = 9999999;
	  	pvmx = -9999999;
	  	pvmn = 9999999;
	  	qvmx = -9999999;
	  	qvmn = 9999999;
	  	mxt = "";
	  	mnt = "";
		mxt2 = "";
	  	mnt2 = "";
		mxt3 = "";
	  	mnt3 = "";
	  	dbnameString= "dl_max";
		dbnameString2 = "prtuana";
		int savno = 0;
		int year = 0 ;
		int mode = 0;
	  	float up=0;
	  	
	   try
	   {
		   mode = 1;
	   }
	   catch (Exception e) 
	   {
		   mode = 1;
	   }
	   //log("mode:"+mode);
	  	dateString = crew.replace('/', '-');
	  	// DateTime dateTime2 = new DateTime(new SimpleDateFormat("yyyy-MM-dd").parse(dateString));
	  	// dateString = ""+(dateTime2.getMonthOfYear()*100+dateTime2.getDayOfMonth());
      DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

      LocalDateTime dateTime2 = LocalDateTime.parse(dateString, formatter);
      dateString = ""+(dateTime2.getMonthValue()*100+dateTime2.getDayOfMonth());
	  	 year = dateTime2.getYear(); 
	  	if((year%4==0 && year%100 != 0)|| year%400==0 ) 
		  monthDay[1]++;
	  	 if (mode==0)
	  	 {
	  		 timestring = " and savetime>="+dateString+"0000 and savetime<"+dateString+"9999 ";
	  	 }
	  	 else
	  	 {
	  		dateString = ""+(dateTime2.getMonthValue()*100+1);
	  		timestring = " and did>="+year+"0000 and did<"+year+"9999 ";
	  	 }
		  //dateTime2 =dateTime2.plusDays(1);
	  	java.util.Map<String,Object> map1 = new HashMap<String,Object>() ;
	  	String  sql = "select a.name anm,get_subs(b.name,1) bnm,b.saveno sav,b.upperlimit up from prtu a,"+dbnameString2+" b where a.rtuno=b.rtuno and b.type=2 and b.name like '%"+pId+"%' order by saveno"; 
        //log(sql);
		   
		    List userData = getJdbcTemplate().queryForList(sql);
		    int size=userData.size() ;
		    String savArr[ ]=new String[size];
		    if (userData.size() > 0)
		    {
		    	for (int i = 0; i<size; i++)
		    	{
		    		Map userMap = (Map)userData.get(i);
		    		
		    		savArr[i] = userMap.get("sav").toString();
		    		jzName=userMap.get("anm").toString()+userMap.get("bnm").toString();
		    		up =Float.parseFloat(userMap.get("up").toString());
		    	}
		    }
		    size=12 ;
		    String infoArr[ ]=new String[size];
		    java.util.Map<String,Object> map2 = new HashMap<String,Object>();  
		    int maxPoint=  size;
		  
	  	//log("ss:"+size);
		    Object xArr[ ]=new Object[maxPoint];
			   Object yArr[ ]=new Object[maxPoint];
			   Object yArr2[ ]=new Object[maxPoint];
			   Object yArr3[ ]=new Object[maxPoint];
			   Object yArr4[ ]=new Object[maxPoint];
			    String tooltipArr[ ]=new String[maxPoint];
			    for (int i=0 ;i<12;i++)
			    {
			    	yArr4[i]=up;
			    	yArr[i]=0;
			    	yArr2[i]=0;
			    	yArr3[i]=0;
			    	xArr[i]=""+(i+1)+"月";
			    }
		    //A相
		 savno = Integer.parseInt(savArr[0]);   
			gno = (int)savno/200;
		  	vno = savno % 200;   
	     sql = "select IFNULL(val,0) val,did,valtime from dl_max where  saveno="+savArr[0]+timestring +"  order by did"; 
	     //log(sql);
		 userData.clear();
	    
	    userData = getJdbcTemplate().queryForList(sql);
	   // 
	   
	  
	    if (userData.size() > 0)
	    {
	    	for (int i = 0; i<userData.size(); i++)
	    	{
	    		Map userMap = (Map)userData.get(i);
	    		 Double dou_obj = new Double(userMap.get("did").toString());
		            NumberFormat nf = NumberFormat.getInstance();
		            nf.setGroupingUsed(false);
		            String dou_str = nf.format(dou_obj);
		          gno=Integer.parseInt(dou_str.substring(4,6))-1;
	    		yArr[gno]=userMap.get("VAL").toString();
	    		
	    		
	    	    //dateString2= userMap.get("savetime").toString();
	    	    vv = Float.parseFloat(userMap.get("VAL").toString());
	    	    
	            
	            //xArr[gno]=dou_str;
	    	    tooltipArr[gno]=" A相:"+userMap.get("VAL").toString();
          	}
	    	  
	         
	          
	          //log(xArr[0]);
	    }
	    
	    
	    //B相
			 savno = Integer.parseInt(savArr[1]);   
				gno = (int)savno/200;
			  	vno = savno % 200;   
			  	 sql = "select IFNULL(val,0) val,did,valtime from dl_max where  saveno="+savArr[1]+timestring +"  order by did"; 
		     //log(sql);
			   
			 userData.clear();
		    
		    userData = getJdbcTemplate().queryForList(sql);
		    
		   
		    size=userData.size() ;
		   
		    if (userData.size() > 0)
		    {
		    	for (int i = 0; i<size; i++)
		    	{
		    		Map userMap = (Map)userData.get(i);
		    	
		    		 Double dou_obj = new Double(userMap.get("did").toString());
			            NumberFormat nf = NumberFormat.getInstance();
			            nf.setGroupingUsed(false);
			            String dou_str = nf.format(dou_obj);
			          gno=Integer.parseInt(dou_str.substring(4,6))-1;
		    		yArr2[gno]=userMap.get("VAL").toString();
		    		
		    		
		    		
		    		
		    		
		    	   
		    	    vv = Float.parseFloat(userMap.get("VAL").toString());
		    	 
		           
		           
		            
		           
		    		tooltipArr[gno]=tooltipArr[gno]+" B相:"+userMap.get("VAL").toString();
	   			 	
		    	}
		    }
	    
		    //C相
			 savno = Integer.parseInt(savArr[2]);   
				gno = (int)savno/200;
			  	vno = savno % 200;   
			  	 sql = "select IFNULL(val,0) val,did,valtime from dl_max where  saveno="+savArr[2]+timestring +"  order by did"; 
			
			 userData.clear();
		    
		    userData = getJdbcTemplate().queryForList(sql);
		   
		   
		    size=userData.size() ;
		   
		    if (userData.size() > 0)
		    {
		    	for (int i = 0; i<size; i++)
		    	{
		    		Map userMap = (Map)userData.get(i);

		    		 Double dou_obj = new Double(userMap.get("did").toString());
			            NumberFormat nf = NumberFormat.getInstance();
			            nf.setGroupingUsed(false);
			            String dou_str = nf.format(dou_obj);
			          gno=Integer.parseInt(dou_str.substring(4,6))-1;
		    		yArr3[gno]=userMap.get("VAL").toString();
		    		
		          
		    		tooltipArr[gno]=tooltipArr[gno]+" C相:"+userMap.get("VAL").toString();
	   			 	
		    	}
		    }
	    
		    
	    map1.put("mx","A相最大值: "+tvmx+" 最大时间: "+mxt+" "+"B相最大值: "+pvmx+" 最大时间: "+mxt2+" "+"C相最大值: "+qvmx+" 最大时间: "+mxt3);
	    map1.put("mn","A相最小值: "+tvmn+" 最小时间: "+mnt+" "+"B相最小值: "+pvmn+" 最小时间: "+mnt2+" "+"C相最小值: "+qvmn+" 最小时间: "+mnt3);

	  
	
	 
	    map1.put("infoArr",infoArr);  
	    map1.put("labels",xArr);  
	    map1.put("jzName",jzName);  
     
			
      map1.put("data",yArr); 
      map1.put("data2",yArr2); 
      map1.put("data3",yArr3);
      map1.put("data4",yArr4);
      map1.put("tooltipArr",tooltipArr);  
      map1.put("result",1);  
      jsonString = JSON.toJSONString(map1);
	    return jsonString;
}

  public String[] jsAddPoint(Date sT, Date eT)
  {
	  int addTime=2;
	  String dateArr[ ]=new String[2];
	  Calendar c = Calendar.getInstance();
	  c.setTime(sT);
	  c.set(Calendar.HOUR_OF_DAY, c.get(Calendar.HOUR_OF_DAY) - addTime);
	  Calendar c2 = Calendar.getInstance();
	  c2.setTime(eT);
	  c2.set(Calendar.HOUR_OF_DAY, c2.get(Calendar.HOUR_OF_DAY)+ addTime);
	  SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	  SimpleDateFormat edf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	  dateArr[0]=sdf.format(c.getTime());
	  dateArr[1]=edf.format(c2.getTime());
	  return  dateArr;
  }
  
  public Map getTfReal(String crew, String crewLong ,int pId)throws ParseException
  {
	  Map map1 = new HashMap<String,Object>();
	 
	  int msiz = 0;
	  String jsonString="";
		int gno=0;
	  	int vno=0;
	  	String dbnameString = "";
	  	String dbnameString2 = "";
	  
	  	String jzName="";
	  	String dateString ="";
	  	//log(crewLong);
	  	if (Integer.parseInt(crewLong) ==1) 
	  		{
	  		dbnameString= "hyc";
	  		dbnameString2 = "prtuana";
	  		}
	  	else {
	  		dbnameString= "hdn";
	  		dbnameString2 = "prtupul";
		}
	  	gno = (int)pId/200;
	  	vno = pId % 200;
	  	dateString = crew.replace('/', '-');
	  	// DateTime dateTime2 = new DateTime(new SimpleDateFormat("yyyy-MM-dd").parse(dateString));
	  	//dateString = ""+(dateTime2.getMonthOfYear()*100+dateTime2.getDayOfMonth());
      DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

      LocalDateTime dateTime2 = LocalDateTime.parse(dateString, formatter);
      dateString = ""+(dateTime2.getMonthValue()*100+dateTime2.getDayOfMonth());
	  	String   sql = "select IFNULL(val"+vno+",0) val,savetime from "+dbnameString+(dateTime2.getYear()%10)+" where   groupno="+gno+" and savetime>="+dateString+"0000 and savetime<"+dateString+"9999 order by savetime"; 
	  	List userData = getJdbcTemplate().queryForList(sql);
	  	int size=userData.size() ; 
	  	 msiz=288+1;
		    String xArr[ ]=new String[msiz];
		    Object yArr[ ]=new Object[msiz];
		    String tooltipArr[ ]=new String[msiz];
	    	for (int t = 0; t<msiz; t++)
	    	{
	    		xArr[t ]="";
	    		tooltipArr[t]="";
	    	}
	    	 if (userData.size() > 0)
	 	    {
	 	    	for (int i = 0; i<size; i++)
	 	    	{
	 	    		Map userMap = (Map)userData.get(i);
	 	    		BigDecimal bd = new BigDecimal(userMap.get("savetime").toString());  
				       
					String st	=	bd.toPlainString();
					if (st.indexOf(".")>0)
						st = st.substring(0,st.indexOf("."));		
		    		int    idx   = Integer.parseInt(st.substring(st.length()-4));
		    		idx = ((int)(idx/100))*12+(idx%100)/5;
	 	    		tooltipArr[idx]=" 当日:"+userMap.get("VAL").toString();
	    			 	
	 	    	}
	 	    }
	  	 
		dateTime2 =dateTime2.plusDays(-1);
	  	dateString = ""+(dateTime2.getMonthValue()*100+dateTime2.getDayOfMonth());
	    sql = "select IFNULL(val"+vno+",0) val,savetime from "+dbnameString+(dateTime2.getYear()%10)+" where   groupno="+gno+" and savetime>="+dateString+"0000 and savetime<"+dateString+"9999 order by savetime";  
	    
	   // String sql = "select TAGNAME,SAVETIME,VAL,time4,pointinfo ,to_char(SAVETIME,'mm-dd') fd from REAL_GL left join (SELECT to_char(TB2.TIME1,'yyyymmdd hh24') TIME4, LTRIM(MAX(SYS_CONNECT_BY_PATH(TB2.TEXT1, '<br>')), '<br>') pointInfo FROM (SELECT TB1.TEXT1, TB1.TIME1,ROW_NUMBER() OVER(PARTITION BY to_char(TB1.TIME1,'yyyymmdd hh24') ORDER BY TB1.TEXT1 asc) RN FROM (SELECT POLY_POINT.M_NAME,POLY_POINT.TIME1,to_char(POLY_POINT.TIME1,'yyyy-mm-dd hh24:mi')||' '||POLY_POINT.M_NAME || ','||POLY_CASE.TEXT1 ||',' ||POLY_POINT.TEXT1 TEXT1 FROM POLY_POINT left join POLY_CASE on (POLY_POINT.CASE_SN = POLY_CASE.SN ) WHERE POLY_POINT.M_NAME='"+crew+"' ORDER BY POLY_POINT.TIME1 ASC) tb1) tb2 START WITH RN = 1 CONNECT BY RN - 1 = PRIOR RN　　　 AND to_char(TB2.TIME1,'yyyymmdd hh24') = PRIOR to_char(TB2.TIME1,'yyyymmdd hh24')GROUP BY to_char(TB2.TIME1,'yyyymmdd hh24') ORDER BY to_char(TB2.TIME1,'yyyymmdd hh24') asc) TTT on (to_char(REAL_GL.SAVETIME,'yyyymmdd hh24') =TTT.TIME4) where TAGNAME='"+crew+"' and  (to_char(SAVETIME,'yyyy-mm-dd hh24:mi:ss') BETWEEN '"+startT+"' AND '"+endT+"') and (to_char(SAVETIME,'miss') = '0000' or to_char(SAVETIME,'miss') = '3000')  ORDER BY SAVETIME asc";
	  //String sql = "select val0 val,savetime from hyc7 where round(savetime/10000)=1020 and groupno=0 order by savetime"; 
	   userData.clear();
	   userData = getJdbcTemplate().queryForList(sql);
	    size=userData.size() ;
	  msiz=288+1;
    	int yAddS=0;
    	int yAddE=0;
    	// String xArr[ ]=new String[msiz];
    	//Object yArr[ ]=new Object[msiz];
	  //  String tooltipArr[ ]=new String[msiz];
    	//for (int t = 0; t<msiz; t++)
    	//{
    	//	xArr[t ]="";
    	//	tooltipArr[t]="";
    	//}
	    if (userData.size() > 0)
	    {
	    	for (int i = 0; i<size; i++)
	    	{
	    		Map userMap = (Map)userData.get(i);
	    		BigDecimal bd = new BigDecimal(userMap.get("savetime").toString());  
			       
				String st	=	bd.toPlainString();
				if (st.indexOf(".")>0)
					st = st.substring(0,st.indexOf("."));		
	    		int    idx   = Integer.parseInt(st.substring(st.length()-4));
	    		idx = ((int)(idx/100))*12+(idx%100)/5;
	    		 yArr[idx]=userMap.get("VAL").toString();
	    		 tooltipArr[idx]=tooltipArr[idx]+" 昨日:"+userMap.get("VAL").toString();
   			 	
	    	}
	    }

        map1.put("data",yArr);  
        map1.put("tooltipArr",tooltipArr);  
        map1.put("result",1);  
        map1.put("infoArr","");  
	  return map1;
  }
  public Map getTfReal_gzr(String crew, String crewLong ,int pId)throws ParseException
  {
	  Map map1 = new HashMap<String,Object>();
	 
	  int msiz = 0;
	  String jsonString="";
		int gno=0;
	  	int vno=0;
	  	String dbnameString = "";
	  	String dbnameString2 = "";
	  
	  	String jzName="";
	  	String dateString ="";
	  	//log(crewLong);
	  	if (Integer.parseInt(crewLong) ==1) 
	  		{
	  		dbnameString= "hyc";
	  		dbnameString2 = "prtuana";
	  		}
	  	else {
	  		dbnameString= "hdn";
	  		dbnameString2 = "prtupul";
		}
	  	gno = (int)pId/200;
	  	vno = pId % 200;
	  	dateString = crew.replace('/', '-');
	  	// DateTime dateTime2 = new DateTime(new SimpleDateFormat("yyyy-MM-dd").parse(dateString));
	  	//dateString = ""+(dateTime2.getMonthOfYear()*100+dateTime2.getDayOfMonth());
      DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

      LocalDateTime dateTime2 = LocalDateTime.parse(dateString, formatter);
      dateString = ""+(dateTime2.getMonthValue()*100+dateTime2.getDayOfMonth());
	  	
	  	String   sql = "select IFNULL(val"+vno+",0) val,savetime from "+dbnameString+(dateTime2.getYear()%10)+" where   groupno="+gno+" and savetime>="+dateString+"0800 and savetime<"+dateString+"2005 order by savetime"; 
	  	List userData = getJdbcTemplate().queryForList(sql);
	  	int size=userData.size() ; 
	  	 msiz=144+1;
		    String xArr[ ]=new String[msiz];
		    Object yArr[ ]=new Object[msiz];
		    String tooltipArr[ ]=new String[msiz];
	    	for (int t = 0; t<msiz; t++)
	    	{
	    		xArr[t ]="";
	    		tooltipArr[t]="";
	    	}
	    	 if (userData.size() > 0)
	 	    {
	 	    	for (int i = 0; i<size; i++)
	 	    	{
	 	    		Map userMap = (Map)userData.get(i);
	 	    		BigDecimal bd = new BigDecimal(userMap.get("savetime").toString());  
				       
					String st	=	bd.toPlainString();
					if (st.indexOf(".")>0)
						st = st.substring(0,st.indexOf("."));		
		    		int    idx   = Integer.parseInt(st.substring(st.length()-4));
		    		idx = ((int)(idx/100))*12+(idx%100)/5-96;
	 	    		tooltipArr[idx]=" 当日:"+userMap.get("VAL").toString();
	    			 	
	 	    	}
	 	    }
	  	 
		dateTime2 =dateTime2.plusDays(-1);
	  	dateString = ""+(dateTime2.getMonthValue()*100+dateTime2.getDayOfMonth());
	    sql = "select IFNULL(val"+vno+",0) val,savetime from "+dbnameString+(dateTime2.getYear()%10)+" where   groupno="+gno+" and savetime>="+dateString+"0800 and savetime<"+dateString+"2005 order by savetime";  
	    
	   // String sql = "select TAGNAME,SAVETIME,VAL,time4,pointinfo ,to_char(SAVETIME,'mm-dd') fd from REAL_GL left join (SELECT to_char(TB2.TIME1,'yyyymmdd hh24') TIME4, LTRIM(MAX(SYS_CONNECT_BY_PATH(TB2.TEXT1, '<br>')), '<br>') pointInfo FROM (SELECT TB1.TEXT1, TB1.TIME1,ROW_NUMBER() OVER(PARTITION BY to_char(TB1.TIME1,'yyyymmdd hh24') ORDER BY TB1.TEXT1 asc) RN FROM (SELECT POLY_POINT.M_NAME,POLY_POINT.TIME1,to_char(POLY_POINT.TIME1,'yyyy-mm-dd hh24:mi')||' '||POLY_POINT.M_NAME || ','||POLY_CASE.TEXT1 ||',' ||POLY_POINT.TEXT1 TEXT1 FROM POLY_POINT left join POLY_CASE on (POLY_POINT.CASE_SN = POLY_CASE.SN ) WHERE POLY_POINT.M_NAME='"+crew+"' ORDER BY POLY_POINT.TIME1 ASC) tb1) tb2 START WITH RN = 1 CONNECT BY RN - 1 = PRIOR RN　　　 AND to_char(TB2.TIME1,'yyyymmdd hh24') = PRIOR to_char(TB2.TIME1,'yyyymmdd hh24')GROUP BY to_char(TB2.TIME1,'yyyymmdd hh24') ORDER BY to_char(TB2.TIME1,'yyyymmdd hh24') asc) TTT on (to_char(REAL_GL.SAVETIME,'yyyymmdd hh24') =TTT.TIME4) where TAGNAME='"+crew+"' and  (to_char(SAVETIME,'yyyy-mm-dd hh24:mi:ss') BETWEEN '"+startT+"' AND '"+endT+"') and (to_char(SAVETIME,'miss') = '0000' or to_char(SAVETIME,'miss') = '3000')  ORDER BY SAVETIME asc";
	  //String sql = "select val0 val,savetime from hyc7 where round(savetime/10000)=1020 and groupno=0 order by savetime"; 
	   userData.clear();
	   userData = getJdbcTemplate().queryForList(sql);
	    size=userData.size() ;
	  msiz=144+1;
    	int yAddS=0;
    	int yAddE=0;
    	// String xArr[ ]=new String[msiz];
    	//Object yArr[ ]=new Object[msiz];
	  //  String tooltipArr[ ]=new String[msiz];
    	//for (int t = 0; t<msiz; t++)
    	//{
    	//	xArr[t ]="";
    	//	tooltipArr[t]="";
    	//}
	    if (userData.size() > 0)
	    {
	    	for (int i = 0; i<size; i++)
	    	{
	    		Map userMap = (Map)userData.get(i);
	    		BigDecimal bd = new BigDecimal(userMap.get("savetime").toString());  
			       
				String st	=	bd.toPlainString();
				if (st.indexOf(".")>0)
					st = st.substring(0,st.indexOf("."));		
	    		int    idx   = Integer.parseInt(st.substring(st.length()-4));
	    		idx = ((int)(idx/100))*12+(idx%100)/5-96;
	    		 yArr[idx]=userMap.get("VAL").toString();
	    		 tooltipArr[idx]=tooltipArr[idx]+" 昨日:"+userMap.get("VAL").toString();
   			 	
	    	}
	    }

        map1.put("data",yArr);  
        map1.put("tooltipArr",tooltipArr);  
        map1.put("result",1);  
        map1.put("infoArr","");  
	  return map1;
  }
  public Map getdlinc(String crew, String crewLong ,int pId)throws ParseException
  {
	  Map map1 = new HashMap<String,Object>();
	 
	  int msiz = 0;
	  String jsonString="";
		int gno=0;
	  	int vno=0;
	  	String dbnameString = "";
	  	String dbnameString2 = "";
	    float zrdl=0;
	  	String jzName="";
	  	String dateString ="";
	  	//log(crewLong);
	  	if (Integer.parseInt(crewLong) ==1) 
	  		{
	  		dbnameString= "hyc";
	  		dbnameString2 = "prtuana";
	  		}
	  	else {
	  		dbnameString= "dn_inc";
	  		dbnameString2 = "prtupul";
		}
	  	gno = (int)pId/200;
	  	vno = pId % 200;
	  	dateString = crew.replace('/', '-');
	  	
	  	// DateTime dateTime2 = new DateTime(new SimpleDateFormat("yyyy-MM-dd").parse(dateString));
      DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

      LocalDateTime dateTime2 = LocalDateTime.parse(dateString, formatter);
      dateString = ""+(dateTime2.getMonthValue()*100+dateTime2.getDayOfMonth());
	  	SimpleDateFormat xDate=new SimpleDateFormat("yyyyMMdd");
	  	dateTime2 =dateTime2.plusDays(-1);
	  	dateString = ""+(dateTime2.getMonthValue()*100+dateTime2.getDayOfMonth());
	  	
	  	String   sql = "select sum(IFNULL(val"+vno+",0)) val,floor(savetime/100)*100 savetime from "+dbnameString+" where   groupno="+gno+" and savetime>="+dateString+"0000 and savetime<"+dateString+"9999 group by floor(savetime/100) order by floor(savetime/100)"; 
	  	log(sql);
	  	List userData = getJdbcTemplate().queryForList(sql);
	  	int size=userData.size() ; 
	  	 msiz=288+1;
		    String xArr[ ]=new String[msiz];
		    Object yArr[ ]=new Object[msiz];
		    String tooltipArr[ ]=new String[msiz];
	    	for (int t = 0; t<msiz; t++)
	    	{
	    		xArr[t ]="";
	    		tooltipArr[t]="";
	    	}
	    	 if (userData.size() > 0)
	 	    {
	 	    	for (int i = 0; i<size; i++)
	 	    	{
	 	    		Map userMap = (Map)userData.get(i);
	 	    		BigDecimal bd = new BigDecimal(userMap.get("savetime").toString());  
				       
					String st	=	bd.toPlainString();
					if (st.indexOf(".")>0)
						st = st.substring(0,st.indexOf("."));		
		    		int    idx   = Integer.parseInt(st.substring(st.length()-4));
		    		idx = ((int)(idx/100));
	 	    		tooltipArr[idx]=" 当日:"+userMap.get("VAL").toString()+"(kWh)";
	 	    		 yArr[idx]=userMap.get("VAL").toString(); 
	 	    		 zrdl = zrdl+Float.parseFloat(userMap.get("VAL").toString());
	 	    	}
	 	    }
	  	 
		//

        map1.put("data",yArr);  
        map1.put("tooltipArr",tooltipArr);  
        map1.put("mn","昨日电量: "+zrdl+"(kWh)");  
        map1.put("infoArr","");  
	  return map1;
  }
  public Map getTfReal_xb(String crew, String crewLong ,String pId)throws ParseException
  {
	  Map map1 = new HashMap<String,Object>();
	 
	  int msiz = 0;
	  String jsonString="";
		int gno=0;
	  	int vno=0;
	  	String dbnameString = "";
	  	String dbnameString2 = "";
	  
	  	String jzName="";
	  	String dateString ="";
	  	//log(crewLong);
	  	if (Integer.parseInt(crewLong) ==1) 
	  		{
	  		dbnameString= "hyc";
	  		dbnameString2 = "prtuana";
	  		}
	  	else {
	  		dbnameString= "hdn";
	  		dbnameString2 = "prtupul";
		}
	  	gno = 0;
	  	vno = 0;
	  	dateString = crew.replace('/', '-');
	  	// DateTime dateTime2 = new DateTime(new SimpleDateFormat("yyyy-MM-dd").parse(dateString));
	  	//dateString = ""+(dateTime2.getMonthOfYear()*100+dateTime2.getDayOfMonth());
      DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

      LocalDateTime dateTime2 = LocalDateTime.parse(dateString, formatter);
      dateString = ""+(dateTime2.getMonthValue()*100+dateTime2.getDayOfMonth());
	  	String   sql = "select IFNULL(val"+vno+",0) val,savetime from "+dbnameString+(dateTime2.getYear()%10)+" where   groupno="+gno+" and savetime>="+dateString+"0000 and savetime<"+dateString+"9999 order by savetime"; 
	  	List userData = getJdbcTemplate().queryForList(sql);
	  	int size=userData.size() ; 
	  	 msiz=288+1;
		    String xArr[ ]=new String[msiz];
		    Object yArr[ ]=new Object[msiz];
		    String tooltipArr[ ]=new String[msiz];
	    	for (int t = 0; t<msiz; t++)
	    	{
	    		xArr[t ]="";
	    		tooltipArr[t]="";
	    	}
	    	 if (userData.size() > 0)
	 	    {
	 	    	for (int i = 0; i<size; i++)
	 	    	{
	 	    		Map userMap = (Map)userData.get(i);
	 	    		tooltipArr[i]=" 当日:"+userMap.get("VAL").toString();
	    			 	
	 	    	}
	 	    }
	  	 
		dateTime2 =dateTime2.plusDays(-1);
	  	dateString = ""+(dateTime2.getMonthValue()*100+dateTime2.getDayOfMonth());
	    sql = "select IFNULL(val"+vno+",0) val,savetime from "+dbnameString+(dateTime2.getYear()%10)+" where   groupno="+gno+" and savetime>="+dateString+"0000 and savetime<"+dateString+"9999 order by savetime";  
	    
	   // String sql = "select TAGNAME,SAVETIME,VAL,time4,pointinfo ,to_char(SAVETIME,'mm-dd') fd from REAL_GL left join (SELECT to_char(TB2.TIME1,'yyyymmdd hh24') TIME4, LTRIM(MAX(SYS_CONNECT_BY_PATH(TB2.TEXT1, '<br>')), '<br>') pointInfo FROM (SELECT TB1.TEXT1, TB1.TIME1,ROW_NUMBER() OVER(PARTITION BY to_char(TB1.TIME1,'yyyymmdd hh24') ORDER BY TB1.TEXT1 asc) RN FROM (SELECT POLY_POINT.M_NAME,POLY_POINT.TIME1,to_char(POLY_POINT.TIME1,'yyyy-mm-dd hh24:mi')||' '||POLY_POINT.M_NAME || ','||POLY_CASE.TEXT1 ||',' ||POLY_POINT.TEXT1 TEXT1 FROM POLY_POINT left join POLY_CASE on (POLY_POINT.CASE_SN = POLY_CASE.SN ) WHERE POLY_POINT.M_NAME='"+crew+"' ORDER BY POLY_POINT.TIME1 ASC) tb1) tb2 START WITH RN = 1 CONNECT BY RN - 1 = PRIOR RN　　　 AND to_char(TB2.TIME1,'yyyymmdd hh24') = PRIOR to_char(TB2.TIME1,'yyyymmdd hh24')GROUP BY to_char(TB2.TIME1,'yyyymmdd hh24') ORDER BY to_char(TB2.TIME1,'yyyymmdd hh24') asc) TTT on (to_char(REAL_GL.SAVETIME,'yyyymmdd hh24') =TTT.TIME4) where TAGNAME='"+crew+"' and  (to_char(SAVETIME,'yyyy-mm-dd hh24:mi:ss') BETWEEN '"+startT+"' AND '"+endT+"') and (to_char(SAVETIME,'miss') = '0000' or to_char(SAVETIME,'miss') = '3000')  ORDER BY SAVETIME asc";
	  //String sql = "select val0 val,savetime from hyc7 where round(savetime/10000)=1020 and groupno=0 order by savetime"; 
	   userData.clear();
	   userData = getJdbcTemplate().queryForList(sql);
	    size=userData.size() ;
	  msiz=288+1;
    	int yAddS=0;
    	int yAddE=0;
    	// String xArr[ ]=new String[msiz];
    	//Object yArr[ ]=new Object[msiz];
	  //  String tooltipArr[ ]=new String[msiz];
    	//for (int t = 0; t<msiz; t++)
    	//{
    	//	xArr[t ]="";
    	//	tooltipArr[t]="";
    	//}
	    if (userData.size() > 0)
	    {
	    	for (int i = 0; i<size; i++)
	    	{
	    		Map userMap = (Map)userData.get(i);
	    		 yArr[i]=userMap.get("VAL").toString();
	    		 tooltipArr[i]=tooltipArr[i]+" 昨日:"+userMap.get("VAL").toString();
   			 	
	    	}
	    }

        map1.put("data",yArr);  
        map1.put("tooltipArr",tooltipArr);  
        map1.put("result",1);  
        map1.put("infoArr","");  
	  return map1;
  }
  
  public String getUserInfo(String uEmail, String uPwd)
  {
    String jsonString = "";
    String sql = "select * from user where (uEmail='" + uEmail + "' or uName='" + uEmail + "'" + ") and uPwd='" + uPwd + "' and `lock`=0";
    List userData = getJdbcTemplate().queryForList(sql);
    if (userData.size() > 0)
    {
      Map userMap = (Map)userData.get(0);
      userMap.put("result", Integer.valueOf(1));
      jsonString = JSON.toJSONString(userMap);
    }
    else
    {
      jsonString = "{\"result\":-1}";
    }
    return jsonString;
  }
  
  public List getUserList4Xls()
  {
    String jsonString = "";
    String sql = "select * from user";
    List userData = getJdbcTemplate().queryForList(sql);
    return userData;
  }
  
  public boolean findNameMail(String uName, String email)
  {
    return false;
  }
  
  public boolean findName(String uName)
  {
    String sql = "select id from user where uName='" + uName + "'";
    List userData = getJdbcTemplate().queryForList(sql);
    if (userData.size() > 0) {
      return true;
    }
    return false;
  }
  

  public boolean checkuser(String key,String sessid)
  {
	  /*String sql = "select * from vo_info where kkey='" + key + "' and (whoCanSet & power (2,("+sessid+"-1)))>0";
//	  logger.warn(sql);
    List userData = getJdbcTemplate().queryForList(sql);
    if (userData.size() > 0) {
      return true;
    }
    return false;*/
      return true;
  }
    public String sendUpdateCMD(String kk)
    {
        String jsonString="{\"result\":0}";
        jedis = RedisUtil.getJedis();
        try {
            jedis.set(kk,"1");
            jsonString="{\"result\":1}";
        }catch (Exception e) {

            e.printStackTrace();
        }
        return jsonString;
    }
   /**
   * optype:1:赋值，2：取反，3：调整 ,循环,4:调整，不循环
   *
   */
    public String setRedisVal(Integer optype,String key,String okey,String val,String hlimit,String username)
    {
        java.util.Map<String,Object> map1 = new HashMap<String,Object>();
        String jsonString="";
        String oldval = "";
        String nkey = key+"_.value";
        String nstat = key+"_.status";

        String nval = val;
        //logger.warn("username :"+username);

        if (checkuser(key,username)) {


            try {

                jedis = RedisUtil.getJedis();
                //jedis.auth("Shcs123");
                // jedis.connect();//连接


                oldval = jedis.get(okey+"_.value");

                // oldval = jedis.get(ikey);
                // log(optype+"---"+ikey+"----"+val+"---"+oldval);
                oldval = oldval!=null?oldval:"0";
                switch(optype){
                    case 1:
                        nval = val;
                        break;
                    case 2:
                        //log(oldval);
                        nval = oldval.matches("1")?"0":"256";
                        break;
                    case 3:

                        nval = ""+(Float.parseFloat(oldval!=null?oldval:"0")+Float.parseFloat(val));
                        if (Float.parseFloat(nval)>Float.parseFloat(hlimit)) nval="0";
                        break;
                    case 4:

                        nval = ""+(Float.parseFloat(oldval!=null?oldval:"0")+Float.parseFloat(val));
                        break;
                    default:
                        logger.warn("类型码错误");
                        break;
                }
                //logger.warn("set :"+nkey+" value:"+nval+" status:1");
                jedis.set(nkey, nval);
                jedis.setex(nstat,30,"1");
                //jedis.set(nstat, "1");
                RedisUtil.close(jedis);
                jedis=null;
                exec_sql(" insert into loginred (uname,evt,kkey,nval) values ('"+username+"',3,'"+key+"',"+nval+")");
                map1.put("result",1);
                map1.put("nval",nval);

            } catch (Exception e) {
                map1.put("result",0);
                e.printStackTrace();
            }
        } else {
            map1.put("result",-1);
        }
        jsonString = JSON.toJSONString(map1);
        return jsonString;

    }
    /**
     * 获取最近上一次定时触发时间
     */
    public Date getLastTime(String cron) throws ParseException {
        CronTriggerImpl cronTriggerImpl = new CronTriggerImpl();
        cronTriggerImpl.setCronExpression(cron);//这里写要准备猜测的cron表达式
        Calendar calendar = Calendar.getInstance();
        Date now = calendar.getTime();
        calendar.add(Calendar.DATE, -31);
        List<Date> dates = TriggerUtils.computeFireTimesBetween(cronTriggerImpl, null, calendar.getTime(), now);//这个是重点，一行代码搞定~~

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        return dates.get(dates.size()-1);
    }
    /**
     * type:1:加锁，0：解锁
     *
     */
    public String TimeTaskLock(String key,Integer type,String username)
    {
        java.util.Map<String,Object> map1 = new HashMap<String,Object>();
        String jsonString="",val="";

        Calendar calendar = Calendar.getInstance();

        calendar.add(Calendar.DATE, -31);
        Date now = calendar.getTime();
        Date tdate;
        //logger.warn("username :"+username);

        if (checkuser(key,username)) {


            try {


                switch(type){
                    case 0:
                        exec_sql(" update do_info set timeTaskLock=0 where kkey='"+key+"'");
                        break;
                    case 1:
                        exec_sql(" update do_info set timeTaskLock=1 where kkey='"+key+"'");

                        String sql = "select * from timetask_detial a,timetask b  where a.taskid=b.id and b.type=1 and a.kkey like '"+key+"'";
                        List userData = getJdbcTemplate().queryForList(sql);

                        if (userData.size() > 0)
                        {
                            //log(sql);
                            for (int i=0;i<userData.size();i++)
                            {
                                Map userMap = (Map)userData.get(i);
                                tdate = getLastTime(userMap.get("cronstr").toString());
                                //now = now.getTime()>tdate.getTime()?now:tdate;
                                if (now.getTime()<tdate.getTime())
                                {
                                    now = tdate;
                                    val = userMap.get("val").toString();
                                }
                            }
                            jedis = RedisUtil.getJedis();
                            log("恢复定时任务状态：set "+key+"_.value="+val);
                            jedis.set(key+"_.value", val);
                            jedis.setex(key+"_.status",30,"1");
                            RedisUtil.close(jedis);
                            jedis=null;


                        }
                        else
                        {
                            jsonString = "{\"result\":-1}";
                        }
                        break;

                    default:
                        logger.warn("类型码错误");
                        break;
                }


                map1.put("result",1);


            } catch (Exception e) {
                map1.put("result",0);
                e.printStackTrace();
            }
        } else {
            map1.put("result",-1);
        }
        jsonString = JSON.toJSONString(map1);
        return jsonString;

    }
  /**
   * 调用lua脚本
   * @param
   */
  public  void testCallLuaFile(String fname){
      String luaStr = null;
      Jedis sjedis = null;
      Object result = null;
      //带反斜杠，路径为classPath，不带反斜杠，路径为类的同一目录
      
      try {
      	 sjedis = RedisUtil.getJedis();
      	Reader r = new InputStreamReader(ThreadDemo.class.getResourceAsStream(fname));
          luaStr = CharStreams.toString(r);
          if (sjedis!=null) result =  sjedis.eval(luaStr);

			  RedisUtil.close(sjedis);

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
  /*
   * linkaction 关联脚本
   * key 设备id
   *  根据vals是否包含","判断处置方式：是：取反，再根据取反后的值执行对应的lua脚本 、否：设置值，执行脚本
   *  //vals="1:aaa.lua,0:bbb.lua"
   */
  public String linkaction(String key,String vals,String username)
	 {
	    
		 java.util.Map<String,Object> map1 = new HashMap<String,Object>();  
		 String jsonString="";
		 String[] sval=vals.split(",");
		 String[] tval=null;
		 if (checkuser(key,username)) {
		 try {
			 switch (vals.split(",").length) {
				case 1:
					  tval=sval[0].split(":");
					 jsonString = setRedisVal(1,key,key,sval[0],"",username);
					 testCallLuaFile(tval[1]);
					break;
				case 2:
					java.util.Map<String,Object> map2 = new HashMap<String,Object>();  
					for (String vv:sval)
					{
						 tval=vv.split(":");
						 map2.put(tval[0],tval[1]);	 
					}
					 jsonString = setRedisVal(2,key,key,"","",username);
					 JSONObject jsonObj=JSON.parseObject(jsonString);
					 testCallLuaFile(map2.get(jsonObj.get("nval")).toString());
					break;
				default:
					break;
				}
			 map1.put("result", 1);
		} catch (Exception e) {
			 map1.put("result", 0);
			// TODO: handle exception
		}
	    }else {
	    	 map1.put("result", -1);
		}
			
		 
	
		
	  jsonString = JSON.toJSONString(map1);
	  return jsonString;

	 }

  public String getRedisVal(String key,int type)
	 {
		 java.util.Map<String,Object> map1 = new HashMap<String,Object>();  
		 String jsonString="";
		 jedis = RedisUtil.getJedis();
		 String tkey = key+"_.value";
	     String skey = key+"_.ref_time";
	     if (type==2) skey = key+".status";
	      
	    try {
	 
	    	
		    map1.put("val", jedis.get(tkey)); 
		    map1.put("sval", jedis.get(skey));
		  
		    map1.put("result",1); 
		 
		} catch (Exception e) {
			  map1.put("result",0); 
		    e.printStackTrace();
		}
	    RedisUtil.close(jedis);	
	    jedis=null;
	  jsonString = JSON.toJSONString(map1);
	  return jsonString;

	 }
  public String saveUser(String jsonStr,Integer sn) throws SQLException, ParseException
  {
	  String jsonString="{\"result\":0}";
	   JSONObject jsonObj=JSON.parseObject(jsonStr);
	   JSONArray addArr= jsonObj.getJSONArray("addArr");
	   JSONArray delArr= jsonObj.getJSONArray("delArr");
	   JSONArray editArr= jsonObj.getJSONArray("editArr");
	   Connection con = null;   
	   PreparedStatement pstm = null; 	    
	   Statement stm = null;   
	   String sql="";
	   int aa=0;
	   try 
	   {   
		   con = getJdbcTemplate().getDataSource().getConnection();
		   con.setAutoCommit(false);
		   stm = con.createStatement();   
		   for (int i = 0; i <  addArr.size(); i++)
			{
				JSONArray  temp=addArr.getJSONArray(i);
				
				//String text2=temp.getString(2)+",以"+temp.getString(4)+"MW/MIN速率"+upDown+temp.getString(6)+"MW";
				
				sql="Insert into user (uname,pwd,leval,valid) values ('"+temp.getString(1)+"','123456','"+temp.getString(2)+"','"+temp.getString(3)+"')";
				String sql2 = "select id from user where uname='" + temp.getString(1) + "'";
			    List userData = getJdbcTemplate().queryForList(sql2);
			    if (userData.size() == 0) {
			    	stm.addBatch(sql);
			    	
			    }else {
					aa=1;
				}
			    
				 
				
			}
		   for (int d = 0; d <  delArr.size(); d++)
			{
			   sql="delete from SHCS_JOIN where id="+delArr.getInteger(d);
			   stm.addBatch(sql);
			   //log("delete:  " + sql);
			}
		   for (int e = 0; e <  editArr.size(); e++)
			{
				JSONArray  temp=editArr.getJSONArray(e);
				
				//String text2=temp.getString(2)+",以"+temp.getString(4)+"MW/MIN速率"+temp.getString(5)+"MW";
				sql="update user set uname='"+temp.getString(1)+"' , leval="+temp.getString(2)+",valid="+temp.getString(3)+" where ID="+temp.getInteger(0);
				
				stm.addBatch(sql);
			}
		   //log("sql:  " + sql);
		   stm.executeBatch();   
		   con.commit();
		   con.setAutoCommit(true);
    	  Map<String,Object> map1 = new HashMap<String,Object>();  
          if (aa==0)  map1.put("result",1);  
          else {
        	  map1.put("result",2);  
		}
          jsonString = JSON.toJSONString(map1);	  
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
       }finally
       {   
    	   stm.close();
    	   stm = null;
    	    con.close();
    	    con = null;
       }   
	  return jsonString;
  }
  public String loadUser() throws ParseException
  {
	    int gno=0;
	  	int vno=0;
	  	String dbnameString = "";
	  	String dbnameString2 = "";
	  	String jsonString="";
	  	String jzName="";
	  	String dateString ="";
	  	//log(crewLong);
	  
		  //dateTime2 =dateTime2.plusDays(1);
	  	java.util.Map<String,Object> map1 = new HashMap<String,Object>() ;
	  	String  sql = "select * from user where leval>1 and leval<4"; 

		  
	  	 List userData = getJdbcTemplate().queryForList(sql);
		   
		    
		    int size=userData.size() ;

			Object arr[] = new Object[size];

		    if (size> 0)
		    {
		    	
		    	for (int i = 0; i<size; i++)
		    	{
		    		ArrayList xCell = new ArrayList();
		    		Map planData = (Map)userData.get(i);
		    		
		    	
			    	xCell.add(""+planData.get("id").toString());
			    	xCell.add(""+planData.get("uname").toString());
			    	
			    	xCell.add(""+planData.get("leval").toString());
			    	xCell.add(""+planData.get("valid").toString());
			    	
			    	
			    	
			    	xCell.add("<a onclick='savUser();' href='javascript:void(0);'><span class='fa fa-save'></span></a>&nbsp;&nbsp;&nbsp;<a onclick='logred(\""+planData.get("uname").toString()+"\");' href='javascript:void(0);'><span class='fa fa-search'></span></a>");

					arr[i] = xCell;	
		    	     }
		    		}
		   
		map1.put("data", arr);
		
        map1.put("result",1);  
	    jsonString = JSON.toJSONString(map1);
	    //log(jsonString);
	    return jsonString;
  }
  public String chgPwd(String uname,String opwd,String npwd) throws ParseException
  {
	    int gno=0;
	  	int vno=0;
	  	String dbnameString = "";
	  	String dbnameString2 = "";
	  	String jsonString="";
	  	String jzName="";
	  	String dateString ="";
	  	
	  
		  //dateTime2 =dateTime2.plusDays(1);
	  	java.util.Map<String,Object> map1 = new HashMap<String,Object>() ;
	  	String  sql = "select * from user where pwd='"+opwd+"' and uname='"+uname+"'"; 

	  	log(sql);  
	  	 List userData = getJdbcTemplate().queryForList(sql);
		   
		    
		    int size=userData.size() ;

			Object arr[] = new Object[size];

		    if (size> 0)
		    {
		    	 sql = " update user set pwd='"+npwd+"' where uname='"+uname+"'"; 
		    	 //log(sql);
		    	 getJdbcTemplate().update(sql);
		    	 map1.put("npwd", npwd);
		 		
		         map1.put("result",1);
		    }else {
		    	 map1.put("result",0);
			}
		   
		 
	    jsonString = JSON.toJSONString(map1);
	    //log(jsonString);
	    return jsonString;
  }
  public String logred(String uname) throws ParseException
  {
	    int gno=0;
	  	int vno=0;
	  	String dbnameString = "";
	  	String dbnameString2 = "";
	  	String jsonString="";
	  	String jzName="";
	  	String dateString ="";
	  	//log(crewLong);
	  
		  //dateTime2 =dateTime2.plusDays(1);
	  	java.util.Map<String,Object> map1 = new HashMap<String,Object>() ;
	  	String  sql = "select * from loginred "; 
        if (!uname.matches("1")) sql=sql+" where uname ='"+uname+"'";
		  
	  	 List userData = getJdbcTemplate().queryForList(sql);
		   
		    
		    int size=userData.size() ;

			Object arr[] = new Object[size];

		    if (size> 0)
		    {
		    	
		    	for (int i = 0; i<size; i++)
		    	{
		    		ArrayList xCell = new ArrayList();
		    		Map planData = (Map)userData.get(i);
		    		
		    	
			    	xCell.add(""+planData.get("id").toString());
			    	xCell.add(""+planData.get("uname").toString());
			    	
			    	xCell.add(""+planData.get("logtime").toString());
			    	
			    	
			    	
			    	
			    	//xCell.add("<a onclick='savUser();' href='javascript:void(0);'><span class='fa fa-save'></span></a>&nbsp;&nbsp;&nbsp;<a onclick='logred("+planData.get("uname").toString()+");' href='javascript:void(0);'><span class='fa fa-search'></span></a>");

					arr[i] = xCell;	
		    	     }
		    		}
		   
		map1.put("data", arr);
		
        map1.put("result",1);  
	    jsonString = JSON.toJSONString(map1);
	    //log(jsonString);
	    return jsonString;
  }
  public String login(String uname,String pwd,int userId)
	 {
		 java.util.Map<String,Object> map1 = new HashMap<String,Object>();  
		 String jsonString="";
	     String ov = "0";
	     String sql = "select * from msg_user  where name='" + uname+"' and passwd='"+pwd+"' and valid=1";
	     //log(sql);
	     List qaData = getJdbcTemplate().queryForList(sql);
	     if (qaData.size() > 0)
	     {
	    	 Map userMap = (Map)qaData.get(0);
	       //httpSession.setAttribute("username", userMap.get("uname").toString());
	    	 username=userMap.get("name").toString();
	       map1.put("result",userMap.get("leval").toString());
	       //log((String) httpSession.getAttribute("username"));
	       int backR = 0;
	       String aBackSql = "insert into loginred  (uname) values ('"+uname+"')";
	       //log(aBackSql);
	       backR = getJdbcTemplate().update(aBackSql);
	     }else {
	    	 map1.put("result",0);
		}
	    
	  jsonString = JSON.toJSONString(map1);
	  return jsonString;

	 }
    public String resetpasswd(String op,String np,String us)
    {
        java.util.Map<String,Object> map1 = new HashMap<String,Object>();
        String jsonString="";
        String ov = "0";
        String sql = "select * from msg_user  where id=" + us+" and passwd='"+op+"' and valid=1";
        //log(sql);
        List qaData = getJdbcTemplate().queryForList(sql);
        if (qaData.size() > 0)
        {
            Map userMap = (Map)qaData.get(0);



            //log((String) httpSession.getAttribute("username"));
            int backR = 0;
            String aBackSql = "update msg_user set passwd='"+np+"' where id="+us;
           // log(aBackSql);
            backR = getJdbcTemplate().update(aBackSql);
            map1.put("result",1);
        }else {
            map1.put("result",2);
        }

        jsonString = JSON.toJSONString(map1);
        return jsonString;

    }
    public String resetphone(String op,String np,String us)
    {
        java.util.Map<String,Object> map1 = new HashMap<String,Object>();
        String jsonString="";


            int backR = 0;
            String aBackSql = "update msg_user set phone='"+op+"',email='"+np+"' where id="+us;
            //log(aBackSql);
            try {

                backR = getJdbcTemplate().update(aBackSql);
                map1.put("result", 1);
            }catch (Exception e)
            {
                map1.put("result",0);
            }


        jsonString = JSON.toJSONString(map1);
        return jsonString;

    }
  public String getsession(String uname,int userId)
	 {
		
		 String jsonString="";
		
		 //log("22:"+(String) httpSession.getAttribute("username"));  
	  jsonString = "{\"result\":\""+username+"\"}";
	  return jsonString;

	 }

    public String loadDevice(String dm,String gkey)
    {

        String jsonString="";

        //String gkey = (String) httpSession.getAttribute("groupKey");
        java.util.Map<String,Object> map1 = new HashMap<String,Object>() ;
        String  sql = "select * from prtu where un_x='"+gkey+"'";

        System.out.println(sql);
        List userData = getJdbcTemplate().queryForList(sql);


        int size=userData.size() ;

        Object arr[] = new Object[size];

        if (size> 0)
        {

            for (int i = 0; i<size; i++)
            {

                Map planData = (Map)userData.get(i);
                //map1.put("prtu", planData);
            }
        }

        sql = "select a.roomno,a.roomname from room a,prtu b where a.rtuno=b.rtuno and b.un_x='"+gkey+"'";


         userData = getJdbcTemplate().queryForList(sql);


         size=userData.size() ;



        if (size> 0)
        {

            for (int i = 0; i<size; i++)
            {

                Map planData = (Map)userData.get(i);
               // map1.put("room_"+i, planData);


            }
        }

        sql = "select a.deviceno,a.devicenm from dev_author a,room b,prtu c where a.type=0 and  a.roomno=b.roomno and b.rtuno=c.rtuno and c.un_x='"+gkey+"'";


        userData = getJdbcTemplate().queryForList(sql);


        size=userData.size() ;



        if (size> 0)
        {

            for (int i = 0; i<size; i++)
            {

                Map planData = (Map)userData.get(i);
                map1.put("dev_"+i, planData);


            }
        }

        map1.put("result",1);
        jsonString = JSON.toJSONString(map1);


        return jsonString;

    }

    public String loadDevice_dig(String dm,String gkey)
    {

        String jsonString="";

        //String gkey = (String) httpSession.getAttribute("groupKey");
        java.util.Map<String,Object> map1 = new HashMap<String,Object>() ;
        String  sql = "select * from prtu where un_x='"+gkey+"'";

        System.out.println(sql);
        List userData = getJdbcTemplate().queryForList(sql);


        int size=userData.size() ;

        Object arr[] = new Object[size];

        if (size> 0)
        {

            for (int i = 0; i<size; i++)
            {

                Map planData = (Map)userData.get(i);
                //map1.put("prtu", planData);
            }
        }

        sql = "select a.roomno,a.roomname from room a,prtu b where a.rtuno=b.rtuno and b.un_x='"+gkey+"'";


        userData = getJdbcTemplate().queryForList(sql);


        size=userData.size() ;



        if (size> 0)
        {

            for (int i = 0; i<size; i++)
            {

                Map planData = (Map)userData.get(i);
                // map1.put("room_"+i, planData);


            }
        }

        sql = "select a.deviceno,a.devicenm from dev_author a,room b,prtu c where   a.roomno=b.roomno and b.rtuno=c.rtuno and c.un_x='"+gkey+"'";


        userData = getJdbcTemplate().queryForList(sql);


        size=userData.size() ;



        if (size> 0)
        {

            for (int i = 0; i<size; i++)
            {

                Map planData = (Map)userData.get(i);
                map1.put("dev_"+i, planData);


            }
        }

        map1.put("result",1);
        jsonString = JSON.toJSONString(map1);


        return jsonString;

    }
  
  
  public boolean findEmail(String email)
  {
    String sql = "select id from user where uEmail='" + email + "'";
    List userData = getJdbcTemplate().queryForList(sql);
    if (userData.size() > 0) {
      return true;
    }
    return false;
  }
  
  public String findPwd(String email)
  {
 
    return "";
  }
  
  public boolean updataScore(int uid, int score)
  {
    String sql = "update user set uScore=uScore+" + score + " where id=" + uid;
    int re = getJdbcTemplate().update(sql);
    if (re == 1) {
      return true;
    }
    return false;
  }
  
  public boolean exec_sql(String sqls)
  {
    String sql = sqls;
    //log(sql);
    int re = getJdbcTemplate().update(sql);
    if (re == 1) {
      return true;
    }
    return false;
  }
  
  public String getUserList(int page)
  {
    String jsonString = "";
    String sql = "SELECT * FROM user LIMIT 0,20";
    //log("getUserID  " + sql);
    List userData = getJdbcTemplate().queryForList(sql);
    if (userData.size() <= 0) {
      jsonString = "{\"result\":-1}";
    }
    return jsonString;
  }
  
  public String getUserList(int draw, int start, int length, String callBack)
  {
    String countSql = "select count(1) as num from user";
    List countData = getJdbcTemplate().queryForList(countSql);
    Map countMap = (Map)countData.get(0);
    
    int recordsTotal = Integer.parseInt(countMap.get("num").toString());
    String recordsFiltered = "";
    String jsonString = "";
    
    String sql = "select id,uName ,uEmail ,`lock`,treeLv,uScore,loginTime from user order by id desc LIMIT " + start + "," + length;
    //log("getUserList  " + sql);
    List userData = getJdbcTemplate().queryForList(sql);
    Map userMap = new HashMap();
    userMap.put("draw", Integer.valueOf(draw));
    userMap.put("recordsTotal", Integer.valueOf(recordsTotal));
    userMap.put("recordsFiltered", Integer.valueOf(recordsTotal));
    Map xx;
    if (userData.size() > 0)
    {
      userMap.put("data", userData);
      xx = (Map)userData.get(0);
    }
    else
    {
      userMap.put("data", null);
    }
    jsonString = callBack + "(" + JSON.toJSONStringWithDateFormat(userMap, "yyyy-MM-dd HH:mm:ss", new SerializerFeature[0]) + ")";
    return jsonString;
  }
  
 /* public String exec_sql(String sqls)
  {
    String sql = sqls;
    log(sql);
    getJdbcTemplate().update(sql);
    
    String jsonString =;
    return jsonString;
  }*/
  
  public String adminLogin(String uName, String pwd, String callback, HttpServletRequest request)
  {
    String jsonString = "";
    String sql = "select * from admin where uName='" + uName + "'  and uPwd='" + pwd + "'";
    //log("adminLogin  " + sql);
    List userData = getJdbcTemplate().queryForList(sql);
    if (userData.size() > 0) {
      jsonString = "ok";
    } else {
      jsonString = "no";
    }
    return jsonString;
  }
  
  public void log(Object msg) 
  {
	   System.out.println(LocalDateTime.now().toString()+":"+msg);
	  
	 
  }


  

}
