package com.medusa.gruul.addon.rebate.mp.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.medusa.gruul.addon.rebate.model.bo.RebateItemPay;
import com.medusa.gruul.common.model.base.ShopProductSkuKey;
import com.medusa.gruul.common.mp.handler.type.MapToKeyValuesTypeHandler;
import com.medusa.gruul.common.mp.model.BaseEntity;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.io.Serial;
import java.util.Map;

/**
 * <p>
 * 消费返利支付
 * </p>
 *
 * @author WuDi
 * @since 2023-07-18
 */
@Getter
@Setter
@Accessors(chain = true)
@TableName(value = "t_rebate_payment", autoResultMap = true)
public class RebatePayment extends BaseEntity {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 用户id
     */
    private Long userId;

    /**
     * 订单号
     */
    private String orderNo;

    /**
     * 支付金额
     */
    private Long payAmount;

    /**
     * 返利支付使用记录
     */
    @TableField(typeHandler = MapToKeyValuesTypeHandler.class)
    private Map<ShopProductSkuKey, RebateItemPay> rebateItemPays;


}
