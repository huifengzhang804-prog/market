package com.medusa.gruul.addon.ic.modules.uupt.service.impl;

import cn.hutool.core.img.ImgUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.qrcode.QrCodeUtil;
import cn.hutool.extra.qrcode.QrConfig;
import com.medusa.gruul.addon.ic.model.ICError;
import com.medusa.gruul.addon.ic.modules.opens.uupt.UuptConfig;
import com.medusa.gruul.addon.ic.modules.opens.uupt.UuptResponse;
import com.medusa.gruul.addon.ic.modules.opens.uupt.api.IOrderApi;
import com.medusa.gruul.addon.ic.modules.opens.uupt.api.IUuptApiFactory;
import com.medusa.gruul.addon.ic.modules.opens.uupt.model.enums.PayTypeEnum;
import com.medusa.gruul.addon.ic.modules.opens.uupt.model.order.OpenCityListResp;
import com.medusa.gruul.addon.ic.modules.opens.uupt.model.user.*;
import com.medusa.gruul.addon.ic.modules.uupt.model.UuptConstant;
import com.medusa.gruul.addon.ic.modules.uupt.model.dto.UuptConfigDTO;
import com.medusa.gruul.addon.ic.modules.uupt.model.dto.UuptShopAuthDTO;
import com.medusa.gruul.addon.ic.modules.uupt.model.dto.UuptShopSmsDTO;
import com.medusa.gruul.addon.ic.modules.uupt.model.vo.UuptShopRechargeVO;
import com.medusa.gruul.addon.ic.modules.uupt.model.vo.UuptShopSmsVO;
import com.medusa.gruul.addon.ic.modules.uupt.model.vo.UuptStatusVO;
import com.medusa.gruul.addon.ic.modules.uupt.service.UuptService;
import com.medusa.gruul.addon.ic.modules.uupt.util.UuptHelper;
import com.medusa.gruul.addon.ic.properties.ICConfigurationProperties;
import com.medusa.gruul.common.model.resp.SystemCode;
import com.medusa.gruul.common.redis.util.RedisUtil;
import com.medusa.gruul.common.system.model.ISystem;
import com.medusa.gruul.shop.api.model.vo.ShopAddressVO;
import com.medusa.gruul.shop.api.rpc.ShopRpcService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Set;

/**
 * @author 张治保
 * @since 2024/8/13
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class UuptServiceImpl implements UuptService {

    private final ShopRpcService shopRpcService;
    private final IUuptApiFactory uuptApiFactory;
    private final ICConfigurationProperties configurationProperties;

    @Override
    public void saveOpenConfig(UuptConfigDTO param) {
        UuptConfig config = uuptApiFactory.config();
        if (config != null) {
            throw SystemCode.DATA_EXISTED.exception();
        }
        UuptConfig uuptConfig = new UuptConfig()
                .setAppid(param.getAppid())
                .setAppKey(param.getAppKey())
                .setOpenId(param.getOpenId())
                .setTest(configurationProperties.isOpenApiTest());
        //测试配置是否正确 ，通过获取开放城市列表测试
        IOrderApi orderApi = () -> uuptConfig;
        //以下情况代表配置不正确
        //UuptResponse(code=88000103, state=88000103, body=null, msg=appId无效, total=null)
        //UuptResponse(code=88000104, state=88000104, body=null, msg=签名验签失败, total=)
        //UuptResponse(code=88000105, state=88000105, body=null, msg=openId无效, total=)
        UuptResponse<OpenCityListResp> openCityListResponse = orderApi.openCityList();
        if (!openCityListResponse.isSuccess()) {
            throw ICError.WRONG_OPEN_API_CONFIG.exception();
        }
        RedisUtil.setCacheMap(UuptConstant.UUPT_CONFIG_CACHE_KEY, uuptConfig);
        UuptHelper.saveOpenCities(openCityListResponse.getBody());
    }

    @Override
    public UuptConfig getOpenConfig() {
        UuptConfig config = RedisUtil.getCacheMap(UuptConstant.UUPT_CONFIG_CACHE_KEY, UuptConfig.class);
        return config == null ? new UuptConfig() : config;
    }

    @Override
    public UuptStatusVO status(Long shopId) {
        UuptStatusVO status = new UuptStatusVO();
        //平台配置是否已开通 uupt 服务
        boolean platformActivated = uuptApiFactory.config() != null;
        status.setPlatformActivated(platformActivated);
        //如果平台未开通则直接返回
        if (!platformActivated) {
            return status;
        }
        String openId = UuptHelper.getOpenid(shopId);
        //店铺所在城市是否开通
        ShopAddressVO shopAddress = shopRpcService.shopAddress(Set.of(shopId)).get(shopId);
        status.setCityOpen(
                shopAddress != null && UuptHelper.cityIsOpen(shopAddress.city(), shopAddress.county(), uuptApiFactory)
        );
        //是否 openId 未激活
        if (StrUtil.isEmpty(openId)) {
            //如果未激活 则查询店铺地址判断当前店铺城市是否可开通
            return status.setActivated(Boolean.FALSE);
        }
        status.setActivated(Boolean.TRUE);
        UuptResponse<AccountResp> response = uuptApiFactory.user()
                .account(
                        openId,
                        new AccountParam()
                                .setPayTypeEnum(PayTypeEnum.BALANCE_PAY)
                );
        if (!response.isSuccess()) {
            throw ICError.UUPT_RESP_ERROR.dataEx(response);
        }
        AccountResp body = response.getBody();
        status.setBalance(UuptHelper.fenToHao(body.getBalance()))
                .setFrozen(UuptHelper.fenToHao(body.getFrozenAmount()));
        return status;
    }

    @Override
    public UuptShopSmsVO sendSms(UuptShopSmsDTO param) {
        UuptResponse<Void> resp = uuptApiFactory.user()
                .sendSmsCode(
                        new SendSmsCodeParam()
                                .setUserMobile(param.getMobile())
                                .setUserIp(ISystem.ipMust())
                                .setImageCode(param.getCaptcha())
                );
        if (resp.isSuccess()) {
            return new UuptShopSmsVO()
                    .setNeedCaptcha(false);
        }
        //需要输入验证码重新发送
        if (resp.getCode().equals(UuptConstant.UUPT_NEED_CAPTCHA)) {
            return new UuptShopSmsVO()
                    .setNeedCaptcha(true)
                    .setBase64Captcha(resp.getMsg());
        }
        //异常情况
        throw ICError.UUPT_RESP_ERROR.dataEx(resp);
    }

    @Override
    public void auth(Long shopId, UuptShopAuthDTO param) {
        ShopAddressVO shopAddress = shopRpcService.shopAddress(Set.of(shopId)).get(shopId);
        if (shopAddress == null) {
            throw new RuntimeException();
        }
        UuptResponse<AuthResp> response = uuptApiFactory.user()
                .auth(
                        new AuthParam()
                                .setUserMobile(param.getMobile())
                                .setUserIp(ISystem.ipMust())
                                .setSmsCode(param.getSmsCode())
                                .setCityName(shopAddress.city())
                                .setCountyName(shopAddress.county())
                );
        if (!response.isSuccess()) {
            log.error("UUpt 授权失败:{}", response);
            throw ICError.UUPT_RESP_ERROR.dataEx(response);
        }
        log.debug("uupt 授权成功:{}", response);
        AuthResp data = response.getBody();
        UuptHelper.setOpenid(shopId, data.getOpenId());
    }

    @Override
    public UuptShopRechargeVO recharge(Long shopId) {
        String openid = UuptHelper.getOpenid(shopId);
        if (StrUtil.isEmpty(openid)) {
            throw ICError.UUPT_UNACTIVATED.exception();
        }
        UuptResponse<RechargeResp> response = uuptApiFactory.user()
                .recharge(openid);
        if (!response.isSuccess()) {
            log.error("UUpt 获取充值链接失败:{}", response);
            throw ICError.UUPT_RESP_ERROR.dataEx(response);
        }
        RechargeResp body = response.getBody();
        return new UuptShopRechargeVO()
                .setH5Qrcode(
                        QrCodeUtil.generateAsBase64(
                                body.getH5Url(),
                                QrConfig.create()
                                        .setHeight(300)
                                        .setHeight(300),
                                ImgUtil.IMAGE_TYPE_PNG
                        )
                ).setPcUrl(body.getWebUrl());
    }

}
