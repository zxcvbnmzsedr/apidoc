package com.ztianzeng.apidoc.yapi.upload;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.ztianzeng.apidoc.models.OpenAPI;
import com.ztianzeng.apidoc.models.Operation;
import com.ztianzeng.apidoc.models.Paths;
import com.ztianzeng.apidoc.utils.Json;
import com.ztianzeng.apidoc.yapi.constant.YapiConstant;
import com.ztianzeng.apidoc.yapi.module.*;
import com.ztianzeng.apidoc.yapi.utils.HttpClientUtil;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpEntity;
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
    /**
     * 上传OpenAPi
     *
     * @param openAPI
     */
    public void upload(OpenAPI openAPI, String projectToken, String yapiUrl) {
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
                yapiSaveParam.setCatid("709");
                try {
                    // 上传
                    YapiResponse yapiResponse = new UploadToYapi().uploadSave(yapiSaveParam);
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
        String response = HttpClientUtil.ObjectToString(HttpClientUtil.getHttpclient().execute(this.getHttpPost(yapiSaveParam.getYapiUrl() + YapiConstant.yapiSave, Json.mapper().writeValueAsString(yapiSaveParam))), "utf-8");
        return Json.mapper().readValue(response, YapiResponse.class);
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

            try {
                yapiApiDTO.setRequestBody(Json.mapper().writeValueAsString(operation.getRequestBody()));
                yapiApiDTO.setResponse(Json.mapper().writeValueAsString(operation.getResponses()));
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
//    public YapiResponse getCatIdOrCreate(YapiSaveParam yapiSaveParam) {
//        Map<String, Integer> catMenuMap = catMap.get(yapiSaveParam.getProjectId().toString());
//        if (catMenuMap != null) {
//            if (!Strings.isNullOrEmpty(yapiSaveParam.getMenu())) {
//                if (Objects.nonNull(catMenuMap.get(yapiSaveParam.getMenu()))) {
//                    return new YapiResponse(catMenuMap.get(yapiSaveParam.getMenu()));
//                }
//            } else {
//                if (Objects.nonNull(catMenuMap.get(YapiConstant.menu))) {
//                    return new YapiResponse(catMenuMap.get(YapiConstant.menu));
//                }
//                yapiSaveParam.setMenu(YapiConstant.menu);
//            }
//        }
//        String response = null;
//        try {
//            response = HttpClientUtil.ObjectToString(HttpClientUtil.getHttpclient().execute(this.getHttpGet(yapiSaveParam.getYapiUrl() + YapiConstant.yapiCatMenu + "?project_id=" + yapiSaveParam.getProjectId() + "&token=" + yapiSaveParam.getToken())), "utf-8");
//            YapiResponse yapiResponse = gson.fromJson(response, YapiResponse.class);
//            if (yapiResponse.getErrcode() == 0) {
//                List<YapiCatResponse> list = (List<YapiCatResponse>) yapiResponse.getData();
//                list = gson.fromJson(gson.toJson(list), new TypeToken<List<YapiCatResponse>>() {
//                }.getType());
//                for (YapiCatResponse yapiCatResponse : list) {
//                    if (yapiCatResponse.getName().equals(yapiSaveParam.getMenu())) {
//                        Map<String, Integer> catMenuMapSub = catMap.get(yapiSaveParam.getProjectId().toString());
//                        if (catMenuMapSub != null) {
//                            catMenuMapSub.put(yapiCatResponse.getName(), yapiCatResponse.get_id());
//                        } else {
//                            catMenuMapSub = new HashMap<>();
//                            catMenuMapSub.put(yapiCatResponse.getName(), yapiCatResponse.get_id());
//                            catMap.put(yapiSaveParam.getProjectId().toString(), catMenuMapSub);
//                        }
//                        return new YapiResponse(yapiCatResponse.get_id());
//                    }
//                }
//            }
//            YapiCatMenuParam yapiCatMenuParam = new YapiCatMenuParam(yapiSaveParam.getMenu(), yapiSaveParam.getProjectId(), yapiSaveParam.getToken());
//            String responseCat = HttpClientUtil.ObjectToString(HttpClientUtil.getHttpclient().execute(this.getHttpPost(yapiSaveParam.getYapiUrl() + YapiConstant.yapiAddCat, gson.toJson(yapiCatMenuParam))), "utf-8");
//            YapiCatResponse yapiCatResponse = gson.fromJson(gson.fromJson(responseCat, YapiResponse.class).getData().toString(), YapiCatResponse.class);
//            Map<String, Integer> catMenuMapSub = catMap.get(yapiSaveParam.getProjectId().toString());
//            if (catMenuMapSub != null) {
//                catMenuMapSub.put(yapiCatResponse.getName(), yapiCatResponse.get_id());
//            } else {
//                catMenuMapSub = new HashMap<>();
//                catMenuMapSub.put(yapiCatResponse.getName(), yapiCatResponse.get_id());
//                catMap.put(yapiSaveParam.getProjectId().toString(), catMenuMapSub);
//            }
//            return new YapiResponse(yapiCatResponse.get_id());
//        } catch (IOException e) {
//            e.printStackTrace();
//            return new YapiResponse(0, e.toString());
//        }
//    }

}
