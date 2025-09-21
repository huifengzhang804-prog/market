package com.medusa.gruul.addon.supplier.modules.order.controller;

import com.medusa.gruul.addon.supplier.model.bo.OrderTimeout;
import com.medusa.gruul.addon.supplier.model.bo.PaymentMethod;
import com.medusa.gruul.addon.supplier.model.enums.PurchaseOrderPaymentMethodEnum;
import com.medusa.gruul.addon.supplier.modules.order.service.SupplierOrderConfigService;
import com.medusa.gruul.common.log.annotation.Log;
import com.medusa.gruul.common.model.resp.Result;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;


/**
 * @author 张治保
 * date 2023/7/24
 */

@RestController
@RequiredArgsConstructor
@RequestMapping("/supplier/order/config")
public class SupplierOrderConfigController {


    private final SupplierOrderConfigService supplierOrderConfigService;

    /**
     * 查询订单超时时间配置
     *
     * @return 超时时间配置
     */
    @Log("查询订单超时时间配置")
    @GetMapping("/timeout")
    public Result<OrderTimeout> timeout() {
        return Result.ok(supplierOrderConfigService.orderTimeout());
    }

    /**
     * 更新订单超时时间配置
     *
     * @param timeout 超时时间配置
     * @return void
     */
    @Log("更新订单超时时间配置")
    @PreAuthorize("""
            @S.matcher().any(@S.ROLES,@S.R.SUPPLIER_ADMIN,@S.PLATFORM_ADMIN)
            .or(@S.consumer().eq(@S.ROLES,@S.PLATFORM_CUSTOM_ADMIN).eq(@S.PERMS,'generalSet'))
            .match()
                       """)
    @PutMapping("/timeout")
    public Result<Void> updateOrderConfig(@RequestBody @Valid OrderTimeout timeout) {
        supplierOrderConfigService.updateOrderTimeout(timeout);
        return Result.ok();
    }

    @Log("设置采购订单的支付方式")
    @PreAuthorize("""
            @S.matcher().any(@S.ROLES,@S.R.SUPPLIER_ADMIN,@S.PLATFORM_ADMIN)
            .or(@S.consumer().eq(@S.ROLES,@S.PLATFORM_CUSTOM_ADMIN).eq(@S.PERMS,'generalSet'))
            .match()
                       """)
    @PutMapping("/payment/method")
    public Result<Void> setPurchaseOrderPaymentMethod(@RequestBody PaymentMethod method) {
        supplierOrderConfigService.configPaymentMethod(method);
        return Result.ok();
    }
    @Log("获取采购订单的支付方式")
    @GetMapping("/payment/method")
    public Result<PaymentMethod> getPaymentMethod() {
        return Result.ok(supplierOrderConfigService.getPaymentMethod());
    }

}
