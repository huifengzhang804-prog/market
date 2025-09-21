package com.medusa.gruul.addon.matching.treasure.model;

public interface MatchingTreasureErrorCode {

    /**
     * 套餐活动保存失败
     */
    Integer SET_MEAL_SAVE_FAILURE = 79001;

    /**
     * 套餐商品不在规定范围
     */
    Integer SET_MEAL_PRODUCT_OUT_OF_RANGE = 79002;


    /**
     * 套餐活动不存在
     */
    Integer SET_MEAL_NOT_EXIST = 79003;


    /**
     * 套餐活动进行中
     */
    Integer SET_MEAL_PROCESSING = 79004;

    /**
     * 套餐活动已下架
     */
    Integer SET_MEAL_SELL_OF = 79005;

    /**
     * 套餐商品重复
     */
    Integer SET_MEAL_PRODUCT_REPEAT = 79006;

}
