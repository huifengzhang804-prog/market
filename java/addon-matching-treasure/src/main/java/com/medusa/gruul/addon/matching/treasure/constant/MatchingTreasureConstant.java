package com.medusa.gruul.addon.matching.treasure.constant;

public interface MatchingTreasureConstant {

    /**
     * 套餐详情页基本信息key
     */
    String SET_MEAL_BASIC_INFO = "addon:set:meal:basic.info";

    /**
     * 套餐key
     */
    String SET_MEAL_EXIST_KEY = "addon:set:meal:shop:key";


    String SET_MEAL_EXIST_REDIS_LOCK_KEY = "GA7jBxssXdf0k";

    /**
     * xxlJobHandler
     */
    String SET_MEAL_XXL_JOB_HANDLER = "matchingTreasureJobHandler";


    /**
     * 应付金额
     */
    String AMOUNT_RECEIVABLE_SQL_TEMPLATE = "amount_receivable = amount_receivable - {}";

}
