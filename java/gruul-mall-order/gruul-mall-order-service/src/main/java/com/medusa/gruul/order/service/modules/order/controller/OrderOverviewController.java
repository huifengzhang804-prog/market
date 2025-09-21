package com.medusa.gruul.order.service.modules.order.controller;

import com.medusa.gruul.common.log.annotation.Log;
import com.medusa.gruul.common.model.enums.SellType;
import com.medusa.gruul.common.model.resp.Result;
import com.medusa.gruul.common.security.resource.helper.ISecurity;
import com.medusa.gruul.order.service.model.dto.OrderQueryDTO;
import com.medusa.gruul.order.service.model.vo.OrderShopOverviewVO;
import com.medusa.gruul.order.service.modules.order.service.OrderOverviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Set;

/**
 * 订单统计控制器
 *
 * @author 张治保
 * date 2022/10/25
 */
@RestController
@RequestMapping("/order/overview")
@RequiredArgsConstructor
public class
OrderOverviewController {

    private final OrderOverviewService orderOverviewService;

    /**
     * 平台总订单数，按照已评价的包裹计算
     *
     * @return 统计数量
     */
    @Log("平台总订单数，和订单列表保持一致")
    @PreAuthorize("@S.platformPerm('overview')")
    @GetMapping("/platform")
    public Result<Long> orderPlatformOverview() {
        return Result.ok(
                orderOverviewService.orderPlatformOverview()
        );
    }

    /**
     * 店铺订单统计
     *
     * @return 统计结果
     */
    @Log("店铺订单统计")
    @PreAuthorize("""
            		@S.matcher().any(@S.ROLES,@S.R.SUPPLIER_ADMIN,@S.SHOP_ADMIN).
            		or(@S.consumer().eq(@S.ROLES,@S.SHOP_CUSTOM_ADMIN).eq(@S.PERMS,'overview')).match()
            """)
    @GetMapping("/shop")
    public Result<OrderShopOverviewVO> orderShopOverView() {
        OrderQueryDTO queryPage = new OrderQueryDTO();
        queryPage.setSearchCount(true);
        queryPage.setSize(0);
        queryPage.setCurrent(1);
        ISecurity.match()
                .ifUser(secureUser -> queryPage.setBuyerId(secureUser.getId()))
                .ifAnyShopAdmin(secureUser -> queryPage.setShopIds(Set.of(secureUser.getShopId())))
                .ifAnySupplierAdmin(
                        secureUser -> queryPage.setSupplierId(secureUser.getShopId())
                                .setSellType(SellType.CONSIGNMENT)
                );
        return Result.ok(
                orderOverviewService.orderShopOverView(queryPage)
        );
    }

}
