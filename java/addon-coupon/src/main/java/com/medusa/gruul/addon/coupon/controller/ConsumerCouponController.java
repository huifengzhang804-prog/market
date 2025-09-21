package com.medusa.gruul.addon.coupon.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.medusa.gruul.addon.coupon.model.dto.*;
import com.medusa.gruul.addon.coupon.model.vo.CouponUserVO;
import com.medusa.gruul.addon.coupon.model.vo.CouponVO;
import com.medusa.gruul.addon.coupon.service.ConsumerCouponService;
import com.medusa.gruul.common.log.annotation.Log;
import com.medusa.gruul.common.model.resp.Result;
import com.medusa.gruul.common.security.model.bean.SecureUser;
import com.medusa.gruul.common.security.resource.helper.ISecurity;
import com.medusa.gruul.common.system.model.ISystem;
import com.medusa.gruul.common.system.model.model.ClientType;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;


/**
 * 消费者优惠券控制器
 *
 * @author 张治保
 * date 2022/11/3
 */
@RestController
@Valid
@RequestMapping("/coupon/consumer")
@PreAuthorize("@S.matcher().role(@S.USER).match()")
@RequiredArgsConstructor
public class ConsumerCouponController {

    private final ConsumerCouponService consumerCouponService;

    /**
     * 领券中心 优惠券分页查询
     *
     * @param query 查询条件
     * @return 分页查询结果
     */
    @GetMapping
    @PreAuthorize("permitAll()")
    @Log("领券中心/我的优惠券/ 可以领取的店铺优惠券查询 分页查询")
    public Result<IPage<CouponVO>> consumerCouponPage(@Valid ConsumerCouponQueryDTO query) {
        return Result.ok(
                consumerCouponService.consumerCouponPage(ISecurity.userOpt().map(SecureUser::getId), query)
        );
    }

    /**
     * 结算页分页查询优惠券
     *
     * @param orderCouponPage 订单优惠券分页参数
     * @return 分页查询结果
     */
    @PostMapping("/order")
    @Log("结算页选择优惠券")
    public Result<IPage<CouponVO>> orderShopCouponPage(@RequestBody @Valid OrderCouponPageDTO orderCouponPage) {
        return Result.ok(
                consumerCouponService.orderShopCouponPage(ISecurity.userMust().getId(), orderCouponPage)
        );
    }

    /**
     * 分页查询指定商品详情优惠券
     *
     * @param productCouponPage 分页查询参数
     * @return 分页查询结果
     */
    @PostMapping("/product")
    @Log("商品详情优惠券优惠")
    @PreAuthorize("permitAll()")
    public Result<IPage<CouponVO>> productShopCouponPage(@Valid @RequestBody ProductCouponPageDTO productCouponPage) {
        return Result.ok(
                consumerCouponService.productShopCouponPage(productCouponPage)
        );
    }

    /**
     * 领取优惠券
     *
     * @param shopId   店铺id
     * @param couponId 优惠券id
     * @return 领取结果 true 可以继续再领  false  不可以继续再领取
     */
    @Log("领取优惠券")
    @PostMapping("/collect/shop/{shopId}/{couponId}")
    public Result<Boolean> collectCoupon(@PathVariable Long shopId, @PathVariable Long couponId) {
        Boolean flag = consumerCouponService.collectCoupon(ISecurity.userMust().getId(), shopId, couponId);
        return Result.ok(flag);
    }


    /**
     * 用券记录
     *
     * @param query 查询条件
     * @return 分页查询结果
     */
    @Log("用券记录")
    // @PreAuthorize("permitAll()")
//    @PreAuthorize("""
//                    @S.matcher()
//                    .any(@S.ROLES,@S.PLATFORM_ADMIN,@S.R.SUPER_CUSTOM_ADMIN,@S.SHOP_ADMIN, @S.SHOP_CUSTOM_ADMIN)
//                    .match()
//            """)
    @PreAuthorize("""
                    @S.matcher()
                    .anyRole(@S.PLATFORM_ADMIN,@S.SHOP_ADMIN)
                    .or(
                        @S.consumer().role(@S.R.SUPER_CUSTOM_ADMIN).perm("vip")
                    )
                    .or(
                        @S.consumer().role(@S.SHOP_CUSTOM_ADMIN).perm("coupon")
                    )
                    .match()
            """)
    @GetMapping("/use/record")
    public Result<IPage<CouponUserVO>> useRecord(@Valid CouponUserDTO query) {
        ClientType clientType = ISystem.clientTypeMust();
        query.setShopId(ClientType.PLATFORM_CONSOLE.equals(clientType)?null:ISecurity.userMust().getShopId());

        return Result.ok(
                consumerCouponService.useRecord(query)
        );
    }


    /**
     * 用券记录列表导出
     *
     * @param query 用券记录查询参数
     * @return 导出id
     */
    @Log("用券记录列表导出")
    @PostMapping("/export")
    @PreAuthorize("""
                    @S.matcher()
                    .anyRole(@S.PLATFORM_ADMIN,@S.SHOP_ADMIN)
                    .or(
                        @S.consumer().role(@S.R.SUPER_CUSTOM_ADMIN).perm("vip")
                    )
                    .or(
                        @S.consumer().role(@S.SHOP_CUSTOM_ADMIN).perm("coupon")
                    )
                    .match()
            """)
    public Result<Long> export(@RequestBody @Valid CouponUserDTO query) {
        Long dataExportId = consumerCouponService.export(query);
        return Result.ok(dataExportId);

    }


}
