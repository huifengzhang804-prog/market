package com.medusa.gruul.order.service.modules.order.controller;

import com.medusa.gruul.common.log.annotation.Log;
import com.medusa.gruul.common.model.resp.Result;
import com.medusa.gruul.order.service.model.dto.ReceiverChangeDTO;
import com.medusa.gruul.order.service.modules.order.service.ReceiverService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;


/**
 * 收货人控制器
 *
 * @author 张治保
 * date 2022/8/2
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/order/{orderNo}/receiver")
public class ReceiverController {

    private final ReceiverService receiverService;


    /**
     * 修改订单收货人信息
     *
     * @param orderNo  订单号
     * @param receiver 收货的人地址
     */
    @Log("修改订单收货人信息")
    @PutMapping
    @PreAuthorize("@S.matcher().role(@S.R.USER).match()")
    public Result<Void> updateReceiver(@PathVariable String orderNo, @RequestBody @Valid ReceiverChangeDTO receiver) {
        receiverService.updateReceiver(orderNo, receiver);
        return Result.ok();
    }
}
