package com.dl.tool;



import org.apache.commons.lang3.StringUtils;
import org.quartz.*;
import org.springframework.jdbc.JdbcUpdateAffectedIncorrectNumberOfRowsException;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.Pipeline;
import redis.clients.jedis.Response;
import redis.clients.jedis.exceptions.JedisConnectionException;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * [任务类]
 * @author xx
 * @date 2020-07-01
 * @copyright copyright (c) 2018
 */
@PersistJobDataAfterExecution
@DisallowConcurrentExecution
public  class saveRedis implements Job {
	   

	@SuppressWarnings("unchecked")
	@Override
    public void execute(JobExecutionContext arg0) throws JobExecutionException {
		Jedis jedis = null;
		jdbcutils.getConnection();
        int gno=0,vno=0,sav=0;
		List<Map<String, Object>> list=null;
		LocalDateTime rightnow = LocalDateTime.now();
         //List<Map<String, Object>> findModeResult
		String sql = "select * from hyc"+(rightnow.getYear() % 10)+" where savetime= DATE_FORMAT(ADDDATE(NOW(),INTERVAL -minute(now()) % 5 minute),'%m%d%H%i') and chgtime is not null";
		try {
			list = jdbcutils.findModeResult(sql, null);
		} catch (SQLException e) {
			e.printStackTrace();
		}
        FirstClass.logger.warn(sql);
		if (!list.isEmpty())
		{
			jedis= JedisUtil.getInstance().getJedis(2);
			for (Map<String, Object> datamap : list) {
				gno = Integer.parseInt(datamap.get("GROUPNO").toString());
				for (Map.Entry<String, Object> entry : datamap.entrySet()) {
					//System.out.println("key = " + entry.getKey() + ", value = " + entry.getValue());

					if (StringUtils.isNotBlank(entry.getValue().toString())&&entry.getKey().toString().contains("val"))
					{
						vno = Integer.parseInt(entry.getKey().toString().replace("val",""));
						sav = gno*200+vno;
						jedis.set(AnaUtil.saveno_kkey.get(String.valueOf(sav))+"_.value",entry.getValue().toString());

					}
				}
			}
			JedisUtil.getInstance().returnJedis(jedis);
		}
		jdbcutils.releaseConn();


    }
    	
}
