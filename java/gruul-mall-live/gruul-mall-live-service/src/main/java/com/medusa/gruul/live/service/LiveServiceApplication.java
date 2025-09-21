package com.medusa.gruul.live.service;

import com.medusa.gruul.global.i18n.I18NPropertiesLoader;
import com.medusa.gruul.global.model.constant.AspectOrder;
import com.medusa.gruul.live.service.properties.AppletsProperties;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.spring.context.annotation.EnableDubbo;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cache.annotation.EnableCaching;


/**
 * @author miskw
 * @date 2022/11/7
 */
@SpringBootApplication
@EnableCaching(order = AspectOrder.CACHE_ASPECT)
@EnableDubbo(scanBasePackages = "com.medusa.gruul.live.service.service.impl")
@Slf4j
@EnableConfigurationProperties(AppletsProperties.class)
public class LiveServiceApplication implements I18NPropertiesLoader {

    public static void main(String[] args) {
        SpringApplication.run(LiveServiceApplication.class, args);
    }

    @Override
    public String path() {
        return "i18n/live";
    }
}
