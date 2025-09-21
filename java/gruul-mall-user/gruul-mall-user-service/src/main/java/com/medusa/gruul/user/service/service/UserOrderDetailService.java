package com.medusa.gruul.user.service.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.medusa.gruul.order.api.model.OrderPaidBroadcastDTO;
import com.medusa.gruul.user.service.mp.entity.UserDealDetail;

/**
 * 用户明细
 *
 * @author xiaoq
 * @Description UserDetailsService.java
 * @date 2022-11-28 16:28
 */
public interface UserOrderDetailService {
    /**
     *  保存用户订单明细
     *
     * @param orderPaidBroadcast 订单付费信息DTO
     */
    void saveOrderDetail(OrderPaidBroadcastDTO orderPaidBroadcast);

    /**
     * 获取用户订单明细
     *
     * @param userId 用户id
     * @param page 分页
     * @return IPage<UserDealDetail>
     */
    IPage<UserDealDetail> getUserDealDetailsList(Long userId, Page<UserDealDetail> page);

}
