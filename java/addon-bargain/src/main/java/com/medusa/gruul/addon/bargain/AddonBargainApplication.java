package com.medusa.gruul.addon.bargain;


import com.medusa.gruul.addon.bargain.properties.BargainConfigurationProperties;
import com.medusa.gruul.global.i18n.I18NPropertiesLoader;
import org.apache.dubbo.config.spring.context.annotation.EnableDubbo;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;


/**
 * @author wudi
 */
@EnableDubbo(scanBasePackages = "com.medusa.gruul.addon.bargain.addon.impl")
@EnableConfigurationProperties(BargainConfigurationProperties.class)
@SpringBootApplication
public class AddonBargainApplication implements I18NPropertiesLoader {

    public static void main(String[] args) {

        SpringApplication.run(AddonBargainApplication.class, args);
    }

    @Override
    public String path() {
        return "i18n/bargain";
    }

}
