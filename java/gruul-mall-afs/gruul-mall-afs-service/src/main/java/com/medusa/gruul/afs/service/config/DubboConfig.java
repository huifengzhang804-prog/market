package com.medusa.gruul.afs.service.config;

import com.medusa.gruul.carrier.pigeon.api.rpc.PigeonChatStatisticsRpcService;
import com.medusa.gruul.order.api.rpc.OrderRpcService;
import com.medusa.gruul.overview.api.rpc.DataExportRecordRpcService;
import com.medusa.gruul.payment.api.rpc.PaymentRpcService;
import com.medusa.gruul.service.uaa.api.rpc.UaaRpcService;
import com.medusa.gruul.shop.api.rpc.ShopRpcService;
import com.medusa.gruul.user.api.rpc.UserRpcService;
import org.apache.dubbo.config.annotation.DubboReference;
import org.apache.dubbo.config.spring.ReferenceBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * <p></p>
 *
 * @author 张治保
 * date 2022/8/4
 */
@ConditionalOnProperty(prefix = "gruul", name = "single", havingValue = "false", matchIfMissing = true)
@Configuration
public class DubboConfig {

    @Bean
    @DubboReference
    public ReferenceBean<OrderRpcService> orderRpcServiceReferenceBean() {
        return new ReferenceBean<>();
    }

    @Bean
    @DubboReference
    public ReferenceBean<PaymentRpcService> paymentRpcServiceReferenceBean() {
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
    public ReferenceBean<UaaRpcService> uaaRpcServiceReferenceBean() {
        return new ReferenceBean<>();
    }
}
