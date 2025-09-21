package com.medusa.gruul.order.api.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.medusa.gruul.common.mp.model.BaseEntity;
import com.medusa.gruul.order.api.enums.DiscountSourceType;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

/**
 * 订单优惠项关联店铺商品详情
 *
 * @author 张治保
 * @since 2022-06-08
 */
@Getter
@Setter
@Accessors(chain = true)
@TableName(value = "t_order_discount_item", autoResultMap = true)
public class OrderDiscountItem extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * 优惠项id
     */
    private Long discountId;

    /**
     * 店铺id
     */
    private Long shopId;


    /**
     * 订单商品项ID
     */
    private Long itemId;

    /**
     * 在该优惠活动中 该商品项 总共优惠了多少钱
     */
    private Long discountAmount;

    /**
     * 优惠类型
     */
    @TableField(exist = false)
    private DiscountSourceType sourceType;


}
