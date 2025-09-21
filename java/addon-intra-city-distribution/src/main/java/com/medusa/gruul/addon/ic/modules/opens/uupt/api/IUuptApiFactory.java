package com.medusa.gruul.addon.ic.modules.opens.uupt.api;

import com.medusa.gruul.addon.ic.modules.opens.uupt.UuptConfig;

/**
 * @author 张治保
 * @since 2024/8/10
 */
public interface IUuptApiFactory {

    /**
     * 获取uupt 配置
     *
     * @return uupt 配置
     */
    UuptConfig config();

    /**
     * 获取uupt 订单 api
     *
     * @return uupt 订单 api
     */
    default IOrderApi order() {
        return this::config;
    }

    /**
     * uupt 用户 api
     *
     * @return 用户 api
     */
    default IUserApi user() {
        return this::config;
    }
}
