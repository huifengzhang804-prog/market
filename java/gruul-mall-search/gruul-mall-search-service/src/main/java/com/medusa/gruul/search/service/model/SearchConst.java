package com.medusa.gruul.search.service.model;

/**
 * @author 张治保
 * date 2022/12/5
 */
public interface SearchConst {

    /**
     * 检索分类ID搜索历史
     */
    String CATEGORYID_SELECT_HISTORY = "gruul:mall:search:category:select";

    /**
     * 更新商品库存 分布式锁
     */
    String PRODUCT_UPDATE_SKU_LOCK = "gruul:mall:search:product:update:sku:lock";


    /**
     * 素材分类新增分布式锁
     */
    String MATERIAL_CATEGORY_ADD_LOCK = "gruul:mall:search:material:category:add:lock";


    /**
     * 聚合最大尺寸
     */
    int AGGREGATION_MAX_SIZE = 500;
}
