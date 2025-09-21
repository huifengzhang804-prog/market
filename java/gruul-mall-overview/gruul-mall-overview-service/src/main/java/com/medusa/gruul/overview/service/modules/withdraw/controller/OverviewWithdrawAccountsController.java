package com.medusa.gruul.overview.service.modules.withdraw.controller;

import cn.hutool.json.JSONObject;
import com.medusa.gruul.common.log.annotation.Log;
import com.medusa.gruul.common.model.resp.Result;
import com.medusa.gruul.common.security.resource.helper.ISecurity;
import com.medusa.gruul.overview.api.enums.DrawType;
import com.medusa.gruul.overview.service.model.dto.WithdrawAccountDTO;
import com.medusa.gruul.overview.service.modules.withdraw.service.WithdrawAccountService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * 提现用户 控制层
 *
 * @author xiaoq
 * @Description OverviewWithdrawAccountsController.java
 * @date 2023-09-08 13:44
 */
@RestController
@RequiredArgsConstructor
@PreAuthorize("@S.matcher().role(@S.R.USER).match()")
@RequestMapping("/withdraw/bonus")
public class OverviewWithdrawAccountsController {
    private final WithdrawAccountService withdrawAccountService;

    /**
     * 查询我的佣金提现账户
     *
     * @return 提现账户详情 key:提现方式 value:账户详情
     */
    @Log("查询我的佣金提现账户")
    @GetMapping("/accounts")
    public Result<Map<DrawType, JSONObject>> getMyAccounts() {
        return Result.ok(
                withdrawAccountService.getAccountsByUserId(ISecurity.userMust().getId())
        );
    }

    /**
     * 编辑提现方式
     *
     * @param account 提现方式与账户资料
     * @return void
     */
    @Log("编辑提现方式")
    @PutMapping("/account")
    public Result<Void> editAccount(@RequestBody @Valid WithdrawAccountDTO account) {
        withdrawAccountService.editAccount(ISecurity.userMust().getId(), account);
        return Result.ok();
    }
}
