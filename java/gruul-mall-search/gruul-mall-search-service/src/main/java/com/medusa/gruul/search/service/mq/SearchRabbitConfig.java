package com.medusa.gruul.search.service.mq;

import com.medusa.gruul.goods.api.enums.GoodsRabbit;
import com.medusa.gruul.order.api.enums.OrderRabbit;
import com.medusa.gruul.search.api.enums.SearchRabbit;
import com.medusa.gruul.shop.api.enums.ShopRabbit;
import com.medusa.gruul.storage.api.enums.StorageRabbit;
import com.medusa.gruul.user.api.enums.UserRabbit;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.*;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * rabbitmq配置
 *
 * @author WuDi
 * date 2022/9/28
 */
@Configuration
@RequiredArgsConstructor
@Slf4j
public class SearchRabbitConfig {

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
     * Search交换机
     */
    @Bean
    @ConditionalOnMissingBean(name = "searchExchange")
    public Exchange searchExchange() {
        return ExchangeBuilder.directExchange(SearchRabbit.EXCHANGE)
                .durable(Boolean.TRUE)
                .build();
    }

    /**
     * 店铺交换机
     */
    @Bean
    @ConditionalOnMissingBean(name = "shopExchange")
    public Exchange shopExchange() {
        return ExchangeBuilder.directExchange(ShopRabbit.EXCHANGE)
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
                .durable(Boolean.TRUE)
                .delayed()
                .build();
    }

    /**
     * 用户交换机
     */
    @Bean
    @ConditionalOnMissingBean(name = "userExchange")
    public Exchange userExchange() {
        return ExchangeBuilder
                .directExchange(UserRabbit.EXCHANGE)
                .durable(true)
                .delayed()
                .build();
    }


    /**
     * 发布商品
     */
    @Bean
    public Queue productReleaseQueue() {
        return QueueBuilder.durable(SearchQueueNames.PRODUCT_RELEASE)
                .build();
    }

    @Bean
    public Binding productReleaseBinding() {
        return BindingBuilder.bind(productReleaseQueue())
                .to(goodsExchange())
                .with(GoodsRabbit.GOODS_RELEASE.routingKey())
                .noargs();
    }

    /**
     * 商品更新
     */
    @Bean
    public Queue productUpdateQueue() {
        return QueueBuilder.durable(SearchQueueNames.PRODUCT_UPDATE)
                .build();
    }

    @Bean
    public Binding productUpdateBinding() {
        return BindingBuilder.bind(productUpdateQueue())
                .to(goodsExchange())
                .with(GoodsRabbit.GOODS_UPDATE.routingKey())
                .noargs();
    }

    /**
     * 商品删除
     */
    @Bean
    public Queue productDeleteQueue() {
        return QueueBuilder.durable(SearchQueueNames.PRODUCT_DELETE)
                .build();
    }

    @Bean
    public Binding productDeleteBinding() {
        return BindingBuilder.bind(productDeleteQueue())
                .to(goodsExchange())
                .with(GoodsRabbit.GOODS_DELETE.routingKey())
                .noargs();
    }

    /**
     * 商品状态改变
     */
    @Bean
    public Queue productStatusUpdateQueue() {
        return QueueBuilder.durable(SearchQueueNames.PRODUCT_STATUS_UPDATE)
                .build();
    }

    @Bean
    public Binding productStatusUpdateBinding() {
        return BindingBuilder.bind(productStatusUpdateQueue())
                .to(goodsExchange())
                .with(GoodsRabbit.GOODS_UPDATE_STATUS.routingKey())
                .noargs();
    }


    /**
     * 商品库存更新
     */
    @Bean
    public Queue updateProductSkuStockQueue() {
        return QueueBuilder.durable(SearchQueueNames.UPDATE_PRODUCT_STOCK)
                .build();
    }

    @Bean
    public Binding updateProductSkuStockBinding() {
        return BindingBuilder.bind(updateProductSkuStockQueue())
                .to(storageExchange())
                .with(StorageRabbit.UPDATE_PRODUCT_STOCK.routingKey())
                .noargs();
    }


    /**
     * 平台分类删除
     */
    @Bean
    public Queue removeClassifyQueue() {
        return QueueBuilder.durable(SearchQueueNames.CLASSIFY_REMOVE).build();
    }

    @Bean
    public Binding removeClassifyBinding() {
        return BindingBuilder.bind(removeClassifyQueue())
                .to(searchExchange())
                .with(SearchRabbit.CLASSIFY_REMOVE.routingKey())
                .noargs();
    }

    /**
     * 店铺状态改变
     */
    @Bean
    public Queue shopStatusChangeQueue() {
        return QueueBuilder.durable(SearchQueueNames.SHOP_STATUS_CHANGE)
                .build();
    }

    @Bean
    public Binding shopStatusChangeBinding() {
        return BindingBuilder.bind(shopStatusChangeQueue())
                .to(shopExchange())
                .with(ShopRabbit.SHOP_ENABLE_DISABLE.routingKey())
                .noargs();
    }

    /**
     * 多规格价格修改
     */
    @Bean
    public Queue updateSkuPriceQueue() {
        return QueueBuilder.durable(SearchQueueNames.UPDATE_SKU_PRICE)
                .build();
    }

    @Bean
    public Binding updateSkuPriceBinding() {
        return BindingBuilder.bind(updateSkuPriceQueue())
                .to(storageExchange())
                .with(StorageRabbit.UPDATE_SKU_PRICE.routingKey())
                .noargs();
    }

    /**
     * 店铺更新队列（店铺名称、店铺类型）
     */
    @Bean
    public Queue searchShopUpdateQueue() {
        return QueueBuilder.durable(SearchQueueNames.SHOP_UPDATE)
                .build();
    }

    @Bean
    public Binding searchShopUpdateQueueBinding() {
        return BindingBuilder.bind(searchShopUpdateQueue())
                .to(shopExchange())
                .with(ShopRabbit.SHOP_UPDATE.routingKey())
                .noargs();
    }

    /**
     * 用户搜索店铺
     */
    @Bean
    public Queue userSearchShopQueue() {
        return QueueBuilder.durable(SearchQueueNames.USER_SEARCH_SHOP)
                .build();
    }

    @Bean
    public Binding userSearchShopBinding() {
        return BindingBuilder.bind(userSearchShopQueue())
                .to(shopExchange())
                .with(ShopRabbit.SHOP_SEARCH.routingKey())
                .noargs();
    }

    /**
     * 用户支付订单
     */
    @Bean
    public Queue userPayOrder() {
        return QueueBuilder.durable(SearchQueueNames.USER_PAY_ORDER)
                .build();
    }

    @Bean
    public Binding userPayOrderBinding() {
        return BindingBuilder.bind(userPayOrder())
                .to(orderExchange())
                .with(OrderRabbit.ORDER_PAID_BROADCAST.routingKey())
                .noargs();
    }

    /**
     * 用户关注店铺
     */
    @Bean
    public Queue userAttentionShop() {
        return QueueBuilder.durable(SearchQueueNames.USER_ATTENTION_SHOP)
                .build();
    }

    @Bean
    public Binding userAttentionShopBinding() {
        return BindingBuilder.bind(userAttentionShop())
                .to(goodsExchange())
                .with(GoodsRabbit.SHOP_ATTENTION.routingKey())
                .noargs();
    }

    /**
     * 用户足迹新增
     */
    @Bean
    public Queue userFootMarkAdd() {
        return QueueBuilder.durable(SearchQueueNames.USER_FOOT_MARK_ADD)
                .build();
    }

    @Bean
    public Binding userFootMarkAddBinding() {
        return BindingBuilder.bind(userFootMarkAdd())
                .to(userExchange())
                .with(UserRabbit.USER_FOOTPRINT_ADD.routingKey())
                .noargs();
    }

    /**
     * 商品标签删除
     */
    @Bean
    public Queue productLabelDeleteQueue() {
        return QueueBuilder.durable(SearchQueueNames.PRODUCT_LABEL_DELETE)
                .build();
    }

    @Bean
    public Binding productLabelDeleteBinding() {
        return BindingBuilder.bind(productLabelDeleteQueue())
                .to(goodsExchange())
                .with(GoodsRabbit.GOODS_LABEL_DELETE.routingKey())
                .noargs();
    }

    /**
     * 商品标签更新队列
     * @return
     */
    @Bean
    public Queue productLabelUpdate(){
        return QueueBuilder.durable(SearchQueueNames.PRODUCT_LABEL_UPDATE)
                .build();
    }
    @Bean
    public Binding productLabelUpdateBinding(){
        return BindingBuilder.bind(productLabelUpdate())
                .to(goodsExchange())
                .with(GoodsRabbit.GOODS_LABEL_UPDATE.routingKey())
                .noargs();
    }

    /**
     * 商品名称修改
     */
    @Bean
    public Queue productNameUpdateQueue() {
        return QueueBuilder.durable(SearchQueueNames.PRODUCT_NAME_UPDATE)
                .build();
    }

    @Bean
    public Binding productNameUpdateBinding() {
        return BindingBuilder.bind(productNameUpdateQueue())
                .to(goodsExchange())
                .with(GoodsRabbit.GOODS_NAME_UPDATE.routingKey())
                .noargs();
    }

}
