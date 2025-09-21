package com.medusa.gruul.addon.matching.treasure.mp.service;

import com.medusa.gruul.addon.matching.treasure.mp.entity.SetMealPaymentInfo;
import com.baomidou.mybatisplus.extension.service.IService;
import com.medusa.gruul.order.api.model.OrderPaidBroadcastDTO;
import com.medusa.gruul.order.api.pojo.OrderInfo;
import java.util.Map;
import java.util.Set;

/**
 *  套餐活动支付信息服务类
 *
 * @author WuDi
 * @since 2023-03-21
 */
public interface ISetMealPaymentInfoService extends IService<SetMealPaymentInfo> {

    /**
     * 套餐活动支付信息
     * @param orderPaidBroadcast 订单支付信息
     */
    void matchingTreasurePaymentInfo(OrderPaidBroadcastDTO orderPaidBroadcast);

    /**
     * 套餐活动支付退款信息
     * @param orderInfo 订单信息
     */
    void matchingTreasureRefundInfo(OrderInfo orderInfo);

  /**
   * 查询活动已经完成订单数
   * @param setMealIds
   * @return
   */
  Map<Long, Integer> querySetMealOrderCount(Set<Long> setMealIds);
}
