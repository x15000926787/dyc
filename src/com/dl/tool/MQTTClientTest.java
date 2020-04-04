package com.dl.tool;

import java.util.Random;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.MqttTopic;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

//import com.syxy.util.StringTool;

public class MQTTClientTest  implements ServletContextListener {  
    private static final long serialVersionUID = 1L;  
  
  
    private MqttClient client;  
    private String host = "tcp://192.168.3.203:1883";  
//  private String host = "tcp://iot.eclipse.org:1883";  
    private String userName = "admin";  
    private String passWord = "admin";  
    private MqttTopic topic;  
    private MqttMessage message;  
  
    private String myTopic = "xx";  
   // String clientMac = StringTool.generalMacString();
    String clientMac = generalMacString();
    private static final Logger logger = LogManager.getLogger(MQTTClientTest.class);
    public MQTTClientTest() {  
  
        try { 
            client = new MqttClient(host, clientMac, new MemoryPersistence());  
            connect();
            //System.out.println("connected----"+host+"-------"+clientMac); 
            client.subscribe(myTopic, 0);
        } catch (Exception e) { 
            e.printStackTrace(); 
        }
  
//        Container container = this.getContentPane();  
//        panel = new JPanel();  
//        button = new JButton(clientMac);  
//        button.addActionListener(new ActionListener() {  
//
//            public void actionPerformed(ActionEvent ae) {  
//                try {
//                    MqttDeliveryToken token = topic.publish(message);  
//                    token.waitForCompletion();  
//                    System.out.println(token.isComplete()+"========");  
//                } catch (Exception e) {  
//                    e.printStackTrace();  
//                }  
//            } 
//        });  
//        panel.add(button);  
//        container.add(panel, "North");  
    }  
    public static String generalMacString(){
		 String str = "ABCDEF0123456789";
		 Random random = new Random();
		 StringBuffer sb = new StringBuffer();
		 int macGroupSize = 6;//mac地址长度为6组16进制
		 int macOneGroup = 2;//一组有两个字符
		 for (int i = 0; i < macGroupSize; i++) {
			 for (int j = 0; j < macOneGroup; j++) {
					int number = random.nextInt(16);
					sb.append(str.charAt(number));
			}
		    sb.append("-");
		}
		String macString = sb.toString().substring(0, sb.length()-1);
		return macString;
	 }
    private void connect() {  
  
        MqttConnectOptions options = new MqttConnectOptions();  
        options.setCleanSession(false);  
        options.setUserName(userName);  
        options.setPassword(passWord.toCharArray());  
        options.setConnectionTimeout(10);    
        options.setKeepAliveInterval(5 * 60);  
        try {  
            client.setCallback(new MqttCallback() {  
  
                public void connectionLost(Throwable cause) {  
                	logger.warn("Mqtt connectionLost-----------");  
                }  
  
                public void deliveryComplete(IMqttDeliveryToken token) { 
                	logger.warn("Mqtt deliveryComplete---------"+token.isComplete());  
                }  
  
                public void messageArrived(String topic, MqttMessage arg1)  
                        throws Exception {  
                	logger.warn("MqttMessageArrived:  "+topic+":"+arg1.toString());  
                	//logger.warn();
                   
  
                }  
            });  
            
            
            topic = client.getTopic(myTopic);  
            
            message = new MqttMessage();
            message.setQos(2);  
            message.setRetained(true);  
            //System.out.println(message.isRetained()+"------ratained״̬");  
            message.setPayload("MqttMessageArrived---".getBytes());  
  
            client.connect(options); 
        } catch (Exception e) {  
            e.printStackTrace();  
        }  
  
    }  
  
   /* public static void main(String[] args) {
    	//for (int i = 0; i < 1000; i++) {
    		MQTTClientTest s = new MQTTClientTest();
    		//System.out.println(i);
		//}
    		
//        s.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);  
//        s.setSize(600, 370);  
//        s.setLocationRelativeTo(null);  
//        s.setVisible(true);  
    }*/
	@Override
	public void contextDestroyed(ServletContextEvent arg0) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void contextInitialized(ServletContextEvent arg0) {
		// TODO Auto-generated method stub
		MQTTClientTest s = new MQTTClientTest();
		
	} 
}  