package com.medusa.gruul.addon.supplier.mp.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.handlers.Fastjson2TypeHandler;
import com.medusa.gruul.common.model.enums.SellType;
import com.medusa.gruul.common.mp.model.BaseEntity;
import com.medusa.gruul.goods.api.entity.Product;
import com.medusa.gruul.goods.api.model.enums.ProductStatus;
import com.medusa.gruul.goods.api.model.enums.ProductType;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

/**
 * 供货商商品
 *
 * @author xiaoq
 * @Description SupplierGoods.java
 * @date 2023-07-17 09:31
 */
@Getter
@Setter
@Accessors(chain = true)
@TableName(value = "t_supplier_goods", autoResultMap = true)
public class SupplierGoods extends BaseEntity {

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
     * 供应商商品状态
     */
    private ProductStatus supplierProductStatus;

    /**
     * 平台二级类目
     */
    private Long platformCategoryParentId;
    /**
     * 平台三级类目
     */
    private Long platformCategoryId;

    /**
     * 商品类型
     */
    private ProductType productType;


    /**
     * 有限库存量
     */
    private Integer limitedStock;
    /**
     * 总库存
     */
    private Long totalStock;

    /**
     * 商品信息
     */
    @TableField(typeHandler = Fastjson2TypeHandler.class)
    private Product product;
}
