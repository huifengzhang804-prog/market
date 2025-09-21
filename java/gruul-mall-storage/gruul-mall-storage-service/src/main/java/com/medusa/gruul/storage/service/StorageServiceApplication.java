package com.medusa.gruul.storage.service;

import com.medusa.gruul.global.i18n.I18NPropertiesLoader;
import com.medusa.gruul.global.model.constant.AspectOrder;
import com.medusa.gruul.storage.service.properties.StorageProperties;
import org.apache.dubbo.config.spring.context.annotation.EnableDubbo;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * @author 张治保
 * date 2022/07/06
 */
@SpringBootApplication
@EnableDubbo(scanBasePackages = "com.medusa.gruul.storage.service.service.rpc")
@EnableConfigurationProperties(StorageProperties.class)
@EnableCaching(order = AspectOrder.CACHE_ASPECT)
@EnableAsync
@EnableTransactionManagement(order = AspectOrder.TRANSACTIONAL_ASPECT)
public class StorageServiceApplication implements I18NPropertiesLoader {
    public static void main(String[] args) {
        SpringApplication.run(StorageServiceApplication.class, args);
    }

    @Override
    public String path() {
        return "i18n/storage";
    }
}
