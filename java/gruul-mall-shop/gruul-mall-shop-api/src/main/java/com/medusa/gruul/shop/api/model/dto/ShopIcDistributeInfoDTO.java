package com.medusa.gruul.shop.api.model.dto;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

@Data
public class ShopIcDistributeInfoDTO implements Serializable {
    /**
     * 店铺id
     */
    private Long shopId;

    /**
     * 起送金额
     */
    private Long minLimit;


    private String showMsg;
    /**
     * 距离
     */
    private BigDecimal distance;
    /**
     * 是否在配送范围
     */
    private Boolean InDeliveryRange;


}
