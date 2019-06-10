package com.ztianzeng.apidoc;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.thoughtworks.qdox.JavaProjectBuilder;
import com.thoughtworks.qdox.model.*;
import com.ztianzeng.apidoc.constants.RequestMethod;
import com.ztianzeng.apidoc.utils.DocUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.ztianzeng.apidoc.constants.GlobalConstants.IGNORE_TAG;
import static com.ztianzeng.apidoc.constants.SpringMvcConstants.*;
import static com.ztianzeng.apidoc.utils.DocUtils.getRequestMappingMethod;
import static com.ztianzeng.apidoc.utils.DocUtils.isRequestMapping;

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



    public JavaProjectBuilder getBuilder() {
        return builder;
    }
}
