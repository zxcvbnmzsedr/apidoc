package com.ztianzeng.apidoc.swagger;

import com.thoughtworks.qdox.JavaProjectBuilder;
import com.thoughtworks.qdox.model.JavaClass;
import com.ztianzeng.apidoc.SourceBuilder;
import com.ztianzeng.apidoc.constants.RequestMethod;
import com.ztianzeng.apidoc.model.ApiMethodDoc;
import com.ztianzeng.apidoc.models.OpenAPI;
import com.ztianzeng.apidoc.models.Operation;
import com.ztianzeng.apidoc.models.PathItem;
import com.ztianzeng.apidoc.models.Paths;

import java.util.List;

/**
 * open api
 *
 * @author zhaotianzeng
 * @version V1.0
 * @date 2019-06-07 12:01
 */
public class Reader {
    private final OpenAPI openAPI;
    private Paths paths;
    private SourceBuilder sourceBuilder;
    private JavaProjectBuilder builder;

    public Reader() {
        this.openAPI = new OpenAPI();
        this.sourceBuilder = new SourceBuilder();
        paths = new Paths();
        this.builder = sourceBuilder.getBuilder();
    }

    public Reader(OpenAPI openAPI) {
        this.openAPI = openAPI;
        paths = new Paths();
        this.sourceBuilder = new SourceBuilder();
        this.builder = sourceBuilder.getBuilder();

    }

    public OpenAPI read(Class<?> cls) {
        JavaClass classByName = builder.getClassByName(cls.getCanonicalName());
        List<ApiMethodDoc> apiMethodDocs = sourceBuilder.buildControllerMethod(classByName);

        for (ApiMethodDoc apiMethodDoc : apiMethodDocs) {
            PathItem pathItemObject;
            if (openAPI.getPaths() != null && openAPI.getPaths().get(apiMethodDoc.getPath()) != null) {
                pathItemObject = openAPI.getPaths().get(apiMethodDoc.getPath());
            } else {
                pathItemObject = new PathItem();
            }

            Operation build = parseMethod(apiMethodDoc);
            setPathItemOperation(pathItemObject, apiMethodDoc.getRequestMethod(), build);

            paths.addPathItem(apiMethodDoc.getPath(), pathItemObject);


        }
        openAPI.setPaths(this.paths);
        return openAPI;
    }

    private void setPathItemOperation(PathItem pathItemObject, RequestMethod method, Operation operation) {
        switch (method) {
            case POST:
                pathItemObject.post(operation);
                break;
            case GET:
                pathItemObject.get(operation);
                break;
            case DELETE:
                pathItemObject.delete(operation);
                break;
            case PUT:
                pathItemObject.put(operation);
                break;

            default:
                // Do nothing here
                break;
        }
    }

    /**
     * 处理方法
     *
     * @return
     */
    public Operation parseMethod(ApiMethodDoc apiMethodDoc) {
        Operation build = Operation.builder()
                .description(apiMethodDoc.getDescription())
                .summary(apiMethodDoc.getSummary())
                .build();

        return build;
    }
}