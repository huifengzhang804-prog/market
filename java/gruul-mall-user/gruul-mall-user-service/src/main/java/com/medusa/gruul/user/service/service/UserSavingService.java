package com.medusa.gruul.user.service.service;

import com.medusa.gruul.payment.api.model.pay.PayResult;
import com.medusa.gruul.user.service.model.dto.UserPayDTO;
import com.medusa.gruul.user.service.model.dto.UserSavingRuleDTO;
import com.medusa.gruul.user.service.mp.entity.UserSavingRule;

/**
 * 
 * 用户储值service
 *
 * @author xiaoq
 * @Description 用户储值service
 * @date 2022-09-01 14:03
 */
public interface UserSavingService {
    /**
     * 获取储值信息
     *
     * @return 用户储蓄规则
     */
    UserSavingRule  getSavingManageInfo();

    /**
     * 设置储值信息状态
     *
     * @param status 储值信息状态
     */
    void setSavingManageSwitch(Boolean status);

    /**
     *  编辑用户储值信息
     *
     * @param userSavingRule 用户储蓄dto
     */
    void editSavingManageInfo(UserSavingRuleDTO userSavingRule);

    /**
     * 用户储值支付
     *
     * @param userPay 用户支付信息
     * @return 支付结果
     */
    PayResult userSavingPay(UserPayDTO userPay);
}
