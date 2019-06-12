package com.ztianzeng.apidoc;

import com.thoughtworks.qdox.JavaProjectBuilder;
import com.thoughtworks.qdox.model.JavaClass;
import com.thoughtworks.qdox.model.JavaField;
import com.thoughtworks.qdox.model.JavaType;
import com.thoughtworks.qdox.model.impl.DefaultJavaParameterizedType;
import com.ztianzeng.apidoc.converter.AnnotatedType;
import com.ztianzeng.apidoc.converter.ModelConverter;
import com.ztianzeng.apidoc.converter.ModelConverterContext;
import com.ztianzeng.apidoc.models.media.ArraySchema;
import com.ztianzeng.apidoc.models.media.MapSchema;
import com.ztianzeng.apidoc.models.media.PrimitiveType;
import com.ztianzeng.apidoc.models.media.Schema;
import com.ztianzeng.apidoc.utils.DocUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.util.*;

import static com.ztianzeng.apidoc.utils.DocUtils.genericityContentType;
import static com.ztianzeng.apidoc.utils.DocUtils.genericityCount;
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
    private JavaProjectBuilder builder;


    public ModelResolver(SourceBuilder sourceBuilder) {
        builder = sourceBuilder.getBuilder();
    }

    @Override
    public Schema resolve(AnnotatedType annotatedType,
                          ModelConverterContext context,
                          Iterator<ModelConverter> chain) {
        if (annotatedType == null) {
            return null;
        }


        // 分析目标类信息
        JavaClass targetClass = annotatedType.getJavaClass();


        Schema schema = new Schema();

        String parentName = annotatedType.getName();
        if (StringUtils.isBlank(parentName)) {
            parentName = findName(targetClass, new StringBuilder());
        }


        Schema resolvedModel = context.resolve(annotatedType);
        if (resolvedModel != null) {
            if (parentName.equals(resolvedModel.getName())) {
                return resolvedModel;
            }
        }

        // 如果泛型大于0
        if (genericityCount(targetClass) > 0) {
            JavaClass aClass = genericityContentType(targetClass);
            if (aClass != null) {
                AnnotatedType aType = new AnnotatedType()
                        .javaClass(aClass)
                        .parent(schema)
                        .resolveAsRef(annotatedType.isResolveAsRef())
                        .jsonViewAnnotation(annotatedType.getJsonViewAnnotation())
                        .skipSchemaName(true)
                        .schemaProperty(true)
                        .propertyName(targetClass.getName());
                context.resolve(aType);

            }
        }

        // 转换成OpenApi定义的字段信息
        PrimitiveType parentType = PrimitiveType.fromType(targetClass.getBinaryName());
        schema.setType(Optional.ofNullable(parentType).orElse(PrimitiveType.OBJECT).getCommonName());
        if (DocUtils.isPrimitive(targetClass.getName())) {
            if (targetClass.isArray()) {
                Schema array = new Schema();
                array.setType(schema.getType());
                schema = new ArraySchema().items(array);
            }
            return schema;
        }
        schema.name(parentName);

        // 分析类的字段
        List<JavaField> fields = new ArrayList<>();

        // 循环当前类信息，所有属性都加到fields
        JavaClass cls = targetClass;
        while (cls != null && !cls.isArray() && !"java.lang.Object".equals(cls.getFullyQualifiedName())) {
            fields.addAll(cls.getFields());
            cls = cls.getSuperJavaClass();
        }

        if (targetClass.isA(Map.class.getName())) {
            // 泛型信息
            List<JavaType> tar = ((DefaultJavaParameterizedType) targetClass).getActualTypeArguments();
            if (tar.isEmpty()) {
                return null;
            }
            JavaType javaType = tar.get(1);

            Schema addPropertiesSchema = context.resolve(
                    new AnnotatedType()
                            .javaClass(builder.getClassByName(javaType.getFullyQualifiedName()))
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
        } else if (targetClass.isA(Collection.class.getName())
                || targetClass.isA(List.class.getName())) {
            // 泛型信息
            List<JavaType> tar = ((DefaultJavaParameterizedType) targetClass).getActualTypeArguments();
            if (tar.isEmpty()) {
                return null;
            }
            JavaType javaType = tar.get(0);
            // 处理集合
            Schema items = context.resolve(new AnnotatedType()
                    .javaClass(builder.getClassByName(javaType.getFullyQualifiedName()))
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


        for (JavaField field : fields) {
            if (DocUtils.isPrimitive(field.getName())) {
                continue;
            }
            JavaClass type = genericityContentType(targetClass) == null ? field.getType() : genericityContentType(targetClass);

            AnnotatedType aType = new AnnotatedType()
                    .javaClass(type)
                    .parent(schema)
                    .resolveAsRef(annotatedType.isResolveAsRef())
                    .jsonViewAnnotation(annotatedType.getJsonViewAnnotation())
                    .skipSchemaName(true)
                    .schemaProperty(true)
                    .propertyName(targetClass.getName());
            // 属性是否为require
            boolean required = DocUtils.isRequired(field);
            // 属性名称
            String name = field.getName();
            Schema propSchema = new Schema();
            propSchema.setName(name);

            PrimitiveType primitiveType = PrimitiveType.fromType(field.getType().getFullyQualifiedName());
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

    /**
     * 设置泛型
     *
     * @param type
     * @param schema
     * @param annotatedType
     * @param targetClass
     * @param context
     */
    public void setRefType(JavaClass type, Schema schema, AnnotatedType annotatedType, JavaClass targetClass, ModelConverterContext context) {
        List<JavaType> ref = ((DefaultJavaParameterizedType) type).getActualTypeArguments();
        if (!ref.isEmpty()) {
            for (JavaType actualTypeArgument : ref) {
                AnnotatedType aType = new AnnotatedType()
                        .javaClass((DefaultJavaParameterizedType) actualTypeArgument)
                        .parent(schema)
                        .resolveAsRef(annotatedType.isResolveAsRef())
                        .jsonViewAnnotation(annotatedType.getJsonViewAnnotation())
                        .skipSchemaName(true)
                        .schemaProperty(true)
                        .propertyName(targetClass.getName());
                context.resolve(aType);
            }
        }
    }

    /**
     * 设置泛型
     *
     * @param type
     */
    public String findName(JavaClass type, StringBuilder stringBuilder) {

        stringBuilder.append(type.getName());
        if (type instanceof DefaultJavaParameterizedType) {
            List<JavaType> ref = ((DefaultJavaParameterizedType) type).getActualTypeArguments();
            if (!ref.isEmpty()) {
                for (JavaType actualTypeArgument : ref) {
                    if (actualTypeArgument instanceof DefaultJavaParameterizedType) {
                        return findName((DefaultJavaParameterizedType) actualTypeArgument, stringBuilder);
                    }
                }
            }
        }
        return stringBuilder.toString();
    }

}