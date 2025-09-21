package com.medusa.gruul.addon.distribute.model.dto;

import com.medusa.gruul.overview.api.enums.DrawType;
import jakarta.validation.constraints.NotNull;
import java.io.Serializable;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * @author 张治保 date 2023/5/15
 */
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
    @NotNull
    private Long amount;
}
