package com.medusa.gruul.addon.rebate.model.dto;

import com.medusa.gruul.common.web.valid.annotation.Price;
import com.medusa.gruul.overview.api.enums.DrawType;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;

@Getter
@Setter
@ToString
public class WithdrawDTO implements Serializable {

    /**
     * 提现类型
     */
    @NotNull
    private DrawType type;

    /**
     * 提现金额
     */

    @Price
    private Long amount;
}
