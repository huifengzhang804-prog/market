package com.egzosn.pay.spring.boot.core.provider.merchant.platform;

import com.egzosn.pay.common.api.PayConfigStorage;
import com.egzosn.pay.common.api.PayService;
import com.egzosn.pay.common.bean.CertStoreType;
import com.egzosn.pay.common.bean.TransactionType;
import com.egzosn.pay.common.http.HttpConfigStorage;
import com.egzosn.pay.common.util.str.StringUtils;
import com.egzosn.pay.spring.boot.core.merchant.PaymentPlatform;
import com.egzosn.pay.spring.boot.core.merchant.bean.CommonPaymentPlatformMerchantDetails;
import com.egzosn.pay.wx.v3.api.WxPayConfigStorage;
import com.egzosn.pay.wx.v3.api.WxPayService;
import com.egzosn.pay.wx.v3.bean.WxTransactionType;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;

/**
 * 微信V3支付平台
 *
 * @author egan
 * <pre>
 * email egan@egzosn.com
 * date  2021/10/7.
 * </pre>
 */
@Configuration(WxV3PaymentPlatform.PLATFORM_NAME)
@ConditionalOnMissingBean(WxV3PaymentPlatform.class)
@ConditionalOnClass(name = {"com.egzosn.pay.wx.v3.api.WxPayConfigStorage"})
public class WxV3PaymentPlatform extends WxPayConfigStorage implements PaymentPlatform {

    public static final String PLATFORM_NAME = "wxV3Pay";
    @Deprecated
    public static final String platformName = PLATFORM_NAME;
    private final Log LOG = LogFactory.getLog(WxV3PaymentPlatform.class);

    /**
     * 获取商户平台
     *
     * @return 商户平台
     */
    @Override
    public String getPlatform() {
        return PLATFORM_NAME;
    }

    /**
     * 获取支付平台对应的支付服务
     *
     * @param payConfigStorage 支付配置
     * @return 支付服务
     */
    @Override
    public PayService getPayService(PayConfigStorage payConfigStorage) {
        return getPayService(payConfigStorage, null);
    }

    /**
     * 获取支付平台对应的支付服务
     *
     * @param payConfigStorage  支付配置
     * @param httpConfigStorage 网络配置
     * @return 支付服务
     */
    @Override
    public PayService getPayService(PayConfigStorage payConfigStorage, HttpConfigStorage httpConfigStorage) {
        if (payConfigStorage instanceof WxPayConfigStorage) {
            WxPayService wxPayService = new WxPayService((WxPayConfigStorage) payConfigStorage);
            wxPayService.setRequestTemplateConfigStorage(httpConfigStorage);
            return wxPayService;
        }
        WxPayConfigStorage configStorage = new WxPayConfigStorage();
        configStorage.setInputCharset(payConfigStorage.getInputCharset());
        configStorage.setAppId(payConfigStorage.getAppId());
        configStorage.setMchId(payConfigStorage.getPid());
        configStorage.setAttach(payConfigStorage.getAttach());
        configStorage.setV3ApiKey(payConfigStorage.getKeyPrivate());
        //微信支付公钥
        String keyPublic = payConfigStorage.getKeyPublic();
        if (StringUtils.isNotEmpty(keyPublic)) {
            String[] splits = keyPublic.split(",");
            configStorage.setKeyPublicId(splits[0]);
            configStorage.setKeyPublic(splits[1]);
        }
        configStorage.setNotifyUrl(payConfigStorage.getNotifyUrl());
        configStorage.setReturnUrl(payConfigStorage.getReturnUrl());
        configStorage.setPayType(payConfigStorage.getPayType());
        configStorage.setTest(payConfigStorage.isTest());
        configStorage.setSignType(payConfigStorage.getSignType());
        if (payConfigStorage instanceof CommonPaymentPlatformMerchantDetails merchantDetails) {
            configStorage.setSubAppId(merchantDetails.getSubAppId());
            configStorage.setSubMchId(merchantDetails.getSubMchId());
            if (null != merchantDetails.getKeyCert()) {
                configStorage.setCertStoreType(merchantDetails.getCertStoreType());
                try {
                    configStorage.setApiClientKeyP12(merchantDetails.getKeyCertInputStream());
                } catch (IOException e) {
                    LOG.error(e);
                }
                configStorage.setCertStoreType(CertStoreType.INPUT_STREAM);

            }
        }


        WxPayService wxPayService = new WxPayService(configStorage);
        wxPayService.setRequestTemplateConfigStorage(httpConfigStorage);
        return wxPayService;
    }

    @Override
    public TransactionType getTransactionType(String name) {
        return WxTransactionType.valueOf(name);
    }


}
