package com.medusa.gruul.search.service.mq;

/**
 * 检索队列
 *
 * @author WuDi
 * date 2022/10/25
 */
public interface SearchQueueNames {

    /**
     * 商品发布
     */
    String PRODUCT_RELEASE = "es.product.release";

    /**
     * 商品更新
     */
    String PRODUCT_UPDATE = "es.product.update";

    /**
     * 商品删除
     */
    String PRODUCT_DELETE = "es.product.delete";

    /**
     * 商品标签删除
     */
    String PRODUCT_LABEL_DELETE = "es.product.label.delete";
    /**
     * 商品标签更新
     */
    String PRODUCT_LABEL_UPDATE = "es.product.label.update";

    /**
     * 商品状态改变
     */
    String PRODUCT_STATUS_UPDATE = "es.product.status";

    /**
     * 增加 库存与销量
     */
    String UPDATE_PRODUCT_STOCK = "es.product.sales.increment";

    /**
     * 类目删除
     */
    String CLASSIFY_REMOVE = "es.search.classify.remove";

    /**
     * 启用禁用店铺
     */
    String SHOP_STATUS_CHANGE = "es.shop.status.change";

    /**
     * 店铺更新
     */
    String SHOP_UPDATE = "es.shop.update";

    /**
     * 多规格价格修改
     */
    String UPDATE_SKU_PRICE = "update.sku.price";


    /**
     * 用户搜索店铺
     */
    String USER_SEARCH_SHOP = "user.search.shop";

    /**
     * 用户支付订单
     */
    String USER_PAY_ORDER = "user.pay.order";


    /**
     * 用户关注店铺
     */
    String USER_ATTENTION_SHOP = "es.user.attention.shop";

    /**
     * 用户足迹新增
     */
    String USER_FOOT_MARK_ADD = "user.foot.mark.add";

    /**
     * 商品名称修改
     */
    String PRODUCT_NAME_UPDATE = "es.product.name.update";
}
