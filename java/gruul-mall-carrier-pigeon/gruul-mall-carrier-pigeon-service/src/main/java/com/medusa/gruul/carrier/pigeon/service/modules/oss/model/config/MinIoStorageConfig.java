package com.medusa.gruul.carrier.pigeon.service.modules.oss.model.config;

import cn.hutool.core.util.DesensitizedUtil;
import com.medusa.gruul.common.fastjson2.annotation.Desensitize;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;


/**
 * minio配置
 *
 * @author jipeng
 * @since 2024/2/22
 */
@Data
public class MinIoStorageConfig extends StorageConf {

    /**
     * minio key
     */
    @Desensitize(start = 3, end = 3)
    @NotBlank(message = "minio key不能为空")
    private String minioAccessKey;

    /**
     * minio secret
     */
    @Desensitize(start = 3, end = 3)
    @NotBlank(message = "minio secret不能为空")
    private String minioSecretKey;

    /**
     * minio endpoint 访问端点
     */
    @NotBlank(message = "访问端点不能为空")
    private String minioEndPoint;

    /**
     * minio 存储桶 名称
     */
    @NotBlank(message = "存储桶名称不能为空")
    private String minioBucketName;

    /**
     * 访问域名
     */
    @NotBlank(message = "访问域名不能为空")
    private String minioDomain = "";

    /**
     * 基础路径
     */
//    @NotBlank(message = "基础路径不能为空")
//    private String minioBsePath = "";

}
