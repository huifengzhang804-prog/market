package com.medusa.gruul.overview.service.mp.withdraw.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.medusa.gruul.overview.api.entity.OverviewWithdrawAccounts;
import com.medusa.gruul.overview.service.mp.withdraw.mapper.OverviewWithdrawAccountsMapper;
import com.medusa.gruul.overview.service.mp.withdraw.service.IOverviewWithdrawAccountService;
import org.springframework.stereotype.Service;

/**
 * 提现用户服务实现层
 *
 * @author xiaoq
 * @Description OverviewWithdrawAccountServiceImpl.java
 * @date 2023-09-08 13:24
 */
@Service
public class OverviewWithdrawAccountServiceImpl extends ServiceImpl<OverviewWithdrawAccountsMapper, OverviewWithdrawAccounts> implements IOverviewWithdrawAccountService {
}
