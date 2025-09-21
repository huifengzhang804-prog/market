package com.medusa.gruul.addon.ic.modules.opens.uupt.model.user;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * @author 张治保
 * @since 2024/8/12
 */
@Getter
@Setter
@ToString
public class AuthResp {

    /**
     * UU用户ID
     */
    private String openId;

    /**
     * 可用的UU服务编码，以英文逗号分割，如UUZC,UUJJS,UUTS，UUZC：专送服务，UUJJS：加急送服务，UUTS：团送服务
     */
    private String serviceCode;
}
