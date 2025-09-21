package com.medusa.gruul.payment.service.service.impl;

import cn.hutool.core.collection.CollUtil;
import com.alibaba.fastjson2.TypeReference;
import com.medusa.gruul.common.model.enums.PayType;
import com.medusa.gruul.common.redis.annotation.Redisson;
import com.medusa.gruul.common.redis.util.RedisUtil;
import com.medusa.gruul.common.system.model.ISystem;
import com.medusa.gruul.common.system.model.model.Platform;
import com.medusa.gruul.payment.service.common.helper.WechatHelper;
import com.medusa.gruul.payment.service.common.model.MerchantConfig;
import com.medusa.gruul.payment.service.model.PayConst;
import com.medusa.gruul.payment.service.model.dto.MerchantDetailsDTO;
import com.medusa.gruul.payment.service.model.enums.PaymentError;
import com.medusa.gruul.payment.service.model.vo.MerchantDetailsVO;
import com.medusa.gruul.payment.service.service.PaymentMerchantService;
import com.medusa.gruul.payment.service.strategy.config.PayConfigStrategyFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author xiaoq
 * @ Description
 * @date 2022-07-13 09:14
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentMerchantServiceImpl implements PaymentMerchantService {

    private final PayConfigStrategyFactory payConfigStrategyFactory;


    /**
     * 编辑(新增/或修改)商户支付配置
     *
     * @param param 支付商户渠道配置DTO
     */
    @Override
    @Redisson(name = PayConst.PAY_CHANNEL_LOCK_KEY, key = "#param.payType")
    @Transactional(rollbackFor = Exception.class)
    public void editPaymentMerchant(MerchantDetailsDTO param) {
        payConfigStrategyFactory.exec(param.getPayType(), param);
    }

    /**
     * 获取商户支付配置
     *
     * @param payType 支付渠道
     * @return 商户支付配置
     */
    @Override
    public List<MerchantDetailsVO> getMerchantDetail(PayType payType) {
        Map<Platform, String> detailsIdMap = RedisUtil.getCacheMap(
                RedisUtil.key(PayConst.PAY_CONFIG_CACHE_KEY, payType),
                new TypeReference<>() {
                }
        );
        if (CollUtil.isEmpty(detailsIdMap)) {
            return List.of();
        }
        Map<String, List<Platform>> detailPlatformsMap = detailsIdMap.entrySet()
                .stream()
                .collect(
                        Collectors.groupingBy(
                                Map.Entry::getValue,
                                Collectors.mapping(Map.Entry::getKey, Collectors.toList())
                        )
                );
        List<MerchantConfig> configs = RedisUtil.getMultiCacheMapValue(
                PayConst.PAY_CONFIG_CACHE_KEY,
                detailPlatformsMap.keySet(),
                MerchantConfig.class
        );
        if (CollUtil.isEmpty(configs)) {
            return List.of();
        }
        Map<String, MerchantConfig> configMap = configs.stream()
                .collect(Collectors.toMap(MerchantConfig::getDetailsId, Function.identity()));
        return detailPlatformsMap.entrySet()
                .stream()
                .map(
                        entry -> {
                            String detailId = entry.getKey();
                            MerchantConfig config = configMap.get(detailId);
                            if (config == null) {
                                return null;
                            }
                            return new MerchantDetailsVO()
                                    .setDetailsId(config.getDetailsId())
                                    .setPayType(config.getPayType())
                                    .setAppid(config.getAppid())
                                    .setKeyPrivate(config.getKeyPrivate())
                                    .setKeyPublic(config.getKeyPublic())
                                    .setMchId(config.getMchId())
                                    .setKeyCert(config.getKeyCert())
                                    .setSubjectName(config.getSubjectName())
                                    .setPlatforms(entry.getValue());
                        }
                ).filter(Objects::nonNull)
                .toList();


    }

    /**
     * 支付证书上传
     *
     * @param file 证书文件
     */
    @Override
    public String uploadCertificate(MultipartFile file) {
        String originalFilename = file.getOriginalFilename();
        if (!WechatHelper.nameVerify(originalFilename)) {
            throw PaymentError.PAYMENT_CERTIFICATE_FORMAT_ERROR.exception();
        }
        String absolutePath = WechatHelper.absolutePath(ISystem.shopIdMust().toString(), originalFilename);
        log.debug("支付证书存放路径:{}", absolutePath);
        try {
            file.transferTo(new File(absolutePath));
        } catch (IOException exception) {
            log.error("保存文件发生错误", exception);
            throw PaymentError.PAYMENT_CERTIFICATE_UPLOAD_ERROR.exception();
        }
        return absolutePath;
    }
}
