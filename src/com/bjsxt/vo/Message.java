package com.bjsxt.vo;

import java.util.Date;
import java.util.List;

public class Message {

	private  String  alert;   //
	
	private  List<String>  names;
	
	private  String  sendMsg;
	
	private String  from;
	
	private String  date;
	
	
	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public String getSendMsg() {
		return sendMsg;
	}

	public void setSendMsg(String sendMsg) {
		this.sendMsg = sendMsg;
	}

	public String getFrom() {
		return from;
	}

	public void setFrom(String from) {
		this.from = from;
	}

	public String getAlert() {
		return alert;
	}

	public void setAlert(String alert) {
		this.alert = alert;
	}

	public List<String> getNames() {
		return names;
	}

	public void setNames(List<String> names) {
		this.names = names;
	}

	public Message() {
		super();
		// TODO Auto-generated constructor stub
	}
	
	
	
	
	
}
