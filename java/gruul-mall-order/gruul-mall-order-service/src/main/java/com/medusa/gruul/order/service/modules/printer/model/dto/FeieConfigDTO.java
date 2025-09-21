package com.medusa.gruul.order.service.modules.printer.model.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * @author 张治保
 * @since 2024/8/22
 */
@Getter
@Setter
@ToString
public class FeieConfigDTO {

    /**
     * 飞鹅开放平台 用户 USER
     */
    @NotBlank
    private String user;

    /**
     * 飞鹅开放平台 用户KEY UKEY
     */
    @NotBlank
    private String ukey;
}
