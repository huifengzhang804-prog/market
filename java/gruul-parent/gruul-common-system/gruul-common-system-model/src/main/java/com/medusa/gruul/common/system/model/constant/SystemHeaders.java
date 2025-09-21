package com.medusa.gruul.common.system.model.constant;

/**
 * @author 张治保
 * date 2022/2/11
 */
public interface SystemHeaders {
    /**
     * 设备id
     */
    String DEVICE_ID = "Device-Id";
    /**
     * 客户端IP地址
     */
    String IP = "X-Real-IP";
    /**
     * 客户端类型
     */
    String CLIENT_TYPE = "Client-Type";

    /**
     * 请求版本
     */
    String VERSION = "Version";

    /**
     * 租户ID请求头
     */
    String PROVIDER_ID = "Provider-Id";

    /**
     * 店铺ID请求头
     */
    String SHOP_ID = "Shop-Id";

    /**
     * 平台
     */
    String PLATFORM = "Platform";


}
