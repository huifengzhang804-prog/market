package com.medusa.gruul.addon.live;

import com.medusa.gruul.addon.live.properties.AddressKeyProperties;
import com.medusa.gruul.global.i18n.I18NPropertiesLoader;
import org.apache.dubbo.config.spring.context.annotation.EnableDubbo;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * @author miskw
 * @date ${DATE}
 * @describe 描述
 */
@EnableScheduling
@SpringBootApplication
@EnableConfigurationProperties(AddressKeyProperties.class)
@EnableDubbo(scanBasePackages = "com.medusa.gruul.addon.live.addon")
public class AddonLiveApplication implements I18NPropertiesLoader {
    public static void main(String[] args) {
        SpringApplication.run(AddonLiveApplication.class);
    }
    @Override
    public String path() {
        return "i18n/addon-live";
    }
}