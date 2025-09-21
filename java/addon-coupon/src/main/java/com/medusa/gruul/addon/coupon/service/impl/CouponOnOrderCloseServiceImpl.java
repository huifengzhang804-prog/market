package com.medusa.gruul.addon.coupon.service.impl;

import cn.hutool.core.collection.CollUtil;
import com.alibaba.fastjson2.JSON;
import com.medusa.gruul.addon.coupon.mp.entity.CouponOrderRecord;
import com.medusa.gruul.addon.coupon.mp.service.ICouponOrderRecordService;
import com.medusa.gruul.addon.coupon.service.CouponOnOrderCloseService;
import com.medusa.gruul.addon.coupon.service.CouponPlusService;
import com.medusa.gruul.common.log.annotation.Log;
import com.medusa.gruul.order.api.enums.OrderCloseType;
import com.medusa.gruul.order.api.pojo.OrderInfo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * @author 张治保
 * date 2022/11/11
 */
@Service
@RequiredArgsConstructor
public class CouponOnOrderCloseServiceImpl implements CouponOnOrderCloseService {

	private final CouponPlusService couponPlusService;
	private final ICouponOrderRecordService couponOrderRecordService;


	@Log("订单关闭 优惠券退回")
	@Override
	public void orderClose(OrderInfo orderInfo) {
		OrderCloseType closeType = orderInfo.getCloseType();
		if (closeType == null || OrderCloseType.AFS == closeType) {
			return;
		}
		String orderNo = orderInfo.getOrderNo();
		Long buyerId = orderInfo.getBuyerId();
		boolean fullClose = OrderCloseType.FULL == closeType;
		CouponOrderRecord record = couponOrderRecordService.lambdaQuery()
				.eq(CouponOrderRecord::getOrderNo, orderNo)
				.eq(CouponOrderRecord::getBuyerId, buyerId)
				.one();
		if (record == null) {
			return;
		}
		Map<Long, Long> coupons = record.getCoupons();
		if (CollUtil.isEmpty(coupons)) {
			this.removeOrderRecord(buyerId, orderNo);
			return;
		}
		Runnable task = fullClose ? () -> this.removeOrderRecord(buyerId, orderNo) :
				() -> {
					Long shopId = orderInfo.getShopId();
					if (!coupons.containsKey(shopId)) {
						return;
					}
					coupons.remove(shopId);
					if (CollUtil.isEmpty(coupons)) {
						this.removeOrderRecord(buyerId, orderNo);
						return;
					}
					couponOrderRecordService.lambdaUpdate()
							.set(CouponOrderRecord::getCoupons, JSON.toJSONString(coupons))
							.eq(CouponOrderRecord::getOrderNo, orderNo)
							.eq(CouponOrderRecord::getBuyerId, buyerId)
							.update();
				};
		couponPlusService.unlockUserCouponBatch(buyerId, coupons, task);
	}

	private void removeOrderRecord(Long buyerId, String orderNo) {
		couponOrderRecordService.lambdaUpdate()
				.eq(CouponOrderRecord::getOrderNo, orderNo)
				.eq(CouponOrderRecord::getBuyerId, buyerId)
				.remove();
	}
}
