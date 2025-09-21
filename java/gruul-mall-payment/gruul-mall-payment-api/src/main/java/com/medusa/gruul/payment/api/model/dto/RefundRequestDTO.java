package com.medusa.gruul.payment.api.model.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.io.Serial;
import java.io.Serializable;

/**
 * 退款请求DTO
 *
 * @author xiaoq
 * @Description RefundRequestDTO.java
 * @date 2022-08-08 11:23
 */
@Data
@NoArgsConstructor
@Accessors(chain = true)
public class RefundRequestDTO implements Serializable {
    @Serial
    private static final long serialVersionUID = 995990252956463721L;

    /**
     * 业务订单唯一标识
     */
    @NotNull
    private String orderNum;

    /**
     * 店铺id
     */
    @NotNull
    private Long shopId;

    /**
     * 业务订单售后唯一标识
     */
    private String afsNum;

    /**
     * 交易流水号
     */
    private String transactionId;


    /**
     * rabbitmq路由key 支付成功回调 为空不进行回调
     */
    private String routeKey;

    /**
     * rabbitmq交换机
     */
    private String exchange;

    /**
     * 退款金额
     */
    @NotNull
    private Long refundFee;

    /**
     * 退款原因描述
     */
    private String desc;


}
