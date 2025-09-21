package com.medusa.gruul.addon.ic.modules.uupt.model.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

/**
 * @author 张治保
 * @since 2024/8/23
 */
@Getter
@Setter
public class UuptConfigDTO {
    /**
     * app id
     */
    @NotBlank
    private String appid;

    /**
     * app key
     */
    @NotBlank
    private String appKey;

    /**
     * 开发者 openid 暂仅用于查询开放城市列表
     */
    @NotBlank
    private String openId;
}
