package com.medusa.gruul.addon.rebate.mp.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.medusa.gruul.addon.rebate.model.dto.RebateOrderQueryDTO;
import com.medusa.gruul.addon.rebate.model.vo.RebateOrderStatistic;
import com.medusa.gruul.addon.rebate.mp.entity.RebateOrder;
import com.medusa.gruul.addon.rebate.mp.entity.RebateOrderItem;

import com.medusa.gruul.order.api.entity.ShopOrderItem;
import java.util.List;
import java.util.Set;

/**
 * <p>
 * 返利订单表 服务类
 * </p>
 *
 * @author WuDi
 * @since 2023-07-17
 */
public interface IRebateOrderService extends IService<RebateOrder> {

    /***
     * 分页查询消费返利订单
     * @param rebateOrderQuery 查询参数
     * @return 消费返利订单
     */
    RebateOrderQueryDTO pageRebateOrder(RebateOrderQueryDTO rebateOrderQuery);

    /**
     * 统计消费返利订单金额
     *
     * @param rebateOrderQuery 查询参数
     * @return 消费返利订单统计
     */
    RebateOrderStatistic rebateOrderStatistic(Set<Long> rebateOrderQuery);

    /**
     * 分页查询消费返利订单 商品Item
     *
     * @param orderNos 订单nos
     * @param keyword  查询数据
     * @return List<RebateOrderItem>
     */
    List<RebateOrderItem> pageRebateOrderItem(Set<String> orderNos, String keyword);


}
