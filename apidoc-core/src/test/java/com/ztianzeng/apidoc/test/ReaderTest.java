package com.ztianzeng.apidoc.test;

import com.thoughtworks.qdox.model.JavaClass;
import com.ztianzeng.apidoc.Reader;
import com.ztianzeng.apidoc.models.OpenAPI;
import com.ztianzeng.apidoc.models.Operation;
import com.ztianzeng.apidoc.models.PathItem;
import com.ztianzeng.apidoc.models.Paths;
import com.ztianzeng.apidoc.models.info.Info;
import com.ztianzeng.apidoc.models.media.ArraySchema;
import com.ztianzeng.apidoc.test.res.*;
import com.ztianzeng.apidoc.test.swagger.SerializationMatchers;
import com.ztianzeng.apidoc.utils.Yaml;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * @author zhaotianzeng
 * @version V1.0
 * @date 2019-06-07 12:00
 */
public class ReaderTest {
    private static final String OPERATION_DESCRIPTION = "Operation Description";
    private static final String OPERATION_SUMMARY = "Operation Summary";
    private static final String PATH_1_REF = "/1";
    private static final int RESPONSES_NUMBER = 1;
    private static final int TAG_NUMBER = 2;
    private static final int SECURITY_SCHEMAS = 2;
    private static final int PARAMETER_NUMBER = 1;
    private static final int SECURITY_REQUIREMENT_NUMBER = 1;
    private static final int SCOPE_NUMBER = 2;
    private static final int PATHS_NUMBER = 1;
    private static final String EXAMPLE_TAG = "Example Tag";
    private static final String SECOND_TAG = "Second Tag";
    private static final String CALLBACK_POST_OPERATION_DESCRIPTION = "payload data will be sent";
    private static final String CALLBACK_GET_OPERATION_DESCRIPTION = "payload data will be received";
    private static final String RESPONSE_CODE_200 = "200";
    private static final String RESPONSE_DESCRIPTION = "voila!";
    private static final String EXTERNAL_DOCS_DESCRIPTION = "External documentation description";
    private static final String EXTERNAL_DOCS_URL = "http://url.com";
    private static final String PARAMETER_IN = "path";
    private static final String PARAMETER_NAME = "subscriptionId";
    private static final String PARAMETER_DESCRIPTION = "parameter description";
    private static final String CALLBACK_SUBSCRIPTION_ID = "subscription";
    private static final String CALLBACK_URL = "http://$request.query.url";
    private static final String SECURITY_KEY = "security_key";
    private static final String SCOPE_VALUE1 = "write:pets";
    private static final String SCOPE_VALUE2 = "read:pets";
    private static final String PATH_REF = "/";
    private static final String PATH_2_REF = "/path";
    private static final String SCHEMA_TYPE = "string";
    private static final String SCHEMA_FORMAT = "uuid";
    private static final String SCHEMA_DESCRIPTION = "the generated UUID";

    @Test
    public void testSimpleReadClass() {
        JavaClass classByName = TestBase.builder.getClassByName(BasicFieldsResource.class.getName());
        Reader reader = new Reader(new OpenAPI());
        OpenAPI openAPI = reader.read(classByName);
        Paths paths = openAPI.getPaths();
        assertEquals(paths.size(), 6);
        PathItem pathItem = paths.get(PATH_1_REF);
        assertNotNull(pathItem);
        assertNull(pathItem.getPost());
        Operation operation = pathItem.getGet();
        assertNotNull(operation);
        assertEquals(OPERATION_SUMMARY, operation.getSummary());
        assertEquals(OPERATION_DESCRIPTION, operation.getDescription());
    }


    @Test
    public void testResolveDuplicatedOperationId() {
        Reader reader = new Reader(new OpenAPI());
        JavaClass classByName = TestBase.builder.getClassByName(DuplicatedOperationIdResource.class.getName());
        OpenAPI openAPI = reader.read(classByName);

        Paths paths = openAPI.getPaths();
        assertNotNull(paths);
        Operation firstOperation = paths.get(PATH_REF).getGet();
        Operation secondOperation = paths.get(PATH_2_REF).getGet();
        Operation thirdOperation = paths.get(PATH_REF).getPost();
        assertNotNull(firstOperation);
        assertNotNull(secondOperation);
        assertNotNull(thirdOperation);
        assertNotEquals(firstOperation.getOperationId(), secondOperation.getOperationId());
        assertNotEquals(firstOperation.getOperationId(), thirdOperation.getOperationId());
        assertNotEquals(secondOperation.getOperationId(), thirdOperation.getOperationId());
    }

    @Test
    public void testResolveDuplicatedOperationIdMethodName() {
        Reader reader = new Reader(new OpenAPI());
        JavaClass classByName = TestBase.builder.getClassByName(DuplicatedOperationMethodNameResource.class.getName());

        OpenAPI openAPI = reader.read(classByName);

        Paths paths = openAPI.getPaths();
        assertNotNull(paths);
        Operation firstOperation = paths.get("/1").getGet();
        Operation secondOperation = paths.get("/2").getGet();
        Operation secondPostOperation = paths.get("/2").getPost();
        Operation thirdPostOperation = paths.get("/3").getPost();
        assertNotNull(firstOperation);
        assertNotNull(secondOperation);
        assertNotNull(secondPostOperation);
        assertNotNull(thirdPostOperation);
        assertNotEquals(firstOperation.getOperationId(), secondOperation.getOperationId());
        assertNotEquals(secondOperation.getOperationId(), secondPostOperation.getOperationId());
        assertNotEquals(secondPostOperation.getOperationId(), thirdPostOperation.getOperationId());
        Operation thirdOperation = paths.get("/3").getGet();
        Operation fourthOperation = paths.get("/4").getGet();
        assertNotNull(thirdOperation);
        assertNotNull(fourthOperation);
        assertNotEquals(thirdOperation.getOperationId(), fourthOperation.getOperationId());

    }


    @Test
    public void print() {
        Reader reader = new Reader(new OpenAPI());
        JavaClass classByName = TestBase.builder.getClassByName(TestController.class.getName());

        OpenAPI openAPI = reader.read(classByName);
        Info info = new Info();
        info.title("dddd");
        info.setVersion("111.111");
        openAPI.setInfo(info);
        Yaml.prettyPrint(openAPI);
    }

    @Test
    public void testMoreResponses() {
        Reader reader = new Reader(new OpenAPI());
        JavaClass classByName = TestBase.builder.getClassByName(EnhancedResponsesResource.class.getName());

        OpenAPI openAPI = reader.read(classByName);
        String yaml = "openapi: 3.0.1\n" +
                "paths:\n" +
                "  /:\n" +
                "    get:\n" +
                "      summary: Simple get operation\n" +
                "      description: Defines a simple get operation with no inputs and a complex output\n" +
                "        object\n" +
                "      operationId: getResponses\n" +
                "      responses:\n" +
                "        200:\n" +
                "          description: voila!\n" +
                "          content:\n" +
                "            application/json:\n" +
                "              schema:\n" +
                "                $ref: '#/components/schemas/SampleResponseSchema'\n" +
                "      deprecated: true\n" +
                "components:\n" +
                "  schemas:\n" +
                "    SampleResponseSchema:\n" +
                "      type: object\n" +
                "      properties:\n" +
                "        id:\n" +
                "          type: string\n" +
                "          description: the user id";

        SerializationMatchers.assertEqualsToYaml(openAPI, yaml);
    }

    //    @Test
    public void testMuRet() {
        Reader reader = new Reader(new OpenAPI());
        JavaClass classByName = TestBase.builder.getClassByName(MuRetController.class.getName());

        OpenAPI openAPI = reader.read(classByName);
        String yaml = "openapi: 3.0.1\n" +
                "paths:\n" +
                "  /get:\n" +
                "    get:\n" +
                "      summary: 获取一个实例\n" +
                "      operationId: get\n" +
                "      responses:\n" +
                "        200:\n" +
                "          description: 返回信息\n" +
                "          content:\n" +
                "            application/json:\n" +
                "              schema:\n" +
                "                $ref: '#/components/schemas/ResultResult2CreateParam'\n" +
                "      deprecated: false\n" +
                "components:\n" +
                "  schemas:\n" +
                "    CreateParam:\n" +
                "      required:\n" +
                "      - user\n" +
                "      type: object\n" +
                "      properties:\n" +
                "        user:\n" +
                "          type: string\n" +
                "          description: 用户名\n" +
                "        mobile:\n" +
                "          type: string\n" +
                "          description: 手机\n" +
                "    Result2CreateParam:\n" +
                "      type: object\n" +
                "      properties:\n" +
                "        msg:\n" +
                "          type: string\n" +
                "        data:\n" +
                "          $ref: '#/components/schemas/CreateParam'\n" +
                "    ResultResult2CreateParam:\n" +
                "      type: object\n" +
                "      properties:\n" +
                "        msg:\n" +
                "          type: string\n" +
                "        data:\n" +
                "          $ref: '#/components/schemas/Result2CreateParam'\n";

        SerializationMatchers.assertEqualsToYaml(openAPI, yaml);
    }


    @Test
    public void test2497() {
        Reader reader = new Reader(new OpenAPI());
        JavaClass classByName = TestBase.builder.getClassByName(ResponseContentWithArrayResource.class.getName());

        OpenAPI openAPI = reader.read(classByName);

        Paths paths = openAPI.getPaths();
        assertEquals(paths.size(), 1);
        PathItem pathItem = paths.get("/user");
        assertNotNull(pathItem);
        Operation operation = pathItem.getGet();
        assertNotNull(operation);
        ArraySchema schema = (ArraySchema) operation.getResponses().get("200").getContent().values().iterator().next().getSchema();
        assertNotNull(schema);
        assertEquals(schema.getItems().get$ref(), "#/components/schemas/User");
    }

}