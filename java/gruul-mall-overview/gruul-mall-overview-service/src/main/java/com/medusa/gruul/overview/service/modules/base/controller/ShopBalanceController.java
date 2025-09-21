package com.medusa.gruul.overview.service.modules.base.controller;

import com.medusa.gruul.common.model.resp.Result;
import com.medusa.gruul.common.security.resource.helper.ISecurity;
import com.medusa.gruul.overview.service.model.dto.ShopWithdrawDTO;
import com.medusa.gruul.overview.service.modules.base.service.ShopBalanceManagerService;
import com.medusa.gruul.overview.service.modules.withdraw.service.WithdrawService;
import com.medusa.gruul.overview.service.mp.base.entity.OverviewShopBalance;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;


/**
 * 店铺余额 控制器
 *
 * @author 张治保
 */
@RestController
@RequiredArgsConstructor
@PreAuthorize("""
        		@S.matcher().any(@S.ROLES,@S.R.SUPPLIER_ADMIN,@S.R.SUPPLIER_CUSTOM_ADMIN,@S.SHOP_ADMIN).
        		or(@S.consumer().eq(@S.ROLES,@S.SHOP_CUSTOM_ADMIN).eq(@S.PERMS,'finance:settle')).match()
        """)
@RequestMapping("/overview/shop/balance")
public class ShopBalanceController {

    private final WithdrawService withdrawService;
    private final ShopBalanceManagerService shopBalanceManagerService;


    /**
     * 查询店铺余额信息
     *
     * @return 店铺余额信息
     */
    @GetMapping
    public Result<OverviewShopBalance> get() {
        return Result.ok(
                shopBalanceManagerService.get(ISecurity.userMust().getShopId())
        );
    }

    /**
     * 店铺余额提现
     */
    @PostMapping("/withdraw")
    public Result<Void> newWithdraw(@Valid @RequestBody ShopWithdrawDTO withdraw) {
        withdrawService.newWithdraw(ISecurity.userMust().getShopId(), withdraw);
        return Result.ok();
    }


}
