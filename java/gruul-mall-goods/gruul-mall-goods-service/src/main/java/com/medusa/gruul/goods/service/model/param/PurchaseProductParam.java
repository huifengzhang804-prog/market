package com.medusa.gruul.goods.service.model.param;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.medusa.gruul.goods.api.model.enums.ProductStatus;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

/**
 * 采购商品查询
 *
 * @author xiaoq
 * @Description PurchaseProductParam.java
 * @date 2023-07-29 14:19
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class PurchaseProductParam extends Page<Object> {

    /**
     * 供应商id (S2B2C)
     */
    private Long supplierId;

    /**
     * 店铺类目
     */
    private Long shopCategoryId;

    /**
     * 平台类目
     */
    private Long platformCategoryParentId;

    /**
     * 商品状态
     */
    private ProductStatus status;


    /**
     * 供应商name(S2B2C)
     */
    private String supplierName;

    /**
     * 商品名称
     */
    private String productName;


    private Boolean isDeleted = Boolean.FALSE;
    /**
     * 铺货员手机号
     */
    private String deliveryUserPhone;
    /**
     * 铺货开始时间
     */
    private LocalDateTime deliveryStartTime;
    /**
     * 铺货结束时间
     */
    private LocalDateTime deliveryEndTime;


    /**
     * 兼容 已铺货 商品名称查询
     *
     * @param supplierGoodsName 商品名称
     */
    public void setSupplierGoodsName(String supplierGoodsName) {
        this.productName = supplierGoodsName;
    }
}
