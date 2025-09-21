package com.medusa.gruul.overview.service.mp.base.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.medusa.gruul.overview.service.mp.base.entity.OverviewShopBalance;
import org.apache.ibatis.annotations.Param;

/**
 * 店铺余额 mapper
 *
 * @author 张治保
 * @since 2022-11-28
 */
public interface OverviewShopBalanceMapper extends BaseMapper<OverviewShopBalance> {

    /**
     * 基于乐观锁扣减店铺undrawn
     * @param overviewShopBalance 店铺余额对象, {@link OverviewShopBalance}
     * @return 受影响的行数
     */
    int decrementUndrawnOfShop(@Param("shopBalance") OverviewShopBalance overviewShopBalance);
}
