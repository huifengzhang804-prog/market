package com.medusa.gruul.overview.service.modules.operate.controller;

import com.medusa.gruul.common.log.annotation.Log;
import com.medusa.gruul.common.model.resp.Result;
import com.medusa.gruul.overview.service.model.param.DealRankingParam;
import com.medusa.gruul.overview.service.model.vo.DealStatisticsVO;
import com.medusa.gruul.overview.service.model.vo.SalableProductTypeVO;
import com.medusa.gruul.overview.service.model.vo.SalableShopVO;
import com.medusa.gruul.overview.service.modules.operate.service.ManageDealRankingService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 交易排行信息控制层
 *
 * @author xiaoq
 * @Description DealRankingController.java
 * @date 2022-10-20 14:16
 */
@RestController
@RequestMapping("/overview/deal")
@RequiredArgsConstructor
public class DealRankingController {

    private final ManageDealRankingService manageDealRankingService;


    /**
     * 获取畅销店铺TOP10
     *
     * @param dealRankingParam 交易排行查询param
     * @return List<SalableShopVO>
     */
    @GetMapping("shop")
    @Log("获取畅销店铺TOP10")
    @PreAuthorize("@S.platformPerm('overview')")
    public Result<List<SalableShopVO>> getSalableShop(DealRankingParam dealRankingParam) {
        return Result.ok(manageDealRankingService.getSalableShop(dealRankingParam));
    }

    /**
     * 获取交易排行信息
     *
     * @param dealRankingParam 交易排行查询param
     * @return List<DealStatisticsVO>
     */
    @Log("获取交易统计信息")
    @GetMapping("statistics")
    @PreAuthorize("""
            		@S.matcher().any(@S.ROLES,@S.R.SUPPLIER_ADMIN,@S.SHOP_ADMIN,@S.PLATFORM_ADMIN).
            		or(@S.consumer().eq(@S.ROLES,@S.SHOP_CUSTOM_ADMIN).eq(@S.PERMS,'overview')).match()
            """)
    public Result<List<DealStatisticsVO>> getDealStatistics(DealRankingParam dealRankingParam) {
        return Result.ok(manageDealRankingService.getDealStatistics(dealRankingParam));
    }

    /**
     * 获取畅销商品销量排行信息
     *
     * @param dealRankingParam 交易排行查询param
     * @return List<SalableProductTypeVO>
     */
    @Log("获取畅销产品销量")
    @GetMapping("product/sales")
    @PreAuthorize("""
            		@S.matcher().any(@S.ROLES,@S.R.SUPPLIER_ADMIN,@S.SHOP_ADMIN,@S.PLATFORM_ADMIN).
            		or(@S.consumer().eq(@S.ROLES,@S.SHOP_CUSTOM_ADMIN).eq(@S.PERMS,'overview')).match()
            """)
    public Result<List<SalableProductTypeVO>> getSalableProductNum(DealRankingParam dealRankingParam) {
        return Result.ok(manageDealRankingService.getSalableProductNum(dealRankingParam));
    }

    /**
     * 获取畅销商品销售额排行信息
     *
     * @param dealRankingParam 交易排行查询param
     * @return List<SalableProductTypeVO>
     */
    @Log("获取畅销产品金额")
    @GetMapping("product/money")
    @PreAuthorize("""
            		@S.matcher().any(@S.ROLES,@S.R.SUPPLIER_ADMIN,@S.SHOP_ADMIN,@S.PLATFORM_ADMIN).
            		or(@S.consumer().eq(@S.ROLES,@S.SHOP_CUSTOM_ADMIN).eq(@S.PERMS,'overview')).match()
            """)
    public Result<List<SalableProductTypeVO>> getSalableProductMoney(DealRankingParam dealRankingParam) {
        return Result.ok(manageDealRankingService.getSalableProductMoney(dealRankingParam));
    }
}
