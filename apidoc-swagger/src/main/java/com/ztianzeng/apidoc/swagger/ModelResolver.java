package com.ztianzeng.apidoc.swagger;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.thoughtworks.qdox.JavaProjectBuilder;
import com.thoughtworks.qdox.model.JavaClass;
import com.ztianzeng.apidoc.SourceBuilder;
import com.ztianzeng.apidoc.model.Parameters;
import com.ztianzeng.apidoc.swagger.converter.AnnotatedType;
import com.ztianzeng.apidoc.swagger.converter.ModelConverter;
import com.ztianzeng.apidoc.swagger.converter.ModelConverterContext;
import com.ztianzeng.apidoc.swagger.models.media.Schema;

import java.util.*;

/**
 * @author zhaotianzeng
 * @version V1.0
 * @date 2019-06-06 13:00
 */
public class ModelResolver implements ModelConverter {
    private SourceBuilder sourceBuilder;
    private JavaProjectBuilder builder;


    protected ObjectMapper mapper;

    public ModelResolver(ObjectMapper mapper) {
        this.mapper = mapper;
        sourceBuilder = new SourceBuilder("src/test/java");
        builder = sourceBuilder.getBuilder();
    }

    @Override
    public Schema resolve(AnnotatedType type,
                          ModelConverterContext context,
                          Iterator<ModelConverter> chain) {
        JavaClass classByName = builder.getClassByName(type.getType().getTypeName());
        List<Parameters> parameters = sourceBuilder.parsingBody(classByName);

        Schema schema = new Schema();
        schema.name(classByName.getName());

        List<String> requiredList = new LinkedList<>();
        Map<String, Schema> properties = new HashMap<>();
        for (Parameters requestParam : parameters) {
            Schema prop = new Schema();
            prop.setType(requestParam.getType().toLowerCase());
            prop.setDescription(requestParam.getDescription());

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