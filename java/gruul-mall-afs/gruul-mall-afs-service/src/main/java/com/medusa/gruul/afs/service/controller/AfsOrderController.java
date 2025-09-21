package com.medusa.gruul.afs.service.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.medusa.gruul.afs.api.enums.RefundType;
import com.medusa.gruul.afs.api.model.AfsCloseDTO;
import com.medusa.gruul.afs.service.model.dto.*;
import com.medusa.gruul.afs.service.mp.entity.AfsOrder;
import com.medusa.gruul.afs.service.mp.entity.AfsOrderItem;
import com.medusa.gruul.afs.service.service.AfsOrderApplyService;
import com.medusa.gruul.afs.service.service.AfsQueryService;
import com.medusa.gruul.afs.service.service.AfsService;
import com.medusa.gruul.common.idem.annotation.Idem;
import com.medusa.gruul.common.log.annotation.Log;
import com.medusa.gruul.common.model.resp.Result;
import com.medusa.gruul.common.security.resource.helper.ISecurity;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Objects;

/**
 * 售后工单表 前端控制器
 *
 * @author 张治保
 * @since 2022-08-03
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/afs/order")
@PreAuthorize("""
            @S.matcher()
            .any(@S.ROLES,@S.PLATFORM_ADMIN,@S.SHOP_ADMIN,@S.USER,@S.R.SUPPLIER_ADMIN,@S.R.SUPPLIER_CUSTOM_ADMIN)
            .or(@S.consumer().eq(@S.ROLES,@S.PLATFORM_CUSTOM_ADMIN).eq(@S.PERMS,'order:afs'))
            .or(@S.consumer().eq(@S.ROLES,@S.SHOP_CUSTOM_ADMIN).eq(@S.PERMS,'order:sale'))
            .match()
        """)
public class AfsOrderController {
    private final AfsService afsService;
    private final AfsQueryService afsQueryService;
    private final AfsOrderApplyService afsOrderApplyService;


    /**
     * 分页查询售后工单
     *
     * @param afsPage 列表查询参数
     * @return IPage<AfsOrder>
     */
    @GetMapping
    @Log("分页查询售后工单")
    public Result<IPage<AfsOrder>> afsOrderPage(AfsPageDTO afsPage) {
        ISecurity.match()
                .ifAnySupplierAdmin(secureUser -> afsPage.setSupplierId(secureUser.getShopId()))
                .ifAnyShopAdmin(secureUser -> afsPage.setShopId(secureUser.getShopId()))
                .ifUser(secureUser -> afsPage.setBuyerId(secureUser.getId()));
        return Result.ok(
                afsQueryService.afsOrderPage(afsPage)
        );
    }

    /**
     * 统计不同售后状态的AFS工单数量
     *
     * @param afsQuery
     * @return
     */
    @PostMapping("query/status/count")
    @Log("统计不同售后状态的售后工单数量")
    public Result<Integer> staticsStatusCount(@RequestBody AfsQueryDTO afsQuery) {
        ISecurity.match()
                .ifAnySupplierAdmin(secureUser -> afsQuery.setSupplierId(secureUser.getShopId()))
                .ifAnyShopAdmin(secureUser -> afsQuery.setShopId(secureUser.getShopId()))
                .ifUser(secureUser -> afsQuery.setBuyerId(secureUser.getId()));

        return Result.ok(afsQueryService.staticsStatusCount(afsQuery));
    }

    /**
     * 售后工单导出
     *
     * @param afsPage
     * @return 导出记录id
     */
    @PostMapping("export")
    @Log("导出售后工单")
    public Result<Long> afsOrderExport(@RequestBody @Valid AfsPageDTO afsPage) {
        if (Objects.nonNull(afsPage.getOrderNo())) {
            afsPage.setAfsNo(afsPage.getOrderNo());
        }
        Long exportRecordeId = afsService.export(afsPage);
        return Result.ok(exportRecordeId);
    }

    /**
     * 根据售后工单号查询售后详情
     *
     * @param afsNo 售后订单号
     * @return AfsOrder
     */
    @GetMapping("/{afsNo}")
    @Log("根据售后工单号查询售后详情")
    @PreAuthorize("@S.matcher().role(@S.R.USER).match()")
    public Result<AfsOrder> getCurrentAfsOrderDetail(@PathVariable String afsNo) {
        return Result.ok(
                afsQueryService.getCurrentAfsOrderDetail(afsNo).getOrNull()
        );
    }

    /**
     * 根据售后工单号查询售后历史
     *
     * @param afsNo 售后订单号
     * @return List<AfsOrder>
     */
    @GetMapping("/{afsNo}/history")
    @Log("根据售后工单号查询售后历史")
    public Result<List<AfsOrder>> getAfsHistory(@PathVariable String afsNo) {
        return Result.ok(
                afsQueryService.getAfsHistory(afsNo)
        );
    }


    /**
     * 买家申请售后
     *
     * @param createAfs 创建信息
     * @return Void
     */
    @PostMapping
    @Log("买家申请售后")
    @PreAuthorize("@S.matcher().role(@S.R.USER).match()")
    public Result<Void> afsApply(@RequestBody @Valid AfsApplyDTO createAfs) {
        afsOrderApplyService.apply(createAfs);
        return Result.ok();
    }

    /**
     * 商家同意售后申请
     *
     * @param afsNo    售后工单号
     * @param afsAudit 审核结果
     * @return Void
     */
    @PutMapping("/{afsNo}/request/audit")
    @Log("商家同意/拒绝售后申请")
    @PreAuthorize("""
            @S.matcher()
            .any(@S.ROLES,@S.SHOP_ADMIN,@S.R.SUPPLIER_ADMIN)
            .or(@S.consumer().eq(@S.ROLES,@S.SHOP_CUSTOM_ADMIN).eq(@S.PERMS,'order:sale'))
            .match()
            """)
    public Result<Void> afsRequestAgreeReject(@PathVariable String afsNo, @RequestBody @Valid AfsAuditDTO afsAudit) {
        afsService.afsRequestAgreeReject(afsNo, afsAudit, Boolean.FALSE);
        return Result.ok();
    }

    /**
     * 买家已退货 退货退款
     *
     * @param afsNo         售后工单号
     * @param type          退货类型
     * @param buyerReturned 用户退货信息
     * @return Void
     */
    @PutMapping("/{afsNo}/{type}/returned")
    @Log("退货退款，买家 发货")
    @PreAuthorize("@S.matcher().role(@S.R.USER).match()")
    public Result<Void> afsBuyerReturned(@PathVariable String afsNo, @PathVariable RefundType type, @RequestBody @Valid BuyerReturnedDTO buyerReturned) {
        afsService.afsBuyerReturned(afsNo, type, buyerReturned);
        return Result.ok();
    }

    /**
     * 商家确认/拒绝收货
     *
     * @param afsNo    售后工单号
     * @param afsAudit 审核信息
     * @return Void
     */
    @PutMapping("/{afsNo}/confirm/audit")
    @Log("退货退款，商家确认/拒绝收货")
    @PreAuthorize("""
            @S.matcher()
            .any(@S.ROLES,@S.SHOP_ADMIN,@S.R.SUPPLIER_ADMIN)
            .or(@S.consumer().eq(@S.ROLES,@S.SHOP_CUSTOM_ADMIN).eq(@S.PERMS,'order:sale'))
            .match()
            """)
    public Result<Void> afsReturnedConfirmReject(@PathVariable String afsNo, @RequestBody @Valid AfsAuditDTO afsAudit) {
        afsService.afsReturnedConfirmReject(afsNo, afsAudit, Boolean.FALSE);
        return Result.ok();
    }

    /**
     * 买家关闭售后
     *
     * @param afsNo 售后订单号
     * @return Void
     */
    @PutMapping("/{afsNo}/close")
    @Log("买家关闭售后")
    @PreAuthorize("@S.matcher().role(@S.R.USER).match()")
    public Result<Void> afsClose(@PathVariable String afsNo) {
        afsService.afsClose(
                new AfsCloseDTO()
                        .setAfsNo(afsNo)
                        .setIsSystem(Boolean.FALSE)
                        .setReason("买家主动关闭")
                        .setUpdateShopOrderItem(Boolean.TRUE)
        );
        return Result.ok();
    }


    /**
     * 根据售后工单号查询售后商品项
     *
     * @param afsNo 售后工单号
     * @return 商品项列表
     */
    @PutMapping("/{afsNo}/item")
    @Log("查询售后商品项")
    @PreAuthorize("@S.matcher().role(@S.R.USER).match()")
    public Result<List<AfsOrderItem>> afsOrderItems(@PathVariable String afsNo) {
        return Result.ok(
                afsQueryService.afsOrderItems(afsNo)
        );

    }

    /**
     * 售后工单批量备注
     *
     * @param afsRemark 售后工单备注参数
     */
    @Idem(500)
    @Log("售后工单批量备注")
    @PutMapping("/remark/batch")
    @PreAuthorize("""
            @S.matcher()
            .any(@S.ROLES,@S.PLATFORM_ADMIN,@S.SHOP_ADMIN,@S.R.SUPPLIER_ADMIN)
            .or(@S.consumer().eq(@S.ROLES,@S.PLATFORM_CUSTOM_ADMIN).eq(@S.PERMS,'order:afs'))
            .or(@S.consumer().eq(@S.ROLES,@S.SHOP_CUSTOM_ADMIN).any(@S.PERMS,'order:afs','order:sale'))
            .match()
            """)
    public Result<Void> afsOrderBatchRemark(@RequestBody @Valid AfsRemarkDTO afsRemark) {
        afsService.afsOrderBatchRemark(afsRemark);
        return Result.ok();
    }

}
