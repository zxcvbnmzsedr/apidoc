package com.ztianzeng.apidoc;

import com.fasterxml.jackson.databind.BeanDescription;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyName;
import com.fasterxml.jackson.databind.introspect.BeanPropertyDefinition;
import com.thoughtworks.qdox.JavaProjectBuilder;
import com.thoughtworks.qdox.model.JavaClass;
import com.thoughtworks.qdox.model.JavaField;
import com.ztianzeng.apidoc.converter.AnnotatedType;
import com.ztianzeng.apidoc.converter.ModelConverter;
import com.ztianzeng.apidoc.converter.ModelConverterContext;
import com.ztianzeng.apidoc.models.media.ArraySchema;
import com.ztianzeng.apidoc.models.media.MapSchema;
import com.ztianzeng.apidoc.models.media.PrimitiveType;
import com.ztianzeng.apidoc.models.media.Schema;
import com.ztianzeng.apidoc.utils.DocUtils;
import com.ztianzeng.apidoc.utils.ReflectionUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.lang.reflect.Type;
import java.util.*;
import java.util.stream.Collectors;

import static com.ztianzeng.apidoc.utils.RefUtils.constructRef;


/**
 * 模型解析器
 *
 * @author zhaotianzeng
 * @version V1.0
 * @date 2019-06-06 13:00
 */
@Slf4j
public class ModelResolver implements ModelConverter {
    private SourceBuilder sourceBuilder;
    private JavaProjectBuilder builder;


    protected ObjectMapper mapper;

    public ModelResolver(ObjectMapper mapper, SourceBuilder sourceBuilder) {
        this.mapper = mapper;
        this.sourceBuilder = sourceBuilder;
        builder = sourceBuilder.getBuilder();
    }

    @Override
    public Schema resolve(AnnotatedType annotatedType,
                          ModelConverterContext context,
                          Iterator<ModelConverter> chain) {
        if (annotatedType == null) {
            return null;
        }
        JavaType javaType = getJavaType(annotatedType.getType());

        // 分析目标类信息
        JavaClass targetClass = builder.getClassByName(javaType.getRawClass().getTypeName());
        // 使用JackSon获取定义的Get属性，最终使用JackSon的为空，因为qdox会拉很多不需要的东西出来
        com.fasterxml.jackson.databind.JavaType targetType = mapper.constructType(annotatedType.getType());
        BeanDescription beanDesc = mapper.getSerializationConfig().introspect(targetType);

        Schema schema = new Schema();

        String parentName = annotatedType.getName();
        if (StringUtils.isBlank(parentName)) {
            if (StringUtils.isBlank(parentName) && !ReflectionUtils.isSystemType(targetType)) {
                parentName = findTypeName(targetType, beanDesc);
            }
        }

        schema.name(parentName);

        Schema resolvedModel = context.resolve(annotatedType);
        if (resolvedModel != null) {
            if (parentName.equals(resolvedModel.getName())) {
                return resolvedModel;
            }
        }


        // 转换成OpenApi定义的字段信息
        PrimitiveType parentType = PrimitiveType.fromType(annotatedType.getType());
        schema.setType(Optional.ofNullable(parentType).orElse(PrimitiveType.OBJECT).getCommonName());


        // 分析类的字段
        List<JavaField> fields = new ArrayList<>();

        // 循环当前类信息，所有属性都加到fields
        JavaClass cls = targetClass;
        while (cls != null && !cls.isArray() && !"java.lang.Object".equals(cls.getFullyQualifiedName())) {
            fields.addAll(cls.getFields());
            cls = cls.getSuperJavaClass();
        }


        JavaType valueType = targetType.getContentType();
        JavaType keyType = targetType.getKeyType();
        // 如果是集合类型，将类型向上抛出继续处理
        if (targetType.isContainerType()) {
            // 处理Map那种两种都有的
            if (keyType != null && valueType != null) {
                Schema addPropertiesSchema = context.resolve(
                        new AnnotatedType()
                                .type(valueType)
                                .schemaProperty(annotatedType.isSchemaProperty())
                                .skipSchemaName(true)
                                .resolveAsRef(annotatedType.isResolveAsRef())
                                .jsonViewAnnotation(annotatedType.getJsonViewAnnotation())
                                .propertyName(annotatedType.getPropertyName())
                                .parent(annotatedType.getParent())
                );

                String pName = null;

                if (addPropertiesSchema != null) {
                    if (StringUtils.isNotBlank(addPropertiesSchema.getName())) {
                        pName = addPropertiesSchema.getName();
                    }
                    if ("object".equals(addPropertiesSchema.getType()) && pName != null) {
                        // create a reference for the items
                        if (context.getDefinedModels().containsKey(pName)) {
                            addPropertiesSchema = new Schema().$ref(constructRef(pName));
                        }
                    } else if (addPropertiesSchema.get$ref() != null) {
                        addPropertiesSchema = new Schema().$ref(StringUtils.isNotEmpty(addPropertiesSchema.get$ref()) ? addPropertiesSchema.get$ref() : addPropertiesSchema.getName());
                    }
                }
                schema = new MapSchema().additionalProperties(addPropertiesSchema);
            } else if (valueType != null) {
                // 处理Array
                Schema items = context.resolve(new AnnotatedType()
                        .type(valueType)
                        .schemaProperty(annotatedType.isSchemaProperty())
                        .skipSchemaName(true)
                        .resolveAsRef(annotatedType.isResolveAsRef())
                        .propertyName(annotatedType.getPropertyName())
                        .jsonViewAnnotation(annotatedType.getJsonViewAnnotation())
                        .parent(annotatedType.getParent()));

                if (items == null) {
                    return null;
                }
                schema = new ArraySchema().items(items);

            }
        }

        Map<String, JavaField> collect = fields.stream().collect(Collectors.toMap(JavaField::getName, r -> r, (r1, r2) -> r1));
        for (BeanPropertyDefinition propertyDef : beanDesc.findProperties()) {

            JavaField field = collect.get(propertyDef.getName());
            if (field == null) {
                continue;
            }

            AnnotatedType aType = new AnnotatedType()
                    .type(propertyDef.getPrimaryType())
                    .parent(schema)
                    .resolveAsRef(annotatedType.isResolveAsRef())
                    .jsonViewAnnotation(annotatedType.getJsonViewAnnotation())
                    .skipSchemaName(true)
                    .schemaProperty(true)
                    .propertyName(propertyDef.getName());
            // 属性是否为require
            boolean required = DocUtils.isRequired(field);
            // 属性名称
            String name = field.getName();
            Schema propSchema = new Schema();
            propSchema.setName(name);

            PrimitiveType primitiveType = PrimitiveType.fromType(propertyDef.getPrimaryType());
            if (primitiveType != null) {
                propSchema = primitiveType.createProperty();
            } else {
                propSchema = context.resolve(aType);
                if (propSchema != null) {
                    if (propSchema.get$ref() == null) {
                        if ("object".equals(propSchema.getType())) {
                            // create a reference for the property
                            if (context.getDefinedModels().containsKey(field.getType().getSimpleName())) {
                                propSchema.set$ref(constructRef(field.getType().getSimpleName()));
                            }
                        }
                    }
                }
            }

            if (propSchema != null) {
                propSchema.setDescription(field.getComment());
            }


            if (required) {
                schema.addRequiredItem(name);
            }
            schema.addProperties(name, propSchema);
        }


        return schema;
    }

    protected String findTypeName(JavaType type, BeanDescription beanDesc) {
        // First, handle container types; they require recursion
        if (type.isArrayType()) {
            return "Array";
        }

        if (type.isMapLikeType() && ReflectionUtils.isSystemType(type)) {
            return "Map";
        }

        if (type.isContainerType() && ReflectionUtils.isSystemType(type)) {
            if (Set.class.isAssignableFrom(type.getRawClass())) {
                return "Set";
            }
            return "List";
        }
        if (beanDesc == null) {
            beanDesc = mapper.getSerializationConfig().introspectClassAnnotations(type);
        }

        PropertyName rootName = mapper.getSerializationConfig().getAnnotationIntrospector().findRootName(beanDesc.getClassInfo());
        if (rootName != null && rootName.hasSimpleName()) {
            return rootName.getSimpleName();
        }
        return type.getRawClass().getSimpleName();
    }


    /**
     * 获取Jackson定义的JavaType
     *
     * @param type 原类型
     * @return jackson类型
     */
    private JavaType getJavaType(Type type) {
        final JavaType javaType;
        if (type instanceof JavaType) {
            javaType = (JavaType) type;
        } else {
            javaType = mapper.constructType(type);
        }
        return javaType;
    }

}