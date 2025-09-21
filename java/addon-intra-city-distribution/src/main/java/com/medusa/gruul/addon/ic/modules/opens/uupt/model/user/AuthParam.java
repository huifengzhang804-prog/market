package com.medusa.gruul.addon.ic.modules.opens.uupt.model.user;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

/**
 * @author 张治保
 * @since 2024/8/12
 */
@Getter
@Setter
@Accessors(chain = true)
public class AuthParam {
    /**
     * 是 用户手机号
     */
    private String userMobile;

    /**
     * 是 用户IP
     */
    private String userIp;

    /**
     * 是 短信验证码
     */
    private String smsCode;

    /**
     * 是 城市名（需要带市，如郑州市）
     */
    private String cityName;

    /**
     * 否 区县名
     */
    private String countyName;

}
