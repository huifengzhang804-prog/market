package com.medusa.gruul.carrier.pigeon.api.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * 消息频道类型
 * channelId枚举
 *
 * @author 张治保
 * date 2021/4/28
 */
@Getter
@RequiredArgsConstructor
public enum Channel {
    /**
     * 新订单
     */
    NEW_ORDER(1),

    /**
     * 系统消息
     */
    SYSTEM(5),

    /**
     * 系统公告
     */
    NOTICE(6),

    /**
     * 客服服务消息
     */
    CUSTOMER_SERVICE(7),

    /**
     * 供应商-店铺
     */
    SUPPLIER_SHOP(8),

    /**
     * 供应商-店铺
     */
    PLATFORM_SHOP_AND_USER(9);

    @EnumValue
    private final Integer value;

}
