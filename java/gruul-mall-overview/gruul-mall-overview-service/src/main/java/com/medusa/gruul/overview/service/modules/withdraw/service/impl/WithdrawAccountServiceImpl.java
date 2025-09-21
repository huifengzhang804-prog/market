package com.medusa.gruul.overview.service.modules.withdraw.service.impl;

import cn.hutool.core.map.MapUtil;
import cn.hutool.json.JSONObject;
import com.medusa.gruul.common.redis.annotation.Redisson;
import com.medusa.gruul.overview.api.entity.OverviewWithdrawAccounts;
import com.medusa.gruul.overview.api.enums.DrawType;
import com.medusa.gruul.overview.api.model.DrawTypeModel;
import com.medusa.gruul.overview.service.model.OverviewConstant;
import com.medusa.gruul.overview.service.model.dto.WithdrawAccountDTO;
import com.medusa.gruul.overview.service.modules.withdraw.service.WithdrawAccountService;
import com.medusa.gruul.overview.service.mp.withdraw.service.IOverviewWithdrawAccountService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * 提现用户服务实现层
 *
 * @author xiaoq
 * @Description WithdrawAccountServiceImpl.java
 * @date 2023-09-08 13:47
 */
@Service
@RequiredArgsConstructor
public class WithdrawAccountServiceImpl implements WithdrawAccountService {
    private final IOverviewWithdrawAccountService overviewWithdrawAccountService;

    @Override
    public Map<DrawType, JSONObject> getAccountsByUserId(Long userId) {
        return overviewWithdrawAccountService.lambdaQuery()
                .eq(OverviewWithdrawAccounts::getUserId, userId)
                .oneOpt()
                .map(OverviewWithdrawAccounts::getAccounts)
                .orElseGet(MapUtil::newHashMap);
    }

    /**
     * todo  有 bug 未加上参数校验
     *
     * @see DrawType#getModelFunction()
     * @see DrawTypeModel#validParam()
     */
    @Override
    @Redisson(name = OverviewConstant.OVERVIEW_ACCOUNTS_LOCK_KEY, key = "#userId")
    public void editAccount(Long userId, WithdrawAccountDTO account) {
        Map<DrawType, JSONObject> accounts = this.getAccountsByUserId(userId);
        accounts.put(account.getType(), account.getDetail());
        overviewWithdrawAccountService.saveOrUpdate(
                new OverviewWithdrawAccounts()
                        .setUserId(userId)
                        .setAccounts(accounts)
        );
    }

}
