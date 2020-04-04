package com.dl.tool;

import com.mysql.jdbc.PreparedStatement;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Properties;




public class ThreadTimer extends Thread {
	   private Thread t;
	   private String threadName;
	   private String url = "jdbc:mysql://localhost:3306/scada";
       // jdbc协议:数据库子协议:主机:端口/连接的数据库   //
	   static SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	   private String user = "root";//用户名
	   private String password = "Cs2017";//密码
	   private static final Logger logger = LogManager.getLogger(ThreadTimer.class);
       String jsonStr = null;
       String sql = null; 
       String[] cmds =new String[3]; 
       String gp_name[] = null;
       String da_name[] = null;
       String cmd[] = null;
       String pid[] = null;
       ResultSet ret = null;  
//	   private  String[] val_yc =new String[1000];
//	   Random rand = new Random();
      
	   public  ThreadTimer(String name) {
	      threadName = name;
	      logger.info("Creating " +  threadName );
	      //logger.info(System.getProperty("user.dir"));//user.dir指定了当前的路径
	      try {  

	        	Class.forName("com.mysql.jdbc.Driver");
	            conn = DriverManager.getConnection(url,user,password);//获取连接  
	           // pstt = (PreparedStatement) conn.prepareStatement(sql);//准备执行语句  
	        } catch (Exception e) {  
	            e.printStackTrace();  
	        }
	   }
	   public Connection conn = null;  
	   public PreparedStatement pst = null;  
	   
	   /**
	    * @desc 执行cmd命令 
	    * @author xx
	  * @throws InterruptedException 
	    * @date 2018-3-29
	    */	   
	   public  int executeCmd(String command) throws IOException, InterruptedException {  
	 	  //logger.info("Execute command :" + command);  
	 	  int exitVal; 
	       Runtime runtime = Runtime.getRuntime();  
	       Process process = runtime.exec("cmd /c " + command); 
	       exitVal = process.waitFor();
	      // BufferedReader br = new BufferedReader(new InputStreamReader(process.getErrorStream(), "GBK"));  

	       BufferedReader br = new BufferedReader(new InputStreamReader(process.getErrorStream(), "GBK"));  
	       String line = null;  
	       StringBuilder build = new StringBuilder();  
	       while ((line = br.readLine()) != null) {  
	     	  logger.info(line);  
	           build.append(line);  
	       } 
	       
	       if (build.length()==0){
	    	   return 1;
	       }
	       else {
	    	   return 0;
	 	  }   
	 	  
	         
	   }  

	   public  String runCMDShow(String strcmd) throws Exception 
	   { 
	 	 
	      // Process p = Runtime.getRuntime().exec("cmd /c start  " + strcmd+" "); 
		   Process p = Runtime.getRuntime().exec("" + strcmd+" "); 
	       logger.info(strcmd);
	       BufferedReader br = new BufferedReader(new InputStreamReader(p.getInputStream()));   
	       String readLine = null; 
	       StringBuilder build = new StringBuilder(); 
	       while ((readLine = br.readLine()) != null) {  
	           //readLine = br.readLine(); 
	           logger.info(readLine); 
	           build.append("	"+readLine); 
	       } 
	       if(br!=null){ 
	           br.close(); 
	       } 
	       p.destroy(); 
	       p=null;
	 	return build.toString(); 
	   } 
	   public PreparedStatement DBconn(String sql) { 
	        PreparedStatement pstt = null;
	        try {  

	        	//Class.forName("com.mysql.jdbc.Driver");
	           // conn = DriverManager.getConnection(url,user,password);//获取连接  
	            pstt = (PreparedStatement) conn.prepareStatement(sql);//准备执行语句  
	        } catch (Exception e) {  
	            e.printStackTrace();  
	        }
			return pstt;  
	    }
	    public void close() {  
	        try {  
	            this.conn.close();  
	            this.pst.close();  
	        } catch (SQLException e) {  
	            e.printStackTrace();  
	        }  
	    } 
	    
	    public String get_dbip() { 
	        Properties prop = new Properties();  
	        String key = null;
	        String homestr = System.getProperty("user.dir");
	        System.out.print(homestr);
	        String path = ".\\webapps\\qs\\WEB-INF\\classes\\dbconfig.properties";
	        if (homestr.indexOf("bin")>0) path = "..\\webapps\\qs\\WEB-INF\\classes\\dbconfig.properties";
	        
	        try{
	            //读取属性文件a.properties
	        	String pathString = System.getProperty("user.dir");
	            InputStream in = new BufferedInputStream (new FileInputStream(path));
	            prop.load(in);     ///加载属性列表
	            key = prop.getProperty("url"); 
	            //logger.info(key);
	            in.close();
	           
	          
	        }
	        catch(Exception e){
	            logger.info(e);
	        }
	        key = "jdbc:mysql://localhost:3306/scada";
	         
			return key;
	    } 
	    public String find_proinfo(String id) throws SQLException, IOException{
	    	String ss = null;
	    	String arr[] = new String[4];
	    	arr[0] = "";
	    	try {
	    	sql = "select * from project_info where id="+id;//SQL语句  
	    	pst = DBconn(sql);
	    	ret = pst.executeQuery();//执行语句，得到结果集  
	    	ret.last(); // 将光标移动到最后一行   
	    	int size = ret.getRow();
	    	ret.first();
	    	
	    	//mosquitto_pub -h 101.89.205.94 -p 1883 -d -q 0 -t /gc/ZGZHDZ/CFF2B7046E1E4E75864D4819942D34B1 -m J{"""h""":{"""rt""":"""
	    	if (size> 0)
		    {
				
		    	for (int i = 0; i<size; i++)
		    	{
					
		    		
		    			arr[0] = (ret.getString(1));
		    			arr[1] = (ret.getString(2));
		    			arr[2] = (ret.getString(3));
		    			arr[3] = (ret.getString(4));
					
		    		
		    		//arr[1] = (ret.getString(1));
		    		//arr[2] = (ret.getString(2));
		    		//arr[3] = (ret.getString(3));
		    		
		    		
		    		ret.next();
		    		
		    	}
		    	  
			      //map1.put("result",1);  
		           
		          ss = arr[3]+"\\mosquitto_pub.exe -h "+arr[0]+" -p 1883 -d -q 0 -t /gc/"+arr[2]+"/"+arr[1]+" -m J{\"\"\"h\"\"\":{\"\"\"rt\"\"\":\"\"\"gp_name\"\"\"},\"\"\"b\"\"\":{\"\"\"dl\"\"\":{\"\"\"da_name\"\"\":\"\"\"on_off\"\"\"}}}";
		         // logger.info(ss);
		    }
	    	} catch (SQLException e) {
				logger.info(e.toString());
			}
	    	
			return ss;
	    	
	    }
	    public String[] find_task() throws SQLException, IOException{
	    	String ss = null;
	    	String arr[] = new String[4];
	    	 Date today = new Date();
		      Calendar c=Calendar.getInstance();
		      c.setTime(today);
		      int weekday=c.get(Calendar.DAY_OF_WEEK)-1;
		      Calendar cal = Calendar.getInstance();
		      //cal.add(Calendar.DAY_OF_YEAR, -1);
		      cal.set(Calendar.HOUR_OF_DAY, 0);      
		      cal.set(Calendar.SECOND, 0);
		      cal.set(Calendar.MINUTE, 0);
		      cal.set(Calendar.MILLISECOND, 0);
		      long cur_min = (long) (System.currentTimeMillis()-cal.getTimeInMillis()) / 60 / 1000;
            //logger.info(cur_min);
		     // cur_min = 1200;
	    	sql = "select * from time_plan_v where ("+cur_min+"-tt) in (0,3,6,9) and week in ("+weekday+",8) order by gp_name";//SQL语句   
	    	//if (cur_min%2==1) sql="select * from time_plan_v where ("+cur_min+"-tt) in (3,9) and week in ("+weekday+",8) order by gp_name desc";//SQL语句  
	    	//sql = "select * from time_plan_v where ("+cur_min+"-tt) >0 and week="+weekday+" order by gp_name";//SQL语句   
	    	logger.info("find task:"+sql);
		     // sql = "select * from time_plan_v where tt="+cur_min+" and week in (3,4)";//SQL语句  
			try {
				pst = DBconn(sql);
		    	ret = pst.executeQuery();//执行语句，得到结果集  
		    	ret.last(); // 将光标移动到最后一行   
		    	int size = ret.getRow();
		    	
		    	ret.first(); 
		    	if (size> 0)
			    {
		    		logger.info(sql);
			    	for (int i = 0; i<size; i++)
			    	{
						
			    		
			    		arr[0]=arr[0]+","+(ret.getString(2));
			    		arr[1]=arr[1]+","+(ret.getString(3));
			    		arr[2]=arr[2]+","+(ret.getString(6));
			    		arr[3]=arr[3]+","+(ret.getString(1));
			    		
			    		ret.next();
			    		
			    	}
			    	 
			         
			    }
		    	//logger.info(arr[0]);
		    	//logger.info(arr[1]);
		    	//logger.info(arr[2]);
		    	//logger.info(arr[3]);
			} catch (Exception e) {
				logger.info(e.toString());
			}
			finally 
			{ 
				//close(); 
			} 
	    	//logger.info(arr[0]);
	    	
			return arr;
	    	
	    }

	public void find_setval_task() throws SQLException, IOException{
		String ss = null;
		String cmdstr = null,gpda=null;
		Calendar beforeTime = Calendar.getInstance();
		beforeTime.add(Calendar.MINUTE, -1);// 1分钟之前的时间
		String rltstr = "";
		Date beforeD = beforeTime.getTime();
		ss = "\\mosquitto_pub.exe -h mqtt_ip -p 1883 -d -q 0 -t /gc/p2/p1 -m J{\"\"\"h\"\"\":{\"\"\"rt\"\"\":\"\"\"gp_name\"\"\"},\"\"\"b\"\"\":{\"\"\"dl\"\"\":{\"\"\"da_name\"\"\":\"\"\"on_off\"\"\"}}}";
		String before5 = new SimpleDateFormat("yyyyMMddHHmmss").format(beforeD);  // 前1分钟时间
		before5=before5.substring(2);
		String sql = "select b.setVal,c.kkey,d.mqtt_ip,d.gw_id,d.pro_name,d.cstation_path from hevt"+(beforeD.getYear()%10)+" a,etype_info b,prtudig c,project_info d,prtu e where c.rtuno=e.rtuno and e.DOMAIN=d.id and a.ch=c.rtuno and a.xh=c.sn and a.ymd*1000000+a.hms>"+before5+" and a.event=b.e_type and a.zt=b.e_zt and b.setval is not null";

		logger.info("find setval task:"+sql);

		try {
			pst = DBconn(sql);
			ret = pst.executeQuery();//执行语句，得到结果集
			ret.last(); // 将光标移动到最后一行
			int size = ret.getRow();

			ret.first();
			if (size> 0)
			{
				logger.info(sql);
				for (int i = 0; i<size; i++)
				{
					gpda = ret.getString(1);
                    cmdstr = ret.getString(5)+ss;
					cmdstr = cmdstr.replace("mqtt_id",ret.getString(2));
					cmdstr = cmdstr.replace("p2",ret.getString(4));
					cmdstr = cmdstr.replace("p1",ret.getString(3));
					cmdstr = cmdstr.replace("gp_name",gpda.split(",")[0]);
					cmdstr = cmdstr.replace("da_name",gpda.split(",")[1]);
					cmdstr = cmdstr.replace("on_off",ret.getString(0));


					try {
						rltstr = runCMDShow(jsonStr);
					} catch (SQLException e) {
						e.printStackTrace();
					}
					ret.next();

				}


			}
			//logger.info(arr[0]);
			//logger.info(arr[1]);
			//logger.info(arr[2]);
			//logger.info(arr[3]);
		} catch (Exception e) {
			logger.info(e.toString());
		}
		finally
		{
			//close();
		}
		//logger.info(arr[0]);



	}
	   public void run() {
	      logger.info("Running " +  threadName );
	      Thread.currentThread().setName(threadName);
	      int i = 1;
	      int rlt = 0;
	      int id = 0;
	      String rltstr = "";
	      try {
	         while(true) {
	        	
	        	 try {
	        		 //ss.isEmpty()
	        		//url = get_dbip();
	        		
	        		cmds = find_task();
	        		if (cmds[0]!=null && cmds[0].length()!=0){
	        			gp_name = cmds[0].split(",");
		        		da_name = cmds[1].split(",");
		        		cmd = cmds[2].split(",");
		        		pid = cmds[3].split(",");
		        		logger.info("have task:"+gp_name.length);
		        		for (i=0;i<gp_name.length-1;i++)
		        		{
		        			jsonStr = find_proinfo(pid[i+1]);
		        			jsonStr = jsonStr.replace("gp_name",gp_name[i+1]);
		        			jsonStr = jsonStr.replace("da_name",da_name[i+1]);
		        			jsonStr = jsonStr.replace("on_off",cmd[i+1]);
		        			
		        			//jsonStr = jsonStr.replace("on_off",""+i);
		        			//rlt = executeCmd(jsonStr);
		        			
		        			rltstr = runCMDShow(jsonStr);
		        			//logger.info(">>>>>"+i+":"+gp_name[i+1]+":"+da_name[i+1]+":"+cmd[i+1]+":"+rltstr);
		        			//logger.info("jsonStr:"+jsonStr);
		        			 Thread.sleep(1000);
		        		 }
		        		
	        		}

					 find_setval_task();
		        	 
			      }catch (Exception ee) {
			         logger.info("Thread err: "+ee.toString()+" "+ee.getMessage() );
			      }
	            Thread.sleep(60*1000);
	         }
	      }catch (InterruptedException e) {
	         logger.info("Thread " +  Thread.currentThread().getName() + " interrupted.");
	      }
	      finally 
			{ 
				close(); 
			} 
	      logger.info("Thread " +  threadName + " exiting.");
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