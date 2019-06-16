package com.ztianzeng.apidoc.test;


import com.thoughtworks.qdox.model.JavaClass;
import com.ztianzeng.apidoc.ModelConverters;
import com.ztianzeng.apidoc.converter.AnnotatedType;
import com.ztianzeng.apidoc.test.res.Person;
import com.ztianzeng.apidoc.test.res.Pet;
import com.ztianzeng.apidoc.models.media.Schema;
import com.ztianzeng.apidoc.test.swagger.ResourceUtils;
import com.ztianzeng.apidoc.test.swagger.SerializationMatchers;
import org.junit.Test;

import java.io.IOException;
import java.util.Map;

/**
 * @author zhaotianzeng
 * @version V1.0
 * @date 2019-06-05 22:22
 */
public class ModelConverterTest {
    @Test
    public void readModel() throws IOException {
        JavaClass classByName = TestBase.builder.getClassByName(Pet.class.getName());
        assertEqualsToJson(readAll(classByName), "Pet.json");
    }

    @Test
    public void convertModel() throws IOException {
        JavaClass classByName = TestBase.builder.getClassByName(Person.class.getName());

        assertEqualsToJson(read(classByName), "Person.json");
    }

    private Map<String, Schema> read(JavaClass type) {
        return ModelConverters.getInstance().read(type);
    }

    private Map<String, Schema> readAll(JavaClass type) {
        return ModelConverters.getInstance().readAll(new AnnotatedType(type));
    }

    private void assertEqualsToJson(Object objectToSerialize, String fileName) throws IOException {
        final String json = ResourceUtils.loadClassResource(getClass(), fileName);
        SerializationMatchers.assertEqualsToJson(objectToSerialize, json);
    }
}