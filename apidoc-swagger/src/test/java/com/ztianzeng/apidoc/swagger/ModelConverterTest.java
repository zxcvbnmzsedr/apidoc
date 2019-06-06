package com.ztianzeng.apidoc.swagger;

import com.ztianzeng.apidoc.swagger.models.Pet;
import com.ztianzeng.apidoc.swagger.models.media.Schema;
import org.junit.Test;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.Map;

/**
 * @author zhaotianzeng
 * @version V1.0
 * @date 2019-06-05 22:22
 */
public class ModelConverterTest {
    @Test
    public void readInterface() throws IOException {
        assertEqualsToJson(readAll(Pet.class), "Pet.json");
    }

    private Map<String, Schema> readAll(Type type) {
        return ModelConverters.getInstance().readAll(type);
    }

    private void assertEqualsToJson(Object objectToSerialize, String fileName) throws IOException {
        final String json = ResourceUtils.loadClassResource(getClass(), fileName);
        SerializationMatchers.assertEqualsToJson(objectToSerialize, json);
    }
}