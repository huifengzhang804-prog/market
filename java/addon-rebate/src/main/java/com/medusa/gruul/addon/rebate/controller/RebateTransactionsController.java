package com.medusa.gruul.addon.rebate.controller;

import com.medusa.gruul.addon.rebate.model.dto.WithdrawDTO;
import com.medusa.gruul.addon.rebate.model.vo.RebatePayDTO;
import com.medusa.gruul.addon.rebate.model.vo.RebateTransactionsVO;
import com.medusa.gruul.addon.rebate.service.RebateTransactionsHandleService;
import com.medusa.gruul.common.idem.annotation.Idem;
import com.medusa.gruul.common.log.annotation.Log;
import com.medusa.gruul.common.model.resp.Result;
import com.medusa.gruul.common.security.model.bean.SecureUser;
import com.medusa.gruul.common.security.resource.helper.ISecurity;
import com.medusa.gruul.goods.api.model.dto.EstimateRebateDTO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/**
 * <p>
 * 消费返利 前端控制器
 * </p>
 *
 * @author WuDi
 * @since 2023-07-17
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/rebateTransactions")
public class RebateTransactionsController {

    private final RebateTransactionsHandleService rebateTransactionsHandleService;

    /**
     * 消费返利是否已启用
     *
     * @return 是否启用
     */
    @GetMapping("/enabled")
    public Result<Boolean> enabled() {
        return Result.ok(
                rebateTransactionsHandleService.enabled()
        );
    }

    /**
     * 商品详情查询预计返利金额
     *
     * @param estimate 商品金额 与服务费比例
     * @return 商品返利金额
     */
    @Log("商品详情查询预计返利金额")
    @GetMapping("/product")
    @PreAuthorize("permitAll()")
    public Result<Long> getProductRebateAmount(@Valid EstimateRebateDTO estimate) {
        return Result.ok(
                rebateTransactionsHandleService.getProductRebateAmount(
                        ISecurity.userOpt().map(SecureUser::getId).getOrNull(),
                        estimate
                )
        );
    }

    /**
     * 获取可支付消费返利余额
     *
     * @param payAmount 订单支付金额
     * @return 可支付消费返利余额
     */
    @Log("获取可支付消费返利余额")
    @PreAuthorize("@S.matcher().role(@S.USER).match()")
    @GetMapping("/rebatePayBalance")
    public Result<RebatePayDTO> getCanRebateBalance(@RequestParam String orderNo, @RequestParam Long payAmount) {
        return Result.ok(
                rebateTransactionsHandleService.getCanRebateBalance(orderNo, payAmount)
        );
    }

    /**
     * 获取用户消费返利相关余额
     *
     * @return 用户消费返利余额
     */
    @Log("获取用户消费返利相关余额")
    @PreAuthorize("@S.matcher().role(@S.USER).match()")
    @GetMapping
    public Result<RebateTransactionsVO> getRebateTransactions() {
        return Result.ok(
                rebateTransactionsHandleService.getRebateTransactions()
        );
    }


    /**
     * 消费返利提现
     *
     * @param withdraw 提现方式
     */
    @Idem
    @Log("消费返利提现")
    @PostMapping("/withdraw")
    public Result<Void> createWithdrawOrder(@RequestBody @Valid WithdrawDTO withdraw) {
        withdraw.getType().getAmountChecker().accept(withdraw.getAmount());
        rebateTransactionsHandleService.createWithdrawOrder(ISecurity.userMust(), withdraw);
        return Result.ok();
    }


}
