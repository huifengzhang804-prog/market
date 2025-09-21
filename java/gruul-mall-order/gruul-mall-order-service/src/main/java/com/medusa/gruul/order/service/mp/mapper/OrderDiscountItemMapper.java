package com.medusa.gruul.order.service.mp.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.medusa.gruul.order.api.entity.OrderDiscountItem;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 订单优惠项 Mapper 接口
 *
 * @author 张治保
 * @since 2022-06-08
 */
public interface OrderDiscountItemMapper extends BaseMapper<OrderDiscountItem> {

    /**
     * 查询订单优惠项
     *
     * @param orderNo 订单号
     * @param shopId  店铺id
     * @param itemId  订单商品项id
     * @return 订单优惠项
     */
    List<OrderDiscountItem> getItem(@Param("orderNo") String orderNo, @Param("shopId") Long shopId, @Param("itemId") Long itemId);
}
