package com.medusa.gruul.addon.integral.config;

import com.medusa.gruul.payment.api.rpc.PaymentRpcService;
import com.medusa.gruul.service.uaa.api.rpc.UaaRpcService;
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
    public ReferenceBean<PaymentRpcService> paymentRpcServiceReferenceBean() {
        return new ReferenceBean<>();
    }


    @Bean
    public ReferenceBean<UserRpcService> userRpcServiceReferenceBean() {
        return new ReferenceBean<>();
    }


    @Bean
    public ReferenceBean<UaaRpcService> UaaRpcServiceReferenceBean() {
        return new ReferenceBean<>();
    }
}
