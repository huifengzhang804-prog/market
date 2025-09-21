package com.medusa.gruul.goods.api.constant;

/**
 * 商品常量
 *
 * @author xiaoq
 */
public interface GoodsConstant {


    String BASE_KEY = "gruul:mall:goods:product:";
    /**
     * 插件支持器 id
     */
    String ADDON_SUPPORT_ID = "goodsAddonSupporter";

    /**
     * 商品详情 gruul:mall:goods:product:{shopId}:{productId}
     */
    String GOODS_DETAIL_CACHE_KEY = "gruul:mall:goods:product";
    /**
     * 商品类目详情 gruul:mall:goods:category:{shopId}:{productId}
     */
    String CATEGORY_DETAIL_CACHE_KEY = "gruul:mall:goods:category";

    /**
     * 一键复制商品详情
     * <a href="https://www.99api.com/test?testid=35&commid=25"/>
     */
    String COPY_GOODS_URL = "https://api09.99api.com/{}/detail?apikey={}&itemid={}";

    /**
     * 一键复制成功返回code
     */
    String SUCCESS_CODE = "0000";
    /**
     * JSON返回对象key
     */
    String RET_CODE = "retcode";
    String DATA = "data";
    String ITEM = "item";


    /**
     * 淘宝
     */
    String ID = "id";
    String SEMICOLON = ";";
    String DOUBLE_DASHED = "--";
    String SKU = "sku";
    /**
     * html后缀
     */
    String HTML_SUFFIX = ".html";

    /**
     * 供应商商品常量
     */
    String ADDON_SUPPLIER_NEW_COUNT_PRODUCT_KEY = "newCreatedProduct";
    String ADDON_SUPPLIER_IRREGULARITY_PRODUCT_KEY = "irregularityProduct";

    /**
     * 商品审核常量
     */
    String GOODS_PRODUCT_AUDIT = "gruul:mall:goods:audit";
    /**
     * 商品库存常量
     */
    String CACHE_KEY_PRODUCT_STORAGE = "gruul:mall:goods:product:storage";
    /**
     * 用户关注店铺的key
     */
    String USER_FOLLOW_SHOP_KEY = BASE_KEY + "user:follow:%d:shops";

    /**
     * 销量 增减 sql模板
     */
    String SQL_SALES_VOLUME_INCREMENT_SQL_TEMPLATE = "total_sales_volume = total_sales_volume+{}";

    /**
     * 库存 增减 sql模板
     */
    String SQL_STOCK_INCREMENT_TEMPLATE = "total_stock = total_stock + {}";
}
