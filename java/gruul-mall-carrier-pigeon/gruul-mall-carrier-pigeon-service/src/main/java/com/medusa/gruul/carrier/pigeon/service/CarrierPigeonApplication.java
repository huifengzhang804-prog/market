package com.medusa.gruul.carrier.pigeon.service;

import com.medusa.gruul.carrier.pigeon.service.properties.RabbitStompProperties;
import com.medusa.gruul.global.i18n.I18NPropertiesLoader;
import com.medusa.gruul.global.model.constant.AspectOrder;
import org.apache.dubbo.config.spring.context.annotation.EnableDubbo;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * 信鸽服务启动类
 *
 * @author 张治保
 * date 2022/4/26
 */
@EnableConfigurationProperties(RabbitStompProperties.class)
@SpringBootApplication
@EnableCaching(order = AspectOrder.CACHE_ASPECT)
@EnableTransactionManagement(order = AspectOrder.TRANSACTIONAL_ASPECT)
@EnableDubbo(scanBasePackages = "com.medusa.gruul.carrier.pigeon.service.modules.base.service.rpc")
@EnableScheduling
public class CarrierPigeonApplication implements I18NPropertiesLoader {
    public static void main(String[] args) {
        SpringApplication.run(CarrierPigeonApplication.class, args);
    }

    @Override
    public String path() {
        return "i18n/pigeon";
    }
}
