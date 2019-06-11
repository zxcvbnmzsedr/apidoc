package com.ztianzeng.apidoc;

import com.fasterxml.jackson.databind.BeanDescription;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.introspect.AnnotatedMethod;
import com.fasterxml.jackson.databind.introspect.AnnotatedParameter;
import com.thoughtworks.qdox.JavaProjectBuilder;
import com.thoughtworks.qdox.model.*;
import com.ztianzeng.apidoc.constants.RequestMethod;
import com.ztianzeng.apidoc.models.*;
import com.ztianzeng.apidoc.models.media.ArraySchema;
import com.ztianzeng.apidoc.models.media.Content;
import com.ztianzeng.apidoc.models.media.MediaType;
import com.ztianzeng.apidoc.models.media.Schema;
import com.ztianzeng.apidoc.models.parameters.Parameter;
import com.ztianzeng.apidoc.models.parameters.RequestBody;
import com.ztianzeng.apidoc.models.responses.ApiResponse;
import com.ztianzeng.apidoc.models.responses.ApiResponses;
import com.ztianzeng.apidoc.utils.DocUtils;
import com.ztianzeng.apidoc.utils.Json;
import org.apache.commons.lang3.StringUtils;

import java.util.*;
import java.util.regex.Matcher;

import static com.ztianzeng.apidoc.constants.HtmlRex.HTML_P_PATTERN;
import static com.ztianzeng.apidoc.utils.DocUtils.*;
import static com.ztianzeng.apidoc.utils.RefUtils.constructRef;

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
    private ObjectMapper mapper;

    public Reader() {
        this.openAPI = new OpenAPI();
        this.sourceBuilder = new SourceBuilder();
        paths = new Paths();
        components = new Components();
        this.builder = sourceBuilder.getBuilder();
        mapper = Json.mapper();
    }

    public Reader(OpenAPI openAPI) {
        this.openAPI = openAPI;
        paths = new Paths();
        components = new Components();
        this.sourceBuilder = new SourceBuilder();
        this.builder = sourceBuilder.getBuilder();
        mapper = Json.mapper();
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

        com.fasterxml.jackson.databind.JavaType targetType = mapper.constructType(cls);
        BeanDescription beanDesc = mapper.getSerializationConfig().introspect(targetType);


        // 处理方法
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

            if (url != null) {
                url = url.replaceAll("\"", "").trim();
            }

            PathItem pathItemObject;
            if (paths != null && paths.get(url) != null) {
                pathItemObject = paths.get(url);
            } else {
                pathItemObject = new PathItem();
            }

            Operation operation = parseMethod(beanDesc, method, deprecated);
            setPathItemOperation(pathItemObject, methodType, operation);


            if (StringUtils.isBlank(operation.getOperationId())) {
                operation.setOperationId(getOperationId(method.getName()));
            }


            paths.addPathItem(url, pathItemObject);
        }


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
    public Operation parseMethod(BeanDescription beanDesc, JavaMethod javaMethod, boolean deprecated) {
        Operation build = Operation.builder()
                .deprecated(deprecated)
                .build();
        setDescAndSummary(build, javaMethod);

        setParametersItem(build, javaMethod);


        JavaType returnType = javaMethod.getReturnType();
        AnnotatedMethod jackSonMethod = null;
        // 筛选，提取jackson的类型
        for (AnnotatedMethod factoryMethod : beanDesc.getClassInfo().memberMethods()) {
            if (factoryMethod.getName().equals(javaMethod.getName())
                    && factoryMethod.getRawReturnType().getName().equals(returnType.getBinaryName())) {
                jackSonMethod = factoryMethod;
            }
        }

        assert jackSonMethod != null;
        setRequestBody(build, javaMethod, jackSonMethod);

        // 处理方法的信息
        Map<String, Schema> schemaMap = ModelConverters.getInstance()
                .readAll(jackSonMethod.getType());

        ApiResponses responses = new ApiResponses();

        ApiResponse apiResponse = new ApiResponse();

        // 必须得返回一个描述，否则swagger报错
        String aReturn = Optional.ofNullable(javaMethod.getTagByName("return")).map(DocletTag::getValue).orElse("response");
        apiResponse.setDescription(aReturn);

        Content content = new Content();
        MediaType mediaType = new MediaType();

        Schema objectSchema = ModelConverters.getInstance()
                .resolve(jackSonMethod.getType());


        if (objectSchema != null) {
            if (objectSchema instanceof ArraySchema) {
                ((ArraySchema) objectSchema).getItems().$ref(constructRef(schemaMap.keySet().stream().findFirst().orElse("")));
            } else {
                objectSchema.$ref(constructRef(DocUtils.findTypeName(jackSonMethod.getType(), beanDesc)));

            }
        }

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
     * 设置方法的请求参数
     */
    private void setParametersItem(Operation apiMethodDoc, JavaMethod method) {
        List<JavaParameter> parameters = method.getParameters();

        Map<String, String> paramDesc = getParamTag(method);

        for (JavaParameter parameter : parameters) {
            if (isContentBody(parameter.getAnnotations())) {
                return;
            }
            // 如果是私有属性直接便利
            if (DocUtils.isPrimitive(parameter.getType().getBinaryName())) {
                Parameter inputParameter = new Parameter();
                inputParameter.in("query");
                inputParameter.setName(parameter.getName());
                Schema schema = new Schema();
                inputParameter.setDescription(paramDesc.get(parameter.getName()));
                inputParameter.setSchema(schema);
                apiMethodDoc.addParametersItem(inputParameter);
            } else {
                Map<String, Schema> stringSchemaMap = ModelConverters.getInstance()
                        .readAll(DocUtils.getTypeForName(parameter.getJavaClass().getBinaryName()));
                for (String s : stringSchemaMap.keySet()) {
                    Schema schema = stringSchemaMap.get(s);
                    Map<String, Schema> properties = schema.getProperties();
                    properties.forEach((k, v) -> {
                        Parameter inputParameter = new Parameter();
                        inputParameter.setName(k);
                        inputParameter.in("query");
                        inputParameter.setSchema(v);
                        apiMethodDoc.addParametersItem(inputParameter);
                    });
                }


            }


        }


    }

    /**
     * 设置方法的请求参数
     */
    private void setRequestBody(Operation apiMethodDoc, JavaMethod method, AnnotatedMethod jacksonMethod) {
        List<JavaParameter> parameters = method.getParameters();

        for (int i = 0; i < parameters.size(); i++) {
            JavaParameter parameter = parameters.get(i);
            if (!isContentBody(parameter.getAnnotations())) {
                return;
            }
            AnnotatedParameter jacksonParam = jacksonMethod.getParameter(i);


            Schema objectSchema = ModelConverters.getInstance()
                    .resolve(jacksonParam.getType());

            Map<String, Schema> schemaMap = ModelConverters.getInstance()
                    .readAll(jacksonParam.getType());


            if (objectSchema instanceof ArraySchema) {
                ((ArraySchema) objectSchema).getItems().$ref(constructRef(schemaMap.keySet().stream().findFirst().orElse("")));
            } else {
                objectSchema.$ref(constructRef(schemaMap.keySet().stream().findFirst().orElse("")));

            }

            RequestBody requestBody = new RequestBody();
            requestBody.setRequired(true);
            Content content = new Content();
            MediaType mediaType = new MediaType();

            mediaType.schema(objectSchema);
            content.addMediaType("application/json", mediaType);
            requestBody.content(content);


            schemaMap.forEach((key, schema) -> {
                components.addSchemas(key, schema);
            });
            apiMethodDoc.setRequestBody(requestBody);
        }


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


    /**
     * 获取方法上param注解
     *
     * @param javaMethod java方法
     * @return
     */
    private Map<String, String> getParamTag(final JavaMethod javaMethod) {
        List<DocletTag> paramTags = javaMethod.getTagsByName("param");
        Map<String, String> paramTagMap = new HashMap<>();
        for (DocletTag docletTag : paramTags) {
            String value = docletTag.getValue();
            String pName;
            String pValue;
            int idx = value.indexOf("\n");
            //如果存在换行
            if (idx > -1) {
                pName = value.substring(0, idx);
                pValue = value.substring(idx + 1);
            } else {
                pName = (value.contains(" ")) ? value.substring(0, value.indexOf(" ")) : value;
                pValue = value.contains(" ") ? value.substring(value.indexOf(' ') + 1) : "";
            }
            paramTagMap.put(pName.trim(), pValue.trim());
        }
        return paramTagMap;
    }


}