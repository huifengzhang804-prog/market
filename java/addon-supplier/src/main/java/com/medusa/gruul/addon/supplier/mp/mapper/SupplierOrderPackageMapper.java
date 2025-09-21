package com.medusa.gruul.addon.supplier.mp.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.medusa.gruul.addon.supplier.model.dto.OrderMatchQueryDTO;
import com.medusa.gruul.addon.supplier.mp.entity.SupplierOrderPackage;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author 张治保
 * date 2023/7/25
 */
public interface SupplierOrderPackageMapper extends BaseMapper<SupplierOrderPackage> {

    /**
     * 查询已发货包裹信息
     *
     * @param query 查询条件
     * @return 已发货包裹信息
     */
    List<SupplierOrderPackage> deliveryPackages(@Param("query") OrderMatchQueryDTO query);
}
