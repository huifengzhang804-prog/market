package com.medusa.gruul.addon.seckill.model.enums;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.medusa.gruul.addon.seckill.mp.entity.SeckillActivity;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;
import java.util.function.Consumer;

/**
 * @author 张治保
 * @since 2024/5/29
 */
@Getter
@RequiredArgsConstructor
public enum SeckillQueryStatus {

    /**
     * 未开始
     */
    NOT_START(
            query -> query.gt(SeckillActivity::getStartTime, LocalDateTime.now())
                    .eq(SeckillActivity::getStatus, SeckillStatus.OK)
    ),

    /**
     * 进行中
     */
    IN_PROGRESS(
            query -> {
                LocalDateTime now = LocalDateTime.now();
                query.le(SeckillActivity::getStartTime, now)
                        .ge(SeckillActivity::getEndTime, now)
                        .eq(SeckillActivity::getStatus, SeckillStatus.OK);
            }
    ),

    /**
     * 已结束
     */
    FINISHED(
            query -> query.lt(SeckillActivity::getEndTime, LocalDateTime.now())
                    .eq(SeckillActivity::getStatus, SeckillStatus.FINISHED)
    ),

    /**
     * 已下架
     */
    OFF_SHELF(
            query -> query.eq(SeckillActivity::getStatus, SeckillStatus.OFF_SHELF)
    ),

    /**
     * 违规下架
     */
    VIOLATION_OFF_SHELF(
            query -> query.eq(SeckillActivity::getStatus, SeckillStatus.VIOLATION_OFF_SHELF)
    );


    private final Consumer<LambdaQueryWrapper<SeckillActivity>> consumer;

}
