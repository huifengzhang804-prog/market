package com.medusa.gruul.addon.coupon.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.medusa.gruul.addon.coupon.model.dto.*;
import com.medusa.gruul.addon.coupon.mp.entity.Coupon;
import com.medusa.gruul.addon.coupon.service.AdminCouponService;
import com.medusa.gruul.common.log.annotation.Log;
import com.medusa.gruul.common.model.resp.Result;
import com.medusa.gruul.common.security.resource.helper.ISecurity;
import com.medusa.gruul.global.model.o.Final;
import io.vavr.control.Option;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;

/**
 * 优惠券控制器
 *
 * @author 张治保 date 2022/11/2
 */
@Validated
@RestController
@RequestMapping("/coupon")
@PreAuthorize("@S.shopPerm('marketing:coupon')")
@RequiredArgsConstructor
public class AdminCouponController {

    private final AdminCouponService adminCouponService;

    /**
     * 新增优惠券
     *
     * @param coupon 信息详情
     * @return 执行结果
     */
    @Log("新增优惠券")
    @PostMapping
    public Result<Void> newCoupon(@RequestBody @Valid CouponDTO coupon) {
        adminCouponService.newCoupon(ISecurity.userMust().getShopId(), coupon);
        return Result.ok();
    }

    /**
     * 分页查询优惠券
     *
     * @param couponQuery 查询参数
     * @return 分页查询结果
     */
    @Log("分页查询优惠券")
    @GetMapping
    @PreAuthorize("@S.anyPerm('fatetrue:coupon','marketing:coupon')")
    public Result<IPage<Coupon>> couponPage(@Valid CouponQueryDTO couponQuery) {
        Long shopId = ISecurity.userMust().getShopId();
        // 0 代表平台 将0 转成null  非0 代表店铺
        Option<Long> option = Option.of(shopId == 0 ? null : shopId);
        IPage<Coupon> result = adminCouponService.couponPage(option, couponQuery);
        return Result.ok(result);
    }

    /**
     * 平台端装修查询指定店铺的优惠券
     *
     * @param shopId
     * @return
     */
    @GetMapping("decoration/{shopId}")
    @PreAuthorize("@S.platformPerm('decoration')")
    public Result<List<Coupon>> couponForDecoration(@PathVariable("shopId") Long shopId) {
        List<Coupon> coupons = adminCouponService.queryShopCouponForDecoration(shopId);

        return Result.ok(coupons);
    }

    /**
     * 查询优惠券详情
     *
     * @param shopId   店铺id
     * @param couponId 优惠券id
     * @return 优惠券详情
     */
    @Log("查询优惠券详情")
    @GetMapping("/shop/{shopId}/{couponId}")
    @PreAuthorize("@S.anyPerm('fatetrue:coupon','marketing:coupon')")
    public Result<Coupon> coupon(@PathVariable Long shopId, @PathVariable Long couponId) {
        Final<Long> box = new Final<>(shopId);
        ISecurity.match().ifAnyShopAdmin(secureUser -> box.set(secureUser.getShopId()));
        return Result.ok(adminCouponService.coupon(box.get(), couponId));
    }

    /**
     * 编辑优惠券
     *
     * @param couponId 优惠券id
     * @param coupon   优惠券详情
     * @return void
     */
    @Log("编辑优惠券")
    @PutMapping("/{couponId}")
    public Result<Void> editCoupon(@PathVariable Long couponId, @RequestBody @Valid CouponDTO coupon) {
        adminCouponService.editCoupon(ISecurity.userMust().getShopId(), couponId, coupon);
        return Result.ok();
    }


    /**
     * 编辑进行中的优惠券
     *
     * @param couponId   优惠券id
     * @param couponEdit 优惠券详情
     * @return void
     */
    @Log("编辑优惠券")
    @PutMapping("/{couponId}/working")
    public Result<Void> editValidCoupon(@PathVariable Long couponId,
                                        @RequestBody @Valid CouponWorkingEditDTO couponEdit) {
        adminCouponService.editValidCoupon(ISecurity.userMust().getShopId(), couponId, couponEdit);
        return Result.ok();
    }

    /**
     * 商家端批量删除优惠券
     *
     * @param couponIds 优惠券id集合
     * @return void
     */
    @Log("商家端批量删除优惠券")
    @DeleteMapping("/shop/batch")
    public Result<Void> deleteShopCouponBatch(@RequestBody @NotNull @Size(min = 1) Set<Long> couponIds) {
        adminCouponService.deleteShopCouponBatch(ISecurity.userMust().getShopId(), couponIds);
        return Result.ok();
    }

    /**
     * 平台端给指定用户发送优惠券
     *
     * @param giftsToUser 赠送优惠券参数
     * @return void
     */
    @Log("指定用户发送优惠券")
    @PreAuthorize("@S.anyPerm({'vip:base'},{'member:list','marketingApp:liveApp'})")
    @PostMapping("/gifts")
    public Result<Void> giftsToUser(@RequestBody @Valid GiftsToUserDTO giftsToUser) {
        Final<Boolean> isPlatformBox = new Final<>(Boolean.FALSE);
        Final<Long> shopIdBox = new Final<>();
        ISecurity.match().ifAnyShopAdmin(secureUser -> {
            isPlatformBox.set(Boolean.FALSE);
            shopIdBox.set(secureUser.getShopId());
        }).ifAnySuperAdmin(secureUser -> {
            isPlatformBox.set(Boolean.TRUE);
            shopIdBox.set(secureUser.getShopId());
        });
        giftsToUser.setPlatform(isPlatformBox.get());
        adminCouponService.giftsToUser(shopIdBox.get(), giftsToUser);
        return Result.ok();
    }


    /**
     * 平台端批量下架 优惠券
     * 平台端违规下架
     *
     * @param shopCoupons 店铺优惠券列表
     * @return void
     */
    @Log("平台端批量下架")
    @PutMapping("/ban/batch")
    @PreAuthorize("@S.platformPerm('fatetrue:coupon')")
    public Result<Void> banCouponBatch(@RequestBody @Valid @NotNull @Size(min = 1) List<ShopCouponMapDTO> shopCoupons) {
        adminCouponService.banCouponBatch(shopCoupons);
        return Result.ok();
    }

    /**
     * 店铺端下架 优惠券
     * 下架
     *
     * @param shopCouponMapDTO 优惠券信息
     * @return void
     */
    @Log("店铺端下架优惠券")
    @PutMapping("/shop/offShelf")
    @PreAuthorize("@S.shopPerm('fatetrue:coupon')")
    public Result<Boolean> shopCouponOffShelf(@RequestBody ShopCouponMapDTO shopCouponMapDTO) {
        //店铺端下架优惠券
        Boolean flag = adminCouponService.shopCouponOffShelf(shopCouponMapDTO);
        return Result.ok(flag);
    }

    /**
     * 查询优惠券活动违规下架的原因
     *
     * @return 优惠券下架的原因
     */
    @Log("查询违规原因")
    @GetMapping("/illegal/{couponId}/reason")
    @PreAuthorize("@S.anyPerm('fatetrue:coupon','marketing:coupon')")
    public Result<String> queryIllegalReason(@PathVariable("couponId") Long couponId) {
        String reason = adminCouponService.queryIllegalReason(couponId);
        return Result.ok(reason);
    }

}
