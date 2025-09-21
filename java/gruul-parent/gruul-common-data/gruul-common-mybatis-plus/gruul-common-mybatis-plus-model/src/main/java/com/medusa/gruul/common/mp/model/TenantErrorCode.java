package com.medusa.gruul.common.mp.model;

/**
 * @author 张治保
 * date 2022/4/22
 */
public interface TenantErrorCode {
    /**
     * 店铺id不可用
     */
    int PLATFORM_ID_INVALID = 3001;

    /**
     * 平台服务商id不存在
     */
    int PLATFORM_ID_NOT_EXISTS = 3002;

    /**
     * 店铺id不可用
     */
    int SHOP_ID_INVALID = 3003;

    /**
     * 店铺id不存在
     */
    int SHOP_ID_NOT_EXISTS = 3004;
}
