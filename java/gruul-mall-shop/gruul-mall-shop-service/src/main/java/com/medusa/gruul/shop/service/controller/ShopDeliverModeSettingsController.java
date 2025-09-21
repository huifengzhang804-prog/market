package com.medusa.gruul.shop.service.controller;

import com.medusa.gruul.common.idem.annotation.Idem;
import com.medusa.gruul.common.log.annotation.Log;
import com.medusa.gruul.common.model.resp.Result;
import com.medusa.gruul.shop.api.model.vo.ShopDeliverModeSettings;
import com.medusa.gruul.shop.service.service.ShopDeliverModeSettingsManageService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/**
 * @author: wufuzhong
 * @Date: 2023/11/07 09:29:05
 * @Description: 自营店铺发货方式配置控制层
 */
@RestController
@RequestMapping("/shop/deliver")
@RequiredArgsConstructor
public class ShopDeliverModeSettingsController {

    private final ShopDeliverModeSettingsManageService shopDeliverModeSettingsManageService;

    /**
     * 设置自营店铺发货方式
     *
     * @param param 自营店铺发货设置入参
     */
    @Log("设置自营店铺发货方式")
    @Idem(500)
    @PreAuthorize("""
                    @S.matcher()
                    .any(@S.ROLES,@S.R.SUPER_ADMIN,@S.R.SUPER_CUSTOM_ADMIN).match()
            """)
    @PutMapping
    public Result<Void> deliverSettings(@RequestBody ShopDeliverModeSettings param) {
        shopDeliverModeSettingsManageService.saveOrUpdate(param);
        return Result.ok();
    }

    /**
     * 获取自营店铺发货方式
     */
    @Log("获取自营店铺发货方式")
    @GetMapping
    @PreAuthorize("permitAll()")
    public Result<ShopDeliverModeSettings> getDeliverSettings() {
        return Result.ok(shopDeliverModeSettingsManageService.getShopDeliverModeSettings());
    }

}
