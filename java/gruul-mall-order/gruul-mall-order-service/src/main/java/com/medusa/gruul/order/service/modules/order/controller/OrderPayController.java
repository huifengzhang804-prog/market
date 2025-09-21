package com.medusa.gruul.order.service.modules.order.controller;

import com.medusa.gruul.common.log.annotation.Log;
import com.medusa.gruul.common.model.resp.Result;
import com.medusa.gruul.order.service.model.dto.OrderPayDTO;
import com.medusa.gruul.order.service.modules.order.service.OrderPayService;
import com.medusa.gruul.payment.api.model.pay.PayResult;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


/**
 * 订单支付
 *
 * @author 张治保
 * date 2022/7/28
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/order/pay")
public class OrderPayController {

    private final OrderPayService orderPayService;

    /**
     * 获取渲染支付数据
     *
     * @param orderPay 订单支付类型
     * @return 业务支付结果
     */
    @Log("获取渲染支付数据")
    @PostMapping("/page")
    @PreAuthorize("@S.matcher().role(@S.R.USER).match()")
    public Result<PayResult> toPay(@RequestBody @Valid OrderPayDTO orderPay) {
        return Result.ok(
                orderPayService.toPay(orderPay)
        );
    }


}
