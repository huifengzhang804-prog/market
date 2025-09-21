package com.medusa.gruul.addon.supplier.model.param;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.medusa.gruul.common.model.enums.SellType;
import com.medusa.gruul.goods.api.enums.ProductSortEnum;
import com.medusa.gruul.goods.api.model.enums.ProductStatus;
import com.medusa.gruul.goods.api.model.enums.ProductType;
import com.medusa.gruul.storage.api.enums.StockChangeType;
import jakarta.validation.constraints.Null;
import java.util.List;
import lombok.Data;


/**
 * 供应商商品查询
 *
 * @author xiaoq
 * @Description SupplierProductParam.java
 * @date 2023-07-17 16:37
 */
@Data
public class SupplierProductParamNoPage{

    /**
     * shopId
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
     * 库存变化类型
     */
    private StockChangeType stockChangeType;

    /**
     * 新增盘点
     */
    private Boolean addInventory = Boolean.FALSE;

    /**
     * 供应商商品状态
     */
    private ProductStatus supplierProductStatus;


    /**
     * 平台三级类目
     */
    private Long platformCategoryId;
    /**
     * 平台二级类目
     */
    private Long secondPlatformCategoryId;

    /**
     * 商品类型
     */
    private ProductType productType;

    /**
     * 供应商商品id
     */
    private Long supplierProductId;


    /**
     * 是否以删除
     */
    private Boolean isDeleted;


    /**
     * 排除得商品ids ,分割
     */
    private String productIds;

    @Null
    private String[] productIdList;
    /**
     * 需要排除的商品ids
     */
    private List<Long> excludeProductIds;

    private ProductSortEnum sort;

}
