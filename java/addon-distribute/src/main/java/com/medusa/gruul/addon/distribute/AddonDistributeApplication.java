package com.medusa.gruul.addon.distribute;

import com.medusa.gruul.addon.distribute.properties.DistributeConfigurationProperties;
import com.medusa.gruul.global.i18n.I18NPropertiesLoader;
import org.apache.dubbo.config.spring.context.annotation.EnableDubbo;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * @author 张治保
 * date 2022/11/14
 */
@SpringBootApplication
@EnableScheduling
@EnableConfigurationProperties(DistributeConfigurationProperties.class)
@EnableDubbo(scanBasePackages = "com.medusa.gruul.addon.distribute.addon.impl")
public class AddonDistributeApplication implements I18NPropertiesLoader {
    public static void main(String[] args) {
        SpringApplication.run(AddonDistributeApplication.class, args);
    }

    @Override
    public String path() {
        return "i18n/distribute";
    }
}
