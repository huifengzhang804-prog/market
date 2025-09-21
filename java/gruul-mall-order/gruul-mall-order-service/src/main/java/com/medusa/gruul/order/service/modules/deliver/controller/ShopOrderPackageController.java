package com.medusa.gruul.order.service.modules.deliver.controller;

import com.medusa.gruul.common.idem.annotation.Idem;
import com.medusa.gruul.common.log.annotation.Log;
import com.medusa.gruul.common.model.enums.SellType;
import com.medusa.gruul.common.model.resp.Result;
import com.medusa.gruul.common.model.resp.SystemCode;
import com.medusa.gruul.common.security.resource.helper.ISecurity;
import com.medusa.gruul.order.api.entity.ShopOrderItem;
import com.medusa.gruul.order.api.entity.ShopOrderPackage;
import com.medusa.gruul.order.api.enums.PackageStatus;
import com.medusa.gruul.order.api.model.OrderPackageKeyDTO;
import com.medusa.gruul.order.service.model.bo.DeliveryQueryBO;
import com.medusa.gruul.order.service.model.dto.OrderDeliveryDTO;
import com.medusa.gruul.order.service.model.dto.OrdersDeliveryDTO;
import com.medusa.gruul.order.service.model.vo.ShopOrderUndeliveredVO;
import com.medusa.gruul.order.service.modules.deliver.service.DeliverService;
import com.medusa.gruul.order.service.modules.deliver.service.ShopOrderDeliverCallShopRpcService;
import com.medusa.gruul.order.service.modules.deliver.service.ShopOrderDeliverService;
import com.medusa.gruul.shop.api.enums.ShopDeliverModeSettingsEnum;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Size;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 订单发货控制器 包裹
 *
 * @author 张治保
 * date 2022/7/26
 */
@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/order")
@PreAuthorize("@S.anyPerm('order','order:delivery')")
public class ShopOrderPackageController {

    private final DeliverService deliverService;
    private final ShopOrderDeliverService shopOrderDeliverService;
    private final ShopOrderDeliverCallShopRpcService shopOrderDeliverCallShopRpcService;

    /**
     * 查询所有未发货商品
     *
     * @param deliveryMatch 订单匹配条件
     * @return 查询所有未发货商品
     */
    @Log("查询所有未发货商品")
    @PreAuthorize("""
            		@S.matcher()
            		.any(@S.ROLES, @S.SHOP_ADMIN,@S.R.SUPPLIER_ADMIN)
            		.or(@S.consumer().eq(@S.ROLES, @S.SHOP_CUSTOM_ADMIN).eq(@S.PERMS, 'order:delivery'))
            		.match()
            """)
    @GetMapping("/{orderNo}/shopOrder/{shopOrderNo}/undelivered")
    public Result<ShopOrderUndeliveredVO> undelivered(DeliveryQueryBO deliveryMatch) {
        ISecurity.match()
                .ifAnyShopAdmin(secureUser -> deliveryMatch.setShopId(secureUser.getShopId()))
                .ifAnySupplierAdmin(secureUser -> deliveryMatch.setSupplierId(secureUser.getShopId()).setSellType(SellType.CONSIGNMENT));
        return Result.ok(
                shopOrderDeliverService.undelivered(deliveryMatch)
        );
    }

    /**
     * 查询所有已发货商品
     *
     * @param deliveryMatch 订单匹配条件
     */
    @Log("查询所有已发货商品")
    @GetMapping("/{orderNo}/shopOrder/{shopOrderNo}/delivered")
    public Result<List<ShopOrderItem>> delivered(DeliveryQueryBO deliveryMatch) {
        return Result.ok(
                shopOrderDeliverService.delivered(deliveryMatch)
        );
    }

    /**
     * 查询订单里所有已发货包裹
     *
     * @param deliveryMatch 订单匹配条件
     */
    @Log("查询订单里所有已发货包裹")
    @PreAuthorize("""
                    @S.matcher()
                    .any(@S.ROLES,@S.USER,@S.PLATFORM_ADMIN,@S.SHOP_ADMIN,@S.R.SUPPLIER_ADMIN)
                    .or(@S.consumer().eq(@S.ROLES,@S.PLATFORM_CUSTOM_ADMIN).eq(@S.PERMS,'order'))
                    .or(@S.consumer().eq(@S.ROLES,@S.SHOP_CUSTOM_ADMIN).eq(@S.PERMS,'order:delivery'))
                    .match()
            """)
    @GetMapping("/{orderNo}/shopOrder/{shopOrderNo}/delivered/package")
    public Result<List<ShopOrderPackage>> deliveredPackages(DeliveryQueryBO deliveryMatch) {
        ISecurity.match()
                .ifAnyShopAdmin(
                        secureUser -> deliveryMatch.setShopId(secureUser.getShopId())
                )
                .ifAnySupplierAdmin(secureUser -> {
                    if (deliveryMatch.getShopId() == null) {
                        throw SystemCode.PARAM_VALID_ERROR.exception();
                    }
                    deliveryMatch.setSupplierId(secureUser.getShopId())
                            .setSellType(SellType.CONSIGNMENT);
                });
        return Result.ok(
                shopOrderDeliverCallShopRpcService.deliveredPackages(deliveryMatch)
        );
    }

    /**
     * 根据店铺订单号查询第一个物流包裹
     *
     * @param orderNo     订单号
     * @param shopOrderNo 店铺订单号
     * @param packageId   包裹id
     */
    @Log("根据店铺订单号查询第一个物流包裹")
    @GetMapping("/{orderNo}/shopOrder/{shopOrderNo}/delivered/01")
    @PreAuthorize("""
                     @S.matcher()
                     .any(@S.ROLES,@S.USER,@S.PLATFORM_ADMIN,@S.SHOP_ADMIN,@S.R.SUPPLIER_ADMIN)
                     .or(@S.consumer().eq(@S.ROLES,@S.PLATFORM_CUSTOM_ADMIN).eq(@S.PERMS,'order'))
                     .or(@S.consumer().eq(@S.ROLES,@S.SHOP_CUSTOM_ADMIN).eq(@S.PERMS,'order:delivery'))
                     .match()
            """)
    public Result<ShopOrderPackage> deliveredPackageFirst(@PathVariable String orderNo, @PathVariable String shopOrderNo, @RequestParam(required = false) Long packageId) {
        return Result.ok(
                shopOrderDeliverService.deliveredPackageFirst(orderNo, shopOrderNo, packageId).getOrNull()
        );
    }

    /**
     * 商品发货
     *
     * @param orderNo       订单号
     * @param orderDelivery 订单发货参数
     */
    @Log("商品发货")
    @Idem
    @PreAuthorize("""
            @S.matcher()
            .any(@S.ROLES,@S.SHOP_ADMIN,@S.R.SUPPLIER_ADMIN,@S.PLATFORM_ADMIN,@S.R.SUPER_CUSTOM_ADMIN)
            .or(@S.consumer().eq(@S.ROLES,@S.SHOP_CUSTOM_ADMIN).eq(@S.PERMS,'order:delivery'))
            .match()
            """)
    @PutMapping("/{orderNo}/deliver")
    public Result<Void> deliver(@PathVariable String orderNo, @RequestBody @Valid OrderDeliveryDTO orderDelivery) {
        DeliveryQueryBO deliveryMatch = new DeliveryQueryBO()
                .setOrderNo(orderNo);
        ISecurity.match()
                .ifAnyShopAdmin(secureUser -> deliveryMatch.setShopId(secureUser.getShopId()))
                .ifAnySupplierAdmin(secureUser -> {
                    if (orderDelivery.getShopId() == null) {
                        throw SystemCode.PARAM_VALID_ERROR.exception();
                    }
                    deliveryMatch.setSupplierId(secureUser.getShopId())
                            .setSellType(SellType.CONSIGNMENT)
                            .setShopId(orderDelivery.getShopId());
                })
                .ifAnySuperAdmin(secureUser -> {
                    //平台发货
                    deliveryMatch.setShopDeliverModeSettings(ShopDeliverModeSettingsEnum.PLATFORM);
                    deliveryMatch.setShopId(ISecurity.secureUser().getShopId());
                    if (orderDelivery.getSelfShopType() == null) {
                        throw SystemCode.PARAM_VALID_ERROR.exception();
                    }
                });
        shopOrderDeliverService.deliver(deliveryMatch, orderDelivery);
        return Result.ok();
    }

    /**
     * 批量发货
     *
     * @param ordersDeliveries 订单发货参数
     */
    @Log("批量发货")
    @Idem
    @PreAuthorize("""
            @S.matcher()
            .any(@S.ROLES,@S.SHOP_ADMIN,@S.R.SUPPLIER_ADMIN,@S.PLATFORM_ADMIN,@S.R.SUPER_CUSTOM_ADMIN)
            .or(@S.consumer().eq(@S.ROLES,@S.SHOP_CUSTOM_ADMIN).eq(@S.PERMS,'order:delivery'))
            .match()
            """)
    @PutMapping("/batch/deliver")
    public Result<Void> batchDeliver(@RequestBody @Valid @Size(min = 1) List<OrdersDeliveryDTO> ordersDeliveries) {
        DeliveryQueryBO deliveryMatch = new DeliveryQueryBO();
        ISecurity.match()
                .ifAnyShopAdmin(secureUser -> deliveryMatch.setShopId(secureUser.getShopId()))
                .ifAnySupplierAdmin(secureUser -> {
                    if (ordersDeliveries.stream().anyMatch(delivery -> delivery.getShopId() == null)) {
                        throw SystemCode.PARAM_VALID_ERROR.exception();
                    }
                    deliveryMatch.setSellType(SellType.CONSIGNMENT)
                            .setSupplierId(secureUser.getShopId());
                })
                .ifAnySuperAdmin(secureUser -> deliveryMatch.setShopDeliverModeSettings(ShopDeliverModeSettingsEnum.PLATFORM));
        shopOrderDeliverService.batchDeliver(deliveryMatch, ordersDeliveries);
        return Result.ok();
    }


    /**
     * 包裹确认收货
     *
     * @param orderNo 订单号
     * @param shopId  店铺 id
     */
    @Log("包裹确认收货")
    @PreAuthorize("@S.matcher().role(@S.R.USER).match()")
    @PutMapping("/{orderNo}/shopOrder/{shopId}/confirm")
    public Result<Void> packageConfirm(@PathVariable String orderNo, @PathVariable Long shopId) {
        deliverService.packageConfirm(
                false,
                new OrderPackageKeyDTO()
                        .setBuyerId(ISecurity.userMust().getId())
                        .setOrderNo(orderNo)
                        .setShopId(shopId),
                PackageStatus.BUYER_WAITING_FOR_COMMENT
        );
        return Result.ok();
    }

}