package com.ztianzeng.apidoc.maven.ssh;

import lombok.Data;
import lombok.ToString;
import org.apache.maven.plugins.annotations.Parameter;

/**
 * SSH 配置
 *
 * @author zhaotianzeng
 * @version V1.0
 * @date 2019-07-18 10:30
 */
@Data
@ToString
public class SSHConfig {
    /**
     * 主机名
     */
    @Parameter(property = "host", required = true)
    private String host;

    /**
     * 密码
     */
    @Parameter(property = "password")
    private String password;
    /**
     * 用户米
     */
    @Parameter(property = "user", required = true)
    private String user;

    /**
     * 公钥文件
     */
    @Parameter(property = "publicKeyFile")
    private String publicKeyFile;

    /**
     * 公钥
     */
    @Parameter(property = "publicKey")
    private String publicKey;

    /**
     * 远程传输配置
     */
    @Parameter(property = "scp")
    private SCPConfig scp;

}