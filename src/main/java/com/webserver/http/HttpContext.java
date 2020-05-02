package com.webserver.http;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

/**
 * HTTP协议相关定义
 * @author ta
 *
 */
public class HttpContext {
	/**
	 * Content-Type的值与资源后缀名的对应关系
	 * key:资源后缀名
	 * value:对应的Content-Type值
	 */
	private static Map<String,String> mime_mapping = new HashMap<>();
	
	static {
		initMimeMapping();
	}
	/**
	 * 初始化资源类型
	 * 读取conf/web.xml中资源类型列表，添加到mime_mapping中
	 * 可以解决大部分市面上常见文件类型与资源类型的映射关系
	 * 
	 * 异常处理原则：能处理尽量处理，处理不了抛出去
	 */
	private static void initMimeMapping() {
		try {
			SAXReader reader=new SAXReader();
			Document doc = reader.read(
				new File("conf/web.xml"));
			Element root=doc.getRootElement();
			List<Element> list=
					root.elements("mime-mapping");
			for (Element e : list) {
				String ext=e.elementTextTrim("extension");
				String type=e.elementTextTrim("mime-type");
				mime_mapping.put(ext, type);
			}
			//System.out.println(mime_mapping); 
		}catch(Exception e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
	}
	
	/**
	 * 根据资源后缀名获取对应的Content-Type的值
	 * @param ext
	 * @return
	 */
	public static String getMimeType(String ext) {
		return mime_mapping.get(ext);
	}
	
	
	public static void main(String[] args) {
		String fileName = "header.css";
		String ext = fileName.substring(
				fileName.lastIndexOf(".")+1);
		System.out.println(getMimeType(ext));
		
	}
}









