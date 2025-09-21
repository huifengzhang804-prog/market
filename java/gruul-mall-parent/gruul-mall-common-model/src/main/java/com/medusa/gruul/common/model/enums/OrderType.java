package com.medusa.gruul.common.model.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * 订单类型
 *
 * @author 张治保
 * date 2022/3/7
 */
@Getter
@RequiredArgsConstructor
public enum OrderType {

    /**
     * 商品订单
     */
    COMMON(1, "商品", false, false, false),

    /**
     * 秒杀
     */
    SPIKE(2, "秒杀", true, true, true),

    /**
     * 拼团
     */
    TEAM(3, "拼团", true, true, true),

    /**
     * 砍价
     */
    BARGAIN(4, "砍价", true, true, true),

    /**
     * 套餐价
     */
    PACKAGE(5, "套餐", false, true, false);

    @EnumValue
    private final Integer value;

    /**
     * 活动描述信息
     * todo 后期修改为 i18n的方式
     */
    private final String desc;

    /**
     * 是否只能单个商品
     */
    private final boolean onlySingleProduct;

    /**
     * 是否是活动类型
     */
    private final boolean activity;

    /**
     * 是否排他 与其他活动互斥
     */
    private final boolean exclusive;

}
