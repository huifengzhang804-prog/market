package com.medusa.gruul.addon.rebate.config;

import com.medusa.gruul.carrier.pigeon.api.rpc.PigeonChatStatisticsRpcService;
import com.medusa.gruul.goods.api.rpc.GoodsRpcService;
import com.medusa.gruul.order.api.rpc.OrderRpcService;
import com.medusa.gruul.overview.api.rpc.DataExportRecordRpcService;
import com.medusa.gruul.overview.api.rpc.OverviewRpcService;
import com.medusa.gruul.service.uaa.api.rpc.UaaRpcService;
import com.medusa.gruul.shop.api.rpc.ShopRpcService;
import com.medusa.gruul.user.api.rpc.UserRpcService;
import java.sql.Ref;
import org.apache.dubbo.config.annotation.DubboReference;
import org.apache.dubbo.config.spring.ReferenceBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConditionalOnProperty(prefix = "gruul", name = "single", havingValue = "false", matchIfMissing = true)
public class DubboConfig {

    @Bean
    @DubboReference
    public ReferenceBean<UserRpcService> userRpcServiceReferenceBean() {
        return new ReferenceBean<>();
    }

    @Bean
    @DubboReference
    public ReferenceBean<ShopRpcService> shopRpcServiceReferenceBean() {
        return new ReferenceBean<>();
    }

    @Bean
    @DubboReference
    public ReferenceBean<OverviewRpcService> overviewRpcServiceReferenceBean() {
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

    @Bean
    @DubboReference
    public ReferenceBean<PigeonChatStatisticsRpcService> pigeonChatStatisticsRpcServiceReferenceBean() {
        return new ReferenceBean<>();
    }

    @Bean
    @DubboReference
    public ReferenceBean<DataExportRecordRpcService> dataExportRecordRpcServiceReferenceBean() {
        return new ReferenceBean<>();
    }

    @Bean
    @DubboReference
    public ReferenceBean<GoodsRpcService> goodsRpcServiceReferenceBean() {
        return new ReferenceBean<>();
    }
}

