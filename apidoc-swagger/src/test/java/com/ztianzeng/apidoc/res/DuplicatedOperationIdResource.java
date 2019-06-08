package com.ztianzeng.apidoc.res;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

public class DuplicatedOperationIdResource {

    @GetMapping("/")
    public void getSummaryAndDescription() {

    }

    @PostMapping
    public void postSummaryAndDescription() {
    }

    @GetMapping("/path")
    public void getDuplicatedOperation() {
    }

}
