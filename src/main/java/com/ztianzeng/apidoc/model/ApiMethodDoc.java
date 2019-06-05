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

    private String description;

    private String url;

    /**
     * HTTP请求方式
     */
    private RequestMethod requestMethod;

    private String headers;

    private String contentType = "application/json";

    private List<Parameters> requestParams;


    private Map<String, String> responseBody;


}
