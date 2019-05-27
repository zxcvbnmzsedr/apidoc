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

    public static final Map<String, String> METHOD_MAP = new HashMap<>();

    /**
     * controller注解全名称
     */
    public static final String CONTROLLER_FULLY = "org.springframework.stereotype.Controller";

    /**
     * rest controller注解全名称
     */
    public static final String REST_CONTROLLER_FULLY = "org.springframework.web.bind.annotation.RestController";

    static {
        METHOD_MAP.put(GET_MAPPING, "GET");
        METHOD_MAP.put(GET_MAPPING_FULLY, "GET");
        METHOD_MAP.put(POST_MAPPING, "POST");
        METHOD_MAP.put(POST_MAPPING_FULLY, "POST");
        METHOD_MAP.put(PUT_MAPPING, "PUT");
        METHOD_MAP.put(PUT_MAPPING_FULLY, "PUT");
        METHOD_MAP.put(DELETE_MAPPING, "DELETE");
        METHOD_MAP.put(DELETE_MAPPING_FULLY, "DELETE");
    }


}