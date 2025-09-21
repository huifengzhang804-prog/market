package com.medusa.gruul.carrier.pigeon.service.modules.oss.model.config;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;


/**
 * @author jipeng
 * @since 2024/2/22
 */
@Data
public class LocalStorageConfig extends StorageConf {
//  /**
//   * 基础路径
//   */
//  private String localBasePath = "";

    /**
     * 存储路径，上传的文件都会存储在这个路径下面，默认“/”，注意“/”结尾
     */
    private String localStoragePath = "/";

    /**
     * 访问域名
     */
    @NotBlank(message = "访问域名不能为空")
    private String localDomain = "";
}
