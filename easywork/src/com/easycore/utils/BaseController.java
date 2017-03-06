package com.easycore.utils;

import java.beans.PropertyEditorSupport;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.RequestMapping;

import com.alibaba.fastjson.JSON;

public class BaseController {
	// 自动绑定日期字段
	@InitBinder
	public void initBinder(WebDataBinder binder) {
		binder.registerCustomEditor(Date.class, new DateEditor());
	}

	// 获取HttpSession
	@RequestMapping("/sessionget")
	public void sessionget(String name, HttpServletRequest req,
			HttpServletResponse resp) {
		String val = this.getSessionVal(req, name).toString().trim();
		this.print(resp, val);
		// System.out.println("sessionget:" + name + "->" + val);
	}

	// 设置HttpSession
	@RequestMapping("/sessionset")
	public void sessionset(String name, String val, HttpServletRequest req) {
		this.setSessionVal(req, name, val);
		// System.out.println("sessionset:" + name + "->" + val);
	}

	// 输出指定string
	public void print(HttpServletResponse resp, String content) {
		resp.setCharacterEncoding("UTF-8");
		PrintWriter out = null;
		try {
			out = resp.getWriter();
			out.print(content);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	// 输出json化的实体/实体集合/map/map集合
	public void printJson(HttpServletResponse resp, Object content) {
		resp.setCharacterEncoding("UTF-8");
		PrintWriter out = null;
		try {
			out = resp.getWriter();
			out.print(JSON.toJSONString(content));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	// 获取Application(ServletContext)对象
	public ServletContext getApp(HttpServletRequest req) {
		return req.getServletContext();
	}

	// 设置Application(ServletContext)对象val
	public void setAppVal(HttpServletRequest req, String name, Object val) {
		ServletContext app = req.getServletContext();
		app.setAttribute(name, val);
	}

	// 获取Application(ServletContext)对象val
	public Object getAppVal(HttpServletRequest req, String name) {
		ServletContext app = req.getServletContext();
		Object obj = app.getAttribute(name);
		return null == obj ? "" : obj;
	}

	// 设置HttpSession对象val
	public void setSessionVal(HttpServletRequest req, String name, Object val) {
		HttpSession ss = req.getSession();
		ss.setAttribute(name, val);
	}

	// 获取HttpSession对象val
	public Object getSessionVal(HttpServletRequest req, String name) {
		HttpSession ss = req.getSession();
		Object obj = ss.getAttribute(name);
		return null == obj ? "" : obj;
	}
}

class DateEditor extends PropertyEditorSupport {
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
