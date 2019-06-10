package com.ztianzeng.apidoc;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.thoughtworks.qdox.JavaProjectBuilder;
import com.ztianzeng.apidoc.converter.AnnotatedType;
import com.ztianzeng.apidoc.converter.ModelConverter;
import com.ztianzeng.apidoc.converter.ModelConverterContext;
import com.ztianzeng.apidoc.models.media.Schema;

import java.util.*;


/**
 * 模型解析器
 *
 * @author zhaotianzeng
 * @version V1.0
 * @date 2019-06-06 13:00
 */
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
//        JavaClass classByName = builder.getClassByName(annotatedType.getType().getTypeName());

//        Schema parameters = sourceBuilder.parsingBody(classByName);

        Schema schema = new Schema();
//        schema.name(classByName.getName());
//        PrimitiveType parentType = PrimitiveType.fromType(DocUtils.getTypeForName(classByName.getBinaryName()));

//        schema.setType(parentType.getCommonName());

        List<String> requiredList = new LinkedList<>();
        Map<String, Schema> properties = new HashMap<>();


//        for (Parameters requestParam : parameters) {
//            String name = requestParam.getName();
//            Schema prop = new Schema();
//            PrimitiveType primitiveType = PrimitiveType.fromType(requestParam.getType());
//
//            if (primitiveType != null) {
//                prop = primitiveType.createProperty();
//            } else {
//                prop.setType("object");
//            }
//            prop.setDescription(requestParam.getDescription());
//
//            final JavaType type = mapper.constructType(requestParam.getType());
//
//            BeanPropertyDefinition beanDescription = requestParam.getBeanDescription();
//            JavaType valueType = beanDescription.getPrimaryType().getContentType();
//
//            // 处理集合问题
//            if (type.isContainerType()) {
//
//
//                Schema addPropertiesSchema = context.resolve(
//                        new AnnotatedType()
//                                .type(valueType.getRawClass())
//                                .schemaProperty(annotatedType.isSchemaProperty())
//                                .skipSchemaName(true)
//                                .resolveAsRef(annotatedType.isResolveAsRef())
//                                .jsonViewAnnotation(annotatedType.getJsonViewAnnotation())
//                                .propertyName(annotatedType.getPropertyName())
//                                .parent(annotatedType.getParent())
//                );
//
//
//                prop = new MapSchema().additionalProperties(addPropertiesSchema);
//                prop.name(name);
//            } else {
//                if ("object".equals(prop.getType())) {
//                    prop = new Schema().$ref(RefUtils.constructRef(beanDescription.getPrimaryType().getRawClass().getSimpleName()));
//
//                }
//            }
//
//
//            if (requestParam.isRequired()) {
//                requiredList.add(requestParam.getName());
//            }
//            properties.put(requestParam.getName(), prop);
//        }

        schema.setProperties(properties);
        schema.setRequired(requiredList);


        return schema;
    }
}