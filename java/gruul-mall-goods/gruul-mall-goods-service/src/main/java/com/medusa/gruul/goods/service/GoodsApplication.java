package com.medusa.gruul.goods.service;

import com.medusa.gruul.global.i18n.I18NPropertiesLoader;
import com.medusa.gruul.global.model.constant.AspectOrder;
import com.medusa.gruul.goods.service.properties.GoodsConfigurationProperties;
import org.apache.dubbo.config.spring.context.annotation.EnableDubbo;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cache.annotation.EnableCaching;

/**
 * 商品服务
 *
 * @author xiaoq
 * @since 2022/03/2 22:34
 */
@SpringBootApplication
@EnableCaching(order = AspectOrder.CACHE_ASPECT)
@EnableConfigurationProperties(GoodsConfigurationProperties.class)
@EnableDubbo(scanBasePackages = "com.medusa.gruul.goods.service.service.impl")
public class GoodsApplication implements I18NPropertiesLoader {

    public static void main(String[] args) {
        SpringApplication.run(GoodsApplication.class, args);
    }


    @Override
    public String path() {
        return "i18n/goods";
    }
}