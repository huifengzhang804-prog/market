package com.medusa.gruul.service.uaa.service.model.dto.account;

import cn.hutool.core.util.DesensitizedUtil;
import com.medusa.gruul.common.fastjson2.annotation.Desensitize;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * 用户收藏 DTO
 *
 * @author Administrator
 * @since 2024/2/24
 */
@Getter
@Setter
@ToString
public class AccountDTO {
    private Long userId;
    /**
     * 用户名
     */
    @Desensitize(start = 3, end = 3)
    private String username;

    @Desensitize(type = DesensitizedUtil.DesensitizedType.PASSWORD)
    private String password;

    @Desensitize(type = DesensitizedUtil.DesensitizedType.PASSWORD)
    private String paymentPsw;

    private String newPsw;
    /**
     * 用户手机号
     * @Desensitize(type = DesensitizedUtil.DesensitizedType.MOBILE_PHONE)
     */
    private String phone;
    /**
     * 推荐码
     */
    private String referralCode;
    /**
     * 验证码
     */
    private String verificationCode;


}
