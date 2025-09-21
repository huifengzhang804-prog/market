package com.medusa.gruul.addon.integral.model.dto;

import com.medusa.gruul.addon.integral.model.constant.IntegralConstant;
import com.medusa.gruul.addon.integral.model.enums.IntegralOrderSourceType;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;


/**
 * 积分订单
 *
 * @author xiaoq
 * @Description 积分订单
 * @date 2023-01-31 13:31
 */
@Getter
@Setter
@ToString
@Accessors(chain = true)
public class IntegralOrderDTO {

    private Long id;

    /**
     * 积分订单来源
     */
    @NotNull
    private IntegralOrderSourceType source;


    /**
     * 收货地址详情
     */
    private IntegralReceiverDTO receiver;


    /**
     * 买家备注
     */
    @NotNull
    private String buyerRemark;


    /**
     * 积分商品id
     */
    @NotNull
    private Long productId;

    /**
     * 购买数量  最多只能购买一件，固定数量
     */
    @Min(1)
    @Max(1)
    private Integer num = IntegralConstant.INTEGRAL_PRODUCT_MAX_BUY_NUM;

}
