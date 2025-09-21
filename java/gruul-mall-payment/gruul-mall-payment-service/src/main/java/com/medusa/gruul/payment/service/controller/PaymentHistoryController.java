package com.medusa.gruul.payment.service.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.medusa.gruul.common.idem.annotation.Idem;
import com.medusa.gruul.common.log.annotation.Log;
import com.medusa.gruul.common.model.resp.Result;
import com.medusa.gruul.common.security.resource.helper.ISecurity;
import com.medusa.gruul.global.model.o.Final;
import com.medusa.gruul.payment.api.model.dto.SavingsOrderRemarkDTO;
import com.medusa.gruul.payment.api.model.param.SavingsOrderHistoryParam;
import com.medusa.gruul.payment.api.model.param.UserPaymentHistoryParam;
import com.medusa.gruul.payment.api.model.vo.SavingsOrderHistoryVO;
import com.medusa.gruul.payment.api.model.vo.UserPaymentHistoryVO;
import com.medusa.gruul.payment.service.model.vo.BalanceBillingVO;
import com.medusa.gruul.payment.service.service.ManagePaymentHistoryService;
import com.medusa.gruul.payment.service.service.UserPaymentHistoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * 支付历史控制层
 *
 * @author xiaoq
 * @since 2022-08-31 09:57
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("user/payment/history")
@PreAuthorize("""
         @S.matcher()
         .anyRole(@S.PLATFORM_ADMIN,@S.USER)
         .or(@S.consumer().role(@S.PLATFORM_CUSTOM_ADMIN).anyPerm('payment','vip:base'))
         .match()
        """)
public class PaymentHistoryController {

    private final UserPaymentHistoryService userPaymentHistoryService;
    private final ManagePaymentHistoryService managePaymentHistoryService;

    /**
     * 获取用户支付历史记录
     *
     * @return Result.ok();
     */
    @GetMapping()
    @Log("获取用户支付历史记录")
    public Result<Page<UserPaymentHistoryVO>> getUserPaymentHistory(UserPaymentHistoryParam paymentHistoryParam) {
        ISecurity.match()
                .ifUser(secureUser -> paymentHistoryParam.setUserId(secureUser.getId()));
        return Result.ok(userPaymentHistoryService.queryUserPaymentHistory(paymentHistoryParam));
    }

    /**
     * 用户删除余额明细
     *
     * @param id 用户支付历史明细id
     */
    @DeleteMapping("/remove/{id}")
    public Result<Void> removeUserPaymentHistory(@PathVariable(name = "id") Long id) {
        userPaymentHistoryService.removeUserPaymentHistory(id);
        return Result.ok();
    }

    /**
     * 用户储蓄信息
     *
     * @param userId 用户id
     * @return Void·
     */
    @Log("获取用户储蓄信息")
    @GetMapping("savings/info")
    public Result<BalanceBillingVO> userSavingsInfo(Long userId) {
        Final<Long> box = new Final<>(userId);
        ISecurity.match()
                .ifUser(secureUser -> box.set(secureUser.getId()));
        return Result.ok(userPaymentHistoryService.getUserSavingsIno(box.get()));
    }

    /**
     * 储值订单信息
     *
     * @return Result<Void>
     */
    @Log("获取储值订单信息")
    @GetMapping("savings/order")
    @PreAuthorize("@S.platformPerm('payment')")
    public Result<IPage<SavingsOrderHistoryVO>> getSavingsOrderList(SavingsOrderHistoryParam savingsOrderHistoryParam) {
        return Result.ok(userPaymentHistoryService.getSavingsOrderList(savingsOrderHistoryParam));
    }

    /**
     * 获取储值订单批量备注
     *
     * @param savingsOrderRemark 储值订单备注dto
     * @return Result<Void>
     */
    @Idem(500)
    @Log("储值订单批量备注")
    @PutMapping("savings/order/remark")
    @PreAuthorize("@S.platformPerm('payment')")
    public Result<Void> savingsOrderBatchRemark(@RequestBody @Validated SavingsOrderRemarkDTO savingsOrderRemark) {
        managePaymentHistoryService.savingsOrderBatchRemark(savingsOrderRemark);
        return Result.ok();
    }
}
