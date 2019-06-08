package com.ztianzeng.apidoc.model;

import com.ztianzeng.apidoc.constants.RequestMethod;
import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * api文档
 *
 * @author tianzeng
 */
@Data
public class ApiMethodDoc {

    /**
     * 详情描述
     */
    private String description;

    private String url;


    private String path;

    /**
     * 概述
     */
    private String summary;

    /**
     * HTTP请求方式
     */
    private RequestMethod requestMethod;

    private String headers;

    private String contentType = "application/json";

    /**
     * 具体的方法名
     */
    private String methodName;

    /**
     * 是否已过期
     */
    private boolean deprecated;


    private List<Parameters> requestParams;


    private Map<String, Parameters> responseBody;


}
