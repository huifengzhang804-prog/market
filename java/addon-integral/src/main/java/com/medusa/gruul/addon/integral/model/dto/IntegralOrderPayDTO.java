package com.medusa.gruul.addon.integral.model.dto;

import com.medusa.gruul.common.model.enums.PayType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;


/**
 * 积分订单支付dto
 *
 * @author shishuqian
 * date 2023/2/2
 * time 17:45
 **/
@Getter
@Setter
@ToString
public class IntegralOrderPayDTO {


    /**
     * 订单号
     */
    @NotBlank
    private String integralOrderNo;


    /**
     * 支付类型
     */
    @NotNull
    private PayType payType;

}
