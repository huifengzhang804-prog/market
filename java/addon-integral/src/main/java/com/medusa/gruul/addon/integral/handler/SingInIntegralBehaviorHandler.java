package com.medusa.gruul.addon.integral.handler;

import cn.hutool.core.map.MapUtil;
import com.medusa.gruul.addon.integral.model.constant.IntegralConstant;
import com.medusa.gruul.addon.integral.model.dto.RulesParameterDTO;
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
import java.util.NavigableMap;

/**
 * 签到积分行为处理
 *
 * @author xiaoq
 * @Description SingInIntegralBehaviorHandler.java
 * @date 2023-02-06 16:55
 */
@Slf4j
@Component
@RuleTypeHandler(GainRuleType.SING_IN)
public class SingInIntegralBehaviorHandler extends AbstractIntegralBehaviorHandler {
    private final IIntegralBehaviorService integralBehaviorService;

    private final RedissonClient redissonClient;

    public SingInIntegralBehaviorHandler(IIntegralRulesService integralRulesService, RabbitTemplate rabbitTemplate, IIntegralBehaviorService integralBehaviorService, RedissonClient redissonClient) {
        super(integralRulesService, rabbitTemplate);
        this.integralBehaviorService = integralBehaviorService;
        this.redissonClient = redissonClient;
    }

    /**
     * 1、先查询今天是否有其签到记录，如果有则退出，不获取积分
     * 2、插入今天的签到记录
     * 3、查看昨天是否有签到记录，没有则设置今天连续签到天数为1天，有则
     * 今天连续签到天数为 {昨天记录的连续签到天数 + 1}。如果昨天连续签到天数>={最大连续签到天数}，
     * 则今天的连续签到天数为 1 天
     * notion: 判断用户能获得的积分时， 积分 = {基础值} + {连续签到天数额外积分奖励}
     */
    @Override
    protected Integer integralBehaviorSave(Long userId, IntegralRules integralRules) {

        //获取redis分布式锁
        RLock lock = this.redissonClient.getLock(RedisUtil.key(IntegralConstant.INTEGRAL_BEHAVIOR_LOCK, userId));
        lock.lock();

        Integer basicsValue;
        NavigableMap<Integer, Integer> extendValue;
        int currentContinueSignInDays;

        try {
            //积分规则参数  签到第几天额外送多少积分
            RulesParameterDTO rulesParameter = integralRules.getRulesParameter();
            //每日签到获取的基础积分
            basicsValue = rulesParameter.getBasicsValue();
            //签到额外积分
            extendValue = rulesParameter.getExtendValue();

            //1、先查询今天是否有签到记录，如果有则退出，不获取积分
            LocalDate now = LocalDate.now();
            IntegralBehavior todayBehavior = this.integralBehaviorService.lambdaQuery()
                    .eq(IntegralBehavior::getUserId, userId)
                    .eq(IntegralBehavior::getRuleType, GainRuleType.SING_IN)
                    .eq(IntegralBehavior::getCurrentDate, now)
                    .one();
            if (todayBehavior != null) {
                //代表当日已经签到过
                return CommonPool.NUMBER_ZERO;
            }

            //2、创建今天的签到记录
            IntegralBehavior integralBehavior = new IntegralBehavior();
            integralBehavior.setUserId(userId)
                    .setRuleType(GainRuleType.SING_IN)
                    .setCurrentDate(now);

            //设置的最大连续登录天数
            Integer maxDays = IntegralConstant.INTEGRAL_BEHAVIOR_MAX_CONTINUE_DAYS;

            //3、获取昨天的签到记录
            IntegralBehavior yesterdayBehavior = this.integralBehaviorService.lambdaQuery()
                    .eq(IntegralBehavior::getUserId, userId)
                    .eq(IntegralBehavior::getRuleType, GainRuleType.SING_IN)
                    .eq(IntegralBehavior::getCurrentDate, now.minusDays(CommonPool.NUMBER_ONE))
                    .one();

            if (yesterdayBehavior == null) {
                //昨天没有签到，则今天连续签到天数为1天
                currentContinueSignInDays = CommonPool.NUMBER_ONE;
            } else {
                if (yesterdayBehavior.getContinueDays() >= maxDays) {
                    //如果昨天连续登录天数>={最大连续登录天数}，则今天的连续登录天数为 1 天
                    currentContinueSignInDays = CommonPool.NUMBER_ONE;
                } else {
                    //连续登录天数为 {昨天记录的连续登录天数 + 1}
                    currentContinueSignInDays = yesterdayBehavior.getContinueDays() + CommonPool.NUMBER_ONE;
                }
            }
            //设置今天的连续登录天数
            integralBehavior.setContinueDays(currentContinueSignInDays);
            //保存今天的登录数据
            this.integralBehaviorService.save(integralBehavior);
        } finally {
            lock.unlock();
        }

        //4、用户能获取的积分  积分 = {基础值} + {连续天数额外积分奖励}
        return basicsValue +
                MapUtil.getInt(extendValue, currentContinueSignInDays, 0);
    }
}
