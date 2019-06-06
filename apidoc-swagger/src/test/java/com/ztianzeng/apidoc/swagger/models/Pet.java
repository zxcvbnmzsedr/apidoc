package com.ztianzeng.apidoc.swagger.models;

import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

/**
 * @author zhaotianzeng
 * @version V1.0
 * @date 2019-06-05 22:20
 */
@Data
public class Pet {
    /**
     * The pet type
     */
    @NotEmpty
    private String type;

    /**
     * The name of the pet
     */
    @NotEmpty
    private String name;

    @NotNull
    private Boolean isDomestic;
}