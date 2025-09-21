package com.medusa.gruul.addon.supplier.mq;

/**
 * @author 张治保
 * date 2023/7/21
 */
public interface SupplierQueueNames {

    /**
     * 供应商信息同步队列
     */
    String SUPPLIER_INFO_SYNC_QUEUE = "supplier.info.sync";

    /**
     * 供应商订单超时支付自动关闭队列
     */
    String SUPPLIER_ORDER_AUTO_PAID_TIMEOUT_CLOSE_QUEUE = "supplier.order.auto.paid.timeout.close";

    /**
     * 监听供应商启用禁用
     */
    String SUPPLIER_ENABLE_DISABLE_QUEUE = "supplier.enable.disable";

    /**
     * 供应商商品库存变更的队列
     */
    String SUPPLIER_GOODS_STOCK_CHANGE_QUEUE="supplier.goods.stock.change";

    /**
     * 供应商签约分类自定义折扣率供发生变更应商商品的监听队列
     */
    String SIGNING_CATEGORY_CUSTOM_DEDUCTION_RATIO_CHANGE_SUPPLIER_GOODS_QUEUE = "platform.supplier.signing.category.custom.deduction.ratio.change.supplier.goods.queue";
}
