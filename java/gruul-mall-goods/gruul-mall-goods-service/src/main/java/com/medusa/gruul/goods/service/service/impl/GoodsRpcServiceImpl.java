package com.medusa.gruul.goods.service.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.CollectionUtil;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.conditions.query.QueryChainWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.medusa.gruul.common.custom.aggregation.classify.dto.CategoryRankDTO;
import com.medusa.gruul.common.model.base.ShopProductKey;
import com.medusa.gruul.common.model.constant.CommonPool;
import com.medusa.gruul.common.model.enums.SellType;
import com.medusa.gruul.common.mp.model.DS;
import com.medusa.gruul.common.mp.model.TenantShop;
import com.medusa.gruul.common.redis.util.RedisUtil;
import com.medusa.gruul.goods.api.entity.Product;
import com.medusa.gruul.goods.api.entity.ShopFollow;
import com.medusa.gruul.goods.api.model.dto.ConsignmentPriceSettingDTO;
import com.medusa.gruul.goods.api.model.dto.ProductDTO;
import com.medusa.gruul.goods.api.model.enums.PricingType;
import com.medusa.gruul.goods.api.model.enums.ProductAuditType;
import com.medusa.gruul.goods.api.model.enums.ProductStatus;
import com.medusa.gruul.goods.api.model.param.AuditProductParam;
import com.medusa.gruul.goods.api.model.param.PlatformCategoryParam;
import com.medusa.gruul.goods.api.model.param.PlatformProductParam;
import com.medusa.gruul.goods.api.model.param.ProductRandomParam;
import com.medusa.gruul.goods.api.model.vo.*;
import com.medusa.gruul.goods.api.rpc.GoodsRpcService;
import com.medusa.gruul.goods.service.addon.GoodsAddonSupporter;
import com.medusa.gruul.goods.service.model.vo.ProductNumVO;
import com.medusa.gruul.goods.service.mp.service.IProductService;
import com.medusa.gruul.goods.service.mp.service.IShopFollowService;
import com.medusa.gruul.goods.service.service.CategoryService;
import com.medusa.gruul.goods.service.service.ProductAuditService;
import com.medusa.gruul.goods.service.util.GoodsUtil;
import com.medusa.gruul.storage.api.dto.ShopProductKeyDTO;
import com.medusa.gruul.storage.api.rpc.StorageRpcService;
import com.medusa.gruul.storage.api.vo.ProductStatisticsVO;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * @author xiaoq
 * @ description GoodsRpc实现
 * @since 2022-07-15 16:39
 */
@Service
@DubboService
@RequiredArgsConstructor
public class GoodsRpcServiceImpl implements GoodsRpcService {
    private final IProductService productService;
    private final IShopFollowService shopFollowService;
    private final GoodsAddonSupporter goodsAddonSupporter;
    private final ProductAuditService productAuditService;
    private final CategoryService categoryService;
    private StorageRpcService storageRpcService;


    @Lazy
    @Autowired
    public void setStorageRpcService(StorageRpcService storageRpcService) {
        this.storageRpcService = storageRpcService;
    }

    /**
     * 平台 获取商品信息
     *
     * @param platformProductParam 查询条件
     * @return 符合条件得商品信息
     */
    @Override
    public Page<PlatformProductVO> queryProductInfoByParam(PlatformProductParam platformProductParam) {
        return productService.queryProductInfoByParam(platformProductParam);
    }

    /**
     * 查询运费模版id是否被商品使用
     *
     * @param templateId 物流模板id
     * @return Boolean
     */
    @Override
    public Boolean checkProductByTemplateId(Long templateId) {
        return DS.sharding(() -> productService.lambdaQuery().eq(Product::getFreightTemplateId, templateId).exists());
    }

    /**
     * 获取商品信息
     *
     * @param shopId    店铺id
     * @param productId 商品id
     * @return 商品信息
     */
    @Override
    public Product getProductInfo(@NotNull Long shopId, @NotNull Long productId) {
        return productService.getProductInfo(shopId, productId);
    }

    @Override
    public Map<ShopProductKey, Product> getProductBatch(Set<ShopProductKey> shopProductKeys) {
        return productService.getProductBatch(shopProductKeys);
    }

    /**
     * 批量获取商品信息 包含已删除商品
     *
     * @param shopProductKeys shopId,productId 店铺 id,商品id
     * @return map<{ shopId, productId }, product> 商品信息map
     */
    @Override
    public Map<ShopProductKey, Product> getProductListBatch(Set<ShopProductKey> shopProductKeys) {
        return productService.getProductListBatch(shopProductKeys);
    }

    /**
     * 平台端获取商品信息
     *
     * @param levelCategoryList     list<三级类目id>
     * @param platformCategoryParam 查询数据
     * @return 平台商品基础Vo
     */
    @Override
    public Page<ApiPlatformProductVO> getProductInfoByPlatformCategoryId(List<Long> levelCategoryList,
                                                                         PlatformCategoryParam platformCategoryParam) {
        Page<ApiPlatformProductVO> page =
                productService.getProductInfoByPlatformCategoryId(levelCategoryList, platformCategoryParam);
        List<ApiPlatformProductVO> records = page.getRecords();
        if (CollUtil.isEmpty(records)) {
            return page;
        }
        Map<String, ProductStatisticsVO> productStatisticsMap = storageRpcService.getProductStatisticsMap(
                page.getRecords().stream()
                        .map(
                                product -> new ShopProductKeyDTO()
                                        .setShopId(product.getSellType() == SellType.CONSIGNMENT ?
                                                product.getSupplierId() : product.getShopId())
                                        .setProductId(product.getId())
                        )
                        .collect(Collectors.toList())
        );
        records.forEach(record -> {
            SellType productSellType = record.getSellType();
            Long shopId = productSellType == SellType.CONSIGNMENT ? record.getSupplierId() : record.getShopId();
            ProductStatisticsVO productStatisticsVO = productStatisticsMap.get(
                    RedisUtil.key(shopId, record.getId())
            );

            if (productSellType == SellType.CONSIGNMENT) {
                ConsignmentPriceSettingDTO consignmentPriceSetting = record.getExtra().getConsignmentPriceSetting();
                if (consignmentPriceSetting != null) {
                    boolean isRegular = consignmentPriceSetting.getType() == PricingType.REGULAR;
                    long newHighestPrice = isRegular
                            ? consignmentPriceSetting.getSale()
                            : (productStatisticsVO.getHighestPrice() * consignmentPriceSetting.getSale() / 1000000);
                    productStatisticsVO.setHighestPrice(productStatisticsVO.getHighestPrice() + newHighestPrice);
                    long newLowestPrice = isRegular
                            ? consignmentPriceSetting.getScribe()
                            :
                            (productStatisticsVO.getLowestPrice() * consignmentPriceSetting.getScribe() / 1000000);
                    productStatisticsVO.setLowestPrice(productStatisticsVO.getLowestPrice() + newLowestPrice);
                }
            }
            record.setStatistics(productStatisticsVO);

        });
        return page;
    }

    /**
     * 获取平台三级类目下商品数量
     *
     * @param thirdIds 平台类目三级ids
     * @return map<平台类目ids, 商品数量>
     */
    @Override
    public Map<Long, Integer> getProductNumByPlatformThirdCategoryId(Set<Long> thirdIds) {
        List<ProductNumVO> productNum = productService.getProductNumByPlatformThirdCategoryId(thirdIds);
        return productNum.stream().collect(Collectors.toMap(ProductNumVO::getPlatformCategoryId, ProductNumVO::getNum));
    }

    /**
     * 获取随机商品
     *
     * @param productRandomParam 商品随机参数
     * @return 随机商品
     */
    @Override
    public Page<Product> randomGoods(ProductRandomParam productRandomParam) {
        return productService.randomGoods(productRandomParam);
    }

    /**
     * 根据平台三级类目ids 获取  ApiProductVO
     *
     * @param categoryRank 类目等级dto
     * @return ApiProductVO
     */
    @Override
    public Page<ApiProductVO> getApiProductInfoByPlatformCategoryId(CategoryRankDTO categoryRank) {
        return productService.getApiProductInfoByPlatformCategoryId(categoryRank);
    }


    /**
     * 获取条件商品信息 包含以删除商品信息
     *
     * @param shopId    店铺id
     * @param productId 商品id
     * @return 商品信息
     */
    @Override
    public Product getConditionProductInfo(Long shopId, Long productId) {
        return productService.getConditionProductInfo(shopId, productId);
    }

    /**
     * 用户收藏店铺数量
     *
     * @param userId 用户userid
     * @return 收藏店铺数量
     */
    @Override
    public Long shopFollow(Long userId) {
        return shopFollowService.lambdaQuery()
                .eq(ShopFollow::getUserId, userId)
                .count();
    }

    @Override
    public boolean getSigningCategoryProduct(Set<Long> signingCategorySecondIds, Long shopId) {
        return productService.getSigningCategoryProduct(signingCategorySecondIds, shopId);
    }

    /**
     * 根据{@code supplierId}和{@code productId}获取商品信息
     *
     * @param supplierId 供应商ID
     * @param productId  商品id
     * @return {@link Product}
     */
    @Override
    public Product getProductBySupplierIdAndProductId(Long supplierId, Long productId) {
        Product product = TenantShop.disable(() -> productService.getProductBySupplierIdAndProductId(supplierId,
                productId));
        if (product != null) {
            return product;
        }
        Set<ShopProductKey> shopProductKeySet = new HashSet<>();
        shopProductKeySet.add(new ShopProductKey().setProductId(productId).setShopId(supplierId));
        // 查询采购商品
        List<ProductDTO> supplierGoods = goodsAddonSupporter.getSupplierGoods(shopProductKeySet);
        if (CollectionUtil.isEmpty(supplierGoods)) {
            return null;
        }
        ProductDTO productDTO = supplierGoods.get(CommonPool.NUMBER_ZERO);
        product = new Product();
        product.setId(productDTO.getId());
        product.setName(productDTO.getName());
        product.setPic(productDTO.getPic());
        return product;
    }

    @Override
    public IPage<AuditProductVO> getAuditProductList(AuditProductParam auditProductParam) {
        return productService.getAuditProductList(auditProductParam);
    }

    @Override
    public ProductAuditType getProductAuditSetting() {
        return productAuditService.productAuditSetting();
    }

    /**
     * 获取所有店铺的所有一级分类下的商品数量
     *
     * @param shopIds 店铺ID集合
     * @return {@link ProductFirstCategoryVO}
     */
    @Override
    public Map<Long, List<ProductFirstCategoryVO>> getProductFirstCategories(Set<Long> shopIds) {
        Map<Long, List<ProductFirstCategoryVO>> result = new HashMap<>();
        Map<Long, List<ProductCategoryLevel1WithNumVO>> shopIdAndCategoryMap = TenantShop
                .disable(() -> categoryService.pageCategoryLevel1WithProductNum(shopIds));
        shopIdAndCategoryMap.forEach((k, v) -> result.put(k, v.stream()
                .map(item ->
                        new ProductFirstCategoryVO()
                                .setId(String.valueOf(item.getId()))
                                .setProductNum(item.getProductNum())
                                .setName(item.getName()))
                .collect(Collectors.toList())));
        return result;
    }

    /**
     * 获取推荐店铺下最新的前5条商品
     *
     * @param shopIds 店铺ids
     * @return Map<店铺id, 商品数组>
     */
    @Override
    public Map<Long, List<ProductVO>> getTopFiveProductOrderTime(Set<Long> shopIds) {
        List<ProductVO> productList = TenantShop.disable(() -> productService.getTopFiveProductOrderTime(shopIds));
        return productList.stream().collect(Collectors.groupingBy(ProductVO::getShopId));
    }

    /**
     * 获取有上架商品的店铺id集合
     *
     * @return 店铺id集合
     */
    @Override
    public List<Long> getShopIdListBySellOnProduct() {
        return TenantShop.disable(productService::getShopIdListBySellOnProduct);
    }

    /**
     * 店铺关注人数
     *
     * @param shopId 店铺id
     * @return 关注人数
     */
    @Override
    public Long followCount(Long shopId) {
        return shopFollowService.followCount(shopId);
    }

    @Override
    public Map<Long, ShopFollowVO> batchShopFollow(Set<Long> shopIds, Long userId) {
        QueryChainWrapper<ShopFollow> queryWrapper = shopFollowService.query();
        //用户id做为总关注数
        if (userId == null) {
            queryWrapper.select("shop_id", "COUNT(user_id) AS user_id");
        } else {
            queryWrapper.select("shop_id", "COUNT(user_id) AS user_id", "COUNT(IF(user_id =" + userId + ",1,null)) AS isFollow");
        }
        Map<Long, ShopFollowVO> result = new HashMap<>(shopIds.size());
        queryWrapper.in("shop_id", shopIds)
                .groupBy("shop_id")
                .list()
                .forEach(
                        shopFollow -> {
                            Long isFollow = shopFollow.getIsFollow();
                            result.put(
                                    shopFollow.getShopId(),
                                    new ShopFollowVO()
                                            .setFollow(shopFollow.getUserId())
                                            .setIsFollow(isFollow != null && isFollow > 0)
                            );
                        }
                );
        for (Long shopId : shopIds) {
            if (result.containsKey(shopId)) {
                continue;
            }
            result.put(shopId, new ShopFollowVO().setFollow(0L).setIsFollow(Boolean.FALSE));
        }
        return result;
    }

    @Override
    public Map<Long, Long> batchSellProductCount(Set<Long> shopIds) {
        Map<Long, Long> result = new HashMap<>(shopIds.size());
        TenantShop.disable(
                        () -> productService.query()
                                //id 作为统计的数量
                                .select("shop_id", "COUNT(id) AS id")
                                .eq("`status`", ProductStatus.SELL_ON.getStatus())
                                .in("shop_id", shopIds)
                                .groupBy("shop_id")
                                .list()
                )
                .forEach(product -> result.put(product.getShopId(), product.getId()));
        for (Long shopId : shopIds) {
            if (result.containsKey(shopId)) {
                continue;
            }
            result.put(shopId, 0L);
        }
        return result;
    }

    @Override
    public Set<Long> queryShopIsUserFollow(Long userId, Set<Long> shopIds) {
        String key = GoodsUtil.userFollowShopKey(userId);
        if (!RedisUtil.getRedisTemplate().hasKey(key)) {
            //缓存不存在
            List<ShopFollow> list = shopFollowService.lambdaQuery()
                    .select(ShopFollow::getShopId)
                    .eq(ShopFollow::getUserId, userId)
                    .list();
            if (CollectionUtil.isEmpty(list)) {
                //沒有数据 空占位
                RedisUtil.setCacheSet(key, Sets.newHashSet(CommonPool.NUMBER_ZERO));
            } else {
                Set<Long> followShopIds = list.stream().map(ShopFollow::getShopId).collect(Collectors.toSet());
                RedisUtil.setCacheSet(key, followShopIds);
            }
            RedisUtil.expire(key, CommonPool.NUMBER_ONE, TimeUnit.DAYS);
        }
        Map<Object, Boolean> map = RedisUtil.getRedisTemplate().opsForSet().isMember(key, shopIds.toArray());
        Set<Long> result = Sets.newHashSet();
        map.forEach((k, v) -> {
            if (v) {
                result.add(Long.valueOf(k.toString()));
            }
        });
        return result;
    }

    @Override
    public Integer queryHasStockAndOnSaleProductCount(Long shopId) {
        Long count = TenantShop.disable(() -> {
            //查询有库存且在售商品数量
            return productService.lambdaQuery()
                    .eq(Product::getShopId, shopId)
                    .eq(Product::getStatus, ProductStatus.SELL_ON.getStatus())
                    .eq(Product::getDeleted, Boolean.FALSE)
                    //库存为空是无限库存 或者库存大于0
                    .apply("(total_stock is null or total_stock > 0)")
                    .count();

        });
        return count.intValue();
    }

    @Override
    public Map<Long, List<Long>> queryShopOnSaleProductIds(Set<Long> shopIds) {
        List<Product> list = TenantShop.disable(() -> productService.lambdaQuery()
                .select(Product::getId, Product::getShopId)
                .eq(Product::getStatus, ProductStatus.SELL_ON.getStatus())
                .eq(Product::getDeleted, Boolean.FALSE)
                .in(Product::getShopId, shopIds)
                .list());
        if (CollectionUtil.isEmpty(list)) {
            return Maps.newHashMap();
        }
        return Optional.ofNullable(list)
                .filter(CollectionUtil::isNotEmpty)
                .map(products -> products.stream()
                        .collect(Collectors.groupingBy(
                                Product::getShopId,
                                Collectors.mapping(
                                        Product::getId,
                                        Collectors.toList()
                                )
                        )))
                .orElse(Maps.newHashMap());
    }

    @Override
    public Set<Long> queryExistsProductIds(Long shopId, Set<Long> productIds) {
        Set<Long> existsProductIds = TenantShop.disable(() -> productService.lambdaQuery()
                        .select(Product::getId)
                        .eq(Product::getShopId, shopId)
                        .in(Product::getId, productIds)
                        .list())
                .stream()
                .map(Product::getId)
                .collect(Collectors.toSet());
        return existsProductIds;
    }

}
