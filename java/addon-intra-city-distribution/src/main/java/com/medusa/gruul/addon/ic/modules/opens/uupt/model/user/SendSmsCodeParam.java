package com.medusa.gruul.addon.ic.modules.opens.uupt.model.user;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

/**
 * @author 张治保
 * @since 2024/8/10
 */
@Getter
@Setter
@ToString
@Accessors(chain = true)
public class SendSmsCodeParam {

    /**
     * 是 用户手机号
     */
    private String userMobile;

    /**
     * 是 用户IP
     */
    private String userIp;

    /**
     * 仅需要是填入
     * 图片验证码（接口返回错误码88100106,返回base64图片,提交图片上数字验证码）
     */
    private String imageCode;

}
