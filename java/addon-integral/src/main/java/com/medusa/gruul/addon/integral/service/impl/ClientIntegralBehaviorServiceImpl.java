package com.medusa.gruul.addon.integral.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.medusa.gruul.addon.integral.handler.RuleTypeHandler;
import com.medusa.gruul.addon.integral.model.constant.IntegralConstant;
import com.medusa.gruul.addon.integral.model.dto.ConsumeJsonDTO;
import com.medusa.gruul.addon.integral.model.enums.GainRuleType;
import com.medusa.gruul.addon.integral.model.vo.IntegralBehaviorVO;
import com.medusa.gruul.addon.integral.mp.entity.IntegralBehavior;
import com.medusa.gruul.addon.integral.mp.entity.IntegralConsumeOrder;
import com.medusa.gruul.addon.integral.mp.entity.IntegralRules;
import com.medusa.gruul.addon.integral.mp.service.IIntegralBehaviorService;
import com.medusa.gruul.addon.integral.mp.service.IIntegralConsumeOrderService;
import com.medusa.gruul.addon.integral.mp.service.IIntegralRulesService;
import com.medusa.gruul.addon.integral.service.ClientIntegralBehaviorService;
import com.medusa.gruul.common.model.constant.CommonPool;
import com.medusa.gruul.common.model.enums.ChangeType;
import com.medusa.gruul.common.model.message.DataChangeMessage;
import com.medusa.gruul.common.model.resp.SystemCode;
import com.medusa.gruul.common.security.model.bean.SecureUser;
import com.medusa.gruul.common.web.handler.Handler;
import com.medusa.gruul.common.web.util.SpringUtils;
import com.medusa.gruul.global.model.exception.GlobalException;
import com.medusa.gruul.order.api.model.OrderCompletedDTO;
import com.medusa.gruul.user.api.enums.ConsumeGrowthValueType;
import com.medusa.gruul.user.api.enums.UserRabbit;
import com.medusa.gruul.user.api.enums.integral.GainIntegralType;
import com.medusa.gruul.user.api.model.dto.integral.IntegralChangeDTO;
import io.vavr.Tuple;
import io.vavr.Tuple4;
import io.vavr.control.Option;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Optional;

/**
 * 客户端积分行为数据实现层
 *
 * @author xiaoq
 * @Description ClientIntegralBehaviorServiceImpl.java
 * @date 2023-02-06 16:02
 */
@Service
@RequiredArgsConstructor
public class ClientIntegralBehaviorServiceImpl implements ClientIntegralBehaviorService {

    private final IIntegralRulesService integralRulesService;

    private final IIntegralBehaviorService integralBehaviorService;
    private final IIntegralConsumeOrderService integralConsumeOrderService;
    private final RabbitTemplate rabbitTemplate;

    @Override
    public Integer saveIntegralBehavior(GainRuleType ruleType, SecureUser secureUser) {
        Handler<Integer> handler = SpringUtils.getBean(RuleTypeHandler.class, ruleType);
        if (handler == null) {
            throw new GlobalException(SystemCode.PARAM_VALID_ERROR_CODE, "不支持当前处理器");
        }

        return handler.handle(secureUser.getId(), ruleType);
    }

    /**
     * 分享：
     * 只需返回今日是否已分享
     * 签到或登录：
     * 1、查询今天是否有记录，有则返回今天的连续天数
     * 2、查看昨天是否有记录，没有返回0天，有则返回昨天的连续天数，如果昨天连续天数>=最大连续天数 返回0
     */
    @Override
    public IntegralBehaviorVO getIntegralBehaviorContinueDays(GainRuleType ruleType, SecureUser secureUser) {

        //积分规则
        IntegralRules integralRules = this.integralRulesService.getIntegralRuleByRuleType(ruleType);

        if (integralRules == null || !integralRules.getOpen().equals(Boolean.TRUE)) {
            return new IntegralBehaviorVO()
                    .setContinueDays(CommonPool.NUMBER_ZERO)
                    .setTodayState(Boolean.FALSE);
        }

        LocalDate now = LocalDate.now();

        //分享 直接返回今日是否分享即可
        if (ruleType == GainRuleType.SHARE) {

            //1、查询今天记录
            IntegralBehavior todayBehavior = this.integralBehaviorService.lambdaQuery()
                    .eq(IntegralBehavior::getUserId, secureUser.getId())
                    .eq(IntegralBehavior::getRuleType, ruleType)
                    .eq(IntegralBehavior::getCurrentDate, now)
                    .one();
            return new IntegralBehaviorVO()
                    .setContinueDays(CommonPool.NUMBER_ZERO)
                    .setTodayState(todayBehavior != null);
        }

        //积分行为最大连续天数
        Integer maxContinueDays = IntegralConstant.INTEGRAL_BEHAVIOR_MAX_CONTINUE_DAYS;

        //1、查询今天记录
        IntegralBehavior todayBehavior = this.integralBehaviorService.lambdaQuery()
                .eq(IntegralBehavior::getUserId, secureUser.getId())
                .eq(IntegralBehavior::getRuleType, ruleType)
                .eq(IntegralBehavior::getCurrentDate, now)
                .one();
        if (todayBehavior != null) {
            //返回今天记录的连续天数
            return new IntegralBehaviorVO()
                    .setContinueDays(todayBehavior.getContinueDays())
                    .setTodayState(Boolean.TRUE);
        }

        //2、查看昨天是否有记录
        IntegralBehavior yesterdayBehavior = this.integralBehaviorService.lambdaQuery()
                .eq(IntegralBehavior::getUserId, secureUser.getId())
                .eq(IntegralBehavior::getRuleType, ruleType)
                .eq(IntegralBehavior::getCurrentDate, now.minusDays(CommonPool.NUMBER_ONE))
                .one();

        IntegralBehaviorVO vo = new IntegralBehaviorVO();
        vo.setTodayState(Boolean.FALSE);
        if (yesterdayBehavior == null) {
            //昨天没有记录，连续天数为0天
            vo.setContinueDays(CommonPool.NUMBER_ZERO);
        } else {
            if (yesterdayBehavior.getContinueDays() >= maxContinueDays) {
                vo.setContinueDays(CommonPool.NUMBER_ZERO);
            } else {
                vo.setContinueDays(yesterdayBehavior.getContinueDays());
            }
        }
        return vo;
    }

    /**
     * 积分消费获得 及时奖励
     *
     * @param orderCompleted 订单信息
     */
    @Override
    public void userOrderFinishBehavior(OrderCompletedDTO orderCompleted) {
        Long totalAmount = orderCompleted.getTotalAmount();
        if (totalAmount == 0) {
            return;
        }
        //如果没开启积分获得规则，直接返回
        IntegralRules integralRule = integralRulesService.getIntegralRuleByRuleType(GainRuleType.CONSUME);
        if (integralRule == null || !integralRule.getOpen()) {
            return;
        }
        Optional<ConsumeJsonDTO> optional = integralRule.getRulesParameter()
                .getConsumeJson()
                .stream()
                .filter(ConsumeJsonDTO::getIsSelected)
                .findFirst();
        if (optional.isEmpty()) {
            return;
        }
        IntegralConsumeOrder consumeOrder = integralConsumeOrderService.lambdaQuery()
                .eq(IntegralConsumeOrder::getUserId, orderCompleted.getUserId())
                .oneOpt()
                .orElseGet(IntegralConsumeOrder::new);
        Tuple4<Boolean, Long, Boolean, Long> growthValue = getGrowthValue(consumeOrder, optional.get(), orderCompleted);
        handlerIntegralConsumeOrder(consumeOrder, growthValue);
        //是否是添加数据
        boolean flag;
        if (growthValue._1) {
            consumeOrder.setUserId(orderCompleted.getUserId());
            flag = integralConsumeOrderService.save(consumeOrder);
        } else {
            flag = integralConsumeOrderService.updateById(consumeOrder);
        }
        if (!flag) {
            throw new GlobalException(SystemCode.PARAM_VALID_ERROR_CODE, "用户订单记录修改失败");
        }
        if (growthValue._2 > 0) {
            //mq 发送消息 给用户加积分
            DataChangeMessage dm = new DataChangeMessage();
            dm.setUserId(orderCompleted.getUserId());
            dm.setValue(growthValue._2);
            dm.setChangeType(ChangeType.INCREASE);
            dm.setExtendInfo(GainIntegralType.INTEGRAL_CONSUME.name());

            this.rabbitTemplate.convertAndSend(
                    UserRabbit.USER_INTEGRAL_CHANGE.exchange(),
                    UserRabbit.USER_INTEGRAL_CHANGE.routingKey(),
                    new IntegralChangeDTO()
                            .setNo(null)
                            .setDataChangeMessage(dm)
            );
        }
    }

    private void handlerIntegralConsumeOrder(IntegralConsumeOrder consumeOrder, Tuple4<Boolean, Long, Boolean, Long> growthValueTwo) {
        if (growthValueTwo._3) {
            consumeOrder.setDealMoneyCount(Option.of(consumeOrder.getDealMoneyCount()).getOrElse(0L) - growthValueTwo._4);
        } else {
            consumeOrder.setDealQuantityCount(Option.of(consumeOrder.getDealQuantityCount()).getOrElse(0) - growthValueTwo._4.intValue());
        }
    }

    /**
     * @param consumeOrder   消费订单记录
     * @param consumeJson    积分规则
     * @param orderCompleted 订单信息
     * @return 是否有订单消费记录、积分奖励值、积分奖励类型、订单消费记录是添加或减少的数据
     */
    private Tuple4<Boolean, Long, Boolean, Long> getGrowthValue(IntegralConsumeOrder consumeOrder, ConsumeJsonDTO consumeJson, OrderCompletedDTO orderCompleted) {
        boolean isNew = BeanUtil.isEmpty(consumeOrder);
        //获得的积分
        long growthValue = 0;
        //余数
        long beyond = 0;
        ConsumeGrowthValueType consumeGrowthValueType = consumeJson.getConsumeGrowthValueType();

        if (ConsumeGrowthValueType.ORDER_AMOUNT == consumeGrowthValueType) {
            long total = Option.of(consumeOrder.getDealMoneyCount()).getOrElse(0L) + orderCompleted.getTotalAmount();
            growthValue = (total / (consumeJson.getOrderQuantityAndAmount() * CommonPool.UNIT_CONVERSION_TEN_THOUSAND)) * consumeJson.getPresentedGrowthValue();
            beyond = total % (consumeJson.getOrderQuantityAndAmount() * CommonPool.UNIT_CONVERSION_TEN_THOUSAND);
        } else if (ConsumeGrowthValueType.ORDER_QUANTITY == consumeGrowthValueType) {
            long total = Option.of(consumeOrder.getDealQuantityCount()).getOrElse(0) + CommonPool.NUMBER_ONE;
            growthValue = (total / consumeJson.getOrderQuantityAndAmount()) * consumeJson.getPresentedGrowthValue();
            beyond = total % consumeJson.getOrderQuantityAndAmount();
        }
        return Tuple.of(isNew, growthValue, ConsumeGrowthValueType.ORDER_AMOUNT == consumeGrowthValueType, Option.of(consumeOrder.getDealMoneyCount()).getOrElse(0L) - beyond);
    }
}
