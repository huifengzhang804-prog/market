package com.medusa.gruul.search.service.config;


import com.medusa.gruul.search.service.properties.SearchConfigurationProperties;
import jakarta.servlet.MultipartConfigElement;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.web.servlet.MultipartConfigFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.unit.DataSize;


/**
 * @author: wufuzhong
 * @Date: 2024/02/04 15:14:24
 * @Description：设置文件上传大小
 */
@Configuration
@RequiredArgsConstructor
public class MultipartConfig {

    private final SearchConfigurationProperties searchConfigurationProperties;

    @Bean
    public MultipartConfigElement multipartConfigElement() {
        MultipartConfigFactory factory = new MultipartConfigFactory();
        // 设置单个文件最大
        factory.setMaxFileSize(DataSize.ofMegabytes(searchConfigurationProperties.getMaxFileSize()));
        // 设置总上传数据总大小
        factory.setMaxRequestSize(DataSize.ofMegabytes(searchConfigurationProperties.getMaxRequestSize()));
        return factory.createMultipartConfig();
    }
}
