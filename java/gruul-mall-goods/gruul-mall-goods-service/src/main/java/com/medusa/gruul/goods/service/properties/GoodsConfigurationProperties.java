package com.medusa.gruul.goods.service.properties;


import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Properties配置
 *
 * @author xiaoq
 */

@Data
@ConfigurationProperties(prefix = "gruul.goods")
public class GoodsConfigurationProperties {

    /**
     * 一键采集 99 api key
     */
    private String copyApikey;
    /**
     * 是否缓存第三方的采集的数据
     */
    private boolean useCache = Boolean.FALSE;


}
