package com.ztianzeng.apidoc.test;

import lombok.Data;

import javax.validation.constraints.NotEmpty;

/**
 * @author zhaotianzeng
 * @version V1.0
 * @date 2019-05-27 13:37
 */
@Data
public class CreateParam {
    /**
     * 用户名
     */
    @NotEmpty
    private String username;
    /**
     * 手机
     */
    private String mobile;

    private CreateParam2 createParam2;
}