package com.medusa.gruul.order.api.entity;

import cn.hutool.core.collection.CollUtil;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.medusa.gruul.common.model.base.ShopProductSkuKey;
import com.medusa.gruul.common.mp.model.BaseEntity;
import com.medusa.gruul.order.api.enums.DiscountSourceStatus;
import com.medusa.gruul.order.api.enums.DiscountSourceType;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.io.Serial;
import java.util.List;
import java.util.Set;

/**
 * 订单优惠项
 *
 * @author 张治保
 * @since 2022-06-08
 */
@Getter
@Setter
@ToString
@Accessors(chain = true)
@TableName(value = "t_order_discount", autoResultMap = true)
public class OrderDiscount extends BaseEntity {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 订单号
     */
    private String orderNo;

    /**
     * 优惠类型
     */
    private DiscountSourceType sourceType;

    /**
     * 优惠生效状态
     */
    private DiscountSourceStatus sourceStatus;

    /**
     * 优惠源Id 比如使用的优惠券id
     */
    private Long sourceId;

    /**
     * 优惠金额
     */
    private Long sourceAmount;

    /**
     * 该优惠作用的所有商品总额
     */
    private Long totalAmount;

    /**
     * 优惠信息描述
     */
    private String sourceDesc;

    /**
     * 优惠项对应商品
     */
    @TableField(exist = false)
    private List<OrderDiscountItem> discountItems;

    /**
     * 店铺商品优惠金额 map
     * 优惠信息对应的商品id列表
     */
    @TableField(exist = false)
    private Set<Long> productIds;

    /**
     * 作用到的 sku key
     */
    @TableField(exist = false)
    private Set<ShopProductSkuKey> skuKeys = CollUtil.newHashSet();


}
