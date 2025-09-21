package com.medusa.gruul.addon.supplier.modules.goods.service;

import com.medusa.gruul.addon.supplier.model.dto.ProductStatusChangeDTO;
import com.medusa.gruul.common.model.base.ShopProductKey;
import com.medusa.gruul.goods.api.entity.Product;
import com.medusa.gruul.goods.api.model.dto.ProductDTO;
import com.medusa.gruul.goods.api.model.enums.ProductStatus;

import java.util.Set;

/**
 * 编辑供应商商品service
 *
 * @author xiaoq
 * @Description EditSupplierGoodsService.java
 * @date 2023-07-17 13:17
 */
public interface EditSupplierGoodsService {
    /**
     * 供应商商品发布
     *
     * @param supplierProduct 供应商商品信息
     */
    Product issueSupplierProduct(ProductDTO supplierProduct);

    /**
     * 供应商商品删除
     *
     * @param ids 供应商商品ids
     */
    void deleteSupplierProductList(Set<Long> ids);

    /**
     * 供应商商品修改
     *
     * @param supplierProduct 供应商商品信息
     */
    void updateSupplierProduct(ProductDTO supplierProduct);

    /**
     * 供应商商品名称修改
     *
     * @param id   供应商商品id
     * @param name 供应商商品name
     */
    void updateSupplierProductName(Long id, String name);

    /**
     * 供应商商品上下架
     *
     * @param supplierUpdate      是否是供应商自己更新的状态
     *                            true 是供应商自己更新的状态
     *                            false 平台更新
     * @param productStatusChange 商品状态change
     * @param status              修改状态
     */
    void updateSupplierProductStatus(boolean supplierUpdate, ProductStatusChangeDTO productStatusChange, ProductStatus status);

    /**
     * 供应商已拒绝商品 再次提交审核
     *
     * @param id 供应商审核商品id
     */
    void supplierAuditProductSubmit(Long id);

    /**
     * 供应商违规下架商品恢复销售
     *
     * @param key 商品 key
     */
    void restoreSaleSupplierProduct(ShopProductKey key);


    /**
     * 更新 供应商商品状态
     *
     * @param supplierUpdate 是否是供应商自己更新的状态
     *                       true 是供应商自己更新的状态
     *                       false 平台更新
     * @param param          商品状态更改描述
     * @param status         目标状态
     */
    void updateProductStatus(boolean supplierUpdate, com.medusa.gruul.goods.api.model.dto.ProductStatusChangeDTO param, ProductStatus status);
}
