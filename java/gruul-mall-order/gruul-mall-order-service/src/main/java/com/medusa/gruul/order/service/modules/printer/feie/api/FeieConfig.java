package com.medusa.gruul.order.service.modules.printer.feie.api;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

/**
 * @author 张治保
 * @since 2024/8/14
 */
@Getter
@Setter
@ToString
@Accessors(chain = true)
public class FeieConfig {

    private static final String DOMAIN = "https://api.feieyun.cn/Api/Open/";

    /**
     * 是否时测试环境
     */
    private boolean isTest;

    /**
     * 飞鹅开发者账号名
     */
    private String user;

    /**
     * 飞鹅云后台注册账号后生成的UKEY
     */
    private String ukey;

    public final String domain() {
        return DOMAIN;
    }

}
