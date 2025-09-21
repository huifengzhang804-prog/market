package com.medusa.gruul.carrier.pigeon.api.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * 消息接收方类型
 * @author 张治保
 * date 2022/10/12
 */
@Getter
@RequiredArgsConstructor
public enum UserType {
    /**
     * 消费者
     */
    CONSUMER(1),

    /**
     * 店铺管理员
     */
    SHOP_ADMIN(2),

    /**
     * 平台管理员
     */
    PLATFORM_ADMIN(3),

    /**
     * 供应商管理员
     */
    SUPPLIER_ADMIN(4);


    @EnumValue
    private final Integer value;





}
