package com.medusa.gruul.addon.coupon;

import com.medusa.gruul.addon.coupon.properties.CouponConfigurationProperties;
import com.medusa.gruul.global.i18n.I18NPropertiesLoader;
import org.apache.dubbo.config.spring.context.annotation.EnableDubbo;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

/**
 * @author 张治保
 * date 2022/11/2
 */
@SpringBootApplication
@EnableConfigurationProperties(CouponConfigurationProperties.class)
@EnableDubbo(scanBasePackages = "com.medusa.gruul.addon.coupon.addon.impl")
public class AddonCouponApplication implements I18NPropertiesLoader {
    public static void main(String[] args) {
        SpringApplication.run(AddonCouponApplication.class, args);
    }
    
    @Override
    public String path() {
        return "i18n/coupon";
    }
}
