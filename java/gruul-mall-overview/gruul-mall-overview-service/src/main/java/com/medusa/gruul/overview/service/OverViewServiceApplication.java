package com.medusa.gruul.overview.service;

import com.medusa.gruul.global.i18n.I18NPropertiesLoader;
import org.apache.dubbo.config.spring.context.annotation.EnableDubbo;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @author WuDi date 2022/10/8
 */
@SpringBootApplication
@EnableDubbo(scanBasePackages =
    {"com.medusa.gruul.overview.service.modules.base.service.rpc",
    "com.medusa.gruul.overview.service.modules.export.service.rpc",
    "com.medusa.gruul.overview.service.modules.activity.service.rpc",
    "com.medusa.gruul.overview.service.modules.withdraw.service.rpc"})
public class OverViewServiceApplication implements I18NPropertiesLoader{

    public static void main(String[] args) {
        SpringApplication.run(OverViewServiceApplication.class, args);
    }

    @Override
    public String path() {
        return "i18n/overview";
    }
}
