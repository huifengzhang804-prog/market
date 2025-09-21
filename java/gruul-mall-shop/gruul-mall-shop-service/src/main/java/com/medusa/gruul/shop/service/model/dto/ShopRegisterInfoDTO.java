package com.medusa.gruul.shop.service.model.dto;

import cn.hutool.core.lang.RegexPool;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;


/**
 * @author 张治保
 * date 2022/4/15
 */
@Getter
@Setter
@ToString
public class ShopRegisterInfoDTO {

    /**
     * 营业执照
     */
    @Pattern(regexp = RegexPool.URL_HTTP)
    private String license;

    /**
     * 法人身份证 正面
     */
    @Pattern(regexp = RegexPool.URL_HTTP)
    private String legalPersonIdFront;

    /**
     * 法人身份证 背面
     */
    @Pattern(regexp = RegexPool.URL_HTTP)
    private String legalPersonIdBack;
}
