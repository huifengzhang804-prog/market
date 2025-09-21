package com.medusa.gruul.addon.ic.modules.opens.uupt;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

/**
 * @author 张治保
 * @since 2024/8/10
 */
@Getter
@Setter
@ToString
@Accessors(chain = true)
public class UuptConfig {

    private static final String DOMAIN = "https://api-open.uupt.com";
    private static final String DOMAIN_TEST = "http://api-open.test.uupt.com";

    /**
     * 是否是沙箱环境
     */
    private boolean test;

    /**
     * app id
     */
    private String appid;

    /**
     * app key
     */
    private String appKey;

    /**
     * 开发者 openid 暂仅用于查询开放城市列表
     */
    private String openId;

    public final String urlPrefix() {
        return (isTest() ? DOMAIN_TEST : DOMAIN) + "/openapi/v3";
    }

}
