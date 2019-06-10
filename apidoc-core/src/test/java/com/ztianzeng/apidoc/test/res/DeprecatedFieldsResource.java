package com.ztianzeng.apidoc.test.res;

import org.springframework.web.bind.annotation.GetMapping;

public class DeprecatedFieldsResource {
    @GetMapping("/")
    @Deprecated
    public void deprecatedMethod() {

    }
}