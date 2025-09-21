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
 * @author 张治保
 * @since 2024/12/11
 */

public class AliConfigStrategy extends AbstractConfigStrategy {


    public AliConfigStrategy(PaymentProperty paymentProperty, GlobalAppProperties globalAppProperties) {
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
                        .setPayType(PayType.ALIPAY)
                        .setCertStoreType(CertStoreType.PATH)
                        .setSignType(SignUtils.RSA2)
                        .setInputCharset(CharsetUtil.UTF_8)
                        .setIsTest(false)
                        //动态值
                        .setSubjectName(config.getSubjectName())
                        .setAppid(config.getAppid())
                        .setKeyPrivate(config.getKeyPrivate())
                        .setKeyPublic(config.getKeyPublic())
                        .setKeyCert(config.getKeyCert())
                        //回调 通知地址
                        .setNotifyUrl(notifyUrl(detailsId))
                        .setReturnUrl(paymentProperty.getReturnUrl())
        );
        return detailsId;
    }
}
