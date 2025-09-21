package com.medusa.gruul.addon.bargain.model.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * 活动状态
 * @author wudi
 */
@RequiredArgsConstructor
@Getter
public enum ActivityStatus {


    /**
     * 未开始
     */
    NOT_STARTED(0, "未开始"),


    /**
     * 进行中
     */
    PROCESSING(1, "进行中"),

    /**
     * 已结束
     */
    OVER(2, "已结束"),

    /**
     * 违规下架
     */
    ILLEGAL_SELL_OFF(3, "违规下架"),
    /**
     * 店铺下架
     */
    SHOP_SELL_OFF(4,"下架"),
    ;

    @EnumValue
    private final Integer value;

    private final String name;
}
