package com.ztianzeng.apidoc.test.res;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

public class DuplicatedOperationMethodNameResource {

    @GetMapping("/1")
    public void getSummaryAndDescription1() {
    }

    @GetMapping("/2")
    public void getSummaryAndDescription2() {
    }

    @PostMapping("/2")
    public void postSummaryAndDescription2() {
    }

    @GetMapping("/3")
    public void getSummaryAndDescription3() {
    }

    @PostMapping("/3")
    public void postSummaryAndDescription3() {
    }

    @GetMapping("/4")
    public void getSummaryAndDescription3(String foo) {
    }

}
