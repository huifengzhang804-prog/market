package com.medusa.gruul.overview.service.modules.withdraw.service;

import cn.hutool.json.JSONObject;
import com.medusa.gruul.overview.api.enums.DrawType;
import com.medusa.gruul.overview.service.model.dto.WithdrawAccountDTO;

import java.util.Map;

/**
 * 提现账户服务层
 *
 * @author xiaoq
 * @Description WithdrawAccountService.java
 * @date 2023-09-08 13:46
 */
public interface WithdrawAccountService {
    /**
     * 根据用户id查询用户佣金提现账户
     *
     * @param userId 用户id
     * @return 用户提现账号详情 可能为空 key:提现方式 value:账户详情
     */
    Map<DrawType, JSONObject> getAccountsByUserId(Long userId);

    /**
     * 编辑用户提现账户
     *
     * @param userId  分销商用户id
     * @param account 账户详情
     */
    void editAccount(Long userId, WithdrawAccountDTO account);
}
