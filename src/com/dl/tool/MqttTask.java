package com.dl.tool;

import com.bjsxt.server.ChatSocket;
import net.sf.json.JSONObject;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.jdbc.core.JdbcTemplate;
import redis.clients.jedis.Jedis;

import java.util.Iterator;

/**
 * @author xuxu
 * @create 2020-03-21 14:31
 */
public class MqttTask implements Runnable {

    public String message;



    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
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



    //public static AnaUtil myana=new AnaUtil();
    private static final Logger logger = FirstClass.logger;

    //private ScriptEngine scriptEngine ;// scriptEngineManager.getEngineByName("nashorn");

    @Override
    public void run() {
       // logger.warn("正在执行mqtttask "+":"+message);
       // jedis = JedisUtil.getInstance().getJedis();

        AnaUtil.handlemqttMessage(message);
       // logger.warn("执行mqtttask结束 "+":"+message);

       // JedisUtil.getInstance().returnJedis(jedis);

    }

}

