package com.medusa.gruul.addon.supplier.mq;

import com.medusa.gruul.addon.supplier.model.enums.SupplierRabbit;
import com.medusa.gruul.goods.api.enums.GoodsRabbit;
import com.medusa.gruul.shop.api.enums.ShopRabbit;
import com.medusa.gruul.storage.api.enums.StorageRabbit;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Exchange;
import org.springframework.amqp.core.ExchangeBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.QueueBuilder;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * RabbitConfig
 *
 * @author xiaoq
 * @Description SupplierRabbitConfig.java
 * @date 2023-07-17 09:39
 */
@Slf4j
@Configuration
@RequiredArgsConstructor
public class SupplierRabbitConfig {


    /**
     * 供应商 交换机
     */
    @Bean
    @ConditionalOnMissingBean(name = "supplierExchange")
    public Exchange supplierExchange() {
        return ExchangeBuilder.directExchange(SupplierRabbit.EXCHANGE)
                .durable(true)
                .delayed()
                .build();
    }

    /**
     * 店铺 交换机
     */
    @Bean
    @ConditionalOnMissingBean(name = "shopExchange")
    public Exchange shopExchange() {
        return ExchangeBuilder.directExchange(ShopRabbit.EXCHANGE)
                .durable(true)
                .build();
    }

    @Bean
    @ConditionalOnMissingBean(name = "goodsExchange")
    public Exchange goodsExchange() {
        return ExchangeBuilder.directExchange(GoodsRabbit.EXCHANGE)
                .durable(true)
                .build();
    }

    /**
     * 库存交换机
     * @return
     */
    @Bean
    @ConditionalOnMissingBean(name = "storageExchange")
    public Exchange storageExchange() {
        return new DirectExchange(StorageRabbit.EXCHANGE, true, false);
    }

    /**
     * 供应商信息同步队列
     *
     * @return Queue
     */
    @Bean
    public Queue supplierInfoSyncQueue() {
        return QueueBuilder.durable(SupplierQueueNames.SUPPLIER_INFO_SYNC_QUEUE)
                .build();
    }

    @Bean
    public Binding supplierInfoSyncBinding() {
        return BindingBuilder.bind(supplierInfoSyncQueue())
                .to(shopExchange())
                .with(ShopRabbit.SHOP_UPDATE.routingKey())
                .noargs();
    }

    /**
     * 供应商订单超时支付 队列
     */
    @Bean
    public Queue supplierOrderAutoPaidTimeOutCloseQueue() {
        return QueueBuilder.durable(SupplierQueueNames.SUPPLIER_ORDER_AUTO_PAID_TIMEOUT_CLOSE_QUEUE)
                .build();
    }

    /**
     * 供应商订单超时支付 队列绑定
     *
     * @return Binding 绑定
     */
    @Bean
    public Binding supplierOrderAutoPaidTimeOutCloseBinding() {
        return BindingBuilder.bind(supplierOrderAutoPaidTimeOutCloseQueue())
                .to(supplierExchange())
                .with(SupplierRabbit.SUPPLIER_ORDER_AUTO_PAID_TIMEOUT_CLOSE.routingKey())
                .noargs();
    }


    /**
     * 供应商启用禁用
     */
    @Bean
    public Queue supplierEnableDisableQueue() {
        return QueueBuilder.durable(SupplierQueueNames.SUPPLIER_ENABLE_DISABLE_QUEUE)
                .build();
    }

    @Bean
    public Binding supplierEnableDisableBinding() {
        return BindingBuilder.bind(supplierEnableDisableQueue())
                .to(shopExchange())
                .with(ShopRabbit.SHOP_ENABLE_DISABLE.routingKey())
                .noargs();
    }


    /**
     * 商品库存变更队列
     * @return
     */
    @Bean
    public Queue productStockUpdateQueue() {
        return QueueBuilder.durable(SupplierQueueNames.SUPPLIER_GOODS_STOCK_CHANGE_QUEUE)
                .build();
    }

    @Bean
    public Binding productStockUpdateBinding() {
        return BindingBuilder.bind(productStockUpdateQueue())
                .to(storageExchange())
                .with(StorageRabbit.UPDATE_PRODUCT_STOCK.routingKey())
                .noargs();
    }

    /**
     * 供应商签约类目扣率发生变化的监听队列
     * @return
     */
    @Bean
    public Queue supplierSigningCategoryCustomDeductionRationChangeQueue() {
        return QueueBuilder.durable(SupplierQueueNames.SIGNING_CATEGORY_CUSTOM_DEDUCTION_RATIO_CHANGE_SUPPLIER_GOODS_QUEUE)
                .build();
    }

    /**
     * 绑定队列与交换机
     *
     * @return
     */
    @Bean
    public Binding shopSigningCategoryCustomDeductionRationChangeBinding() {
        return BindingBuilder
                .bind(supplierSigningCategoryCustomDeductionRationChangeQueue())
                .to(goodsExchange())
                .with(GoodsRabbit.GOODS_CUSTOM_DEDUCTION_RATIO_CHANGE.routingKey())
                .noargs();
    }





}
