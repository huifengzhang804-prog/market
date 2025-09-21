package com.medusa.gruul.payment.service.strategy.config;

import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson2.TypeReference;
import com.medusa.gruul.common.model.enums.PayType;
import com.medusa.gruul.common.redis.util.RedisUtil;
import com.medusa.gruul.common.system.model.model.Platform;
import com.medusa.gruul.global.config.GlobalAppProperties;
import com.medusa.gruul.global.model.constant.Services;
import com.medusa.gruul.global.model.strategy.IStrategy;
import com.medusa.gruul.payment.service.model.PayConst;
import com.medusa.gruul.payment.service.model.dto.MerchantDetailsDTO;
import com.medusa.gruul.payment.service.model.enums.PaymentError;
import com.medusa.gruul.payment.service.properties.PaymentProperty;
import lombok.RequiredArgsConstructor;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author 张治保
 * @since 2024/12/11
 */
@RequiredArgsConstructor
public abstract class AbstractConfigStrategy implements IStrategy<PayType, MerchantDetailsDTO, Void> {

    protected final PaymentProperty paymentProperty;
    protected final GlobalAppProperties globalAppProperties;

    @Override
    public Void execute(PayType type, MerchantDetailsDTO config) {
        //是否是编辑操作 通过id是否为空判断 不为空则是编辑操作
        String editDetailId = config.getDetailsId();
        boolean edit = StrUtil.isNotBlank(editDetailId);

        //校验支付平台是否发生了重复
        //如果查询出的
        String platformCacheKey = RedisUtil.key(PayConst.PAY_CONFIG_CACHE_KEY, type);
        //key 平台类型 value 商户详情id
        Map<String, String> detailIdMap = RedisUtil.getCacheMap(
                platformCacheKey,
                new TypeReference<>() {
                }
        );
        Set<Platform> platformEnums = config.getPlatforms();
        detailIdMap = detailIdMap == null ? new HashMap<>(platformEnums.size()) : detailIdMap;
        Set<String> platforms = platformEnums.stream().map(Enum::name).collect(Collectors.toSet());
        //校验是否重复配置支付平台
        Iterator<Map.Entry<String, String>> iterator = detailIdMap.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<String, String> entry = iterator.next();
            String curPlatform = entry.getKey();
            String curDetailId = entry.getValue();
            //如果是编辑操作 则校验是否重复配置支付平台
            if (edit) {
                //当前配置id是否和编辑的id相同
                boolean notContainsCurPlatform = !platforms.contains(curPlatform);
                if (curDetailId.equals(editDetailId)) {
                    //如果不包含当前平台 则删除
                    if (notContainsCurPlatform) {
                        iterator.remove();
                    }
                    continue;
                }
                //没绑定当前平台  且当前平台被别的商户绑定了 则跳过
                if (notContainsCurPlatform) {
                    continue;
                }
                //id不相同 说明重复配置
                throw PaymentError.PAYMENT_CHANNEL_DUPLICATE.exception();
            }
            //如果是新增操作 则校验是否重复配置支付平台
            if (platforms.contains(curPlatform)) {
                throw PaymentError.PAYMENT_CHANNEL_DUPLICATE.exception();
            }
        }
        //保存新支付平台配置
        String detailsId = updateOrSave(config);
        //1. 删除旧支付平台配置
        if (edit) {
            RedisUtil.delete(platformCacheKey);
        }
        for (String platform : platforms) {
            detailIdMap.put(platform, detailsId);
        }
        //2. 保存新支付平台配置
        RedisUtil.setCacheMap(platformCacheKey, detailIdMap);
        return VOID;
    }

    protected abstract String updateOrSave(MerchantDetailsDTO config);


    /**
     * 获取支付回调地址
     *
     * @param detailsId 支付配置详情id
     * @return 支付回调地址
     */
    protected String notifyUrl(String detailsId) {
        return globalAppProperties.fullUrl(Services.GRUUL_MALL_PAYMENT + "/merchant/pay/notify/" + detailsId);
    }

}
