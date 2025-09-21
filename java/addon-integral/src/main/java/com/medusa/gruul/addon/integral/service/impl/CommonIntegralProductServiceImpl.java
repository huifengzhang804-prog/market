package com.medusa.gruul.addon.integral.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.medusa.gruul.addon.integral.model.param.IntegralProductParam;
import com.medusa.gruul.addon.integral.model.vo.IntegralProductListVO;
import com.medusa.gruul.addon.integral.mp.entity.IntegralProduct;
import com.medusa.gruul.addon.integral.mp.service.IIntegralProductService;
import com.medusa.gruul.addon.integral.service.CommonIntegralProductService;
import com.medusa.gruul.addon.integral.util.IntegralUtil;
import com.medusa.gruul.common.model.constant.CommonPool;
import com.medusa.gruul.common.redis.util.RedisUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.function.Supplier;

/**
 * 平台+用户端 通用积分商品数据实现层
 *
 * @author xiaoq
 * @Description 平台+用户端 通用积分商品数据实现层
 * @date 2023-02-01 10:43
 */

@Service
@RequiredArgsConstructor
public class CommonIntegralProductServiceImpl implements CommonIntegralProductService {
    private final IIntegralProductService integralProductService;

    @Override
    public IPage<IntegralProductListVO> getIntegralProductList(IntegralProductParam integralProductParam) {
        return integralProductService.getIntegralProductList(integralProductParam);
    }

    @Override
    public IntegralProduct getIntegralProductInfo(Long id) {
        String key = IntegralUtil.integralProductCacheKey(id);
        return getInfo(() -> getIntegralProductInfoById(id), key);
    }

    /**
     * 获取 积分商品信息 supplier or key
     * (特定情况下更新续期时长 or 设置缓存)
     *
     * @param supplier sql
     * @param key      redis
     * @return 积分商品信息
     */
    public IntegralProduct getInfo(Supplier<IntegralProduct> supplier, String key) {
        return RedisUtil.getCacheMap(
                IntegralProduct.class,
                supplier,
                Duration.ofSeconds(RedisUtil.expireWithRandom(CommonPool.UNIT_CONVERSION_TEN_THOUSAND)),
                key
        );
    }

    private IntegralProduct getIntegralProductInfoById(Long id) {
        return integralProductService.getIntegralProductInfoById(id);
    }

}
