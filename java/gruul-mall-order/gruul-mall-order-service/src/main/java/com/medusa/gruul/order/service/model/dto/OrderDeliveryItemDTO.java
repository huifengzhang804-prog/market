package com.medusa.gruul.order.service.model.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;


/**
 * @author 张治保
 * date 2022/7/27
 */
@Getter
@Setter
@ToString
public class OrderDeliveryItemDTO {
    /**
     * 店铺订单商品id
     */
    @NotNull
    private Long itemId;
    /**
     * 发货数量
     */
    @NotNull
    @Min(1)
    private Integer num;
}
