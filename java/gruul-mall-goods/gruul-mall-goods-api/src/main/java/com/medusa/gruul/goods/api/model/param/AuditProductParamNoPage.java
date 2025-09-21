package com.medusa.gruul.goods.api.model.param;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.medusa.gruul.common.model.enums.SellType;
import com.medusa.gruul.goods.api.entity.Product;
import com.medusa.gruul.goods.api.enums.ProductSortEnum;
import com.medusa.gruul.goods.api.model.enums.ProductAuditStatus;
import com.medusa.gruul.goods.api.model.enums.ProductType;
import lombok.Data;

/**
 * 审核商品param
 *
 * @author xiaoq
 * @Description AuditProductParam.java
 * @date 2023-09-27 13:22
 */
@Data
public class AuditProductParamNoPage  {

    /**
     * 商品名称
     */
    private String name;


    /**
     * 店铺三级类目id
     */
    private Long categoryId;


    /**
     * 商品类型
     */
    private ProductType productType;


    /**
     * 店铺id
     */
    private Long shopId;


    /**
     * 商品审核状态
     */
    private ProductAuditStatus productAuditStatus;


    /**
     * 平台类目id
     */
    private Long platformCategoryId;


    /**
     * 销售方式
     */
    private SellType sellType;
    /**
     * 排序方式
     */
    private ProductSortEnum sort;


}
