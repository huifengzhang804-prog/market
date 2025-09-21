package com.medusa.gruul.overview.service.modules.withdraw.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.medusa.gruul.common.log.annotation.Log;
import com.medusa.gruul.common.model.resp.Result;
import com.medusa.gruul.overview.service.model.dto.PurchasePayoutQueryDTO;
import com.medusa.gruul.overview.service.model.vo.PurchasePayoutVO;
import com.medusa.gruul.overview.service.mp.statement.service.IOverviewStatementService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


/**
 * @author Andy.Yan
 */
@RestController
@RequiredArgsConstructor
@PreAuthorize("""
        		@S.matcher().any(@S.ROLES,@S.R.SUPPLIER_ADMIN,@S.SHOP_ADMIN).
        		or(@S.consumer().eq(@S.ROLES,@S.SHOP_CUSTOM_ADMIN).eq(@S.PERMS,'finance:settle')).match()
        """)
@RequestMapping("/overview")
public class PurchaseOrderOverviewController {

    private final IOverviewStatementService overviewStatementService;

    /**
     * 分页查询采购支出
     *
     * @param query {@link PurchasePayoutQueryDTO}
     * @return {@link PurchasePayoutVO}
     */
    @Log("分页查询店铺采购支出")
    @GetMapping("/purchase-payout")
    public Result<IPage<PurchasePayoutVO>> queryPurchasePayoutList(@Valid PurchasePayoutQueryDTO query) {
        return Result.ok(
                overviewStatementService.queryPurchasePayoutList(query)
        );
    }
}
