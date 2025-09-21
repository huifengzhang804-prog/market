package com.medusa.gruul.user.service.mp.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.medusa.gruul.common.model.enums.PayType;
import com.medusa.gruul.common.mp.model.BaseEntity;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;

/**
 * 用户会员交易明细
 *
 * @author xiaoq
 * @Description UserDealDetail.java
 * @date 2022-11-28 16:05
 */
@Getter
@Setter
@Accessors(chain = true)
@TableName(value = "t_user_deal_detail", autoResultMap = true)
public class UserDealDetail extends BaseEntity {

    /**
     * 用户id
     */
    private Long userId;

    /**
     * 店铺id
     */
    private Long shopId;


    /**
     * 订单编号
     */
    private String orderNo;


    /**
     * 支付类型
     */
    private PayType payType;

    /**
     * 商品金额
     */
    private Long goodsAmount;


    /**
     * 总运费
     */
    private Long freightAmount;

    /**
     * 优惠金额
     */
    private Long discountAmount;

    /**
     * 实付金额
     */
    private Long payAmount;

    /**
     * 交易时间
     */
    private LocalDateTime dealTime;
}
