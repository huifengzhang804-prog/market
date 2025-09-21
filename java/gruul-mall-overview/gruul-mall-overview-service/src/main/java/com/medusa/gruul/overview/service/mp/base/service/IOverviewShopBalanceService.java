package com.medusa.gruul.overview.service.mp.base.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.medusa.gruul.overview.service.mp.base.entity.OverviewShopBalance;

/**
 * 店铺余额 服务类
 *
 * @author 张治保
 */
public interface IOverviewShopBalanceService extends IService<OverviewShopBalance> {


    /**
     * 基于乐观锁扣减店铺余额
     *
     * @param overviewShopBalance
     */
    void decrementUndrawnOfShop(OverviewShopBalance overviewShopBalance);

}
