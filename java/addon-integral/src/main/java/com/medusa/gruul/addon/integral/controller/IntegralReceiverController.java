package com.medusa.gruul.addon.integral.controller;

import com.medusa.gruul.addon.integral.model.dto.IntegralReceiverDTO;
import com.medusa.gruul.addon.integral.service.ReceiverService;
import com.medusa.gruul.common.log.annotation.Log;
import com.medusa.gruul.common.model.resp.Result;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;


/**
 * @author miskw
 * @date 2023/7/20
 * @describe 修改积分订单收货地址
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/integral/{orderNo}/receiver")
public class IntegralReceiverController {
    private final ReceiverService receiverService;

    /**
     * 修改订单收货人信息
     *
     * @param orderNo  订单号
     * @param receiver 收货的人地址
     */
    @Log("修改订单收货人信息")
    @PutMapping
    @PreAuthorize("@S.matcher().role(@S.USER).match()")
    public Result<Void> updateReceiver(@PathVariable String orderNo, @RequestBody @Valid IntegralReceiverDTO receiver) {
        receiverService.updateReceiver(orderNo, receiver);
        return Result.ok();
    }
}
