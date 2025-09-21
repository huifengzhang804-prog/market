package com.medusa.gruul.cart.service.mq;

import cn.hutool.core.collection.CollUtil;
import com.medusa.gruul.cart.api.dto.ShopIdSkuIdsDTO;
import com.medusa.gruul.cart.service.model.vo.ProductSkuVO;
import com.medusa.gruul.cart.service.service.CartService;
import com.medusa.gruul.order.api.entity.ShopOrderItem;
import com.medusa.gruul.order.api.enums.OrderSourceType;
import com.medusa.gruul.order.api.model.OrderCreatedDTO;
import com.rabbitmq.client.Channel;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * <p></p>
 *
 * @author 张治保
 * date 2022/7/26
 */
@Component
@RequiredArgsConstructor
public class CartRabbitListener {

    private final CartService cartService;

    /**
     * 删除购物车商品
     */
    @RabbitListener(queues = CartQueueNames.CART_DELETE_PRODUCT_QUEUE)
    public void deleteProduct(OrderCreatedDTO orderCreated, Channel channel, Message message) throws IOException {
        if (OrderSourceType.CART != orderCreated.getSource()) {
            //确认消息
            channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
            return;
        }
        cartService.deleteCartProduct(
                orderCreated.getBuyerId(),
                null,
                CollUtil.emptyIfNull(orderCreated.getShopOrderItems())
                        .stream()
                        .collect(Collectors.groupingBy(ShopOrderItem::getShopId))
                        .entrySet()
                        .stream()
                        .map(
                                entry -> new ShopIdSkuIdsDTO()
                                        .setShopId(entry.getKey())
                                        .setSkuSpecsMap(
                                                entry.getValue()
                                                        .stream()
                                                        .collect(
                                                                Collectors.groupingBy(
                                                                        ShopOrderItem::getSkuId,
                                                                        Collectors.mapping(
                                                                                ShopOrderItem::getSpecs,
                                                                                Collectors.toList()
                                                                        )
                                                                )
                                                        )
                                        )
                        ).collect(Collectors.toList())
        );
        //确认消息
        channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
    }


    /**
     * 店铺加购数 更新
     */
    @RabbitListener(queues = CartQueueNames.SHOP_CART_UPDATE)
    public void updateShopCart(Map<Long, List<ProductSkuVO>> shopCountMap, Channel channel, Message message) throws IOException {
        cartService.updateShopCount(shopCountMap);
        //确认消息
        channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
    }
}
