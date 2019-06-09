package com.ztianzeng.apidoc;



import com.ztianzeng.apidoc.models.Person;
import com.ztianzeng.apidoc.models.Pet;
import com.ztianzeng.apidoc.models.media.Schema;
import com.ztianzeng.apidoc.swagger.ModelConverters;
import com.ztianzeng.apidoc.swagger.ResourceUtils;
import com.ztianzeng.apidoc.swagger.SerializationMatchers;
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
    public void readModel() throws IOException {
        assertEqualsToJson(readAll(Pet.class), "Pet.json");
    }

    @Test
    public void convertModel() throws IOException {
        assertEqualsToJson(read(Person.class), "Person.json");
    }

    private Map<String, Schema> read(Type type) {
        return ModelConverters.getInstance().read(type);
    }

    private Map<String, Schema> readAll(Type type) {
        return ModelConverters.getInstance().readAll(type);
    }

    private void assertEqualsToJson(Object objectToSerialize, String fileName) throws IOException {
        final String json = ResourceUtils.loadClassResource(getClass(), fileName);
        SerializationMatchers.assertEqualsToJson(objectToSerialize, json);
    }
}