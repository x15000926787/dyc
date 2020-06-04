package com.dl.tool;

import com.alibaba.fastjson.JSONObject;
import org.apache.logging.log4j.LogManager;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.jdbc.core.JdbcTemplate;

import java.io.*;
import java.sql.ResultSet;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadPoolExecutor;


/**
 * Desc:报表任务，读取模板列表，分配线程
 * Created by xx on 2020/04/13.
 */
public  class ReportdnJob implements Job {
	private static final org.apache.logging.log4j.Logger logger = LogManager.getLogger(ReportdnJob.class);
	public  String reportPath ,bbname;
	//public DBConnection dbcon=new DBConnection();
	ResultSet rs=null;
	List<Map<String, Object>> rptlist = new ArrayList<Map<String, Object>>();
	DateTimeFormatter df = DateTimeFormatter.ofPattern("yyyy-MM-dd");
	public static JdbcTemplate jdbcTemplate;
	public ThreadPoolExecutor executor = null;

	public ReportdnJob()
	   {
		   reportPath = PropertyUtil.getProperty("reportPath");
		   jdbcTemplate=FirstClass.jdbcTemplate;
		  // executor = FirstClass.executor;

	   }

	private static void copyFileUsingFileStreams(File source, File dest)
			throws IOException {
		InputStream input = null;
		OutputStream output = null;
		try {
			input = new FileInputStream(source);
			output = new FileOutputStream(dest);
			byte[] buf = new byte[1024];
			int bytesRead;
			while ((bytesRead = input.read(buf)) > 0) {
				output.write(buf, 0, bytesRead);
			}
		} finally {
			input.close();
			output.close();
		}
	}

	@Override
	public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
		LocalDate rightnow = LocalDate.now();
		rightnow = rightnow.minusDays(1);
		//LocalDate tdate = rightnow.toLocalDate();
		JSONObject paraobj =(JSONObject) (jobExecutionContext.getJobDetail().getJobDataMap().get("taskdetial"));
		if (paraobj.containsKey("dtstr")) rightnow = LocalDate.parse(paraobj.getString("dtstr"),df);

		try {
			File file = new File(reportPath+"/"+rightnow.getYear());
			if (!file.exists()) {
				file.mkdirs();
			}
			file = new File(reportPath+"/"+rightnow.getYear()+"/"+rightnow.getMonthValue());
			if (!file.exists()) {
				file.mkdirs();
			}
			file = new File(reportPath+"/"+rightnow.getYear()+"/"+rightnow.getMonthValue()+"/"+rightnow.getDayOfMonth());
			if (!file.exists()) {
				file.mkdirs();
			}

			/*file = new File(reportPath+"\\bb");
			if (file.isDirectory()) {

				File[] fileArr = file.listFiles(); // 遍历目录,只遍历直接子目录。
				for(File f : fileArr){
					bbname = f.getName();
					if
				}


			}*/

			String sql = "select * from rptlist where valid=1 and type=3 order by id";
			try{
				rptlist= jdbcTemplate.queryForList(sql);
				File tfile = null;
				for(Map<String, Object> tmap : rptlist)
				{
					String bbname = (String)tmap.get("filename")+".xlsx";
					file = new File(reportPath+"/mb/"+bbname);
					tfile=new File(reportPath+"/"+rightnow.getYear()+"/"+rightnow.getMonthValue()+"/"+bbname);


					copyFileUsingFileStreams(file, tfile);
					Report3Task report3Task = new Report3Task(file,bbname,tmap.get("type").toString(),jdbcTemplate,rightnow);
					//if (executor.getQueue().size()<100)
					executor.execute(report3Task);







				}

			}catch(Exception e){
				logger.warn("出错了"+e.toString());
				e.printStackTrace();
			}


		} catch (Exception e) {
			logger.warn("readfile()   Exception:" + e.getMessage());
		}

	}
}
