package com.medusa.gruul.addon.full.reduction.components;

import com.medusa.gruul.addon.full.reduction.model.FullReductionConstant;
import com.medusa.gruul.common.model.constant.CommonPool;
import com.medusa.gruul.common.redis.util.RedisExist;
import org.springframework.stereotype.Component;

import java.time.Duration;

/**
 * @author 张治保
 * @since 2024/6/21
 */
@Component
public class FullReductionOrderExists extends RedisExist<String> {
    public FullReductionOrderExists() {
        super(FullReductionConstant.FULL_REDUCTION_ORDER_EXISTS_KEY);
        //设置自动清除
        autoClear(
                // 定期清理数据的时间间隔 1天
                Duration.ofDays(CommonPool.NUMBER_ONE),
                // 最多的存储时间 30天
                Duration.ofDays(CommonPool.NUMBER_THIRTY)
        );
    }
}
