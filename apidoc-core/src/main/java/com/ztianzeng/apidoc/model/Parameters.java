package com.ztianzeng.apidoc.model;

import lombok.*;

import java.util.List;

/**
 * 参数实体
 *
 * @author zhaotianzeng
 * @version V1.0
 * @date 2019-06-05 13:37
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
public class Parameters {
    /**
     * 是否必须
     */
    private boolean required;

    /**
     * 字段名
     */
    private String name;

    /**
     * 字段描述
     */
    private String description;

    /**
     * 字段类型
     */
    private String type;

    /**
     * 嵌套对象的详情
     */
    private List<Parameters> detail;

    public Parameters(String type) {
        this.type = type;
    }
}