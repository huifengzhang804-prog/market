package com.medusa.gruul.addon.distribute.controller;

import com.medusa.gruul.addon.distribute.model.dto.WithdrawDTO;
import com.medusa.gruul.addon.distribute.model.vo.DistributorWithdraw;
import com.medusa.gruul.addon.distribute.service.DistributeBonusService;
import com.medusa.gruul.common.idem.annotation.Idem;
import com.medusa.gruul.common.log.annotation.Log;
import com.medusa.gruul.common.model.resp.Result;
import com.medusa.gruul.common.security.model.bean.SecureUser;
import com.medusa.gruul.common.security.resource.helper.ISecurity;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


/**
 * 分销佣金控制器
 *
 * @author 张治保 date 2023/5/16
 */
@RestController
@RequiredArgsConstructor
@PreAuthorize("@S.matcher().role(@S.USER).match()")
@RequestMapping("/distribute/bonus")
public class DistributeBonusController {


    private final DistributeBonusService distributeBonusService;

    /**
     * 查询佣金提现检查
     *
     * @return 佣金提现检查
     */
    @Log("佣金提现检查")
    @GetMapping("/withdraw")
    public Result<DistributorWithdraw> withdrawCheck() {
        SecureUser user = ISecurity.userMust();
        return Result.ok(
                distributeBonusService.withdrawCheck(user.getOpenid(), user.getId())
        );
    }


    /**
     * 提交佣金提现工单
     *
     * @param withdraw 提现方式
     * @return void
     */
    @Idem
    @Log("提交佣金提现工单")
    @PostMapping("/withdraw")
    public Result<Void> createWithdrawOrder(@RequestBody @Valid WithdrawDTO withdraw) {
        withdraw.getType().getAmountChecker().accept(withdraw.getAmount());
        distributeBonusService.createWithdrawOrder(withdraw, ISecurity.userMust());
        return Result.ok();
    }
}
