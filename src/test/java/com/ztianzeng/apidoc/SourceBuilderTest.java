package com.ztianzeng.apidoc;

import com.ztianzeng.apidoc.model.ApiMethodDoc;
import org.junit.Test;

import java.util.List;

/**
 * @author zhaotianzeng
 * @version V1.0
 * @date 2019-05-27 13:02
 */
public class SourceBuilderTest {
    @Test
    public void test() {
        SourceBuilder sourceBuilder = new SourceBuilder();
        List<ApiMethodDoc> controllerData = sourceBuilder.getControllerData();
        for (ApiMethodDoc controllerDatum : controllerData) {
            System.out.println("url->" + controllerDatum.getUrl());
            System.out.println("method->" + controllerDatum.getRequestMethod());
            System.out.println("requestParam->");
            controllerDatum.getRequestParams().forEach(k -> System.out.println("             " + k));
            System.out.println("=======================\n\n");
        }
    }
}