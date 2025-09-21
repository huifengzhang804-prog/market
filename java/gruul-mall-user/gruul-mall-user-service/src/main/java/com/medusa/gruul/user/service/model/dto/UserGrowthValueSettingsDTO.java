package com.medusa.gruul.user.service.model.dto;

import jakarta.validation.Valid;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

@Getter
@Setter
@ToString
public class UserGrowthValueSettingsDTO {


    /**
     * id
     */
    private Long id;

    /**
     * 是否开启注册
     */
    private Boolean registerEnabled;

    /**
     * 注册成长值
     */
    private Long registerGrowthValue;

    /**
     * 消费成长值
     */
    private Boolean consumeEnabled;

    /**
     * 消费成长值Json
     */
    @Valid
    private List<ConsumeJsonDTO> consumeJson;
}
