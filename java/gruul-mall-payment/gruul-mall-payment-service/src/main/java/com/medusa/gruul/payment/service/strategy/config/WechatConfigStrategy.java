package com.medusa.gruul.payment.service.strategy.config;

import cn.hutool.core.util.CharsetUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import com.egzosn.pay.common.bean.CertStoreType;
import com.egzosn.pay.common.util.sign.SignUtils;
import com.medusa.gruul.common.model.enums.PayType;
import com.medusa.gruul.global.config.GlobalAppProperties;
import com.medusa.gruul.payment.service.common.model.MerchantConfig;
import com.medusa.gruul.payment.service.config.RedisMerchantPayConfigManager;
import com.medusa.gruul.payment.service.model.dto.MerchantDetailsDTO;
import com.medusa.gruul.payment.service.properties.PaymentProperty;

/**
 * <a href="https://pay.weixin.qq.com/doc/v3/merchant/4012154180">如何从平台证书切换成微信支付公钥</a>
 *
 * @author 张治保
 * @since 2024/12/11
 */

public class WechatConfigStrategy extends AbstractConfigStrategy {


    public WechatConfigStrategy(PaymentProperty paymentProperty, GlobalAppProperties globalAppProperties) {
        super(paymentProperty, globalAppProperties);
    }

    @Override
    protected String updateOrSave(MerchantDetailsDTO config) {
        String detailsId = config.getDetailsId();
        detailsId = StrUtil.isNotEmpty(detailsId) ? detailsId : IdUtil.fastSimpleUUID();
        // id不存在进行商户新增
        RedisMerchantPayConfigManager.instance.save(
                new MerchantConfig()
                        .setDetailsId(detailsId)
                        //固定值
                        .setPayType(PayType.WECHAT)
                        .setCertStoreType(CertStoreType.PATH)
                        .setSignType(SignUtils.MD5)
                        .setInputCharset(CharsetUtil.UTF_8)
                        .setIsTest(Boolean.FALSE)
                        //动态值
                        .setSubjectName(config.getSubjectName())
                        .setAppid(config.getAppid())
                        .setMchId(config.getMchId())
                        .setKeyPrivate(config.getKeyPrivate())
                        .setKeyPublic(config.getKeyPublic())
                        .setKeyCert(config.getKeyCert())
                        .setKeyCertPwd(config.getMchId())
                        //回调 通知地址
                        .setNotifyUrl(notifyUrl(detailsId))
                        .setReturnUrl(paymentProperty.getReturnUrl())
        );
        return detailsId;
    }
}
