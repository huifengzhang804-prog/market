package com.medusa.gruul.user.service.config;


import com.medusa.gruul.carrier.pigeon.api.rpc.PigeonChatStatisticsRpcService;
import com.medusa.gruul.cart.api.rpc.CartRpcService;
import com.medusa.gruul.goods.api.rpc.GoodsRpcService;
import com.medusa.gruul.order.api.rpc.OrderRpcService;
import com.medusa.gruul.overview.api.rpc.DataExportRecordRpcService;
import com.medusa.gruul.payment.api.rpc.PaymentRpcService;
import com.medusa.gruul.service.uaa.api.rpc.UaaRpcService;
import com.medusa.gruul.storage.api.rpc.StorageRpcService;
import org.apache.dubbo.config.annotation.DubboReference;
import org.apache.dubbo.config.spring.ReferenceBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author xiaoq
 */

@Configuration
@ConditionalOnProperty(prefix = "gruul",name = "single",havingValue = "false",matchIfMissing = true)
public class DubboConfig {

    @Bean
    @DubboReference
    public ReferenceBean<GoodsRpcService> goodsRpcServiceReferenceBean(){
        return new ReferenceBean<>();
    }

    @Bean
    @DubboReference
    public ReferenceBean<StorageRpcService> storageRpcServiceReferenceBean(){
        return new ReferenceBean<>();
    }

    @Bean
    @DubboReference
    public ReferenceBean<PaymentRpcService> paymentRpcServiceReferenceBean(){
        return new ReferenceBean<>();
    }

    @Bean
    @DubboReference
    public ReferenceBean<OrderRpcService> orderRpcServiceReferenceBean() {
        return new ReferenceBean<>();
    }
    @Bean
    @DubboReference
    public ReferenceBean<DataExportRecordRpcService> dataExportRecordRpcServiceReferenceBean(){
        return new ReferenceBean<>();
    }
    @Bean
    @DubboReference
    public ReferenceBean<PigeonChatStatisticsRpcService> pigeonChatStatisticsRpcServiceReferenceBean(){
        return new ReferenceBean<>();
    }
    @Bean
    @DubboReference
    public ReferenceBean<UaaRpcService> uaaRpcServiceReferenceBean(){
        return new ReferenceBean<>();
    }

    @Bean
    @DubboReference
    public ReferenceBean<CartRpcService> cartRpcServiceReferenceBean(){
        return new ReferenceBean<>();
    }

}
