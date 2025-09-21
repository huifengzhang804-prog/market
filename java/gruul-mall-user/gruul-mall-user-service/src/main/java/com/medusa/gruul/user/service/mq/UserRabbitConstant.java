package com.medusa.gruul.user.service.mq;

/**
 * @author: WuDi
 * @date: 2022/9/28
 */
public interface UserRabbitConstant {

    /**
     * 用户支付消息队列
     */
    String USER_BALANCE_PAY_ACCOMPLISH_QUEUE = "gruul.user.balance.pay.accomplish.queue";

    /**
     * 用户交易总额队列
     */
    String USER_TRADE_AMOUNT = "user.trade.amount";


    /**
     * 用户开通付费会员
     */
    String USER_DREDGE_PAID_MEMBER_QUEUE = "user.dredge.paid.member.queue";

    /**
     * 用户成功支付订单信息
     */
    String USER_ORDER_PAY_SUCCEED_QUEUE = "user.order.pay.succeed.queue";


    /**
     * 用户提交订单队列
     */
    String USER_SUBMIT_ORDER_QUEUE = "user.submit.order.queue";


    /**
     * 用户更新权限
     */
    String USER_UPDATE_AUTHORITY_QUEUE = "user.update.authority.queue";

    /**
     * 用户积分变动
     */
    String USER_INTEGRAL_CHANGE_QUEUE = "user.integral.change.queue";

    /**
     * 用户批量注册
     */
    String USER_BATCH_REGISTER_QUEUE = "user.batch.register.queue";


    /**
     * 用户会员开通|续费  虚拟发货
     */
    String USER_DREDGE_PAID_MEMBER_VIRTUAL_DELIVER_QUEUE = "user.dredge.paid.member.virtual.deliver.queue";


    /**
     * 用户余额虚拟发货队列
     */
    String USER_BALANCE_VIRTUAL_DELIVER_QUEUE = "user.balance.virtual.deliver.queue";


}
