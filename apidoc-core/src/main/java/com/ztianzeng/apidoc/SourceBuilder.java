package com.ztianzeng.apidoc;

import com.thoughtworks.qdox.JavaProjectBuilder;
import com.thoughtworks.qdox.model.JavaClass;
import com.ztianzeng.apidoc.utils.SpringMvcUtils;

import java.io.File;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/**
 * 核心处理器
 *
 * @author zhaotianzeng
 * @version V1.0
 * @date 2019-05-27 13:13
 */
public class SourceBuilder {
	
	private JavaProjectBuilder builder;
	
	private Collection<JavaClass> javaClasses;
	
	/**
	 * 加载Java源文件到builder中
	 */
	public SourceBuilder() {
		loadJavaFiles("src");
	}
	
	/**
	 * 加载Java源文件到builder中
	 *
	 * @param uri 文件地址
	 */
	public SourceBuilder(String uri) {
		loadJavaFiles(uri);
	}
	
	/**
	 * 加载Java源文件到builder中
	 *
	 * @param uri 文件地址
	 */
	private void loadJavaFiles(String uri) {
		
		builder = new JavaProjectBuilder();
		builder.addSourceTree(new File(uri));
		
		this.javaClasses = builder.getClasses();
	}
	
	/**
	 * 获取包含@Controller的类
	 *
	 * @return controller
	 */
	public Set<JavaClass> getControllerData() {
		Set<JavaClass> apiMethodDocs = new HashSet<>();
		for (JavaClass javaClass : javaClasses) {
			if (SpringMvcUtils.isController(javaClass)) {
				apiMethodDocs.add(javaClass);
			}
		}
		return apiMethodDocs;
	}
	
	
	public JavaProjectBuilder getBuilder() {
		return builder;
	}
}
