package com.medusa.gruul.goods.service.mp.mapper;

import com.baomidou.mybatisplus.annotation.InterceptorIgnore;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.medusa.gruul.common.custom.aggregation.classify.dto.CategoryRankDTO;
import com.medusa.gruul.common.model.base.ShopProductKey;
import com.medusa.gruul.goods.api.entity.Product;
import com.medusa.gruul.goods.api.entity.ProductCategory;
import com.medusa.gruul.goods.api.model.param.*;
import com.medusa.gruul.goods.api.model.vo.*;
import com.medusa.gruul.goods.service.model.param.PurchaseProductParam;
import com.medusa.gruul.goods.service.model.param.SupplierProductParam;
import com.medusa.gruul.goods.service.model.vo.ProductNumVO;
import com.medusa.gruul.goods.service.model.vo.ProductStatusQuantityVO;
import com.medusa.gruul.goods.service.model.vo.SupplierIssueProductListVO;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Set;

/**
 * 商品信息表 Mapper 接口
 *
 * @author xiaoq
 * @since 2022-03-04
 */
public interface ProductMapper extends BaseMapper<Product> {


    /**
     * 获取商品详细信息
     *
     * @param id 商品id
     * @return 单个商品详细信息
     */
    ProductVO getProductById(@Param("id") Long id);


    /**
     * 获取商品信息list
     *
     * @param productParam 商品信息查询参数
     * @return 商品list信息
     */
    IPage<ProductVO> queryProductList(@Param("productParam") ProductParam productParam);


    /**
     * productInfoByParam
     *
     * @param platformProductParam 查询条件
     * @return PlatformProductVo
     */
    @InterceptorIgnore(tenantLine = "true")
    Page<PlatformProductVO> queryProductInfoByParam(@Param("platformProductParam") PlatformProductParam platformProductParam);

    /**
     * 供应商商品信息
     *
     * @param supplierProductParam 供应商商品查询信息
     * @return IPage(供应商商品基础信息)
     */
    IPage<Product> getSupplierProductList(@Param("supplierProductParam") SupplierProductParam supplierProductParam);


    /**
     * 根据平台类目id 获取所对应得商品信息
     *
     * @param platformCategoryParam 分页
     * @param levelCategoryList     levelCategoryList
     * @return 查询结果
     */
    @InterceptorIgnore(tenantLine = "true")
    Page<ApiPlatformProductVO> queryProductInfoByPlatformCategoryIds(@Param("platformCategoryParam") PlatformCategoryParam platformCategoryParam, @Param("levelCategoryList") List<Long> levelCategoryList);

    /**
     * 获取店铺商品基础信息 By apiProductParam
     *
     * @param apiProductParam 查询条件
     * @return Page<ApiProductVo>
     */
    Page<ApiProductVO> getProductInfoByParam(@Param("apiProductParam") ApiProductParam apiProductParam);


    /**
     * 根据平台三级类目id 获取商品信息list
     *
     * @param platformCategoryParam 分页param
     * @return Page<ApiPlatformProductVO>
     */

    Page<ApiPlatformProductVO> getProductInfoByPlatformThirdlyCategoryId(@Param("platformCategoryParam") PlatformCategoryParam platformCategoryParam);

    /**
     * x
     *
     * @param categoryId 类目id
     * @return ProductCategory.java
     */
    ProductCategory queryProductCategory(Long categoryId);


    /**
     * 根据平台三级类目获取 对应的商品数量
     *
     * @param thirdIds 平台三级类目ids
     * @return Map<平台类目id, 商品数量>
     */
    List<ProductNumVO> getProductNumByPlatformThirdCategoryId(@Param("thirdIds") Set<Long> thirdIds);

    /**
     * 获取随机商品
     *
     * @param productRandomParam 参数
     * @return 商品
     */
    Page<Product> randomGoods(@Param("productRandomParam") ProductRandomParam productRandomParam);

    /**
     * 获取商品数量 by status
     *
     * @return ProductStatusQuantityVO.java
     */
    List<ProductStatusQuantityVO> queryGoodsQuantity();

    /**
     * 查询今日新增商品数量
     *
     * @param shopId 店铺id
     * @return 新增商品数量
     */
    Long queryTodayAddGoodsQuantity(@Param("shopId") Long shopId);

    /**
     * 根据三级类目ids 获取商品基础信息
     *
     * @param categoryRank 查询条件
     * @return 店铺商品
     */
    Page<ApiProductVO> getProductInfoByCategoryId(@Param("categoryRank") CategoryRankDTO categoryRank);


    /**
     * 根据平台三级类目ids 获取  ApiProductVO
     *
     * @param categoryRank 类目等级dto
     * @return ApiProductVO
     */
    Page<ApiProductVO> getApiProductInfoByPlatformCategoryId(@Param("categoryRank") CategoryRankDTO categoryRank);

    /**
     * 获取条件商品信息 包含以删除商品信息
     *
     * @param shopId    店铺id
     * @param productId 商品id
     * @return 商品信息
     */
    Product getConditionProductInfo(@Param("shopId") Long shopId, @Param("productId") Long productId);


    /**
     * 批量查询商品信息
     *
     * @param shopProductKeys ShopProductKey
     * @return List<Product>  商品信息
     */
    List<Product> getProductBatch(@Param("shopProductKeys") Set<ShopProductKey> shopProductKeys);

    /**
     * 批量查询商品信息 包含已删除商品
     *
     * @param shopProductKeys ShopProductKey
     * @return List<Product>  商品信息
     */
    List<Product> getProductListBatch(@Param("shopProductKeys") Set<ShopProductKey> shopProductKeys);

    /**
     * 查看签约类目下是否存在商品
     *
     * @param signingCategorySecondIds 签约类目二级ids
     * @param shopId                   店铺id
     * @return 是否存在
     */
    boolean querySigningCategoryProduct(@Param("signingCategorySecondIds") Set<Long> signingCategorySecondIds, @Param("shopId") Long shopId);


    /**
     * 根据商品id 获取商品信息
     *
     * @param productId     商品id
     * @param isQueryDelete 是否查询删除商品
     * @param shopId        店铺id
     * @return 商品信息
     */
    Product getProductInfoById(@Param("productId") Long productId, @Param("isQueryDelete") Boolean isQueryDelete, @Param("shopId") Long shopId);

    /**
     * 根据商品id 获取商品信息
     *
     * @param supplierId    供应商id
     * @param productId     商品id
     * @param isQueryDelete 是否查询删除商品
     * @return 商品信息
     */
    List<Product> getProductBySupplierIdAndProductId(@Param("supplierId") Long supplierId, @Param("productId") Long productId, @Param("isQueryDelete") Boolean isQueryDelete);


    /**
     * 获取商品基础信息
     *
     * @param param             检索条件
     * @param excludeProductIds 排除商品id
     * @return IPage<ProductStockVO>
     */
    IPage<ProductStockVO> queryProductStockBaseInfo(@Param("param") ProductStockParam param,
                                                    @Param("excludeProductIds") List<Long> excludeProductIds);


    /**
     * 获取采购发布商品信息
     *
     * @param param 查询param
     * @return IPage<SupplierIssueProductListVO>
     */
    IPage<SupplierIssueProductListVO> queryPurchaseIssueProducts(@Param("param") PurchaseProductParam param);

    /**
     * 已铺货代销商品
     *
     * @param purchaseProductParam 查询参数
     * @return IPag<已铺货代销商品信息>
     */
    IPage<SupplierIssueProductListVO> getPaveGoods(@Param("param") PurchaseProductParam purchaseProductParam);

    /**
     * 根据商品id集合查询 商品信息(包含已逻辑删除)
     *
     * @param hashProductId keys是否包含商品 id 否则 只是用 shopId 作为供应商 id 查询
     * @param keys          商品id + 店铺id
     * @return 商品信息
     */
    Set<ShopProductKey> queryGoodsByProductIds(@Param("hashProductId") boolean hashProductId, @Param("keys") Set<ShopProductKey> keys);

    /**
     * 强删商品
     *
     * @param shopProductKeys 商品id + 店铺id
     */
    void supplierForceGoodsStatus(@Param("shopProductKeys") Set<ShopProductKey> shopProductKeys);

    /**
     * 修改店铺供应商商品
     *
     * @param id     产品id
     * @param shopId 店铺id
     */
    void updateSupplierSellGoods(@Param("id") Long id, @Param("shopId") Long shopId);

    /**
     * B端商品查看功能
     *
     * @param id     商品id
     * @param shopId 店铺id
     * @return 查看商品信息
     */
    LookProductVO queryLookProductInfo(@Param("id") Long id, @Param("shopId") Long shopId);

    /**
     * 获取审核商品数据
     *
     * @param auditProductParam 审核商品param
     * @return 审核商品列表结果
     */
    IPage<AuditProductVO> queryAuditProductList(@Param("auditProductParam") AuditProductParam auditProductParam);

    /**
     * 按照创建时间排序获取最新的前5条商品
     *
     * @param shopIds 店铺ids
     * @return 商品信息
     */
    List<ProductVO> getTopFiveProductOrderTime(Set<Long> shopIds);

    /**
     * 获取有上架商品的店铺id集合
     *
     * @return 店铺id集合
     */
    List<Long> getShopIdListBySellOnProduct();

    /**
     * 更新已删除商品名称
     *
     * @param product 商品信息
     * @return 更新数量
     */
    int updateProductName(Product product);

    /**
     * 清除商品标签
     *
     * @param id 商品Id
     */
    void clearLabel(@Param("productId") Long id);

    /**
     * 查询违规下架商品数量
     *
     * @param param 查询参数
     * @return
     */
    int illegalProductCount(@Param("platformProductParam") PlatformProductParamNoPage param);

    /**
     * 查询待审核商品数量
     *
     * @param productParam 参数
     * @return 数量
     */
    Integer auditingCount(@Param("auditProductParam") AuditProductParamNoPage productParam);

    /**
     * 查询指定店铺不同tab页下商品数量
     *
     * @param productParam
     * @return
     */
    Long productCount(@Param("productParam") ProductParamNoPage productParam);

    /**
     * 查询有上新的店铺ID
     *
     * @param startDate
     * @param shopIds
     * @return
     */
    List<Long> getNewUpShop(@Param("startDate") String startDate, @Param("shopIds") List<Long> shopIds);
}
