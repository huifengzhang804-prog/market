package com.medusa.gruul.goods.api.rpc;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.medusa.gruul.common.custom.aggregation.classify.dto.CategoryRankDTO;
import com.medusa.gruul.common.model.base.ShopProductKey;
import com.medusa.gruul.goods.api.entity.Product;
import com.medusa.gruul.goods.api.model.enums.ProductAuditType;
import com.medusa.gruul.goods.api.model.param.AuditProductParam;
import com.medusa.gruul.goods.api.model.param.PlatformCategoryParam;
import com.medusa.gruul.goods.api.model.param.PlatformProductParam;
import com.medusa.gruul.goods.api.model.param.ProductRandomParam;
import com.medusa.gruul.goods.api.model.vo.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author : xiaoq
 * @description : GoodsRpcService.java
 * @date : 2022/7/17 20:44
 */
public interface GoodsRpcService {


    /**
     * 平台 获取商品信息
     *
     * @param platformProductParam 查询条件
     * @return 符合条件得商品信息
     */
    Page<PlatformProductVO> queryProductInfoByParam(PlatformProductParam platformProductParam);


    /**
     * 查询运费模版id是否被商品使用
     *
     * @param templateId
     * @return Boolean
     */
    Boolean checkProductByTemplateId(Long templateId);


    /**
     * 根据shopId productId 获取商品信息
     *
     * @param shopId    店铺id
     * @param productId 商品id
     * @return Product
     */
    Product getProductInfo(@NotNull Long shopId, @NotNull Long productId);


    /**
     * 批量获取商品 信息
     *
     * @param shopProductKeys shopId,productId
     * @return map<{ shopId, productId }, product>
     */
    Map<ShopProductKey, Product> getProductBatch(Set<ShopProductKey> shopProductKeys);

    /**
     * 批量获取商品信息 包含已删除商品
     *
     * @param shopProductKeys shopId,productId
     * @return map<{ shopId, productId }, product>
     */
    Map<ShopProductKey, Product> getProductListBatch(Set<ShopProductKey> shopProductKeys);


    /**
     * 根据 平台三级类目id 获取商品信息
     *
     * @param levelCategoryList     list<三级类目id>
     * @param platformCategoryParam 查询数据
     * @return <<Page<ApiPlatformProductVO>>
     */
    Page<ApiPlatformProductVO> getProductInfoByPlatformCategoryId(List<Long> levelCategoryList,
                                                                  PlatformCategoryParam platformCategoryParam);


    /**
     * 获取平台三级类目下商品数量
     *
     * @param thirdIds 平台类目三级ids
     * @return map<平台类目ids, 商品数量>
     */
    Map<Long, Integer> getProductNumByPlatformThirdCategoryId(@NotNull Set<Long> thirdIds);

    /**
     * 获取随机商品
     *
     * @param productRandomParam 商品随机参数
     * @return 随机商品
     */
    Page<Product> randomGoods(ProductRandomParam productRandomParam);

    /**
     * 根据平台三级类目ids 获取  ApiProductVO
     *
     * @param categoryRank 类目等级dto
     * @return ApiPlatformProductVO
     */
    Page<ApiProductVO> getApiProductInfoByPlatformCategoryId(CategoryRankDTO categoryRank);


    /**
     * 获取条件商品信息 包含以删除商品信息
     *
     * @param shopId    店铺id
     * @param productId 商品id
     * @return 商品信息
     */
    Product getConditionProductInfo(Long shopId, Long productId);

    /**
     * 用户收藏店铺数量
     *
     * @param userId 用户userid
     * @return 收藏店铺数量
     */
    Long shopFollow(Long userId);


    /**
     * 获取当前签约类目下是否有商品
     *
     * @param signingCategorySecondIds 签约类目二级ids
     * @param shopId                   店铺id
     * @return 是否可以删除
     */
    boolean getSigningCategoryProduct(Set<Long> signingCategorySecondIds, Long shopId);

    /**
     * 根据{@code supplierId}和{@code productId}获取商品信息
     *
     * @param supplierId 供应商ID
     * @param productId  商品id
     * @return #{@link Product}
     */
    Product getProductBySupplierIdAndProductId(@NotNull Long supplierId, @NotNull Long productId);


    /**
     * 平台获取审核商品数据
     *
     * @param auditProductParam 查询审核商品param
     * @return IPage<AuditProductVO>
     */
    IPage<AuditProductVO> getAuditProductList(AuditProductParam auditProductParam);

    /**
     * 获取商品审核配置
     *
     * @return 商品审核配置
     */
    ProductAuditType getProductAuditSetting();


    /**
     * 获取所有店铺的所有一级分类下的商品数量
     *
     * @param shopIDs 店铺ID集合
     * @return {@link ProductFirstCategoryVO}
     */
    Map<Long, List<ProductFirstCategoryVO>> getProductFirstCategories(Set<Long> shopIDs);

    /**
     * 获取推荐店铺下最新的前5条商品
     *
     * @param shopIds 店铺ids
     * @return Map<店铺id, 商品数组>
     */
    Map<Long, List<ProductVO>> getTopFiveProductOrderTime(Set<Long> shopIds);

    /**
     * 获取有上架商品的店铺id集合
     *
     * @return 店铺id集合
     */
    List<Long> getShopIdListBySellOnProduct();

    /**
     * 店铺关注人数
     *
     * @param shopId 店铺id
     * @return 关注人数
     */
    Long followCount(@NotNull Long shopId);


    /**
     * 批量获取店铺关注人数 和当前用户是否关注
     *
     * @param shopIds 店铺id 列表
     * @param userId  用户id 未登录时为null
     * @return map<店铺id, 关注信息>
     */
    Map<Long, ShopFollowVO> batchShopFollow(@NotNull @Size(min = 1) Set<Long> shopIds, Long userId);

    /**
     * 批量获取店铺在售商品数量
     *
     * @param shopIds 店铺id 列表
     * @return map<店铺id, 在售商品数量>
     */
    Map<Long, Long> batchSellProductCount(@NotNull @Size(min = 1) Set<Long> shopIds);

    /**
     * 根据用户传入的店铺,返回用户关注的店铺ids
     *
     * @param userId
     * @param shopIds
     * @return
     */
    Set<Long> queryShopIsUserFollow(Long userId, Set<Long> shopIds);

    /**
     * 查询店铺在售并且有库存的商品数量
     *
     * @param shopId 店铺id
     * @return 在售商品数量
     */
    Integer queryHasStockAndOnSaleProductCount(Long shopId);

    /**
     * 查询店铺已经上架销售的商品ids
     *
     * @param shopIds
     * @return
     */
    Map<Long, List<Long>> queryShopOnSaleProductIds(Set<Long> shopIds);

    /**
     * 查询店铺未删除的商品Ids
     *
     * @param shopId
     * @param productIds
     * @return
     */
    Set<Long> queryExistsProductIds(Long shopId, Set<Long> productIds);

}
