package com.medusa.gruul.addon.distribute.model.dto;

import cn.hutool.core.lang.RegexPool;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.validator.constraints.Length;


/**
 * @author 张治保
 * date 2022/11/17
 */
@Getter
@Setter
@ToString
public class ApplyAffairsDTO {

    /**
     * 姓名
     */
    @NotBlank
    private String name;

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
    @Length(min = 4, max = 4)
    private String code;
}
