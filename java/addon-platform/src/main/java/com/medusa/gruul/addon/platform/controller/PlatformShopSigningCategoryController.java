package com.medusa.gruul.addon.platform.controller;

import com.medusa.gruul.addon.platform.model.vo.CategoryVO;
import com.medusa.gruul.addon.platform.model.vo.SigningCategoryVO;
import com.medusa.gruul.addon.platform.service.SigningCategoryService;
import com.medusa.gruul.common.idem.annotation.Idem;
import com.medusa.gruul.common.log.annotation.Log;
import com.medusa.gruul.common.model.resp.Result;
import com.medusa.gruul.common.security.resource.helper.ISecurity;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;

/**
 * 平台店铺签约类目
 *
 * @author xiaoq
 * @Description PlatformShopSigningCategoryController.java
 * @date 2023-05-12 17:34
 */

@RestController
@RequiredArgsConstructor
@RequestMapping("/platform/shop/signing/category")
public class PlatformShopSigningCategoryController {

    private final SigningCategoryService signingCategoryService;


    /**
     * 店铺端删除签约类目
     *
     * @param signingCategoryIds 签约类目ids
     * @return Void
     */
    @Log("店铺端删除签约类目")
    @PostMapping("del")
    @PreAuthorize("""
            	@S.matcher()
                   .any(@S.ROLES, @S.SHOP_ADMIN,@S.R.SUPPLIER_ADMIN)
                   .or(@S.consumer().eq(@S.ROLES, @S.SHOP_CUSTOM_ADMIN).eq(@S.PERMS, 'mall:general:setting'))
                   .match()
            """)
    public Result<Void> delSigningCategory(@RequestBody @NotNull Set<Long> signingCategoryIds) {
        signingCategoryService.delSigningCategory(signingCategoryIds);
        return Result.ok();
    }


    /**
     * 店铺新增签约类目
     *
     * @param currentCategoryId 店铺二级类目ids
     * @return Void
     */
    @Idem(1000)
    @Log("店铺端新增签约类目")
    @PostMapping("add")
    @PreAuthorize("""
            	@S.matcher()
                   .any(@S.ROLES, @S.SHOP_ADMIN,@S.R.SUPPLIER_ADMIN)
                   .or(@S.consumer().eq(@S.ROLES, @S.SHOP_CUSTOM_ADMIN).eq(@S.PERMS, 'mall:general:setting'))
                   .match()
            """)
    public Result<Void> addSigningCategory(@RequestBody @NotNull Set<Long> currentCategoryId) {
        signingCategoryService.addSigningCategory(currentCategoryId);
        return Result.ok();
    }


    /**
     * 店铺端签约类目 List 签约类目VO
     *
     * @return List 签约类目VO
     */
    @Idem(1000)
    @Log("店铺端签约类目List")
    @GetMapping("list")
    @PreAuthorize("""
            	@S.matcher()
                   .any(@S.ROLES,@S.PLATFORM_ADMIN, @S.SHOP_ADMIN,@S.R.SUPPLIER_ADMIN)
                   .or(@S.consumer().eq(@S.ROLES, @S.SHOP_CUSTOM_ADMIN).eq(@S.PERMS, 'mall:general:setting'))
                   .or(@S.consumer().eq(@S.ROLES, @S.PLATFORM_CUSTOM_ADMIN).eq(@S.PERMS, 'shopList'))
                   .match()
            """)
    public Result<List<SigningCategoryVO>> signingCategoryList(Long shopId) {
        if (shopId == null) {
            shopId = ISecurity.userMust().getShopId();
        }
        return Result.ok(signingCategoryService.getSigningCategoryList(shopId, Boolean.FALSE));
    }

    @Idem(1000)
    @Log("店铺端签约类目List(当不存在自定义扣率时从平台类目中获取扣率)")
    @GetMapping("listWithParent")
    @PreAuthorize("""
            	@S.matcher()
                   .any(@S.ROLES,@S.PLATFORM_ADMIN, @S.SHOP_ADMIN,@S.R.SUPPLIER_ADMIN)
                   .or(@S.consumer().eq(@S.ROLES, @S.SHOP_CUSTOM_ADMIN).eq(@S.PERMS, 'mall:general:setting'))
                   .or(@S.consumer().eq(@S.ROLES, @S.PLATFORM_CUSTOM_ADMIN).eq(@S.PERMS, 'shopList'))
                   .match()
            """)
    public Result<List<SigningCategoryVO>> signingCategoryWithParentList(Long shopId) {
        if (shopId == null) {
            shopId = ISecurity.userMust().getShopId();
        }
        return Result.ok(signingCategoryService.getSigningCategoryList(shopId, Boolean.TRUE));
    }


    @Log("店铺端签约类目List--携带三级类目")
    @GetMapping("choosable/list")
    public Result<List<CategoryVO>> getChoosableCategoryInfo() {
        Long shopId = ISecurity.userMust().getShopId();
        return Result.ok(signingCategoryService.getChoosableCategoryInfo(shopId));
    }

}
