package com.ztianzeng.apidoc.test.res;

import lombok.Getter;

/**
 * @author zhaotianzeng
 * @version V1.0
 * @date 2019-06-11 13:13
 */
@Getter
public class Result2<T> {
    private String msg;
    private T data;

    protected Result2() {
    }

    private Result2(String msg, T t) {
        this.msg = msg;
        this.data = t;
    }

}