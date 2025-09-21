package com.medusa.gruul.addon.ic.modules.opens.uupt.api;

import com.alibaba.fastjson2.TypeReference;
import com.medusa.gruul.addon.ic.modules.opens.uupt.UuptResponse;
import com.medusa.gruul.addon.ic.modules.opens.uupt.model.user.*;

/**
 * @author 张治保
 * @since 2024/8/10
 */
public interface IUserApi extends IBaseUuptApi {

    /**
     * 发送短信验证码
     * <a href="https://open.uupt.com/#/ApiDocument/v3/InterfaceList/sendSmsCode?PlatType=PLAT_MERCHANT&version=3&t=%E5%8F%91%E9%80%81%E7%9F%AD%E4%BF%A1%E9%AA%8C%E8%AF%81%E7%A0%81&index=1-5-12">
     * 发送短信验证码
     * </a>
     *
     * @return void
     */
    default UuptResponse<Void> sendSmsCode(SendSmsCodeParam param) {
        return request(
                "unauthorized/sendSmsCode",
                param,
                null
        );
    }

    /**
     * 用户授权 (绑定用户获取 open id)
     * <a href="https://open.uupt.com/#/ApiDocument/v3/InterfaceList/auth?PlatType=PLAT_MERCHANT&version=3&t=%E7%94%A8%E6%88%B7%E6%8E%88%E6%9D%83&index=1-5-13">
     * 用户授权
     * </a>
     *
     * @return 用户授权结果 （可获取用户 openId）
     */
    default UuptResponse<AuthResp> auth(AuthParam param) {
        return request(
                "unauthorized/auth",
                param,
                new TypeReference<>() {
                }
        );
    }

    /**
     * 用户解除授权 (解除绑定关系)
     * <a href="https://open.uupt.com/#/ApiDocument/v3/InterfaceList/unbind?PlatType=PLAT_MERCHANT&version=3&t=%E7%94%A8%E6%88%B7%E8%A7%A3%E9%99%A4%E6%8E%88%E6%9D%83&index=1-5-14">
     * 用户解除授权
     * </a>
     *
     * @return 用户授权结果 （可获取用户 openId）
     */
    default UuptResponse<Void> unbind(String openid) {
        return withOpenId(
                openid,
                () -> request(
                        null,
                        new TypeReference<Void>() {
                        }
                )
        );
    }


    /**
     * 获取账户余额
     * <a href="https://open.uupt.com/#/ApiDocument/v3/InterfaceList/account?PlatType=PLAT_MERCHANT&version=3&t=%E8%8E%B7%E5%8F%96%E8%B4%A6%E6%88%B7%E4%BD%99%E9%A2%9D&index=1-5-10">
     * 获取账户余额
     * </a>
     *
     * @return 账户余额
     */
    default UuptResponse<AccountResp> account(String openid, AccountParam param) {
        return withOpenId(
                openid,
                () -> request(
                        param,
                        new TypeReference<AccountResp>() {
                        }
                )
        );
    }

    /**
     * 获取充值URL
     * <a href="https://open.uupt.com/#/ApiDocument/v3/InterfaceList/recharge?PlatType=PLAT_MERCHANT&version=3&t=%E8%8E%B7%E5%8F%96%E5%85%85%E5%80%BCURL&index=1-5-11">
     * 获取充值URL
     * </a>
     *
     * @return 充值URL
     */
    default UuptResponse<RechargeResp> recharge(String openid) {
        return withOpenId(
                openid,
                () -> request(
                        null,
                        new TypeReference<RechargeResp>() {
                        }
                )
        );
    }

    @Override
    default String apiPrefix() {
        return "/user/";
    }
}
