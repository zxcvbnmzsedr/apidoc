package com.ztianzeng.apidoc.swagger;

import com.fasterxml.jackson.databind.type.TypeFactory;
import com.ztianzeng.apidoc.swagger.converter.AnnotatedType;
import com.ztianzeng.apidoc.swagger.converter.ModelConverter;
import com.ztianzeng.apidoc.swagger.converter.ModelConverterContextImpl;
import com.ztianzeng.apidoc.swagger.models.media.Schema;
import com.ztianzeng.apidoc.swagger.util.Json;
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
        converters = new CopyOnWriteArrayList<>();
        converters.add(new ModelResolver(Json.mapper()));
    }

    public Map<String, Schema> readAll(Type type) {
        return readAll(new AnnotatedType().type(type));
    }

    public Map<String, Schema> readAll(AnnotatedType type) {
        if (shouldProcess(type.getType())) {
            ModelConverterContextImpl context = new ModelConverterContextImpl(converters);

            log.debug("ModelConverters readAll from " + type);
            context.resolve(type);
            return context.getDefinedModels();
        }
        return new HashMap<>();
    }

    private boolean shouldProcess(Type type) {
        final Class<?> cls = TypeFactory.defaultInstance().constructType(type).getRawClass();
        if (cls.isPrimitive()) {
            return false;
        }
        String className = cls.getName();
        for (String packageName : skippedPackages) {
            if (className.startsWith(packageName)) {
                return false;
            }
        }
        return !skippedClasses.contains(className);
    }
}