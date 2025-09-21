package com.medusa.gruul.addon.platform;

import com.medusa.gruul.addon.platform.properties.PlatformConfigurationProperties;
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
 * 平台端服务插件
 *
 * @author 张治保
 * date 2022/4/14
 */
@SpringBootApplication
@EnableConfigurationProperties(PlatformConfigurationProperties.class)
@EnableCaching(order = AspectOrder.CACHE_ASPECT)
@EnableDubbo(scanBasePackages = "com.medusa.gruul.addon.platform.addon.impl")
@EnableTransactionManagement(order = AspectOrder.TRANSACTIONAL_ASPECT)
@EnableScheduling
public class AddonPlatformApplication implements I18NPropertiesLoader {
    public static void main(String[] args) {
        SpringApplication.run(AddonPlatformApplication.class, args);
    }

    @Override
    public String path() {
        return "i18n/platform";
    }
}
