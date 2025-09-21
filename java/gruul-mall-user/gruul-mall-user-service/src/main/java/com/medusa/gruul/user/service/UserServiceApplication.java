package com.medusa.gruul.user.service;

import com.medusa.gruul.global.i18n.I18NPropertiesLoader;
import com.medusa.gruul.global.model.constant.AspectOrder;
import com.medusa.gruul.user.service.properties.UserConfigurationProperties;
import org.apache.dubbo.config.spring.context.annotation.EnableDubbo;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cache.annotation.EnableCaching;

/**
 * 用户服务启动类
 *
 * @author 张治保
 * date 2022/3/8
 */
@SpringBootApplication
@EnableCaching(order = AspectOrder.CACHE_ASPECT)
@EnableConfigurationProperties(UserConfigurationProperties.class)
@EnableDubbo(scanBasePackages = "com.medusa.gruul.user.service.service.impl")
public class UserServiceApplication implements I18NPropertiesLoader {

    public static void main(String[] args) {
        SpringApplication.run(UserServiceApplication.class, args);
    }

    @Override
    public String path() {
        return "i18n/user";
    }
}
