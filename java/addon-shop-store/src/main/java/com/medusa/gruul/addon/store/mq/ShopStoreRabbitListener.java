package com.medusa.gruul.addon.store.mq;

import cn.hutool.json.JSONObject;
import com.medusa.gruul.addon.store.model.dto.ShopStoreOrderExtraDTO;
import com.medusa.gruul.addon.store.service.ManageShopStoreService;
import com.medusa.gruul.addon.store.service.SaveShopStoreOrderService;
import com.medusa.gruul.addon.store.service.UpdateShopStoreOrderService;
import com.medusa.gruul.common.log.annotation.Log;
import com.medusa.gruul.common.model.constant.CommonPool;
import com.medusa.gruul.common.model.enums.DistributionMode;
import com.medusa.gruul.order.api.model.OrderCompletedDTO;
import com.medusa.gruul.order.api.model.OrderPackageKeyDTO;
import com.medusa.gruul.order.api.model.OrderPaidBroadcastDTO;
import com.medusa.gruul.shop.api.model.vo.ShopInfoVO;
import com.rabbitmq.client.Channel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 店铺门店RabbitListener
 *
 * @author xiaoq
 * @Description 店铺门店RabbitListener
 * @date 2023-03-15 21:43
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class ShopStoreRabbitListener {

    private final ManageShopStoreService manageShopStoreService;

    private final SaveShopStoreOrderService saveShopStoreOrderService;

    private final UpdateShopStoreOrderService updateShopStoreOrderService;


    @Log("店铺信息改动")
    @RabbitListener(queues = ShopStoreQueueNames.SHOP_INFO_UPDATE_QUEUE)
    public void shopInfoUpdate(ShopInfoVO shopInfo, Channel channel, Message message) throws IOException {
        manageShopStoreService.updateShopName(shopInfo);
        // 确认消息
        channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
    }


    @Log("订单支付成功")
    @RabbitListener(queues = ShopStoreQueueNames.STORE_ORDER_PAY_SUCCEED_QUEUE)
    public void storeOrderPaySucceedQueue(OrderPaidBroadcastDTO orderPaidBroadcast, Channel channel, Message message) throws IOException {
        log.debug(orderPaidBroadcast.toString());
        JSONObject orderExtra;
        if (DistributionMode.SHOP_STORE != orderPaidBroadcast.getDistributionMode() || (orderExtra = orderPaidBroadcast.getExtra()) == null) {
            log.debug("当前订单非门店订单");
            // 确认消息
            channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
            return;
        }

        Set<Long> shopIds = orderPaidBroadcast.getShopPayAmounts()
                .stream()
                .map(OrderPaidBroadcastDTO.ShopPayAmountDTO::getShopId)
                .collect(Collectors.toSet());
        if (shopIds.size() > CommonPool.NUMBER_ONE) {
            // 确认消息
            channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
            return;
        }
        saveShopStoreOrderService.saveStoreOrder(
                orderPaidBroadcast,
                orderExtra.toBean(ShopStoreOrderExtraDTO.class)
        );
        // 确认消息
        channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
    }


    @Log("订单完成")
    @RabbitListener(queues = ShopStoreQueueNames.STORE_ORDER_COMPLETE_QUEUE)
    public void storeOrderComplete(OrderCompletedDTO orderCompleted, Channel channel, Message message) throws IOException {
        if (orderCompleted.getExtra() == null) {
            // 确认消息
            channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
            return;
        }

        Optional<ShopStoreOrderExtraDTO> shopStoreOrderExtra = Optional.ofNullable(orderCompleted.getExtra().toBean(ShopStoreOrderExtraDTO.class));

        shopStoreOrderExtra.filter(extra -> extra.getDistributionMode() == DistributionMode.SHOP_STORE)
                .ifPresent(extra -> updateShopStoreOrderService.updateStoreOrder(orderCompleted, extra));

        // 确认消息
        channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
    }


    @Log("订单发货")
    @RabbitListener(queues = ShopStoreQueueNames.STORE_ORDER_DELIVER_GOODS_QUEUE)
    public void storeOrderDeliverGoods(OrderPackageKeyDTO orderPackageKey, Channel channel, Message message) throws IOException {
        if (orderPackageKey.getExtra() == null) {
            // 确认消息
            channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
            return;
        }
        Optional<ShopStoreOrderExtraDTO> shopStoreOrderExtra = Optional.ofNullable(orderPackageKey.getExtra().toBean(ShopStoreOrderExtraDTO.class));

        shopStoreOrderExtra.filter(extra -> extra.getDistributionMode() == DistributionMode.SHOP_STORE)
                .ifPresent(extra -> updateShopStoreOrderService.storeOrderDeliverGoods(orderPackageKey, extra));

        channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
    }


}
