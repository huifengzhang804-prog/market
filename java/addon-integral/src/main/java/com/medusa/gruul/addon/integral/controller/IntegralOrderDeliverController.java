package com.medusa.gruul.addon.integral.controller;


import com.medusa.gruul.addon.integral.model.dto.IntegralOrderDeliveryDTO;
import com.medusa.gruul.addon.integral.mp.entity.IntegralOrder;
import com.medusa.gruul.addon.integral.service.IntegralOrderDeliverService;
import com.medusa.gruul.common.log.annotation.Log;
import com.medusa.gruul.common.model.resp.Result;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Size;
import lombok.RequiredArgsConstructor;
import me.chanjar.weixin.common.error.WxErrorException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author shishuqian
 * date 2023/2/6
 * time 9:21
 * 积分订单发货
 **/

@RestController
@RequestMapping("/integral/order/deliver")
@RequiredArgsConstructor
@PreAuthorize("@S.platformPerm('fatetrue:integral')")
public class IntegralOrderDeliverController {

    private final IntegralOrderDeliverService integralOrderDeliverService;


    /**
     * 查看所有待发货商品  批量发货
     *
     * @return 待发货积分订单列表
     */
    @Log("查询所有待发货积分订单 批量发货")
    @GetMapping("/batch/undeliver")
    public Result<List<IntegralOrder>> undeliverBatch() {
        return Result.ok(
                this.integralOrderDeliverService.undeliverBatch()
        );
    }

    /**
     * 查看待发货商品信息  手动发货
     *
     * @param integralOrderNo 积分订单号
     * @return 待发货积分订单信息
     */
    @Log("待发货积分订单信息 手动发货")
    @GetMapping("/single/undeliver/{integralOrderNo}")
    public Result<IntegralOrder> undeliver(@PathVariable("integralOrderNo") String integralOrderNo) {
        return Result.ok(
                this.integralOrderDeliverService.undeliver(integralOrderNo)
        );
    }

    /**
     * 积分订单发货
     *
     * @param integralOrderDeliveryDTOList 要发货的积分订单信息列表
     */
    @Log("积分订单发货")
    @PutMapping
    public Result<Void> deliver(@RequestBody @Valid @Size(min = 1) List<IntegralOrderDeliveryDTO> integralOrderDeliveryDTOList) throws WxErrorException {
        this.integralOrderDeliverService.deliver(integralOrderDeliveryDTOList);
        return Result.ok();
    }


    /**
     * 积分订单确认收货
     *
     * @param integralOrderNo 积分订单号
     */
    @Log("积分订单确认收货")
    @PutMapping("/complete/{integralOrderNo}")
    @PreAuthorize("@S.matcher().role(@S.USER).match()")
    public Result<Void> complete(@PathVariable("integralOrderNo") String integralOrderNo) {
        this.integralOrderDeliverService.complete(false, integralOrderNo);
        return Result.ok();
    }


}
