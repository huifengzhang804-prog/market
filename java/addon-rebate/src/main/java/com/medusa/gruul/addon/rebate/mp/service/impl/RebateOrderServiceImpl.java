package com.medusa.gruul.addon.rebate.mp.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.medusa.gruul.addon.rebate.model.dto.RebateOrderQueryDTO;
import com.medusa.gruul.addon.rebate.model.vo.RebateOrderStatistic;
import com.medusa.gruul.addon.rebate.mp.entity.RebateOrder;
import com.medusa.gruul.addon.rebate.mp.entity.RebateOrderItem;
import com.medusa.gruul.addon.rebate.mp.mapper.RebateOrderMapper;
import com.medusa.gruul.addon.rebate.mp.service.IRebateOrderService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

/**
 * <p>
 * 返利订单表 服务实现类
 * </p>
 *
 * @author WuDi
 * @since 2023-07-17
 */
@Service
public class RebateOrderServiceImpl extends ServiceImpl<RebateOrderMapper, RebateOrder> implements IRebateOrderService {

    /***
     * 分页查询消费返利订单
     * @param rebateOrderQuery 查询参数
     * @return 消费返利订单
     */
    @Override
    public RebateOrderQueryDTO pageRebateOrder(RebateOrderQueryDTO rebateOrderQuery) {
        return baseMapper.pageRebateOrder(rebateOrderQuery);
    }

    /**
     * 统计消费返利订单金额
     *
     * @param rebateOrderIds 返利订单ids
     * @return 消费返利订单统计
     */
    @Override
    public RebateOrderStatistic rebateOrderStatistic(Set<Long> rebateOrderIds) {
        return baseMapper.rebateOrderStatistic(rebateOrderIds);
    }

    @Override
    public List<RebateOrderItem> pageRebateOrderItem(Set<String> orderNos, String keyword) {
        return baseMapper.pageRebateOrderItem(orderNos, keyword);
    }
}
