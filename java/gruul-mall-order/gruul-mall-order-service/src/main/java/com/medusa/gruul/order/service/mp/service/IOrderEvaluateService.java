package com.medusa.gruul.order.service.mp.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.medusa.gruul.order.api.entity.OrderEvaluate;
import com.medusa.gruul.order.service.model.dto.ProductEvaluateQueryDTO;
import com.medusa.gruul.order.service.model.vo.ProductEvaluateOverviewVO;

import java.util.Map;
import java.util.Set;

/**
 * 订单评论 服务类
 *
 * @author 张治保
 * @since 2022-09-05
 */
public interface IOrderEvaluateService extends IService<OrderEvaluate> {

    /**
     * 商品评价概况
     *
     * @param productEvaluateQuery 查询条件
     * @return 商品评价概况
     */
    ProductEvaluateOverviewVO productEvaluateOverview(ProductEvaluateQueryDTO productEvaluateQuery);

    /**
     * 根据productIds查询已评价人数
     *
     * @param productIds 商品ids
     * @return 人数
     */
    Map<Long, Long> getEvaluatePerson(Set<Long> productIds);
}
