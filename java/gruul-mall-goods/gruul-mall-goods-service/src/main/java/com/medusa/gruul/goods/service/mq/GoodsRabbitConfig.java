package com.medusa.gruul.goods.service.mq;

import com.medusa.gruul.goods.api.enums.GoodsRabbit;
import com.medusa.gruul.order.api.enums.OrderRabbit;
import com.medusa.gruul.shop.api.enums.ShopRabbit;
import com.medusa.gruul.storage.api.enums.StorageRabbit;
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
 * 队列交换机绑定
 *
 * @author xiaoq
 * @description 队列交换机绑定
 * @date 2022-06-24 09:31
 */
@Configuration
public class GoodsRabbitConfig {


    /**
     * 创建交换机
     */
    @Bean
    @ConditionalOnMissingBean(name = "goodsExchange")
    public Exchange goodsExchange() {
        return new DirectExchange(GoodsRabbit.EXCHANGE, true, false);
    }

    /**
     * 店铺交换机
     */
    @Bean
    @ConditionalOnMissingBean(name = "shopExchange")
    public Exchange shopExchange() {
        return new DirectExchange(ShopRabbit.EXCHANGE, true, false);
    }

    /**
     * 订单交换机
     */
    @Bean
    @ConditionalOnMissingBean(name = "orderExchange")
    public Exchange orderExchange() {
        return ExchangeBuilder.directExchange(OrderRabbit.ORDER_ACCOMPLISH.exchange())
                .durable(true)
                .delayed().build();
    }


    /**
     * 店铺交换机
     */
    @Bean
    @ConditionalOnMissingBean(name = "storageExchange")
    public Exchange storageExchange() {
        return new DirectExchange(StorageRabbit.EXCHANGE, true, false);
    }

    /**
     * 店铺状态改变 启用禁用队列
     *
     * @return Queue
     */
    @Bean
    public Queue shopChangeQueue() {
        return new Queue(GoodsRabbitQueueNames.SHOP_CHANGE_QUEUE, true);
    }

    @Bean
    public Binding shopExchangeBinding() {
        return BindingBuilder
                .bind(shopChangeQueue())
                .to(shopExchange())
                .with(ShopRabbit.SHOP_ENABLE_DISABLE.routingKey())
                .noargs();
    }


    /**
     * ---------------------------------------订单完成 ---------------------------------
     */
    @Bean
    public Queue orderAccomplishQueue() {
        return QueueBuilder.durable(GoodsRabbitQueueNames.ORDER_ACCOMPLISH_QUEUE)
                .build();
    }


    @Bean
    public Binding orderAccomplishBinding() {
        return BindingBuilder
                .bind(orderAccomplishQueue())
                .to(orderExchange())
                .with(OrderRabbit.ORDER_ACCOMPLISH.routingKey())
                .noargs();
    }

    /**
     * ---------------------------------------店铺更新 ---------------------------------
     */
    @Bean
    public Queue goodsShopInfoUpdateQueue() {
        return QueueBuilder.durable(GoodsRabbitQueueNames.SHOP_INFO_UPDATE_QUEUE)
                .build();
    }

    @Bean
    public Binding goodsShopInfoUpdateBinding() {
        return BindingBuilder
                .bind(goodsShopInfoUpdateQueue())
                .to(shopExchange())
                .with(ShopRabbit.SHOP_UPDATE.routingKey())
                .noargs();
    }


    /**
     * ---------------------------------------供应商商品状态更新 ---------------------------------
     */
    @Bean
    public Queue supplierGoodsUpdateStatusQueue() {
        return QueueBuilder.durable(GoodsRabbitQueueNames.SUPPLIER_GOODS_UPDATE_STATUS_QUEUE)
                .build();
    }

    @Bean
    public Binding supplierGoodsUpdateStatusBinding() {
        return BindingBuilder
                .bind(supplierGoodsUpdateStatusQueue())
                .to(goodsExchange())
                .with(GoodsRabbit.SUPPLIER_GOODS_UPDATE_STATUS.routingKey())
                .noargs();
    }


    /**
     * ---------------------------------------供应商商品删除---------------------------------
     */
    @Bean
    public Queue supplierForceGoodsStatusQueue() {
        return QueueBuilder.durable(GoodsRabbitQueueNames.SUPPLIER_FORCE_GOODS_STATUS_QUEUE)
                .build();
    }

    @Bean
    public Binding supplierForceGoodsStatusBinding() {
        return BindingBuilder
                .bind(supplierForceGoodsStatusQueue())
                .to(goodsExchange())
                .with(GoodsRabbit.SUPPLIER_FORCE_GOODS_STATUS.routingKey())
                .noargs();
    }


    /**
     * ---------------------------------------供应商商品修改---------------------------------
     */
    @Bean
    public Queue supplierUpdateGoodsQueue() {
        return QueueBuilder.durable(GoodsRabbitQueueNames.SUPPLIER_UPDATE_GOODS)
                .build();
    }

    @Bean
    public Binding supplierUpdateGoodsQueueBinding() {
        return BindingBuilder
                .bind(supplierUpdateGoodsQueue())
                .to(goodsExchange())
                .with(GoodsRabbit.SUPPLIER_UPDATE_GOODS.routingKey())
                .noargs();
    }

    /**
     * 店铺签约类目自定义
     *
     * @return
     */
    @Bean
    public Queue shopSigningCategoryCustomDeductionRationChangeQueue() {
        return new Queue(GoodsRabbitQueueNames.SIGNING_CATEGORY_CUSTOM_DEDUCTION_RATIO_CHANGE_QUEUE, true);
    }

    /**
     * 绑定队列与交换机
     *
     * @return
     */
    @Bean
    public Binding shopSigningCategoryCustomDeductionRationChangeBinding() {
        return BindingBuilder
                .bind(shopSigningCategoryCustomDeductionRationChangeQueue())
                .to(goodsExchange())
                .with(GoodsRabbit.GOODS_CUSTOM_DEDUCTION_RATIO_CHANGE.routingKey())
                .noargs();
    }


    /**
     * 多规格价格修改
     */
    @Bean
    public Queue productSkuPriceUpdateQueue() {
        return QueueBuilder.durable(GoodsRabbitQueueNames.PRODUCT_SKU_PRICE_UPDATE)
                .build();
    }

    @Bean
    public Binding productSkuPriceUpdateBinding() {
        return BindingBuilder.bind(productSkuPriceUpdateQueue())
                .to(storageExchange())
                .with(StorageRabbit.UPDATE_SKU_PRICE.routingKey())
                .noargs();
    }

    /**
     * 商品销量变更队列
     * @return
     */
    @Bean
    public Queue productSalesUpdateQueue() {
        return QueueBuilder.durable(GoodsRabbitQueueNames.PRODUCT_SALES_UPDATE)
                .build();
    }

    /**
     * 绑定队列与交换机
     * @return
     */
    @Bean
    public Binding productSalesUpdateBinding() {
        return BindingBuilder.bind(productSalesUpdateQueue())
                .to(storageExchange())
                .with(StorageRabbit.UPDATE_PRODUCT_STOCK.routingKey())
                .noargs();
    }

}
