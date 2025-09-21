package com.medusa.gruul.user.service.mq;

import com.medusa.gruul.order.api.enums.OrderRabbit;
import com.medusa.gruul.service.uaa.api.enums.UaaRabbit;
import com.medusa.gruul.user.api.enums.UserRabbit;
import com.medusa.gruul.user.api.enums.paid.PaidMemberRabbit;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.*;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


/**
 * mq
 *
 * @author xiaoq
 * Description mq
 * date 2022-09-15 10:00
 */
@Slf4j
@Configuration
@RequiredArgsConstructor
public class UserRabbitConfig {

    @Bean
    @ConditionalOnMissingBean(name = "userExchange")
    public Exchange userExchange() {
        return ExchangeBuilder
                .directExchange(UserRabbit.EXCHANGE)
                .durable(true)
                .delayed()
                .build();
    }


    @Bean
    @ConditionalOnMissingBean(name = "uaaExchange")
    public Exchange uaaExchange() {
        return ExchangeBuilder.directExchange(UaaRabbit.EXCHANGE)
                .durable(true)
                .build();
    }

    @Bean
    @ConditionalOnMissingBean(name = "paidMemberExchange")
    public Exchange paidMemberExchange() {
        return ExchangeBuilder.directExchange(PaidMemberRabbit.EXCHANGE)
                .durable(true)
                .build();
    }

    @Bean
    @ConditionalOnMissingBean(name = "orderExchange")
    public Exchange orderExchange() {
        return ExchangeBuilder.directExchange(OrderRabbit.EXCHANGE)
                .durable(true)
                .delayed().build();
    }

    /**
     * 用户提交订单
     */
    @Bean
    public Queue userSubmitOrderQueue() {
        return QueueBuilder
                .durable(UserRabbitConstant.USER_SUBMIT_ORDER_QUEUE)
                .build();
    }

    @Bean
    public Binding userSubmitOrderBinding() {
        return BindingBuilder
                .bind(userSubmitOrderQueue())
                .to(orderExchange())
                .with(OrderRabbit.ORDER_CREATED.routingKey())
                .noargs();
    }

    /**
     * 批量注册
     */
    @Bean
    public Queue userUaaBatchRegisterQueue() {
        return QueueBuilder.durable(UserRabbitConstant.USER_BATCH_REGISTER_QUEUE)
                .build();
    }

    @Bean
    public Binding userUaaBatchRegisterBinding() {
        return BindingBuilder
                .bind(userUaaBatchRegisterQueue())
                .to(uaaExchange())
                .with(UaaRabbit.BATCH_REGISTER.routingKey())
                .noargs();
    }

    /**
     * 用户储值到余额 ------------------------------------------------------------------------------
     */

    @Bean
    public Queue userRechargeBalanceAccomplishQueue() {
        return QueueBuilder.durable(UserRabbitConstant.USER_BALANCE_PAY_ACCOMPLISH_QUEUE)
                .build();
    }

    @Bean
    public Binding userRechargeBalanceAccomplishBinding() {
        return BindingBuilder
                .bind(userRechargeBalanceAccomplishQueue())
                .to(userExchange())
                .with(UserRabbit.USER_RECHARGE_BALANCE_OK.routingKey())
                .noargs();
    }

    @Bean
    public Queue userTradeAmountQueue() {
        return QueueBuilder.durable(UserRabbitConstant.USER_TRADE_AMOUNT)
                .build();
    }

    @Bean
    public Binding userTradeAmountBinding() {
        return BindingBuilder
                .bind(userTradeAmountQueue())
                .to(orderExchange())
                .with(OrderRabbit.ORDER_ACCOMPLISH.routingKey())
                .noargs();
    }


//-------------------------------------------付费会员开通-----------------------------------------------
//-------------------------------------------付费会员开通-----------------------------------------------


    @Bean
    public Queue paidMemberDredgeQueue() {
        return QueueBuilder.durable(UserRabbitConstant.USER_DREDGE_PAID_MEMBER_QUEUE)
                .build();
    }

    @Bean
    public Binding paidMemberDredgeBinding() {
        return BindingBuilder
                .bind(paidMemberDredgeQueue())
                .to(paidMemberExchange())
                .with(PaidMemberRabbit.PAID_MEMBER_DREDGE.routingKey())
                .noargs();
    }


//-------------------------------------------用户订单成功支付-----------------------------------------------
//-------------------------------------------用户订单成功支付-----------------------------------------------


    @Bean
    public Queue userOrderPaySucceedQueue() {
        return QueueBuilder.durable(UserRabbitConstant.USER_ORDER_PAY_SUCCEED_QUEUE)
                .build();
    }


    @Bean
    public Binding userOrderPaySucceedBinding() {
        return BindingBuilder
                .bind(userOrderPaySucceedQueue())
                .to(orderExchange())
                .with(OrderRabbit.ORDER_PAID_BROADCAST.routingKey())
                .noargs();
    }


//-------------------------------------------用户修改权限-----------------------------------------------
//-------------------------------------------用户修改权限-----------------------------------------------


    @Bean
    public Queue userAuthorityQueue() {
        return QueueBuilder.durable(UserRabbitConstant.USER_UPDATE_AUTHORITY_QUEUE)
                .build();
    }


    @Bean
    public Binding userAuthorityBinding() {
        return BindingBuilder
                .bind(userAuthorityQueue())
                .to(uaaExchange())
                .with(UaaRabbit.UPDATE_AUTHORITY.routingKey())
                .noargs();
    }

//-------------------------------------------用户积分变动-----------------------------------------------
//-------------------------------------------用户积分变动-----------------------------------------------


    @Bean
    public Queue userIntegralChangeQueue() {
        return QueueBuilder
                .durable(UserRabbitConstant.USER_INTEGRAL_CHANGE_QUEUE)
                .build();
    }

    @Bean
    public Binding userIntegralChangeBinding() {
        return BindingBuilder
                .bind(userIntegralChangeQueue())
                .to(userExchange())
                .with(UserRabbit.USER_INTEGRAL_CHANGE.routingKey())
                .noargs();
    }

//	用户储值流水
//	@Bean
//	public Queue userBlanceHistroyQueue() {
//		return QueueBuilder.durable(UserRabbitConstant.USER_BALANCE_HISTORY_QUEUE).build();
//	}
//	@Bean
//	public Binding userBlanceHistoryBinding(){
//    return BindingBuilder.bind(userBlanceHistroyQueue())
//    			.to(userExchange())
//    			.with(UserRabbit.USER_BALANCE_CHANGE.routingKey())
//    			.noargs();
//  }


//-------------------------------------------付费会员开通虚拟发货-----------------------------------------------
//-------------------------------------------付费会员开通虚拟发货-----------------------------------------------


    @Bean
    public Queue paidMemberDredgeVirtualDeliverQueue() {
        return QueueBuilder.durable(UserRabbitConstant.USER_DREDGE_PAID_MEMBER_VIRTUAL_DELIVER_QUEUE)
                .build();
    }

    @Bean
    public Binding paidMemberDredgeVirtualDeliverBinding() {
        return BindingBuilder
                .bind(paidMemberDredgeVirtualDeliverQueue())
                .to(userExchange())
                .with(UserRabbit.USER_VIRTUAL_DELIVER.routingKey())
                .noargs();
    }

//-------------------------------------------付费余额虚拟发货-----------------------------------------------
//-------------------------------------------付费余额虚拟发货-----------------------------------------------

    @Bean
    public Queue userBanlanceVirtualDeliverQueue() {
        return QueueBuilder.durable(UserRabbitConstant.USER_BALANCE_VIRTUAL_DELIVER_QUEUE)
                .build();
    }

    @Bean
    public Binding userBanlanceVirtualDeliverBinding() {
        return BindingBuilder
                .bind(userBanlanceVirtualDeliverQueue())
                .to(userExchange())
                .with(UserRabbit.USER_BALANCE_VIRTUAL_DELIVER.routingKey())
                .noargs();
    }

}
