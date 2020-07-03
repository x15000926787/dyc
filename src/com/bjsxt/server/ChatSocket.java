package com.bjsxt.server;

import com.alibaba.fastjson.JSONObject;
import com.bjsxt.thread.ThreadSubscriber;
import com.dl.tool.FirstClass;
import com.google.gson.Gson;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.websocket.*;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.*;


@ServerEndpoint("/chatSocket")
public final  class ChatSocket {


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

	public  static  Set<ChatSocket>  sockets=new HashSet<ChatSocket>();
	//	private  static  Map<String, ChatSocket>  sMap=new HashMap<String, ChatSocket>();
	//private static final Logger logger = LogManager.getLogger(ThreadSubscriber.class);
	public   List<String>   names=new ArrayList<String>();
	public  static  ChatSocket chatSocket=null;
	public  Session  session;
	public  String username;
	public  String groupKey;
	public  String uId;
	//private ThreadDemo T1 = null;
	//private ThreadSubscriber T2 = null;

	private static Gson  gson=new Gson();
	static JSONObject msgmap = null;
	@OnOpen
	public  void open(Session  session){

		this.session=session;


		String  queryString = session.getQueryString().split("&")[0];

		this.username = queryString.substring(queryString.indexOf("=")+1);
		queryString = session.getQueryString().split("&")[1];
		this.groupKey = queryString.substring(queryString.indexOf("=")+1);;
		queryString = session.getQueryString().split("&")[2];
		this.uId = queryString.substring(queryString.indexOf("=")+1);;
		sockets.add(this);
		names.add(this.username);
		FirstClass.logger.info("欢迎你："+this.username+"("+this.groupKey+")"+this.uId);
		//FirstClass.logger.info(sockets.size());
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
		FirstClass.logger.warn("发生错误:"+error.toString());
		error.printStackTrace();
	}
	@OnMessage
	public  void receive(Session  session,String msg ){
		
		/*Message  message=new Message();
		message.setSendMsg(msg);
		message.setFrom(this.username);
		message.setDate(new Date().toLocaleString());*/

		broadcast(gson.toJson(msg),-1);
	}

	@OnClose
	public  void close(Session session) throws IOException {
		names.remove(this.username);
		/*T1.killThreadByName(this.session.getId());
		
		if (T2.killThreadByName("thread_subscriber"+this.session.getId())) 
			logger.warn("thread_subscriber"+this.session.getId()+":  have been closed");*/
		session.close();
		sockets.remove(this);
		FirstClass.logger.warn(this.username+"再见");
		//broadcast(sockets, gson.toJson(message));
	}

	public static void broadcast(String msg ,long tt){


		//logger.warn(msg);
		msgmap = JSONObject.parseObject(msg);
		if (tt<0) {                                                         //发送redis中的 key-value

			for (Iterator iterator = sockets.iterator(); iterator.hasNext(); ) {

				 chatSocket = (ChatSocket) iterator.next();

				try {

					{


								if (chatSocket.groupKey.matches("un_all_")) {

									for (String str : msgmap.keySet()) {
										if (chatSocket.session.isOpen()) {
											try {
												synchronized (chatSocket.session) {
													chatSocket.session.getBasicRemote().sendText(gson.toJson(msgmap.get(str)));
												}
											}catch (Exception e)
											{}

										}
									}
								} else {
									if (msgmap.containsKey(chatSocket.groupKey))
										synchronized (chatSocket.session) {
											chatSocket.session.getBasicRemote().sendText(gson.toJson(msgmap.get(chatSocket.groupKey)));
										}
								}



					}
				} catch (IOException e) {
					//e.printStackTrace();
				}

			}
		}
		else
		{                                                                   //发送alert message
			//logger.info(msg);
			for (Iterator iterator = sockets.iterator(); iterator.hasNext(); ) {

				chatSocket = (ChatSocket) iterator.next();
				//FirstClass.logger.warn(chatSocket.username.toString()+":"+chatSocket.session.toString());
				try {
					//synchronized(chatSocket.session)
					{
						//logger.warn(chatSocket.groupKey);

							if (chatSocket.groupKey.matches("un_all_")) {

								for (String str : msgmap.keySet()) {

									if (chatSocket.session.isOpen()) {

											try {
												synchronized(chatSocket.session) {
												chatSocket.session.getBasicRemote().sendText(gson.toJson(msgmap.get(str)));
												}
											}catch (Exception e)
											{

											}


									}
								}
							} else {
								if (msgmap.containsKey(chatSocket.groupKey))

									    try {
											synchronized(chatSocket.session) {
											chatSocket.session.getBasicRemote().sendText(gson.toJson(msgmap.get(chatSocket.groupKey)));
											}
										}catch (Exception e)
										{

										}


							}




					}
				} catch (Exception e) {
					//e.printStackTrace();
				}
			}
		}

		//msgmap=null;

	}
	public static void broadcast(String msg ){


		//logger.warn(msg);
		msgmap = JSONObject.parseObject(msg);
		//msgmap = (JSONObject)msgmap.get("dl");

		{                                                                   //发送alert message
			//logger.info(msg);
			for (Iterator iterator = sockets.iterator(); iterator.hasNext(); ) {

				chatSocket = (ChatSocket) iterator.next();
				try {
					synchronized(chatSocket.session){
						//logger.warn(chatSocket.groupKey);

						if (chatSocket.groupKey.matches("un_all_")) {

							//for (String str : msgmap.keySet()) {
								if (chatSocket.session.isOpen()) {
									chatSocket.session.getBasicRemote().sendText(gson.toJson(msgmap.get("dl")));
								}
							//}
						} else {
							if (msgmap.containsKey(chatSocket.groupKey))

								chatSocket.session.getBasicRemote().sendText(gson.toJson(msgmap.get(chatSocket.groupKey)));
						}




					}
				} catch (IOException e) {
					//e.printStackTrace();
				}
			}
		}

		//msgmap=null;

	}

}
