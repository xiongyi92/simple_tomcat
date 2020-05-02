package com.webserver.core;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import com.webserver.servlet.HttpServlet;

/**
 * 服务端相关配置信息
 * @author ta
 *
 */
public class ServerContext {
	/**
	 * 请求与对应Servlet的关系
	 * key:请求路径
	 * value:对应的Servlet实例
	 */
	private static Map<String,HttpServlet> servletMapping = new HashMap<>();

	static {
		initServletMapping();
	}
	
	private static void initServletMapping() {
//		servletMapping.put("/myweb/reg", new RegServlet());
//		servletMapping.put("/myweb/login", new LoginServlet());
//		servletMapping.put("/myweb/showAllUser", new ShowAllUserServlet());
		/*
		 * 解析conf/servlets.xml文件，将根标签
		 * 下所有名为<servlet>的子标签得到，然后
		 * 将每个servlet子标签中的属性path作为key
		 * 将属性className的值得到后利用反射加载
		 * 这个类的类对象并进行实例化，将实例化的
		 * 对象作为value保存到servletMapping这个
		 * Map中完成初始化。
		 */
		try {
			SAXReader reader = new SAXReader();
			Document doc = reader.read(
					new File("./conf/servlets.xml"));
			Element root = doc.getRootElement();
			List<Element> servlets = root.elements();
			for(Element servletEle : servlets) {
				String key 
					= servletEle.attributeValue("path");	
				String className 
					= servletEle.attributeValue("className");
				Class cls = Class.forName(className);
				HttpServlet servlet = (HttpServlet)cls.newInstance();
				
				servletMapping.put(key, servlet);
			}
			
			
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	/**
	 * 根据请求获取对应的Servlet
	 * @param path
	 * @return
	 */
	public static HttpServlet getServlet(String path) {
		return servletMapping.get(path);
	}
	
}









