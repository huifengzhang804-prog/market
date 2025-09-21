package com.medusa.gruul.addon.team.service;

import com.medusa.gruul.addon.team.mp.entity.TeamOrder;
import com.medusa.gruul.common.model.base.ShopProductSkuKey;
import com.medusa.gruul.order.api.model.OrderPaidBroadcastDTO;

/**
 * @author 张治保
 * date 2023/3/16
 */
public interface TeamRabbitService {

    /**
     * 订单支付成功
     *
     * @param orderPaid 订单支付成功消息
     */
    void orderPaid(OrderPaidBroadcastDTO orderPaid);


    /**
     * 参团
     *
     * @param isOver    是否已经结束
     * @param userNum   参团人数
     * @param teamOrder 团订单
     * @param skuKey    商品sku
     */
    void joinTeam(boolean isOver, int userNum, TeamOrder teamOrder, ShopProductSkuKey skuKey);
    
}
