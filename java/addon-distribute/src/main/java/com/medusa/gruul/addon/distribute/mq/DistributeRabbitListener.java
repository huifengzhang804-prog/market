package com.medusa.gruul.addon.distribute.mq;

import com.medusa.gruul.addon.distribute.mp.entity.DistributeProduct;
import com.medusa.gruul.addon.distribute.mp.service.IDistributeProductService;
import com.medusa.gruul.addon.distribute.service.DistributeBonusService;
import com.medusa.gruul.addon.distribute.service.DistributeOrderHandleService;
import com.medusa.gruul.addon.distribute.service.DistributeProductHandleService;
import com.medusa.gruul.addon.distribute.service.DistributorHandleService;
import com.medusa.gruul.common.model.base.ShopProductKey;
import com.medusa.gruul.goods.api.model.dto.ProductBroadcastDTO;
import com.medusa.gruul.goods.api.model.dto.ProductDeleteDTO;
import com.medusa.gruul.goods.api.model.dto.ProductNameUpdateDTO;
import com.medusa.gruul.goods.api.model.dto.ProductUpdateStatusDTO;
import com.medusa.gruul.order.api.model.OrderCreatedDTO;
import com.medusa.gruul.order.api.model.OrderPaidBroadcastDTO;
import com.medusa.gruul.order.api.pojo.OrderInfo;
import com.medusa.gruul.overview.api.entity.OverviewWithdraw;
import com.medusa.gruul.service.uaa.api.dto.UserBaseDataDTO;
import com.medusa.gruul.shop.api.model.vo.ShopInfoVO;
import com.medusa.gruul.storage.api.dto.ProductPriceUpdateDTO;
import com.rabbitmq.client.Channel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;


/**
 * @author 张治保
 * date 2022/11/15
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class DistributeRabbitListener {

    private final DistributorHandleService distributorHandleService;
    private final DistributeBonusService distributeBonusService;
    private final IDistributeProductService distributeProductService;
    private final DistributeOrderHandleService distributeOrderHandleService;
    private final DistributeProductHandleService distributeProductHandleService;

    /**
     * 商品删除
     */
    @RabbitListener(queues = DistributeRabbitQueueNames.GOODS_DELETE_QUEUE)
    public void goodsDelete(ProductDeleteDTO productDelete, Channel channel, Message message) throws IOException {
        distributeProductHandleService.deleteBatch(productDelete.getShopId(), productDelete.getProductIds());
        channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
    }

    /**
     * 商品更新
     */
    @RabbitListener(queues = DistributeRabbitQueueNames.GOODS_UPDATE_QUEUE)
    public void goodsUpdate(ProductBroadcastDTO productUpdate, Channel channel, Message message) throws IOException {
        distributeProductHandleService.updateProductByProduct(productUpdate.getProduct());
        channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
    }

    /**
     * 商品状态更改
     */
    @RabbitListener(queues = DistributeRabbitQueueNames.GOODS_UPDATE_STATUS_QUEUE)
    public void goodsUpdateStatus(List<ProductUpdateStatusDTO> productUpdateStatus, Channel channel, Message message) throws IOException {
        distributeProductHandleService.updateProductStatus(productUpdateStatus);
        channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
    }

    /**
     * 店铺信息更改
     */
    @RabbitListener(queues = DistributeRabbitQueueNames.SHOP_UPDATE)
    public void shopUpdate(ShopInfoVO shopInfo, Channel channel, Message message) throws IOException {
        distributorHandleService.updateShopInfo(shopInfo);
        channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
    }

    /**
     * 订单已创建
     */
    @RabbitListener(queues = DistributeRabbitQueueNames.ORDER_CREATED)
    public void orderCreated(OrderCreatedDTO orderCreated, Channel channel, Message message) throws IOException {
        log.info("订单创建监听:orderCreated{}", orderCreated);
        distributeOrderHandleService.orderCreated(orderCreated);
        channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
    }

    /**
     * 订单已关闭
     */
    @RabbitListener(queues = DistributeRabbitQueueNames.ORDER_CLOSED)
    public void orderClosed(OrderInfo orderInfo, Channel channel, Message message) throws IOException {
        distributeOrderHandleService.orderClosed(orderInfo);
        channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
    }

    /**
     * 订单已支付
     */
    @RabbitListener(queues = DistributeRabbitQueueNames.ORDER_PAID)
    public void orderPaid(OrderPaidBroadcastDTO orderPaidBroadcast, Channel channel, Message message) throws IOException {
        distributeOrderHandleService.orderPaid(orderPaidBroadcast);
        channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
    }


    /**
     * 提现工单被关闭
     */
    @RabbitListener(queues = DistributeRabbitQueueNames.WITHDRAW_ORDER_FORBIDDEN)
    public void withdrawOrderForbidden(OverviewWithdraw overviewWithdraw, Channel channel, Message message) throws IOException {
        distributeBonusService.withdrawOrderForbidden(overviewWithdraw);
        channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
    }

    /**
     * 修改个人资料
     */
    @RabbitListener(queues = DistributeRabbitQueueNames.USER_DATA_UPDATE)
    public void updateUserData(UserBaseDataDTO userData, Channel channel, Message message) throws IOException {
        distributorHandleService.updateUserData(userData);
        channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
    }


    /**
     * 商品sku价格修改
     */
    @RabbitListener(queues = DistributeRabbitQueueNames.UPDATE_PRODUCT_SKU_PRICE)
    public void updateProductSkuPrice(ProductPriceUpdateDTO priceUpdate, Channel channel, Message message) throws IOException {
        log.debug("更改商品价格：\n{}", priceUpdate);
        distributeProductHandleService.updateProductSkuPrice(priceUpdate);
        channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
    }


    /**
     * 商品名称修改
     */
    @RabbitListener(queues = DistributeRabbitQueueNames.PRODUCT_NAME_UPDATE)
    public void productNameUpdate(ProductNameUpdateDTO productName, Channel channel, Message message) throws IOException {
        ShopProductKey productKey = productName.getKey();
        distributeProductService.lambdaUpdate()
                .set(DistributeProduct::getName, productName.getName())
                .eq(DistributeProduct::getShopId, productKey.getShopId())
                .eq(DistributeProduct::getProductId, productKey.getProductId())
                .update();
        channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
    }
}
