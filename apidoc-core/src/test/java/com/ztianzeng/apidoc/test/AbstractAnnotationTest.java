package com.ztianzeng.apidoc.test;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.JsonNode;
import com.ztianzeng.apidoc.Reader;
import com.ztianzeng.apidoc.models.OpenAPI;
import com.ztianzeng.apidoc.test.swagger.SerializationMatchers;
import com.ztianzeng.apidoc.utils.Yaml;
import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import static junit.framework.TestCase.fail;


public abstract class AbstractAnnotationTest {




    public void compareAsYaml(final String actualYaml, final String expectedYaml) throws IOException {
        SerializationMatchers.assertEqualsToYaml(Yaml.mapper().readValue(actualYaml, OpenAPI.class), expectedYaml);
    }

    public void compareAsJson(final String actualJson, final String expectedJson) throws IOException {
        SerializationMatchers.assertEqualsToJson(Yaml.mapper().readValue(actualJson, OpenAPI.class), expectedJson);
    }

    protected String getOpenAPIAsString(final String file) throws IOException {
        InputStream in = null;
        try {
            in = getClass().getClassLoader().getResourceAsStream(file);
            return IOUtils.toString(in, StandardCharsets.UTF_8);
        } finally {
            IOUtils.closeQuietly(in);
        }
    }
}
