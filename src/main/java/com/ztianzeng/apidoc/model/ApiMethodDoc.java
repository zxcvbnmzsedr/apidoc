package com.ztianzeng.apidoc.model;

import com.ztianzeng.apidoc.constants.RequestMethod;
import lombok.Data;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * api文档
 */
@Data
public class ApiMethodDoc implements Serializable {

    private String desc;

    private String url;

    /**
     * HTTP请求方式
     */
    private RequestMethod requestMethod;

    private String headers;

    private String contentType = "application/x-www-form-urlencoded";

    private List<Parameters> requestParams;


    private Map<String, String> responseBody;



}
