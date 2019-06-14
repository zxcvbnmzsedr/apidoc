package com.ztianzeng.apidoc.yapi.module;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * yapi 保存请求参数
 *
 * @author chengsheng@qbb6.com
 * @date 2019/1/31 11:43 AM
 */
@Data
public class YapiSaveParam implements Serializable {
    /**
     * 项目 token  唯一标识
     */
    private String token;

    /**
     * 请求参数
     */
    private List req_query;
    /**
     * header
     */
    private List req_headers;
    /**
     * 请求参数 form 类型
     */
    private List req_body_form;
    /**
     * 标题
     */
    private String title;
    /**
     * 分类id
     */
    private String catid;
    /**
     * 请求数据类型   raw,form,json
     */
    private String req_body_type = "json";
    /**
     * 请求数据body
     */
    private String req_body_other;
    /**
     * 请求参数body 是否为json_schema
     */
    private boolean req_body_is_json_schema;
    /**
     * 路径
     */
    private String path;
    /**
     * 状态 undone,默认done
     */
    private String status = "done";
    /**
     * 返回参数类型  json
     */
    private String res_body_type = "json";

    /**
     * 返回参数
     */
    private String res_body;

    /**
     * 返回参数是否为json_schema
     */
    private boolean res_body_is_json_schema = true;

    /**
     * 创建的用户名
     */
    private Integer edit_uid = 11;
    /**
     * 用户名称
     */
    private String username;

    /**
     * 邮件开关
     */
    private boolean switch_notice;

    private String message = " ";
    /**
     * 文档描述
     */
    private String desc = "<h3>请补充描述</h3>";

    /**
     * 请求方式
     */
    private String method = "POST";
    /**
     * 请求参数
     */
    private List req_params;


    private String id;
    /**
     * 项目id
     */
    private Integer projectId;

    /**
     * yapi 地址
     */
    private String yapiUrl;
    /**
     * 菜单名称
     */
    private String menu;


    public YapiSaveParam(String token, String title, String path, String req_body_other, String res_body, Integer projectId, String yapiUrl, String desc) {
        this.token = token;
        this.title = title;
        this.path = path;
        this.res_body = res_body;
        this.req_body_other = req_body_other;
        this.projectId = projectId;
        this.yapiUrl = yapiUrl;
        this.desc = desc;
    }


    public YapiSaveParam(String token, String title, String path, List req_query, String req_body_other, String res_body, Integer projectId, String yapiUrl, boolean req_body_is_json_schema, String method, String desc, List header) {
        this.token = token;
        this.title = title;
        this.path = path;
        this.req_query = req_query;
        this.res_body = res_body;
        this.req_body_other = req_body_other;
        this.projectId = projectId;
        this.yapiUrl = yapiUrl;
        this.req_body_is_json_schema = req_body_is_json_schema;
        this.method = method;
        this.desc = desc;
        this.req_headers = header;
    }


}
