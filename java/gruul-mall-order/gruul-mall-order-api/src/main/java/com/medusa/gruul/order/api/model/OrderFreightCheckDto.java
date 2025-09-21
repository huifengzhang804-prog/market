package com.medusa.gruul.order.api.model;

import lombok.Data;

/**
 * 订单签收确认DTO
 */
@Data
public class OrderFreightCheckDto {
    private String orderNo;
    /**
     * 签收时间戳 秒
     */
    private Long ts;
}
