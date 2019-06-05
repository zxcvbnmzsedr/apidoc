package com.ztianzeng.apidoc;

import com.thoughtworks.qdox.JavaProjectBuilder;
import com.thoughtworks.qdox.model.DocletTag;
import com.thoughtworks.qdox.model.JavaAnnotation;
import com.thoughtworks.qdox.model.JavaClass;
import com.thoughtworks.qdox.model.JavaMethod;
import com.ztianzeng.apidoc.model.ApiMethodDoc;
import com.ztianzeng.apidoc.utils.StringUtils;

import java.io.File;
import java.util.*;

import static com.ztianzeng.apidoc.constants.GlobalConstants.IGNORE_TAG;
import static com.ztianzeng.apidoc.constants.SpringMvcConstants.*;

/**
 * @author zhaotianzeng
 * @version V1.0
 * @date 2019-05-27 13:13
 */
public class SourceBuilder {

    public Map<String, JavaClass> javaFilesMap = new HashMap<>();

    private JavaProjectBuilder builder;

    private Collection<JavaClass> javaClasses;
    private String appUrl;

    public SourceBuilder() {
        this.appUrl = "http://{server}";

        loadJavaFiles();
    }

    private void loadJavaFiles() {
        JavaProjectBuilder builder = new JavaProjectBuilder();
        builder.addSourceTree(new File("src/main/java"));

        this.builder = builder;
        this.javaClasses = builder.getClasses();
        for (JavaClass cls : javaClasses) {
            javaFilesMap.put(cls.getFullyQualifiedName(), cls);
        }
    }

    public void getControllerData() {
        for (JavaClass javaClass : javaClasses) {
            if (isController(javaClass)) {
                buildControllerMethod(javaClass);
            }
        }
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
            apiMethodDoc.setDesc(method.getComment());
            List<JavaAnnotation> annotations = method.getAnnotations();
            String url = null;
            String methodType = null;
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

                apiMethodDoc.setType(methodType);
                if (StringUtils.isNotEmpty(baseUrl)) {
                    baseUrl = StringUtils.equals("/", baseUrl.subSequence(0, 1)) ? baseUrl : "/" + baseUrl;
                    apiMethodDoc.setUrl(this.appUrl + (baseUrl + "/" + url).replace("//", "/"));
                } else {
                    url = StringUtils.equals("/", url.subSequence(0, 1)) ? url : "/" + url;
                    apiMethodDoc.setUrl(this.appUrl + (url).replace("//", "/"));
                }

                Map<String,String> comment = getCommentTag(method, "param");

                apiMethodDoc.setRequestParams(comment);
                methodDocList.add(apiMethodDoc);

            }
        }

        return methodDocList;
    }

    /**
     * 获取方法上的tag
     *
     * @param method  方法
     * @param tagName tag名字
     * @return
     */
    private Map<String, String> getCommentTag(JavaMethod method, String tagName) {
        List<DocletTag> paramTags = method.getTagsByName(tagName);
        Map<String, String> paramTagMap = new HashMap<>();

        for (DocletTag paramTag : paramTags) {
            String value = paramTag.getValue();

            String pName;
            String pValue;
            int idx = value.indexOf("\n");
            //如果存在换行
            if (idx > -1) {
                pName = value.substring(0, idx);
                pValue = value.substring(idx + 1);
            } else {
                pName = (value.contains(" ")) ? value.substring(0, value.indexOf(" ")) : value;
                pValue = value.contains(" ") ? value.substring(value.indexOf(' ') + 1) : "No comments found.";
            }
            paramTagMap.put(pName, pValue);
        }

        return paramTagMap;
    }


    /**
     * 获取请求的方法
     *
     * @param annotation
     * @return
     */
    private String getRequestMappingMethod(JavaAnnotation annotation) {
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
        return methodType;
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
}
