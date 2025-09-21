package com.medusa.gruul.addon.coupon.model.enums;

import lombok.Getter;

/**
 * @author 张治保
 * date 2022/11/2
 */
@Getter
public enum QueryStatus {
    /**
     * 进行中
     */
    OPEN,

    /**
     * 未开始
     */
    NOT_OPEN,

    /**
     * 已关闭
     */
    CLOSED,

    /**
     * 违规下架-- 平台操作
     */
    BANED,
    /**
     * 下架-店铺操作
     */
    SHOP_BANED,



}
