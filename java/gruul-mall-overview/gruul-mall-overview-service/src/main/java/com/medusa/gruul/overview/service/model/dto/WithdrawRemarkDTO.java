package com.medusa.gruul.overview.service.model.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.Set;

/**
 * @author 张治保
 * date 2022/12/1
 */
@Getter
@Setter
@ToString
public class WithdrawRemarkDTO {

    /**
     * 提现工单号
     */
    @NotNull
    @Size(min = 1)
    private Set<String> nos;

    /**
     * 备注内容
     */
    @NotBlank
    private String remark;
}
