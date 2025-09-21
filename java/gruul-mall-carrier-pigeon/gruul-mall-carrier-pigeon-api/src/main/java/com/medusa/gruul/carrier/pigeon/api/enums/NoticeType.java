package com.medusa.gruul.carrier.pigeon.api.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * @author 张治保
 * date 2022/4/26
 */
@Getter
@RequiredArgsConstructor
public enum NoticeType {

    /**
     * 公告
     */
    ANNOUNCEMENT(0,"/#/message/notice/{}"),
    /**
     * 提现工单审核通过
     */
    CASH_PASS(1,""),
    /**
     * 提现工单未通过审核
     */
    CASH_FAIL(2,""),
    /**
     * 商品发布通过审核
     */
    PRODUCT_PASS(3,""),
    /**
     * 商品发布未通过审核
     */
    PRODUCT_FAIL(4,""),
    /**
     * 优惠券下架
     */
    COUPON_FORBIDDEN(5,""),
    /**
     * 新订单
     */
    NEW_ORDER(6,""),
    /**
     * 通知
     */
    NOTIFICATION(7,""),
    /**
     * 其他消息
     */
    OTHER(8,"");
    /**
     * 数据库value
     */
    @EnumValue
    private final Integer value;
    /**
     * url格式
     */
    private final String urlFormat;
}
