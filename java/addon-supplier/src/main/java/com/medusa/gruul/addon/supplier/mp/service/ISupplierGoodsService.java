package com.medusa.gruul.addon.supplier.mp.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.medusa.gruul.addon.supplier.model.dto.SupplierNewProductCountDTO;
import com.medusa.gruul.addon.supplier.model.param.SupplierProductParam;
import com.medusa.gruul.addon.supplier.model.param.SupplierProductParamNoPage;
import com.medusa.gruul.addon.supplier.model.param.SupplyListParam;
import com.medusa.gruul.addon.supplier.model.vo.SupplierProductListVO;
import com.medusa.gruul.addon.supplier.mp.entity.SupplierGoods;
import com.medusa.gruul.common.model.base.ShopProductKey;
import com.medusa.gruul.goods.api.entity.Product;
import com.medusa.gruul.goods.api.model.dto.CategorySigningCustomDeductionRationMqDTO;
import com.medusa.gruul.goods.api.model.param.AuditProductParam;
import com.medusa.gruul.goods.api.model.vo.AuditProductVO;
import com.medusa.gruul.storage.api.bo.StSvBo;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 供货商商品数据层
 *
 * @author xiaoq
 * @Description ISupplierGoodsService.java
 * @date 2023-07-17 09:34
 */
public interface ISupplierGoodsService extends IService<SupplierGoods> {

    IPage<SupplierProductListVO> getSupplierProductList(SupplierProductParam supplierProductParam);

    IPage<SupplierProductListVO> getSupplyListByPlatformCategory(SupplyListParam supplyListParam);

    IPage<SupplierProductListVO> getProductStockBaseList(SupplierProductParam supplierProductParam);

    Map<ShopProductKey, Product> getProductBatch(Set<ShopProductKey> shopProductKeys);

    List<SupplierGoods> getSupplierGoods(Set<ShopProductKey> shopProductKeys);

    /**
     * 更新商品名称，包含已删除商品
     *
     * @param supplierGoods 商品信息
     * @return 结果
     */
    int updateSupplierGoodById(SupplierGoods supplierGoods);

    /**
     * 按照{@code dto}统计供应商新增商品数量
     *
     * @param dto {@link SupplierNewProductCountDTO}
     * @return 新增的商品数量
     */
    Integer countNewProduct(SupplierNewProductCountDTO dto);

    /**
     * 统计所有供应商新增商品数量
     *
     * @return {@link Integer}
     */
    Integer countAllSupplierNewProduct();

    /**
     * 统计所有供应商违规商品
     *
     * @return {@link Integer}
     */
    Integer countIrregularityProduct();

    /**
     * 供应商审核商品VO
     *
     * @param auditProductParam 查询param
     * @return {@link AuditProductVO}
     */
    IPage<AuditProductVO> getSupplierAuditProduct(AuditProductParam auditProductParam);

    /**
     * 查询违规下架供应商商品数量
     *
     * @param supplierProductParam
     * @return
     */
    Integer illegalCount(SupplierProductParamNoPage supplierProductParam);

    /**
     * 更新商品的库存
     *
     * @param shopProductSkuKeyStSvBoMap
     */
    void updateProductStock(Map<ShopProductKey, StSvBo> shopProductSkuKeyStSvBoMap);

    /**
     * 更新供应商商品的类目扣率
     * @param dto
     */
    void updateProductCategoryDeductionRation(CategorySigningCustomDeductionRationMqDTO dto);
}
