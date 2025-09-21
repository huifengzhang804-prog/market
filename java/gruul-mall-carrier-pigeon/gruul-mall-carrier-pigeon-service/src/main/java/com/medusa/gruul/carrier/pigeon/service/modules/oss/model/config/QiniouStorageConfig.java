package com.medusa.gruul.carrier.pigeon.service.modules.oss.model.config;

import cn.hutool.core.util.DesensitizedUtil;
import com.medusa.gruul.common.fastjson2.annotation.Desensitize;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;


/**
 * @author whh
 */
@Data
public class QiniouStorageConfig extends StorageConf {

    /**
     * 七牛绑定的域名
     */
    @NotBlank(message = "七牛绑定的域名不能为空")
    private String qiniuDomain;

    /**
     * 七牛路径前缀
     */
    @NotBlank(message = "七牛路径前缀不能为空")
    private String qiniuPrefix;

    /**
     * 七牛ACCESS_KEY
     */
    @Desensitize(start = 3, end = 3)
    @NotBlank(message = "七牛ACCESS_KEY不能为空")
    private String qiniuAccessKey;

    /**
     * 七牛SecretKey不能为空
     */
    @Desensitize(start = 3, end = 3)
    @NotBlank(message = "七牛SecretKey不能为空")
    private String qiniuSecretKey;

    /**
     * 七牛存储空间名
     */
    @NotBlank(message = "七牛存储空间名不能为空")
    private String qiniuBucketName;
}
