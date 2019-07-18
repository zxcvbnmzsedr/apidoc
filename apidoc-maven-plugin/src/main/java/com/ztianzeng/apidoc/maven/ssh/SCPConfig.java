package com.ztianzeng.apidoc.maven.ssh;

import lombok.Data;
import org.apache.maven.plugins.annotations.Parameter;

/**
 * @author zhaotianzeng
 * @version V1.0
 * @date 2019-07-18 11:10
 */
@Data
public class SCPConfig {
    /**
     * 目录
     */
    @Parameter(property = "remoteTargetDirectory", required = true)
    private String remoteTargetDirectory;

    /**
     * 权限
     */
    @Parameter(property = "model")
    private String model;
}