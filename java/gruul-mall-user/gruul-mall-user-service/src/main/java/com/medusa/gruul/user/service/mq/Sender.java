package com.medusa.gruul.user.service.mq;

import com.medusa.gruul.common.log.annotation.Log;
import com.medusa.gruul.user.api.enums.UserRabbit;
import com.medusa.gruul.user.api.model.dto.BalanceChangeDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

/**
 * @author xiaoq
 * @Description 用户生成队列
 * @date 2022-10-08 09:52
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class Sender {

    private final RabbitTemplate rabbitTemplate;

    /**
     * 用户余额变化 通知支付服务生成相关明细
     *
     * @param balanceChangeMessage 余额变化消息体
     */
    @Log("用户余额变化 通知支付服务生成相关明细")
    public void sendPaymentChangeMessage(BalanceChangeDTO balanceChangeMessage) {
        log.debug("sendPaymentChangeMessage: ".concat(balanceChangeMessage.toString()));
        rabbitTemplate.convertAndSend(UserRabbit.USER_BALANCE_CHANGE.exchange(), UserRabbit.USER_BALANCE_CHANGE.routingKey(), balanceChangeMessage);
    }

}
