package com.medusa.gruul.addon.rebate.mp.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.medusa.gruul.addon.rebate.model.dto.RebateOrderQueryDTO;
import com.medusa.gruul.addon.rebate.model.vo.RebateOrderStatistic;
import com.medusa.gruul.addon.rebate.mp.entity.RebateOrder;
import com.medusa.gruul.addon.rebate.mp.entity.RebateOrderItem;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Set;

/**
 * <p>
 * 返利订单表 Mapper 接口
 * </p>
 *
 * @author WuDi
 * @since 2023-07-17
 */
public interface RebateOrderMapper extends BaseMapper<RebateOrder> {

    /***
     * 分页查询消费返利订单
     * @param rebateOrderQuery 查询参数
     * @return 消费返利订单
     */
    RebateOrderQueryDTO pageRebateOrder(@Param("rebateOrderQuery") RebateOrderQueryDTO rebateOrderQuery);

    /**
     * 统计消费返利订单金额
     *
     * @param rebateOrderIds 返利订单ids
     * @return 消费返利订单统计
     */
    RebateOrderStatistic rebateOrderStatistic(Set<Long> rebateOrderIds);

    /**
     * 分页查询消费返利订单商品item
     *
     * @param orderNos 查询订单号
     * @param keyword  查询参数
     * @return 消费返利订单item
     */
    List<RebateOrderItem> pageRebateOrderItem(@Param("orderNos") Set<String> orderNos, @Param("keyword") String keyword);
}
