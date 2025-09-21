package com.medusa.gruul.goods.service.config;

import com.medusa.gruul.carrier.pigeon.api.rpc.PigeonChatStatisticsRpcService;
import com.medusa.gruul.cart.api.rpc.CartRpcService;
import com.medusa.gruul.order.api.rpc.OrderRpcService;
import com.medusa.gruul.search.api.rpc.SearchRpcService;
import com.medusa.gruul.service.uaa.api.rpc.UaaRpcService;
import com.medusa.gruul.shop.api.rpc.ShopRpcService;
import com.medusa.gruul.storage.api.rpc.StorageRpcService;
import com.medusa.gruul.user.api.rpc.UserRpcService;
import org.apache.dubbo.config.annotation.DubboReference;
import org.apache.dubbo.config.annotation.Method;
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
    @DubboReference
    public ReferenceBean<StorageRpcService> storageRpcServiceReferenceBean() {
        return new ReferenceBean<>();
    }

    @Bean
    @DubboReference
    public ReferenceBean<OrderRpcService> orderRpcServiceReferenceBean() {
        return new ReferenceBean<>();
    }


    @Bean
    @DubboReference
    public ReferenceBean<ShopRpcService> shopRpcServiceReferenceBean() {
        return new ReferenceBean<>();
    }

    @Bean
    @DubboReference
    public ReferenceBean<UserRpcService> userRpcServiceReferenceBean() {
        return new ReferenceBean<>();
    }

    @Bean
    @DubboReference
    public ReferenceBean<CartRpcService> cartRpcServiceReferenceBean() {
        return new ReferenceBean<>();
    }

    @Bean
    @DubboReference
    public ReferenceBean<SearchRpcService> searchRpcServiceReferenceBean() {
        return new ReferenceBean<>();
    }

    @Bean
    @DubboReference(methods = {
            @Method(name = "batchUploadUrls", timeout = 30000)
    })
    public ReferenceBean<PigeonChatStatisticsRpcService> pigeonChatStatisticsRpcServiceReferenceBean() {
        return new ReferenceBean<>();
    }

    @Bean
    @DubboReference
    public ReferenceBean<UaaRpcService> uaaRpcServiceReferenceBean() {
        return new ReferenceBean<>();
    }
}