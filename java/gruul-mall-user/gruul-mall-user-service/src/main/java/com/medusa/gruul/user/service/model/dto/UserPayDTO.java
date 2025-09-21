package com.medusa.gruul.user.service.model.dto;

import com.medusa.gruul.common.model.enums.PayType;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;


/**
 * 用户储值
 *
 * @author xiaoq
 * @Description UserPayDTO.java
 * @date 2022-09-05 09:40
 */

@Getter
@Setter
@ToString
@Accessors(chain = true)
public class UserPayDTO {
    /**
     * 支付渠道
     */
    @NotNull
    private PayType payType;

    /**
     * 支付金额
     */
    @NotNull
    private Long payAmount;

    /**
     * 储值规则id
     */
    private Long ruleId;
}
