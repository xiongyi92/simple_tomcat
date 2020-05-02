package com.webserver.test;

import java.io.UnsupportedEncodingException;
import java.util.Arrays;

public class Test {

	public static void main(String[] args) throws UnsupportedEncodingException {
		String str = "范";
		byte[] data = str.getBytes("UTF-8");
		System.out.println(Arrays.toString(data));
	}

}
