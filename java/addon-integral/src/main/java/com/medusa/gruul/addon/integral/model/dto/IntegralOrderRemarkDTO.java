package com.medusa.gruul.addon.integral.model.dto;

import com.medusa.gruul.global.model.constant.RegexPools;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.Set;

/**
 * 积分订单备注DTO
 *
 * @author xiaoq
 * @Description 积分订单备注DTO
 * @date 2023-01-31 15:39
 */
@Getter
@Setter
@ToString
public class IntegralOrderRemarkDTO {
    
    @NotNull
    @Size(min = 1)
    private Set<String> nos;

    /**
     * 备注
     */
    @NotNull
    @Pattern(regexp = RegexPools.NOT_BLANK)
    private String remark;
}
