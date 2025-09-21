package com.medusa.gruul.addon.integral.model.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;


/**
 * 积分设置dto
 *
 * @author shishuqian
 * date 2023/2/9
 * time 11:21
 **/

@Getter
@Setter
@ToString
public class IntegralSettingDTO {

    @NotNull
    private Long ratio;

    @NotNull
    private Long ceiling;
}
