package com.medusa.gruul.addon.integral.handler;

import com.medusa.gruul.addon.integral.model.enums.GainRuleType;
import com.medusa.gruul.addon.integral.mp.entity.IntegralRules;
import com.medusa.gruul.addon.integral.mp.service.IIntegralRulesService;
import com.medusa.gruul.common.model.constant.CommonPool;
import com.medusa.gruul.common.model.enums.ChangeType;
import com.medusa.gruul.common.model.message.DataChangeMessage;
import com.medusa.gruul.common.model.resp.SystemCode;
import com.medusa.gruul.common.web.handler.Handler;
import com.medusa.gruul.global.model.exception.GlobalException;
import com.medusa.gruul.user.api.enums.UserRabbit;
import com.medusa.gruul.user.api.enums.integral.GainIntegralType;
import com.medusa.gruul.user.api.model.dto.integral.IntegralChangeDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.core.annotation.AnnotationUtils;

/**
 * 抽象积分行为handler
 *
 * @author xiaoq
 * @Description AbstractIntegralBehaviorHandler.java
 * @date 2023-02-06 16:34
 */
@Slf4j
@RequiredArgsConstructor
public abstract class AbstractIntegralBehaviorHandler implements Handler<Integer> {
    private final IIntegralRulesService integralRulesService;

    private final RabbitTemplate rabbitTemplate;

    @Override
    public Integer handle(Object... params) {
        if (this.hasErrorParam(params, Long.class, GainRuleType.class)) {
            throw new GlobalException(SystemCode.PARAM_VALID_ERROR_CODE, SystemCode.PARAM_TYPE_ERROR.getMsg());
        }
        GainRuleType ruleType = cast(params[1], GainRuleType.class);
        //获取积分规则
        IntegralRules integralRules = integralRulesService.getIntegralRuleByRuleType(ruleType);

        if (integralRules == null || !integralRules.getOpen().equals(Boolean.TRUE)) {
            return CommonPool.NUMBER_ZERO;
        }

        log.debug("integralRules={}", integralRules);

        Long userId = this.cast(params[0], Long.class);
        //用户能获取的积分值
        Integer integralValue = this.integralBehaviorSave(userId, integralRules);

        //获得积分为0，无需进行接下来的处理，直接返回
        if (integralValue.equals(CommonPool.NUMBER_ZERO)) {
            return integralValue;
        }

        // 发送mq给用户 增加积分明细
        RuleTypeHandler annotation = AnnotationUtils.findAnnotation(this.getClass(), RuleTypeHandler.class);
        GainRuleType gainRuleType = annotation.value();

        //积分获取类型
        GainIntegralType gainIntegralType = switch (gainRuleType) {
            case LOGIN -> GainIntegralType.DAY_LOGIN;
            case SING_IN -> GainIntegralType.DAY_SIGN_IN;
            default -> GainIntegralType.DAY_SHARE;
        };

        //mq 发送消息 给用户加积分
        DataChangeMessage dm = new DataChangeMessage();
        dm.setUserId(userId);
        dm.setValue(integralValue.longValue());
        dm.setChangeType(ChangeType.INCREASE);
        dm.setExtendInfo(gainIntegralType.name());

        this.rabbitTemplate.convertAndSend(
                UserRabbit.USER_INTEGRAL_CHANGE.exchange(),
                UserRabbit.USER_INTEGRAL_CHANGE.routingKey(),
                new IntegralChangeDTO()
                        .setNo(null)
                        .setDataChangeMessage(dm)
        );

        return integralValue;
    }

    /**
     * 用户积分行为触发
     *
     * @param userId        用户id
     * @param integralRules 积分规则
     * @return 获取的积分值
     */
    protected abstract Integer integralBehaviorSave(Long userId, IntegralRules integralRules);
}
