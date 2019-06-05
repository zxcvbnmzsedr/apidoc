package com.ztianzeng.apidoc.swagger;

import com.ztianzeng.apidoc.swagger.models.Pet;
import org.junit.Test;

import java.io.IOException;

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

    private void assertEqualsToJson(Object objectToSerialize, String fileName) throws IOException {
        final String json = ResourceUtils.loadClassResource(getClass(), fileName);
        SerializationMatchers.assertEqualsToJson(objectToSerialize, json);
    }
}