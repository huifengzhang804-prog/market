package com.medusa.gruul.order.service.model.dto;

import com.medusa.gruul.common.model.enums.PayType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;


/**
 * @author 张治保
 * date 2022/8/1
 */
@Getter
@Setter
@ToString
public class OrderPayDTO {

    /**
     * 订单号
     */
    @NotBlank
    private String orderNo;
    /**
     * 支付类型
     */
    @NotNull
    private PayType payType;

    /**
     * 是否使用消费返利
     */
    private Boolean rebate = Boolean.FALSE;
}
