package com.ztianzeng.apidoc;

import com.thoughtworks.qdox.JavaProjectBuilder;
import com.thoughtworks.qdox.model.JavaClass;
import com.ztianzeng.apidoc.model.ApiMethodDoc;
import com.ztianzeng.apidoc.model.Parameters;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

/**
 * @author zhaotianzeng
 * @version V1.0
 * @date 2019-05-27 13:02
 */
public class SourceBuilderTest {
    private SourceBuilder sourceBuilder;
    private JavaProjectBuilder builder;

    @Before
    public void setUp() {
        sourceBuilder = new SourceBuilder("src/test/java");
        builder = sourceBuilder.getBuilder();
    }

    @Test
    public void test() throws ClassNotFoundException {
        List<ApiMethodDoc> controllerData = sourceBuilder.getControllerData();
        for (ApiMethodDoc controllerDatum : controllerData) {
            System.out.println("url->" + controllerDatum.getUrl());
            System.out.println("method->" + controllerDatum.getRequestMethod());
            System.out.println("description->" + controllerDatum.getDescription());
            System.out.println("contentType->" + controllerDatum.getContentType());
            System.out.println("requestParam->");
            controllerDatum.getRequestParams().forEach(k -> System.out.println("             " + k));


            System.out.println("=======================\n\n");
        }
    }

    @Test
    public void getControllerData() {
    }

    @Test
    public void buildControllerMethod() {
    }

    /**
     * list对象解析
     */
    @Test
    public void parsingList() {
        JavaClass cls = builder.getClassByName("com.ztianzeng.apidoc.test.ListParam");
        List<Parameters> parameters = sourceBuilder.parsingBody(cls);
        System.out.println(parameters);
        for (Parameters parameter : parameters) {
            Assert.assertNotNull(parameter.getName());

        }
    }

    /**
     * 嵌套list对象解析
     */
    @Test
    public void parsingList2() {
        JavaClass cls = builder.getClassByName("com.ztianzeng.apidoc.test.ListParam2");
        List<Parameters> parameters = sourceBuilder.parsingBody(cls);
        System.out.println(parameters);
        for (Parameters parameter : parameters) {
            Assert.assertNotNull(parameter.getName());

        }
    }

    /**
     * 嵌套对象解析
     */
    @Test
    public void parsingBody() {
        JavaClass cls = builder.getClassByName("com.ztianzeng.apidoc.test.CreateParam");
        List<Parameters> parameters = sourceBuilder.parsingBody(cls);
        System.out.println(parameters);
        for (Parameters parameter : parameters) {
            Assert.assertNotNull(parameter.getName());

        }
    }

    /**
     * 测试普通对象解析
     */
    @Test
    public void parsingBody2() {
        JavaClass cls = builder.getClassByName("com.ztianzeng.apidoc.test.CreateParam2");
        List<Parameters> parameters = sourceBuilder.parsingBody(cls);
        System.out.println(parameters);
        Assert.assertEquals(parameters.size(), 2);

        for (Parameters parameter : parameters) {
            Assert.assertNotNull(parameter.getName());
        }

    }


}