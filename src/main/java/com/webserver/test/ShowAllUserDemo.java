package com.webserver.test;

import java.io.IOException;
import java.io.RandomAccessFile;

/**
 * 显示所有员工信息
 * @author ta
 *
 */
public class ShowAllUserDemo {
	public static void main(String[] args) throws IOException {
		/*
		 * 使用RAF读取user.dat文件
		 * 循环读取该文件，循环次数应当是文件长度/100
		 * 每条记录读取时:
		 * 首先读取32字节，这是用户名，将该字节按照
		 * UTF-8编码转换为字符串，注意，转换后要trim,
		 * 因为这个字符串含有空白字符。
		 * 依次类推读取密码，昵称。
		 * 之后再读取一个int值，这个是年龄。
		 * 
		 * 输出格式例如:
		 * 张三,123456,阿三,22
		 */
		
		RandomAccessFile raf
			= new RandomAccessFile("user.dat","r");
		
		for(int i=0;i<raf.length()/100;i++) {
			//读取用户名
			byte[] data = new byte[32];
			raf.read(data);
			String username = new String(data,"UTF-8").trim();
			
			raf.read(data);
			String password = new String(data,"UTF-8").trim();
			
			raf.read(data);
			String nickname = new String(data,"UTF-8").trim();
			
			int age = raf.readInt();
			System.out.println("pos:"+raf.getFilePointer());
			System.out.println(username+","+password+","+nickname+","+age);
		}
		
		raf.close();
	}
}






