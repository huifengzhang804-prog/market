package com.medusa.gruul.addon.supplier.model.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;

/**
 * @author 张治保
 * date 2022/7/27
 */
@Getter
@Setter
@EqualsAndHashCode(of = "itemId")
@ToString
public class OrderDeliveryItemDTO implements Serializable {
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
