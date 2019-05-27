package com.ztianzeng.apidoc.test;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * 测试控制器
 *
 * @author zhaotianzeng
 * @version V1.0
 * @date 2019-05-27 13:22
 */
@RequestMapping("/test")
@RestController
public class TestController {

    /**
     * 新增一个实例
     */
    @RequestMapping(value = "/create",method = RequestMethod.POST)
    public void add() {

    }

    /**
     * 获取一个实例
     */
    @GetMapping(value = "/get")
    public void get() {

    }
}