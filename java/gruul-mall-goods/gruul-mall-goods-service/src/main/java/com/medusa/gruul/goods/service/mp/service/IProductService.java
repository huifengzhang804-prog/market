package com.medusa.gruul.goods.service.mp.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.medusa.gruul.common.custom.aggregation.classify.dto.CategoryRankDTO;
import com.medusa.gruul.common.model.base.ShopProductKey;
import com.medusa.gruul.common.model.base.ShopProductSkuKey;
import com.medusa.gruul.goods.api.entity.Product;
import com.medusa.gruul.goods.api.model.dto.CategorySigningCustomDeductionRationMqDTO;
import com.medusa.gruul.goods.api.model.dto.ProductDTO;
import com.medusa.gruul.goods.api.model.dto.ProductStatusChangeDTO;
import com.medusa.gruul.goods.api.model.dto.SupplierGoodsUpdateStatusDTO;
import com.medusa.gruul.goods.api.model.enums.ProductStatus;
import com.medusa.gruul.goods.api.model.param.*;
import com.medusa.gruul.goods.api.model.vo.*;
import com.medusa.gruul.goods.service.model.dto.ConsignmentProductDTO;
import com.medusa.gruul.goods.service.model.dto.ProductDetailDTO;
import com.medusa.gruul.goods.service.model.dto.ShopProductSkuIdDTO;
import com.medusa.gruul.goods.service.model.param.PurchaseProductParam;
import com.medusa.gruul.goods.service.model.vo.ProductDeliveryVO;
import com.medusa.gruul.goods.service.model.vo.ProductDetailVO;
import com.medusa.gruul.goods.service.model.vo.ProductNumVO;
import com.medusa.gruul.goods.service.model.vo.SupplierIssueProductListVO;
import com.medusa.gruul.order.api.model.OrderCompletedDTO;
import com.medusa.gruul.shop.api.model.dto.ShopsEnableDisableDTO;
import com.medusa.gruul.storage.api.bo.StSvBo;
import com.medusa.gruul.storage.api.dto.ProductPriceUpdateDTO;
import com.medusa.gruul.storage.api.vo.ProductSaleVolumeVO;
import com.medusa.gruul.storage.api.vo.ProductSkusVO;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Supplier;

/**
 * 商品信息表 服务类
 *
 * @author xiaoq
 * @since 2022-03-04
 */
public interface IProductService extends IService<Product> {

    /**
     * 商品发布
     *
     * @param productDto 商品信息Dto
     */
    Product issueProduct(ProductDTO productDto);

    /**
     * 商品删除
     *
     * @param productIds 商品ids
     */
    void deleteProductList(Set<Long> productIds);

    /**
     * 产品上下架
     *
     * @param isPlatform          是否是平台更新的状态
     * @param productStatusChange 商品状态更改信息
     * @param status              产品上下架状态
     */
    void updateProductStatus(boolean isPlatform, ProductStatusChangeDTO productStatusChange, ProductStatus status);

    /**
     * 商品信息修改
     *
     * @param productDto 商品信息
     */
    void updateProduct(ProductDTO productDto);


    /**
     * 查询单个商品信息
     *
     * @param id     商品id
     * @param shopId 店铺id
     * @return 商品详情
     */
    ProductVO getProductById(Long id, Long shopId);

    /**
     * 查询商品详情
     *
     * @param param 商品详情查询参数
     * @return 商品详情
     */
    ProductDetailVO details(ProductDetailDTO param);

    /**
     * 查询商品信息列表
     *
     * @param productParam 查询条件
     * @return 符合条件的商品信息
     */
    IPage<ProductVO> getProductList(ProductParam productParam);


    /**
     * 平台获取商品信息
     *
     * @param platformProductParam 查询条件
     * @return 符合条件得所有商品信息
     */
    Page<PlatformProductVO> queryProductInfoByParam(PlatformProductParam platformProductParam);

    //==============================C端========================================================
    //==============================C端=========================================================


    /**
     * 根据平台类目Id 获取商品信息List
     *
     * @param platformCategoryParam 商品查询param by平台类目
     * @param levelCategoryList     三级类目
     * @return Page<ApiPlatformProductVO>
     */
    Page<ApiPlatformProductVO> getProductInfoByPlatformCategoryId(List<Long> levelCategoryList,
                                                                  PlatformCategoryParam platformCategoryParam);

    /**
     * 根据排序type获取店铺商品信息
     *
     * @param apiProductParam apiProductParam
     * @return Page<ApiProductVo>
     */
    Page<ApiProductVO> getProductInfoByParam(ApiProductParam apiProductParam);

    /**
     * 根据平台类目三级Id 获取商铺信息List
     *
     * @param platformCategoryParam platformCategoryParam
     * @return Page<ApiPlatformProductVO>
     */
    Page<ApiPlatformProductVO> getProductInfoByPlatformThirdlyCategoryId(PlatformCategoryParam platformCategoryParam);


    /**
     * 店铺状态改变 启用/禁用
     *
     * @param shopsEnableDisable 店铺禁用启用参数
     */
    void shopChange(ShopsEnableDisableDTO shopsEnableDisable);

    /**
     * 获取商品信息
     *
     * @param shopId    店铺id
     * @param productId 商品id
     * @return 商品信息
     */
    Product getProductInfo(Long shopId, Long productId);

    /**
     * 批量获取商品 信息
     *
     * @param shopProductKeys shopId,productId
     * @return map<{ shopId, productId }, product>
     */
    Map<ShopProductKey, Product> getProductBatch(Set<ShopProductKey> shopProductKeys);

    /**
     * 批量获取商品 信息 包含已删除商品
     *
     * @param shopProductKeys shopId,productId
     * @return map<{ shopId, productId }, product>
     */
    Map<ShopProductKey, Product> getProductListBatch(Set<ShopProductKey> shopProductKeys);

    /**
     * 获取商品信息
     *
     * @param productSupplier Supplier
     * @param key             redisKey
     * @return Product
     */
    Product init(Supplier<Product> productSupplier, String key);

    /**
     * 获取店铺在售商品数量
     *
     * @param shopId 店铺id
     * @return 店铺在售商品数量
     */
    Long getShopSalesProductCount(Long shopId);

    /**
     * 获取平台三级类目下商品数量
     *
     * @param thirdIds 平台类目三级ids
     * @return map<平台类目ids, 商品数量>
     */
    List<ProductNumVO> getProductNumByPlatformThirdCategoryId(Set<Long> thirdIds);

    /**
     * 获取随机商品
     *
     * @param productRandomParam 参数
     * @return 商品
     */
    Page<Product> randomGoods(ProductRandomParam productRandomParam);

    /**
     * 获取商品状态数量
     *
     * @return List<ProductStatusQuantityVO>
     */
    Map<ProductStatus, Long> getGoodsQuantity();

    /**
     * 获取今日新增商品数量
     *
     * @return 今日新增商品数量
     */
    Long getTodayAddGoodsQuantity();


    /**
     * 根据类目id 及类目级别 获取商品信息
     *
     * @param categoryRank 类目级别dto
     * @return Page<ApiProductVO>
     */
    Page<ApiProductVO> getProductInfoByCategoryId(CategoryRankDTO categoryRank);

    /**
     * pc端-看了又看
     *
     * @param productRandomParam 参数
     * @return 看了又看
     */
    Page<ApiProductLookAndSeeVO> lookAndSeePage(ProductRandomParam productRandomParam);

    /**
     * 根据平台三级类目ids 获取  ApiProductVO
     *
     * @param categoryRank 类目等级dto
     * @return ApiProductVO
     */
    Page<ApiProductVO> getApiProductInfoByPlatformCategoryId(CategoryRankDTO categoryRank);

    /**
     * pc端-店铺热销
     *
     * @param shopId 店铺id
     * @param size   查询数量
     * @return 店铺热销
     */
    List<ProductSaleVolumeVO> shopHotSales(Long shopId, Long size);

    /**
     * pc端-热门关注
     *
     * @return 热门关注
     */
    List<ApiProductPopularAttentionVO> shopPopularAttention();

    /**
     * 分页获取商品和规格信息
     *
     * @param productParam 商品查询参数
     * @return 商品规格信息
     */
    IPage<ProductSkusVO> getProductSkus(ProductParam productParam);

    /**
     * 保存供应商商品评分
     *
     * @param orderCompleted 订单完成数据
     */
    void saveSupplierProductRate(OrderCompletedDTO orderCompleted);

    /**
     * 获取条件商品信息 包含以删除商品信息
     *
     * @param shopId    店铺id
     * @param productId 商品id
     * @return 商品信息
     */
    Product getConditionProductInfo(Long shopId, Long productId);

    /**
     * 查询运费相关信息
     *
     * @param productSkuIds 商品skuId
     * @return 运费相关信息
     */
    List<ProductDeliveryVO> getProductDelivery(List<ShopProductSkuIdDTO> productSkuIds);

    /**
     * 查看签约类目下是否存在商品
     *
     * @param signingCategorySecondIds 二级签约类目ids
     * @param shopId                   店铺id
     * @return 是否存在
     */
    boolean getSigningCategoryProduct(Set<Long> signingCategorySecondIds, Long shopId);

    /**
     * 商品名称修改
     *
     * @param id   商品id
     * @param name 商品名称
     */
    void updateProductName(Long id, String name);

    /**
     * 获取商品库存基础信息
     *
     * @param param 查询param
     * @return ProductStockVO
     */
    IPage<ProductStockVO> getProductStockBaseInfo(ProductStockParam param);

    /**
     * 获取采购发布商品
     *
     * @param param 查询param
     * @return IPage<SupplierIssueProductListVO>
     */
    IPage<SupplierIssueProductListVO> getPurchaseIssueProducts(PurchaseProductParam param);

    /**
     * 已采购发布商品装修修改
     *
     * @param id 商品id
     */
    void purchaseIssueProductUpdateStatus(Long id);

    /**
     * 供应商商品状态更新
     *
     * @param supplierGoodsUpdateStatus 更新的商品数据
     */
    void supplierGoodsUpdateStatus(SupplierGoodsUpdateStatusDTO supplierGoodsUpdateStatus);

    /**
     * 已铺货代销商品
     *
     * @param purchaseProductParam 查询参数
     * @return IPage<已铺货的代销商品>
     */
    IPage<SupplierIssueProductListVO> getPaveGoods(PurchaseProductParam purchaseProductParam);

    /**
     * 已铺货代销商品上架
     *
     * @param productId 商品id
     */
    void consignmentProductUpdateStatus(Long productId);

    /**
     * 供应商商品删除
     *
     * @param keys 供应商 id 、商品 id 集合
     */
    void supplierForceGoodsStatus(Set<ShopProductKey> keys);

    /**
     * 供应商商品信息修改
     *
     * @param supplierProduct 商品信息
     */
    void supplierUpdateGoods(Product supplierProduct);

    /**
     * 代销商品信息
     *
     * @param id 商品id
     * @return 商品信息
     */
    ProductVO getConsignmentProductInfo(Long id);

    /**
     * 代销商品修改
     *
     * @param consignmentProduct 代销商品修改DTO
     */
    void consignmentProductUpdate(ConsignmentProductDTO consignmentProduct);


    /**
     * 根据{@code supplierId}和{@code productId}获取商品信息
     *
     * @param supplierId 供应商ID
     * @param productId  商品id
     * @return #{@link Product}
     */
    Product getProductBySupplierIdAndProductId(Long supplierId, Long productId);

    /**
     * 查看商品基础信息
     *
     * @param id     商品id
     * @param shopId 店铺id
     * @return LookProductVO
     */
    LookProductVO getLookProductInfo(Long id, Long shopId);

    /**
     * 审核商品列表
     *
     * @param auditProductParam 审核商品列表查询param
     * @return IPage<AuditProductVO>
     */
    IPage<AuditProductVO> getAuditProductList(AuditProductParam auditProductParam);

    /**
     * 审核商品再次提交
     *
     * @param id 商品id
     */
    void auditProductSubmit(Long id);

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
     * 更新商品的类目扣率
     *
     * @param dto 更新商品类目扣率DTO
     */
    void updateProductCategoryDeductionRation(CategorySigningCustomDeductionRationMqDTO dto);

    /**
     * 商品详情，sku切换
     *
     * @param shopId    店铺id
     * @param productId 商品id
     * @param skuId     skuId
     * @param salePrice 销售价
     */
    ProductVO getProductChangeSkuInfo(Long shopId, Long productId, Long skuId, Long salePrice);

    /**
     * 商品sku金额修改 同步
     *
     * @param priceUpdate 商品价格更新DTO
     */
    void productSkuPriceUpdate(ProductPriceUpdateDTO priceUpdate);

    /**
     * 更新商品标签
     *
     * @param id      商品Id
     * @param labelId 商品标签Id
     */
    void updateProductLabel(Long id, Long labelId);

    /**
     * 查询违规下架商品数量
     *
     * @param productParam
     * @return
     */
    int illegalProductCount(PlatformProductParamNoPage productParam);

    /**
     * 商品恢复下架
     *
     * @param shopProductKey
     */
    void restoreSale(ShopProductKey shopProductKey);

    /**
     * 查询审核中的商品的数量
     *
     * @param productParam
     * @return 数量
     */
    Integer auditingCount(AuditProductParamNoPage productParam);

    /**
     * 查询指定类型的商品数量
     *
     * @param productParam 查询参数
     * @return 数量
     */
    Long getProductCount(ProductParamNoPage productParam);

    /**
     * 更新产品的销量
     *
     * @param mergeResult
     */
    void updateProductSales(Map<ShopProductSkuKey, StSvBo> mergeResult);

    /**
     * 查询上新店铺
     *
     * @param shopIds
     * @return
     */
    List<Long> getNewUpShop(List<Long> shopIds);
}
