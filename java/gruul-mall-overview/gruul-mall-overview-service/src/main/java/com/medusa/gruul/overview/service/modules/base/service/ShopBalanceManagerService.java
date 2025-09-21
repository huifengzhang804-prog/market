package com.medusa.gruul.overview.service.modules.base.service;

import com.medusa.gruul.overview.service.mp.base.entity.OverviewShopBalance;

/**
 * @author 张治保
 * date 2022/11/28
 */
public interface ShopBalanceManagerService {

    /**
     * 生成默认数据的店铺余额
     *
     * @param shopId 店铺id
     */
    OverviewShopBalance getOrCreateShopBalance(Long shopId);

    /**
     * 根据店铺id查询店铺余额信息
     *
     * @param shopId 店铺id
     * @return 店铺余额信息
     */
    OverviewShopBalance get(Long shopId);

    /**
     * 增加未结算余额
     *
     * @param shopId 店铺id
     * @param amount 未结算余额
     */
    void uncompletedBalanceIncrement(Long shopId, Long amount);


    /**
     * 使用未结算余额
     *
     * @param shopId 店铺id
     * @param amount 未结算余额
     */
    void undrawnBalanceDecrement(Long shopId, Long amount);

    /**
     * 增加未提现与余额
     *
     * @param updateTotal 是否更新总额
     * @param shopId      店铺id
     * @param amount      未结算余额
     */
    void undrawnBalanceIncrement(boolean updateTotal, Long shopId, Long amount);


    /**
     * 订单完成 加 总额与带提现余额
     *
     * @param shopId 店铺id
     * @param amount 额度
     */
    void orderCompleted(Long shopId, Long amount);

    /**
     * 更新店铺余额
     *
     * @param shopId 店铺id
     * @param amount 金额
     */
    void updateTotalByShopId(Long shopId, Long amount);

}
