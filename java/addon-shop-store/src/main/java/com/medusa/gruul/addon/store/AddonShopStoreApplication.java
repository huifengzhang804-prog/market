package com.medusa.gruul.addon.store;

import com.medusa.gruul.global.i18n.I18NPropertiesLoader;
import org.apache.dubbo.config.spring.context.annotation.EnableDubbo;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @author xiaoq
 * date 2023/03/02
 */
@SpringBootApplication
@EnableDubbo(scanBasePackages = "com.medusa.gruul.addon.store.addon.impl")
public class AddonShopStoreApplication implements I18NPropertiesLoader {
    public static void main(String[] args) {
        SpringApplication.run(AddonShopStoreApplication.class, args);
    }

    @Override
    public String path() {
        return "i18n/shop-store";
    }
}
