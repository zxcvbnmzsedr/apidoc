package com.ztianzeng.apidoc.test.res;

import org.springframework.web.bind.annotation.GetMapping;

/**
 * @author zhaotianzeng
 * @version V1.0
 * @date 2019-06-12 18:33
 */
public class MuRetController {
    /**
     * 获取一个实例
     *
     * @return 返回信息
     */
    @GetMapping(value = "/get")
    public Result<Result2<CreateParam>> get() {
        return new Result<>();
    }
}