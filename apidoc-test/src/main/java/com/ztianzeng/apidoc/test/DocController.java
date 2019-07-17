package com.ztianzeng.apidoc.test;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.io.InputStream;

/**
 * @author zhaotianzeng
 * @version V1.0
 * @date 2019-07-16 22:15
 */
@CrossOrigin(maxAge = 3600)
@RestController
public class DocController {
    @GetMapping("/api")
    public String api() throws IOException {
        InputStream resourceAsStream = getClass().getClassLoader().getResourceAsStream("doc.json");
        byte[] bytes = new byte[resourceAsStream.available()];
        resourceAsStream.read(bytes);
        return new String(bytes);
    }
}