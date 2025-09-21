package com.medusa.gruul.storage.service.mq;

import com.medusa.gruul.goods.api.enums.GoodsRabbit;
import com.medusa.gruul.order.api.enums.OrderRabbit;
import com.medusa.gruul.storage.api.enums.StorageRabbit;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.*;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 路由模式
 *
 * @author 张治保
 * date 2022/5/25
 */
@Slf4j
@Configuration
public class StorageRabbitConfig {

    /**
     * storage 交换机
     */
    @Bean
    @ConditionalOnMissingBean(name = "storageExchange")
    public Exchange storageExchange() {
        return ExchangeBuilder.directExchange(StorageRabbit.EXCHANGE)
                .durable(true)
                .build();
    }

    /**
     * order 交换机
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
     * 商品交换机
     */
    @Bean
    @ConditionalOnMissingBean(name = "goodsExchange")
    public Exchange goodsExchange() {
        return ExchangeBuilder.directExchange(GoodsRabbit.EXCHANGE)
                .durable(true)
                .build();
    }

    /**
     * 订单创建失败队列
     */
    @Bean
    public Queue orderCreateFailedQueue() {
        return QueueBuilder.durable(StorageQueueNames.ORDER_CREATE_FAILED_QUEUE)
                .build();
    }

    @Bean
    public Binding orderCreateFailedBinding() {
        return BindingBuilder.bind(orderCreateFailedQueue())
                .to(orderExchange())
                .with(OrderRabbit.ORDER_CREATE_FAILED.routingKey())
                .noargs();
    }

    /**
     * 订单关闭
     */
    @Bean
    public Queue orderClosedQueue() {
        return QueueBuilder.durable(StorageQueueNames.ORDER_CLOSED)
                .build();
    }

    @Bean
    public Binding orderClosedBinding() {
        return BindingBuilder.bind(orderClosedQueue())
                .to(orderExchange())
                .with(OrderRabbit.ORDER_CLOSE.routingKey())
                .noargs();
    }

    /**
     * 归还库存
     */
    @Bean
    public Queue orderReturnStockQueue() {
        return QueueBuilder.durable(StorageQueueNames.RETURN_STOCK)
                .build();
    }

    @Bean
    public Binding orderReturnStockBinding() {
        return BindingBuilder.bind(orderReturnStockQueue())
                .to(storageExchange())
                .with(StorageRabbit.RETURN_STOCK.routingKey())
                .noargs();
    }

    /**
     * 修改库存队列
     */
    @Bean
    public Queue updateSkuStockQueue() {
        return QueueBuilder
                .durable(StorageQueueNames.UPDATE_SKU_STOCK_QUEUE)
                .build();
    }

    @Bean
    public Binding lockSkuStockBinding() {
        return BindingBuilder.bind(updateSkuStockQueue())
                .to(storageExchange())
                .with(StorageRabbit.UPDATE_SKU_STOCK.routingKey())
                .noargs();
    }


    /**
     * 删除商品对应库存队列
     */
    @Bean
    public Queue productDeleteStorageSkuQueue() {
        return QueueBuilder.durable(StorageQueueNames.PRODUCT_DELETE)
                .build();
    }

    /**
     * 删除商品对应库存绑定关系
     */
    @Bean
    public Binding productDeleteStorageSkuBinding() {
        return BindingBuilder.bind(productDeleteStorageSkuQueue())
                .to(goodsExchange())
                .with(GoodsRabbit.GOODS_DELETE.routingKey())
                .noargs();
    }

}
