package com.medusa.gruul.addon.store.model.dto;

import cn.hutool.core.lang.RegexPool;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;


/**
 * 店铺店员DTO
 *
 * @author xiaoq
 * @Description 店铺店员DTO
 * @date 2023-03-14 14:40
 */
@Getter
@Setter
@ToString
@Accessors(chain = true)
public class ShopAssistantDTO {

    /**
     * 店员手机号
     */
    @NotBlank
    @Pattern(regexp = RegexPool.MOBILE)
    private String assistantPhone;


    /**
     * 店员手机号验证码
     */
    @NotBlank
    private String assistantPhoneCode;
}
