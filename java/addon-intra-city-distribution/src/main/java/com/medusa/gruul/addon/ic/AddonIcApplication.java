package com.medusa.gruul.addon.ic;

import com.medusa.gruul.addon.ic.properties.ICConfigurationProperties;
import com.medusa.gruul.global.i18n.I18NPropertiesLoader;
import org.apache.dubbo.config.spring.context.annotation.EnableDubbo;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

/**
 * 同城配送服务启动类
 *
 * @author miskw
 */
@SpringBootApplication
@EnableConfigurationProperties(ICConfigurationProperties.class)
@EnableDubbo(scanBasePackages = "com.medusa.gruul.addon.ic.addon")
public class AddonIcApplication implements I18NPropertiesLoader {
    public static void main(String[] args) {
        SpringApplication.run(AddonIcApplication.class, args);
    }

    @Override
    public String path() {
        return "i18n/addon-ic";
    }
}
