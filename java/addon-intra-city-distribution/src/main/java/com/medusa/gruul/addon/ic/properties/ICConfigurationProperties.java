package com.medusa.gruul.addon.ic.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author 张治保
 * @since 2024/8/23
 */
@Getter
@Setter
@ConfigurationProperties(prefix = "gruul.addon.ic")
public class ICConfigurationProperties {

    /**
     * 是否使用同城开放平台的测试环境
     */
    private boolean openApiTest = true;

}
