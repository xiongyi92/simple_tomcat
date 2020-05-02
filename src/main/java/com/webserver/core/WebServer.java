package com.webserver.core;

import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * WebServer主类
 * @author ta
 *
 */
public class WebServer {
	private ServerSocket server;
	
	private ExecutorService threadPool;
	
	public WebServer() {
		try {
			System.out.println("正在启动服务端...");
			//新建一个socket进行监听网络请求
			server = new ServerSocket(8089);
			//创建一个线程池提高并发度
			threadPool = Executors.newFixedThreadPool(8);
			System.out.println("服务端启动完毕!");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void start() {
		try {
			while(true) {
				System.out.println("等待客户端连接...");
				//socket监听请求
				Socket socket = server.accept();
				//接收到一个请求后，将socket传到ClientHandler中进行处理
				System.out.println("一个客户端连接了!");
				ClientHandler handler 
					= new ClientHandler(socket);
				//放到线程池中
				threadPool.execute(handler);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) {
		WebServer server = new WebServer();
		server.start();
	}
}











