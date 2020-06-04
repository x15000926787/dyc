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
    public String channel;
    public String message;
    //public Jedis mjedis;
    public JdbcTemplate jdbcTemplate;
    public ChatSocket skt;
    //public Jedis tjedis;

    public String getChannel() {
        return channel;
    }

    public void setChannel(String channel) {
        this.channel = channel;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public JdbcTemplate getJdbcTemplate() {
        return jdbcTemplate;
    }

    public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public ChatSocket getSkt() {
        return skt;
    }

    public void setSkt(ChatSocket skt) {
        this.skt = skt;
    }


/*

    public Jedis getTjedis() {
        return tjedis;
    }

    public void setTjedis(Jedis tjedis) {
        this.tjedis = tjedis;
    }

    public Jedis getMjedis() {
        return mjedis;
    }

    public void setMjedis(Jedis mjedis) {
        this.mjedis = mjedis;
    }
*/



    public static AnaUtil myana=new AnaUtil();
    private static final Logger logger = LogManager.getLogger(MyTask.class);

    //private ScriptEngine scriptEngine ;// scriptEngineManager.getEngineByName("nashorn");

    @Override
    public void run() {
        logger.warn("正在执行task "+channel+":"+message);
        //tjedis = RedisUtil.getJedis();
        //mjedis = RedisUtil.getJedis(1);
         {
            /*try {
                if (channel.indexOf("expired") > 0)
                    myana.handleExpired(message, jdbcTemplate);
                else {
                    myana.handleMessage( message, jdbcTemplate, skt);

                }
            } catch (Exception e) {
                e.printStackTrace();
            }*/
        }
        logger.warn("执行task结束 "+channel+":"+message);
        //logger.warn("task "+channel+":"+message+"执行完毕");
        //this.channel=null;
        //this.message=null;
        //this.jdbcTemplate=null;
        //this.skt=null;


    }

}

