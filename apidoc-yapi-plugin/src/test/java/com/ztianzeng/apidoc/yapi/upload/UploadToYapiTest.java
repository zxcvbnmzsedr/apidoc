package com.ztianzeng.apidoc.yapi.upload;

import com.ztianzeng.apidoc.models.OpenAPI;
import com.ztianzeng.apidoc.utils.Json;
import org.junit.Test;

import java.io.IOException;

/**
 * @author zhaotianzeng
 * @version V1.0
 * @date 2019-06-14 14:39
 */
public class UploadToYapiTest {

    public static String openAPI_S = "{\n" +
            "  \"openapi\" : \"3.0.1\",\n" +
            "  \"info\" : {\n" +
            "    \"title\" : \"dddd\",\n" +
            "    \"version\" : \"111.111\"\n" +
            "  },\n" +
            "  \"paths\" : {\n" +
            "    \"/mobile/instance/dynamic/start\" : {\n" +
            "      \"post\" : {\n" +
            "        \"tags\" : [ \"流程实例\" ],\n" +
            "        \"summary\" : \"启动审批\",\n" +
            "        \"operationId\" : \"start\",\n" +
            "        \"requestBody\" : {\n" +
            "          \"content\" : {\n" +
            "            \"application/json\" : {\n" +
            "              \"schema\" : {\n" +
            "                \"$ref\" : \"#/components/schemas/StartInstanceParam\"\n" +
            "              }\n" +
            "            }\n" +
            "          },\n" +
            "          \"required\" : true\n" +
            "        },\n" +
            "        \"responses\" : {\n" +
            "          \"200\" : {\n" +
            "            \"description\" : \"\",\n" +
            "            \"content\" : {\n" +
            "              \"application/json\" : {\n" +
            "                \"schema\" : {\n" +
            "                  \"$ref\" : \"#/components/schemas/Result\"\n" +
            "                }\n" +
            "              }\n" +
            "            }\n" +
            "          }\n" +
            "        },\n" +
            "        \"deprecated\" : false\n" +
            "      }\n" +
            "    },\n" +
            "    \"/mobile/instance/dynamic/commitRecord\" : {\n" +
            "      \"post\" : {\n" +
            "        \"tags\" : [ \"流程实例\" ],\n" +
            "        \"summary\" : \"获取我发起的审批记录\",\n" +
            "        \"operationId\" : \"page\",\n" +
            "        \"requestBody\" : {\n" +
            "          \"content\" : {\n" +
            "            \"application/json\" : {\n" +
            "              \"schema\" : {\n" +
            "                \"$ref\" : \"#/components/schemas/CommitRecordQuery\"\n" +
            "              }\n" +
            "            }\n" +
            "          },\n" +
            "          \"required\" : true\n" +
            "        },\n" +
            "        \"responses\" : {\n" +
            "          \"200\" : {\n" +
            "            \"description\" : \"response\",\n" +
            "            \"content\" : {\n" +
            "              \"application/json\" : {\n" +
            "                \"schema\" : {\n" +
            "                  \"$ref\" : \"#/components/schemas/ResultIPageDynamicContentListVO\"\n" +
            "                }\n" +
            "              }\n" +
            "            }\n" +
            "          }\n" +
            "        },\n" +
            "        \"deprecated\" : false\n" +
            "      }\n" +
            "    },\n" +
            "    \"/mobile/instance/dynamic/detail\" : {\n" +
            "      \"get\" : {\n" +
            "        \"tags\" : [ \"流程实例\" ],\n" +
            "        \"summary\" : \"获取流程实例的状态\",\n" +
            "        \"operationId\" : \"detail\",\n" +
            "        \"parameters\" : [ {\n" +
            "          \"name\" : \"processId\",\n" +
            "          \"in\" : \"query\",\n" +
            "          \"description\" : \"流程信息ID\",\n" +
            "          \"schema\" : { }\n" +
            "        } ],\n" +
            "        \"responses\" : {\n" +
            "          \"200\" : {\n" +
            "            \"description\" : \"response\",\n" +
            "            \"content\" : {\n" +
            "              \"application/json\" : {\n" +
            "                \"schema\" : {\n" +
            "                  \"$ref\" : \"#/components/schemas/ResultWorkFlowDetailVO\"\n" +
            "                }\n" +
            "              }\n" +
            "            }\n" +
            "          }\n" +
            "        },\n" +
            "        \"deprecated\" : false\n" +
            "      }\n" +
            "    },\n" +
            "    \"/mobile/instance/dynamic/complete\" : {\n" +
            "      \"post\" : {\n" +
            "        \"tags\" : [ \"流程实例\" ],\n" +
            "        \"summary\" : \"通过审批\",\n" +
            "        \"operationId\" : \"complete\",\n" +
            "        \"requestBody\" : {\n" +
            "          \"content\" : {\n" +
            "            \"application/json\" : {\n" +
            "              \"schema\" : {\n" +
            "                \"$ref\" : \"#/components/schemas/CompleteParam\"\n" +
            "              }\n" +
            "            }\n" +
            "          },\n" +
            "          \"required\" : true\n" +
            "        },\n" +
            "        \"responses\" : {\n" +
            "          \"200\" : {\n" +
            "            \"description\" : \"response\",\n" +
            "            \"content\" : {\n" +
            "              \"application/json\" : {\n" +
            "                \"schema\" : {\n" +
            "                  \"$ref\" : \"#/components/schemas/Result\"\n" +
            "                }\n" +
            "              }\n" +
            "            }\n" +
            "          }\n" +
            "        },\n" +
            "        \"deprecated\" : false\n" +
            "      }\n" +
            "    },\n" +
            "    \"/mobile/instance/dynamic/dismiss\" : {\n" +
            "      \"post\" : {\n" +
            "        \"tags\" : [ \"流程实例\" ],\n" +
            "        \"summary\" : \"驳回审批\",\n" +
            "        \"description\" : \"驳回的时候将整个流程都结束掉。不做驳回到指定节点的处理\",\n" +
            "        \"operationId\" : \"dismiss\",\n" +
            "        \"requestBody\" : {\n" +
            "          \"content\" : {\n" +
            "            \"application/json\" : {\n" +
            "              \"schema\" : {\n" +
            "                \"$ref\" : \"#/components/schemas/DismissParam\"\n" +
            "              }\n" +
            "            }\n" +
            "          },\n" +
            "          \"required\" : true\n" +
            "        },\n" +
            "        \"responses\" : {\n" +
            "          \"200\" : {\n" +
            "            \"description\" : \"response\",\n" +
            "            \"content\" : {\n" +
            "              \"application/json\" : {\n" +
            "                \"schema\" : {\n" +
            "                  \"$ref\" : \"#/components/schemas/Result\"\n" +
            "                }\n" +
            "              }\n" +
            "            }\n" +
            "          }\n" +
            "        },\n" +
            "        \"deprecated\" : false\n" +
            "      }\n" +
            "    },\n" +
            "    \"/mobile/instance/dynamic/self\" : {\n" +
            "      \"post\" : {\n" +
            "        \"tags\" : [ \"流程实例\" ],\n" +
            "        \"summary\" : \"用户的审批记录\",\n" +
            "        \"operationId\" : \"self\",\n" +
            "        \"requestBody\" : {\n" +
            "          \"content\" : {\n" +
            "            \"application/json\" : {\n" +
            "              \"schema\" : {\n" +
            "                \"$ref\" : \"#/components/schemas/DynamicSelfQuery\"\n" +
            "              }\n" +
            "            }\n" +
            "          },\n" +
            "          \"required\" : true\n" +
            "        },\n" +
            "        \"responses\" : {\n" +
            "          \"200\" : {\n" +
            "            \"description\" : \"response\",\n" +
            "            \"content\" : {\n" +
            "              \"application/json\" : {\n" +
            "                \"schema\" : {\n" +
            "                  \"$ref\" : \"#/components/schemas/ResultIPageDynamicContentListVO\"\n" +
            "                }\n" +
            "              }\n" +
            "            }\n" +
            "          }\n" +
            "        },\n" +
            "        \"deprecated\" : false\n" +
            "      }\n" +
            "    },\n" +
            "    \"/mobile/instance/dynamic/self/label\" : {\n" +
            "      \"get\" : {\n" +
            "        \"tags\" : [ \"流程实例\" ],\n" +
            "        \"summary\" : \"查询审批标签\",\n" +
            "        \"operationId\" : \"label\",\n" +
            "        \"responses\" : {\n" +
            "          \"200\" : {\n" +
            "            \"description\" : \"response\",\n" +
            "            \"content\" : {\n" +
            "              \"application/json\" : {\n" +
            "                \"schema\" : {\n" +
            "                  \"$ref\" : \"#/components/schemas/ResultListProcessLabelVO\"\n" +
            "                }\n" +
            "              }\n" +
            "            }\n" +
            "          }\n" +
            "        },\n" +
            "        \"deprecated\" : false\n" +
            "      }\n" +
            "    }\n" +
            "  },\n" +
            "  \"components\" : {\n" +
            "    \"schemas\" : {\n" +
            "      \"Object\" : {\n" +
            "        \"type\" : \"object\"\n" +
            "      },\n" +
            "      \"StartInstanceParam\" : {\n" +
            "        \"type\" : \"object\",\n" +
            "        \"properties\" : {\n" +
            "          \"templateId\" : {\n" +
            "            \"type\" : \"integer\",\n" +
            "            \"description\" : \"模板ID\",\n" +
            "            \"format\" : \"int32\"\n" +
            "          },\n" +
            "          \"inputData\" : {\n" +
            "            \"type\" : \"object\",\n" +
            "            \"additionalProperties\" : {\n" +
            "              \"$ref\" : \"#/components/schemas/Object\"\n" +
            "            },\n" +
            "            \"description\" : \"上传参数\"\n" +
            "          },\n" +
            "          \"approvalUsers\" : {\n" +
            "            \"type\" : \"array\",\n" +
            "            \"description\" : \"抄送人\",\n" +
            "            \"items\" : {\n" +
            "              \"type\" : \"string\"\n" +
            "            }\n" +
            "          },\n" +
            "          \"copySendMan\" : {\n" +
            "            \"type\" : \"array\",\n" +
            "            \"description\" : \"抄送人\",\n" +
            "            \"items\" : {\n" +
            "              \"type\" : \"string\"\n" +
            "            }\n" +
            "          },\n" +
            "          \"send\" : {\n" +
            "            \"type\" : \"boolean\",\n" +
            "            \"description\" : \"是否通过聊天发送给审批人\"\n" +
            "          }\n" +
            "        }\n" +
            "      },\n" +
            "      \"Result\" : {\n" +
            "        \"type\" : \"object\",\n" +
            "        \"properties\" : {\n" +
            "          \"serialVersionUID\" : {\n" +
            "            \"type\" : \"integer\",\n" +
            "            \"format\" : \"int64\"\n" +
            "          },\n" +
            "          \"msg\" : {\n" +
            "            \"type\" : \"string\"\n" +
            "          },\n" +
            "          \"code\" : {\n" +
            "            \"type\" : \"integer\",\n" +
            "            \"format\" : \"int32\"\n" +
            "          },\n" +
            "          \"data\" : {\n" +
            "            \"type\" : \"object\"\n" +
            "          }\n" +
            "        }\n" +
            "      },\n" +
            "      \"CommitRecordQuery\" : {\n" +
            "        \"type\" : \"object\",\n" +
            "        \"properties\" : {\n" +
            "          \"type\" : {\n" +
            "            \"type\" : \"integer\",\n" +
            "            \"description\" : \"流程类型（下发的模板ID）\",\n" +
            "            \"format\" : \"int32\"\n" +
            "          },\n" +
            "          \"status\" : {\n" +
            "            \"type\" : \"integer\",\n" +
            "            \"description\" : \"流程状态\",\n" +
            "            \"format\" : \"int32\"\n" +
            "          },\n" +
            "          \"serialVersionUID\" : {\n" +
            "            \"type\" : \"integer\",\n" +
            "            \"format\" : \"int64\"\n" +
            "          },\n" +
            "          \"total\" : {\n" +
            "            \"type\" : \"integer\",\n" +
            "            \"format\" : \"int64\"\n" +
            "          },\n" +
            "          \"size\" : {\n" +
            "            \"type\" : \"integer\",\n" +
            "            \"format\" : \"int64\"\n" +
            "          },\n" +
            "          \"current\" : {\n" +
            "            \"type\" : \"integer\",\n" +
            "            \"format\" : \"int64\"\n" +
            "          },\n" +
            "          \"ascs\" : {\n" +
            "            \"type\" : \"array\",\n" +
            "            \"items\" : {\n" +
            "              \"type\" : \"string\"\n" +
            "            }\n" +
            "          },\n" +
            "          \"descs\" : {\n" +
            "            \"type\" : \"array\",\n" +
            "            \"items\" : {\n" +
            "              \"type\" : \"string\"\n" +
            "            }\n" +
            "          },\n" +
            "          \"optimizeCountSql\" : {\n" +
            "            \"type\" : \"boolean\"\n" +
            "          },\n" +
            "          \"isSearchCount\" : {\n" +
            "            \"type\" : \"boolean\"\n" +
            "          }\n" +
            "        }\n" +
            "      },\n" +
            "      \"DynamicContentListVO\" : {\n" +
            "        \"type\" : \"object\",\n" +
            "        \"properties\" : {\n" +
            "          \"title\" : {\n" +
            "            \"type\" : \"string\"\n" +
            "          },\n" +
            "          \"gmtCreate\" : {\n" +
            "            \"type\" : \"string\",\n" +
            "            \"format\" : \"date-time\"\n" +
            "          },\n" +
            "          \"itemContent\" : {\n" +
            "            \"type\" : \"object\"\n" +
            "          },\n" +
            "          \"status\" : {\n" +
            "            \"type\" : \"integer\",\n" +
            "            \"format\" : \"int32\"\n" +
            "          },\n" +
            "          \"avatar\" : {\n" +
            "            \"type\" : \"string\"\n" +
            "          },\n" +
            "          \"processId\" : {\n" +
            "            \"type\" : \"string\",\n" +
            "            \"description\" : \"流程ID\"\n" +
            "          }\n" +
            "        }\n" +
            "      },\n" +
            "      \"IPageDynamicContentListVO\" : {\n" +
            "        \"type\" : \"object\"\n" +
            "      },\n" +
            "      \"ResultIPageDynamicContentListVO\" : {\n" +
            "        \"type\" : \"object\",\n" +
            "        \"properties\" : {\n" +
            "          \"serialVersionUID\" : {\n" +
            "            \"type\" : \"integer\",\n" +
            "            \"format\" : \"int64\"\n" +
            "          },\n" +
            "          \"msg\" : {\n" +
            "            \"type\" : \"string\"\n" +
            "          },\n" +
            "          \"code\" : {\n" +
            "            \"type\" : \"integer\",\n" +
            "            \"format\" : \"int32\"\n" +
            "          },\n" +
            "          \"data\" : {\n" +
            "            \"$ref\" : \"#/components/schemas/IPageDynamicContentListVO\"\n" +
            "          }\n" +
            "        }\n" +
            "      },\n" +
            "      \"ResultWorkFlowDetailVO\" : {\n" +
            "        \"type\" : \"object\",\n" +
            "        \"properties\" : {\n" +
            "          \"serialVersionUID\" : {\n" +
            "            \"type\" : \"integer\",\n" +
            "            \"format\" : \"int64\"\n" +
            "          },\n" +
            "          \"msg\" : {\n" +
            "            \"type\" : \"string\"\n" +
            "          },\n" +
            "          \"code\" : {\n" +
            "            \"type\" : \"integer\",\n" +
            "            \"format\" : \"int32\"\n" +
            "          },\n" +
            "          \"data\" : {\n" +
            "            \"$ref\" : \"#/components/schemas/WorkFlowDetailVO\"\n" +
            "          }\n" +
            "        }\n" +
            "      },\n" +
            "      \"WorkFlowDetailVO\" : {\n" +
            "        \"type\" : \"object\",\n" +
            "        \"properties\" : {\n" +
            "          \"cards\" : {\n" +
            "            \"type\" : \"array\",\n" +
            "            \"description\" : \"卡片\",\n" +
            "            \"items\" : {\n" +
            "              \"type\" : \"object\"\n" +
            "            }\n" +
            "          }\n" +
            "        }\n" +
            "      },\n" +
            "      \"CompleteParam\" : {\n" +
            "        \"required\" : [ \"processId\" ],\n" +
            "        \"type\" : \"object\",\n" +
            "        \"properties\" : {\n" +
            "          \"processId\" : {\n" +
            "            \"type\" : \"string\",\n" +
            "            \"description\" : \"流程ID\"\n" +
            "          },\n" +
            "          \"comment\" : {\n" +
            "            \"type\" : \"string\"\n" +
            "          }\n" +
            "        }\n" +
            "      },\n" +
            "      \"DismissParam\" : {\n" +
            "        \"required\" : [ \"comment\", \"processId\" ],\n" +
            "        \"type\" : \"object\",\n" +
            "        \"properties\" : {\n" +
            "          \"processId\" : {\n" +
            "            \"type\" : \"string\",\n" +
            "            \"description\" : \"流程ID\"\n" +
            "          },\n" +
            "          \"comment\" : {\n" +
            "            \"type\" : \"string\",\n" +
            "            \"description\" : \"原因\"\n" +
            "          }\n" +
            "        }\n" +
            "      },\n" +
            "      \"DynamicSelfQuery\" : {\n" +
            "        \"type\" : \"object\",\n" +
            "        \"properties\" : {\n" +
            "          \"finished\" : {\n" +
            "            \"type\" : \"boolean\",\n" +
            "            \"description\" : \"是否流程已经结束\"\n" +
            "          },\n" +
            "          \"type\" : {\n" +
            "            \"type\" : \"integer\",\n" +
            "            \"description\" : \"流程类型（下发的模板ID）\",\n" +
            "            \"format\" : \"int32\"\n" +
            "          },\n" +
            "          \"serialVersionUID\" : {\n" +
            "            \"type\" : \"integer\",\n" +
            "            \"format\" : \"int64\"\n" +
            "          },\n" +
            "          \"total\" : {\n" +
            "            \"type\" : \"integer\",\n" +
            "            \"format\" : \"int64\"\n" +
            "          },\n" +
            "          \"size\" : {\n" +
            "            \"type\" : \"integer\",\n" +
            "            \"format\" : \"int64\"\n" +
            "          },\n" +
            "          \"current\" : {\n" +
            "            \"type\" : \"integer\",\n" +
            "            \"format\" : \"int64\"\n" +
            "          },\n" +
            "          \"ascs\" : {\n" +
            "            \"type\" : \"array\",\n" +
            "            \"items\" : {\n" +
            "              \"type\" : \"string\"\n" +
            "            }\n" +
            "          },\n" +
            "          \"descs\" : {\n" +
            "            \"type\" : \"array\",\n" +
            "            \"items\" : {\n" +
            "              \"type\" : \"string\"\n" +
            "            }\n" +
            "          },\n" +
            "          \"optimizeCountSql\" : {\n" +
            "            \"type\" : \"boolean\"\n" +
            "          },\n" +
            "          \"isSearchCount\" : {\n" +
            "            \"type\" : \"boolean\"\n" +
            "          }\n" +
            "        }\n" +
            "      },\n" +
            "      \"ProcessLabelVO\" : {\n" +
            "        \"type\" : \"object\",\n" +
            "        \"properties\" : {\n" +
            "          \"type\" : {\n" +
            "            \"type\" : \"integer\",\n" +
            "            \"description\" : \"0 我已审批 1 待我审批\",\n" +
            "            \"format\" : \"int32\"\n" +
            "          },\n" +
            "          \"name\" : {\n" +
            "            \"type\" : \"string\",\n" +
            "            \"description\" : \"标签名字\"\n" +
            "          },\n" +
            "          \"num\" : {\n" +
            "            \"type\" : \"integer\",\n" +
            "            \"description\" : \"数量\",\n" +
            "            \"format\" : \"int64\"\n" +
            "          }\n" +
            "        }\n" +
            "      },\n" +
            "      \"ResultListProcessLabelVO\" : {\n" +
            "        \"type\" : \"object\",\n" +
            "        \"properties\" : {\n" +
            "          \"serialVersionUID\" : {\n" +
            "            \"type\" : \"integer\",\n" +
            "            \"format\" : \"int64\"\n" +
            "          },\n" +
            "          \"msg\" : {\n" +
            "            \"type\" : \"string\"\n" +
            "          },\n" +
            "          \"code\" : {\n" +
            "            \"type\" : \"integer\",\n" +
            "            \"format\" : \"int32\"\n" +
            "          },\n" +
            "          \"data\" : {\n" +
            "            \"type\" : \"array\",\n" +
            "            \"items\" : {\n" +
            "              \"type\" : \"object\",\n" +
            "              \"properties\" : {\n" +
            "                \"type\" : {\n" +
            "                  \"type\" : \"integer\",\n" +
            "                  \"description\" : \"0 我已审批 1 待我审批\",\n" +
            "                  \"format\" : \"int32\"\n" +
            "                },\n" +
            "                \"name\" : {\n" +
            "                  \"type\" : \"string\",\n" +
            "                  \"description\" : \"标签名字\"\n" +
            "                },\n" +
            "                \"num\" : {\n" +
            "                  \"type\" : \"integer\",\n" +
            "                  \"description\" : \"数量\",\n" +
            "                  \"format\" : \"int64\"\n" +
            "                }\n" +
            "              }\n" +
            "            }\n" +
            "          }\n" +
            "        }\n" +
            "      }\n" +
            "    }\n" +
            "  }\n" +
            "}";

    @Test
    public void upload() throws IOException {
        OpenAPI openAPI = Json.mapper().readValue(openAPI_S, OpenAPI.class);


        UploadToYapi uploadToYapi = new UploadToYapi();
        uploadToYapi.upload(openAPI, "0feace17c2d8c89f2849", "http://yapi.cnabc-inc.com");
    }
}