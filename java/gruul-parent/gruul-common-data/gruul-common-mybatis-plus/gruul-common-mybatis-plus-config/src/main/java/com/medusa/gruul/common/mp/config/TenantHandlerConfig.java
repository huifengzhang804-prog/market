package com.medusa.gruul.common.mp.config;

import com.medusa.gruul.common.mp.handler.ProviderTenantHandler;
import com.medusa.gruul.common.mp.handler.ShopTenantHandler;
import com.medusa.gruul.common.mp.properties.TenantConfigProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;

/**
 * 多租户配置
 * @author 张治保
 * date 2022/4/22
 */
@RequiredArgsConstructor
public class TenantHandlerConfig {


    @Bean
    @ConditionalOnProperty(prefix = "gruul.tenant",name = "enable-multi-provider")
    public ProviderTenantHandler providerTenantHandler(TenantConfigProperties properties){
        return new ProviderTenantHandler(properties);
    }

    @Bean
    @ConditionalOnProperty(prefix = "gruul.tenant",name = "enable-multi-shop")
    public ShopTenantHandler shopTenantHandler(TenantConfigProperties properties){
        return new ShopTenantHandler(properties);
    }
}
