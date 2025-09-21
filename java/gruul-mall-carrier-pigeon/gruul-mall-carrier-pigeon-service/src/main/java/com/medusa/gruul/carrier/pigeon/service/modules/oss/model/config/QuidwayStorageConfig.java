package com.medusa.gruul.carrier.pigeon.service.modules.oss.model.config;

import cn.hutool.core.util.DesensitizedUtil;
import com.medusa.gruul.common.fastjson2.annotation.Desensitize;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.io.Serial;

/**
 * 华为云配置
 *
 * @Author: xiaoq
 * @Date : 2022-04-11 17:06
 */
@Data
public class QuidwayStorageConfig extends StorageConf {

    @Serial
    private static final long serialVersionUID = -4521898139569541597L;

    /**
     * 华为云的 Access Key Id
     */
    @Desensitize(start = 3, end = 3)
    @NotBlank(message = "华为云的 Access Key Id不能为空")
    private String quidwayAccessKeyId;


    /**
     * 华为云的 Access Key Secret
     */
    @Desensitize(start = 3, end = 3)
    @NotBlank(message = "华为云的 Access Key Secret不能为空")
    private String quidwayAccessKeySecret;

    /**
     * 华为云连接的地址节点
     */
    @NotBlank(message = "华为云连接的地址节点不能为空")
    private String quidwayEndpoint;

    /**
     * 华为云存储桶名称
     */
    @NotBlank(message = "华为云存储桶名称不能为空")
    private String obsBucketName;

    /**
     * 华为云绑定的域名
     */
    @NotBlank(message = "华为云绑定的域名不能为空")
    private String quidwayDomain;

    /**
     * 华为云路径前缀
     */
    @NotBlank(message = "华为云路径前缀不能为空")
    private String quidwayPrefix;
}
