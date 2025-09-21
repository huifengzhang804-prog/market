package com.medusa.gruul.overview.service.mp.base.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.medusa.gruul.overview.service.model.enums.OverviewError;
import com.medusa.gruul.overview.service.mp.base.entity.OverviewShopBalance;
import com.medusa.gruul.overview.service.mp.base.mapper.OverviewShopBalanceMapper;
import com.medusa.gruul.overview.service.mp.base.service.IOverviewShopBalanceService;
import org.springframework.stereotype.Service;

/**
 * 店铺余额 服务实现类
 *
 * @author 张治保
 */
@Service
public class OverviewShopBalanceServiceImpl extends ServiceImpl<OverviewShopBalanceMapper, OverviewShopBalance> implements IOverviewShopBalanceService {

    /**
     * 基于乐观锁扣减店铺undrawn
     *
     * @param overviewShopBalance 店铺余额对象, {@link OverviewShopBalance}
     */
    @Override
    public void decrementUndrawnOfShop(OverviewShopBalance overviewShopBalance) {
        int affectedRows = baseMapper.decrementUndrawnOfShop(overviewShopBalance);
        OverviewError.INSUFFICIENT_BALANCE.trueThrow(affectedRows != 1);
    }

}
