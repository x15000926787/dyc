package com.dl.mqtt;

import com.dl.tool.FirstClass;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class MqttService {
	private static final Logger logger = LogManager.getLogger(MqttService.class);
    public void startCase(String message){
        //FirstClass.logger.info("ddd");
        logger.info(message);
    }
}
