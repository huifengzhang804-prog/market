package com.medusa.gruul.payment.service.config;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.map.WeakConcurrentMap;
import cn.hutool.core.util.BooleanUtil;
import com.egzosn.pay.common.api.PayMessageHandler;
import com.egzosn.pay.common.api.PayMessageInterceptor;
import com.egzosn.pay.common.api.PayService;
import com.egzosn.pay.common.bean.CertStoreType;
import com.egzosn.pay.common.bean.PayMessage;
import com.egzosn.pay.spring.boot.core.configurers.PayMessageConfigurer;
import com.egzosn.pay.spring.boot.core.merchant.MerchantNotFoundException;
import com.egzosn.pay.spring.boot.core.merchant.bean.CommonPaymentPlatformMerchantDetails;
import com.egzosn.pay.spring.boot.core.provider.JdbcMerchantDetailsManager;
import com.egzosn.pay.spring.boot.core.provider.MerchantDetailsManager;
import com.egzosn.pay.spring.boot.core.provider.merchant.platform.UnionPaymentPlatform;
import com.medusa.gruul.common.redis.util.RedisUtil;
import com.medusa.gruul.payment.service.common.model.MerchantConfig;
import com.medusa.gruul.payment.service.model.PayConst;
import lombok.NoArgsConstructor;
import org.springframework.data.redis.core.HashOperations;

import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * @author 张治保
 * @see JdbcMerchantDetailsManager
 * @since 2025/1/4
 */
@NoArgsConstructor(access = lombok.AccessLevel.PRIVATE)
public class RedisMerchantPayConfigManager implements MerchantDetailsManager<CommonPaymentPlatformMerchantDetails> {

    public static RedisMerchantPayConfigManager instance = new RedisMerchantPayConfigManager();

    /**
     * 商户支付配置详情 内存缓存
     */
    private final Map<String, CommonPaymentPlatformMerchantDetails> merchantMap = new WeakConcurrentMap<>();
    private PayMessageConfigurer configurer;


    /**
     * 新增商户支付配置
     *
     * @param config 商户支付配置
     */
    public void save(MerchantConfig config) {
        String detailsId = config.getDetailsId();
        assert detailsId != null;
        RedisUtil.setCacheMapValue(PayConst.PAY_CONFIG_CACHE_KEY, detailsId, config);
        merchantMap.remove(detailsId);
    }


    @Override
    public void createMerchant(CommonPaymentPlatformMerchantDetails merchant) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void createMerchant(Collection<CommonPaymentPlatformMerchantDetails> merchants) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void updateMerchant(CommonPaymentPlatformMerchantDetails merchant) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void deleteMerchant(String id) {
        RedisUtil.delCacheMapValue(PayConst.PAY_CONFIG_CACHE_KEY, id);
        merchantMap.remove(id);
    }

    @Override
    public boolean merchantExists(String id) {
        HashOperations<String, Object, Object> hashOperations = RedisUtil.getRedisTemplate().opsForHash();
        return BooleanUtil.isTrue(hashOperations.hasKey(PayConst.PAY_CONFIG_CACHE_KEY, id));
    }

    @Override
    public void setPayMessageConfigurer(PayMessageConfigurer configurer) {
        this.configurer = configurer;
    }


    @Override
    @SuppressWarnings("rawtypes")
    public CommonPaymentPlatformMerchantDetails loadMerchantByMerchantId(String detailsId) {
        return merchantMap.computeIfAbsent(detailsId, key -> {
            MerchantConfig config = RedisUtil.getCacheMapValue(PayConst.PAY_CONFIG_CACHE_KEY, key, MerchantConfig.class);
            if (config == null) {
                throw new MerchantNotFoundException(key);
            }
            CommonPaymentPlatformMerchantDetails details = new CommonPaymentPlatformMerchantDetails();
            details.setDetailsId(key);
            details.setAppId(config.getAppid());
            details.setPayType(config.getPayType().getPlatform());
            details.setMchId(config.getMchId());
            CertStoreType certStoreType = config.getCertStoreType();
            if (certStoreType != null) {
                details.setCertStoreType(certStoreType);
            }
            if (details.getCertStoreType() == CertStoreType.INPUT_STREAM && !UnionPaymentPlatform.PLATFORM_NAME.equals(details.getPayType())) {
                details.setKeyPrivate(config.getKeyPrivate());
                details.setKeyPublic(config.getKeyPublic());
            } else {
                details.setKeystore(config.getKeyPrivate());
                details.setKeyPublicCert(config.getKeyPublic());
            }
            details.setKeyCert(config.getKeyCert());
            details.setKeystorePwd(config.getKeyCertPwd());
            details.setNotifyUrl(config.getNotifyUrl());
            details.setReturnUrl(config.getReturnUrl());
            details.setSignType(config.getSignType().getName());
            details.setSeller(config.getSeller());
            details.setSubAppId(config.getSubAppId());
            details.setSubMchId(config.getSubMchId());
            details.setInputCharset(config.getInputCharset());
            details.setTest(config.getIsTest());
            PayService payService = details.initService().getPayService();
            PayMessageHandler<PayMessage, PayService> handler = configurer.getHandler(details.getPaymentPlatform());
            if (null != handler) {
                payService.setPayMessageHandler(handler);
            }
            List<PayMessageInterceptor<PayMessage, PayService>> interceptors = configurer.getInterceptor(details.getPaymentPlatform());
            if (CollUtil.isNotEmpty(interceptors)) {
                for (PayMessageInterceptor<PayMessage, PayService> interceptor : interceptors) {
                    payService.addPayMessageInterceptor(interceptor);
                }
            }
            return details;
        });


    }
}
