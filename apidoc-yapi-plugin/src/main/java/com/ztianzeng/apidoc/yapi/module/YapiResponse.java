package com.ztianzeng.apidoc.yapi.module;

import lombok.Data;

import java.io.Serializable;

/**
 * yapi 返回结果
 *
 * @author chengsheng@qbb6.com
 * @date 2019/1/31 12:08 PM
 */
@Data
public class YapiResponse implements Serializable {
    /**
     * 状态码
     */
    private Integer errcode;
    /**
     * 状态信息
     */
    private String errmsg;
    /**
     * 返回结果
     */
    private Object data;


    public YapiResponse() {
    }

    public YapiResponse(Object data) {
        this.errcode = 0;
        this.errmsg = "success";
        this.data = data;
    }

    public YapiResponse(Integer errcode, String errmsg) {
        this.errcode = errcode;
        this.errmsg = errmsg;
    }
}
