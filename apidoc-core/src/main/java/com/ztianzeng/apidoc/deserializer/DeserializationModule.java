package com.ztianzeng.apidoc.deserializer;

import com.fasterxml.jackson.databind.module.SimpleModule;
import com.ztianzeng.apidoc.models.Paths;
import com.ztianzeng.apidoc.models.callbacks.Callback;
import com.ztianzeng.apidoc.models.headers.Header;
import com.ztianzeng.apidoc.models.media.Encoding;
import com.ztianzeng.apidoc.models.media.EncodingProperty;
import com.ztianzeng.apidoc.models.media.Schema;
import com.ztianzeng.apidoc.models.parameters.Parameter;
import com.ztianzeng.apidoc.models.responses.ApiResponses;
import com.ztianzeng.apidoc.models.security.SecurityScheme;
import com.ztianzeng.apidoc.swagger.EncodingPropertyStyleEnumDeserializer;
import com.ztianzeng.apidoc.swagger.EncodingStyleEnumDeserializer;
import com.ztianzeng.apidoc.swagger.HeaderStyleEnumDeserializer;

public class DeserializationModule extends SimpleModule {

    public DeserializationModule() {

        this.addDeserializer(Schema.class, new ModelDeserializer());
        this.addDeserializer(Parameter.class, new ParameterDeserializer());
        this.addDeserializer(Header.StyleEnum.class, new HeaderStyleEnumDeserializer());
        this.addDeserializer(Encoding.StyleEnum.class, new EncodingStyleEnumDeserializer());
        this.addDeserializer(EncodingProperty.StyleEnum.class, new EncodingPropertyStyleEnumDeserializer());

        this.addDeserializer(SecurityScheme.class, new SecuritySchemeDeserializer());

        this.addDeserializer(ApiResponses.class, new ApiResponsesDeserializer());
        this.addDeserializer(Paths.class, new PathsDeserializer());
        this.addDeserializer(Callback.class, new CallbackDeserializer());
    }
}
