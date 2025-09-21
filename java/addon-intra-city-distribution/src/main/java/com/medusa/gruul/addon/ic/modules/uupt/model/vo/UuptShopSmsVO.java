package com.medusa.gruul.addon.ic.modules.uupt.model.vo;

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
public class UuptShopSmsVO {

    /**
     * 是否需要验证码
     * 如果需要使用base64Captcha渲染出验证码
     */
    private boolean needCaptcha;

    /**
     * base64 格式的验证码
     */
    private String base64Captcha;

}
