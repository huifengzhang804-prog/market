package com.medusa.gruul.service.uaa.service.service;

import cloud.tianai.captcha.spring.vo.CaptchaResponse;
import cloud.tianai.captcha.spring.vo.ImageCaptchaVO;
import com.medusa.gruul.common.system.model.model.ClientType;
import com.medusa.gruul.service.uaa.service.model.ResetPasswordModel;
import com.medusa.gruul.service.uaa.service.model.dto.ResetPasswordDTO;
import com.medusa.gruul.service.uaa.service.model.dto.captcha.SmsCaptcha;
import com.medusa.gruul.service.uaa.service.model.dto.captcha.SmsCodeOrCaptchaDTO;
import com.medusa.gruul.shop.api.model.vo.ShopInfoVO;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

import java.util.List;
import java.util.Set;

/**
 * @author 张治保
 * date 2022/3/19
 */
public interface SecureAuthService {


    /**
     * 生成滑块验证码
     *
     * @return 滑块验证码信息
     */
    SmsCodeOrCaptchaDTO captchaSlider(SmsCaptcha sms);

    CaptchaResponse<ImageCaptchaVO> captchaSlider();

    /**
     * 获取可切换的店铺列表
     *
     * @param clientType 客户端类型
     * @param shopId     店铺 id
     * @param userId     用户 id
     * @return 可切换的店铺列表
     */
    List<ShopInfoVO> getSwitchLoginShops(ClientType clientType, Long shopId, Long userId);

    /**
     * 获取有登录权限的店铺列表
     *
     * @param clientType 客户端类型
     * @param username   用户名
     * @return 店铺列表
     */
    List<ShopInfoVO> getLoginPermShops(ClientType clientType, String username);

    /**
     * 根据用户 id 集合获取有登录权限的店铺列表
     *
     * @param clientType 客户端类型
     * @param userIds    用户 id 集合
     * @return 店铺列表
     */
    List<ShopInfoVO> getLoginPermShops(ClientType clientType, Set<Long> userIds);

    /**
     * 发送 重置密码短信验证码
     *
     * @return 暂时返回 后续调整
     */
    String resetMyPasswordCaptchaSms();

    /**
     * 重置密码
     *
     * @param resetPassword 重置密码参数
     */
    void resetMyPassword(ResetPasswordModel resetPassword);

    /**
     * 发送 重置密码短信验证码
     *
     * @param mobile 手机号
     * @return 暂时返回 后续调整
     */
    String resetPasswordCaptchaSms(String mobile);

    /**
     * 重置密码
     *
     * @param resetPassword 重置密码参数
     */
    void resetPassword(ResetPasswordDTO resetPassword);

}
