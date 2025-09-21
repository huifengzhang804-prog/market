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
 * 登入积分处理
 *
 * @author xiaoq
 * @Description LoginIntegralBehaviorHandler.java
 * @date 2023-02-06 16:54
 */
@Slf4j
@Component
@RuleTypeHandler(GainRuleType.LOGIN)
public class LoginIntegralBehaviorHandler extends AbstractIntegralBehaviorHandler {
    private final IIntegralBehaviorService integralBehaviorService;

    private final RedissonClient redissonClient;

    public LoginIntegralBehaviorHandler(IIntegralRulesService integralRulesService, RabbitTemplate rabbitTemplate, IIntegralBehaviorService integralBehaviorService, RedissonClient redissonClient) {
        super(integralRulesService, rabbitTemplate);
        this.integralBehaviorService = integralBehaviorService;
        this.redissonClient = redissonClient;
    }

    /**
     * 1、先今天是否有登录记录，如果有则退出，不获取积分
     * 2、插入今天的登录记录
     * 3、查看昨天是否有登录记录，没有则设置今天连续登录天数为1天，有则
     * 今天连续登录天数为 {昨天记录的连续登录天数 + 1}。如果昨天连续登录天数>={最大连续登录天数}，
     * 则今天的连续登录天数为 1 天
     * notion: 判断用户能获得的积分时， 积分 = {基础值} + {连续天数额外积分奖励}
     */
    @Override
    protected Integer integralBehaviorSave(Long userId, IntegralRules integralRules) {

        //获取redis分布式锁
        RLock lock = this.redissonClient.getLock(RedisUtil.key(IntegralConstant.INTEGRAL_BEHAVIOR_LOCK, userId));
        lock.lock();
        Integer basicsValue;
        NavigableMap<Integer, Integer> extendValue;
        int currentContinueLoginDays;

        try {
            //积分规则参数  登录第几天额外送多少积分
            RulesParameterDTO rulesParameter = integralRules.getRulesParameter();
            //每日登录获取的基础积分
            basicsValue = rulesParameter.getBasicsValue();
            //额外积分
            extendValue = rulesParameter.getExtendValue();

            //1、先查询今天是否有登录记录，如果有则退出，不获取积分
            LocalDate now = LocalDate.now();
            IntegralBehavior todayBehavior = this.integralBehaviorService.lambdaQuery()
                    .eq(IntegralBehavior::getUserId, userId)
                    .eq(IntegralBehavior::getRuleType, GainRuleType.LOGIN)
                    .eq(IntegralBehavior::getCurrentDate, now)
                    .one();
            if (todayBehavior != null) {
                //代表当日已经登录过
                return CommonPool.NUMBER_ZERO;
            }

            //2、创建今天的登录记录
            IntegralBehavior integralBehavior = new IntegralBehavior();
            integralBehavior.setUserId(userId)
                    .setRuleType(GainRuleType.LOGIN)
                    .setCurrentDate(now);

            //设置的最大连续登录天数
            Integer maxDays = IntegralConstant.INTEGRAL_BEHAVIOR_MAX_CONTINUE_DAYS;

            //3、获取昨天的登录记录
            IntegralBehavior yesterdayBehavior = this.integralBehaviorService.lambdaQuery()
                    .eq(IntegralBehavior::getUserId, userId)
                    .eq(IntegralBehavior::getRuleType, GainRuleType.LOGIN)
                    .eq(IntegralBehavior::getCurrentDate, now.minusDays(CommonPool.NUMBER_ONE))
                    .one();

            if (yesterdayBehavior == null) {
                //昨天没有登录，则今天连续登录天数为1天
                currentContinueLoginDays = CommonPool.NUMBER_ONE;
            } else {
                if (yesterdayBehavior.getContinueDays() >= maxDays) {
                    //如果昨天连续登录天数>={最大连续登录天数}，则今天的连续登录天数为 1 天
                    currentContinueLoginDays = CommonPool.NUMBER_ONE;
                } else {
                    //连续登录天数为 {昨天记录的连续登录天数 + 1}
                    currentContinueLoginDays = yesterdayBehavior.getContinueDays() + CommonPool.NUMBER_ONE;
                }
            }
            //设置今天的连续登录天数
            integralBehavior.setContinueDays(currentContinueLoginDays);
            //保存今天的登录数据
            integralBehaviorService.save(integralBehavior);
        } finally {
            lock.unlock();
        }

        //4、用户能获取的积分  积分 = {基础值} + {连续天数额外积分奖励}
        return basicsValue +
                MapUtil.getInt(extendValue, currentContinueLoginDays, 0);

    }
}
