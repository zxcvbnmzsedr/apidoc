package com.ztianzeng.apidoc.test.res;

import lombok.Data;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

public class ResponseContentWithArrayResource {

    @GetMapping("/user")
    public List<User> getUsers() {
        return null;
    }

    @Data
    public static class User {
        public String foo;
    }
}
