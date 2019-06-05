package com.ztianzeng.apidoc.constants;

import java.util.HashMap;
import java.util.Map;

/**
 * @author zhaotianzeng
 * @version V1.0
 * @date 2019-05-27 13:18
 */
public class SpringMvcConstants {

    public static final String REQUEST_MAPPING = "RequestMapping";
    public static final String REQUEST_MAPPING_FULLY = "org.springframework.web.bind.annotation.RequestMapping";

    private static final String GET_MAPPING = "GetMapping";

    private static final String GET_MAPPING_FULLY = "org.springframework.web.bind.annotation.GetMapping";

    private static final String POST_MAPPING = "PostMapping";

    private static final String POST_MAPPING_FULLY = "org.springframework.web.bind.annotation.PostMapping";

    private static final String PUT_MAPPING = "PutMapping";

    private static final String PUT_MAPPING_FULLY = "org.springframework.web.bind.annotation.PutMapping";

    private static final String DELETE_MAPPING = "DeleteMapping";

    private static final String DELETE_MAPPING_FULLY = "org.springframework.web.bind.annotation.DeleteMapping";

    public static final Map<String, RequestMethod> METHOD_MAP = new HashMap<>();

    /**
     * controller注解全名称
     */
    public static final String CONTROLLER_FULLY = "org.springframework.stereotype.Controller";

    /**
     * rest controller注解全名称
     */
    public static final String REST_CONTROLLER_FULLY = "org.springframework.web.bind.annotation.RestController";

    /**
     * RequestBody
     */
    public static final String REQUEST_BODY = "RequestBody";

    /**
     * 初始化Spring枚举到Http方法的对应
     */
    static {
        METHOD_MAP.put(GET_MAPPING, RequestMethod.GET);
        METHOD_MAP.put(GET_MAPPING_FULLY, RequestMethod.GET);
        METHOD_MAP.put(POST_MAPPING, RequestMethod.POST);
        METHOD_MAP.put(POST_MAPPING_FULLY, RequestMethod.POST);
        METHOD_MAP.put(PUT_MAPPING, RequestMethod.PUT);
        METHOD_MAP.put(PUT_MAPPING_FULLY, RequestMethod.PUT);
        METHOD_MAP.put(DELETE_MAPPING, RequestMethod.DELETE);
        METHOD_MAP.put(DELETE_MAPPING_FULLY, RequestMethod.DELETE);
    }


}