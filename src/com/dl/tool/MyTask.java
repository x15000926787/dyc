package com.dl.tool;

import com.bjsxt.server.ChatSocket;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.jdbc.core.JdbcTemplate;
import redis.clients.jedis.Jedis;

/**
 * @author xuxu
 * @create 2020-03-21 14:31
 */
public class MyTask implements Runnable {
    private String channel;
    private String message;
    private JdbcTemplate jdbcTemplate;
    private Jedis tjedis;
    private Jedis mjedis;
    private ChatSocket skt;
    public static AnaUtil myana=new AnaUtil();
    private static final Logger logger = LogManager.getLogger(MyTask.class);

    //private ScriptEngine scriptEngine ;// scriptEngineManager.getEngineByName("nashorn");
    public MyTask(String channel,String message,JdbcTemplate jdbcTemplate, ChatSocket skt) {
        this.channel = channel;
        this.message = message;
        this.jdbcTemplate = jdbcTemplate;
        this.skt = skt;
       // this.scriptEngine = scriptEngine;
        //this.tjedis = tjedis;
    }

    @Override
    public void run() {
        //logger.warn("正在执行task "+channel+":"+message);
        tjedis = RedisUtil.getJedis();
        mjedis = RedisUtil.getJedis(1);
        try {
           if (channel.indexOf("expired")>0)
               myana.handleExpired(channel,message,jdbcTemplate,tjedis);
           else {
               myana.handleMessage(channel, message, jdbcTemplate, tjedis,mjedis, skt);

           }
        } catch (Exception e) {
            e.printStackTrace();
        }
        RedisUtil.close(tjedis);
        tjedis=null;
        RedisUtil.close(mjedis);
        mjedis=null;
        //logger.warn("task "+channel+":"+message+"执行完毕");
    }
}

