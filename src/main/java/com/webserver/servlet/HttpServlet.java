package com.webserver.servlet;

import java.io.File;

import com.webserver.http.HttpRequest;
import com.webserver.http.HttpResponse;

/**
 * 所有Servlet的超类
 * @author ta
 *
 */
public abstract class HttpServlet {
	/**
	 * 用于处理请求的方法。ClientHandler在调用
	 * 某请求对应的处理类(某Servlet)时，会调用
	 * 其service方法。
	 * @param request
	 * @param response
	 */
	public abstract void service(HttpRequest request,HttpResponse response);

	/**
	 * 跳转到指定页面
	 * @param path 从webapps之后开始指定路径,
	 *             如"/myweb/xxx.html"
	 * @param request
	 * @param response
	 */
	public void forward(String path,HttpRequest request,HttpResponse response) {
		File file = new File("./webapps"+path);
		response.setEntity(file);
	}
}









