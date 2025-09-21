package com.medusa.gruul.addon.ic.modules.uupt.config;

import com.medusa.gruul.addon.ic.modules.opens.uupt.UuptConfig;
import com.medusa.gruul.addon.ic.modules.opens.uupt.api.IUuptApiFactory;
import com.medusa.gruul.addon.ic.modules.opens.uupt.core.UuptApiFactory;
import com.medusa.gruul.addon.ic.modules.uupt.model.UuptConstant;
import com.medusa.gruul.addon.ic.properties.ICConfigurationProperties;
import com.medusa.gruul.common.redis.util.RedisUtil;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author 张治保
 * @since 2024/8/23
 */
@Configuration
public class UuptConfiguration {

    @Bean
    public IUuptApiFactory uuptApiFactory(ICConfigurationProperties configurationProperties) {
        UuptConfig uuptConfig = RedisUtil.getCacheMap(UuptConstant.UUPT_CONFIG_CACHE_KEY, UuptConfig.class);
        if (uuptConfig != null) {
            return new UuptApiFactory(uuptConfig.setTest(configurationProperties.isOpenApiTest()));
        }
        return new IUuptApiFactory() {
            private UuptConfig config;

            @Override
            public UuptConfig config() {
                if (config != null) {
                    return config;
                }
                config = RedisUtil.getCacheMap(UuptConstant.UUPT_CONFIG_CACHE_KEY, UuptConfig.class);
                if (config != null) {
                    config.setTest(configurationProperties.isOpenApiTest());
                }
                return config;
            }
        };
    }
}
