package com.medusa.gruul.addon.ic.modules.uupt.model.dto;

import cn.hutool.core.lang.RegexPool;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

/**
 * @author 张治保
 * @since 2024/8/23
 */
@Getter
@Setter
@ToString
@Accessors(chain = true)
public class UuptShopAuthDTO {

    /**
     * 手机号
     */
    @NotBlank
    @Pattern(regexp = RegexPool.MOBILE)
    private String mobile;

    /**
     * 短信验证码
     */
    @NotBlank
    private String smsCode;

}
