package com.medusa.gruul.order.service.modules.order.service;

import com.medusa.gruul.order.service.model.dto.OrderQueryDTO;
import com.medusa.gruul.order.service.model.vo.OrderShopOverviewVO;

/**
 * @author 张治保
 * date 2022/10/25
 */
public interface OrderOverviewService {

    /**
     * 平台 已完成订单统计
     *
     * @return 统计数量 已以评价包裹为准
     */
    Long orderPlatformOverview();

    /**
     * 店铺 订单统计
     *
     * @param queryPage 统计条件
     * @return 统计查询结果
     */
    OrderShopOverviewVO orderShopOverView(OrderQueryDTO queryPage);
}
