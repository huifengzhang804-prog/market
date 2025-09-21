package com.medusa.gruul.order.service;

import com.medusa.gruul.global.i18n.I18NPropertiesLoader;
import com.medusa.gruul.order.service.properties.OrderConfigurationProperties;
import org.apache.dubbo.config.spring.context.annotation.EnableDubbo;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * @author xiaoq
 * date 2.25
 */
@SpringBootApplication
@EnableConfigurationProperties(OrderConfigurationProperties.class)
@EnableTransactionManagement
@EnableDubbo(scanBasePackages = "com.medusa.gruul.order.service.modules.order.service.rpc")
public class OrderServiceApplication implements I18NPropertiesLoader {

    public static void main(String[] args) {
        SpringApplication.run(OrderServiceApplication.class, args);
    }


    @Override
    public String path() {
        return "i18n/order";
    }
}
