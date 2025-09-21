package com.medusa.gruul.order.service.mq;

import cn.hutool.core.collection.CollUtil;
import com.medusa.gruul.common.log.annotation.Log;
import com.medusa.gruul.common.model.base.ProductSkuKey;
import com.medusa.gruul.common.model.constant.CommonPool;
import com.medusa.gruul.common.model.enums.DistributionMode;
import com.medusa.gruul.common.mp.model.TenantShop;
import com.medusa.gruul.common.mq.rabbit.RabbitConstant;
import com.medusa.gruul.global.config.Global;
import com.medusa.gruul.global.model.exception.GlobalException;
import com.medusa.gruul.order.api.entity.ShopOrderItem;
import com.medusa.gruul.order.api.enums.ItemStatus;
import com.medusa.gruul.order.api.enums.PackageStatus;
import com.medusa.gruul.order.api.model.OrderPackageKeyDTO;
import com.medusa.gruul.order.api.model.OrderPaidBroadcastDTO;
import com.medusa.gruul.order.service.model.bo.OrderDetailInfoBO;
import com.medusa.gruul.order.service.model.dto.OrderEvaluateDTO;
import com.medusa.gruul.order.service.model.dto.OrderEvaluateItemDTO;
import com.medusa.gruul.order.service.modules.deliver.service.DeliverService;
import com.medusa.gruul.order.service.modules.evaluate.service.EvaluateService;
import com.medusa.gruul.order.service.modules.order.service.CloseOrderService;
import com.medusa.gruul.order.service.modules.order.service.CreateOrderService;
import com.medusa.gruul.order.service.modules.order.service.OrderPayService;
import com.medusa.gruul.order.service.modules.printer.model.dto.OrderPrintDTO;
import com.medusa.gruul.order.service.modules.printer.model.enums.PrintMode;
import com.medusa.gruul.order.service.modules.printer.model.enums.PrintScene;
import com.medusa.gruul.order.service.modules.printer.service.PrintTaskService;
import com.medusa.gruul.order.service.mp.service.IShopOrderItemService;
import com.medusa.gruul.order.service.mq.service.OrderRabbitService;
import com.medusa.gruul.payment.api.model.dto.PayNotifyResultDTO;
import com.rabbitmq.client.Channel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.support.converter.SmartMessageConverter;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


/**
 * @author 张治保
 * date 2022/6/9
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class OrderRabbitListener {

    private final CreateOrderService createOrderService;
    private final CloseOrderService closeOrderService;
    private final OrderPayService orderPayService;
    private final DeliverService deliverService;
    private final EvaluateService evaluateService;
    private final PrintTaskService printTaskService;
    private final IShopOrderItemService shopOrderItemService;
    private final SmartMessageConverter smartMessageConverter;
    private final OrderRabbitService orderRabbitService;

    /**
     * 把订单数据刷新到数据库
     */
    @RabbitListener(queues = OrderQueueNames.ORDER_CREATE_FLUSH_DATA2DB_QUEUE, containerFactory = RabbitConstant.BATCH_LISTENER_CONTAINER_FACTORY, executor = Global.GLOBAL_TASK_EXECUTOR)
    public void orderCreate(List<Message> messages, Channel channel) throws IOException {
        createOrderService.saveOrder2DbBatch(
                messages.stream().
                        map(message -> (OrderDetailInfoBO) smartMessageConverter.fromMessage(message))
                        .collect(Collectors.toList())
        );
        //批量 确认消息
        for (Message message : messages) {
            channel.basicAck(message.getMessageProperties().getDeliveryTag(), true);
        }
    }

    /**
     * 支付超时取消订单
     */
    @RabbitListener(queues = OrderQueueNames.ORDER_AUTO_PAID_TIMEOUT_CLOSE_QUEUE)
    public void orderCancelNotPay(String orderNo, Channel channel, Message message) throws IOException {
        closeOrderService.closeOrderPaidTimeout(orderNo);
        channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
    }

    /**
     * 订单支付结果
     */
    @RabbitListener(queues = OrderQueueNames.ORDER_PAID_CALLBACK_QUEUE)
    public void orderPaidResult(PayNotifyResultDTO payNotifyResult, Channel channel, Message message) throws IOException {
        orderPayService.payNotify(payNotifyResult);
        channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
    }

    /**
     * 自动确认收货
     */
    @Log("自动确认收货")
    @RabbitListener(queues = OrderQueueNames.ORDER_AUTO_CONFIRM_RECEIPT_QUEUE)
    public void orderAutoConfirmReceipt(OrderPackageKeyDTO orderPackageKey, Channel channel, Message message) throws IOException {
        deliverService.packageConfirm(true, orderPackageKey, PackageStatus.SYSTEM_WAITING_FOR_COMMENT);
        channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
    }

    /**
     * 自动好评
     */
    @Log("系统自动好评")
    @RabbitListener(queues = OrderQueueNames.ORDER_AUTO_COMMENT_QUEUE)
    public void orderAutoComment(OrderPackageKeyDTO orderPackageKey, Channel channel, Message message) throws IOException {
        //检查是否有待评价商品
        List<ShopOrderItem> items = TenantShop.disable(() -> shopOrderItemService.lambdaQuery()
                .select(ShopOrderItem::getProductId, ShopOrderItem::getSkuId, ShopOrderItem::getSpecs)
                .eq(ShopOrderItem::getOrderNo, orderPackageKey.getOrderNo())
                .eq(ShopOrderItem::getShopId, orderPackageKey.getShopId())
                .eq(ShopOrderItem::getStatus, ItemStatus.OK)
                .in(ShopOrderItem::getPackageStatus, PackageStatus.SYSTEM_WAITING_FOR_COMMENT, PackageStatus.BUYER_WAITING_FOR_COMMENT)
                .list());
        if (CollUtil.isEmpty(items)) {
            channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
            return;
        }
        String comment = "系统自动好评";
        //商品分组
        //商品sku分组 相同规格只评价一次
        Map<ProductSkuKey, Map<String, List<ShopOrderItem>>> itemMap = items.stream()
                .collect(
                        Collectors.groupingBy(
                                item -> new ProductSkuKey().setProductId(item.getProductId()).setSkuId(item.getSkuId()),
                                Collectors.groupingBy(item -> OrderEvaluateItemDTO.specsToString(item.getSpecs()))
                        )
                );
        OrderEvaluateDTO orderEvaluate = new OrderEvaluateDTO()
                .setOrderNo(orderPackageKey.getOrderNo())
                .setShopId(orderPackageKey.getShopId())
                .setItems(
                        //渲染评价数据
                        itemMap.entrySet()
                                .stream()
                                .flatMap(entry -> {
                                            ProductSkuKey key = entry.getKey();
                                            return entry.getValue()
                                                    .values()
                                                    .stream()
                                                    .map(
                                                            shopOrderItems -> new OrderEvaluateItemDTO()
                                                                    .setKey(key)
                                                                    .setSpecs(shopOrderItems.get(CommonPool.NUMBER_ZERO).getSpecs())
                                                                    .setComment(comment)
                                                                    .setRate(CommonPool.NUMBER_FIVE)
                                                    );
                                        }
                                ).toList()
                );
        try {
            evaluateService.evaluate(Boolean.TRUE, orderPackageKey.getBuyerId(), orderEvaluate);
        } catch (GlobalException ignored) {
        }
        channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
    }

    @Log("小程序订单发货录入")
    @RabbitListener(queues = OrderQueueNames.MINI_APP_ORDER_DELIVERY_QUEUE)
    public void miniAppOrderDeliver(OrderPackageKeyDTO orderPackageKey, Channel channel, Message message) throws Exception {
        deliverService.miniAppOrderDeliver(orderPackageKey);
        channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
    }

    @Log("小程序订单退货发货录入")
    @RabbitListener(queues = OrderQueueNames.MINI_APP_ORDER_RETURN_GOODS_DELIVERY_QUEUE)
    public void miniAppOrderReturnGoodsDeliver(ShopOrderItem shopOrderItem, Channel channel, Message message) throws Exception {
        deliverService.miniAppOrderReturnGoodsDeliver(shopOrderItem);
        channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
    }

    /**
     * 订单已支付后 的处理，如 打印小票等
     *
     * @param broadcast 订单支付广播参数
     * @param channel   rabbit channel
     * @param message   rabbit msg
     */
    @RabbitListener(queues = OrderQueueNames.ORDER_PAID_QUEUE)
    public void orderOrderPaid(OrderPaidBroadcastDTO broadcast, Channel channel, Message message) throws IOException {
        DistributionMode distributionMode = broadcast.getDistributionMode();
        //同城配送
        boolean isIntraCity = DistributionMode.INTRA_CITY_DISTRIBUTION == distributionMode;
        //门店自提
        if (isIntraCity || DistributionMode.SHOP_STORE == distributionMode) {
            orderRabbitService.sendPrintOrder(
                    new OrderPrintDTO()
                            .setOrderNo(broadcast.getOrderNo())
                            .setMode(isIntraCity ? PrintMode.INTRA_CITY : PrintMode.STORE_PICKUP_SELF)
                            .setScene(PrintScene.AUTO_PAID)
            );
        }
        channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
    }

    /**
     * 打印订单小票
     *
     * @param param   打印订单小票参数
     * @param channel rabbit channel
     * @param message rabbit msg
     */
    @RabbitListener(queues = OrderQueueNames.ORDER_PRINT_QUEUE)
    public void orderPrint(OrderPrintDTO param, Channel channel, Message message) throws IOException {
        printTaskService.printOrder(param);
        channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
    }




}


