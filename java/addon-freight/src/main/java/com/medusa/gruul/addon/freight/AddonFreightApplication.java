package com.medusa.gruul.addon.freight;

import com.medusa.gruul.global.i18n.I18NPropertiesLoader;
import com.medusa.gruul.global.model.constant.AspectOrder;
import org.apache.dubbo.config.spring.context.annotation.EnableDubbo;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

/**
 * Description: 物流服务
 *
 * @author xiaoq
 * date : 2022/6/2 22:34
 */
@SpringBootApplication
@EnableCaching(order = AspectOrder.CACHE_ASPECT)
@EnableDubbo(scanBasePackages = "com.medusa.gruul.addon.freight.addon.impl")
public class AddonFreightApplication implements I18NPropertiesLoader {

    public static void main(String[] args) {
        SpringApplication.run(AddonFreightApplication.class, args);
    }

    @Override
    public String path() {
        return "i18n/freight";
    }
}