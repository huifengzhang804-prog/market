package com.medusa.gruul.service.uaa.service.service.impl;

import cloud.tianai.captcha.spring.application.ImageCaptchaApplication;
import cloud.tianai.captcha.spring.vo.CaptchaResponse;
import cloud.tianai.captcha.spring.vo.ImageCaptchaVO;
import cn.hutool.core.collection.CollUtil;
import com.medusa.gruul.common.model.resp.SystemCode;
import com.medusa.gruul.common.module.app.shop.ShopStatus;
import com.medusa.gruul.common.mp.model.TenantShop;
import com.medusa.gruul.common.redis.util.RedisUtil;
import com.medusa.gruul.common.security.model.enums.SecureCodes;
import com.medusa.gruul.common.security.resource.helper.ISecurity;
import com.medusa.gruul.common.system.model.model.ClientType;
import com.medusa.gruul.global.model.constant.SecurityConst;
import com.medusa.gruul.service.uaa.api.enums.SmsType;
import com.medusa.gruul.service.uaa.service.model.ResetPasswordModel;
import com.medusa.gruul.service.uaa.service.model.dto.ResetPasswordDTO;
import com.medusa.gruul.service.uaa.service.model.dto.captcha.SmsCaptcha;
import com.medusa.gruul.service.uaa.service.model.dto.captcha.SmsCodeOrCaptchaDTO;
import com.medusa.gruul.service.uaa.service.model.enums.UaaError;
import com.medusa.gruul.service.uaa.service.mp.entity.User;
import com.medusa.gruul.service.uaa.service.mp.entity.UserRole;
import com.medusa.gruul.service.uaa.service.mp.service.IUserRoleService;
import com.medusa.gruul.service.uaa.service.mp.service.IUserService;
import com.medusa.gruul.service.uaa.service.service.SecureAuthService;
import com.medusa.gruul.service.uaa.service.service.SmsService;
import com.medusa.gruul.shop.api.model.vo.ShopInfoVO;
import com.medusa.gruul.shop.api.rpc.ShopRpcService;
import io.vavr.control.Option;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author 张治保
 * date 2022/3/19
 */
@Service
@RequiredArgsConstructor
public class SecureAuthServiceImpl implements SecureAuthService {

    private final IUserService userService;
    private final SmsService smsService;
    private final PasswordEncoder passwordEncoder;
    private final IUserRoleService userRoleService;
    private final ShopRpcService shopRpcService;
    private final ImageCaptchaApplication imageCaptchaApplication;

    @Override
    public SmsCodeOrCaptchaDTO captchaSlider(SmsCaptcha smsCaptcha) {
        SmsCodeOrCaptchaDTO result=new SmsCodeOrCaptchaDTO();
        Object cacheObject = RedisUtil.getCacheObject("gruul:mall:pigeon:sms:captcha:flag");
        if (Objects.isNull(cacheObject)|| Boolean.FALSE.equals(cacheObject)) {
            //没有开启短信滑块 直接发送短信
            String code = smsService.sendSmsCode(smsCaptcha.getSmsType(), smsCaptcha.getMobile());
            result.setSmsCode(code);
            return result;
        }

        CaptchaResponse<ImageCaptchaVO> imageCaptchaVOCaptchaResponse = this.imageCaptchaApplication.generateCaptcha();
        result.setCaptcha(imageCaptchaVOCaptchaResponse);
        return result;
    }

    @Override
    public CaptchaResponse<ImageCaptchaVO> captchaSlider() {
        return this.imageCaptchaApplication.generateCaptcha();
    }

    @Override
    public List<ShopInfoVO> getSwitchLoginShops(ClientType clientType, Long shopId, Long userId) {
        Set<Long> shopIds = TenantShop.disable(
                () -> {
                    List<UserRole> userRoles = this.userRoleService.lambdaQuery()
                            .select(UserRole::getShopId)
                            .notIn(UserRole::getShopId, shopId, SecurityConst.NO_SHOP_ID_CLIENT_DEFAULT_SHOP_ID)
                            .eq(UserRole::getEnable, Boolean.TRUE)
                            .eq(UserRole::getUserId, userId)
                            .exists("SELECT role.id FROM t_role AS role WHERE role.id = t_user_role.role_id AND role.client_type={0}", clientType.getValue())
                            .list();
                    return userRoles.stream().map(UserRole::getShopId).collect(Collectors.toSet());
                }
        );
        if (CollUtil.isEmpty(shopIds)) {
            return Collections.emptyList();
        }
        //查询所有店铺信息
        return CollUtil.emptyIfNull(this.shopRpcService.getShopInfoByShopIdList(shopIds))
                .stream()
                .filter(Objects::nonNull)
                .filter(shop -> ShopStatus.NORMAL == shop.getStatus())
                .collect(Collectors.toList());
    }

    @Override
    public List<ShopInfoVO> getLoginPermShops(ClientType clientType, String username) {
        //查询当前用户名对应的所有用户
        List<User> users = this.userService.lambdaQuery()
                .eq(User::getUsername, username)
                .or()
                .eq(User::getMobile, username)
                .list();
        if (CollUtil.isEmpty(users)) {
            throw SecureCodes.USERNAME_PASSWORD_INVALID.exception();
        }
        return this.getLoginPermShops(clientType, users.stream().map(User::getId).collect(Collectors.toSet()));
    }

    @Override
    public List<ShopInfoVO> getLoginPermShops(ClientType clientType, Set<Long> userIds) {
        //查询所有店铺id
        List<UserRole> userRoleList = TenantShop.disable(
                () -> this.userRoleService.lambdaQuery()
                        .select(UserRole::getShopId, UserRole::getEnable)
                        .exists(
                                "SELECT t_role.id from t_role WHERE t_user_role.role_id=t_role.id AND t_role.deleted=0 AND t_role.client_type={0}",
                                clientType.getValue())
                        .in(UserRole::getUserId, userIds)
                        .list()
        );
        if (CollUtil.isEmpty(userRoleList)) {
            //查询不到店铺权限
            throw UaaError.MERCHANDISER_NOT_EXIST.exception();
        }

        Set<Long> shopIds = userRoleList.stream().map(UserRole::getShopId)
                .collect(Collectors.toSet());
        //查询所有店铺信息
        List<ShopInfoVO> shops = this.shopRpcService.getShopInfoByShopIdList(shopIds);
        shops = CollUtil.emptyIfNull(shops)
                .stream()
                .filter(Objects::nonNull)
                .filter(shop -> ShopStatus.NORMAL == shop.getStatus())
                .collect(Collectors.toList());
        //查询不到正常状态下的店铺
        if (CollUtil.isEmpty(shops)) {
            //查询不到店铺权限
            throw UaaError.MERCHANDISER_NOT_EXIST.exception();
        }

        return shops;
    }


    @Override
    public String resetMyPasswordCaptchaSms() {
        return Option.of(
                        this.userService.lambdaQuery().eq(User::getId, ISecurity.userMust().getId()).one()
                ).map(
                        user -> Option.of(user.getMobile())
                                .map(mobile -> this.smsService.sendSmsCode(SmsType.RESET_PASSWORD, mobile))
                                .getOrElseThrow(SystemCode.DATA_NOT_EXIST::exception)
                )
                .getOrElseThrow(SystemCode.DATA_NOT_EXIST::exception);

    }

    @Override
    public void resetMyPassword(ResetPasswordModel resetPassword) {
        Option.of(
                        this.userService.lambdaQuery().eq(User::getId, ISecurity.userMust().getId()).one()
                ).peek(
                        user -> Option.of(user.getMobile())
                                .peek(mobile -> {
                                    ResetPasswordDTO reset = new ResetPasswordDTO()
                                            .setMobile(mobile);
                                    reset.setCode(resetPassword.getCode())
                                            .setPassword(resetPassword.getPassword())
                                            .setConfirmPassword(resetPassword.getConfirmPassword());
                                    this.resetPassword(reset);
                                })
                                .getOrElseThrow(SecureCodes.ACCOUNT_INVALID::exception)
                )
                .getOrElseThrow(SecureCodes.ACCOUNT_INVALID::exception);
    }

    @Override
    public String resetPasswordCaptchaSms(String mobile) {
        boolean exists = this.userService.lambdaQuery().eq(User::getMobile, mobile).exists();
        if (!exists) {
            throw SecureCodes.ACCOUNT_INVALID.exception();
        }
        return this.smsService.sendSmsCode(SmsType.RESET_PASSWORD, mobile);
    }

    @Override
    public void resetPassword(ResetPasswordDTO resetPassword) {
        String mobile = resetPassword.getMobile();
        String code = resetPassword.getCode();
        String password = resetPassword.getPassword();
        if (!password.equals(resetPassword.getConfirmPassword())) {
            throw UaaError.CONFIRM_PASSWORD_INCORRECT.exception();
        }
        this.smsService.checkSmsCode(SmsType.RESET_PASSWORD, mobile, code);
        boolean exists = this.userService.lambdaQuery().eq(User::getMobile, mobile).exists();
        if (!exists) {
            throw SecureCodes.ACCOUNT_INVALID.exception();
        }
        boolean update = this.userService.lambdaUpdate()
                .set(User::getPassword, this.passwordEncoder.encode(password))
                .eq(User::getMobile, mobile)
                .update();
        SystemCode.DATA_UPDATE_FAILED.falseThrow(update);
    }


}
