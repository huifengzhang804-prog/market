package com.medusa.gruul.afs.service.filter;

import com.medusa.gruul.afs.service.model.bo.AfsUpdateBO;
import com.medusa.gruul.afs.service.model.enums.AfsError;
import com.medusa.gruul.afs.service.mp.entity.AfsOrder;
import com.medusa.gruul.afs.service.util.AfsUtil;
import com.medusa.gruul.common.model.resp.SystemCode;
import com.medusa.gruul.global.model.filter.FilterContext;
import com.medusa.gruul.global.model.filter.IAutomaticFilter;
import com.medusa.gruul.order.api.entity.ShopOrderItem;
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
public class GetAndCheckShopOrderItemFilter implements IAutomaticFilter<AfsUpdateBO> {

	private final OrderRpcService orderRpcService;

	@Override
	public void doFilter(FilterContext<AfsUpdateBO> context) {
		AfsOrder afsOrder = context.getData().getAfsOrder();
		String orderNo = afsOrder.getOrderNo();
		Long shopId = afsOrder.getShopId();
		Long shopOrderItemId = afsOrder.getShopOrderItemId();
		ShopOrderItem shopOrderItem = orderRpcService.getShopOrderItem(orderNo, shopId, shopOrderItemId).getOrElseThrow(SystemCode.DATA_NOT_EXIST::exception);
		AfsUtil.checkShopOrderItemForAfs(afsOrder.getBuyerId(), shopOrderItem);
		AfsError.AFS_STATUS_NOT_MATCH_ORDER_STATUS.falseThrow(afsOrder.getStatus() == shopOrderItem.getAfsStatus());
		context.getData().setShopOrderItem(shopOrderItem);
	}
}
