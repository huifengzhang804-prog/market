package com.medusa.gruul.search.service.mq;

import com.alibaba.fastjson2.JSONObject;
import com.medusa.gruul.common.log.annotation.Log;
import com.medusa.gruul.common.model.base.ShopProductSkuKey;
import com.medusa.gruul.common.mq.rabbit.RabbitConstant;
import com.medusa.gruul.common.redis.util.RedisUtil;
import com.medusa.gruul.goods.api.entity.Product;
import com.medusa.gruul.goods.api.model.dto.ProductBroadcastDTO;
import com.medusa.gruul.goods.api.model.dto.ProductDeleteDTO;
import com.medusa.gruul.goods.api.model.dto.ProductNameUpdateDTO;
import com.medusa.gruul.goods.api.model.dto.ProductUpdateStatusDTO;
import com.medusa.gruul.order.api.model.OrderPaidBroadcastDTO;
import com.medusa.gruul.search.api.model.NestedCategory;
import com.medusa.gruul.search.service.service.EsProductService;
import com.medusa.gruul.search.service.service.SearchUserActionSaveService;
import com.medusa.gruul.shop.api.model.dto.ShopOperationDTO;
import com.medusa.gruul.shop.api.model.dto.ShopsEnableDisableDTO;
import com.medusa.gruul.shop.api.model.vo.ShopInfoVO;
import com.medusa.gruul.storage.api.bo.StSvBo;
import com.medusa.gruul.storage.api.dto.ProductPriceUpdateDTO;
import com.medusa.gruul.storage.api.vo.ProductSkusVO;
import com.rabbitmq.client.Channel;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.support.converter.SmartMessageConverter;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 检索监听
 *
 * @author WuDi
 * date 2022/9/28
 */
@Component
@RequiredArgsConstructor
public class SearchRabbitListener {


    private final SmartMessageConverter smartMessageConverter;
    private final EsProductService esProductService;

    private final SearchUserActionSaveService searchUserActionSaveService;


    /**
     * 修改店铺状态
     */
    @RabbitListener(queues = SearchQueueNames.SHOP_STATUS_CHANGE, containerFactory = RabbitConstant.BATCH_LISTENER_CONTAINER_FACTORY)
    public void shopStatusChange(List<Message> messages, Channel channel) throws IOException {
        esProductService.shopStatusChange(
                messages.stream().
                        map(message -> (ShopsEnableDisableDTO) smartMessageConverter.fromMessage(message))
                        .collect(Collectors.toList())
        );
        //批量 确认消息
        for (Message message : messages) {
            channel.basicAck(message.getMessageProperties().getDeliveryTag(), true);
        }
    }

    /**
     * 商品发布
     */
    @RabbitListener(queues = SearchQueueNames.PRODUCT_RELEASE)
    public void productRelease(ProductBroadcastDTO productRelease, Channel channel, Message message) throws IOException {
        esProductService.productRelease(productRelease);
        channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
    }

    /**
     * 商品更新
     */
    @RabbitListener(queues = SearchQueueNames.PRODUCT_UPDATE)
    public void productUpdate(ProductBroadcastDTO productUpdate, Channel channel, Message message) throws IOException {
        esProductService.productUpdate(productUpdate);
        channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
    }

    /**
     * 商品删除
     */
    @RabbitListener(queues = SearchQueueNames.PRODUCT_DELETE)
    public void productDelete(ProductDeleteDTO productDelete, Channel channel, Message message) throws IOException {
        esProductService.productDelete(productDelete);
        channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
    }

    /**
     * 商品状态改变
     */
    @RabbitListener(queues = SearchQueueNames.PRODUCT_STATUS_UPDATE)
    public void productStatusUpdate(List<ProductUpdateStatusDTO> updateStatus, Channel channel, Message message) throws IOException {
        esProductService.productStatusUpdate(updateStatus);
        channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
    }


    /**
     * 扣减库存 则 增加销量
     */
    @SuppressWarnings("unchecked")
    @RabbitListener(queues = SearchQueueNames.UPDATE_PRODUCT_STOCK, containerFactory = RabbitConstant.BATCH_LISTENER_CONTAINER_FACTORY)
    public void updateProductStock(List<Message> messages, Channel channel) throws IOException {
        //反序列化 合并相同key的库存销量
        Map<String, Map<Long, StSvBo>> productIdAndSkuStSv = new HashMap<>();
        messages.stream()
                .map(message -> ((Map<ShopProductSkuKey, StSvBo>) smartMessageConverter.fromMessage(message)))
                .forEach(
                        map -> map.forEach(
                                (key, stsv) -> {
                                    Map<Long, StSvBo> currentSkuStsvMap = productIdAndSkuStSv.computeIfAbsent(
                                            RedisUtil.key(key.getShopId(), key.getProductId()),
                                            k -> new HashMap<>()
                                    );
                                    StSvBo stSvBo = currentSkuStsvMap.computeIfAbsent(key.getSkuId(), k -> new StSvBo());
                                    stSvBo.merge(stsv);
                                }
                        )
                );

        esProductService.updateProductStockAndSales(productIdAndSkuStSv);
        //批量 确认消息
        for (Message message : messages) {
            channel.basicAck(message.getMessageProperties().getDeliveryTag(), true);
        }
    }


    @RabbitListener(queues = SearchQueueNames.CLASSIFY_REMOVE)
    public void classifyRemove(NestedCategory nestedCategory, Channel channel, Message message) throws IOException {
        esProductService.productClassifyEmpty(nestedCategory);
        channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
    }

    /**
     * 多规格价格修改
     *
     * @param priceUpdate 商品价格更新DTO
     */
    @RabbitListener(queues = SearchQueueNames.UPDATE_SKU_PRICE)
    public void updateSkuPrice(ProductPriceUpdateDTO priceUpdate, Channel channel, Message message) throws IOException {
        esProductService.updateSkuPrice(priceUpdate);
        channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
    }

    /**
     * 店铺信息变更，更新商品信息中的店铺名称和店铺类型
     *
     * @param shopInfo 店铺信息
     */
    @RabbitListener(queues = SearchQueueNames.SHOP_UPDATE)
    public void updateProductShopInfo(ShopInfoVO shopInfo, Channel channel, Message message) throws IOException {
        esProductService.updateProductShopInfo(shopInfo);
        channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
    }


    /**
     * @param shopOperation 店铺操作dto
     * @param channel       channel
     * @param message       message
     */
    @RabbitListener(queues = SearchQueueNames.USER_SEARCH_SHOP)
    public void userSearchShop(ShopOperationDTO shopOperation, Channel channel, Message message) throws IOException {
        searchUserActionSaveService.saveSearchShop(shopOperation);
        channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
    }


    /**
     * @param orderPaidBroadcast 订单支付DTO
     * @param channel            channel
     * @param message            message
     */
    @Log("用户订单支付成功")
    @RabbitListener(queues = SearchQueueNames.USER_PAY_ORDER)
    public void userOrderPaySucceed(OrderPaidBroadcastDTO orderPaidBroadcast, Channel channel, Message message) throws IOException {
        searchUserActionSaveService.savePayShop(orderPaidBroadcast);
        channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
    }


    /**
     * @param shopOperation 店铺操作dto
     * @param channel       channel
     * @param message       message
     */
    @RabbitListener(queues = SearchQueueNames.USER_ATTENTION_SHOP)
    public void userAttentionShop(ShopOperationDTO shopOperation, Channel channel, Message message) throws IOException {
        searchUserActionSaveService.saveUserAttentionShop(shopOperation);
        channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
    }

    @RabbitListener(queues = SearchQueueNames.USER_FOOT_MARK_ADD)
    public void userFootMarkAdd(JSONObject jsonObject, Channel channel, Message message) throws IOException {
        Long shopId = (Long) jsonObject.get("shopId");
        Long userId = (Long) jsonObject.get("userId");
        if (shopId != null && userId != null) {
            searchUserActionSaveService.userFootMarkAdd(shopId, userId);
        }
        channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
    }

    /**
     * 商品标签删除
     */
    @RabbitListener(queues = SearchQueueNames.PRODUCT_LABEL_DELETE)
    public void productLabelDelete(List<Product> productList, Channel channel, Message message) throws IOException {
        esProductService.productLabelDelete(productList);
        channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
    }
    @RabbitListener(queues = SearchQueueNames.PRODUCT_LABEL_UPDATE)
    public void productLabelUpdate(Product product, Channel channel, Message message) throws IOException {
        esProductService.updateProductLabel(product);
        channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
    }


    /**
     * 商品名称修改
     */
    @RabbitListener(queues = SearchQueueNames.PRODUCT_NAME_UPDATE)
    public void productNameUpdate(ProductNameUpdateDTO productName, Channel channel, Message message) throws IOException {
        esProductService.productNameUpdate(productName);
        channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
    }
}
