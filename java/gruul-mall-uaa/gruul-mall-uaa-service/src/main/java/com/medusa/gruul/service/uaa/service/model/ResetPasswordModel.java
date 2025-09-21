package com.medusa.gruul.service.uaa.service.model;

import cn.hutool.core.lang.RegexPool;
import com.medusa.gruul.global.model.constant.RegexPools;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;
import org.hibernate.validator.constraints.Length;


/**
 * @author 张治保
 * date 2022/10/28
 */
@Getter
@Setter
@ToString
@Accessors(chain = true)
public class ResetPasswordModel {

    /**
     * 验证码
     */
    @NotNull
    @Length(min = 4, max = 4)
    @Pattern(regexp = RegexPool.NUMBERS)
    private String code;

    /**
     * 密码
     * 密码包含数字、小写字母、大写字母 至少两种 6-20位
     */
    @NotBlank
    @Pattern(regexp = RegexPools.PASSWORD)
    private String password;

    /**
     * 密码
     * 密码包含数字、小写字母、大写字母 至少两种 6-20位
     */
    @NotBlank
    @Pattern(regexp = RegexPools.PASSWORD)
    private String confirmPassword;
}
