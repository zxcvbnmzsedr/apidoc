package com.ztianzeng.apidoc;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.BeanDescription;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.introspect.BeanPropertyDefinition;
import com.thoughtworks.qdox.JavaProjectBuilder;
import com.thoughtworks.qdox.model.*;
import com.ztianzeng.apidoc.constants.RequestMethod;
import com.ztianzeng.apidoc.model.ApiMethodDoc;
import com.ztianzeng.apidoc.model.Parameters;
import com.ztianzeng.apidoc.utils.DocUtils;
import com.ztianzeng.apidoc.utils.StringUtils;

import java.io.File;
import java.lang.reflect.Type;
import java.util.*;
import java.util.regex.Matcher;
import java.util.stream.Collectors;

import static com.ztianzeng.apidoc.constants.GlobalConstants.IGNORE_TAG;
import static com.ztianzeng.apidoc.constants.HtmlRex.HTML_P_PATTERN;
import static com.ztianzeng.apidoc.constants.SpringMvcConstants.*;

/**
 * @author zhaotianzeng
 * @version V1.0
 * @date 2019-05-27 13:13
 */
public class SourceBuilder {

    public Map<String, JavaClass> javaFilesMap = new HashMap<>();

    private JavaProjectBuilder builder;

    protected final ObjectMapper mapper;

    private Collection<JavaClass> javaClasses;

    private String appUrl;

    public SourceBuilder() {
        this.mapper = objectMapper();
        this.appUrl = "http://{server}";
        loadJavaFiles("src");
    }

    public SourceBuilder(String uri) {
        this.mapper = objectMapper();
        this.appUrl = "http://{server}";
        loadJavaFiles(uri);
    }

    private void loadJavaFiles(String uri) {

        JavaProjectBuilder builder = new JavaProjectBuilder();
        builder.addSourceTree(new File(uri));

        this.builder = builder;
        this.javaClasses = builder.getClasses();
        for (JavaClass cls : javaClasses) {
            javaFilesMap.put(cls.getFullyQualifiedName(), cls);
        }
    }

    public List<ApiMethodDoc> getControllerData() {
        List<ApiMethodDoc> apiMethodDocs = new ArrayList<>();
        for (JavaClass javaClass : javaClasses) {
            if (isController(javaClass)) {
                apiMethodDocs.addAll(buildControllerMethod(javaClass));
            }
        }
        return apiMethodDocs;
    }

    public List<ApiMethodDoc> buildControllerMethod(final JavaClass cls) {
        List<JavaAnnotation> classAnnotations = cls.getAnnotations();
        // 请求的基本路径
        String baseUrl = null;
        for (JavaAnnotation annotation : classAnnotations) {
            // 处理被@RequestMapping注解的类
            if (isRequestMapping(annotation)) {
                baseUrl = annotation.getNamedParameter("value").toString();
                baseUrl = baseUrl.replaceAll("\"", "");
            }
        }
        List<JavaMethod> methods = cls.getMethods();
        List<ApiMethodDoc> methodDocList = new ArrayList<>(methods.size());

        for (JavaMethod method : methods) {
            ApiMethodDoc apiMethodDoc = new ApiMethodDoc();

            setDescAndSummary(apiMethodDoc, method);

            List<JavaAnnotation> annotations = method.getAnnotations();
            String url = null;
            RequestMethod methodType = null;
            int methodCounter = 0;
            // 处理方法上面的注解
            for (JavaAnnotation annotation : annotations) {
                if (null == annotation.getNamedParameter("value")) {
                    url = "/";
                } else {
                    url = annotation.getNamedParameter("value").toString();
                }
                // @RequestMapping
                if (isRequestMapping(annotation)) {
                    methodType = getRequestMappingMethod(annotation);
                    methodCounter++;
                } else {
                    String annotationName = annotation.getType().getName();

                    methodType = METHOD_MAP.get(annotationName);
                    methodCounter++;
                }
            }

            if (methodCounter > 0) {
                if (null != method.getTagByName(IGNORE_TAG)) {
                    continue;
                }
                url = url.replaceAll("\"", "").trim();

                apiMethodDoc.setRequestMethod(methodType);
                String path;
                if (StringUtils.isNotEmpty(baseUrl)) {
                    baseUrl = StringUtils.equals("/", baseUrl.subSequence(0, 1)) ? baseUrl : "/" + baseUrl;
                    path = (baseUrl + "/" + url).replace("//", "/");

                } else {
                    url = StringUtils.equals("/", url.subSequence(0, 1)) ? url : "/" + url;
                    path = (url).replace("//", "/");

                }

                apiMethodDoc.setUrl(this.appUrl + path);
                apiMethodDoc.setPath(path);

                apiMethodDoc.setContentType(getContentType(method));

                apiMethodDoc.setRequestParams(getRequest(method));

                apiMethodDoc.setResponseBody(getResponse(method));

                methodDocList.add(apiMethodDoc);


            }
        }

        return methodDocList;
    }

    /**
     * 设置方法上的详情和概述
     *
     * @param apiMethodDoc
     * @param method
     */
    private void setDescAndSummary(ApiMethodDoc apiMethodDoc, JavaMethod method) {
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
     * 获取方法上的请求类型
     *
     * @param method 方法
     * @return 请求类型
     */
    private String getContentType(JavaMethod method) {
        List<JavaParameter> parameterList = method.getParameters();
        for (JavaParameter parameter : parameterList) {
            JavaType javaType = parameter.getType();
            String typeName = javaType.getFullyQualifiedName();
            if (!DocUtils.isMvcParams(typeName)) {
                List<JavaAnnotation> annotations = parameter.getAnnotations();
                for (JavaAnnotation annotation : annotations) {
                    String annotationName = annotation.getType().getSimpleName();
                    if (REQUEST_BODY.equals(annotationName) || REQUEST_BODY_FULLY.equals(annotationName)) {
                        return JSON_CONTENT_TYPE;
                    }
                }

            }
        }
        return FORM_CONTENT_TYPE;

    }


    /**
     * 获取方法的请求参数
     *
     * @param method 方法
     * @return 查询出来的kv
     */
    private List<Parameters> getRequest(JavaMethod method) {
        List<DocletTag> paramTags = method.getTagsByName("param");
        List<Parameters> parameters = new LinkedList<>();
        for (DocletTag paramTag : paramTags) {
            String value = paramTag.getValue();
            String pName = (value.contains(" ")) ? value.substring(0, value.indexOf(" ")) : value;
            String pValue = value.contains(" ") ? value.substring(value.indexOf(' ') + 1) : "No comments found.";

            // 获取对应的type
            JavaParameter parameterByName = method.getParameterByName(pName);

            Type type = null;

            boolean required = false;
            // 如果参数不为空
            if (parameterByName != null) {
                type = DocUtils.getTypeForName(parameterByName.getType().getValue());
                // 以@RequestBody为主
                if (isRequestBody(parameterByName)) {
                    required = true;
                }
                // 以@RequestParam为主
                List<JavaAnnotation> annotations = parameterByName.getAnnotations();
                for (JavaAnnotation annotation : annotations) {
                    if (annotation.getType().getSimpleName().equals(REQUEST_PARAM)) {
                        required = true;
                        if (null != annotation.getProperty("required")) {
                            required = Boolean.valueOf(annotation.getProperty("required").toString());
                        }
                        if (null != annotation.getProperty("value")) {
                            pName = annotation.getProperty("value").toString();
                        }
                    }

                }
            }


            parameters.add(Parameters.builder()
                    .name(pName.trim())
                    .type(type)
                    .required(required)
                    .description(pValue.trim())
                    .build());
        }
        // 获取方法的参数列表
        List<JavaParameter> parameterList = method.getParameters();
        for (JavaParameter javaParameter : parameterList) {
            // 如果是request Body 对象，则解析对象类型
            parameters.addAll(parsingBody(javaParameter.getJavaClass()));
        }

        return parameters;
    }

    /**
     * 获取结果对象
     * 因为解析时会直接拿泛型的去解析，因此重新构造一个新的塞入
     */
    private List<Parameters> getResponse(JavaMethod method) {

        JavaClass returns = method.getReturns();
        // 如果是request Body 对象，则解析对象类型
        return parsingBody(returns);
    }

    /**
     * 解析body
     *
     * @param cls 类信息
     * @return 返回类中的每个字段的信息
     */
    public List<Parameters> parsingBody(JavaClass cls) {
        if (DocUtils.isPrimitive(cls.getFullyQualifiedName()) || cls.isPrimitive()) {
            return Collections.emptyList();
        }
        List<Parameters> parameters = new LinkedList<>();

        List<JavaField> fields = new ArrayList<>();

        Type targetClass = DocUtils.getTypeForName(cls.getFullyQualifiedName());

        while (cls != null && !cls.getFullyQualifiedName().equals("java.lang.Object")) {
            fields.addAll(cls.getFields());
            cls = cls.getSuperJavaClass();
        }

        com.fasterxml.jackson.databind.JavaType targetType = mapper.constructType(targetClass);
        final BeanDescription beanDesc = mapper.getSerializationConfig().introspect(targetType);

        Map<String, JavaField> collect = fields.stream().collect(Collectors.toMap(JavaField::getName, r -> r, (r1, r2) -> r1));
        for (BeanPropertyDefinition property : beanDesc.findProperties()) {

            JavaField field = collect.get(property.getName());
            if (field == null) {
                continue;
            }
            // 属性是否为require
            boolean required = DocUtils.isRequired(field);


            // 会递归获取参数的信息
            parameters.add(
                    new Parameters(required,
                            field.getName(),
                            field.getComment(),
                            DocUtils.getTypeForName(field.getType().getBinaryName()),
                            property)
            );
        }


        return parameters;
    }


    /**
     * 是否是request body 对象
     *
     * @param javaParameter java参数
     * @return 是否
     */
    private boolean isRequestBody(JavaParameter javaParameter) {
        List<JavaAnnotation> annotations = javaParameter.getAnnotations();
        for (JavaAnnotation annotation : annotations) {
            if (annotation.getType().getName().equals(REQUEST_BODY)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 获取请求的方法
     *
     * @param annotation
     * @return
     */
    private RequestMethod getRequestMappingMethod(JavaAnnotation annotation) {
        String methodType;
        if (null != annotation.getNamedParameter("method")) {
            methodType = annotation.getNamedParameter("method").toString();
            if ("RequestMethod.POST".equals(methodType)) {
                methodType = "POST";
            } else if ("RequestMethod.GET".equals(methodType)) {
                methodType = "GET";
            } else if ("RequestMethod.PUT".equals(methodType)) {
                methodType = "PUT";
            } else if ("RequestMethod.DELETE".equals(methodType)) {
                methodType = "DELETE";
            } else {
                methodType = "GET";
            }
        } else {
            methodType = "GET";
        }
        return RequestMethod.valueOf(methodType);
    }

    /**
     * 是否为@RequestMapping的注解
     *
     * @param annotation
     * @return
     */
    private boolean isRequestMapping(JavaAnnotation annotation) {
        String annotationName = annotation.getType().getName();
        return REQUEST_MAPPING.equals(annotationName) || REQUEST_MAPPING_FULLY.equals(annotationName);
    }


    /**
     * 检测controller上的注解
     *
     * @param cls
     * @return
     */
    private boolean isController(JavaClass cls) {
        List<JavaAnnotation> classAnnotations = cls.getAnnotations();
        for (JavaAnnotation annotation : classAnnotations) {
            String annotationName = annotation.getType().getName();
            if ("Controller".equals(annotationName) || "RestController".equals(annotationName)
                    || REST_CONTROLLER_FULLY.equals(annotationName)
                    || CONTROLLER_FULLY.equals(annotationName)) {
                return true;
            }
        }
        return false;
    }


    public ObjectMapper objectMapper() {
        ObjectMapper mapper = new ObjectMapper();

        mapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
        mapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
        mapper.configure(SerializationFeature.WRITE_ENUMS_USING_TO_STRING, true);
        mapper.configure(SerializationFeature.WRITE_NULL_MAP_VALUES, false);
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);

        return mapper;
    }

    public JavaProjectBuilder getBuilder() {
        return builder;
    }
}
