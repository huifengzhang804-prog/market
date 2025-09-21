package com.medusa.gruul.order.service.modules.deliver.service;

import com.medusa.gruul.order.api.entity.ShopOrderItem;
import com.medusa.gruul.order.api.entity.ShopOrderPackage;
import com.medusa.gruul.order.api.enums.PackageStatus;
import com.medusa.gruul.order.api.model.OrderPackageKeyDTO;
import com.medusa.gruul.order.service.model.dto.OrderDeliveryItemDTO;
import java.math.BigDecimal;
import java.util.List;

/**
 * 发货服务类
 *
 * @author 张治保
 * date 2022/8/1
 */
public interface DeliverService {


	/**
	 * 拆分发货
	 *
	 * @param shopOrderPackage  订单包裹详情
	 * @param orderPackageKey   订单包裹关键 key
	 * @param orderDeliveryItem 拆分详情
	 * @return 商品总重量
	 */
	BigDecimal splitShopOrderItems(ShopOrderPackage shopOrderPackage, OrderPackageKeyDTO orderPackageKey, List<OrderDeliveryItemDTO> orderDeliveryItem);


	/**
	 * 包裹确认收货
	 *
	 * @param isSystem        是否是系統自動處理业务消息
	 * @param orderPackageKey 订单包裹关键 key
	 * @param nextStatus      下一个状态
	 */
	void packageConfirm(boolean isSystem, OrderPackageKeyDTO orderPackageKey, PackageStatus nextStatus);

	/**
	 * 小程序订单 发货录入
	 *
	 * @param orderPackageKeyDTO 订单包裹关键 key
	 * @throws Exception 微信接口异常
	 */
	void miniAppOrderDeliver(OrderPackageKeyDTO orderPackageKeyDTO) throws Exception;

	/**
	 * 小程序订单 退货发货录入
	 *
	 * @param shopOrderItem afsNo
	 * @throws Exception 微信接口异常
	 */
	void miniAppOrderReturnGoodsDeliver(ShopOrderItem shopOrderItem) throws Exception;
}
