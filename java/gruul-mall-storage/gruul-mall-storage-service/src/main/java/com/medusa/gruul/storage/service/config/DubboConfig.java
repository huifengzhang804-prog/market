package com.medusa.gruul.storage.service.config;

import com.medusa.gruul.carrier.pigeon.api.rpc.PigeonChatStatisticsRpcService;
import com.medusa.gruul.goods.api.rpc.GoodsRpcService;
import com.medusa.gruul.overview.api.rpc.DataExportRecordRpcService;
import com.medusa.gruul.search.api.rpc.SearchRpcService;
import com.medusa.gruul.service.uaa.api.rpc.UaaRpcService;
import org.apache.dubbo.config.annotation.DubboReference;
import org.apache.dubbo.config.spring.ReferenceBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author 张治保
 * date 2023/3/6
 */
@Configuration
@ConditionalOnProperty(prefix = "gruul", name = "single", havingValue = "false", matchIfMissing = true)
public class DubboConfig {

    @Bean
    @DubboReference
    public ReferenceBean<SearchRpcService> searchRpcServiceReferenceBean() {
        return new ReferenceBean<>();
    }

    @Bean
    @DubboReference
    public ReferenceBean<GoodsRpcService> goodsRpcServiceReferenceBean() {
        return new ReferenceBean<>();
    }

    @Bean
    @DubboReference
    public ReferenceBean<UaaRpcService> uaaRpcServiceReferenceBean() {
        return new ReferenceBean<>();
    }

    @Bean
    @DubboReference
    public ReferenceBean<DataExportRecordRpcService> dataExportRecordRpcServiceReferenceBean() {
        return new ReferenceBean<>();
    }

    @Bean
    @DubboReference
    public ReferenceBean<PigeonChatStatisticsRpcService> pigeonChatStatisticsRpcServiceReferenceBean() {
        return new ReferenceBean<>();
    }


}
