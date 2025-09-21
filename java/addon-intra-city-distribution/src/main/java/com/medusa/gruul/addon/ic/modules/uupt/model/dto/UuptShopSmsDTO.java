package com.medusa.gruul.addon.ic.modules.uupt.model.dto;

import cn.hutool.core.lang.RegexPool;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

/**
 * @author 张治保
 * @since 2024/8/23
 */
@Setter
@Getter
@Accessors(chain = true)
public class UuptShopSmsDTO {

    /**
     * 手机号
     */
    @NotBlank
    @Pattern(regexp = RegexPool.MOBILE)
    private String mobile;

    /**
     * 用户输入的验证码
     */
    private String captcha;

}
