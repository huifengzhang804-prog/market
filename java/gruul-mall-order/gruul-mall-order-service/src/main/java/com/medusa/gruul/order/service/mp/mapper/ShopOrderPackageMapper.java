package com.medusa.gruul.order.service.mp.mapper;

import com.medusa.gruul.order.api.entity.ShopOrderPackage;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.medusa.gruul.order.service.model.bo.ShopOrderPackageQueryBO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 
 * 订单物流配送 Mapper 接口
 * 
 *
 * @author 张治保
 * @since 2022-06-08
 */
public interface ShopOrderPackageMapper extends BaseMapper<ShopOrderPackage> {

    /**
     * 根据查询条件查询所有的订单包裹
     * @param orderNo 订单号
     * @param query 查询条件
     * @return 订单包裹
     */
    List<ShopOrderPackage> deliveredPackages(@Param("orderNo") String orderNo, @Param("query") ShopOrderPackageQueryBO query);
}
