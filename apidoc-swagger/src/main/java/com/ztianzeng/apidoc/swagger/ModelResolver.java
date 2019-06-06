package com.ztianzeng.apidoc.swagger;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.thoughtworks.qdox.JavaProjectBuilder;
import com.thoughtworks.qdox.model.JavaClass;
import com.ztianzeng.apidoc.SourceBuilder;
import com.ztianzeng.apidoc.model.Parameters;
import com.ztianzeng.apidoc.swagger.converter.AnnotatedType;
import com.ztianzeng.apidoc.swagger.converter.ModelConverter;
import com.ztianzeng.apidoc.swagger.converter.ModelConverterContext;
import com.ztianzeng.apidoc.models.media.MapSchema;
import com.ztianzeng.apidoc.models.media.PrimitiveType;
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
        JavaClass classByName = builder.getClassByName(annotatedType.getType().getTypeName());

        final JavaType jacksonJavaType = mapper.constructType(annotatedType.getType());

        List<Parameters> parameters = sourceBuilder.parsingBody(classByName);

        Schema schema = new Schema();
        schema.name(classByName.getName());

        List<String> requiredList = new LinkedList<>();
        Map<String, Schema> properties = new HashMap<>();


        for (Parameters requestParam : parameters) {
            String name = requestParam.getName();
            Schema prop = new Schema();
            PrimitiveType primitiveType = PrimitiveType.fromType(requestParam.getType());

            if (primitiveType != null) {
                prop = primitiveType.createProperty();
            } else {
                prop.setType("object");
            }
            prop.setDescription(requestParam.getDescription());

            final JavaType type = mapper.constructType(requestParam.getType());


            if (type.isContainerType()) {
                JavaType keyType = type.getKeyType();

                JavaType valueType = type.getContentType();

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


                prop = new MapSchema().additionalProperties(addPropertiesSchema);
                prop.name(name);
            }


            if (requestParam.isRequired()) {
                requiredList.add(requestParam.getName());
            }
            properties.put(requestParam.getName(), prop);
        }
        schema.setType("object");
        schema.setProperties(properties);
        schema.setRequired(requiredList);


        return schema;
    }
}