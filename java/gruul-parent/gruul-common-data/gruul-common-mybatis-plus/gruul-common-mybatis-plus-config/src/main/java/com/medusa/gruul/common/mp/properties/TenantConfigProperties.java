package com.medusa.gruul.common.mp.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.HashSet;
import java.util.Set;

/**
 * @author sombody
 */
@Data
@ConfigurationProperties(prefix = "gruul.tenant")
public class TenantConfigProperties {

    /**
     * 是否启用多服务商模式
     */
    private Boolean enableMultiProvider = Boolean.FALSE;
    /**
     * 是否使用多店铺模式
     */
    private Boolean enableMultiShop = Boolean.FALSE;
    /**
     * 维护服务商列名称
     */
    private String platformId = "platform_id";
    /**
     * 维护商铺列名称
     */
    private String shopId = "shop_id";
    /**
     * 忽略所有租户注入的表
     */
    private Set<String> ignoreAllTables = new HashSet<>();
    /**
     * 忽略tenantId注入的表
     */
    private Set<String> ignorePlatformIdTables = new HashSet<>();
    /**
     * 忽略shopId注入的表
     */
    private Set<String> ignoreShopIdTables = new HashSet<>();
}
