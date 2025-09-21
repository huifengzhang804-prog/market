package com.medusa.gruul.shop.service.properties;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

/**
 * todo 后期优化 使用 properties 方式处理 这个类舍弃
 *
 * @author wufuzhong
 * @Date 2023 2023/12/16 14:14
 * @Description 高德 config
 */

@Configuration
@Data
public class GdConfig {

    /**
     * 高德 key
     */
    @Value("${spring.gd.key:''}")
    private String key;

    /**
     * 高德 ip定位 url
     */
    @Value("${spring.gd.url:''}")
    private String url;

    @Value("${spring.gd.ip:''}")
    private String ip;
}
