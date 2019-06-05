package com.ztianzeng.apidoc.test;

import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

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
     *
     * @param createParam 创建对象
     */
    @PostMapping(value = "/create")
    public void add(@RequestBody @Valid CreateParam createParam) {

    }

    /**
     * 获取一个实例
     *
     * @param userId 用户ID
     * @param sex    性别
     * @return 返回信息
     */
    @GetMapping(value = "/get")
    public String get(String userId, String sex) {
        return "";
    }
}