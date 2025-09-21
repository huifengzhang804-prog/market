package com.medusa.gruul.overview.service.modules.withdraw.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.medusa.gruul.common.log.annotation.Log;
import com.medusa.gruul.common.model.resp.Result;
import com.medusa.gruul.common.security.resource.helper.ISecurity;
import com.medusa.gruul.overview.api.entity.OverviewWithdraw;
import com.medusa.gruul.overview.api.enums.OwnerType;
import com.medusa.gruul.overview.service.model.dto.*;
import com.medusa.gruul.overview.service.modules.withdraw.service.WithdrawService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;


/**
 * 提现工单 前端控制器
 *
 * @author 张治保
 * @since 2022-11-19
 */
@RestController
@RequiredArgsConstructor
@PreAuthorize("@S.platformPerm('withdrawOrder')")
@RequestMapping("/overview/withdraw")
public class OverviewWithdrawController {

    private final WithdrawService withdrawService;

    /**
     * 分页查询提现工单
     *
     * @param query 查询条件
     * @return 查询结果
     */
    @GetMapping
    @Log("分页查询提现工单")
    public Result<IPage<OverviewWithdraw>> orderPage(@Valid PlatformWithdrawQueryDTO query) {
        return Result.ok(
                withdrawService.orderPage(query)
        );
    }

    /**
     * 导出提现工单
     *
     * @param query 提现工单查询条件
     * @return 导出结果
     */
    @PostMapping("export")
    @Log("导出提现工单")
    public Result<Long> exportWithDrawOrder(@RequestBody PlatformWithdrawQueryDTO query) {

        return Result.ok(withdrawService.exportWithDrawOrder(query));
    }

    /**
     * 批量备注提现工单号
     *
     * @param withdrawRemark 备注信息
     */
    @PutMapping("/batch/remark")
    @Log("批量备注")
    public Result<Void> remarkBatch(@RequestBody @Valid WithdrawRemarkDTO withdrawRemark) {
        withdrawService.remarkBatch(withdrawRemark);
        return Result.ok();
    }

    @Log("批准（结算）/拒绝申请")
    @PutMapping("/audit/{orderNo}")
    public Result<Void> orderAuditByNo(@PathVariable String orderNo,
                                       @RequestBody @Valid WithdrawAuditDTO audit) {
        withdrawService.orderAuditByNo(orderNo, audit);
        return Result.ok();
    }

    /**
     * 分销商分页查询提现工单
     *
     * @param query 分页查询条件
     * @return 分页查询结果
     */
    @Log("分销商分页查询提现工单")
    @PreAuthorize("@S.matcher().role(@S.R.USER).match()")
    @GetMapping("/distribute/mine")
    public Result<IPage<OverviewWithdraw>> distributeWithdrawPage(WithdrawQuery query) {
        query.setOwnerType(query.getOwnerType());
        return Result.ok(
                withdrawService.distributeWithdrawPage(ISecurity.userMust().getId(), query)
        );
    }

    /**
     * 店铺分页查询提现工单
     *
     * @param query 查询条件
     * @return 分页查询结果
     */
    @Log("店铺分页查询提现工单")
    @PreAuthorize("""
            		@S.matcher().any(@S.ROLES,@S.R.SUPPLIER_ADMIN,@S.R.SUPPLIER_CUSTOM_ADMIN,@S.SHOP_ADMIN).
            		or(@S.consumer().eq(@S.ROLES,@S.SHOP_CUSTOM_ADMIN).any(@S.PERMS,'finance:settle'))
            		.match()
            """)
    @GetMapping("/shop")
    public Result<IPage<OverviewWithdraw>> shopWithdrawPage(@Valid ShopWithdrawQueryDTO query) {
        return Result.ok(
                withdrawService.shopWithdrawPage(ISecurity.userMust().getShopId(), query)
        );
    }

    @Log("供应商结算导出")
    @PreAuthorize("""
            		@S.matcher().any(@S.ROLES,@S.R.SUPPLIER_ADMIN,@S.SHOP_ADMIN).
            		or(@S.consumer().eq(@S.ROLES,@S.SHOP_CUSTOM_ADMIN).any(@S.PERMS,'finance:settle'))
            		.match()
            """)
    @PostMapping("/supplier/export")
    public Result<Long> supplierExport(@RequestBody ShopWithdrawQueryDTO query) {

        query.setType(OwnerType.SUPPLIER);
        Long recorderId = withdrawService.export(query);
        return Result.ok(recorderId);
    }

    @Log("店铺结算导出")
    @PreAuthorize("""
            		@S.matcher().any(@S.ROLES,@S.R.SUPPLIER_ADMIN,@S.SHOP_ADMIN).
            		or(@S.consumer().eq(@S.ROLES,@S.SHOP_CUSTOM_ADMIN).any(@S.PERMS,'finance:settle'))
            		.match()
            """)
    @PostMapping("/shop/export")
    public Result<Long> shopExport(@RequestBody ShopWithdrawQueryDTO query) {

        query.setType(OwnerType.SHOP);
        Long recorderId = withdrawService.export(query);
        return Result.ok(recorderId);
    }


}
