package com.ztianzeng.apidoc.utils;

import com.thoughtworks.qdox.JavaProjectBuilder;
import com.thoughtworks.qdox.model.JavaClass;
import com.thoughtworks.qdox.model.JavaMethod;
import org.junit.Assert;
import org.junit.Test;

import java.io.File;
import java.util.Collection;
import java.util.List;

/**
 * @author zhaotianzeng
 * @version V1.0
 * @date 2019-07-20 16:38
 */
@Deprecated
public class QdoxUtilsTest {
	
	@Test
	public void testDeprecated() {
		JavaProjectBuilder builder = new JavaProjectBuilder();
		File file = new File("src/test/java/com/ztianzeng/apidoc/utils/QdoxUtilsTest.java");
		builder.addSourceTree(file);
		
		Collection<JavaClass> classes = builder.getClasses();
		JavaClass aClass = classes.stream().findFirst().orElse(null);
		boolean present = QdoxUtils.getAnnotation(aClass, Deprecated.class).isPresent();
		Assert.assertTrue(present);
		List<JavaMethod> methods = aClass.getMethods();
		
		Assert.assertTrue(QdoxUtils.getAnnotation(methods.get(1), Deprecated.class).isPresent());
		Assert.assertFalse(QdoxUtils.getAnnotation(methods.get(2), Deprecated.class).isPresent());
		
	}
	
	/**
	 *
	 */
	@Deprecated
	public void method1() {
	
	}
	
	/**
	 *
	 */
	public void method() {
	
	}
	
	
}