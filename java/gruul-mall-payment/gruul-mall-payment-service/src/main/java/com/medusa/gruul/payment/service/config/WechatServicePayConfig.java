package com.medusa.gruul.payment.service.config;

import com.github.binarywang.wxpay.config.WxPayConfig;
import com.github.binarywang.wxpay.service.WxPayService;
import com.github.binarywang.wxpay.service.impl.WxPayServiceImpl;
import com.medusa.gruul.global.config.GlobalAppProperties;
import com.medusa.gruul.global.model.constant.Services;
import com.medusa.gruul.payment.service.properties.PaymentProperty;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author 张治保
 * date 2023/6/2
 */
@Configuration
@RequiredArgsConstructor
@ConditionalOnProperty(prefix = "gruul.pay", name = "enable-service-mode", havingValue = "true")
public class WechatServicePayConfig {

    private final PaymentProperty payProperty;
    private final GlobalAppProperties globalAppProperties;

    /**
     * v3 版本微信支付
     */
    @Bean
    public WxPayService wxPayService() {
        PaymentProperty.WxServiceConf wxServiceConf = payProperty.getWxServiceConf();
        WxPayConfig payConfig = new WxPayConfig();
        payConfig.setAppId(wxServiceConf.getAppid());
        payConfig.setMchId(wxServiceConf.getMchId());
        payConfig.setNotifyUrl(globalAppProperties.fullUrl(Services.GRUUL_MALL_PAYMENT + "/wx/service/merchant/combine/notify"));
        payConfig.setSignType(wxServiceConf.getSignType());
        payConfig.setKeyPath(wxServiceConf.getKeyPath());
        payConfig.setPrivateCertPath(wxServiceConf.getPrivateCertPath());
        payConfig.setPrivateKeyPath(wxServiceConf.getPrivateKeyPath());
        payConfig.setApiV3Key(wxServiceConf.getApiV3Key());
        WxPayService wxPayService = new WxPayServiceImpl();
        wxPayService.setConfig(payConfig);
        return wxPayService;
    }

}
