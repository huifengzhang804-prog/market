package com.medusa.gruul.addon.integral.controller;

import com.medusa.gruul.addon.integral.model.dto.IntegralOrderPayDTO;
import com.medusa.gruul.addon.integral.mp.service.IIntegralOrderPaymentService;
import com.medusa.gruul.addon.integral.service.IntegralOrderPayService;
import com.medusa.gruul.common.log.annotation.Log;
import com.medusa.gruul.common.model.resp.Result;
import com.medusa.gruul.common.security.model.bean.SecureUser;
import com.medusa.gruul.common.security.resource.helper.ISecurity;
import com.medusa.gruul.payment.api.model.pay.PayResult;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;


/**
 * @author shishuqian
 * date 2023/2/2
 * time 17:26
 **/

@RestController
@RequestMapping("/integral/order/pay")
@RequiredArgsConstructor
@PreAuthorize("@S.matcher().role(@S.USER).match()")
public class IntegralOrderPayController {
    private final IntegralOrderPayService integralOrderPayService;

    private final IIntegralOrderPaymentService iIntegralOrderPaymentService;

    /**
     * 用户发起订单支付，获取积分订单支付渲染数据
     *
     * @param integralOrderPayDTO 积分订单支付信息dto
     * @return 渲染的支付数据
     * @author shishuqian
     */
    @Log("获取积分订单支付渲染数据")
    @PostMapping("/get")
    public Result<PayResult> toPay(@RequestBody @Valid IntegralOrderPayDTO integralOrderPayDTO) {
        SecureUser secureUser = ISecurity.userMust();
        return Result.ok(
                this.integralOrderPayService.toPay(integralOrderPayDTO, secureUser)
        );
    }

    /**
     * 获取积分订单是否支付成功
     *
     * @param integralOrderNo 积分订单号
     * @return 积分订单是否支付成功
     * true:支付成功
     * false:未支付成功
     * @author shishuqian
     */
    @Log("获取积分订单是否支付成功")
    @GetMapping("/success/{integralOrderNo}")
    public Result<Boolean> geyPayStatus(@PathVariable("integralOrderNo") String integralOrderNo) {
        return Result.ok(
                this.iIntegralOrderPaymentService.integralOrderPayStatus(integralOrderNo)
        );
    }

}
