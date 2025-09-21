package com.medusa.gruul.addon.integral;

import com.medusa.gruul.addon.integral.properties.IntegralOrderProperties;
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
@EnableConfigurationProperties({IntegralOrderProperties.class})
@EnableDubbo(scanBasePackages = "com.medusa.gruul.addon.integral.addon.impl")
public class AddonIntegralApplication implements I18NPropertiesLoader {
    public static void main(String[] args) {
        SpringApplication.run(AddonIntegralApplication.class, args);
    }

    @Override
    public String path() {
        return "i18n/integral";
    }
}
