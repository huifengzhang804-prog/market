package com.medusa.gruul.addon.full.reduction.config;

import com.medusa.gruul.goods.api.rpc.GoodsRpcService;
import com.medusa.gruul.shop.api.rpc.ShopRpcService;
import org.apache.dubbo.config.annotation.DubboReference;
import org.apache.dubbo.config.spring.ReferenceBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author 张治保
 * date 2022/7/12
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
    public ReferenceBean<GoodsRpcService> goodsRpcServiceReferenceBean() {
        return new ReferenceBean<>();
    }
}
