package com.medusa.gruul.addon.matching.treasure;


import com.medusa.gruul.addon.matching.treasure.properties.MatchingTreasureConfigurationProperties;
import com.medusa.gruul.global.i18n.I18NPropertiesLoader;
import org.apache.dubbo.config.spring.context.annotation.EnableDubbo;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties(MatchingTreasureConfigurationProperties.class)
@EnableDubbo(scanBasePackages = "com.medusa.gruul.addon.matching.treasure.addon.impl")
public class AddonMatchingTreasureApplication implements I18NPropertiesLoader {

    public static void main(String[] args) {
        SpringApplication.run(AddonMatchingTreasureApplication.class, args);
    }

    @Override
    public String path() {
        return "i18n/matching-treasure";
    }
}
