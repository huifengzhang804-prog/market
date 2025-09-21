package com.medusa.gruul.addon.supplier.mp.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.medusa.gruul.addon.supplier.model.dto.SupplierNewProductCountDTO;
import com.medusa.gruul.addon.supplier.model.param.SupplierProductParam;
import com.medusa.gruul.addon.supplier.model.param.SupplierProductParamNoPage;
import com.medusa.gruul.addon.supplier.model.param.SupplyListParam;
import com.medusa.gruul.addon.supplier.model.vo.SupplierProductListVO;
import com.medusa.gruul.addon.supplier.mp.entity.SupplierGoods;
import com.medusa.gruul.common.model.base.ShopProductKey;
import com.medusa.gruul.goods.api.entity.Product;
import com.medusa.gruul.goods.api.model.param.AuditProductParam;
import com.medusa.gruul.goods.api.model.vo.AuditProductVO;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Set;

/**
 * 供应商商品持久层
 *
 * @author xiaoq
 * @Description SupplierGoodsMapper.java
 * @date 2023-07-17 09:33
 */
public interface SupplierGoodsMapper extends BaseMapper<SupplierGoods> {

    /**
     * 分页获取供应商商品列表VO
     *
     * @param supplierProductParam 供应商商品param
     * @return IPage<SupplierProductListVO>
     */
    IPage<SupplierProductListVO> querySupplierProductList(@Param("supplierProductParam") SupplierProductParam supplierProductParam);

    /**
     * 分页获取供应商货源
     *
     * @param supplyListParam 供应商货源param
     * @return IPage<SupplierProductListVO>
     */
    IPage<SupplierProductListVO> querySupplyListByPlatformCategory(@Param("supplyListParam") SupplyListParam supplyListParam);

    /**
     * 获取商品库存基础列表数据
     *
     * @param supplierProductParam 检索天骄
     * @return IPage<SupplierProductListVO>
     */
    IPage<SupplierProductListVO> queryProductStockBaseList(@Param("supplierProductParam") SupplierProductParam supplierProductParam);

    /**
     * 查询商品 批量
     *
     * @param shopProductKeys Set<shopId,productId>
     * @return List<Product>
     */
    List<Product> queryProductBatch(@Param("shopProductKeys") Set<ShopProductKey> shopProductKeys);

    List<SupplierGoods> getSupplierGoods(@Param("shopProductKeys") Set<ShopProductKey> shopProductKeys);

    /**
     * 获取商品包含已删除商品
     *
     * @param supplierGoods 商品信息
     * @return 结果
     */
    int updateSupplierGoodById(SupplierGoods supplierGoods);

    /**
     * 统计供应商新增商品数量
     *
     * @param dto {@link SupplierNewProductCountDTO}
     * @return {@link Integer}
     */
    Integer countSupplierNewProduct(@Param("dto") SupplierNewProductCountDTO dto);

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
     * 查询符合条件的供应商审核商品VO
     *
     * @param auditProductParam 查询param
     * @return IPage<AuditProductVO>
     */
    IPage<AuditProductVO> querySupplierAuditProduct(@Param("auditProductParam") AuditProductParam auditProductParam);

    /**
     * 查询违规下架供应商商品数量
     *
     * @param supplierProductParam
     * @return
     */
    Integer illegalCount(@Param("supplierProductParam") SupplierProductParamNoPage supplierProductParam);
}
