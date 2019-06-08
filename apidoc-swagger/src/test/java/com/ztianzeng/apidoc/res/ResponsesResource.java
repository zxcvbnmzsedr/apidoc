package com.ztianzeng.apidoc.res;


import lombok.Data;
import org.springframework.web.bind.annotation.GetMapping;

public class ResponsesResource {

    /**
     * @return voila!
     */
    @GetMapping("/")
    public SampleResponseSchema getResponses() {
        return new SampleResponseSchema();
    }

    @Data
    public static class SampleResponseSchema {
        private String id;
    }

}
