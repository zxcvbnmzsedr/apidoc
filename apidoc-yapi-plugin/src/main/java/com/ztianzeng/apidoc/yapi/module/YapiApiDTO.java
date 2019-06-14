package com.ztianzeng.apidoc.yapi.module;

import lombok.Data;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * yapi dto
 *
 * @author chengsheng@qbb6.com
 * @date 2019/2/11 3:16 PM
 */
@Data
public class YapiApiDTO implements Serializable {
    /**
     * 路径
     */
    private String path;
    /**
     * 请求参数
     */
    private List<YapiQueryDTO> params;
    /**
     * 头信息
     */
    private List header;
    /**
     * title
     */
    private String title;
    /**
     * 响应
     */
    private String response;
    /**
     * 请求体
     */
    private String requestBody;

    private String tag;

    /**
     * 请求方法
     */
    private String method = "POST";

    /**
     * 请求 类型 raw,form,json
     */
    private String req_body_type;
    /**
     * 请求form
     */
    private List<Map<String, String>> req_body_form;

    /**
     * 描述
     */
    private String desc;
    /**
     * 菜单
     */
    private String menu;

    /**
     * 请求参数
     */
    private List req_params;


    public YapiApiDTO() {
    }


}
