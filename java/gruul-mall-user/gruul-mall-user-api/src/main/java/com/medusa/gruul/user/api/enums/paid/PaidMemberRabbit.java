package com.medusa.gruul.user.api.enums.paid;

import com.medusa.gruul.common.model.enums.RabbitParent;
import lombok.RequiredArgsConstructor;

/**
 * 付费会员mq配置
 *
 * @author xiaoq
 * @Description PaidMemberRabbit.java
 * @date 2022-11-17 16:36
 */
@RequiredArgsConstructor
public enum PaidMemberRabbit implements RabbitParent {

    /**
     * 付费会员开通
     */
    PAID_MEMBER_DREDGE("paid.member.dredge"),;

    /**
     * 路由key
     */
    private final String routingKey;

    public static final String EXCHANGE = "paid.member.direct";

    @Override
    public String exchange() {
        return PaidMemberRabbit.EXCHANGE;
    }

    @Override
    public String routingKey() {
        return routingKey;
    }


}