package com.medusa.gruul.service.uaa.service.model.dto.captcha;

import cn.hutool.core.lang.RegexPool;
import com.medusa.gruul.common.encrypt.Crypt;
import com.medusa.gruul.service.uaa.api.enums.SmsType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;

/**
 * @author 张治保
 * @since 2024/5/25
 */
@Getter
@Setter
public class SmsCaptcha {

    /**
     * 手机号
     */
    @Crypt
    @NotBlank
    @Pattern(regexp = RegexPool.MOBILE)
    private String mobile;
    /**
     * 短信类型
     */
    private SmsType smsType;

}
