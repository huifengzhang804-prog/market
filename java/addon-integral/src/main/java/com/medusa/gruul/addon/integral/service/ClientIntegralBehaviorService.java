package com.medusa.gruul.addon.integral.service;

import com.medusa.gruul.addon.integral.model.enums.GainRuleType;
import com.medusa.gruul.addon.integral.model.vo.IntegralBehaviorVO;
import com.medusa.gruul.common.security.model.bean.SecureUser;
import com.medusa.gruul.order.api.model.OrderCompletedDTO;

/**
 * 客户端积分行为数据层
 *
 * @author xiaoq
 * @Description ClientIntegralBehaviorService.java
 * @date 2023-02-06 16:01
 */
public interface ClientIntegralBehaviorService {
    /**
     * 积分行为触发
     *
     * @param ruleType   积分类型
     * @param secureUser 用户
     * @return 获取的积分值
     */
    Integer saveIntegralBehavior(GainRuleType ruleType, SecureUser secureUser);

    /**
     * 获取积分行为的连续天数  连续  签到/登录  的天数
     *
     * @param ruleType   积分类型
     * @param secureUser 用户信息
     * @return 行为连续信息
     */
    IntegralBehaviorVO getIntegralBehaviorContinueDays(GainRuleType ruleType, SecureUser secureUser);

    /**
     * 消费所得订单处理
     *
     * @param orderCompleted 订单信息
     */
    void userOrderFinishBehavior(OrderCompletedDTO orderCompleted);
}
