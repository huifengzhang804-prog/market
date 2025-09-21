package com.medusa.gruul.payment.service.controller;

import com.medusa.gruul.common.log.annotation.Log;
import com.medusa.gruul.common.model.resp.Result;
import com.medusa.gruul.payment.api.enums.NotifyStatus;
import com.medusa.gruul.payment.service.model.HttpRequestNoticeParams;
import com.medusa.gruul.payment.service.service.MultiPayNotifyService;
import com.medusa.gruul.payment.service.service.MultiPayOrderService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


/**
 * 商户支付控制层
 *
 * @author xiaoq
 * @since 2022-07-27 14:33
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/merchant/pay")
@Slf4j
public class MultiPayController {

    private final MultiPayNotifyService multiPayNotifyService;
    private final MultiPayOrderService multiPayOrderService;

    /**
     * 业务回调
     *
     * @param request   回调请求体
     * @param detailsId 支付商户表id
     * @return 支付数据
     */
    @Log("回调")
    @RequestMapping(value = "notify/{detailsId}", produces = MediaType.TEXT_PLAIN_VALUE)
    public String payNotify(HttpServletRequest request, @PathVariable String detailsId) {
        return multiPayNotifyService.payNotify(detailsId, new HttpRequestNoticeParams(request));
    }

    /**
     * 根据业务订单号获取业务订单支付状态
     *
     * @param outTradeNo 业务订单号
     * @return NotifyStatus.java
     **/
    @GetMapping("order/status")
    @PreAuthorize("@S.matcher().role(@S.R.USER).match()")
    @Log("支付轮训")
    public Result<NotifyStatus> orderPayStatus(@NotNull String outTradeNo) {
        return Result.ok(multiPayOrderService.orderPayStatus(outTradeNo));
    }


}
