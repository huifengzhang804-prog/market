package com.medusa.gruul.order.service.modules.order.controller;

import com.medusa.gruul.common.idem.annotation.Idem;
import com.medusa.gruul.common.log.annotation.Log;
import com.medusa.gruul.common.model.resp.Result;
import com.medusa.gruul.common.security.resource.helper.ISecurity;
import com.medusa.gruul.global.model.o.FormInput;
import com.medusa.gruul.order.api.entity.OrderForm;
import com.medusa.gruul.order.api.entity.OrderTimeout;
import com.medusa.gruul.order.service.modules.order.service.OrderConfigService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Size;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 订单配置控制器
 *
 * @author 张治保
 * date 2023/2/8
 */
@RestController
@RequestMapping("/order/config")
@RequiredArgsConstructor
public class OrderConfigController {

    private final OrderConfigService orderConfigService;


    /**
     * 订单超时时间配置
     *
     * @param timeout 配置详情
     * @return void
     */
    @Idem
    @PostMapping("/timeout")
    @PreAuthorize("@S.platformPerm('generalSet')")
    public Result<Void> timeout(@RequestBody @Valid OrderTimeout timeout) {
        orderConfigService.timeout(timeout);
        return Result.ok();
    }

    /**
     * 查询订单超时时间配置
     *
     * @return 超时时间配置
     */
    @GetMapping("/timeout")
    @PreAuthorize("@S.platformPerm('generalSet')")
    public Result<OrderTimeout> timeout() {
        return Result.ok(orderConfigService.timeout());
    }


    /**
     * 商铺交易信息编辑
     *
     * @param orderForm orderForm
     */
    @Idem
    @Log("商铺交易信息编辑")
    @PostMapping("/form")
    @PreAuthorize("@S.shopPerm('mall:general:setting')")
    public Result<Void> orderForm(@RequestBody @Valid OrderForm orderForm) {
        orderForm.validParam();
        orderConfigService.orderForm(ISecurity.userMust().getShopId(), orderForm);
        return Result.ok();
    }

    /**
     * 店铺交易信息
     *
     * @return 店铺交易设置
     */
    @Log("店铺交易信息")
    @GetMapping("/form")
    @PreAuthorize("@S.shopPerm('mall:general:setting')")
    public Result<OrderForm> orderForm() {
        return Result.ok(orderConfigService.orderForm(ISecurity.userMust().getShopId()));
    }

    /**
     * 根据店铺id列表 批量查询下单设置
     *
     * @return 批量查询结果
     */
    @Log("批量查询下单设置")
    @GetMapping("/form/batch")
    public Result<Map<Long, List<FormInput>>> orderForms(@RequestParam @Size(min = 1) Set<Long> shopIds) {
        return Result.ok(orderConfigService.orderFormsInput(shopIds));
    }

}
