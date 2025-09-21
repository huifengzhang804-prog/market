package com.medusa.gruul.addon.seckill.config;

import com.medusa.gruul.goods.api.rpc.GoodsRpcService;
import com.medusa.gruul.shop.api.rpc.ShopRpcService;
import com.medusa.gruul.storage.api.rpc.StorageActivityRpcService;
import com.medusa.gruul.storage.api.rpc.StorageRpcService;
import org.apache.dubbo.config.spring.ReferenceBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author 张治保
 * @since 2024/5/28
 */
@Configuration
@ConditionalOnProperty(prefix = "gruul", name = "single", havingValue = "false", matchIfMissing = true)
public class DubboConfig {

    @Bean
    public ReferenceBean<GoodsRpcService> goodsRpcServiceReferenceBean() {
        return new ReferenceBean<>();
    }


    @Bean
    public ReferenceBean<StorageActivityRpcService> storageActivityRpcServiceReferenceBean() {
        return new ReferenceBean<>();
    }

    @Bean
    public ReferenceBean<StorageRpcService> storageRpcServiceReferenceBean() {
        return new ReferenceBean<>();
    }


    @Bean
    public ReferenceBean<ShopRpcService> shopRpcServiceReferenceBean() {
        return new ReferenceBean<>();
    }


}
