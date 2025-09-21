package com.medusa.gruul.service.uaa.service.controller;

import cloud.tianai.captcha.spring.annotation.Captcha;
import cloud.tianai.captcha.spring.vo.CaptchaResponse;
import cloud.tianai.captcha.spring.vo.ImageCaptchaVO;
import cn.hutool.core.lang.RegexPool;
import com.medusa.gruul.common.idem.annotation.Idem;
import com.medusa.gruul.common.log.annotation.Log;
import com.medusa.gruul.common.model.resp.Result;
import com.medusa.gruul.common.security.model.bean.SecureUser;
import com.medusa.gruul.common.security.resource.helper.ISecurity;
import com.medusa.gruul.common.system.model.ISystem;
import com.medusa.gruul.common.system.model.model.ClientType;
import com.medusa.gruul.service.uaa.api.enums.SmsType;
import com.medusa.gruul.service.uaa.service.model.ResetPasswordModel;
import com.medusa.gruul.service.uaa.service.model.dto.ResetPasswordDTO;
import com.medusa.gruul.service.uaa.service.model.dto.captcha.CaptchaRequestDTO;
import com.medusa.gruul.service.uaa.service.model.dto.captcha.SmsCaptcha;
import com.medusa.gruul.service.uaa.service.model.dto.captcha.SmsCodeOrCaptchaDTO;
import com.medusa.gruul.service.uaa.service.service.SecureAuthService;
import com.medusa.gruul.service.uaa.service.service.SmsService;
import com.medusa.gruul.shop.api.model.vo.ShopInfoVO;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Pattern;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 认证控制器
 *
 * @author 张治保
 */
@RestController
@RequestMapping("/uaa/auth")
@RequiredArgsConstructor
public class AuthenticationController {

    private final SmsService smsService;
    private final SecureAuthService secureAuthService;


    /**
     * 获取可切换登录的店铺列表
     */
    @Log("获取可切换登录的店铺列表")
    @GetMapping("/switch")
    @PreAuthorize("@S.matcher().any(@S.ROLES,@S.SHOP_ADMIN,@S.SHOP_CUSTOM_ADMIN,@S.R.SUPPLIER_ADMIN,@S.R.SUPPLIER_CUSTOM_ADMIN).match()")
    public Result<List<ShopInfoVO>> getSwitchLoginShops() {
        SecureUser<?> secureUser = ISecurity.userMust();
        ClientType clientType = secureUser.getRoles().iterator().next().getClientType();
        return Result.ok(
                this.secureAuthService.getSwitchLoginShops(clientType, secureUser.getShopId(), secureUser.getId())
        );
    }

    /**
     * 获取有登录权限的店铺列表
     *
     * @param username 用户名
     * @return 店铺列表
     */
    @Log("获取有登录权限的店铺列表")
    @GetMapping("/shop/{username}")
    public Result<List<ShopInfoVO>> getLoginPermShops(@PathVariable String username) {
        return Result.ok(
                this.secureAuthService.getLoginPermShops(ISystem.clientTypeMust(), username)
        );
    }


    /**
     * 发送短信验证码
     *
     * @param request 发送短信验证码参数
     * @return 验证码
     */
    @Log("发送短信验证码")
    @Captcha
    @PostMapping("/captcha/sms")
    public Result<String> captchaSms(@RequestBody @Valid CaptchaRequestDTO<SmsCaptcha> request) {
        return Result.ok(this.smsService.sendSmsCode(SmsType.LOGIN, request.getForm().getMobile()));
    }


    /**
     * 发送当前用户的 重置密码短信验证码
     */
    @Log("发送 重置密码 短信验证码")
    @Idem
    @GetMapping("/reset/my/password/sms")
    public Result<String> resetMyPasswordCaptchaSms() {
        return Result.ok(
                this.secureAuthService.resetMyPasswordCaptchaSms()
        );
    }

    /**
     * 重置当前用户密码
     *
     * @param resetPassword 重置密码参数
     */
    @Log("重置密码")
    @Idem
    @PutMapping("/reset/my/password")
    public Result<Void> resetMyPassword(@RequestBody @Valid ResetPasswordModel resetPassword) {
        this.secureAuthService.resetMyPassword(resetPassword);
        return Result.ok();
    }

    /**
     * 发送 重置密码 短信验证码
     *
     * @param mobile 手机号
     * @return 验证码
     */
    @Log("发送 重置密码 短信验证码")
    @Idem
    @GetMapping("/reset/{mobile}/password/sms")
    public Result<String> resetPasswordCaptchaSms(@PathVariable @Pattern(regexp = RegexPool.MOBILE) String mobile) {
        return Result.ok(
                this.secureAuthService.resetPasswordCaptchaSms(mobile)
        );
    }

    /**
     * 重置密码
     *
     * @param resetPassword 重置密码参数
     */
    @Log("重置密码")
    @Idem
    @PutMapping("/reset/password")
    public Result<Void> resetPassword(@RequestBody @Valid ResetPasswordDTO resetPassword) {
        this.secureAuthService.resetPassword(resetPassword);
        return Result.ok();
    }


    /**
     * 生成滑块验证码或者直接发短信
     *
     * @return 滑块验证码信息 或者短信信息
     */
    @PostMapping("/captcha/slider/new")
    @PreAuthorize("permitAll()")
    public Result<SmsCodeOrCaptchaDTO> captchaSlider(@RequestBody @Valid SmsCaptcha sms) {
        return Result.ok(
                this.secureAuthService.captchaSlider(sms)
        );
    }

    /**
     * 生成滑块验证码
     *
     * @return 滑块验证码信息
     */
    @GetMapping("/captcha/slider")
    @PreAuthorize("permitAll()")
    public Result<CaptchaResponse<ImageCaptchaVO>> captchaSlider() {
        return Result.ok(
                this.secureAuthService.captchaSlider()
        );
    }




    /**
     * 根据类型发送短信验证码
     *
     * @param request 发送短信验证码参数
     * @param smsType 短信类型
     * @return 验证码
     */
    @Log("发送短信验证码")
    @Captcha
    @PostMapping("/captcha/sms/{smsType}")
    public Result<String> captchaSmsType(@RequestBody @Valid CaptchaRequestDTO<String> request, @PathVariable SmsType smsType) {
        return Result.ok(this.smsService.sendSmsCode(smsType, request.getForm()));
    }

}