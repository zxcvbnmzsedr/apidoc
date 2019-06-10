package com.ztianzeng.apidoc;

import com.thoughtworks.qdox.JavaProjectBuilder;
import com.thoughtworks.qdox.model.*;
import com.ztianzeng.apidoc.constants.RequestMethod;
import com.ztianzeng.apidoc.models.*;
import com.ztianzeng.apidoc.models.media.Content;
import com.ztianzeng.apidoc.models.media.MediaType;
import com.ztianzeng.apidoc.models.media.Schema;
import com.ztianzeng.apidoc.models.responses.ApiResponse;
import com.ztianzeng.apidoc.models.responses.ApiResponses;
import com.ztianzeng.apidoc.utils.DocUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.*;
import java.util.regex.Matcher;

import static com.ztianzeng.apidoc.constants.HtmlRex.HTML_P_PATTERN;
import static com.ztianzeng.apidoc.utils.DocUtils.*;

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
    private Components components;
    public static final String COMPONENTS_REF = "#/components/schemas/";

    public Reader() {
        this.openAPI = new OpenAPI();
        this.sourceBuilder = new SourceBuilder();
        paths = new Paths();
        components = new Components();
        this.builder = sourceBuilder.getBuilder();
    }

    public Reader(OpenAPI openAPI) {
        this.openAPI = openAPI;
        paths = new Paths();
        components = new Components();
        this.sourceBuilder = new SourceBuilder();
        this.builder = sourceBuilder.getBuilder();

    }

    /**
     * 读取class的method
     *
     * @param cls
     * @return
     */
    public OpenAPI read(Class<?> cls) {
        openAPI.setPaths(this.paths);
        openAPI.setComponents(components);
        JavaClass classByName = builder.getClassByName(cls.getCanonicalName());

        // controller上面的URL
        String classBaseUrl = null;

        for (JavaAnnotation annotation : classByName.getAnnotations()) {
            if (isRequestMapping(annotation)) {
                classBaseUrl = getRequestMappingUrl(annotation);
            }
        }


        // 处理方法上面的注解
        for (JavaMethod method : classByName.getMethods()) {
            RequestMethod methodType = null;
            boolean deprecated = false;
            String url = null;
            List<JavaAnnotation> annotations = method.getAnnotations();
            for (JavaAnnotation annotation : annotations) {
                if (isRequestMapping(annotation)) {
                    url = getRequestMappingUrl(annotation);
                    methodType = getRequestMappingMethod(annotation);

                }

                if (annotation.getType().isA("java.lang.Deprecated")) {
                    deprecated = true;
                }


            }

            url = url.replaceAll("\"", "").trim();

            PathItem pathItemObject;
            if (paths != null && paths.get(url) != null) {
                pathItemObject = paths.get(url);
            } else {
                pathItemObject = new PathItem();
            }

            Operation operation = parseMethod(method, deprecated);
            setPathItemOperation(pathItemObject, methodType, operation);

            if (StringUtils.isBlank(operation.getOperationId())) {
                operation.setOperationId(getOperationId(method.getName()));
            }
            paths.addPathItem(url, pathItemObject);
        }


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


        return openAPI;
    }

    protected String getOperationId(String operationId) {
        boolean operationIdUsed = existOperationId(operationId);
        String operationIdToFind = null;
        int counter = 0;
        while (operationIdUsed) {
            operationIdToFind = String.format("%s_%d", operationId, ++counter);
            operationIdUsed = existOperationId(operationIdToFind);
        }
        if (operationIdToFind != null) {
            operationId = operationIdToFind;
        }
        return operationId;
    }

    private boolean existOperationId(String operationId) {
        if (openAPI == null) {
            return false;
        }
        if (openAPI.getPaths() == null || openAPI.getPaths().isEmpty()) {
            return false;
        }
        for (PathItem path : openAPI.getPaths().values()) {
            Set<String> pathOperationIds = extractOperationIdFromPathItem(path);
            if (pathOperationIds.contains(operationId)) {
                return true;
            }
        }
        return false;
    }

    private Set<String> extractOperationIdFromPathItem(PathItem path) {
        Set<String> ids = new HashSet<>();
        if (path.getGet() != null && StringUtils.isNotBlank(path.getGet().getOperationId())) {
            ids.add(path.getGet().getOperationId());
        }
        if (path.getPost() != null && StringUtils.isNotBlank(path.getPost().getOperationId())) {
            ids.add(path.getPost().getOperationId());
        }
        if (path.getPut() != null && StringUtils.isNotBlank(path.getPut().getOperationId())) {
            ids.add(path.getPut().getOperationId());
        }
        if (path.getDelete() != null && StringUtils.isNotBlank(path.getDelete().getOperationId())) {
            ids.add(path.getDelete().getOperationId());
        }
        if (path.getOptions() != null && StringUtils.isNotBlank(path.getOptions().getOperationId())) {
            ids.add(path.getOptions().getOperationId());
        }
        if (path.getHead() != null && StringUtils.isNotBlank(path.getHead().getOperationId())) {
            ids.add(path.getHead().getOperationId());
        }
        if (path.getPatch() != null && StringUtils.isNotBlank(path.getPatch().getOperationId())) {
            ids.add(path.getPatch().getOperationId());
        }
        return ids;
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
    public Operation parseMethod(JavaMethod javaMethod, boolean deprecated) {
        Operation build = Operation.builder()
                .deprecated(deprecated)
                .build();
        setDescAndSummary(build, javaMethod);


        JavaType returnType = javaMethod.getReturnType();

        Map<String, Schema> schemaMap = ModelConverters.getInstance()
                .readAll(DocUtils.getTypeForName(returnType.getBinaryName()));
        ApiResponses responses = new ApiResponses();

        ApiResponse apiResponse = new ApiResponse();

        Optional<String> aReturn = Optional.ofNullable(javaMethod.getTagByName("return")).map(DocletTag::getValue);
        aReturn.ifPresent(apiResponse::setDescription);

        Content content = new Content();
        MediaType mediaType = new MediaType();
        Schema objectSchema = new Schema();
        objectSchema.$ref(COMPONENTS_REF + schemaMap.keySet().stream().findFirst().orElse(""));

        mediaType.schema(objectSchema);
        content.addMediaType("application/json", mediaType);
        apiResponse.setContent(content);
        // 成功时候的返回
        responses.addApiResponse("200", apiResponse);


        // swagger规定必须有一个response
        if (responses.size() == 0) {
            apiResponse.setDescription("response");
            responses.addApiResponse("200", apiResponse);
        }


        // 在这边添加schema
        schemaMap.forEach((key, schema) -> {
            components.addSchemas(key, schema);
        });

        build.setResponses(responses);
        return build;
    }

    /**
     * 设置方法上的详情和概述
     *
     * @param apiMethodDoc
     * @param method
     */
    private void setDescAndSummary(Operation apiMethodDoc, JavaMethod method) {
        String comment = method.getComment();

        if (comment != null) {
            String desc = null;
            Matcher m = HTML_P_PATTERN.matcher(comment);

            if (m.find()) {
                desc = m.group(0).replace("<p>", "").replace("</p>", "").trim();
                comment = m.replaceAll("");
            }
            apiMethodDoc.setSummary(comment.trim());
            apiMethodDoc.setDescription(desc);
        }


    }


}