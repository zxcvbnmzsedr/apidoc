package com.ztianzeng.apidoc;

import com.ztianzeng.apidoc.models.OpenAPI;
import com.ztianzeng.apidoc.models.Operation;
import com.ztianzeng.apidoc.models.PathItem;
import com.ztianzeng.apidoc.models.Paths;
import com.ztianzeng.apidoc.res.BasicFieldsResource;
import com.ztianzeng.apidoc.swagger.Reader;
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

    @Test
    public void testSimpleReadClass() {
        Reader reader = new Reader(new OpenAPI());
        OpenAPI openAPI = reader.read(BasicFieldsResource.class);
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
}