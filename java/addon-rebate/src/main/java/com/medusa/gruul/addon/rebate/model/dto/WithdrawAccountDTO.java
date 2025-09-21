package com.medusa.gruul.addon.rebate.model.dto;

import cn.hutool.json.JSONObject;
import com.medusa.gruul.overview.api.enums.DrawType;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;

/**
 * 用户提现账号
 */

@Getter
@Setter
@ToString
public class WithdrawAccountDTO implements Serializable {


    /**
     * 提现类型
     */
    @NotNull(message = "提现类型不能为空")
    private DrawType type;

    /**
     * 提现账号详情
     */
    @NotEmpty(message = "提现账号详情不能为空")
    private JSONObject detail;


}
