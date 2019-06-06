package com.ztianzeng.apidoc.swagger.util;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.ser.BeanSerializerModifier;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.fasterxml.jackson.dataformat.yaml.YAMLGenerator;
import com.ztianzeng.apidoc.models.*;
import com.ztianzeng.apidoc.models.callbacks.Callback;
import com.ztianzeng.apidoc.models.examples.Example;
import com.ztianzeng.apidoc.models.headers.Header;
import com.ztianzeng.apidoc.models.info.Contact;
import com.ztianzeng.apidoc.models.info.Info;
import com.ztianzeng.apidoc.models.info.License;
import com.ztianzeng.apidoc.models.links.Link;
import com.ztianzeng.apidoc.models.links.LinkParameter;
import com.ztianzeng.apidoc.models.media.*;
import com.ztianzeng.apidoc.models.responses.ApiResponse;
import com.ztianzeng.apidoc.models.responses.ApiResponses;
import com.ztianzeng.apidoc.models.security.OAuthFlow;
import com.ztianzeng.apidoc.models.security.OAuthFlows;
import com.ztianzeng.apidoc.models.security.Scopes;
import com.ztianzeng.apidoc.models.security.SecurityScheme;
import com.ztianzeng.apidoc.models.servers.Server;
import com.ztianzeng.apidoc.models.servers.ServerVariable;
import com.ztianzeng.apidoc.models.servers.ServerVariables;
import com.ztianzeng.apidoc.models.tags.Tag;
import com.ztianzeng.apidoc.swagger.*;
import com.ztianzeng.apidoc.swagger.parameters.Parameter;
import com.ztianzeng.apidoc.swagger.parameters.RequestBody;

import java.util.LinkedHashMap;
import java.util.Map;

public class ObjectMapperFactory {

    protected static ObjectMapper createJson() {
        return create(null);
    }

    protected static ObjectMapper createYaml() {
        YAMLFactory factory = new YAMLFactory();
        factory.disable(YAMLGenerator.Feature.WRITE_DOC_START_MARKER);
        factory.enable(YAMLGenerator.Feature.MINIMIZE_QUOTES);
        factory.enable(YAMLGenerator.Feature.SPLIT_LINES);
        factory.enable(YAMLGenerator.Feature.ALWAYS_QUOTE_NUMBERS_AS_STRINGS);

        return create(factory);
    }

    private static ObjectMapper create(JsonFactory jsonFactory) {
        ObjectMapper mapper = jsonFactory == null ? new ObjectMapper() : new ObjectMapper(jsonFactory);

        // handle ref schema serialization skipping all other props
        mapper.registerModule(new SimpleModule() {
            @Override
            public void setupModule(SetupContext context) {
                super.setupModule(context);
                context.addBeanSerializerModifier(new BeanSerializerModifier() {
                    @Override
                    public JsonSerializer<?> modifySerializer(
                            SerializationConfig config, BeanDescription desc, JsonSerializer<?> serializer) {
                        if (Schema.class.isAssignableFrom(desc.getBeanClass())) {
                            return new SchemaSerializer((JsonSerializer<Object>) serializer);
                        }
                        return serializer;
                    }
                });
            }
        });

        Module deserializerModule = new DeserializationModule();
        mapper.registerModule(deserializerModule);

        Map<Class<?>, Class<?>> sourceMixins = new LinkedHashMap<>();

        sourceMixins.put(ApiResponses.class, ExtensionsMixin.class);
        sourceMixins.put(ApiResponse.class, ExtensionsMixin.class);
        sourceMixins.put(Callback.class, ExtensionsMixin.class);
        sourceMixins.put(Components.class, ComponentsMixin.class);
        sourceMixins.put(Contact.class, ExtensionsMixin.class);
        sourceMixins.put(Encoding.class, ExtensionsMixin.class);
        sourceMixins.put(EncodingProperty.class, ExtensionsMixin.class);
        sourceMixins.put(Example.class, ExtensionsMixin.class);
        sourceMixins.put(ExternalDocumentation.class, ExtensionsMixin.class);
        sourceMixins.put(Header.class, ExtensionsMixin.class);
        sourceMixins.put(Info.class, ExtensionsMixin.class);
        sourceMixins.put(License.class, ExtensionsMixin.class);
        sourceMixins.put(Link.class, ExtensionsMixin.class);
        sourceMixins.put(LinkParameter.class, ExtensionsMixin.class);
        sourceMixins.put(MediaType.class, ExtensionsMixin.class);
        sourceMixins.put(OAuthFlow.class, ExtensionsMixin.class);
        sourceMixins.put(OAuthFlows.class, ExtensionsMixin.class);
        sourceMixins.put(OpenAPI.class, OpenAPIMixin.class);
        sourceMixins.put(Operation.class, OperationMixin.class);
        sourceMixins.put(Parameter.class, ExtensionsMixin.class);
        sourceMixins.put(PathItem.class, ExtensionsMixin.class);
        sourceMixins.put(Paths.class, ExtensionsMixin.class);
        sourceMixins.put(RequestBody.class, ExtensionsMixin.class);
        sourceMixins.put(Scopes.class, ExtensionsMixin.class);
        sourceMixins.put(SecurityScheme.class, ExtensionsMixin.class);
        sourceMixins.put(Server.class, ExtensionsMixin.class);
        sourceMixins.put(ServerVariable.class, ExtensionsMixin.class);
        sourceMixins.put(ServerVariables.class, ExtensionsMixin.class);
        sourceMixins.put(Tag.class, ExtensionsMixin.class);
        sourceMixins.put(XML.class, ExtensionsMixin.class);
        sourceMixins.put(Schema.class, ExtensionsMixin.class);

        mapper.setMixIns(sourceMixins);
        mapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
        mapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
        mapper.configure(SerializationFeature.WRITE_ENUMS_USING_TO_STRING, true);
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        mapper.configure(SerializationFeature.WRITE_NULL_MAP_VALUES, false);
        mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);

        return mapper;
    }
}
