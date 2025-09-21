package com.medusa.gruul.order.service.mp.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.medusa.gruul.order.api.entity.OrderDiscount;
import com.medusa.gruul.order.service.model.dto.OrderDetailQueryDTO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 订单优惠项 Mapper 接口
 *
 * @author 张治保
 * @since 2022-06-08
 */
public interface OrderDiscountMapper extends BaseMapper<OrderDiscount> {

    /**
     * 查询订单折扣详情列表
     *
     * @param query 查询条件
     * @return 订单折扣详情列表
     */
    List<OrderDiscount> orderDiscounts(@Param("query") OrderDetailQueryDTO query);
    /**
     * description 根据订单号查询订单折扣列表
     * param [orderNos]
     * return java.util.List<com.medusa.gruul.order.api.entity.OrderDiscount>
     * author jipeng
     * createTime 2024/1/10 9:14
     **/
    List<OrderDiscount> orderDiscountsByOrderNos(@Param("orderIds") List<String> orderNos);
}
