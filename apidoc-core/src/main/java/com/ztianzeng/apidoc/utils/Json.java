package com.ztianzeng.apidoc.utils;

import com.fasterxml.jackson.core.util.DefaultPrettyPrinter;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;

import java.io.File;
import java.io.IOException;

/**
 * JSON输出工具类
 *
 * @author tianzeng
 */
public class Json {
	
	private static ObjectMapper mapper;
	
	/**
	 * 默认的JSON
	 */
	private Json() {
	
	}
	
	/**
	 * default mapper
	 *
	 * @return
	 */
	public static ObjectMapper mapper() {
		if (mapper == null) {
			mapper = ObjectMapperFactory.createJson();
		}
		return mapper;
	}
	
	/**
	 * pretty
	 */
	private static ObjectWriter pretty() {
		return mapper().writer(new DefaultPrettyPrinter());
	}
	
	/**
	 * 输出String
	 *
	 * @param o o
	 * @return
	 */
	public static String pretty(Object o) {
		try {
			return pretty().writeValueAsString(o);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	/**
	 * 输出到控制台
	 *
	 * @param o
	 */
	public static void prettyPrint(Object o) {
		try {
			System.out.println(pretty().writeValueAsString(o).replace("\r", ""));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 输出到文件
	 *
	 * @param filePath 文件路径
	 * @param o        类
	 * @throws IOException
	 */
	public static void pretty(String filePath, Object o) throws IOException {
		File file = new File(filePath);
		mapper().writeValue(file, o);
	}
}
