package com.ztianzeng.apidoc.test.res;

import org.springframework.web.bind.annotation.GetMapping;

/**
 * @author zhaotianzeng
 * @version V1.0
 * @date 2019-06-10 17:34
 */
public class MyBeanParamResource {
    @GetMapping("/")
    public String getWithBeanParam(ListOfStringsBeanParam listOfStringsBean) {
        return "result";
    }
}