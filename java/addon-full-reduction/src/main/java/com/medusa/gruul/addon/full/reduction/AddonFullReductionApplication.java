package com.medusa.gruul.addon.full.reduction;


import com.medusa.gruul.global.i18n.I18NPropertiesLoader;
import org.apache.dubbo.config.spring.context.annotation.EnableDubbo;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @author wudi
 */
@SpringBootApplication
@EnableDubbo(scanBasePackages = "com.medusa.gruul.addon.full.reduction.addon.impl")
public class AddonFullReductionApplication implements I18NPropertiesLoader {

    public static void main(String[] args) {
        SpringApplication.run(AddonFullReductionApplication.class, args);
    }

    @Override
    public String path() {
        return "i18n/full-reduction";
    }

}