package com.medusa.gruul.payment.api.model.dto;

import com.medusa.gruul.global.model.constant.RegexPools;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.Set;

/**
 * 储值订单备注DTO
 *
 * @author xiaoq
 * @Description SavingsOrderRemarkDTO.JAVA
 * @date 2022-12-22 18:40
 */
@Getter
@Setter
@ToString
public class SavingsOrderRemarkDTO {
    /**
     * ids
     */
    @NotNull
    @Size(min = 1)
    private Set<Long> ids;


    /**
     * 备注
     */
    @NotNull
    @Pattern(regexp = RegexPools.NOT_BLANK)
    private String remark;


}
