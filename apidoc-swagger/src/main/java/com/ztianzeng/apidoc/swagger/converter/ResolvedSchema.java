package com.ztianzeng.apidoc.swagger.converter;

import com.ztianzeng.apidoc.swagger.models.media.Schema;

import java.util.Map;

public class ResolvedSchema {
    public Schema schema;
    public Map<String, Schema> referencedSchemas;
}
