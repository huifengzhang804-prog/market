package com.medusa.gruul.service.uaa.service.model.dto;

import cn.hutool.core.lang.RegexPool;
import com.medusa.gruul.service.uaa.service.model.ResetPasswordModel;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;


/**
 * @author 张治保
 * date 2022/10/26
 */
@Getter
@Setter
@ToString
@Accessors(chain = true)
public class ResetPasswordDTO extends ResetPasswordModel {

    /**
     * 手机号
     */
    @NotBlank
    @Pattern(regexp = RegexPool.MOBILE)
    private String mobile;


}
