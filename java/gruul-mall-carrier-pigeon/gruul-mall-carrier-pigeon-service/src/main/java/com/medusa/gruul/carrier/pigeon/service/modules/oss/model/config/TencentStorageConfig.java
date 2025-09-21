package com.medusa.gruul.carrier.pigeon.service.modules.oss.model.config;

import cn.hutool.core.util.DesensitizedUtil;
import com.medusa.gruul.common.fastjson2.annotation.Desensitize;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.io.Serial;

/**
 * @author whh
 */
@Data
public class TencentStorageConfig extends StorageConf {

    @Serial
    private static final long serialVersionUID = 6697917083773365808L;

    /**
     * 腾讯云绑定的域名
     */
    @NotBlank(message = "腾讯云绑定的域名不能为空")
    private String qcloudDomain;

    /**
     * 腾讯云路径前缀
     */
    @NotBlank(message = "腾讯云路径前缀不能为空")
    private String qcloudPrefix;

    /**
     * 腾讯云AppId
     */
    @NotNull(message = "腾讯云AppId不能为空")
    private Integer qcloudAppId;

    /**
     * 腾讯云SecretId
     */
    @Desensitize(start = 3, end = 3)
    @NotBlank(message = "腾讯云SecretId不能为空")
    private String qcloudSecretId;

    /**
     * 腾讯云SecretKey
     */
    @Desensitize(start = 3, end = 3)
    @NotBlank(message = "腾讯云SecretKey不能为空")
    private String qcloudSecretKey;

    /**
     * 腾讯云BucketName
     */
    @NotBlank(message = "腾讯云BucketName不能为空")
    private String qcloudBucketName;

    /**
     * 腾讯云COS所属地区
     */
    @NotBlank(message = "腾讯云COS所属地区不能为空")
    private String qcloudRegion;
}
