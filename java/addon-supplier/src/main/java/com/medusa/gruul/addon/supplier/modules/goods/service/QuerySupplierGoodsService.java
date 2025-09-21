package com.medusa.gruul.addon.supplier.modules.goods.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.medusa.gruul.addon.supplier.model.param.SupplierProductParam;
import com.medusa.gruul.addon.supplier.model.param.SupplierProductParamNoPage;
import com.medusa.gruul.addon.supplier.model.param.SupplyListParam;
import com.medusa.gruul.addon.supplier.model.vo.SupplierProductListVO;
import com.medusa.gruul.addon.supplier.mp.entity.SupplierGoods;
import com.medusa.gruul.goods.api.entity.Product;
import com.medusa.gruul.goods.api.model.param.AuditProductParam;
import com.medusa.gruul.goods.api.model.vo.AuditProductVO;
import com.medusa.gruul.goods.api.model.vo.LookProductVO;
import com.medusa.gruul.goods.api.model.vo.ProductVO;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * <p>
 *
 * </p>
 *
 * @author xiaoq
 * @Description
 * @date 2023-07-17 13:28
 */
public interface QuerySupplierGoodsService {

    /**
     * 分页获取供应商商品列表信息
     *
     * @param supplierProductParam 供应商查询param
     * @return IPage<供应商商品ListVO>
     */
    IPage<SupplierProductListVO> getSupplierProductList(SupplierProductParam supplierProductParam);


    /**
     * 获取供应商商品信息
     *
     * @param shopId 供应商id
     * @param id     供应商商品id
     * @return ProductVO
     */
    ProductVO getSupplierProductById(Long shopId, Long id);

    /**
     * 批量查询供应商商品信息
     * @param ids
     * @return
     */
    Map<Long,SupplierGoods> getSupplierProductListByIds(Set<Long> ids);

    /**
     * 店铺获取货源信息 byPlatformCategory
     *
     * @param supplyListParam 检索param
     * @return IPage<SupplierProductListVO>
     */
    IPage<SupplierProductListVO> getSupplyListByPlatformCategory(SupplyListParam supplyListParam);

    /**
     * 库存中心 获取商品库存信息
     * @param supplierProductParam 检索条件
     * @return IPage<SupplierProductListVO>
     */
    IPage<SupplierProductListVO> getProductStockBaseList(SupplierProductParam supplierProductParam);

    /**
     * 供应商商品查看基础数据
     *
     * @param id  商品id
     * @param shopId 供应商id
     * @return  LookProductVO.class
     */
    LookProductVO getLookProductInfo(Long id, Long shopId);

    /**
     * 供应商审核商品列表
     *
     * @param auditProductParam 审核商品列表查询param
     * @return  IPage<AuditProductVO>
     */
    IPage<AuditProductVO> getSupplierAuditProductList(AuditProductParam auditProductParam);

    /**
     * 查询违规下架供应商商品数量
     * @param supplierProductParam
     * @return
     */
    Integer illegalCount(SupplierProductParamNoPage supplierProductParam);
}
