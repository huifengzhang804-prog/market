package com.medusa.gruul.addon.distribute.service;

import com.medusa.gruul.addon.distribute.model.DistributorBonus;
import com.medusa.gruul.addon.distribute.model.dto.WithdrawDTO;
import com.medusa.gruul.addon.distribute.model.vo.DistributorWithdraw;
import com.medusa.gruul.common.security.model.bean.SecureUser;
import com.medusa.gruul.overview.api.entity.OverviewWithdraw;

import java.util.Map;

/**
 * @author 张治保
 * date 2023/5/16
 */
public interface DistributeBonusService {

    /**
     * 用户分佣
     *
     * @param distributorBonusMap 分销商用户佣金分配map
     *                            key: 分销商用户id
     *                            value: 分销商用户佣金分配
     */
    void shareBonusToUser(Map<Long, DistributorBonus> distributorBonusMap);


    /**
     * 用户提现检查
     *
     * @param openid openId
     * @param userId 用户id
     * @return 提现检查结果
     */
    DistributorWithdraw withdrawCheck(String openid, Long userId);

    /**
     * 创建提现工单
     *
     * @param withdraw 提现明细
     */
    void createWithdrawOrder(WithdrawDTO withdraw, SecureUser secureUser);


    /**
     * 提现工单被拒绝
     *
     * @param overviewWithdraw 提现工单信息
     */
    void withdrawOrderForbidden(OverviewWithdraw overviewWithdraw);

    /**
     * 未支付订单关单 增加已失效的佣金
     *
     * @param bonusMap
     */
    void changeBonusInvalidCommission(Map<Long, Long> bonusMap);
}
