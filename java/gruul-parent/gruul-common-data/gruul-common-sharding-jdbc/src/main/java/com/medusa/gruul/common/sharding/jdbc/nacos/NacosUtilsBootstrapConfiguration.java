package com.medusa.gruul.common.sharding.jdbc.nacos;

import com.alibaba.cloud.nacos.NacosConfigManager;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;

/**
 * @author 张治保
 * @since 2024/5/9
 */
@ConditionalOnClass(name = "com.alibaba.cloud.nacos.NacosConfigBootstrapConfiguration")
@ConditionalOnProperty(name = {"spring.cloud.nacos.config.enabled"}, matchIfMissing = true)
public class NacosUtilsBootstrapConfiguration {

    public NacosUtilsBootstrapConfiguration(NacosConfigManager nacosConfigManager) {

        NacosConfigiServiceUtils.init(nacosConfigManager);

    }

}
