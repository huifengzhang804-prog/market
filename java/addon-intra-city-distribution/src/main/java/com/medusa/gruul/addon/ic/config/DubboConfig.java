package com.medusa.gruul.addon.ic.config;

import com.medusa.gruul.order.api.rpc.OrderRpcService;
import com.medusa.gruul.service.uaa.api.rpc.UaaRpcService;
import com.medusa.gruul.shop.api.rpc.ShopRpcService;
import org.apache.dubbo.config.annotation.DubboReference;
import org.apache.dubbo.config.spring.ReferenceBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author miskw
 * @date 2023/3/2
 */
@Configuration
@ConditionalOnProperty(prefix = "gruul", name = "single", havingValue = "false", matchIfMissing = true)
public class DubboConfig {
    @Bean
    @DubboReference
    public ReferenceBean<ShopRpcService> shopRpcServiceReferenceBean() {
        return new ReferenceBean<>();
    }

    @Bean
    @DubboReference
    public ReferenceBean<OrderRpcService> orderRpcServiceReferenceBean() {
        return new ReferenceBean<>();
    }

    @Bean
    @DubboReference
    public ReferenceBean<UaaRpcService> uaaRpcServiceReferenceBean() {
        return new ReferenceBean<>();
    }


}
