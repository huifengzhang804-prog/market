package com.medusa.gruul.addon.supplier;

import com.medusa.gruul.global.i18n.I18NPropertiesLoader;
import org.apache.dubbo.config.spring.context.annotation.EnableDubbo;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @author 张治保
 * date 2023/7/3
 */
@SpringBootApplication
@EnableDubbo(scanBasePackages = "com.medusa.gruul.addon.supplier.addon.impl")
public class AddonSupplierApplication implements I18NPropertiesLoader {
    public static void main(String[] args) {
        SpringApplication.run(AddonSupplierApplication.class, args);
    }

    @Override
    public String path() {
        return "i18n/supplier";
    }
}
