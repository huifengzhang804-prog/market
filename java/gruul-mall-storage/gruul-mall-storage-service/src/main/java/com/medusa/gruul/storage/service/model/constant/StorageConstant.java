package com.medusa.gruul.storage.service.model.constant;

import com.medusa.gruul.storage.api.enums.StockType;

/**
 * @author 张治保
 * date 2022/7/6
 */
public interface StorageConstant {

    /**
     * 缓存订单号，防止重复消费
     */
    String CACHE_KEY_ORDER_NO = "gruul:mall:storage:order:no";


    /**
     * 商品仓储缓存
     * gruul:mall:order:product:{shopId}:{productId}:{skuId}
     */
    String CACHE_KEY_PRODUCT_SKU_STORAGE = "gruul:mall:storage:product";
    /**
     * 限购类型 字段名
     */
    String FIELD_SKU_STORAGE_LIMIT_TYPE = "limitType";
    /**
     * 限购数量字段名
     */
    String FIELD_SKU_STORAGE_LIMIT_NUM = "limitNum";


    /**
     * 销售价格字段名
     */
    String FIELD_SKU_STORAGE_SALE_PRICE = "salePrice";

    /**
     * 原价字段名
     */
    String FIELD_SKU_STORAGE_PRICE = "price";

    /**
     * 销量 增减 sql模板
     */
    String SQL_SALES_VOLUME_INCREMENT_SQL_TEMPLATE = "sales_volume = sales_volume+{}";

    /**
     * 库存 增减 sql模板
     */
    String SQL_STOCK_INCREMENT_SQL_WITH_STOCK_TYPE_TEMPLATE = "stock = stock + IF(stock_type = " + StockType.UNLIMITED.getValue() + ",0,{})";

    /**
     * 库存条件判断sql模板 当库存类型为无限时，不比较剩余库存
     */
    String SQL_STOCK_CONDITION_SQL_TEMPLATE = "IF(stock_type = " + StockType.UNLIMITED.getValue() + ",1,stock >= {})";


    String STORAGE_MANAGEMENT_ORDER_NO_PREFIX = "SMO";


    /**
     * 商品仓储管理
     * gruul:mall:management:storage:product
     */
    String CACHE_KEY_MANAGEMENT_PRODUCT_SKU_STORAGE = "gruul:mall:management:storage:product";

    String STORAGE_MANAGEMENT_ORDER = "gruul:mall:storage:management:order";


    /**
     * 供应商主订单号 前缀
     */
    String SUPPLIER_MAIN_NO_PREFIX = "MSP";

    /**
     * 供应商订单号前缀
     */
    String SUPPLIER_NO_PREFIX = "SUP";


    /**
     * 商品代销关系维护 缓存 key
     */
    String CACHE_KEY_CONSIGNMENT_RELATION = "gruul:mall:storage:consignment:relation";

    /**
     * 供应商查询商品被哪些店铺代销了
     */
    String CACHE_KEY_CONSIGNMENT_SUPPLIER_RELATION = "gruul:mall:storage:consignment:relation";

    /**
     * 是否是供应商缓存 bitmap key
     * 用以缓存店铺 id 判断是否是供应商
     */
    String CACHE_KEY_SUPPLIER_EXISTS = "gruul:mall:storage:supplier:exists";
}
