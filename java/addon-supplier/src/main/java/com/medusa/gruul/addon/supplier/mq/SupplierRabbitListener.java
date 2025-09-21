package com.medusa.gruul.addon.supplier.mq;

import cn.hutool.core.collection.CollUtil;
import com.baomidou.mybatisplus.extension.conditions.update.LambdaUpdateChainWrapper;
import com.google.common.collect.Maps;
import com.medusa.gruul.addon.supplier.model.enums.OrderStatus;
import com.medusa.gruul.addon.supplier.modules.order.service.SupplierOrderHandleService;
import com.medusa.gruul.addon.supplier.mp.entity.SupplierGoods;
import com.medusa.gruul.addon.supplier.mp.entity.SupplierGoodsPublish;
import com.medusa.gruul.addon.supplier.mp.entity.SupplierInfo;
import com.medusa.gruul.addon.supplier.mp.service.ISupplierGoodsPublishService;
import com.medusa.gruul.addon.supplier.mp.service.ISupplierGoodsService;
import com.medusa.gruul.addon.supplier.mp.service.ISupplierInfoService;
import com.medusa.gruul.common.log.annotation.Log;
import com.medusa.gruul.common.model.base.ShopProductKey;
import com.medusa.gruul.common.model.base.ShopProductSkuKey;
import com.medusa.gruul.common.model.enums.SellType;
import com.medusa.gruul.common.module.app.shop.ShopMode;
import com.medusa.gruul.common.module.app.shop.ShopStatus;
import com.medusa.gruul.common.mq.rabbit.RabbitConstant;
import com.medusa.gruul.global.config.Global;
import com.medusa.gruul.goods.api.enums.GoodsRabbit;
import com.medusa.gruul.goods.api.model.dto.CategorySigningCustomDeductionRationMqDTO;
import com.medusa.gruul.goods.api.model.enums.ProductStatus;
import com.medusa.gruul.shop.api.model.dto.ShopsEnableDisableDTO;
import com.medusa.gruul.shop.api.model.vo.ShopInfoVO;
import com.medusa.gruul.storage.api.bo.StSvBo;
import com.rabbitmq.client.Channel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.SmartMessageConverter;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Executor;
import java.util.stream.Collectors;

/**
 * @author 张治保
 * date 2023/7/21
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class SupplierRabbitListener {

    private final Executor globalExecutor;
    private final RabbitTemplate rabbitTemplate;
    private final ISupplierInfoService supplierInfoService;
    private final SmartMessageConverter smartMessageConverter;
    private final ISupplierGoodsService supplierGoodsService;
    private final SupplierOrderHandleService supplierOrderHandleService;
    private final ISupplierGoodsPublishService supplierGoodsPublishService;

    /**
     * 合并商品SKU库存与销量变化到商品维度
     *
     * @param map 商品SKU库存与销量变化
     * @return 商品库存与销量变化
     */
    public static Map<ShopProductKey, StSvBo> mergeProductSales(Map<ShopProductSkuKey, StSvBo> map) {
        Map<ShopProductKey, StSvBo> result = Maps.newHashMap();
        map.forEach(
                (key, value) -> result.computeIfAbsent(key.toShopProductKey(), k -> new StSvBo())
                        .merge(value)
        );
        log.info("合并商品SKU库存与销量变化到商品维度:{}", result);
        return result;

    }

    @Log("供应商信息同步")
    @RabbitListener(queues = SupplierQueueNames.SUPPLIER_INFO_SYNC_QUEUE)
    public void supplierInfoSync(ShopInfoVO shopInfo, Channel channel, Message message) throws IOException {
        if (shopInfo.getShopMode() != ShopMode.SUPPLIER) {
            channel.basicAck(message.getMessageProperties().getDeliveryTag(), true);
            return;
        }
        supplierInfoService.saveOrUpdate(
                new SupplierInfo()
                        .setId(shopInfo.getId())
                        .setPhone(shopInfo.getContractNumber())
                        .setName(shopInfo.getName())
                        .setEnabled(ShopStatus.NORMAL == shopInfo.getStatus())
        );
        channel.basicAck(message.getMessageProperties().getDeliveryTag(), true);
    }

    @Log("供应商延迟队列")
    @SuppressWarnings("unchecked")
    @RabbitListener(queues = SupplierQueueNames.SUPPLIER_ORDER_AUTO_PAID_TIMEOUT_CLOSE_QUEUE, containerFactory = RabbitConstant.BATCH_LISTENER_CONTAINER_FACTORY, executor = Global.GLOBAL_TASK_EXECUTOR)
    public void supplierOrderCancelNotPay(List<Message> messages, Channel channel) throws IOException {
        supplierOrderHandleService.updateOrderStatus(
                Boolean.TRUE,
                messages.stream()
                        .flatMap(message -> ((Collection<String>) smartMessageConverter.fromMessage(message)).stream())
                        .collect(Collectors.toSet()),
                OrderStatus.UNPAID, OrderStatus.SYSTEM_CLOSED);
        for (Message message : messages) {
            channel.basicAck(message.getMessageProperties().getDeliveryTag(), true);
        }
    }

    @Log("供应商禁用")
    @RabbitListener(queues = SupplierQueueNames.SUPPLIER_ENABLE_DISABLE_QUEUE)
    @Transactional(rollbackFor = Exception.class)
    public void supplierEnableDisable(ShopsEnableDisableDTO enableDisable, Channel channel, Message message) throws IOException {
        supplierEnableDisable(enableDisable);
        channel.basicAck(message.getMessageProperties().getDeliveryTag(), true);
    }

    private void supplierEnableDisable(ShopsEnableDisableDTO enableDisable) {
        Set<Long> supplierIds = enableDisable.getShopIds();
        if (CollUtil.isEmpty(supplierIds)) {
            return;
        }
        //过滤出存在的供应商
        supplierIds = supplierInfoService.lambdaQuery()
                .select(SupplierInfo::getId)
                .in(SupplierInfo::getId, supplierIds)
                .list()
                .stream()
                .map(SupplierInfo::getId)
                .collect(Collectors.toSet());
        if (CollUtil.isEmpty(supplierIds)) {
            return;
        }
        Boolean enable = enableDisable.getEnable();
        //更新供应商可用状态
        supplierInfoService.lambdaUpdate()
                .set(SupplierInfo::getEnabled, enable)
                .in(SupplierInfo::getId, supplierIds)
                .update();
        supplierGoodsService.lambdaUpdate()
                .set(SupplierGoods::getSupplierProductStatus, enable ? ProductStatus.SELL_ON : ProductStatus.UNUSABLE)
                .eq(SupplierGoods::getSupplierProductStatus, enable ? ProductStatus.UNUSABLE : ProductStatus.SELL_ON)
                .in(SupplierGoods::getShopId, supplierIds)
                .update();
        if (enable) {
            return;
        }
        //查询出代销商品 通知代销商家商品删除
        List<SupplierGoods> allGoods = supplierGoodsService.lambdaQuery()
                .select(SupplierGoods::getShopId, SupplierGoods::getId)
                .eq(SupplierGoods::getSellType, SellType.CONSIGNMENT)
                .in(SupplierGoods::getShopId, supplierIds)
                .list();
        if (CollUtil.isEmpty(allGoods)) {
            return;
        }
        //删除已发布的代销商品
        LambdaUpdateChainWrapper<SupplierGoodsPublish> deleteWrapper = supplierGoodsPublishService.lambdaUpdate();
        allGoods.forEach(
                product -> deleteWrapper.or(inner -> inner.eq(SupplierGoodsPublish::getSupplierId, product.getShopId()).eq(SupplierGoodsPublish::getProductId, product.getId()))
        );
        deleteWrapper.remove();

        //通知商家端删除商品
        globalExecutor.execute(
                () -> rabbitTemplate.convertAndSend(
                        GoodsRabbit.SUPPLIER_FORCE_GOODS_STATUS.exchange(),
                        GoodsRabbit.SUPPLIER_FORCE_GOODS_STATUS.routingKey(),
                        allGoods.stream()
                                .map(product -> new ShopProductKey().setShopId(product.getShopId()).setProductId(product.getId()))
                                .collect(Collectors.toSet())
                )
        );

    }

    @Log("供应商商品库存变更")
    @RabbitListener(queues = SupplierQueueNames.SUPPLIER_GOODS_STOCK_CHANGE_QUEUE)
    @Transactional(rollbackFor = Exception.class)
    public void supplierGoodsStockUpdate(Map<ShopProductSkuKey, StSvBo> result, Channel channel, Message message) throws IOException {
        //批量更新 sku 库存：{
        log.debug("批量更新 sku 库存：{}", result);
        //更新库存
        supplierGoodsService.updateProductStock(mergeProductSales(result));
        //确认消息
        channel.basicAck(message.getMessageProperties().getDeliveryTag(), true);
    }

    @Log("更新商品类目扣率")
    @RabbitListener(queues = SupplierQueueNames.SIGNING_CATEGORY_CUSTOM_DEDUCTION_RATIO_CHANGE_SUPPLIER_GOODS_QUEUE)
    @Transactional(rollbackFor = Exception.class)
    public void updateProductCategoryDeductionRation(CategorySigningCustomDeductionRationMqDTO dto,
                                                     Channel channel,
                                                     Message message) throws IOException {
        log.debug("更新供应商商品类目扣率:{}", dto);
        supplierGoodsService.updateProductCategoryDeductionRation(dto);
        channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
    }
}
