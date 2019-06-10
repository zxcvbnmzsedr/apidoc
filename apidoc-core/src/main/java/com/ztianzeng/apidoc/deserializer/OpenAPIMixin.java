package com.ztianzeng.apidoc.deserializer;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.ztianzeng.apidoc.models.Paths;

import java.util.Map;

public abstract class OpenAPIMixin {

    @JsonAnyGetter
    public abstract Map<String, Object> getExtensions();

    @JsonAnySetter
    public abstract void addExtension(String name, Object value);

    @JsonSerialize(using = PathsSerializer.class)
    public abstract Paths getPaths();
}
