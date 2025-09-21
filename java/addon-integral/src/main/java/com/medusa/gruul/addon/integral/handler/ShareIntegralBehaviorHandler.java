package com.medusa.gruul.addon.integral.handler;

import com.medusa.gruul.addon.integral.model.constant.IntegralConstant;
import com.medusa.gruul.addon.integral.model.enums.GainRuleType;
import com.medusa.gruul.addon.integral.mp.entity.IntegralBehavior;
import com.medusa.gruul.addon.integral.mp.entity.IntegralRules;
import com.medusa.gruul.addon.integral.mp.service.IIntegralBehaviorService;
import com.medusa.gruul.addon.integral.mp.service.IIntegralRulesService;
import com.medusa.gruul.common.model.constant.CommonPool;
import com.medusa.gruul.common.redis.util.RedisUtil;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

/**
 * 分享积分处理
 *
 * @author xiaoq
 * @Description ShareIntegralBehaviorHandler.java
 * @date 2023-02-06 16:52
 */
@Slf4j
@Component
@RuleTypeHandler(GainRuleType.SHARE)
public class ShareIntegralBehaviorHandler extends AbstractIntegralBehaviorHandler {
    private final IIntegralBehaviorService integralBehaviorService;

    private final RedissonClient redissonClient;


    public ShareIntegralBehaviorHandler(IIntegralRulesService integralRulesService, RabbitTemplate rabbitTemplate, IIntegralBehaviorService integralBehaviorService, RabbitTemplate rabbitTemplate1, RedissonClient redissonClient) {
        super(integralRulesService, rabbitTemplate);
        this.integralBehaviorService = integralBehaviorService;
        this.redissonClient = redissonClient;
    }


    @Override
    protected Integer integralBehaviorSave(Long userId, IntegralRules integralRules) {

        //获取redis分布式锁
        RLock lock = this.redissonClient.getLock(RedisUtil.key(IntegralConstant.INTEGRAL_BEHAVIOR_LOCK, userId));
        lock.lock();

        Integer basicsValue;
        try {
            IntegralBehavior integralBehavior = integralBehaviorService.lambdaQuery()
                    .eq(IntegralBehavior::getUserId, userId)
                    .eq(IntegralBehavior::getCurrentDate, LocalDate.now())
                    .eq(IntegralBehavior::getRuleType, GainRuleType.SHARE)
                    .one();
            if (integralBehavior != null) {
                return CommonPool.NUMBER_ZERO;
            }
            integralBehavior = new IntegralBehavior();
            log.info("保存用户今日分享数据信息---------------------------------------");
            integralBehavior.setUserId(userId)
                    .setRuleType(GainRuleType.SHARE)
                    .setCurrentDate(LocalDate.now());
            integralBehaviorService.save(integralBehavior);

            //每日分享获取的积分值
            basicsValue = integralRules.getRulesParameter().getBasicsValue();
        } finally {
            lock.unlock();
        }

        return basicsValue;
    }


}
