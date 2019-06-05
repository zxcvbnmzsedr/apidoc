package com.ztianzeng.apidoc.swagger.models;

import lombok.Data;

/**
 * @author zhaotianzeng
 * @version V1.0
 * @date 2019-06-05 22:20
 */
@Data
public class Pet {
    private String type;

    private String name;

    private Boolean isDomestic;
}