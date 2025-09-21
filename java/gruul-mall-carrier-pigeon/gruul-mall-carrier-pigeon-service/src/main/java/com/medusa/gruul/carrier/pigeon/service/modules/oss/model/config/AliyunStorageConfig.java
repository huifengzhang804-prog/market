package com.medusa.gruul.carrier.pigeon.service.modules.oss.model.config;

import cn.hutool.core.util.DesensitizedUtil;
import com.medusa.gruul.common.fastjson2.annotation.Desensitize;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;


/**
 * @author whh
 */
@Data
public class AliyunStorageConfig extends StorageConf {

    /**
     * 阿里云绑定的域名
     */
    @NotBlank(message = "阿里云绑定的域名不能为空")
    private String aliyunDomain;
    /**
     * 阿里云路径前缀
     */
    @NotBlank(message = "阿里云路径前缀不能为空")
    private String aliyunPrefix;
    /**
     * 阿里云EndPoint
     */
    @NotBlank(message = "阿里云EndPoint不能为空")
    private String aliyunEndPoint;
    /**
     * 阿里云AccessKeyId
     */
    @Desensitize(start = 3, end = 3)
    @NotBlank(message = "阿里云AccessKeyId不能为空")
    private String aliyunAccessKeyId;
    /**
     * 阿里云AccessKeySecret
     */
    @Desensitize(start = 3, end = 3)
    @NotBlank(message = "阿里云AccessKeySecret不能为空")
    private String aliyunAccessKeySecret;
    /**
     * 阿里云BucketName
     */
    @NotBlank(message = "阿里云BucketName不能为空")
    private String aliyunBucketName;
}
