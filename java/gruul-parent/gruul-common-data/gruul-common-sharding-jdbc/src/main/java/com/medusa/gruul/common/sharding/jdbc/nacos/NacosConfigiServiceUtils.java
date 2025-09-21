package com.medusa.gruul.common.sharding.jdbc.nacos;

import com.alibaba.cloud.nacos.NacosConfigManager;
import com.alibaba.nacos.api.config.ConfigService;
import lombok.Getter;

/**
 * @author 张治保
 * @since 2024/5/9
 */
public class NacosConfigiServiceUtils {

    @Getter
    private static NacosConfigManager configManager;
    @Getter
    private static ConfigService configService;

    public static void init(NacosConfigManager nacosConfigManager) {
        configManager = nacosConfigManager;
        configService = nacosConfigManager.getConfigService();

    }

}
