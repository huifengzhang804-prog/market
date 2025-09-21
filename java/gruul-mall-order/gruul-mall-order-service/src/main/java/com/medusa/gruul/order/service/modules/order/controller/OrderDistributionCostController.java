package com.medusa.gruul.order.service.modules.order.controller;

import com.medusa.gruul.common.idem.annotation.Idem;
import com.medusa.gruul.common.log.annotation.Log;
import com.medusa.gruul.common.model.enums.DistributionMode;
import com.medusa.gruul.common.model.resp.Result;
import com.medusa.gruul.order.api.addon.freight.PlatformFreightParam;
import com.medusa.gruul.order.service.modules.order.service.OrderProductFreightCalculate;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * 订单运费计算
 *
 * @author xiaoq
 * @Description OrderDistributionCostController.java
 * @date 2023-05-10 16:48
 */
@RestController
@RequestMapping("/order/distribution/cost")
@RequiredArgsConstructor
public class OrderDistributionCostController {
    private final OrderProductFreightCalculate orderProductFreightCalculate;

    @PostMapping("")
    @Idem(expire = 500)
    @Log("计算商品所需运费金额")
    @PreAuthorize("""
            		@S.matcher()
            		.any(@S.ROLES,  @S.SHOP_ADMIN, @S.USER)
            		.or(@S.consumer().eq(@S.ROLES, @S.SHOP_CUSTOM_ADMIN))
            		.match()
            """)
    public Result<Map<DistributionMode, Map<String, ?>>> getFreightAmount(@RequestBody @Valid PlatformFreightParam platformFreight) {
        return Result.ok(orderProductFreightCalculate.getFreightAmount(platformFreight));
    }

}
