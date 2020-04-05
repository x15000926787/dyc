package com.bjsxt.server;

import com.alibaba.fastjson.JSONObject;
import com.bjsxt.thread.ThreadSubscriber;
import com.dl.tool.AnaUtil;
import com.dl.tool.DBConnection;
import com.google.gson.Gson;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.internal.runners.model.EachTestNotifier;

import javax.websocket.*;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.*;


@ServerEndpoint("/chatSocket")
public class ChatSocket {

	
	/**
	 * @return the sockets
	 */
	public static Set<ChatSocket> getSockets() {
		return sockets;
	}




	/**
	 * @param sockets the sockets to set
	 */
	public static void setSockets(Set<ChatSocket> sockets) {
		ChatSocket.sockets = sockets;
	}

	private  static  Set<ChatSocket>  sockets=new HashSet<ChatSocket>();
//	private  static  Map<String, ChatSocket>  sMap=new HashMap<String, ChatSocket>();
	private static final Logger logger = LogManager.getLogger(ThreadSubscriber.class);
	private  static  List<String>   names=new ArrayList<String>();

	private  Session  session;
	private String username;
	private String groupKey;
	private String uId;
	//private ThreadDemo T1 = null;
	//private ThreadSubscriber T2 = null;
	private Gson  gson=new Gson();

	
	@OnOpen
	public  void open(Session  session){
		    
			this.session=session;
			sockets.add(this);
			
			String  queryString = session.getQueryString().split("&")[0];

			this.username = queryString.substring(queryString.indexOf("=")+1);
		    queryString = session.getQueryString().split("&")[1];
			this.groupKey = queryString.substring(queryString.indexOf("=")+1);;
		    queryString = session.getQueryString().split("&")[2];
		    this.uId = queryString.substring(queryString.indexOf("=")+1);;
			names.add(this.username);
		    logger.info("欢迎你："+this.username+"("+this.groupKey+")"+this.uId);
			
			//当websocket客户端连接成功，建立ThreadDemo线程，从实时库取数据
			/**/
	    /*    T2 = new ThreadSubscriber("thread_subscriber"+this.session.getId(),this.session);
		    try {
		    	 T2.start();
			} catch (Exception e) {
				logger.error(e.toString());
			}
		    T1 = new ThreadDemo(this.session.getId(),this.session);
		    T1.start();*/
//			
			
	}
	
	
	
	
	@OnError
	public void onError(Session session, Throwable error) {  
        logger.warn("发生错误:"+error.toString());
        error.printStackTrace();  
    }  
	@OnMessage
	public  void receive(Session  session,String msg ){
		
		/*Message  message=new Message();
		message.setSendMsg(msg);
		message.setFrom(this.username);
		message.setDate(new Date().toLocaleString());*/
		
		broadcast(sockets, gson.toJson(msg),-1);
	}
	
	@OnClose
	public  void close(Session session){
		names.remove(this.username);
		/*T1.killThreadByName(this.session.getId());
		
		if (T2.killThreadByName("thread_subscriber"+this.session.getId())) 
			logger.warn("thread_subscriber"+this.session.getId()+":  have been closed");*/
		sockets.remove(this);
		logger.warn(this.username+"再见");
		//broadcast(sockets, gson.toJson(message));
	}
	
	public void broadcast(Set<ChatSocket>  ss ,String msg ,long tt){
		int j=0;

		JSONObject msgmap = JSONObject.parseObject(msg);
		if (tt<0) {                                                         //发送redis中的 key-value

			for (Iterator iterator = ss.iterator(); iterator.hasNext(); ) {

				ChatSocket chatSocket = (ChatSocket) iterator.next();

				try {
					synchronized (chatSocket.session) {
						if (msgmap.containsKey(chatSocket.groupKey))
							chatSocket.session.getBasicRemote().sendText(gson.toJson(msgmap.get(chatSocket.groupKey)));

					}
				} catch (IOException e) {
					e.printStackTrace();
				}
				j++;
			}
		}
		else
		{                                                                   //发送alert message
			//logger.info(msg);
			for (Iterator iterator = ss.iterator(); iterator.hasNext(); ) {
				ChatSocket chatSocket = (ChatSocket) iterator.next();
				try {
					synchronized (chatSocket.session) {


						//for (int i=0;i<dd.length;i++){

                                    //if (msgmap.containsKey("gkey")) {

										String s = Integer.toBinaryString(Integer.parseInt((String) msgmap.get("auth")));
						                char[]  auths= (new StringBuffer(s).reverse()).toString().toCharArray();
										if (msgmap.get("gkey").toString().matches(chatSocket.groupKey) && auths[Integer.parseInt(chatSocket.uId)-1]=='1') {
											//logger.warn(chatSocket.groupKey + ":"+chatSocket.uId + ":" + msg);
											chatSocket.session.getBasicRemote().sendText(msg);

										}
									//}



						//}


					}
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
   
	
}
