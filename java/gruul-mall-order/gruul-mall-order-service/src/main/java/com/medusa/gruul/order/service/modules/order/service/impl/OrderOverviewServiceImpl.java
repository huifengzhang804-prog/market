package com.medusa.gruul.order.service.modules.order.service.impl;

import com.medusa.gruul.common.mp.model.TenantShop;
import com.medusa.gruul.order.service.model.dto.OrderQueryDTO;
import com.medusa.gruul.order.service.model.enums.OrderQueryStatus;
import com.medusa.gruul.order.service.model.vo.OrderShopOverviewVO;
import com.medusa.gruul.order.service.modules.order.service.OrderOverviewService;
import com.medusa.gruul.order.service.mp.service.IOrderService;
import com.medusa.gruul.order.service.mp.service.IShopOrderItemService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * @author 张治保
 * date 2022/10/25
 */
@Service
@RequiredArgsConstructor
public class OrderOverviewServiceImpl implements OrderOverviewService {


    private final IOrderService orderService;
    private final IShopOrderItemService shopOrderItemService;

    @Override
    public Long orderPlatformOverview() {
        return TenantShop.disable(shopOrderItemService::countCompletedOrderNum);
    }

    @Override
    public OrderShopOverviewVO orderShopOverView(OrderQueryDTO queryPage) {
        OrderShopOverviewVO result = new OrderShopOverviewVO();
        TenantShop.disable(
                () -> {
                    result.setUnpaid(orderService.orderPage(queryPage.setStatus(OrderQueryStatus.UNPAID)).getTotal());
                    result.setUndelivered(orderService.orderPage(queryPage.setStatus(OrderQueryStatus.UN_DELIVERY)).getTotal());
                    result.setUnreceived(orderService.orderPage(queryPage.setStatus(OrderQueryStatus.UN_RECEIVE)).getTotal());
                }
        );
        return result;
    }
}
