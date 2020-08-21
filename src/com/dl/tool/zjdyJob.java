package com.dl.tool;

import com.googlecode.aviator.AviatorEvaluator;
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
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * Desc:总加任务
 * Created by xx on 2020/04/26.
 */
public  class zjdyJob implements Job {


	//(String)taskdetial.get("txtname");
	//public ThreadPoolExecutor executor = null;

	public zjdyJob()
	   {

		   //logger.warn("create zjdyjob");

	   }



	@Override
	public void execute(JobExecutionContext arg0) throws JobExecutionException {
		String zjdyPath ;
		zjdyPath = PropertyUtil.getProperty("zjdyPath")+"zjdy.txt";
		BufferedReader br = null;
		FileReader fileReader = null;
		String[] calc = null;
		Jedis jedis = null;
		String result=null;
		String key = null,val=null;
		Pattern p = null;//Pattern.compile("val\\{[^\\}]+");
		Matcher m = null;//p.matcher(calc[1]);//strTmp替换成你的字符串

		String s = null;
		//HashMap<String, Object> taskdetial = null;//(HashMap<String, Object>) (arg0.getJobDetail().getJobDataMap().get("taskdetial"));

		//taskdetial = (HashMap<String, Object>) (arg0.getJobDetail().getJobDataMap().get("taskdetial"));
		//luaname ="zjdy.txt";

		try {
			jedis= JedisUtil.getInstance().getJedis();


			try{
				 fileReader = new FileReader(zjdyPath);
				 br = new BufferedReader(fileReader);//构造一个BufferedReader类来读取文件

				//System.out.println("ddd");
				while((s = br.readLine())!=null){//使用readLine方法，一次读一行
					//result.append(System.lineSeparator()+s);
					//System.out.println(s);
					 calc=s.split("=");
					//System.out.println(calc);


					 p = Pattern.compile("val\\{[^\\}]+");
					 m = p.matcher(calc[1]);//strTmp替换成你的字符串
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
					 result =  AviatorEvaluator.execute(calc[1]).toString();
					//logger.warn(result);
					jedis.set(calc[0]+"_.value",String.valueOf(result));

					calc=null;
				}
				br.close();
				br=null;
				fileReader.close();
				fileReader=null;
				//br = null;
			}catch(Exception e){
				//logger.warn(e.toString());
				e.printStackTrace();
			}

			JedisUtil.getInstance().returnJedis(jedis);

			s = null;
			// br = null;
			//fileReader = null;

			//RedisUtil.close(jedis);

			result=null;
			key = null;
			val=null;
			p = null;
			m = null;
			//manager = null;
			//engine = null;

			//taskdetial = null;//(HashMap<String, Object>) (arg0.getJobDetail().getJobDataMap().get("taskdetial"));

			zjdyPath=null;

		} catch (Exception e) {

		}
		//taskdetial.clear();
		//taskdetial = null;
		//logger.warn("calc :" + luaname);
	}
}
