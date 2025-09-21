package com.medusa.gruul.addon.supplier.model.param;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.medusa.gruul.common.model.enums.SellType;
import lombok.Data;

import java.util.Set;

/**
 * 货源param
 *
 * @author xiaoq
 * @Description SupplyListParam.java
 * @date 2023-07-21 18:00
 */
@Data
public class SupplyListParam extends Page<Object> {

    /**
     * 供应商id
     */
    private Long supplierId;

    /**
     * 店铺id
     */
    private Long shopId;

    /**
     * 供应商商品名称
     */
    private String supplierGoodsName;


    /**
     * 销售类型
     */
    private SellType sellType;


    /**
     * 平台二级类目id
     */
    private Long platformCategoryParentId;

    /**
     * 平台二级类目ids
     */
    private Set<Long>  platformCategoryParentIds;


}
