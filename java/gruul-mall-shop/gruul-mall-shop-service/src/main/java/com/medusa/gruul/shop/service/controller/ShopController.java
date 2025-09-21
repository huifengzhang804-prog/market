package com.medusa.gruul.shop.service.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.medusa.gruul.common.idem.annotation.Idem;
import com.medusa.gruul.common.log.annotation.Log;
import com.medusa.gruul.common.model.resp.Result;
import com.medusa.gruul.common.model.resp.SystemCode;
import com.medusa.gruul.common.module.app.shop.ShopStatus;
import com.medusa.gruul.shop.api.model.vo.ShopAuditVo;
import com.medusa.gruul.shop.service.model.dto.ShopDTO;
import com.medusa.gruul.shop.service.model.dto.ShopQueryNoPageDTO;
import com.medusa.gruul.shop.service.model.dto.ShopQueryPageDTO;
import com.medusa.gruul.shop.service.model.vo.ShopListVO;
import com.medusa.gruul.shop.service.model.vo.ShopVO;
import com.medusa.gruul.shop.service.model.vo.SupplierStatisticsVO;
import com.medusa.gruul.shop.service.service.ShopManageService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

/**
 * 商家注册信息 前端控制器
 *
 * @author 张治保
 * @since 2022-04-14
 */
@Validated
@RestController
@RequiredArgsConstructor
@PreAuthorize("@S.matcher().anyRole(@S.R.SUPER_ADMIN,@S.R.SUPER_CUSTOM_ADMIN," +
        "@S.R.SUPPLIER_ADMIN,@S.R.SUPER_CUSTOM_ADMIN).match()")
@RequestMapping("/shop")
public class ShopController {

    private final ShopManageService shopManageService;


    /**
     * 分页查询店铺
     *
     * @param page 分页参数
     * @return 分页查询结果
     */
    @Log("分页查询商户列表")
    @GetMapping
    @Deprecated(since = "2.1.0 前端无调用后删除", forRemoval = true)
    public Result<IPage<ShopVO>> pageShop(ShopQueryPageDTO page) {
        return Result.ok(
                shopManageService.pageShop(page)
        );
    }


    /**
     * 分页查询店铺列表数据
     *
     * @param page 分页参数
     * @return 分页查询结果
     */
    @Log("分页查询商户列表list")
    @GetMapping("list")
    public Result<IPage<ShopListVO>> pageShopList(ShopQueryPageDTO page) {
        return Result.ok(shopManageService.pageShopList(page));
    }

    @Log("查询店铺的详情")
    @GetMapping("/{id}/detail")
    public Result<ShopVO> getShopInfo(@PathVariable("id") Long shopId) {
        ShopVO shopVO = shopManageService.getShopDetail(shopId);
        return Result.ok(shopVO);
    }

    /**
     * 新增店铺信息
     *
     * @param shop 店铺信息
     */
    @Idem
    @Log("新增商户")
    @PostMapping
    @PreAuthorize("""
              @S.matcher()
              .any(@S.ROLES,@S.PLATFORM_ADMIN,@S.PLATFORM_CUSTOM_ADMIN,@S.USER)
              .or(@S.consumer().eq(@S.ROLES, @S.PLATFORM_CUSTOM_ADMIN).eq(@S.PERMS,'shopList'))
              .match()
            """)
    public Result<Void> newShop(@RequestBody @Valid ShopDTO shop) {
        shopManageService.newShop(shop);
        return Result.ok();
    }

    /**
     * 编辑店铺信息
     *
     * @param shopId 店铺id
     * @param shop   店铺信息
     */
    @Idem
    @Log("编辑商户")
    @PutMapping("/{shopId}")
    public Result<Void> editShop(@PathVariable @Min(1) Long shopId, @RequestBody @Valid ShopDTO shop) {
        shopManageService.editShop(shopId, shop);
        return Result.ok();
    }

    /**
     * 店铺审核
     *
     * @param shopAuditVo 店铺审核信息
     * @return void
     */
    @Idem
    @Log("店铺审核")
    @PostMapping("/audit")
    public Result<Void> shopAudit(@RequestBody ShopAuditVo shopAuditVo) {
        if (!shopAuditVo.getPassFlag()) {
            //未通过
            if (Objects.isNull(shopAuditVo.getReasonForRejection()) ||
                    shopAuditVo.getReasonForRejection().length() > 20) {
                throw SystemCode.PARAM_VALID_ERROR.exception();
            }
        }
        shopManageService.shopAudit(shopAuditVo.getShopId(),
                shopAuditVo.getPassFlag(),
                shopAuditVo.getReasonForRejection());
        return Result.ok();
    }

    /**
     * 查询店铺审核未通过原因
     *
     * @param shopId 店铺ID
     * @return 店铺审核未通过原因
     */
    @GetMapping("{id}/rejectReason")
    public Result<String> rejectReason(@PathVariable("id") Long shopId) {
        String reason = shopManageService.queryRejectReason(shopId);
        return Result.ok(reason);
    }

    /**
     * 批量删除商家信息
     *
     * @param shopIds 商家id列表
     */
    @Idem
    @Log("商户批量删除")
    @DeleteMapping
    @Deprecated(since = "2.1.0 需求变动不允许删除店铺 预留一版", forRemoval = true)
    public Result<Void> batchDeleteShop(@RequestParam @Size(min = 1) Set<Long> shopIds) {
        shopManageService.deleteShop(shopIds);
        return Result.ok();
    }

    /**
     * 批量启用禁用禁用商家
     *
     * @param isEnable 是否启用
     * @param shopIds  商家id列表
     */
    @Idem(value = 500)
    @Log("商户批量启用禁用")
    @PatchMapping("/{isEnable}")
    public Result<Void> batchEnableDisableShops(@PathVariable Boolean isEnable,
                                                @RequestParam @Size(min = 1) Set<Long> shopIds) {
        shopManageService.enableDisableShop(isEnable, shopIds);
        return Result.ok();
    }

    /**
     * 获取今日新增店铺数量
     *
     * @return 今日店铺新增数量
     */
    @Log("获取今日新增店铺数量")
    @GetMapping("/today/shopQuantity")
    public Result<Long> getTodayAddShopQuantity() {
        return Result.ok(shopManageService.getTodayAddShopQuantity());
    }


    /**
     * 获取店铺数量
     *
     * @return 店铺数量
     */
    @Log("获取店铺数量")
    @GetMapping("/shopQuantity")
    public Result<Map<ShopStatus, Long>> getShopQuantity() {
        return Result.ok(shopManageService.getShopQuantity());
    }

    /**
     * 获取供应商数量
     *
     * @return 供应商数量
     */
    @Log("获取供应商数量")
    @GetMapping("/supplierQuantity")
    public Result<List<SupplierStatisticsVO>> getSupplierQuantity() {
        return Result.ok(shopManageService.getSupplierQuantity());
    }


    /**
     * 店铺重新审核
     *
     * @param shopId 店铺id
     * @return void
     */
    @Idem(1000)
    @Log("店铺重新审核")
    @PutMapping("/review/audit/{shopId}")
    public Result<Void> reviewAuditShop(@PathVariable @NotNull Long shopId) {
        shopManageService.reviewAuditShop(shopId);
        return Result.ok();
    }

    /**
     * 获取店铺状态数量
     *
     * @return 待审核商品数量
     */
    @Log("店铺状态数量")
    @PostMapping("/status/count")
    public Result<Long> getShopStatusCount(@RequestBody ShopQueryNoPageDTO page) {
        Long shopStatusCount = shopManageService.getShopStatusCount(page);
        return Result.ok(shopStatusCount);
    }

}

