package com.easycore.utils;

import java.util.Date;
import com.alibaba.fastjson.annotation.JSONField;

public class ChatMessage {
	@JSONField(format = "yyyy-MM-dd HH:mm:ss")
	private Date sendtm;
	private String from;
	private String sendto;
	private Object content;
	private int type = 0;// 0常规/1me/2在线list/3进入/4离开

	public Date getSendtm() {
		return sendtm;
	}

	public void setSendtm(Date sendtm) {
		this.sendtm = sendtm;
	}

	public String getFrom() {
		return from;
	}

	public void setFrom(String from) {
		this.from = from;
	}

	public String getSendto() {
		return sendto;
	}

	public void setSendto(String sendto) {
		this.sendto = sendto;
	}

	public Object getContent() {
		return content;
	}

	public void setContent(Object content) {
		this.content = content;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}
}
