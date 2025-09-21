package com.medusa.gruul.order.service.mp.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.CollectionUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.medusa.gruul.common.mp.model.TenantShop;
import com.medusa.gruul.order.api.entity.OrderEvaluate;
import com.medusa.gruul.order.service.model.dto.ProductEvaluateQueryDTO;
import com.medusa.gruul.order.service.model.vo.ProductEvaluateOverviewVO;
import com.medusa.gruul.order.service.mp.mapper.OrderEvaluateMapper;
import com.medusa.gruul.order.service.mp.service.IOrderEvaluateService;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 订单评论 服务实现类
 *
 * @author 张治保
 * @since 2022-09-05
 */
@Service
public class OrderEvaluateServiceImpl extends ServiceImpl<OrderEvaluateMapper, OrderEvaluate> implements IOrderEvaluateService {

    @Override
    public ProductEvaluateOverviewVO productEvaluateOverview(ProductEvaluateQueryDTO productEvaluateQuery) {
        return baseMapper.productEvaluateOverview(productEvaluateQuery);
    }


    /**
     * 根据productIds查询已评价人数
     *
     * @param productIds 商品ids
     * @return 人数
     */
    @Override
    public Map<Long, Long> getEvaluatePerson(Set<Long> productIds) {
        if (CollUtil.isEmpty(productIds)) {
            return Collections.emptyMap();
        }
        List<OrderEvaluate> orderEvaluateList = TenantShop.disable(() -> baseMapper.getEvaluatePerson(productIds));
        if (CollectionUtil.isEmpty(orderEvaluateList)) {
            return Collections.emptyMap();
        }
        return orderEvaluateList.stream().collect(Collectors.toMap(OrderEvaluate::getProductId, OrderEvaluate::getEvaluatePerson));
    }
}
