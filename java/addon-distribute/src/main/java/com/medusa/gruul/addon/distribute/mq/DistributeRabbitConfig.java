package com.medusa.gruul.addon.distribute.mq;

import com.medusa.gruul.goods.api.enums.GoodsRabbit;
import com.medusa.gruul.order.api.enums.OrderRabbit;
import com.medusa.gruul.overview.api.enums.OverviewRabbit;
import com.medusa.gruul.overview.api.enums.OwnerType;
import com.medusa.gruul.service.uaa.api.enums.UaaRabbit;
import com.medusa.gruul.shop.api.enums.ShopRabbit;
import com.medusa.gruul.storage.api.enums.StorageRabbit;
import org.springframework.amqp.core.*;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author 张治保
 * date 2022/11/15
 */
@Configuration
public class DistributeRabbitConfig {


    /**
     * uaa交换机
     */
    @Bean
    @ConditionalOnMissingBean(name = "uaaExchange")
    public Exchange uaaExchange() {
        return ExchangeBuilder.directExchange(UaaRabbit.EXCHANGE)
                .durable(true)
                .build();
    }

    /**
     * overview 交换机
     */
    @Bean
    @ConditionalOnMissingBean(name = "overviewExchange")
    public Exchange overviewExchange() {
        return ExchangeBuilder.directExchange(OverviewRabbit.EXCHANGE)
                .durable(true)
                .delayed().build();
    }

    /**
     * 店铺交换机
     */
    @Bean
    @ConditionalOnMissingBean(name = "shopExchange")
    public Exchange shopExchange() {
        return ExchangeBuilder.directExchange(ShopRabbit.EXCHANGE)
                .durable(true)
                .build();
    }

    /**
     * 商品交换机
     */
    @Bean
    @ConditionalOnMissingBean(name = "goodsExchange")
    public Exchange goodsExchange() {
        return ExchangeBuilder.directExchange(GoodsRabbit.EXCHANGE)
                .durable(Boolean.TRUE)
                .build();
    }

    /**
     * 订单交换机
     */
    @Bean
    @ConditionalOnMissingBean(name = "orderExchange")
    public Exchange orderExchange() {
        return ExchangeBuilder.directExchange(OrderRabbit.EXCHANGE)
                .durable(true)
                .delayed()
                .build();
    }


    /**
     * storage 交换机
     */
    @Bean
    @ConditionalOnMissingBean(name = "storageExchange")
    public Exchange storageExchange() {
        return ExchangeBuilder.directExchange(StorageRabbit.EXCHANGE)
                .durable(Boolean.TRUE)
                .build();
    }

    /**
     * 店铺信息更新
     */
    @Bean
    public Queue distributeShopUpdateQueue() {
        return QueueBuilder.durable(DistributeRabbitQueueNames.SHOP_UPDATE)
                .build();
    }

    @Bean
    public Binding distributeShopUpdateQueueBinding() {
        return BindingBuilder.bind(distributeShopUpdateQueue())
                .to(shopExchange())
                .with(ShopRabbit.SHOP_UPDATE.routingKey())
                .noargs();
    }

    /**
     * 监听商品删除队列
     */
    @Bean
    public Queue distributeGoodsDeleteQueue() {
        return QueueBuilder.durable(DistributeRabbitQueueNames.GOODS_DELETE_QUEUE)
                .build();
    }

    @Bean
    public Binding distributeGoodsDeleteBinding() {
        return BindingBuilder.bind(distributeGoodsDeleteQueue())
                .to(goodsExchange())
                .with(GoodsRabbit.GOODS_DELETE.routingKey())
                .noargs();
    }


    /**
     * 监听商品修改队列
     */
    @Bean
    public Queue distributeGoodsUpdateQueue() {
        return QueueBuilder.durable(DistributeRabbitQueueNames.GOODS_UPDATE_QUEUE)
                .build();
    }

    @Bean
    public Binding distributeGoodsUpdateBinding() {
        return BindingBuilder.bind(distributeGoodsUpdateQueue())
                .to(goodsExchange())
                .with(GoodsRabbit.GOODS_UPDATE.routingKey())
                .noargs();
    }

    /**
     * 监听商品状态修改
     */
    @Bean
    public Queue distributeGoodsUpdateStatusQueue() {
        return QueueBuilder.durable(DistributeRabbitQueueNames.GOODS_UPDATE_STATUS_QUEUE)
                .build();
    }

    @Bean
    public Binding distributeGoodsUpdateStatusBinding() {
        return BindingBuilder.bind(distributeGoodsUpdateStatusQueue())
                .to(goodsExchange())
                .with(GoodsRabbit.GOODS_UPDATE_STATUS.routingKey())
                .noargs();
    }

    /**
     * 订单创建完成
     */
    @Bean
    public Queue distributeOrderCreatedQueue() {
        return QueueBuilder.durable(DistributeRabbitQueueNames.ORDER_CREATED)
                .build();
    }

    @Bean
    public Binding distributeOrderCreatedBinding() {
        return BindingBuilder.bind(distributeOrderCreatedQueue())
                .to(orderExchange())
                .with(OrderRabbit.ORDER_CREATED.routingKey())
                .noargs();
    }

    /**
     * 订单关闭
     */
    @Bean
    public Queue distributeOrderClosedQueue() {
        return QueueBuilder.durable(DistributeRabbitQueueNames.ORDER_CLOSED)
                .build();
    }

    @Bean
    public Binding distributeOrderClosedBinding() {
        return BindingBuilder.bind(distributeOrderClosedQueue())
                .to(orderExchange())
                .with(OrderRabbit.ORDER_CLOSE.routingKey())
                .noargs();
    }

    /**
     * 订单已支付
     */
    @Bean
    public Queue orderPaidQueue() {
        return QueueBuilder.durable(DistributeRabbitQueueNames.ORDER_PAID)
                .build();
    }

    @Bean
    public Binding orderPaidBinding() {
        return BindingBuilder.bind(orderPaidQueue())
                .to(orderExchange())
                .with(OrderRabbit.ORDER_PAID_BROADCAST.routingKey())
                .noargs();
    }

    /**
     * 提现工单审核未通过
     */
    @Bean
    public Queue withdrawOrderForbiddenQueue() {
        return QueueBuilder.durable(DistributeRabbitQueueNames.WITHDRAW_ORDER_FORBIDDEN)
                .build();
    }

    @Bean
    public Binding withdrawOrderForbiddenBinding() {
        return BindingBuilder.bind(withdrawOrderForbiddenQueue())
                .to(overviewExchange())
                .with(OverviewRabbit.WITHDRAW_ORDER_FORBIDDEN.routingKey() + OwnerType.DISTRIBUTOR.name())
                .noargs();
    }

    /**
     * 用户修改个人资料
     */
    @Bean
    public Queue updateUserDataQueue() {
        return QueueBuilder.durable(DistributeRabbitQueueNames.USER_DATA_UPDATE)
                .build();
    }

    @Bean
    public Binding updateUserDataBinding() {
        return BindingBuilder.bind(updateUserDataQueue())
                .to(uaaExchange())
                .with(UaaRabbit.UPDATE_DATA.routingKey())
                .noargs();
    }


    /**
     * 多规格价格修改
     */
    @Bean
    public Queue distributeUpdateSkuPriceQueue() {
        return QueueBuilder.durable(DistributeRabbitQueueNames.UPDATE_PRODUCT_SKU_PRICE)
                .build();
    }

    @Bean
    public Binding distributeUpdateSkuPriceBinding() {
        return BindingBuilder.bind(distributeUpdateSkuPriceQueue())
                .to(storageExchange())
                .with(StorageRabbit.UPDATE_SKU_PRICE.routingKey())
                .noargs();
    }

    /**
     * 商品名称修改
     */
    @Bean
    public Queue distributeProductNameUpdateQueue() {
        return QueueBuilder.durable(DistributeRabbitQueueNames.PRODUCT_NAME_UPDATE)
                .build();
    }

    @Bean
    public Binding distributeProductNameUpdateBinding() {
        return BindingBuilder.bind(distributeProductNameUpdateQueue())
                .to(goodsExchange())
                .with(GoodsRabbit.GOODS_NAME_UPDATE.routingKey())
                .noargs();
    }


}