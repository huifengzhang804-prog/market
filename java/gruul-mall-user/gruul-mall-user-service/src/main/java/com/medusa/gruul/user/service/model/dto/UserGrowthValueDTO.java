package com.medusa.gruul.user.service.model.dto;

import com.medusa.gruul.common.model.enums.ChangeType;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;


@Getter
@Setter
@ToString
public class UserGrowthValueDTO {


    /**
     * 用户id
     */
    @NotNull
    private Long userId;

    /**
     * 变化值
     */
    @NotNull
    @Min(1)
    private Long growthValue;


    /**
     * 变化类型
     */
    @NotNull
    private ChangeType changeType;
}
