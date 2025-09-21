package com.medusa.gruul.payment.service;

import com.egzosn.pay.spring.boot.autoconfigue.PayAutoConfiguration;
import com.medusa.gruul.global.i18n.I18NPropertiesLoader;
import com.medusa.gruul.global.model.constant.AspectOrder;
import com.medusa.gruul.payment.service.properties.PaymentProperty;
import org.apache.dubbo.config.spring.context.annotation.EnableDubbo;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Import;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * 支付服务
 *
 * @author xiaoq
 * Description 支付服务
 * date 2022-07-12 17:05
 */

@Import(PayAutoConfiguration.class)
@SpringBootApplication
@EnableConfigurationProperties(PaymentProperty.class)
@EnableScheduling
@EnableCaching(order = AspectOrder.CACHE_ASPECT)
@EnableDubbo(scanBasePackages = "com.medusa.gruul.payment.service.service.impl")
public class PaymentApplication implements I18NPropertiesLoader {

    public static void main(String[] args) {
        SpringApplication.run(PaymentApplication.class, args);
    }


    @Override
    public String path() {
        return "i18n/payment";
    }
}
