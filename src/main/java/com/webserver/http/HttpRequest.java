package com.webserver.http;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.Socket;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.Map;

import com.webserver.exception.EmptyRequestException;

/**
 * 请求对象
 * 请求对象的每一个实例用于表示客户端(浏览器)发送过来
 * 的一个标准HTTP请求内容
 * 
 * 一个请求由三部分构成:请求行，消息头，消息正文
 * @author ta
 *
 */
public class HttpRequest {
	/*
	 * 请求行相关信息定义
	 */
	//请求方式
	private String method;
	//请求的抽象路径部分
	private String url;
	//请求使用的HTTP协议版本
	private String protocol;
	/*
	 * 由于请求行中抽象路径部分因客户端请求方式
	 * 不同会有不同内容:
	 * 1:不带参数的抽象路径，如:
	 *   /myweb/index.html
	 * 2:form表单GET形式提交，抽象路径就带参数，如:
	 *   /myweb/reg?username=xxx&password=xxx&...  
	 */
	//抽象路径中的请求部分。"?"左侧内容
	private String requestURI;
	//抽象路径中的参数部分。"?"右侧内容
	private String queryString;
	//保存具体的每一组参数
	private Map<String,String> parameters = new HashMap<>();
	
	
	/*
	 * 消息头相关信息定义
	 */
	//key:消息头的名字   value:消息头对应的值
	private Map<String,String> headers = new HashMap<>();
	
	
	
	/*
	 * 消息正文相关信息定义
	 */
	
	/*
	 * 与连接相关的属性
	 */
	private Socket socket;
	private InputStream in;
	/**
	 * 构造方法，用来初始化请求对象
	 * 初始化的过程就是解析请求的过程。
	 * @throws EmptyRequestException 
	 */
	public HttpRequest(Socket socket) throws EmptyRequestException {
		try {
			this.socket = socket;
			/*
			 * 通过socket获取输入流，用于读取客户端
			 * 发送过来的请求内容
			 */			
			this.in = socket.getInputStream();
			/*
			 * 解析请求分为三步
			 * 1:解析请求行内容
			 * 2:解析消息头内容
			 * 3:解析消息正文内容
			 */
			parseRequestLine();
			parseHeaders();
			parseContent();
		}catch(EmptyRequestException e) {
			throw e;
		}catch(Exception e) {
			e.printStackTrace();
		}
		
	}
	/**
	 * 解析请求行
	 * @throws EmptyRequestException 
	 */
	private void parseRequestLine() throws EmptyRequestException {
		System.out.println("开始解析请求行...");
		try {
			String line = readLine();
			//是否为空请求
			if("".equals(line)) {
				//抛出空请求异常
				throw new EmptyRequestException();
			}
			
			System.out.println("请求行:"+line);
			/*
			 * 将请求行的内容按照空格拆分为三部分，
			 * 并分别设置到属性：method,url,protocol上
			 */
			String[] data = line.split("\\s");
			method = data[0];
			url = data[1];
			protocol = data[2];
			//进一步解析抽象路径部分
			parseURL();
			
			System.out.println("method:"+method);
			System.out.println("url:"+url);
			System.out.println("protocol:"+protocol);
			
		} catch(EmptyRequestException e) {
			throw e;
		} catch (Exception e) {
			e.printStackTrace();
		}
		System.out.println("解析请求行完毕");
	}
	/**
	 * 进一步解析请求行中的抽象路径部分
	 */
	private void parseURL() {
		System.out.println("进一步解析抽象路径部分...");
		/*
		 *  在解析url时，首先判断该抽象路径是否
		 *  含有参数，标志就是有没有"?"。
		 *  如果没有则说明没有参数，那么直接将当前
		 *  抽象路径url的值赋值给requestURI即可
		 *  
		 *  如果有则说明有参数，那么首先按照"?"
		 *  将抽象路径拆分为两部分，第一部分就是
		 *  请求部分，赋值给requestURI,第二部分
		 *  就是参数部分赋值给queryString.
		 *  然后在对queryString部分进行进一步拆分
		 *  按照"&"先拆分出每一个参数，然后每个参数
		 *  再按照"="拆分为参数名与参数值，只有将
		 *  参数名作为key,参数值作为value保存到
		 *  属性parameters这个Map中完成解析工作
		 */
		if(url.indexOf("?")==-1) {
			//没有参数
			requestURI = url;
			
		}else {
			//有参数
			String[] data = url.split("\\?");
			requestURI = data[0];
			//判断"?"右侧确实包含参数部分
			if(data.length>1) {
				queryString = data[1];
				//对queryString解码
				try {
					/*
					 * String decode(String str,String enc)
					 * 将给定的字符串中含有"%XX"的内容按照指定
					 * 的字符集还原为对应字符串并替换这些"%XX",
					 * 然后将替换后的字符串返回。
					 */
					queryString = URLDecoder.decode(queryString, "UTF-8");
				} catch (UnsupportedEncodingException e) {
					e.printStackTrace();
				}
			
				//拆分每一个参数
				parseParameters(queryString);
				
			}
		}
		
		
		
		
		
		System.out.println("requestURI:"+requestURI);
		System.out.println("queryString:"+queryString);
		System.out.println("parameters:"+parameters);
		System.out.println("解析抽象路径部分完毕!");
	}
	/**
	 * 解释参数
	 * 参数的格式应当为:name=value&name=value&...
	 * @param line
	 */
	private void parseParameters(String line) {
		String[] data = line.split("&");
		for(String para : data) {
			//每个参数按照"="拆分
			String[] arr = para.split("=");
			if(arr.length>1) {
				parameters.put(arr[0], arr[1]);
			}else {
				parameters.put(arr[0], null);
			}
		}
	}
	
	/**
	 * 解析消息头
	 */
	private void parseHeaders(){
		System.out.println("开始解析消息头...");
		try {
			/*
			 * 消息头有若干行，因此我们应当循环调用
			 * readLine方法读取每一个消息头。但是
			 * 如果调用readLine方法返回值为""(空字
			 * 符串)时则说明单独读取到了CRLF，此时
			 * 表示消息头全部解析完毕，应当停止读取
			 * 工作。
			 * 每个消息头读取到以后，按照冒号空格(: )
			 * 进行拆分，并将拆分的第一项作为消息头的
			 * 名字，第二项作为消息头的值并以key,value
			 * 形式保存到属性headers这个Map中完成消息
			 * 头的解析工作
			 */
			String line = null;
			while(!"".equals(line = readLine())) {
				String[] data = line.split(": ");
				headers.put(data[0], data[1]);
			}	
			System.out.println("headers:" +headers);
		} catch (Exception e) {
			e.printStackTrace();
		}
		System.out.println("解析消息头完毕");
	}
	/**
	 * 解析消息正文
	 */
	private void parseContent() {
		System.out.println("开始解析消息正文...");
		/*
		 * 一个请求是否包含消息正文可以根据这个请求
		 * 发送过来的消息头中是否包含：
		 * Content-Length来判定。如果包含这个头
		 * 就说明是含有正文的，并且指定了正文的长度
		 * 
		 * Content-Type头是客户端告知服务端消息
		 * 正文的内容是什么类型的数据。
		 * 
		 * Content-Type的值若为:
		 * application/x-www-form-urlencoded
		 * 则表明该正文是一个字符串，内容是页面表单
		 * 提交上来的用户输入的内容，格式与GET请求
		 * 中在url的"?"右侧内容一致。如:
		 * username=zhangsan&password=123456
		 * 
		 * 
		 * 1:首先在解析消息正文时先判断本次请求的
		 *   消息头中是否含有Content-Length
		 *   如果含有则说明包含消息正文，没有则忽略
		 *   解析消息正文的工作
		 * 2:获取消息头Content-Length的值，这是一个
		 *   数字，用来得知消息正文的长度(字节量)
		 * 3:通过输入流读取Content-Length指定的字节
		 *   量将消息正文数据读取出来。
		 * 4:在获取Content-Type头的值，根据这个值来
		 *   判定消息正文的数据类型，这里只判断一种:
		 *   application/x-www-form-urlencoded
		 *   若是上述的值，则说明这个正文内容是页面表单
		 *   提交上来的用户数据，将其转换为字符串,字符
		 *   集使用ISO8859-1。
		 * 5:转换后的字符串就可以进行拆分参数了，将
		 *   拆分后的参数再次存入parameters这个Map中        
		 * 
		 */
		try {
			if(headers.containsKey("Content-Length")) {
				int length = Integer.parseInt(
						headers.get("Content-Length"));
				byte[] data = new byte[length];
				//读取所有正文内容的字节
				in.read(data);
				//获取正文类型
				String type = headers.get("Content-Type");
				
				//判断是否为form表单数据
				if("application/x-www-form-urlencoded".equals(type)) {
					String line = new String(data,"ISO8859-1");
					//转码
					line = URLDecoder.decode(line,"UTF-8");
					//拆分出每一个参数
					parseParameters(line);
				}
				
				
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		
		System.out.println("解析消息正文完毕");
	}
	
	/**
	 * 通过输入流读取客户端发送过来的一行字符串
	 * 一行结束的标志为CR,LF
	 * @return
	 * @throws IOException 
	 */
	private String readLine() throws IOException {
		StringBuilder builder = new StringBuilder();
		//c1表示上次读取到的字符，c2表示本次读取到的字符
		int c1=-1,c2=-1;
		while((c2 = in.read())!=-1) {				
			//是否连读读取到了CR,LF
			if(c1==13&&c2==10) {
				break;
			}
			builder.append((char)c2);
			c1 = c2;
		}
		return builder.toString().trim();
	}
	
	
	
	public String getMethod() {
		return method;
	}
	public String getUrl() {
		return url;
	}
	public String getProtocol() {
		return protocol;
	}
	/**
	 * 根据消息头的名字获取消息头对应的值
	 * @param name
	 * @return
	 */
	public String getHeaders(String name) {
		/*
		 * 这里的设计没有直接将headers这个Map对外
		 * 范围，避免外界拿到这个Map后可以任意操作，
		 * 这样破坏了当前属性的封装性。
		 * 实际开发中，很多这种集合，Map的属性都不
		 * 对外直接返回。
		 */
		return headers.get(name);
	}
	public String getRequestURI() {
		return requestURI;
	}
	public String getQueryString() {
		return queryString;
	}
	/**
	 * 获取指定参数对应的值
	 * @param name
	 * @return
	 */
	public String getParameter(String name) {
		return parameters.get(name);
	}
	
	
	
	
	
	
	
}









