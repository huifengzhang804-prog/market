package com.medusa.gruul.addon.integral.mp.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.medusa.gruul.addon.integral.model.dto.IntegralOrderQueryDTO;
import com.medusa.gruul.addon.integral.model.enums.IntegralOrderStatus;
import com.medusa.gruul.addon.integral.model.vo.IntegralOrderListVO;
import com.medusa.gruul.addon.integral.mp.entity.IntegralOrder;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 *
 * </p>
 *
 * @author xiaoq
 * @Description
 * @date 2023-01-31 15:04
 */
public interface IntegralOrderMapper extends BaseMapper<IntegralOrder> {


    /**
     * 分页查询积分订单信息
     * AND
     * order.`no` LIKE CONCAT('%',#{query.keywords},'%'
     * OR
     * order.product_name LIKE CONCAT '%',#{query.keywords},'%'
     *
     */
    IPage<IntegralOrderListVO> integralOrderPage(@Param("query") IntegralOrderQueryDTO integralOrderQueryDTO);

    IntegralOrder getIntegralOrderDetailInfo(@Param("integralOrderNo") String integralOrderNo);

    List<IntegralOrder> unDeliverBatch(@Param("status") IntegralOrderStatus status);

    IntegralOrder undeliver(@Param("orderNo") String orderNo, @Param("status") IntegralOrderStatus status);
}
