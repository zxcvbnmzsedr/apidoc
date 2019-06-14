package com.ztianzeng.apidoc.yapi.upload;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ztianzeng.apidoc.models.OpenAPI;
import com.ztianzeng.apidoc.models.Operation;
import com.ztianzeng.apidoc.models.Paths;
import com.ztianzeng.apidoc.utils.Json;
import com.ztianzeng.apidoc.yapi.constant.YapiConstant;
import com.ztianzeng.apidoc.yapi.module.*;
import com.ztianzeng.apidoc.yapi.utils.HttpClientUtil;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;

import java.io.IOException;
import java.util.*;

/**
 * 上传到yapi
 *
 * @author zhaotianzeng
 * @version V1.0
 * @date 2019-06-14 14:18
 */
public class UploadToYapi {
    public static Map<String, Map<String, Integer>> catMap = new HashMap<>();
    public final ObjectMapper mapper = Json.mapper();
    private final String projectToken;
    private final String yapiUrl;
    private final String projectId;

    public UploadToYapi(String projectToken, String yapiUrl, String projectId) {
        this.projectToken = projectToken;
        this.yapiUrl = yapiUrl;
        this.projectId = projectId;
    }

    /**
     * 上传OpenAPi
     *
     * @param openAPI
     */
    public void upload(OpenAPI openAPI) {
        List<YapiApiDTO> yapiApiDTOS = toYapiDTO(openAPI);
        if (yapiApiDTOS != null) {
            for (YapiApiDTO yapiApiDTO : yapiApiDTOS) {
                YapiSaveParam yapiSaveParam = new YapiSaveParam(projectToken, yapiApiDTO.getTitle(), yapiApiDTO.getPath(), yapiApiDTO.getParams(), yapiApiDTO.getRequestBody(), yapiApiDTO.getResponse(), null, yapiUrl, true, yapiApiDTO.getMethod(), yapiApiDTO.getDesc(), yapiApiDTO.getHeader());
                yapiSaveParam.setReq_body_form(yapiApiDTO.getReq_body_form());
                yapiSaveParam.setReq_body_type(yapiApiDTO.getReq_body_type());
                yapiSaveParam.setReq_params(yapiApiDTO.getReq_params());
                if (StringUtils.isNotEmpty(yapiApiDTO.getMenu())) {
                    yapiSaveParam.setMenu(yapiApiDTO.getMenu());
                } else {
                    yapiSaveParam.setMenu(YapiConstant.menu);
                }

                yapiSaveParam.setCatid(getCatIdOrCreate(yapiApiDTO.getTag()));
                try {
                    // 上传
                    YapiResponse yapiResponse = uploadSave(yapiSaveParam);
                    if (yapiResponse.getErrcode() != 0) {

                    } else {

                    }
                } catch (Exception e1) {
                    e1.printStackTrace();
                }
            }
        }
    }


    /**
     * @description: 调用保存接口
     * @param: [yapiSaveParam, attachUpload, path]
     * @return: com.qbb.dto.YapiResponse
     * @author: chengsheng@qbb6.com
     * @date: 2019/5/15
     */
    public YapiResponse uploadSave(YapiSaveParam yapiSaveParam) throws IOException {
        if (StringUtils.isEmpty(yapiSaveParam.getTitle())) {
            yapiSaveParam.setTitle(yapiSaveParam.getPath());
        }
        YapiHeaderDTO yapiHeaderDTO = new YapiHeaderDTO();
        if ("form".equals(yapiSaveParam.getReq_body_type())) {
            yapiHeaderDTO.setName("Content-Type");
            yapiHeaderDTO.setValue("application/x-www-form-urlencoded");
            yapiSaveParam.setReq_body_form(yapiSaveParam.getReq_body_form());
        } else {
            yapiHeaderDTO.setName("Content-Type");
            yapiHeaderDTO.setValue("application/json");
            yapiSaveParam.setReq_body_type("json");
        }
        if (Objects.isNull(yapiSaveParam.getReq_headers())) {
            List list = new ArrayList();
            list.add(yapiHeaderDTO);
            yapiSaveParam.setReq_headers(list);
        } else {
            yapiSaveParam.getReq_headers().add(yapiHeaderDTO);
        }
        String response = HttpClientUtil.ObjectToString(HttpClientUtil.getHttpclient().execute(this.getHttpPost(yapiSaveParam.getYapiUrl() + YapiConstant.yapiSave, mapper.writeValueAsString(yapiSaveParam))), "utf-8");
        return mapper.readValue(response, YapiResponse.class);
    }

    /**
     * 获得httpPost
     *
     * @return
     */
    private HttpPost getHttpPost(String url, String body) {
        HttpPost httpPost = null;
        try {
            httpPost = new HttpPost(url);
            httpPost.setHeader("Content-type", "application/json;charset=utf-8");
            HttpEntity reqEntity = new StringEntity(body == null ? "" : body, "UTF-8");
            httpPost.setEntity(reqEntity);
        } catch (Exception e) {
        }
        return httpPost;
    }

    /**
     * 从openapi协议转换成对应的yapi协议
     *
     * @param openAPI
     * @return
     */
    public List<YapiApiDTO> toYapiDTO(OpenAPI openAPI) {
        Paths paths = openAPI.getPaths();
        List<YapiApiDTO> apiDTOS = new ArrayList<>(paths.size());

        openAPI.getPaths().forEach((k, v) -> {
            Operation operation = v.getPost();
            if (operation == null) {
                operation = v.getGet();
            }

            YapiApiDTO yapiApiDTO = new YapiApiDTO();
            yapiApiDTO.setDesc(operation.getDescription());


            yapiApiDTO.setReq_params(operation.getParameters());
            yapiApiDTO.setTag(operation.getTags().stream().findFirst().orElse(""));
            try {
                yapiApiDTO.setRequestBody(mapper.writeValueAsString(operation.getRequestBody()));
                yapiApiDTO.setResponse(mapper.writeValueAsString(operation.getResponses()));
            } catch (JsonProcessingException e) {
                e.printStackTrace();
            }
            yapiApiDTO.setTitle(operation.getSummary());
            yapiApiDTO.setPath(k);

            if (v.getGet() != null) {
                yapiApiDTO.setMethod("GET");
            }
            apiDTOS.add(yapiApiDTO);


        });

        return apiDTOS;

    }

    /**
     * @description: 获得分类或者创建分类或者
     * @param: [yapiSaveParam]
     * @return: com.qbb.dto.YapiResponse
     * @author: chengsheng@qbb6.com
     * @date: 2019/5/15
     */
    public Integer getCatIdOrCreate(String tag) {
        Integer catId = null;
        String response = null;
        if (Optional.ofNullable(catMap.get(projectId)).map(a -> a.get(tag)).isPresent()) {
            return catMap.get(projectId).get(tag);
        }
        try {
            response = HttpClientUtil.ObjectToString(HttpClientUtil.getHttpclient().execute(this.getHttpGet(yapiUrl + YapiConstant.yapiCatMenu + "?project_id=" + projectId + "&token=" + projectToken)), "utf-8");
            YapiResponse yapiResponse = mapper.readValue(response, YapiResponse.class);
            if (yapiResponse.getErrcode() == 0) {
                List<YapiCatResponse> list = (List<YapiCatResponse>) yapiResponse.getData();
                list = mapper.readValue(mapper.writeValueAsString(list), new TypeReference<List<YapiCatResponse>>() {
                });
                for (YapiCatResponse yapiCatResponse : list) {
                    if (yapiCatResponse.getName().equals(tag)) {
                        Map<String, Integer> catMenuMapSub = catMap.get(projectId);
                        if (catMenuMapSub != null) {
                            catMenuMapSub.put(yapiCatResponse.getName(), yapiCatResponse.get_id());
                        } else {
                            catMenuMapSub = new HashMap<>();
                            catMenuMapSub.put(yapiCatResponse.getName(), yapiCatResponse.get_id());
                            catMap.put(projectId, catMenuMapSub);
                        }
                        catId = yapiCatResponse.get_id();
                    }
                }
            }
            YapiCatMenuParam yapiCatMenuParam = new YapiCatMenuParam(tag, projectId, projectToken);
            String responseCat = HttpClientUtil.ObjectToString(HttpClientUtil.getHttpclient().execute(this.getHttpPost(yapiUrl + YapiConstant.yapiAddCat, mapper.writeValueAsString(yapiCatMenuParam))), "utf-8");
            catId = (Integer) ((LinkedHashMap) mapper.readValue(responseCat, YapiResponse.class).getData()).get("_id");
            String name = (String) ((LinkedHashMap) mapper.readValue(responseCat, YapiResponse.class).getData()).get("name");
            Map<String, Integer> catMenuMapSub = catMap.get(projectId);
            if (catMenuMapSub != null) {
                catMenuMapSub.put(name, catId);
            } else {
                catMenuMapSub = new HashMap<>();
                catMenuMapSub.put(name, catId);
                catMap.put(projectId, catMenuMapSub);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        return catMap.get(projectId).get(tag);
    }

    private HttpGet getHttpGet(String url) {
        try {
            return HttpClientUtil.getHttpGet(url, "application/json", "application/json; charset=utf-8");
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

}
