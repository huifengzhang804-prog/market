package com.medusa.gruul.order.service.model.dto;


import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.util.List;

/**
 * 订单评价
 *
 * @author wudi
 */
@Getter
@Setter
@ToString
@Accessors(chain = true)
public class OrderEvaluateDTO {

    /**
     * 订单号
     */
    @NotBlank
    private String orderNo;

    /**
     * 包裹id
     */
    @NotNull
    private Long shopId;

    /**
     * 订单包含的商品
     */
    @NotNull
    @Valid
    @Size(min = 1, max = 1)
    private List<OrderEvaluateItemDTO> items;
}
