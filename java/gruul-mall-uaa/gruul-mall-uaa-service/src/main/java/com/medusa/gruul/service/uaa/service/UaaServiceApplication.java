package com.medusa.gruul.service.uaa.service;

import com.medusa.gruul.global.i18n.I18NPropertiesLoader;
import com.medusa.gruul.global.model.constant.AspectOrder;
import com.medusa.gruul.service.uaa.service.properties.UaaProperties;
import org.apache.dubbo.config.spring.context.annotation.EnableDubbo;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cache.annotation.EnableCaching;

/**
 * RBAC模型 uaa授权服务
 *
 * @author 张治保
 * date 2022/2/23
 */
@SpringBootApplication
@EnableCaching(order = AspectOrder.CACHE_ASPECT)
@EnableDubbo(scanBasePackages = "com.medusa.gruul.service.uaa.service.service.rpc")
@EnableConfigurationProperties(UaaProperties.class)
public class UaaServiceApplication implements I18NPropertiesLoader {
    public static void main(String[] args) {
        SpringApplication.run(UaaServiceApplication.class, args);
    }

    @Override
    public String path() {
        return "i18n/uaa";
    }
}
