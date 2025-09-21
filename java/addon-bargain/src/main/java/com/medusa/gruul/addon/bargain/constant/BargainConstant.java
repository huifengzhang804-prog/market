package com.medusa.gruul.addon.bargain.constant;

/**
 * 砍价常量
 * @author wudi
 */
public interface BargainConstant {

    /**
     * 砍价商品详情sku缓存key
     */
    String BARGAIN_PRODUCT_DETAIL_CACHE_KEY = "addon:bargain:product:detail";

    /**
     * 砍价商品redis lock key
     */
    String BARGAIN_PRODUCT_REDIS_LOCK_KEY = "addon:bargain:product:redis:lock:key";

    /**
     * 发起砍价商品金额缓存key
     */
    String BARGAIN_SPONSOR_PRODUCT_AMOUNT_CACHE_KEY = "addon:bargain:sponsor:product:amount";

    /**
     * 发起砍价商品sku缓存key
     */
    String BARGAIN_SPONSOR_PRODUCT_SKU_CACHE_KEY = "addon:bargain:sponsor:product:sku";

    /**
     * 不是砍价商品
     */
    String NOT_BARGAIN_PRODUCT = "addon:not_bargain_product";

    /**
     * 砍价活动是否存在
     */
    String BARGAIN_ACTIVITY_EXIST = "addon:bargain:activity:exist";
    /**
     * 砍价好友
     */
    String BARGAIN_FRIENDS = "addon:bargain:friends";

    /**
     * 帮砍金额
     */
    String BARGAIN_HELP_PEOPLE_COUNT = "amountCut";


    /**
     * 砍价发起状态
     */
    String BARGAIN_SPONSOR_STATUS = "bargainSponsorSkuStatus";

    /**
     * 砍价人数
     */
    String BARGAIN_HELP_PEOPLE_SQL_TEMPLATE = "bargaining_people = bargaining_people + {}";
    /**
     * xxlJobHandler
     */
    String BARGAIN_XXL_JOB_HANDLER = "bargainJobHandler";
}
