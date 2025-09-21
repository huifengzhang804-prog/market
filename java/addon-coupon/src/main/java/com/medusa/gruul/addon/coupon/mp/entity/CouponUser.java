package com.medusa.gruul.addon.coupon.mp.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.handlers.Fastjson2TypeHandler;
import com.medusa.gruul.addon.coupon.model.BaseCouponModel;
import com.medusa.gruul.addon.coupon.model.enums.CouponCollectionType;
import com.medusa.gruul.addon.coupon.model.enums.CouponStatus;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.util.Set;

/**
 * 用户优惠券关联表 领取时的优惠券快照
 *
 * @author 张治保
 * @since 2022-11-02
 */
@Getter
@Setter
@Accessors(chain = true)
@TableName(value = "t_coupon_user", autoResultMap = true)
public class CouponUser extends BaseCouponModel {

    /**
     * 用户id
     */
    private Long userId;

    /**
     * 优惠券id 只用于优惠券统计
     */
    private Long couponId;

    /**
     * 是否已使用
     */
    private Boolean used;

    /**
     * 可以使用的商品id 列表
     */
    @TableField(typeHandler = Fastjson2TypeHandler.class)
    private Set<Long> productIds;

    /**
     * 优惠券用户手机号
     */
    private String userPhone;

    /**
     * 赠券用户id
     */
    private Long giftUserId;

    /**
     * 赠券用户手机号
     */
    private String giftUserPhone;

    /**
     * 领券方式
     */
    private CouponCollectionType collectType;


    /**
     * 关联使用优惠券的订单编号
     */
    private String associatedOrderNo;

    /**
     * 关联优惠券可使用或已使用的店铺(店铺券在领券时赋值, 平台券在用券后赋值)
     */
    @TableField(value = "associated_shop_ids", typeHandler = Fastjson2TypeHandler.class)
    private Set<Long> associatedShopIds;


}
