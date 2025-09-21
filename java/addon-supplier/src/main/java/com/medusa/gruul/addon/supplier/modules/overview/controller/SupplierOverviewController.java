package com.medusa.gruul.addon.supplier.modules.overview.controller;

import com.medusa.gruul.addon.supplier.model.dto.SupplierGoodsTradeStaticDTO;
import com.medusa.gruul.addon.supplier.model.dto.SupplierTradeStaticDTO;
import com.medusa.gruul.addon.supplier.model.enums.DateRangeType;
import com.medusa.gruul.addon.supplier.model.vo.SupplierGoodsTradeAmountTopVO;
import com.medusa.gruul.addon.supplier.model.vo.SupplierGoodsTradeStaticVO;
import com.medusa.gruul.addon.supplier.model.vo.SupplierGoodsTradeTopVO;
import com.medusa.gruul.addon.supplier.model.vo.ToDoListVO;
import com.medusa.gruul.addon.supplier.modules.overview.service.ISupplierOverviewService;
import com.medusa.gruul.common.log.annotation.Log;
import com.medusa.gruul.common.model.resp.Result;
import com.medusa.gruul.common.security.resource.helper.ISecurity;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 供应商经营概况Controller
 *
 * @author An.Yan
 */
@RestController
@RequestMapping("/supplier-overview")
@RequiredArgsConstructor
@PreAuthorize("""
        		@S.matcher().any(@S.ROLES,@S.PLATFORM_ADMIN,@S.R.SUPPLIER_ADMIN,@S.R.SUPPLIER_CUSTOM_ADMIN,@S.SHOP_ADMIN).
        		or(@S.consumer().eq(@S.ROLES,@S.SHOP_CUSTOM_ADMIN).eq(@S.PERMS,'overview')).
        		or(@S.consumer().eq(@S.ROLES,@S.PLATFORM_CUSTOM_ADMIN).eq(@S.PERMS,'overview'))
        		.match()
        """)
public class SupplierOverviewController {

    private final ISupplierOverviewService supplierOverviewService;


    /**
     * 获取当天咨询供应商的店铺数量
     *
     * @return {@link Integer}
     */
    @GetMapping("/inquiry-number")
    @Log("获取当天咨询供应商的店铺数量")
    public Result<Integer> getInquiryNumber() {
        return Result.ok(supplierOverviewService.getInquiryNumberByShopId(ISecurity.userMust().getShopId()));
    }

    /**
     * 获取供应商今日新增商品统计数据
     *
     * @param dateRangeType {@link DateRangeType}
     * @return {@link Integer}
     */
    @GetMapping("/new-created-product/{dateRangeType}")
    @Log("获取供应商今日新增商品统计数据")
    public Result<Integer> getNewCreatedProductCount(@Valid @PathVariable("dateRangeType") DateRangeType dateRangeType) {
        return Result.ok(supplierOverviewService.getNewCreatedProductCount(ISecurity.userMust().getShopId(), dateRangeType));
    }

    /**
     * 获取供应商今日待办统计数据
     *
     * @param dateRangeType 日期范围
     * @return {@link ToDoListVO}
     */
    @GetMapping("/to-do-list/{dateRangeType}")
    @Log("获取供应商今日待办统计数据")
    public Result<ToDoListVO> getToDoList(@Valid @PathVariable("dateRangeType") DateRangeType dateRangeType) {
        return Result.ok(supplierOverviewService.getToDoList(ISecurity.userMust().getShopId(), dateRangeType));
    }

    /**
     * 获取供应商商品交易统计数据
     *
     * @param dto {@link SupplierTradeStaticDTO}
     * @return {@link SupplierGoodsTradeStaticVO}
     */
    @GetMapping("/supplier-trade")
    @Log("获取供应商商品交易统计数据")
    public Result<List<SupplierGoodsTradeStaticVO>> getSupplierGoodsTradeStatic(@Valid SupplierTradeStaticDTO dto) {
        dto.setShopId(ISecurity.userMust().getShopId());
        return Result.ok(supplierOverviewService.getSupplierGoodsTradeStatic(dto));
    }


    /**
     * 获取供应商商品交易量TOP5
     *
     * @param dto {@link SupplierGoodsTradeStaticDTO}
     * @return {@link SupplierGoodsTradeTopVO}
     */
    @GetMapping("/supplier-goods-trade-num")
    @Log("获取供应商商品交易统计数据")
    public Result<List<SupplierGoodsTradeTopVO>> getSupplierGoodsTradeStatic(@Valid SupplierGoodsTradeStaticDTO dto) {
        dto.setSupplierId(ISecurity.userMust().getShopId());
        return Result.ok(supplierOverviewService.getSupplierGoodsTradeNumTopList(dto));
    }

    /**
     * 获取供应商商品交易金额TOP5
     *
     * @param dto {@link SupplierGoodsTradeStaticDTO}
     * @return {@link SupplierGoodsTradeAmountTopVO}
     */
    @GetMapping("/supplier-goods-trade-amount")
    @Log("获取供应商商品交易统计数据")
    public Result<List<SupplierGoodsTradeAmountTopVO>> getSupplierGoodsTradeAmountStatic(@Valid SupplierGoodsTradeStaticDTO dto) {
        dto.setSupplierId(ISecurity.userMust().getShopId());
        return Result.ok(supplierOverviewService.getSupplierGoodsTradeAmountTopList(dto));
    }

    /**
     * 获取所有供应商的采购单数量-和采购列表保持一致
     *
     * @return {@link Long}
     */
    @GetMapping("/supplier-purchase-order-number")
    @Log("获取供应商商品交易统计数据")
    public Result<Long> getSupplierNumber() {
        return Result.ok(supplierOverviewService.getPurchaseOrderCount());
    }
}
