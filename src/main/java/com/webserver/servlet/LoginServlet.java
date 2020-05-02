package com.webserver.servlet;

import java.io.File;
import java.io.RandomAccessFile;

import com.webserver.http.HttpRequest;
import com.webserver.http.HttpResponse;
/**
 * 处理登录业务
 * @author ta
 *
 */
public class LoginServlet extends HttpServlet{
	public void service(HttpRequest request,HttpResponse response) {
		System.out.println("LoginServlet:开始处理登录...");
		//1获取用户登录信息
		String username = request.getParameter("username");
		String password = request.getParameter("password");
		System.out.println("username:"+username);
		System.out.println("password:"+password);
		
		//2验证登录
		try (
			RandomAccessFile raf
				= new RandomAccessFile("user.dat","r");
		){
			for(int i=0;i<raf.length()/100;i++) {
				raf.seek(i*100);
				//读取用户名
				byte[] data = new byte[32];
				raf.read(data);
				String name = new String(data,"UTF-8").trim();
				if(name.equals(username)) {
					raf.read(data);
					String pwd = new String(data,"UTF-8").trim();
					if(pwd.equals(password)) {
						//登录成功
						forward("/myweb/login_success.html",request,response);
						return;
					}
					/*
					 * 只要用户名对了，无论密码是否匹配，都
					 * 应当停止后续读取工作。因为user.dat
					 * 文件中不存在重复的用户名，减少没有必要
					 * 的读取操作，提高性能。
					 */
					break;
				}
			}//for循环结束
			
			//登录失败
			forward("/myweb/login_fail.html", request, response);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		
		
		System.out.println("LoginServlet:处理登录完毕!");
	}
}





