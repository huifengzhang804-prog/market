package com.medusa.gruul.goods.service.mq;

import com.google.common.collect.Maps;
import com.medusa.gruul.common.log.annotation.Log;
import com.medusa.gruul.common.model.base.ShopProductKey;
import com.medusa.gruul.common.model.base.ShopProductSkuKey;
import com.medusa.gruul.common.mp.model.TenantShop;
import com.medusa.gruul.goods.api.entity.Product;
import com.medusa.gruul.goods.api.model.dto.CategorySigningCustomDeductionRationMqDTO;
import com.medusa.gruul.goods.api.model.dto.SupplierGoodsUpdateStatusDTO;
import com.medusa.gruul.goods.service.mp.service.IProductService;
import com.medusa.gruul.goods.service.mp.service.IShopFollowService;
import com.medusa.gruul.order.api.model.OrderCompletedDTO;
import com.medusa.gruul.shop.api.model.dto.ShopsEnableDisableDTO;
import com.medusa.gruul.shop.api.model.vo.ShopInfoVO;
import com.medusa.gruul.storage.api.bo.StSvBo;
import com.medusa.gruul.storage.api.dto.ProductPriceUpdateDTO;
import com.rabbitmq.client.Channel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.support.converter.SmartMessageConverter;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Map;
import java.util.Set;

/**
 * goods队列监听实现
 *
 * @author xiaoq
 * @description goods队列监听实现
 * @date 2022-06-24 10:09
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class GoodsRabbitListener {

    private final IProductService productService;
    private final IShopFollowService shopFollowService;
    private final SmartMessageConverter smartMessageConverter;

    /**
     * 合并商品SKU库存与销量到商品维度
     *
     * @param map
     * @return
     */
    public static Map<ShopProductSkuKey, StSvBo> mergeProductSales(Map<ShopProductSkuKey, StSvBo> map) {
        Map<ShopProductSkuKey, StSvBo> result = Maps.newHashMap();
        map.forEach((k, v) -> {
            k.setSkuId(null);
            if (result.containsKey(k)) {
                StSvBo stSvBo = result.get(k);
                stSvBo.setSales(stSvBo.getSales() + v.getSales());
                stSvBo.setStock(stSvBo.getStock() + v.getStock());
            } else {
                result.put(k, v);
            }
        });
        return result;

    }

    /**
     * 店铺状态改变 禁用/开启
     *
     * @param shopsEnableDisable 店铺开启禁用DTO
     * @param channel            Channel
     * @param message            Message
     */
    @Log("店铺状态改变 禁用/开启")
    @RabbitListener(queues = GoodsRabbitQueueNames.SHOP_CHANGE_QUEUE)
    public void shopChange(ShopsEnableDisableDTO shopsEnableDisable, Channel channel, Message message)
            throws IOException {
        log.warn("receive message:{}", shopsEnableDisable);
        shopsEnableDisable.validParam();
        productService.shopChange(shopsEnableDisable);
        channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
    }

    /**
     * 订单完成
     *
     * @param orderCompleted 订单完成DTO
     * @param channel        channel
     * @param message        message
     */
    @Log("商品评价完成")
    @RabbitListener(queues = GoodsRabbitQueueNames.ORDER_ACCOMPLISH_QUEUE)
    public void orderAccomplish(OrderCompletedDTO orderCompleted, Channel channel, Message message)
            throws IOException {
        log.debug("receive message:{}", orderCompleted);
        productService.saveSupplierProductRate(orderCompleted);
        channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
    }

    /**
     * 店铺信息修改
     *
     * @param shopInfoVO 店铺信息VO
     * @param channel    channel
     * @param message    message
     */
    @Log("店铺信息修改")
    @RabbitListener(queues = GoodsRabbitQueueNames.SHOP_INFO_UPDATE_QUEUE)
    public void shopInfoUpdate(ShopInfoVO shopInfoVO, Channel channel, Message message)
            throws IOException {
        log.debug("店铺信息修改：{}", shopInfoVO);
        shopFollowService.updateShopFollowInfo(shopInfoVO);
        channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
    }

    /**
     * 供应商商品状态更新
     *
     * @param supplierGoodsUpdateStatus 供应商商品更新状态DTO
     * @param channel                   channel
     * @param message                   message
     */
    @Log("供应商商品状态更新")
    @RabbitListener(queues = GoodsRabbitQueueNames.SUPPLIER_GOODS_UPDATE_STATUS_QUEUE)
    public void supplierGoodsUpdateStatus(SupplierGoodsUpdateStatusDTO supplierGoodsUpdateStatus,
                                          Channel channel, Message message) throws IOException {
        productService.supplierGoodsUpdateStatus(supplierGoodsUpdateStatus);
        log.debug("供应商商品状态更新:{}", supplierGoodsUpdateStatus);
        channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
    }

    /**
     * 供应商商品删除
     *
     * @param keys    Set<{@link  ShopProductKey}>
     * @param channel channel
     * @param message message
     */
    @Log("供应商商品删除")
    @RabbitListener(queues = GoodsRabbitQueueNames.SUPPLIER_FORCE_GOODS_STATUS_QUEUE)
    public void supplierForceGoodsStatus(Set<ShopProductKey> keys, Channel channel, Message message)
            throws IOException {
        log.debug("供应商商品删除:{}", keys);
        TenantShop.disable(() -> productService.supplierForceGoodsStatus(keys));
        channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
    }

    /**
     * 供应商商品修改
     *
     * @param supplierProduct 供应商商品信息
     * @param channel         channel
     * @param message         message
     */
    @Log("供应商商品信息修改")
    @RabbitListener(queues = GoodsRabbitQueueNames.SUPPLIER_UPDATE_GOODS)
    public void supplierUpdateGoods(Product supplierProduct, Channel channel, Message message)
            throws IOException {
        log.debug("供应商商品信息修改:{}", supplierProduct);
        productService.supplierUpdateGoods(supplierProduct);
        channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
    }

    /**
     * 更新商品类目扣率
     *
     * @param dto     参数
     * @param channel channel
     * @param message message
     * @throws IOException
     */
    @Log("更新商品类目扣率")
    @RabbitListener(queues = GoodsRabbitQueueNames.SIGNING_CATEGORY_CUSTOM_DEDUCTION_RATIO_CHANGE_QUEUE)
    public void updateProductCategoryDeductionRation(CategorySigningCustomDeductionRationMqDTO dto,
                                                     Channel channel,
                                                     Message message) throws IOException {
        log.debug("更新商品类目扣率:{}", dto);
        productService.updateProductCategoryDeductionRation(dto);
        channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
    }

    /**
     * 商品sku价格修改
     */
    @RabbitListener(queues = GoodsRabbitQueueNames.PRODUCT_SKU_PRICE_UPDATE)
    public void productSkuPriceUpdate(ProductPriceUpdateDTO priceUpdate, Channel channel, Message message) throws IOException {
        productService.productSkuPriceUpdate(priceUpdate);
        channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
    }

    @Log("更新商品销量")
    @RabbitListener(queues = GoodsRabbitQueueNames.PRODUCT_SALES_UPDATE)
    public void productSpecificationUpdate(Message message, Channel channel) throws IOException {
        try {
            log.info("更新商品销量库存数据:{}", message);
            Map<ShopProductSkuKey, StSvBo> result = (Map<ShopProductSkuKey, StSvBo>) smartMessageConverter.fromMessage(message);

            productService.updateProductSales(mergeProductSales(result));


            //确认消息
            channel.basicAck(message.getMessageProperties().getDeliveryTag(), true);

        } catch (Exception e) {
            log.error("监听消息出现问题", e);
        }

    }

}
