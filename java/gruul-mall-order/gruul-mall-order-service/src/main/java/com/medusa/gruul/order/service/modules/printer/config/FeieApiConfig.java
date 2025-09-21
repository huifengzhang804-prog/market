package com.medusa.gruul.order.service.modules.printer.config;

import com.medusa.gruul.common.redis.util.RedisUtil;
import com.medusa.gruul.order.service.modules.printer.feie.api.FeieApi;
import com.medusa.gruul.order.service.modules.printer.feie.api.FeieConfig;
import com.medusa.gruul.order.service.modules.printer.feie.api.IFeieApi;
import com.medusa.gruul.order.service.modules.printer.model.PrinterConstant;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author 张治保
 * @since 2024/8/21
 */
@Configuration
@RequiredArgsConstructor
public class FeieApiConfig {


    @Bean
    public IFeieApi feieApi() {
        FeieConfig feieConfig = RedisUtil.getCacheMap(PrinterConstant.FEIE_CONFIG_CACHE_KEY, FeieConfig.class);
        if (feieConfig != null) {
            return new FeieApi(feieConfig);
        }
        return new FeieApi() {
            @Override
            public FeieConfig config() {
                if (config != null) {
                    return config;
                }
                config = RedisUtil.getCacheMap(PrinterConstant.FEIE_CONFIG_CACHE_KEY, FeieConfig.class);
                return config;
            }
        };
    }

}
