package com.medusa.gruul.addon.store.config;

import com.medusa.gruul.order.api.rpc.OrderRpcService;
import com.medusa.gruul.service.uaa.api.rpc.UaaRpcService;
import com.medusa.gruul.shop.api.rpc.ShopRpcService;
import com.medusa.gruul.user.api.rpc.UserRpcService;
import org.apache.dubbo.config.spring.ReferenceBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author xiaoq
 */
@Configuration
@ConditionalOnProperty(prefix = "gruul", name = "single", havingValue = "false", matchIfMissing = true)
public class DubboConfig {



    @Bean
    public ReferenceBean<UserRpcService> userRpcServiceReferenceBean() {
        return new ReferenceBean<>();
    }


    @Bean
    public ReferenceBean<UaaRpcService> uaaRpcServiceReferenceBean() {
        return new ReferenceBean<>();
    }


    @Bean
    public ReferenceBean<OrderRpcService> orderRpcServiceReferenceBean() {
        return new ReferenceBean<>();
    }



    @Bean
    public ReferenceBean<ShopRpcService> shopRpcServiceReferenceBean() {
        return new ReferenceBean<>();
    }
}
