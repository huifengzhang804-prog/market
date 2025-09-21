package com.medusa.gruul.order.service.model.dto;

import com.medusa.gruul.order.api.enums.SelfShopTypeEnum;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;


/**
 * @author 张治保
 * date 2022/8/8
 */
@Getter
@Setter
@ToString
public class OrdersDeliveryDTO {

    /**
     * 店铺id 供应商发货比传
     */
    private Long shopId;

    /**
     * 订单号
     */
    @NotBlank
    private String orderNo;

    /**
     * 自营店铺类型
     */
    private SelfShopTypeEnum selfShopType;

    /**
     * 发货信息
     */
    @Valid
    @NotNull
    private OrderDeliveryDTO orderDelivery;
}
