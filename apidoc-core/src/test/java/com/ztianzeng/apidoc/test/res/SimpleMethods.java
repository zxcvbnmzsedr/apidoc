package com.ztianzeng.apidoc.test.res;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

public class SimpleMethods {

    @GetMapping("/object")
    public TestBean getTestBean() {
        return new TestBean();
    }

    @GetMapping("/int")
    public int getInt() {
        return 0;
    }

    @GetMapping("/intArray")
    public int[] getIntArray() {
        return new int[]{0};
    }

    @GetMapping("/string")
    public String[] getStringArray() {
        return new String[]{};
    }

    @GetMapping("/stringArray")
    public void getWithIntArrayInput(@RequestParam("ids") int[] inputs) {
    }

    public static class TestBean {
        public String foo;
        public TestChild testChild;
    }

    public static class TestChild {
        public String foo;
    }
}