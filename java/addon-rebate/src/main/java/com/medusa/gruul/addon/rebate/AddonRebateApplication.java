package com.medusa.gruul.addon.rebate;

import com.medusa.gruul.global.i18n.I18NPropertiesLoader;
import org.apache.dubbo.config.spring.context.annotation.EnableDubbo;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@EnableDubbo(scanBasePackages = "com.medusa.gruul.addon.rebate.addon.impl")
public class AddonRebateApplication implements I18NPropertiesLoader {

    public static void main(String[] args) {
        SpringApplication.run(AddonRebateApplication.class, args);
    }

    @Override
    public String path() {
        return "i18n/rebate";
    }
}
