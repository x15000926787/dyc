package com.dl.mqtt;

import com.bjsxt.server.ChatSocket;
import com.dl.tool.FirstClass;
import com.dl.tool.MqttTask;
import com.dl.tool.MyTask;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class MqttService {
	private static final Logger logger = FirstClass.logger;
    public void startCase(String message){
        //ChatSocket.broadcast(message);
        {
            MqttTask mqttTask = new MqttTask();
            mqttTask.setMessage(message);
            //if (executor.getQueue().size()<100)
            FirstClass.executor.execute(mqttTask);
            //logger.warn("线程池中线程数目："+executor.getPoolSize()+"，队列中等待执行的任务数目："+executor.getQueue().size()+"，已执行完毕的任务数目："+executor.getCompletedTaskCount());
        }
       // logger.info(message);
    }
}
