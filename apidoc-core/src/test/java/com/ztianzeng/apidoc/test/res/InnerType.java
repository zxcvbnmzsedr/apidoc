package com.ztianzeng.apidoc.test.res;

import lombok.Data;

@Data
public class InnerType {
    public int foo;
    public String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}