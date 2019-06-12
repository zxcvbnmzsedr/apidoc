package com.ztianzeng.apidoc.test;


import com.thoughtworks.qdox.model.JavaClass;
import com.ztianzeng.apidoc.Reader;
import com.ztianzeng.apidoc.models.OpenAPI;
import com.ztianzeng.apidoc.models.media.ArraySchema;
import com.ztianzeng.apidoc.models.media.Schema;
import com.ztianzeng.apidoc.models.parameters.Parameter;
import com.ztianzeng.apidoc.test.res.MyBeanParamResource;
import org.junit.Assert;
import org.junit.Test;

import java.util.List;

public class BeanParamTest {


    @Test
    public void shouldSerializeTypeParameter() {
        JavaClass classByName = TestBase.builder.getClassByName(MyBeanParamResource.class.getName());
        OpenAPI openApi = new Reader(new OpenAPI()).read(classByName);
        List<Parameter> getOperationParams = openApi.getPaths().get("/").getGet().getParameters();
        Assert.assertEquals(getOperationParams.size(), 1);
        Parameter param = getOperationParams.get(0);
        Assert.assertEquals(param.getName(), "listOfStrings");
        Schema<?> schema = param.getSchema();
        // These are the important checks:
        Assert.assertEquals(schema.getClass(), ArraySchema.class);
        Assert.assertEquals(((ArraySchema) schema).getItems().getType(), "string");
    }

}