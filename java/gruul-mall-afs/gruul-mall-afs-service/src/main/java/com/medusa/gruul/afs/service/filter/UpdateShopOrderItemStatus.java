package com.medusa.gruul.afs.service.filter;

import com.medusa.gruul.afs.service.model.bo.AfsUpdateBO;
import com.medusa.gruul.global.model.filter.FilterContext;
import com.medusa.gruul.global.model.filter.IAutomaticFilter;
import com.medusa.gruul.order.api.entity.ShopOrderItem;
import com.medusa.gruul.order.api.model.ChangeAfsStatusDTO;
import com.medusa.gruul.order.api.rpc.OrderRpcService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * <p></p>
 *
 * @author 张治保
 * date 2022/8/22
 */
@Component
@RequiredArgsConstructor
public class UpdateShopOrderItemStatus implements IAutomaticFilter<AfsUpdateBO> {

    private final OrderRpcService orderRpcService;

    @Override
    public void doFilter(FilterContext<AfsUpdateBO> context) {
        AfsUpdateBO data = context.getData();
        ShopOrderItem shopOrderItem = data.getShopOrderItem();
        orderRpcService.updateShopOrderItemAfsStatus(
                new ChangeAfsStatusDTO()
                        .setOrderNo(shopOrderItem.getOrderNo())
                        .setShopId(shopOrderItem.getShopId())
                        .setItemId(shopOrderItem.getId())
                        .setAfsNo(data.getAfsNo())
                        .setStatus(data.getNextStatus())
                        .setAfsTradeNo(data.getAfsTradeNo())
                        .setRefundAmount(data.getAfsOrder().getRefundAmount())
        );
    }
}
