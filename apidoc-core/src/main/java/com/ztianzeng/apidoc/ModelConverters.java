package com.ztianzeng.apidoc;

import com.thoughtworks.qdox.model.JavaClass;
import com.ztianzeng.apidoc.converter.AnnotatedType;
import com.ztianzeng.apidoc.converter.ModelConverter;
import com.ztianzeng.apidoc.converter.ModelConverterContextImpl;
import com.ztianzeng.apidoc.converter.ResolvedSchema;
import com.ztianzeng.apidoc.models.media.Schema;
import com.ztianzeng.apidoc.utils.Json;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Type;
import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * @author zhaotianzeng
 * @version V1.0
 * @date 2019-06-05 22:04
 */
@Slf4j
public class ModelConverters {
    private static final ModelConverters SINGLETON = new ModelConverters();
    private final List<ModelConverter> converters;
    private final Set<String> skippedPackages = new HashSet<>();
    private final Set<String> skippedClasses = new HashSet<>();

    public static ModelConverters getInstance() {
        return SINGLETON;
    }

    public ModelConverters() {
        SourceBuilder sourceBuilder = new SourceBuilder();
        converters = new CopyOnWriteArrayList<>();
        converters.add(new ModelResolver(Json.mapper(), sourceBuilder));
    }


    public Map<String, Schema> read(JavaClass type) {
        return read(new AnnotatedType().javaClass(type));
    }

    public Map<String, Schema> read(AnnotatedType type) {
        Map<String, Schema> modelMap = new HashMap<>();
        if (shouldProcess(type.getJavaClass())) {
            ModelConverterContextImpl context = new ModelConverterContextImpl(
                    converters);
            Schema resolve = context.resolve(type);
            for (Map.Entry<String, Schema> entry : context.getDefinedModels()
                    .entrySet()) {
                if (entry.getValue().equals(resolve)) {
                    modelMap.put(entry.getKey(), entry.getValue());
                }
            }
        }
        return modelMap;
    }


    public Map<String, Schema> readAll(JavaClass type) {
        return readAll(new AnnotatedType().javaClass(type));
    }

    public Map<String, Schema> readAll(AnnotatedType type) {
        if (shouldProcess(type.getJavaClass())) {
            ModelConverterContextImpl context = new ModelConverterContextImpl(converters);

            log.debug("ModelConverters readAll from " + type);
            context.resolve(type);
            return context.getDefinedModels();
        }
        return new HashMap<>();
    }


    public Schema resolve(JavaClass type) {
        return resolve(new AnnotatedType().javaClass(type));
    }

    public Schema resolve(AnnotatedType type) {
        if (shouldProcess(type.getJavaClass())) {
            ModelConverterContextImpl context = new ModelConverterContextImpl(converters);

            log.debug("ModelConverters readAll from " + type);
            return context.resolve(type);
        }
        return null;
    }


    private boolean shouldProcess(JavaClass javaClass) {
        if (javaClass.isPrimitive()) {
            return false;
        }
        String className = javaClass.getName();
        for (String packageName : skippedPackages) {
            if (className.startsWith(packageName)) {
                return false;
            }
        }
        return !skippedClasses.contains(className);
    }


    public ResolvedSchema readAllAsResolvedSchema(AnnotatedType type) {
        if (shouldProcess(type.getJavaClass())) {
            ModelConverterContextImpl context = new ModelConverterContextImpl(
                    converters);

            ResolvedSchema resolvedSchema = new ResolvedSchema();
            resolvedSchema.schema = context.resolve(type);
            resolvedSchema.referencedSchemas = context.getDefinedModels();

            return resolvedSchema;
        }
        return null;
    }
}