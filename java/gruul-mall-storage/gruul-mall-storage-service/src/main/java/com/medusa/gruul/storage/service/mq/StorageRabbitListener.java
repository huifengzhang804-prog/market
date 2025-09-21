package com.medusa.gruul.storage.service.mq;

import cn.hutool.core.collection.CollUtil;
import com.medusa.gruul.common.log.annotation.Log;
import com.medusa.gruul.common.model.base.ActivityShopProductSkuKey;
import com.medusa.gruul.common.model.enums.OrderType;
import com.medusa.gruul.common.mq.rabbit.RabbitConstant;
import com.medusa.gruul.global.config.Global;
import com.medusa.gruul.goods.api.model.dto.ProductDeleteDTO;
import com.medusa.gruul.order.api.enums.OrderCloseType;
import com.medusa.gruul.order.api.pojo.OrderInfo;
import com.medusa.gruul.storage.api.bo.StSvBo;
import com.medusa.gruul.storage.api.enums.StockChangeType;
import com.medusa.gruul.storage.service.model.bo.UpdateStockOrder;
import com.medusa.gruul.storage.service.service.SkuStockService;
import com.medusa.gruul.storage.service.util.StorageUtil;
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
public class StorageRabbitListener {

    private final SkuStockService skuStockService;
    private final SmartMessageConverter smartMessageConverter;

    /**
     * 更新sku库存
     */
    @SuppressWarnings("unchecked")
    @RabbitListener(queues = StorageQueueNames.UPDATE_SKU_STOCK_QUEUE, containerFactory = RabbitConstant.BATCH_LISTENER_CONTAINER_FACTORY, executor = Global.GLOBAL_TASK_EXECUTOR)
    public void updateSkuStock(List<Message> messages, Channel channel) throws IOException {
        skuStockService.updateSkuStockDb(
                //反序列化 合并相同key的库存销量
                StorageUtil.mergeStocks(
                        messages.stream()
                                .map(message -> (Map<ActivityShopProductSkuKey, StSvBo>) smartMessageConverter.fromMessage(message))
                                .toList()
                )
        );
        //批量 确认消息
        for (Message message : messages) {
            channel.basicAck(message.getMessageProperties().getDeliveryTag(), true);
        }
    }

    /**
     * 订单创建失败
     */
    @Log("订单创建失败")
    @RabbitListener(queues = StorageQueueNames.ORDER_CREATE_FAILED_QUEUE)
    public void orderCreateFailed(OrderInfo orderInfo, Channel channel, Message message) throws IOException {
        String orderNo = orderInfo.getOrderNo();
        StorageUtil.orderNoCheck(
                orderNo,
                () -> {
                },
                () -> orderCloseBatch(List.of(orderInfo))
        );
        //批量 确认消息
        channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
    }

    /**
     * 订单批量关闭
     *
     * @param orders 订单信息
     */
    private void orderCloseBatch(List<OrderInfo> orders) {
        if (CollUtil.isEmpty(orders)) {
            return;
        }
        //是否是单个订单取消
        List<UpdateStockOrder> updateStockOrders = orders.stream()
                .collect(
                        //根据关闭类型 获取库存变动类型 根据库存变动类型分组
                        Collectors.groupingBy(order -> OrderCloseType.AFS == order.getCloseType() ? StockChangeType.RETURNED_INBOUND : StockChangeType.ORDER_CANCELLED_INBOUND)
                ).entrySet()
                .stream()
                .flatMap(
                        (entry) -> {
                            StockChangeType changeType = entry.getKey();
                            return entry.getValue().stream()
                                    .map(
                                            orderInfo -> {
                                                OrderType activityType = orderInfo.getActivityType();
                                                Long activityId = orderInfo.getActivityId();
                                                return new UpdateStockOrder()
                                                        .setOrderNo(orderInfo.getOrderNo())
                                                        .setGenerateDetail(true)
                                                        .setChangeType(changeType)
                                                        .setStSvMap(
                                                                orderInfo.getSkuStocks()
                                                                        .stream()
                                                                        .collect(
                                                                                Collectors.toMap(
                                                                                        skuStock -> (ActivityShopProductSkuKey) new ActivityShopProductSkuKey()
                                                                                                .setSkuId(skuStock.getSkuId())
                                                                                                .setProductId(skuStock.getProductId())
                                                                                                .setShopId(skuStock.getShopId())
                                                                                                .setActivityId(activityId)
                                                                                                .setActivityType(activityType),
                                                                                        skuStock -> {
                                                                                            //归还的库存和减少销量
                                                                                            int stock = skuStock.getNum();
                                                                                            return new StSvBo(stock, -stock);
                                                                                        }
                                                                                )
                                                                        )
                                                        );
                                            }
                                    );
                        }
                )
                .toList();

        skuStockService.updateStock(true, true, updateStockOrders);
    }


    /**
     * 订单关闭
     */
    @Log("订单关闭")
    @RabbitListener(queues = StorageQueueNames.ORDER_CLOSED, containerFactory = RabbitConstant.BATCH_LISTENER_CONTAINER_FACTORY, executor = Global.GLOBAL_TASK_EXECUTOR)
    public void orderClose(List<Message> messages, Channel channel) throws IOException {
        //反序列化
        List<OrderInfo> orderInfos = messages.stream()
                .map(message -> (OrderInfo) smartMessageConverter.fromMessage(message, OrderInfo.class))
                .toList();
        log.debug("订单关闭数据 {}", orderInfos);
        orderCloseBatch(orderInfos);
        //批量 确认消息
        for (Message message : messages) {
            channel.basicAck(message.getMessageProperties().getDeliveryTag(), true);
        }
    }

    /**
     * 归还库存  目前只用于 供应商的采购订单关闭
     *
     * @param orderNoStSvMap 订单号与sku库存销量映射
     * @param channel        channel
     * @param message        message
     * @throws IOException ioexception
     */
    @RabbitListener(queues = StorageQueueNames.RETURN_STOCK)
    public void returnStock(Map<String, Map<ActivityShopProductSkuKey, StSvBo>> orderNoStSvMap, Channel channel, Message message) throws IOException {
        if (CollUtil.isEmpty(orderNoStSvMap)) {
            channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
            return;
        }
        skuStockService.updateStock(true, false,
                orderNoStSvMap.entrySet()
                        .stream()
                        .filter(entry -> CollUtil.isNotEmpty(entry.getValue()))
                        .map(
                                entry -> {
                                    String orderNo = entry.getKey();
                                    Map<ActivityShopProductSkuKey, StSvBo> stSvBoMap = entry.getValue();
                                    return new UpdateStockOrder()
                                            .setOrderNo(orderNo)
                                            .setGenerateDetail(true)
                                            .setChangeType(StockChangeType.ORDER_CANCELLED_INBOUND)
                                            .setStSvMap(stSvBoMap);
                                }
                        ).toList()
        );
        channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
    }

    /**
     * 删除库存 sku
     *
     * @param productDeleteDTO delete
     */
    @RabbitListener(queues = StorageQueueNames.PRODUCT_DELETE)
    public void productDeleteStorageSku(ProductDeleteDTO productDeleteDTO, Channel channel, Message message) throws IOException {
        skuStockService.productDeleteStorageSku(productDeleteDTO);
        channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
    }

}
