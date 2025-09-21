package com.medusa.gruul.addon.matching.treasure.model.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import lombok.Getter;
import lombok.RequiredArgsConstructor;


@RequiredArgsConstructor
@Getter
public enum SetMealStatus {

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
     * (已下架)商家下架
     */
    MERCHANT_SELL_OFF(4, "已下架");


    @EnumValue
    private final Integer value;

    private final String name;

}
