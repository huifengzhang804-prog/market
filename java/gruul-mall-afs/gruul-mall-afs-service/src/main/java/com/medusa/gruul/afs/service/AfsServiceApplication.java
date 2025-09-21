package com.medusa.gruul.afs.service;

import com.medusa.gruul.afs.service.properties.AfsConfigurationProperties;
import com.medusa.gruul.global.i18n.I18NPropertiesLoader;
import com.medusa.gruul.global.model.constant.AspectOrder;
import org.apache.dubbo.config.spring.context.annotation.DubboComponentScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * 售后服务启动类
 *
 * @author 张治保
 * @since 2022/8/1
 */
@SpringBootApplication
@DubboComponentScan(basePackages = "com.medusa.gruul.afs.service.service.rpc")
@EnableCaching(order = AspectOrder.CACHE_ASPECT)
@EnableConfigurationProperties(AfsConfigurationProperties.class)
@EnableTransactionManagement(order = AspectOrder.TRANSACTIONAL_ASPECT)
public class AfsServiceApplication implements I18NPropertiesLoader {

    public static void main(String[] args) {
        SpringApplication.run(AfsServiceApplication.class, args);
    }


    @Override
    public String path() {
        return "i18n/afs";
    }
}
