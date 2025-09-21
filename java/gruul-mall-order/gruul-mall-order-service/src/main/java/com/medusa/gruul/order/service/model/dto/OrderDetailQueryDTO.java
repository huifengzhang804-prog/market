package com.medusa.gruul.order.service.model.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.util.List;

/**
 * 订单详情查询条件
 *
 * @author 张治保
 * date 2022/9/2
 */
@Getter
@Setter
@ToString
@Accessors(chain = true)
public class OrderDetailQueryDTO {

    /**
     * 订单号
     */
    @NotBlank
    private String orderNo;

    /**
     * 店铺ID
     */
    private Long shopId;

    /**
     * 是否根据包裹查询数据
     */
    private Boolean usePackage;

    /**
     * 包裹id
     */
    private Long packageId;

    /**
     * 店铺订单itemIds
     */
    private List<Long> shopOrderItemIds;
}
