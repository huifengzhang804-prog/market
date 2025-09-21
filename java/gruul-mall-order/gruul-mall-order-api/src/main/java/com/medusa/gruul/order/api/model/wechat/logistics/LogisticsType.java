package com.medusa.gruul.order.api.model.wechat.logistics;

import com.baomidou.mybatisplus.annotation.EnumValue;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * 微信小程序订单上传 物流模式
 *
 * @author wufuzhong
 * time 2024/01/08 15:11
 * desc 小程序订单上传 物流模式
 */
@Getter
@RequiredArgsConstructor
public enum LogisticsType {
    /**
     * 快递配送
     */
    EXPRESS(1),

    /**
     * 同城配送
     */
    INTRA_CITY_DISTRIBUTION(2),

    /**
     * 虚拟商品
     */
    VIRTUAL_PRODUCT(3),

    /**
     * 自提
     */
    SELF_DELIVERY(4);

    @EnumValue
    private final Integer value;
}
