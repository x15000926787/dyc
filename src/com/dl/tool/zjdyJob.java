package com.dl.tool;

import org.apache.logging.log4j.LogManager;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import redis.clients.jedis.Jedis;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import java.io.BufferedReader;
import java.io.FileReader;
import java.util.HashMap;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * Desc:总加任务
 * Created by xx on 2020/04/26.
 */
public  class zjdyJob implements Job {
	private static final org.apache.logging.log4j.Logger logger = LogManager.getLogger(zjdyJob.class);
	public  String zjdyPath ,bbname;
	Jedis jedis = null;
	String result=null;
	String key = null,val=null;
	BufferedReader br = null;
	ScriptEngineManager manager = new ScriptEngineManager();
	ScriptEngine engine = manager.getEngineByName("js");
	String[] calc = null;
	HashMap<String, Object> taskdetial = null;//(HashMap<String, Object>) (arg0.getJobDetail().getJobDataMap().get("taskdetial"));
	String luaname = null;//(String)taskdetial.get("txtname");
	public ThreadPoolExecutor executor = null;

	public zjdyJob()
	   {
		   zjdyPath = PropertyUtil.getProperty("zjdyPath");

	   }



	@Override
	public void execute(JobExecutionContext arg0) throws JobExecutionException {

		taskdetial = (HashMap<String, Object>) (arg0.getJobDetail().getJobDataMap().get("taskdetial"));
		luaname =(String)taskdetial.get("txtname");

		try {
			jedis = RedisUtil.getJedis();

			try{
				 br = new BufferedReader(new FileReader(zjdyPath+luaname));//构造一个BufferedReader类来读取文件
				String s = null;
				//System.out.println("ddd");
				while((s = br.readLine())!=null){//使用readLine方法，一次读一行
					//result.append(System.lineSeparator()+s);
					//System.out.println(s);
					 calc=s.split("=");
					//System.out.println(calc);


					Pattern p = Pattern.compile("val\\{[^\\}]+");
					Matcher m = p.matcher(calc[1]);//strTmp替换成你的字符串
					while (m.find()) {
						//logger.warn("find: "+m.group(0));
						key = m.group(0).replace("val{","")+"_.value";
						//logger.warn(key);
						if (jedis.exists(key))
						{
							//logger.warn(key);
							val = jedis.get(key);
							calc[1] = calc[1].replace(m.group(0)+"}",val);
						}
						else
						{
							calc[1] = calc[1].replace(m.group(0)+"}","0");
						}
					}
					//logger.warn(calc[1]);
					 result =  engine.eval(calc[1]).toString();
					//logger.warn(result);
					jedis.set(calc[0]+"_.value",String.valueOf(result));

					calc=null;
				}
				br.close();
				//br = null;
			}catch(Exception e){
				//logger.warn(e.toString());
				e.printStackTrace();
			}

			RedisUtil.close(jedis);
			//jedis=null;

		} catch (Exception e) {
			logger.warn("readfile()   Exception:" + e.getMessage());
		}
		//taskdetial.clear();
		//taskdetial = null;
		//logger.warn("calc :" + luaname);
	}
}
