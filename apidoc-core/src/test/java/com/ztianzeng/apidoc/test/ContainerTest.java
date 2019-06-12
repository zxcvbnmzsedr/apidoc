package com.ztianzeng.apidoc.test;


import com.thoughtworks.qdox.model.JavaClass;
import com.ztianzeng.apidoc.ModelResolver;
import com.ztianzeng.apidoc.converter.AnnotatedType;
import com.ztianzeng.apidoc.converter.ModelConverterContextImpl;
import com.ztianzeng.apidoc.models.media.ArraySchema;
import com.ztianzeng.apidoc.models.media.Schema;
import com.ztianzeng.apidoc.test.res.CreateParam;
import com.ztianzeng.apidoc.test.res.InnerType;
import com.ztianzeng.apidoc.test.res.Result;
import org.junit.Test;

import java.util.Calendar;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;


public class ContainerTest extends TestBase {

    @Test
    public void testArray() throws Exception {
        final ModelResolver modelResolver = new ModelResolver(sourceBuilder);

        final ModelConverterContextImpl context = new ModelConverterContextImpl(modelResolver);
        JavaClass classByName = TestBase.builder.getClassByName(ArrayBean.class.getName());

        final Schema model = context
                .resolve(new AnnotatedType(classByName));

        final Map<String, Schema> props = model.getProperties();
        assertEquals(1, props.size());
        final Schema prop = props.get("a");
        assertNotNull(prop);
        assertEquals(prop.getType(), "array");

        final Schema items = ((ArraySchema) prop).getItems();
        assertNotNull(items);
        assertEquals(items.getType(), "integer");
    }

    @Test
    public void testMap() throws Exception {
        final ModelResolver modelResolver = new ModelResolver(sourceBuilder);
        final ModelConverterContextImpl context = new ModelConverterContextImpl(modelResolver);
        JavaClass classByName = TestBase.builder.getClassByName(MapBean.class.getName());

        final Schema model = context
                .resolve(new AnnotatedType(classByName));

        final Map<String, Schema> props = model.getProperties();
        assertEquals(1, props.size());
        final Schema prop = props.get("stuff");
        assertNotNull(prop);
        assertEquals(prop.getType(), "object");

        final Schema items = (Schema) prop.getAdditionalProperties();
        assertNotNull(items);
        assertEquals(items.getType(), "date-time");
    }

    @Test
    public void testComplexMap() throws Exception {
        ModelResolver resolver = new ModelResolver(sourceBuilder);

        final ModelConverterContextImpl context = new ModelConverterContextImpl(resolver);
        JavaClass classByName = TestBase.builder.getClassByName(WrapperType.class.getName());
        context.resolve(new AnnotatedType(classByName));

        final Map<String, Schema> models = context.getDefinedModels();
        final Schema innerType = models.get("InnerType");
        assertNotNull(innerType);
        final Map<String, Schema> innerProps = innerType.getProperties();
        assertEquals(innerProps.size(), 2);
        final Schema foo = innerProps.get("foo");
        assertEquals(foo.getType(), "integer");
        assertEquals(foo.getFormat(), "int32");
        final Schema name = innerProps.get("name");
        assertEquals(name.getType(), "string");

        final Schema wrapperType = models.get("WrapperType");
        assertNotNull(wrapperType);
        assertEquals(((Schema) wrapperType.getProperties().get("innerType")).getType(), "object");
    }


    @Test
    public void testResult() {
        ModelResolver resolver = new ModelResolver(sourceBuilder);

        final ModelConverterContextImpl context = new ModelConverterContextImpl(resolver);
        JavaClass classByName = TestBase.builder.getClassByName(ResultBean.class.getName());
        context.resolve(new AnnotatedType(classByName));

        final Map<String, Schema> models = context.getDefinedModels();
        final Schema createParam = models.get("CreateParam");
        assertNotNull(createParam);
        final Map<String, Schema> innerProps = createParam.getProperties();
        assertEquals(innerProps.size(), 2);
        final Schema foo = innerProps.get("username");
        assertEquals(foo.getType(), "string");
        final Schema name = innerProps.get("mobile");
        assertEquals(name.getType(), "string");


    }

    static class ArrayBean {
        public int[] a;
    }

    static class MapBean {
        public Map<Short, Calendar> stuff;
    }

    static class WrapperType {
        public Map<String, InnerType> innerType;
    }

    static class ResultBean {
        public Result<CreateParam> result;
    }

}
