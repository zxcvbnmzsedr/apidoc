//package com.ztianzeng.apidoc;
//
//import com.thoughtworks.qdox.JavaProjectBuilder;
//import com.thoughtworks.qdox.model.JavaClass;
//import com.ztianzeng.apidoc.ModelConverters;
//import com.ztianzeng.apidoc.SourceBuilder;
//import com.ztianzeng.apidoc.constants.RequestMethod;
//import com.ztianzeng.apidoc.model.ApiMethodDoc;
//import com.ztianzeng.apidoc.model.Parameters;
//import com.ztianzeng.apidoc.models.*;
//import com.ztianzeng.apidoc.models.media.Content;
//import com.ztianzeng.apidoc.models.media.MediaType;
//import com.ztianzeng.apidoc.models.media.Schema;
//import com.ztianzeng.apidoc.models.responses.ApiResponse;
//import com.ztianzeng.apidoc.models.responses.ApiResponses;
//import org.apache.commons.lang3.StringUtils;
//
//import java.util.*;
//
///**
// * open api
// *
// * @author zhaotianzeng
// * @version V1.0
// * @date 2019-06-07 12:01
// */
//public class Reader {
//    private final OpenAPI openAPI;
//    private Paths paths;
//    private SourceBuilder sourceBuilder;
//    private JavaProjectBuilder builder;
//    private Components components;
//    public static final String COMPONENTS_REF = "#/components/schemas/";
//
//    public Reader() {
//        this.openAPI = new OpenAPI();
//        this.sourceBuilder = new SourceBuilder();
//        paths = new Paths();
//        components = new Components();
//        this.builder = sourceBuilder.getBuilder();
//    }
//
//    public Reader(OpenAPI openAPI) {
//        this.openAPI = openAPI;
//        paths = new Paths();
//        components = new Components();
//        this.sourceBuilder = new SourceBuilder();
//        this.builder = sourceBuilder.getBuilder();
//
//    }
//
//    /**
//     * 读取class的method
//     *
//     * @param cls
//     * @return
//     */
//    public OpenAPI read(Class<?> cls) {
//        openAPI.setPaths(this.paths);
//        openAPI.setComponents(components);
//        JavaClass classByName = builder.getClassByName(cls.getCanonicalName());
//        List<ApiMethodDoc> apiMethodDocs = sourceBuilder.buildControllerMethod(classByName);
//
//        for (ApiMethodDoc apiMethodDoc : apiMethodDocs) {
//            PathItem pathItemObject;
//            if (paths != null && paths.get(apiMethodDoc.getPath()) != null) {
//                pathItemObject = paths.get(apiMethodDoc.getPath());
//            } else {
//                pathItemObject = new PathItem();
//            }
//            Operation operation = parseMethod(apiMethodDoc);
//            setPathItemOperation(pathItemObject, apiMethodDoc.getRequestMethod(), operation);
//
//            if (StringUtils.isBlank(operation.getOperationId())) {
//                operation.setOperationId(getOperationId(apiMethodDoc.getMethodName()));
//            }
//
//            paths.addPathItem(apiMethodDoc.getPath(), pathItemObject);
//
//
//        }
//
//
//        return openAPI;
//    }
//
//    protected String getOperationId(String operationId) {
//        boolean operationIdUsed = existOperationId(operationId);
//        String operationIdToFind = null;
//        int counter = 0;
//        while (operationIdUsed) {
//            operationIdToFind = String.format("%s_%d", operationId, ++counter);
//            operationIdUsed = existOperationId(operationIdToFind);
//        }
//        if (operationIdToFind != null) {
//            operationId = operationIdToFind;
//        }
//        return operationId;
//    }
//
//    private boolean existOperationId(String operationId) {
//        if (openAPI == null) {
//            return false;
//        }
//        if (openAPI.getPaths() == null || openAPI.getPaths().isEmpty()) {
//            return false;
//        }
//        for (PathItem path : openAPI.getPaths().values()) {
//            Set<String> pathOperationIds = extractOperationIdFromPathItem(path);
//            if (pathOperationIds.contains(operationId)) {
//                return true;
//            }
//        }
//        return false;
//    }
//
//    private Set<String> extractOperationIdFromPathItem(PathItem path) {
//        Set<String> ids = new HashSet<>();
//        if (path.getGet() != null && StringUtils.isNotBlank(path.getGet().getOperationId())) {
//            ids.add(path.getGet().getOperationId());
//        }
//        if (path.getPost() != null && StringUtils.isNotBlank(path.getPost().getOperationId())) {
//            ids.add(path.getPost().getOperationId());
//        }
//        if (path.getPut() != null && StringUtils.isNotBlank(path.getPut().getOperationId())) {
//            ids.add(path.getPut().getOperationId());
//        }
//        if (path.getDelete() != null && StringUtils.isNotBlank(path.getDelete().getOperationId())) {
//            ids.add(path.getDelete().getOperationId());
//        }
//        if (path.getOptions() != null && StringUtils.isNotBlank(path.getOptions().getOperationId())) {
//            ids.add(path.getOptions().getOperationId());
//        }
//        if (path.getHead() != null && StringUtils.isNotBlank(path.getHead().getOperationId())) {
//            ids.add(path.getHead().getOperationId());
//        }
//        if (path.getPatch() != null && StringUtils.isNotBlank(path.getPatch().getOperationId())) {
//            ids.add(path.getPatch().getOperationId());
//        }
//        return ids;
//    }
//
//    private void setPathItemOperation(PathItem pathItemObject, RequestMethod method, Operation operation) {
//        switch (method) {
//            case POST:
//                pathItemObject.post(operation);
//                break;
//            case GET:
//                pathItemObject.get(operation);
//                break;
//            case DELETE:
//                pathItemObject.delete(operation);
//                break;
//            case PUT:
//                pathItemObject.put(operation);
//                break;
//
//            default:
//                // Do nothing here
//                break;
//        }
//    }
//
//    /**
//     * 处理方法
//     *
//     * @return
//     */
//    public Operation parseMethod(ApiMethodDoc apiMethodDoc) {
//        Operation build = Operation.builder()
//                .description(apiMethodDoc.getDescription())
//                .summary(apiMethodDoc.getSummary())
//                .deprecated(apiMethodDoc.isDeprecated())
//                .build();
//
//
//        Map<String, Parameters> responseBody = apiMethodDoc.getResponseBody();
//
//        ApiResponses responses = new ApiResponses();
//        Map<String, Schema> schemaMap = new HashMap<>();
//
//        for (String parametrString : responseBody.keySet()) {
//            Parameters parameters = responseBody.get(parametrString);
//
//            schemaMap = ModelConverters.getInstance().read(parameters.getOrigin());
//
//            ApiResponse apiResponse = new ApiResponse();
//            apiResponse.setDescription(parametrString);
//
//            Content content = new Content();
//            MediaType mediaType = new MediaType();
//            Schema objectSchema = new Schema();
//            objectSchema.$ref(COMPONENTS_REF + schemaMap.keySet().stream().findFirst().orElse(""));
//
//            mediaType.schema(objectSchema);
//            content.addMediaType("application/json", mediaType);
//            apiResponse.setContent(content);
//            // 成功时候的返回
//            responses.addApiResponse("200", apiResponse);
//        }
//        // swagger规定必须有一个response
//        if (responses.size() == 0){
//            ApiResponse apiResponse = new ApiResponse();
//            apiResponse.setDescription("response");
//            responses.addApiResponse("200", apiResponse);
//
//        }
//
//
//        // 在这边添加schema
//        schemaMap.forEach((key, schema) -> {
//            components.addSchemas(key, schema);
//        });
//
//        build.setResponses(responses);
//        return build;
//    }
//}