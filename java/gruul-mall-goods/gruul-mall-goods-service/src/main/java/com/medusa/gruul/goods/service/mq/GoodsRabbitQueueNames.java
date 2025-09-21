package com.medusa.gruul.goods.service.mq;

/**
 * @author 张治保
 * @date 2022/12/5
 */
public interface GoodsRabbitQueueNames {

    /**
     * 店铺状态改变 启用禁用
     */
    String SHOP_CHANGE_QUEUE = "goods.shop.change.queue";

    /**
     * 订单完成(评价后)
     */
    String ORDER_ACCOMPLISH_QUEUE = "goods.order.accomplish.queue";

    /**
     * 店鋪信息修改
     */
    String SHOP_INFO_UPDATE_QUEUE = "goods.shop.info.update.queue";

    /**
     * 供应商更新商品状态
     */
    String SUPPLIER_GOODS_UPDATE_STATUS_QUEUE = "supplier.goods.update.status.queue";

    /**
     * 强制删除商品
     */
    String SUPPLIER_FORCE_GOODS_STATUS_QUEUE = "supplier.force.goods.status.queue";

    /**
     * 供应商代销商品修改
     */
    String SUPPLIER_UPDATE_GOODS = "supplier.update.goods";

    /**
     * 店铺签约分类自定义折扣率发生变更的监听队列
     */
    String SIGNING_CATEGORY_CUSTOM_DEDUCTION_RATIO_CHANGE_QUEUE = "platform.shop.signing.category.custom.deduction.ratio.change.queue";

    /**
     * 商品sku修改价格
     */
    String PRODUCT_SKU_PRICE_UPDATE = "goods.sku.price.update";
    /**
     * 商品的销量变更
     */
    String PRODUCT_SALES_UPDATE = "goods.product.sale.count.update";
}
