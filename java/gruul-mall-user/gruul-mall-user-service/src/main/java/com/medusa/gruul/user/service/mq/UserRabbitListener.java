package com.medusa.gruul.user.service.mq;

import com.alibaba.fastjson2.JSON;
import com.medusa.gruul.common.log.annotation.Log;
import com.medusa.gruul.order.api.model.OrderCompletedDTO;
import com.medusa.gruul.order.api.model.OrderCreatedDTO;
import com.medusa.gruul.order.api.model.OrderPaidBroadcastDTO;
import com.medusa.gruul.payment.api.model.dto.PayNotifyResultDTO;
import com.medusa.gruul.payment.api.model.dto.PaymentInfoDTO;
import com.medusa.gruul.service.uaa.api.dto.UserAuthorityDTO;
import com.medusa.gruul.service.uaa.api.dto.UserRegisterDataDTO;
import com.medusa.gruul.user.api.model.dto.integral.IntegralChangeDTO;
import com.medusa.gruul.user.api.model.dto.paid.PaidMemberDealDTO;
import com.medusa.gruul.user.service.model.dto.UserVirtualDeliverDTO;
import com.medusa.gruul.user.service.service.*;
import com.rabbitmq.client.Channel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;

/**
 * @author xiaoq
 * @Description
 * @date 2022-09-15 11:13
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class UserRabbitListener {

    private final UserManageService userManageService;

    private final MemberCardService memberCardService;

    private final UserOrderDetailService userOrderDetailService;

    private final UserIntegralService userIntegralService;

    private final ShopUserAccountManageService shopUserAccountManageService;

    private final MiniDeliverService miniDeliverService;


    @Log("支付回调完成")
    @RabbitListener(queues = UserRabbitConstant.USER_BALANCE_PAY_ACCOMPLISH_QUEUE)
    public void userPayResult(PayNotifyResultDTO payNotifyResultDTO, Channel channel, Message message) throws IOException {
        log.debug("支付队列".concat(payNotifyResultDTO.toString()));
        userManageService.userBalanceRecharge(payNotifyResultDTO);
        // 确认消息
        channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);

    }


    @Log("付费会员开通")
    @RabbitListener(queues = UserRabbitConstant.USER_DREDGE_PAID_MEMBER_QUEUE)
    public void userPayPaidMember(PayNotifyResultDTO paidNotify, Channel channel, Message message) throws IOException {
        log.debug("付费会员开通 {}", paidNotify);

        PaymentInfoDTO businessParams = paidNotify.getBusinessParams();
        PaidMemberDealDTO paidMemberDeal = JSON.parseObject(businessParams.getAttach(), PaidMemberDealDTO.class);
        String transactionId = paidNotify.getShopIdTransactionMap().values().iterator().next().getTransactionId();
        memberCardService.userPayPaidMember(transactionId, paidMemberDeal, businessParams);

        // 确认消息
        channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
    }


    @Log("订单完成更新用户交易总额")
    @RabbitListener(queues = UserRabbitConstant.USER_TRADE_AMOUNT)
    public void updateUserTradeTotalAmount(OrderCompletedDTO orderCompletedDTO, Channel channel, Message message) throws IOException {
        log.debug("订单完成更新用户:{} 交易总额:{} ", orderCompletedDTO.getUserId(), orderCompletedDTO.getTotalAmount());
        userManageService.updateUserTradeTotalAmount(orderCompletedDTO);
        // 确认消息
        channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
    }


    @Log("用户订单支付成功")
    @RabbitListener(queues = UserRabbitConstant.USER_ORDER_PAY_SUCCEED_QUEUE)
    public void userOrderPaySucceed(OrderPaidBroadcastDTO orderPaidBroadcast, Channel channel, Message message) throws IOException {
        log.debug("用户订单支付成功 ".concat(orderPaidBroadcast.toString()));
        userOrderDetailService.saveOrderDetail(orderPaidBroadcast);
        channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
    }


    @Log("用户权限变动")
    @RabbitListener(queues = UserRabbitConstant.USER_UPDATE_AUTHORITY_QUEUE)
    public void editMemberAuthority(UserAuthorityDTO userAuthority, Channel channel, Message message) throws IOException {
        log.debug("修改权限用户:{} 修改权限状态:{} ", userAuthority.getUserIds(), userAuthority.getRoles());
        userManageService.editMemberAuthority(userAuthority);
        // 确认消息
        channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
    }


    @Log("用户积分变动")
    @RabbitListener(queues = UserRabbitConstant.USER_INTEGRAL_CHANGE_QUEUE)
    public void userIntegralChange(IntegralChangeDTO integralChangeDTO, Channel channel, Message message) throws IOException {

        //处理积分变化消息
        this.userIntegralService.handlerIntegralChangeMessage(integralChangeDTO);

        // 确认消息
        channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
    }

    @Log("用户批量注册")
    @RabbitListener(queues = UserRabbitConstant.USER_BATCH_REGISTER_QUEUE)
    public void userBatchRegister(List<UserRegisterDataDTO> userDataList, Channel channel, Message message) throws IOException {
        log.debug("用户批量注册：userData：{}", userDataList);
        userManageService.userBatchRegister(userDataList);
        // 确认消息
        channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
    }

    @Log("用户提交订单")
    @RabbitListener(queues = UserRabbitConstant.USER_SUBMIT_ORDER_QUEUE)
    public void addShopUserAccount(OrderCreatedDTO orderCreated, Channel channel, Message message) throws IOException {
        log.debug("用户提交订单：orderCreated：{}", orderCreated);
        shopUserAccountManageService.addShopUserAccount(orderCreated);
        // 确认消息
        channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
    }


    @Log("用户付费会员开通|续费 虚拟发货")
    @RabbitListener(queues = UserRabbitConstant.USER_DREDGE_PAID_MEMBER_VIRTUAL_DELIVER_QUEUE)
    public void userPayPaidMemberVirtualDeliver(UserVirtualDeliverDTO userVirtualDeliver, Channel channel, Message message) throws IOException {
        miniDeliverService.miniDeliver(
                userVirtualDeliver.getTransactionId(),
                userVirtualDeliver.getOpenId(),
                userVirtualDeliver.getDesc(),
                userVirtualDeliver.getPlatform()
        );
        // 确认消息
        channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
    }


    @Log("用户余额储值 虚拟发货")
    @RabbitListener(queues = UserRabbitConstant.USER_BALANCE_VIRTUAL_DELIVER_QUEUE)
    public void userBalanceVirtualDeliver(UserVirtualDeliverDTO userVirtualDeliver, Channel channel, Message message) throws IOException {
        miniDeliverService.miniDeliver(
                userVirtualDeliver.getTransactionId(),
                userVirtualDeliver.getOpenId(),
                userVirtualDeliver.getDesc(),
                userVirtualDeliver.getPlatform()
        );
        // 确认消息
        channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
    }


}
