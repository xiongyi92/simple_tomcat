package com.webserver.servlet;

import java.io.File;
import java.io.RandomAccessFile;
import java.util.Arrays;

import com.webserver.http.HttpRequest;
import com.webserver.http.HttpResponse;

/**
 * 处理用户注册业务   
 * @author ta
 *
 */
public class RegServlet extends HttpServlet {
	
	public void service(HttpRequest request,HttpResponse response) {
		System.out.println("RegServlet:开始处理注册...");
		//1通过request获取用户的注册信息
		String username = request.getParameter("username");
		String password = request.getParameter("password");
		String nickname = request.getParameter("nickname");
		int age = Integer.parseInt(request.getParameter("age"));
		System.out.println("username:"+username);
		System.out.println("password:"+password);
		System.out.println("nickname:"+nickname);
		System.out.println("age:"+age);
		//2将注册信息写入文件user.dat		
		try (
			RandomAccessFile raf
				= new RandomAccessFile("user.dat","rw");	
		){
			/*
			 * 首先判断user.dat文件中是否已经存在该
			 * 用户，若存在则响应用户注册提示页面，
			 * 提示该用户已存在，否则才将该用户信息
			 * 写入user.dat文件
			 * 
			 * 注册提示页面:reg_have_user.html
			 * 提示也中显示一行字，内容为:该用户已存在，请重写注册!
			 */
			for(int i=0;i<raf.length()/100;i++) {
				raf.seek(i*100);
				byte[] data = new byte[32];
				raf.read(data);
				String name = new String(data,"UTF-8").trim();
				if(name.equals(username)) {
					//该用户已存在
					forward("/myweb/reg_have_user.html", request, response);
					return;
				}
			}
			
			
			
			
			
			//先将指针移动到文件末尾
			raf.seek(raf.length());

			//写用户名
			byte[] data = username.getBytes("UTF-8");
			//将字节数组扩容至32字节
			data = Arrays.copyOf(data, 32);
			//将32字节一次性写入文件
			raf.write(data);
			
			//写密码
			data = password.getBytes("UTF-8");
			data = Arrays.copyOf(data, 32);
			raf.write(data);
			
			
			//写昵称
			data = nickname.getBytes("UTF-8");
			data = Arrays.copyOf(data, 32);
			raf.write(data);

			
			//写年龄
			raf.writeInt(age);
		
			//注册完毕,响应注册成功页面给客户端
			forward("/myweb/reg_success.html", request, response);
			
			
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		
		
		
		
		//3设置response响应客户端注册结果
		
		
		System.out.println("RegServlet:处理注册完毕!");
	}
}







