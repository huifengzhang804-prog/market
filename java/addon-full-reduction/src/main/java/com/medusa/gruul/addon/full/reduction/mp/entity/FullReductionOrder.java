package com.medusa.gruul.addon.full.reduction.mp.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.medusa.gruul.addon.full.reduction.model.enums.FullReductionOrderStatus;
import com.medusa.gruul.common.mp.model.BaseEntity;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

/**
 * 满减活动订单关联表
 *
 * @author 张治保
 * @since 2024/6/15
 */
@Getter
@Setter
@ToString
@Accessors(chain = true)
@TableName("t_full_reduction_order")
public class FullReductionOrder extends BaseEntity {

    /**
     * 店铺id
     */
    private Long shopId;

    /**
     * 满减活动id
     */
    private Long activityId;

    /**
     * 用户 id
     */
    private Long userId;

    /**
     * 订单号
     */
    private String orderNo;

    /**
     * 状态
     */
    @TableField("`status`")
    private FullReductionOrderStatus status;

    /**
     * 参与的用户数量
     */
    @TableField(exist = false)
    private Long user;

    /**
     * 支付订单数
     */
    @TableField(exist = false)
    private Long order;

}
