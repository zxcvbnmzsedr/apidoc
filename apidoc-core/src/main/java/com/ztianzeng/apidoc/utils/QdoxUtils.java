package com.ztianzeng.apidoc.utils;

import com.thoughtworks.qdox.model.JavaAnnotatedElement;
import com.thoughtworks.qdox.model.JavaAnnotation;

import java.lang.reflect.Type;
import java.util.Optional;

/**
 * @author zhaotianzeng
 * @version V1.0
 * @date 2019-07-20 16:31
 */
public final class QdoxUtils {
	private QdoxUtils() {
	
	}
	
	/**
	 * 获取注解
	 *
	 * @param annotatedElement 类型
	 * @param name             完整类名
	 */
	public static Optional<JavaAnnotation> getAnnotation(JavaAnnotatedElement annotatedElement, String name) {
		for (JavaAnnotation annotation : annotatedElement.getAnnotations()) {
			if (annotation.getType().isA(name)) {
				return Optional.of(annotation);
			}
		}
		return Optional.empty();
	}
	
	/**
	 * 获取注解
	 *
	 * @param annotatedElement 类型
	 * @param type             java类
	 */
	public static Optional<JavaAnnotation> getAnnotation(JavaAnnotatedElement annotatedElement, Type type) {
		return getAnnotation(annotatedElement, type.getTypeName());
	}
	
}