package com.medusa.gruul.storage.service.mq;

/**
 * @author 张治保
 * date 2022/6/14
 */
public interface StorageQueueNames {

    /**
     * mq异步扣出数据库库存 队列
     */
    String UPDATE_SKU_STOCK_QUEUE = "storage.sku.stock.update";

    /**
     * 订单创建失败队列
     */
    String ORDER_CREATE_FAILED_QUEUE = "storage.stock.return.orderCreateFailed";

    /**
     * 订单关闭
     */
    String ORDER_CLOSED = "storage.stock.return.orderClosed";

    /**
     * 商品删除
     */
    String PRODUCT_DELETE = "storage.product.delete";

    /**
     * 归还库存
     */
    String RETURN_STOCK = "storage.stock.return.queue";

}
