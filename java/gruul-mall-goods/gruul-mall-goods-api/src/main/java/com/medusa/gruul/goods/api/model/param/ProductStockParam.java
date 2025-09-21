package com.medusa.gruul.goods.api.model.param;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.medusa.gruul.common.model.enums.SellType;
import com.medusa.gruul.goods.api.model.enums.ProductStatus;
import com.medusa.gruul.goods.api.model.enums.ProductType;
import com.medusa.gruul.storage.api.enums.StockChangeType;
import jakarta.validation.constraints.Null;
import lombok.Data;


/**
 * ProductStockParam.java
 *
 * @author xiaoq
 * @Description ProductStockParam.java
 * @date 2023-07-19 14:59
 */
@Data
public class ProductStockParam extends Page<Object> {

    /**
     * 商品id
     */
    private Long productId;


    /**
     * 产品名称
     */
    private String productName;
    /**
     * 库存变化类型
     */
    private StockChangeType stockChangeType;
    /**
     * 新增盘点
     */
    private Boolean addInventory = Boolean.FALSE;


    /**
     * 销售类型
     */
    private SellType sellType;


    /**
     * 商品类型
     */
    private ProductType productType;


    /**
     * 店铺类目id
     */
    private Long shopCategoryId;


    private ProductStatus status;

    private Boolean isDeleted;


    private Long shopId;

    @Null
    private String[] productIdList;

    /**
     * 排除得商品ids ,分割
     */
    private String productIds;
}
