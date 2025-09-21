package com.medusa.gruul.order.service.mp.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.medusa.gruul.order.api.entity.OrderEvaluate;
import com.medusa.gruul.order.service.model.dto.ProductEvaluateQueryDTO;
import com.medusa.gruul.order.service.model.vo.ProductEvaluateOverviewVO;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Set;

/**
 * 订单评论 Mapper 接口
 *
 * @author 张治保
 * @since 2022-09-05
 */
public interface OrderEvaluateMapper extends BaseMapper<OrderEvaluate> {


    /**
     * 商品评价概况
     *
     * @param productEvaluateQuery 查询条件
     * @return 商品评价概况
     */
    ProductEvaluateOverviewVO productEvaluateOverview(@Param("query") ProductEvaluateQueryDTO productEvaluateQuery);

    /**
     * 根据productIds查询已评价人数
     *
     * @param productIds 商品ids
     * @return 人数
     */
    List<OrderEvaluate> getEvaluatePerson(@Param("productIds") Set<Long> productIds);
}
