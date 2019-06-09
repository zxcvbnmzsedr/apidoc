package com.ztianzeng.apidoc.res;

import lombok.Data;
import org.springframework.web.bind.annotation.GetMapping;

public class EnhancedResponsesResource {

    /**
     * Simple get operation
     * <p>Defines a simple get operation with no inputs and a complex output object</p>
     *
     * @return voila!
     */
    @GetMapping("/")
    @Deprecated
    public SampleResponseSchema getResponses() {
        return new SampleResponseSchema();
    }

    @Data
    static class SampleResponseSchema {
        /**
         * the user id
         */
        private String id;
    }


}
