package com.easycore.utils;

import java.beans.PropertyEditorSupport;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import org.java_websocket.WebSocket;
import org.java_websocket.framing.Framedata;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

/**
 * A simple WebSocketServer implementation. Keeps track of a "chatroom".
 */
public class ChatServer extends WebSocketServer {
	// userkey-clientkey
	protected ConcurrentHashMap<String, WebSocket> map = new ConcurrentHashMap<String, WebSocket>();

	// 自动绑定日期字段
	@InitBinder
	public void initBinder(WebDataBinder binder) {
		binder.registerCustomEditor(Date.class, new DateEditorC());
	}

	// 无参构造/读取配置文件websocket.ip
	public ChatServer() {
		super(new InetSocketAddress(Integer.parseInt(ConfigUtils.getVal(
				"config/chat.properties", "websocket.ip").split(":")[2])));
	}

	public ChatServer(int port) throws UnknownHostException {
		super(new InetSocketAddress(port));
	}

	public ChatServer(InetSocketAddress address) {
		super(address);
	}

	// 返回配置文件websocket.ip
	public String getChatServerIp() {
		return ConfigUtils.getVal("config/chat.properties", "websocket.ip")
				.trim();
	}

	// 开启chat服务
	@Override
	public void start() {
		try {
			super.start();
		} catch (Exception e) {
			System.out.println("can only be started once");
		}
	}

	// onOpen
	@Override
	public void onOpen(WebSocket conn, ClientHandshake handshake) {
	}

	// onClose
	@Override
	public void onClose(WebSocket conn, int code, String reason, boolean remote) {
		ChatMessage msg = new ChatMessage();
		msg.setSendtm(new Date());
		for (Entry<String, WebSocket> et : map.entrySet()) {
			if (et.getValue().toString().trim().equals(conn.toString().trim()))
				msg.setFrom(et.getKey());
		}
		msg.setContent("(离开房间)");
		msg.setType(4);
		this.sendToAll(JSON.toJSONString(msg));
		// 删除map影射关系
		map.remove(msg.getFrom());
	}

	// onMessage
	@Override
	public void onMessage(WebSocket conn, String message) {
		// 解析message:from/type
		ChatMessage msg = JSONObject.parseObject(message, ChatMessage.class);
		String from = msg.getFrom().trim();
		int type = msg.getType();
		// 设置msg
		msg.setSendtm(new Date());
		if (from.equals(""))
			msg.setFrom("No." + conn.toString().split("@")[1]);
		// 当msg为进入信息
		if (type == 3) {
			map.put(msg.getFrom(), conn);
			ChatMessage msgx = new ChatMessage();
			BeanUtils.copyProperties(msg, msgx);
			msgx.setContent("(进入房间)");
			this.sendToAll(JSON.toJSONString(msgx));
			return;
		}
		// 当msg为在线list信息
		if (type == 2) {
			List<String> users = new ArrayList<String>();
			for (Entry<String, WebSocket> et : map.entrySet()) {
				if (!et.getKey().trim().equals(msg.getFrom().trim()))
					users.add(et.getKey().trim());
			}
			ChatMessage msgx = new ChatMessage();
			BeanUtils.copyProperties(msg, msgx);
			msgx.setContent(users);
			this.sendToOne(JSON.toJSONString(msgx), msg.getFrom());
			return;
		}
		// 当msg为个人信息
		if (type == 1) {
			ChatMessage msgx = new ChatMessage();
			BeanUtils.copyProperties(msg, msgx);
			msgx.setContent(msg.getFrom());
			this.sendToOne(JSON.toJSONString(msgx), msg.getFrom());
			return;
		}
		// 解析message:sendto/content
		String sendto = msg.getSendto().trim();
		String content = HtmlUtils.getChatMsg(msg.getContent().toString());
		msg.setContent(content);
		// content=空时拒绝发送
		if (content.equals(""))
			return;
		// sendto=all时发送到全体
		if (sendto.equals("all")) {
			this.sendToAll(JSON.toJSONString(msg));
		} else {// 发送到单个用户
			this.sendToOne(JSON.toJSONString(msg), sendto);
		}
	}

	// @Override
	public void onFragment(WebSocket conn, Framedata fragment) {
		System.out.println("received fragment: " + fragment);
	}

	@Override
	public void onError(WebSocket conn, Exception ex) {
		ex.printStackTrace();
		if (conn != null) {
			// some errors like port binding failed may not be assignable to a
			// specific websocket
		}
	}

	// 发送至公聊
	public void sendToAll(String text) {
		Collection<WebSocket> con = connections();
		synchronized (con) {
			for (WebSocket c : con) {
				c.send(text);
			}
		}
	}

	// 发送至私聊/sendto为userkey或clientkey
	public void sendToOne(String text, String sendto) {
		if (!map.containsKey(sendto))
			return;
		Collection<WebSocket> con = connections();
		synchronized (con) {
			for (WebSocket c : con) {
				if (c.toString().trim()
						.equals(map.get(sendto).toString().trim()))
					c.send(text);
			}
		}
	}
}

class DateEditorC extends PropertyEditorSupport {
	@Override
	public void setAsText(String text) throws IllegalArgumentException {
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Date date = null;
		try {
			date = format.parse(text);
		} catch (ParseException e) {
			format = new SimpleDateFormat("yyyy-MM-dd");
			try {
				date = format.parse(text);
			} catch (ParseException e1) {
				e1.printStackTrace();
			}
		}
		setValue(date);
	}
}