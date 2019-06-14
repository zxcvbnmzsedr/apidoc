package com.ztianzeng.apidoc;

import com.thoughtworks.qdox.JavaProjectBuilder;
import com.thoughtworks.qdox.model.JavaAnnotation;
import com.thoughtworks.qdox.model.JavaClass;

import java.io.File;
import java.util.*;

import static com.ztianzeng.apidoc.constants.SpringMvcConstants.CONTROLLER_FULLY;
import static com.ztianzeng.apidoc.constants.SpringMvcConstants.REST_CONTROLLER_FULLY;

/**
 * 核心处理器
 *
 * @author zhaotianzeng
 * @version V1.0
 * @date 2019-05-27 13:13
 */
public class SourceBuilder {

    public Map<String, JavaClass> javaFilesMap = new HashMap<>();

    private JavaProjectBuilder builder;


    private Collection<JavaClass> javaClasses;


    public SourceBuilder() {
        loadJavaFiles("src");
    }

    public SourceBuilder(String uri) {
        loadJavaFiles(uri);
    }

    private void loadJavaFiles(String uri) {

        builder = new JavaProjectBuilder();
        builder.addSourceTree(new File(uri));

        this.javaClasses = builder.getClasses();
        for (JavaClass cls : javaClasses) {
            javaFilesMap.put(cls.getFullyQualifiedName(), cls);
        }
    }

    public Set<JavaClass> getControllerData() {
        Set<JavaClass> apiMethodDocs = new HashSet<>();
        for (JavaClass javaClass : javaClasses) {
            if (isController(javaClass)) {
                apiMethodDocs.add(javaClass);
            }
        }
        return apiMethodDocs;
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

    public JavaProjectBuilder getBuilder() {
        return builder;
    }
}
