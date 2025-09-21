package com.medusa.gruul.search.service;


import com.medusa.gruul.global.i18n.I18NPropertiesLoader;
import com.medusa.gruul.search.service.properties.SearchConfigurationProperties;
import org.apache.dubbo.config.spring.context.annotation.EnableDubbo;
import org.dromara.easyes.starter.register.EsMapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;


/**
 * @author WuDi
 * date 2022/9/28
 */
@SpringBootApplication
@EnableDubbo(scanBasePackages = "com.medusa.gruul.search.service.service.rpc")
@EsMapperScan("com.medusa.gruul.search.service.es.mapper")
@EnableConfigurationProperties(SearchConfigurationProperties.class)
public class SearchServiceApplication implements I18NPropertiesLoader {

    public static void main(String[] args) {
        SpringApplication.run(SearchServiceApplication.class, args);
    }

    @Override
    public String path() {
        return "i18n/search";
    }
}

