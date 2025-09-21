package com.medusa.gruul.search.service.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.text.StrPool;
import cn.hutool.core.util.NumberUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSON;
import com.google.common.collect.Lists;
import com.medusa.gruul.common.fastjson2.FastJson2;
import com.medusa.gruul.common.model.base.ActivityShopProductKey;
import com.medusa.gruul.common.model.base.ShopProductKey;
import com.medusa.gruul.common.model.constant.CommonPool;
import com.medusa.gruul.common.model.enums.OrderType;
import com.medusa.gruul.common.model.enums.SellType;
import com.medusa.gruul.common.redis.util.RedisUtil;
import com.medusa.gruul.common.security.model.bean.SecureUser;
import com.medusa.gruul.common.security.resource.extension.RoleTask;
import com.medusa.gruul.common.security.resource.helper.ISecurity;
import com.medusa.gruul.global.model.exception.GlobalException;
import com.medusa.gruul.global.model.helper.CompletableTask;
import com.medusa.gruul.global.model.o.Final;
import com.medusa.gruul.goods.api.entity.Product;
import com.medusa.gruul.goods.api.model.CategoryLevel;
import com.medusa.gruul.goods.api.model.dto.*;
import com.medusa.gruul.goods.api.model.enums.ProductStatus;
import com.medusa.gruul.search.api.enums.BrandStatus;
import com.medusa.gruul.search.api.enums.CategoryCountType;
import com.medusa.gruul.search.api.enums.OperationType;
import com.medusa.gruul.search.api.model.*;
import com.medusa.gruul.search.service.es.entity.EsProductActivityEntity;
import com.medusa.gruul.search.service.es.entity.EsProductEntity;
import com.medusa.gruul.search.service.es.entity.EsProductLabel;
import com.medusa.gruul.search.service.es.mapper.EsProductActivityMapper;
import com.medusa.gruul.search.service.es.mapper.EsProductMapper;
import com.medusa.gruul.search.service.model.SearchConst;
import com.medusa.gruul.search.service.model.SearchParam;
import com.medusa.gruul.search.service.model.dto.SuggestDTO;
import com.medusa.gruul.search.service.model.vo.SearchMemberInfoVO;
import com.medusa.gruul.search.service.model.vo.ShopProductSalesTopVO;
import com.medusa.gruul.search.service.mp.entity.SearchUserAction;
import com.medusa.gruul.search.service.mp.service.ISearchUserActionService;
import com.medusa.gruul.search.service.service.EsProductService;
import com.medusa.gruul.search.service.service.ProductSearchHistoryService;
import com.medusa.gruul.search.service.service.SearchService;
import com.medusa.gruul.search.service.service.addon.SearchAddonSupporter;
import com.medusa.gruul.shop.api.model.dto.ShopsEnableDisableDTO;
import com.medusa.gruul.shop.api.model.vo.ShopInfoVO;
import com.medusa.gruul.shop.api.rpc.ShopRpcService;
import com.medusa.gruul.storage.api.bo.StSvBo;
import com.medusa.gruul.storage.api.dto.ProductPriceUpdateDTO;
import com.medusa.gruul.storage.api.dto.ShopProductKeyDTO;
import com.medusa.gruul.storage.api.entity.StorageSku;
import com.medusa.gruul.storage.api.enums.StockType;
import com.medusa.gruul.storage.api.rpc.StorageRpcService;
import com.medusa.gruul.storage.api.vo.ProductSaleVolumeVO;
import com.medusa.gruul.storage.api.vo.ProductSkusVO;
import com.medusa.gruul.user.api.enums.RightsType;
import com.medusa.gruul.user.api.model.vo.CurrentMemberVO;
import com.medusa.gruul.user.api.model.vo.MemberAggregationInfoVO;
import com.medusa.gruul.user.api.model.vo.RelevancyRightsVO;
import com.medusa.gruul.user.api.rpc.UserRpcService;
import io.vavr.Tuple;
import io.vavr.Tuple7;
import io.vavr.control.Option;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.dromara.easyes.core.biz.EsPageInfo;
import org.dromara.easyes.core.biz.OrderByParam;
import org.dromara.easyes.core.conditions.select.LambdaEsQueryWrapper;
import org.dromara.easyes.core.kernel.EsWrappers;
import org.dromara.easyes.core.kernel.WrapperProcessor;
import org.dromara.easyes.core.toolkit.EntityInfoHelper;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.script.Script;
import org.elasticsearch.script.ScriptType;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.bucket.terms.ParsedLongTerms;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.elasticsearch.search.aggregations.bucket.terms.TermsAggregationBuilder;
import org.elasticsearch.search.aggregations.metrics.ParsedTopHits;
import org.elasticsearch.search.aggregations.metrics.TopHitsAggregationBuilder;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.sort.ScriptSortBuilder;
import org.elasticsearch.search.sort.SortBuilders;
import org.elasticsearch.search.sort.SortMode;
import org.elasticsearch.search.sort.SortOrder;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.Executor;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author 张治保 date 2022/12/1
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class EsProductServiceImpl implements EsProductService {

    private final SearchService searchService;
    private final ShopRpcService shopRpcService;
    private final RedissonClient redissonClient;
    private final EsProductMapper esProductMapper;
    private final StorageRpcService storageRpcService;
    private final EsProductActivityMapper esProductActivityMapper;
    private final Executor searchCompletableTaskExecutor;
    private final ProductSearchHistoryService productSearchHistoryService;
    private final SearchAddonSupporter searchAddonSupporter;
    private final ISearchUserActionService searchUserActionService;
    private final UserRpcService userRpcService;


    /**
     * 将 SKU 转换为和SKU 一一对应 的商品展开平铺数据
     *
     * @param productSkus 商品sku
     * @return 平铺数据 skuIds, specs, prices, salePrices, stockTypes, stock, salesVolume
     */
    private static Tuple7<List<Long>, List<String>, List<Long>, List<Long>, List<StockType>, List<Long>, Long> getCurrentProductSkus(
            List<ProductSkusVO.SkuVO> productSkus) {
        if (CollUtil.isEmpty(productSkus)) {
            return Tuple.of(
                    List.of(),
                    List.of(),
                    List.of(),
                    List.of(),
                    List.of(),
                    List.of(),
                    0L
            );
        }
        //skuId
        List<Long> skuIds = new ArrayList<>();
        //规格
        List<String> specs = new ArrayList<>();
        //原价
        List<Long> prices = new ArrayList<>();
        //销售价
        List<Long> salePrices = new ArrayList<>();
        //库存类型
        List<StockType> stockTypes = new ArrayList<>();
        //库存
        List<Long> stock = new ArrayList<>();
        //销量
        AtomicLong salesVolume = new AtomicLong(0);
        //按价格从小到大排序
        CollUtil.sort(productSkus, (sku1, sku2) -> NumberUtil.compare(sku1.getSkuPrice(), sku2.getSkuPrice()))
                .forEach(
                        sku -> {
                            skuIds.add(sku.getSkuId());
                            specs.add(String.join(StrPool.COMMA, CollUtil.emptyIfNull(sku.getSkuName())));
                            prices.add(sku.getPrice());
                            salePrices.add(sku.getSkuPrice());
                            stockTypes.add(sku.getStockType());
                            stock.add(sku.getSkuStock());
                            salesVolume.addAndGet(sku.getSalesVolume());
                        }
                );
        return Tuple.of(
                skuIds,
                specs,
                prices,
                salePrices,
                stockTypes,
                stock,
                salesVolume.get()
        );
    }

    @Override
    public void shopStatusChange(List<ShopsEnableDisableDTO> shopStatus) {
        Set<Long> enableShopIds = new HashSet<>();
        Set<Long> disableShopIds = new HashSet<>();
        shopStatus.forEach(
                shopSta -> {
                    Set<Long> shopIds = shopSta.getEnable() ? enableShopIds : disableShopIds;
                    shopIds.addAll(shopSta.getShopIds());
                }
        );
        if (CollUtil.isNotEmpty(enableShopIds)) {
            esProductMapper.update(null,
                    EsWrappers.lambdaUpdate(EsProductEntity.class)
                            .in(EsProductEntity::getShopId, enableShopIds)
                            .eq(EsProductEntity::getStatus, ProductStatus.UNUSABLE)
                            .set(EsProductEntity::getStatus, ProductStatus.SELL_ON)
            );
        }
        if (CollUtil.isNotEmpty(disableShopIds)) {
            esProductMapper.update(null,
                    EsWrappers.lambdaUpdate(EsProductEntity.class)
                            .in(EsProductEntity::getShopId, disableShopIds)
                            .eq(EsProductEntity::getStatus, ProductStatus.SELL_ON)
                            .set(EsProductEntity::getStatus, ProductStatus.UNUSABLE)
            );
        }
    }

    @Override
    public void productRelease(ProductBroadcastDTO productRelease) {
        CategoryLevel platformCategory = productRelease.getPlatformCategory();
        CategoryLevel shopCategory = productRelease.getShopCategory();
        Product product = productRelease.getProduct();
        SellType sellType = product.getSellType();
        ConsignmentPriceSettingDTO consignmentPriceSetting = product.getExtra().getConsignmentPriceSetting();
        Long shopId = product.getShopId();
        String shopName = Option.of(shopRpcService.getShopInfoByShopId(shopId)).map(ShopInfoVO::getName).getOrNull();
        Long productId = product.getId();
        Long supplierId = product.getSupplierId();
        Long brandId = product.getBrandId();

        Tuple7<List<Long>, List<String>, List<Long>, List<Long>, List<StockType>, List<Long>, Long> currentProductSkus = getCurrentProductSkus(
                shopId, productId, sellType, supplierId);
        EsProductEntity entity = new EsProductEntity()
                .setId(RedisUtil.key(shopId, productId))
                .setShopId(shopId)
                .setShopName(shopName)
                .setProductId(productId)
                .setProductName(product.getName())
                .setShopType(product.getShopType())
                .setSaleDescribe(product.getSaleDescribe())
                .setProductLabel(getEsProductLabel(product))
                .setFreightTemplateId(product.getFreightTemplateId())
                .setSellType(product.getSellType())
                .setPricingType(consignmentPriceSetting != null ? consignmentPriceSetting.getType() : null)
                .setSale(consignmentPriceSetting != null ? consignmentPriceSetting.getSale() : null)
                .setScribe(consignmentPriceSetting != null ? consignmentPriceSetting.getScribe() : null)
                .setStatus(product.getStatus())
                .setPic(product.getPic())
                .setWidePic(product.getWidePic())
                .setBrandId(brandId)
                .setBrandStatus(brandId != null ? BrandStatus.SELL_ON : null)
                .setSkuIds(currentProductSkus._1())
                .setSpecs(currentProductSkus._2())
                .setPrices(currentProductSkus._3())
                .setSalePrices(currentProductSkus._4())
                .setStockTypes(currentProductSkus._5())
                .setStocks(currentProductSkus._6())
                .setSalesVolume(currentProductSkus._7())
                .setCategoryFirstId(shopCategory.getOne())
                .setCategorySecondId(shopCategory.getTwo())
                .setCategoryThirdId(shopCategory.getThree())
                .setPlatformCategoryFirstId(platformCategory.getOne())
                .setPlatformCategorySecondId(platformCategory.getTwo())
                .setIsDistributed(Boolean.FALSE)
                .setPlatformCategoryThirdId(platformCategory.getThree())
                .setDistributionMode(CollUtil.emptyIfNull(product.getDistributionMode()));
        entity.setCreateTime(product.getCreateTime());
        try {
            esProductMapper.insert(entity);
        } catch (Exception exception) {
            log.error("保存商品数据异常", exception);
        }

    }

    /**
     * productLabel 转 EsProductLabel
     *
     * @param product 商品
     * @return EsProductLabel
     */
    private EsProductLabel getEsProductLabel(Product product) {
        EsProductLabel esProductLabel = new EsProductLabel();
        if (null != product.getProductLabel()) {
            //todo 禁用 BeanUtil.copyProperties
            BeanUtil.copyProperties(product.getProductLabel(), esProductLabel);
        }
        return esProductLabel;
    }

    private Tuple7<List<Long>, List<String>, List<Long>, List<Long>, List<StockType>, List<Long>, Long> getCurrentProductSkus(
            Long shopId, Long productId, SellType sellType, Long supplierId) {
        if (SellType.CONSIGNMENT == sellType) {
            shopId = supplierId;
        }
        List<ProductSkusVO.SkuVO> productSkus = storageRpcService.getProductSkusByShopProductKeys(
                Collections.singletonList(new ShopProductKeyDTO().setShopId(shopId).setProductId(productId)));
        return getCurrentProductSkus(productSkus);
    }

    @Override
    public void productUpdate(ProductBroadcastDTO productUpdate) {
        CategoryLevel platformCategory = productUpdate.getPlatformCategory();
        CategoryLevel shopCategory = productUpdate.getShopCategory();
        Product product = productUpdate.getProduct();
        ConsignmentPriceSettingDTO consignmentPriceSetting = product.getExtra().getConsignmentPriceSetting();
        Long shopId = product.getShopId();
        Long productId = product.getId();
        Long supplierId = product.getSupplierId();
        SellType sellType = product.getSellType();
        String key = RedisUtil.key(shopId, productId);
        Long count = esProductMapper.selectCount(
                EsWrappers.lambdaQuery(EsProductEntity.class).eq(EsProductEntity::getId, key));
        if (count == null || count <= 0) {
            this.productRelease(productUpdate);
            return;
        }
        RLock lock = redissonClient.getLock(RedisUtil.key(SearchConst.PRODUCT_UPDATE_SKU_LOCK, key));
        lock.lock();
        try {
            Tuple7<List<Long>, List<String>, List<Long>, List<Long>, List<StockType>, List<Long>, Long> currentProductSkus = getCurrentProductSkus(
                    shopId, productId, sellType, supplierId);
            EsProductEntity esProductEntity = new EsProductEntity()
                    .setId(key)
                    .setShopId(shopId)
                    .setProductId(productId)
                    .setProductName(product.getName())
                    .setSaleDescribe(product.getSaleDescribe())
                    .setProductLabel(getEsProductLabel(product))
                    .setFreightTemplateId(product.getFreightTemplateId())
                    .setStatus(product.getStatus())
                    .setPic(product.getPic())
                    .setWidePic(product.getWidePic())
                    .setBrandId(product.getBrandId())
                    .setSellType(product.getSellType())
                    .setPricingType(consignmentPriceSetting != null ? consignmentPriceSetting.getType() : null)
                    .setSale(consignmentPriceSetting != null ? consignmentPriceSetting.getSale() : null)
                    .setScribe(consignmentPriceSetting != null ? consignmentPriceSetting.getScribe() : null)
                    .setBrandStatus(product.getBrandId() != null ? BrandStatus.SELL_ON : null)
                    .setSkuIds(currentProductSkus._1())
                    .setSpecs(currentProductSkus._2())
                    .setPrices(currentProductSkus._3())
                    .setSalePrices(currentProductSkus._4())
                    .setStockTypes(currentProductSkus._5())
                    .setStocks(currentProductSkus._6())
                    .setSalesVolume(currentProductSkus._7())
                    .setCategoryFirstId(shopCategory.getOne())
                    .setCategorySecondId(shopCategory.getTwo())
                    .setCategoryThirdId(shopCategory.getThree())
                    .setPlatformCategoryFirstId(platformCategory.getOne())
                    .setPlatformCategorySecondId(platformCategory.getTwo())
                    .setPlatformCategoryThirdId(platformCategory.getThree())
                    .setDistributionMode(CollUtil.emptyIfNull(product.getDistributionMode()));
            esProductEntity.setCreateTime(product.getCreateTime());
            Integer successCount = esProductMapper.updateById(esProductEntity);
            log.info("更新商品数据成功,更新数量:{}", successCount);
        } finally {
            lock.unlock();
        }

    }

    @Override
    public void productStatusUpdate(List<ProductUpdateStatusDTO> updateStatus) {
        updateStatus.forEach(
                productUpdateStatus -> esProductMapper.update(
                        null,
                        EsWrappers.lambdaUpdate(EsProductEntity.class)
                                .eq(EsProductEntity::getShopId, productUpdateStatus.getShopId())
                                .in(EsProductEntity::getProductId, productUpdateStatus.getProductIds())
                                .set(EsProductEntity::getStatus, productUpdateStatus.getProductStatus())
                )
        );
    }

    @Override
    public void productDelete(ProductDeleteDTO productDelete) {
        Long shopId = productDelete.getShopId();
        esProductMapper.deleteBatchIds(
                productDelete.getProductIds().stream().map(productId -> RedisUtil.key(shopId, productId))
                        .collect(Collectors.toList())
        );
    }

    @Override
    public EsPageInfo<EsProductEntity> search(boolean isConsumer, SearchParam param) {
        SearchSourceBuilder sourceBuilder = paramsToSearchBuilder(isConsumer, param);
        EsPageInfo<EsProductEntity> searches = esProductMapper.pageQuery(
                EsWrappers.lambdaQuery(EsProductEntity.class)
                        .setSearchSourceBuilder(sourceBuilder),
                param.getCurrent(),
                param.getSize());
        renderData(searches.getList());
        //查询 优惠券、历史数据
        handleConsumerProduct(searches.getList(), param);
        return searches;
    }

    @Override
    public Map<Long, CategoryStaticVo> categoryCount(CategoryCountParam param) {
        param.validParam();
        log.debug("normal:{}", param.getNormal());
        CategoryCountType countType = param.getType();
        boolean isShop = CategoryCountType.SHOP == param.getType();
        Map<Long, CategoryStaticVo> resp = MapUtil.newHashMap();
        //查询字段
        List<Function<EsProductEntity, Long>> fields = isShop ?
                List.of(
                        EsProductEntity::getCategoryFirstId,
                        EsProductEntity::getCategorySecondId,
                        EsProductEntity::getCategoryThirdId
                ) :
                List.of(
                        EsProductEntity::getPlatformCategoryFirstId,
                        EsProductEntity::getPlatformCategorySecondId,
                        EsProductEntity::getPlatformCategoryThirdId
                );
        searchService.consumerAllMatch(
                esProductMapper,
                EsWrappers.lambdaQuery(EsProductEntity.class)
                        .select(countType.getFirst(), countType.getSecond(), countType.getThird(),countType.getSalesVolume())
                        .eq(param.getNormal(), EsProductEntity::getStatus, ProductStatus.SELL_ON)
                        .eq(isShop, EsProductEntity::getShopId, param.getShopId())
                        .in(CollUtil.isNotEmpty(param.getFirstIds()), countType.getFirst(), param.getFirstIds())
                        .orderByAsc(EsProductEntity::getId),
                product -> fields.forEach(
                        field -> {
                            Long value = field.apply(product);
                            if (value == null) {
                                return;
                            }

                            resp.compute(value, (k, v) -> {
                                if (Objects.isNull(v)) {
                                    return new CategoryStaticVo()
                                            .setProductCount(1L)
                                            .setSalesCount(product.getSalesVolume());
                                }
                                 v.addProductCount(1L);
                                 v.addSalesCount(product.getSalesVolume());
                                return v;

                            });
                        }
                )
        );
        return resp;
    }

    /**
     * 查询优惠券、历史数据
     *
     * @param list 商品列表
     */
    private void handleConsumerProduct(List<EsProductEntity> list, SearchParam param) {
        if (CollUtil.isEmpty(list)) {
            return;
        }
        //店铺id集合
        //优惠券Map集合
        Final<Map<Long, List<SearchCouponVO>>> couponMap = new Final<>(Map.of());
        //历史数据Map集合
        Final<Map<Long, List<SearchUserAction>>> searchUserMap = new Final<>(Map.of());
        //SKU的信息
        Final<Map<ActivityShopProductKey, List<StorageSku>>> skuMap = new Final<>(Map.of());
        //会员信息
        Final<MemberAggregationInfoVO> memberInfo = new Final<>();
        Set<Long> shopIds = list.stream().map(EsProductEntity::getShopId).collect(Collectors.toSet());
        RoleTask match = ISecurity.match();
        CompletableTask.getOrThrowException(
                CompletableTask.allOf(
                        searchCompletableTaskExecutor,
                        ()->{
                            if (!param.getShowSku()) {
                                return ;
                            }
                            //获取商品的SKU
                            Set<ActivityShopProductKey> shopProductKeys =
                                    list.stream().map(this::generateSkuKey).collect(Collectors.toSet());
                            skuMap.set(storageRpcService.productSkuStockBatch(shopProductKeys));


                        },
                        //优惠券
                        () -> {
                            //rpc查询优惠券
                            if (!param.getShowCoupon()) {
                                return;
                            }
                            List<SearchCouponVO> couponList = searchAddonSupporter.getCouponList(shopIds);
                            if (CollUtil.isNotEmpty(couponList)) {
                                couponMap.set(couponList.stream().collect(Collectors.groupingBy(SearchCouponVO::getShopId)));
                            }

                        },
                        //历史数据
                        () -> match
                                .ifUser(user -> {
                                    Long userId = user.getId();
                                    //查询会员信息
                                    Option.of(userRpcService.getTopMemberCardInfo(userId))
                                            .peek(memberInfo::set);
                                    //登录状态再去查询历史数据
                                    List<SearchUserAction> searchUserActions = param.getShowHistory() ?
                                            searchUserActionService.lambdaQuery()
                                                    .eq(SearchUserAction::getUserId, userId)
                                                    .in(SearchUserAction::getShopId, shopIds)
                                                    .list()
                                            : List.of();
                                    searchUserMap.set(
                                            searchUserActions.stream()
                                                    .collect(Collectors.groupingBy(SearchUserAction::getShopId))
                                    );
                                })
                )
        );

        list.forEach(vo -> {
            ActivityShopProductKey activityShopProductKey = generateSkuKey(vo);
            List<StorageSku> storageSkus = skuMap.get().get(activityShopProductKey);
            if (CollUtil.isNotEmpty(storageSkus)) {
                List<ProductSkusVO.SkuVO> skuVOList = storageSkus.stream().map(storageSku -> {
                    ProductSkusVO.SkuVO skuVO = new ProductSkusVO.SkuVO();
                    skuVO.setSkuId(storageSku.getId());
                    skuVO.setImage(storageSku.getImage());
                    return skuVO;
                }).toList();
                vo.setSkuVOS(skuVOList);
            }
            //优惠券
            Map<Long, List<SearchCouponVO>> shopCouponMap = couponMap.get();
            List<SearchCouponVO> coupons = shopCouponMap.getOrDefault(vo.getShopId(), List.of());
            if (CollUtil.isNotEmpty(coupons)) {
                List<SearchCouponVO> tempCoupons=Lists.newArrayList();
                for (SearchCouponVO coupon : coupons) {
                    if (CollectionUtil.isEmpty(coupon.getSearchCouponProductVOList())&&
                    CollectionUtil.isEmpty(coupon.getExcludeCouponProductVOList())) {
                            //全部商品参加
                        tempCoupons.add(coupon);
                    }else if (CollectionUtil.isNotEmpty(coupon.getSearchCouponProductVOList())) {
                        //指定商品参加
                        Set<Long> joinProductIds = coupon.getSearchCouponProductVOList().stream()
                                .map(SearchCouponProductVO::getProductId).collect(
                                        Collectors.toSet());
                        if (joinProductIds.contains(vo.getProductId())) {
                            tempCoupons.add(coupon);
                        }
                    }else if (CollectionUtil.isNotEmpty(coupon.getExcludeCouponProductVOList())){
                        //指定商品不参加
                        Set<Long> excludeProductIds = coupon.getExcludeCouponProductVOList().stream()
                                .map(SearchCouponProductVO::getProductId).collect(
                                        Collectors.toSet());
                        if (!excludeProductIds.contains(vo.getProductId())) {
                            tempCoupons.add(coupon);
                        }
                    }
                }
                tempCoupons.sort(Comparator.comparing(SearchCouponVO::getRowNum));
                vo.setCouponList(tempCoupons);
            }
            //历史数据
            List<SearchUserAction> searchUserActionList = MapUtil.isNotEmpty(searchUserMap.get())
                    ? searchUserMap.get().get(vo.getShopId()) : List.of();
            if (CollUtil.isNotEmpty(searchUserActionList)) {
                //每个用户的每个店铺，有且只有一条历史操作数据
                List<OperationType> typeList = searchUserActionList.get(0).getOperation();
                if (CollUtil.isNotEmpty(typeList)) {
                    typeList.stream()
                            .min(Comparator.comparing(OperationType::getValue))
                            .ifPresent(type -> vo.setShopOperationHistory(type.getMessage()));
                }
            }
            //会员信息：会员类型、会员价标签、会员权益
            if (null != memberInfo.get()) {
                SearchMemberInfoVO infoVO = new SearchMemberInfoVO();
                infoVO.setMemberLabel(memberInfo.get().getMemberLabel());
                infoVO.setMemberType(memberInfo.get().getMemberType());
                CurrentMemberVO currentMemberVO = memberInfo.get().getCurrentMemberVO();
                Map<RightsType, List<RelevancyRightsVO>> relevancyRightsMap = Option.of(currentMemberVO.getRelevancyRights())
                        .getOrElse(new HashMap<>());
                infoVO.setRelevancyRights(relevancyRightsMap.get(RightsType.GOODS_DISCOUNT));
                vo.setMemberInfo(infoVO);
            }
        });
    }

    private ActivityShopProductKey generateSkuKey(EsProductEntity esproductEntity){
        ActivityShopProductKey activityShopProductKey = new ActivityShopProductKey();
        activityShopProductKey.setShopId(esproductEntity.getShopId());
        activityShopProductKey.setProductId(esproductEntity.getProductId());
        activityShopProductKey.setActivityType(OrderType.COMMON);
        activityShopProductKey.setActivityId(0L);
        return activityShopProductKey;
    }

    @Override
    public void updateProductStockAndSales(Map<String, Map<Long, StSvBo>> productIdAndSkuStSv) {
        if (CollUtil.isEmpty(productIdAndSkuStSv)) {
            return;
        }
        //获取所有商品的锁 批量加锁
        List<RLock> locks = productIdAndSkuStSv.keySet().stream()
                .map(id -> redissonClient.getLock(RedisUtil.key(SearchConst.PRODUCT_UPDATE_SKU_LOCK, id))).toList();
        RLock multiLock = redissonClient.getMultiLock(locks.toArray(new RLock[0]));
        multiLock.lock();
        try {
            //查询出所有的商品
            List<EsProductEntity> products = esProductMapper.selectList(
                    EsWrappers.lambdaQuery(
                            EsProductEntity.class
                    ).in(EsProductEntity::getId, productIdAndSkuStSv.keySet())

            );
            if (CollUtil.isEmpty(products)) {
                return;
            }
            //设置商品每个
            products.forEach(
                    product -> {
                        Map<Long, StSvBo> skuStSv = productIdAndSkuStSv.get(product.getId());
                        if (CollUtil.isEmpty(skuStSv)) {
                            return;
                        }
                        Map<Long, Integer> skuIdIndexMap = product.skuIdIndexMap();
                        List<Long> skuIds = product.getSkuIds();
                        List<Long> stocks = product.getStocks();
//                        List<StockType> stockTypes = product.getStockTypes();
                        long sales = 0;
                        for (Long skuId : skuIds) {
                            Integer index = skuIdIndexMap.get(skuId);
                            //如果没有这个skuId 则跳过
                            if (index == null) {
                                continue;
                            }
                            StSvBo stSvBo = skuStSv.get(skuId);
                            //如果没有这个skuId的库存销量 则跳过
                            if (stSvBo == null || stSvBo.invalid()) {
                                continue;
                            }
                            //更新库存
                            stocks.set(index, stocks.get(index) + stSvBo.getStock());
                            //更新销量
                            sales += stSvBo.getSales();
                        }
                        product.setSalesVolume(product.getSalesVolume() + sales);
                    }
            );
            //更新es
            esProductMapper.updateBatchByIds(products);
        } finally {
            multiLock.unlock();
        }
    }

    @Override
    public List<EsProductEntity> suggest(SuggestDTO suggest) {
        String keyword = suggest.getKeyword();
        if (StrUtil.isBlank(keyword)) {
            return Collections.emptyList();
        }
        return esProductMapper.selectList(
                EsWrappers.lambdaQuery(EsProductEntity.class)
                        .select(EsProductEntity::getProductName)
                        .eq(EsProductEntity::getStatus, ProductStatus.SELL_ON)
                        .eq(suggest.getShopId() != null, EsProductEntity::getShopId, suggest.getShopId())
                        .and(
                                wrapper -> wrapper.prefixQuery(EsProductEntity::getProductName, keyword, 1.0F)
                                        .or().matchPhrase(EsProductEntity::getProductName, keyword, 0.9F)
                                        .or().match(EsProductEntity::getProductName, keyword, 0.5F)
                                        .or().match("skuStocks.specs", keyword, 0.2F)
                        )
                        .sortByScore()
                        .size(CommonPool.NUMBER_SEVEN)

        );
    }

    /**
     * 商品类目清空
     *
     * @param nestedCategory 类目信息
     */
    @Override
    public void productClassifyEmpty(NestedCategory nestedCategory) {
        esProductMapper.update(
                null,
                EsWrappers.lambdaUpdate(EsProductEntity.class)
                        .set(EsProductEntity::getPlatformCategoryFirstId, null)
                        .set(EsProductEntity::getPlatformCategorySecondId, null)
                        .set(EsProductEntity::getPlatformCategoryThirdId, null)
                        .eq(nestedCategory.getOne() != null, EsProductEntity::getPlatformCategoryFirstId,
                                nestedCategory.getOne())
                        .eq(nestedCategory.getTwo() != null, EsProductEntity::getPlatformCategorySecondId,
                                nestedCategory.getTwo())
                        .eq(nestedCategory.getThree() != null, EsProductEntity::getPlatformCategoryThirdId,
                                nestedCategory.getThree())
        );
    }

    /**
     * 更新品牌状态
     *
     * @param brandId 品牌id
     * @param status  品牌状态
     */
    @Override
    public void brandStatusUpdate(Long brandId, BrandStatus status) {
        esProductMapper.update(
                null,
                EsWrappers.lambdaUpdate(EsProductEntity.class)
                        .set(EsProductEntity::getBrandStatus, status)
                        .eq(EsProductEntity::getBrandId, brandId)
        );
    }

    /**
     * 删除品牌
     *
     * @param brandId 品牌id
     */
    @Override
    public void brandDelete(Long brandId) {
        LambdaEsQueryWrapper<EsProductEntity> queryWrapper = EsWrappers.lambdaQuery(EsProductEntity.class)
                .eq(EsProductEntity::getBrandId, brandId);
        if (queryWrapper == null) {
            return;
        }
        esProductMapper.update(
                null,
                EsWrappers.lambdaUpdate(EsProductEntity.class)
                        .set(EsProductEntity::getBrandId, null)
                        .set(EsProductEntity::getBrandStatus, null)
                        .eq(EsProductEntity::getBrandId, brandId)
        );
    }

    /**
     * 参数 转 查询条件封装器
     *
     * @param isConsumer 是否是消费端查询
     * @param param      搜索参数
     * @return 查询条件封装器
     */
    private SearchSourceBuilder paramsToSearchBuilder(boolean isConsumer, SearchParam param) {
        //productSearchMapper.searchAfterPage()
        //分类 id
        Long platformCategoryFirstId = param.getPlatformCategoryFirstId();
        Long platformCategorySecondId = param.getPlatformCategorySecondId();
        Long platformCategoryThirdId = param.getPlatformCategoryThirdId();
        Long categoryFirstId = param.getCategoryFirstId();
        Long categorySecondId = param.getCategorySecondId();
        Long categoryThirdId = param.getCategoryThirdId();
        //关键词
        String keyword = param.getKeyword();
        boolean notEmptyKeyword = StrUtil.isNotBlank(keyword);
        Option<Long> userIdOpt = ISecurity.userOpt().map(SecureUser::getId);
        //消费者搜索记录
        if (isConsumer && notEmptyKeyword) {
            searchCompletableTaskExecutor.execute(
                    () -> productSearchHistoryService.saveSearchKeywords(userIdOpt, keyword)
            );
        }
        //排除的商品id
        Set<Long> excludeProductIds = new HashSet<>(sameTimeProductIds(isConsumer, param));
        if (CollUtil.isNotEmpty(param.getExcludeProductIds())) {
            excludeProductIds.addAll(param.getExcludeProductIds());
        }
        boolean notEmptyIds = CollUtil.isNotEmpty(param.getIds());
        LambdaEsQueryWrapper<EsProductEntity> queryWrapper = EsWrappers.lambdaQuery(EsProductEntity.class)
                .eq(EsProductEntity::getStatus, ProductStatus.SELL_ON)
                .and(
                        notEmptyKeyword,
                        wrapper ->
                                wrapper.prefixQuery(EsProductEntity::getProductName, keyword, 0.9F)
                                        .or().like(EsProductEntity::getProductName, keyword, 0.85F)
                                        .or().matchPhrase(EsProductEntity::getProductName, keyword, 0.8F)
                                        .or().match(EsProductEntity::getProductName, keyword, 0.5F)
                                        .or().match(EsProductEntity::getSpecs, keyword, 0.1F)
                )
                .in(notEmptyIds, EsProductEntity::getId, param.getIds())
                .in(CollUtil.isNotEmpty(param.getProductId()), EsProductEntity::getProductId, param.getProductId())
                .and(CollUtil.isNotEmpty(excludeProductIds), wrapper -> wrapper.not().in(EsProductEntity::getProductId, excludeProductIds))
                .ge(param.getMinPrice() != null, EsProductEntity::getSalePrices, param.getMinPrice())
                .le(param.getMaxPrice() != null, EsProductEntity::getSalePrices, param.getMaxPrice())
                .eq(param.getShopId() != null, EsProductEntity::getShopId, param.getShopId())
                //状态
                .in(isConsumer, EsProductEntity::getStatus, "SELL_ON", "SELL_OUT")
                //分类
                .eq(platformCategoryFirstId != null, EsProductEntity::getPlatformCategoryFirstId,
                        platformCategoryFirstId)
                .eq(platformCategorySecondId != null, EsProductEntity::getPlatformCategorySecondId,
                        platformCategorySecondId)
                .eq(platformCategoryThirdId != null, EsProductEntity::getPlatformCategoryThirdId,
                        platformCategoryThirdId)
                .eq(categoryFirstId != null, EsProductEntity::getCategoryFirstId, categoryFirstId)
                .eq(categorySecondId != null, EsProductEntity::getCategorySecondId, categorySecondId)
                .eq(categoryThirdId != null, EsProductEntity::getCategoryThirdId, categoryThirdId)
                //查询品牌 品牌 id 不为空 则关联 查询正常状态的品牌的商品
                .and(CollUtil.isNotEmpty(param.getBrandId()),
                        wp -> wp.eq(EsProductEntity::getBrandStatus, BrandStatus.SELL_ON)
                                .in(EsProductEntity::getBrandId, param.getBrandId())
                )
                //在searchConsignmentProduct为false 进行代销商品的过滤
                .and(!param.getSearchConsignmentProduct(), wrapper -> wrapper.not().eq(EsProductEntity::getSellType, SellType.CONSIGNMENT))
                //不限库存 或 库存大于0
                .and(
                        param.getSearchTotalStockGtZero(),
                        wp -> wp.eq(EsProductEntity::getStockTypes, StockType.UNLIMITED)
                                .or()
                                .gt(EsProductEntity::getStocks, CommonPool.NUMBER_ZERO)
                )
                //配送方式
                .eq(param.getDistributionMode() != null, EsProductEntity::getDistributionMode, param.getDistributionMode());
        //如果检索的商品是分销商品
        if (param.getDistributeProduct()) {
            //消费者端代表检索所有分销商品
            queryWrapper.eq(isConsumer, EsProductEntity::getIsDistributed, Boolean.TRUE)
                    //否则表示 添加、编辑 分销商品自动把已经参加分销的分销商品过滤掉
                    .eq(!isConsumer, EsProductEntity::getIsDistributed, Boolean.FALSE);
        }

        //排序
        List<OrderByParam> orderParams = param.getOrderByParams();
        boolean emptySort = CollUtil.isEmpty(orderParams);
        //排序参数为空
        if (emptySort) {
            SearchSourceBuilder sourceBuilder;
            //排序参数为空 且 检索的商品是根据Id列表检索的 则按照 Id 列表顺序排序
            if (notEmptyIds) {
                sourceBuilder = esProductMapper.getSearchSourceBuilder(queryWrapper);
                sourceBuilder.sort(
                        SortBuilders.scriptSort(
                                new Script(
                                        ScriptType.INLINE, "painless",
                                        "params.order.indexOf(doc['_id'].value)", Map.of("order", param.getIds())
                                ),
                                ScriptSortBuilder.ScriptSortType.NUMBER
                        )
                );
            } else {
                if (Objects.nonNull(param.getKeyword())) {
                    //存在关键字 则根据得分进行排序
                    sourceBuilder = esProductMapper.getSearchSourceBuilder(queryWrapper.sortByScore());
                }else {
                    sourceBuilder = esProductMapper.getSearchSourceBuilder(queryWrapper.sort(SortBuilders.fieldSort("productId")));
                }
            }
            return sourceBuilder;
        }
        //排序参数不为空则完全按照排序参数排序
        // 排序字段 salePrices 特殊处理 按照最小值排序
        final String fieldSalePrices = "salePrices";
        //是否包含 排序字段 salePrices
        Final<Boolean> orderBySalePrices = new Final<>(false);
        // 排序字段 salePrices 是否为升序
        Final<Boolean> salePricesAsc = new Final<>(false);
        //过滤 排序字段 salePrices,特殊处理
        orderParams = CollUtil.emptyIfNull(orderParams)
                .stream()
                .filter(
                        order -> {
                            if (fieldSalePrices.equals(order.getOrder())) {
                                orderBySalePrices.set(true);
                                salePricesAsc.set(SortOrder.ASC.name().equalsIgnoreCase(order.getSort()));
                                return false;
                            }
                            return true;
                        }
                ).toList();
        //渲染为SearchSourceBuilder
        SearchSourceBuilder sourceBuilder = esProductMapper.getSearchSourceBuilder(
                queryWrapper.orderBy(
                        CollUtil.isNotEmpty(orderParams),
                        orderParams
                )
        );
        // salePrices排序不为空 按照最小值排序
        if (orderBySalePrices.get()) {
            sourceBuilder.sort(
                    SortBuilders.fieldSort(fieldSalePrices)
                            .order(salePricesAsc.get() ? SortOrder.ASC : SortOrder.DESC)
                            .sortMode(SortMode.MIN)
            );
        }
        return sourceBuilder;
    }


    /**
     * 活动时间有交集的商品id 集合
     *
     * @param param 搜索参数
     * @return 商品id集合
     */
    public Set<Long> sameTimeProductIds(boolean isConsumer, SearchParam param) {
        SearchParam.ForActivity activity;
        if (isConsumer || param.getShopId() == null || (activity = param.getActivity()) == null) {
            if (Objects.nonNull(param.getExcludeProductId())) {
                return Set.of(param.getExcludeProductId());
            }
            return Set.of();
        }
        OrderType activityType = activity.getActivityType();
        LocalDateTime startTime = activity.getStartTime();
        LocalDateTime endTime = activity.getEndTime();
        return esProductActivityMapper.selectList(
                        EsWrappers.lambdaQuery(EsProductActivityEntity.class)
                                .select(EsProductActivityEntity::getProductId)
                                .eq(EsProductActivityEntity::getShopId, param.getShopId())
                                .eq(activityType != null, EsProductActivityEntity::getActivityType, activityType)
                                .lt(EsProductActivityEntity::getStartTime, FastJson2.DATETIME_FORMATTER.format(endTime))
                                .gt(EsProductActivityEntity::getEndTime, FastJson2.DATETIME_FORMATTER.format(startTime))
                ).stream()
                .map(EsProductActivityEntity::getProductId)
                .collect(Collectors.toSet());
    }

    /**
     * 获取不同店铺商品销量前6
     *
     * @param shopIds 商家id集合
     */
    @Override
    public List<ShopProductSalesTopVO> getShopProductSalesTop(List<Long> shopIds) {

        LambdaEsQueryWrapper<EsProductEntity> wrapper = EsWrappers.lambdaQuery(EsProductEntity.class)
                .in(EsProductEntity::getShopId, shopIds)
                .eq(EsProductEntity::getStatus, ProductStatus.SELL_ON);
        SearchSourceBuilder searchSourceBuilder = WrapperProcessor.buildSearchSourceBuilder(wrapper, EsProductEntity.class);
        String indexName = EntityInfoHelper.getEntityInfo(EsProductEntity.class).getIndexName();
        SearchRequest searchRequest = new SearchRequest(indexName);
//        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        // 构建查询条件
        // 构建查询条件
//        searchSourceBuilder.query(
//                QueryBuilders.boolQuery()
//                        .must(QueryBuilders.termsQuery("shopId", shopIds))
//                        .must(QueryBuilders.termsQuery("status", ProductStatus.SELL_ON.name()))
//        );

//        searchSourceBuilder.query()
        // 构建聚合条件
        TermsAggregationBuilder shopAggregation = AggregationBuilders.terms("shops").field("shopId")
                .size(shopIds.size());
        TopHitsAggregationBuilder topProductsAggregation = AggregationBuilders.topHits(
                        "top_products")
                .size(CommonPool.NUMBER_SIX)
                .sort("salesVolume", org.elasticsearch.search.sort.SortOrder.DESC);

        shopAggregation.subAggregation(topProductsAggregation);
        searchSourceBuilder.aggregation(shopAggregation);

        // 设置查询请求的源
        searchRequest.source(searchSourceBuilder);
        SearchResponse search;
        try {
            search = esProductMapper.search(searchRequest, RequestOptions.DEFAULT);
        } catch (IOException e) {
            throw new GlobalException("数据查询发生异常");
        }
        List<EsProductEntity> esProductEntities = Lists.newArrayList();
        // 获取 "shops" 聚合
        ParsedLongTerms shopsAggregation = search.getAggregations().get("shops");
        // 获取每个桶（店铺）的结果
        for (Terms.Bucket shopBucket : shopsAggregation.getBuckets()) {
            // 获取 "top_products" 聚合
            ParsedTopHits parsedTopHits = shopBucket.getAggregations().get("top_products");
            // 获取每个桶中的文档
            for (SearchHit hit : parsedTopHits.getHits().getHits()) {
                esProductEntities.add(
                        JSON.parseObject(hit.getSourceAsString(), EsProductEntity.class));
            }
        }

        if (CollUtil.isEmpty(esProductEntities)) {
            return Collections.emptyList();
        }
        renderData(esProductEntities);
        return esProductEntities.stream()
                .collect(Collectors.groupingBy(EsProductEntity::getShopId))
                .entrySet()
                .stream()
                .map(entry -> new ShopProductSalesTopVO()
                        .setShopId(entry.getKey())
                        .setProductSaleVolumes(
                                entry.getValue()
                                        .stream()
                                        .map(product -> new ProductSaleVolumeVO()
                                                .setProductId(product.getProductId())
                                                .setProductName(product.getProductName())
                                                .setProductPrice(product.getSalePrices().get(CommonPool.NUMBER_ZERO))
                                                .setPic(product.getPic())
                                                .setSalesVolume(product.getSalesVolume())
                                        ).toList()
                        )

                ).toList();
    }

    /**
     * 多规格价格修改
     *
     * @param priceUpdate 商品价格更新DTO
     */
    @Override
    public void updateSkuPrice(ProductPriceUpdateDTO priceUpdate) {
        Set<Long> shopIds = priceUpdate.getShopIds();
        if (CollUtil.isEmpty(shopIds)) {
            return;
        }
        //只查询出第一个店铺 id 的商品 
        //因为是同一个商品  sku 价格 规格 库存 都一样
        //处理完数据批量更新即可
        Long firstShopId = shopIds.iterator().next();
        EsProductEntity firstProduct = esProductMapper.selectOne(
                EsWrappers.lambdaQuery(EsProductEntity.class)
                        .select(
                                EsProductEntity::getSkuIds,
                                EsProductEntity::getPrices,
                                EsProductEntity::getSalePrices
                        )
                        .eq(EsProductEntity::getProductId, priceUpdate.getProductId())
                        .eq(EsProductEntity::getShopId, firstShopId)

        );
        if (firstProduct == null) {
            return;
        }
        Map<Long, ProductPriceUpdateDTO.SkuPriceDTO> skuPriceMap = priceUpdate.getSkuPriceMap();
        List<Long> skuIds = firstProduct.getSkuIds();
        List<Long> prices = firstProduct.getPrices();
        List<Long> salePrices = firstProduct.getSalePrices();

        for (int index = 0; index < skuIds.size(); index++) {
            Long skuId = skuIds.get(index);
            ProductPriceUpdateDTO.SkuPriceDTO skuPriceDTO = skuPriceMap.get(skuId);
            if (skuPriceDTO != null) {
                prices.set(index, skuPriceDTO.getPrice());
                salePrices.set(index, skuPriceDTO.getSalePrice());
            }
        }
        //批量加锁 防止数据不一致
        Long productId = priceUpdate.getProductId();
        RLock lock = redissonClient.getMultiLock(
                shopIds.stream().map(shopId -> redissonClient.getLock(RedisUtil.key(SearchConst.PRODUCT_UPDATE_SKU_LOCK, RedisUtil.key(shopId, productId)))).toArray(RLock[]::new)
        );
        lock.lock();
        try {
            esProductMapper.update(
                    null,
                    EsWrappers.lambdaUpdate(EsProductEntity.class)
                            .set(EsProductEntity::getPrices, prices)
                            .set(EsProductEntity::getSalePrices, salePrices)
                            .eq(EsProductEntity::getProductId, priceUpdate.getProductId())
                            .in(EsProductEntity::getShopId, shopIds)
            );
        } finally {
            lock.unlock();
        }
    }

    /**
     * 店铺信息变更，更新商品信息中的店铺名称和店铺类型
     *
     * @param shopInfo 店铺信息
     */
    @Override
    public void updateProductShopInfo(ShopInfoVO shopInfo) {
        Long count = esProductMapper.selectCount(
                EsWrappers.lambdaQuery(EsProductEntity.class).eq(EsProductEntity::getShopId, shopInfo.getId()));
        if (count == null || count <= 0) {
            return;
        }
        List<EsProductEntity> products = esProductMapper.selectList(
                EsWrappers.lambdaQuery(
                        EsProductEntity.class
                ).in(EsProductEntity::getShopId, shopInfo.getId())
        );
        //设置商品的店铺名称和店铺类型
        products.forEach(
                product -> {
                    product.setShopName(shopInfo.getName());
                    if (null != shopInfo.getShopType()) {
                        product.setShopType(shopInfo.getShopType());
                    }
                }
        );
        //更新es
        esProductMapper.updateBatchByIds(products);

    }

    /**
     * 商品标签删除
     *
     * @param productList 商品list
     */
    @Override
    public void productLabelDelete(List<Product> productList) {
        if (CollUtil.isEmpty(productList)) {
            return;
        }
        Set<Long> productIds = productList.stream().map(Product::getId).collect(Collectors.toSet());
        esProductMapper.update(null,
                EsWrappers.lambdaUpdate(EsProductEntity.class)
                        .in(EsProductEntity::getProductId, productIds)
                        .set(EsProductEntity::getProductLabel, null));
    }

    @Override
    public void productNameUpdate(ProductNameUpdateDTO productName) {
        ShopProductKey productKey = productName.getKey();
        esProductMapper.update(
                null,
                EsWrappers.lambdaUpdate(EsProductEntity.class)
                        .eq(EsProductEntity::getShopId, productKey.getShopId())
                        .eq(EsProductEntity::getProductId, productKey.getProductId())
                        .set(EsProductEntity::getProductName, productName.getName())
        );
    }

    @Override
    public void updateProductLabel(Product product) {
        esProductMapper.update(null,
                EsWrappers.lambdaUpdate(EsProductEntity.class)
                        .eq(EsProductEntity::getProductId, product.getId())
                        .set(EsProductEntity::getProductLabel, product.getProductLabel()));

    }


    /**
     * 处理数据
     *
     * @param searches List<EsProductEntity>
     */
    private void renderData(List<EsProductEntity> searches) {
        searches.stream()
                //计算代销商品金额
                .filter(search -> search.getSellType() == SellType.CONSIGNMENT)
                .forEach(search -> {
                    ConsignmentPriceSettingDTO consignmentPriceUpdate = new ConsignmentPriceSettingDTO()
                            .setType(search.getPricingType())
                            .setScribe(search.getScribe())
                            .setSale(search.getSale());
                    // 修改代销商品金额
                    List<Long> newSalePrices = search.getSalePrices()
                            .stream()
                            .map(salePrice -> consignmentPriceUpdate.singlePrice(salePrice).getSalePrice())
                            .collect(Collectors.toList());
                    search.setSalePrices(newSalePrices);
                });
    }

}
