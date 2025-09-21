package com.medusa.gruul.addon.supplier.modules.goods.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.CollectionUtil;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.medusa.gruul.addon.supplier.addon.SupplierAddonSupporter;
import com.medusa.gruul.addon.supplier.model.enums.SupplierError;
import com.medusa.gruul.addon.supplier.model.param.SupplierProductParam;
import com.medusa.gruul.addon.supplier.model.param.SupplierProductParamNoPage;
import com.medusa.gruul.addon.supplier.model.param.SupplyListParam;
import com.medusa.gruul.addon.supplier.model.vo.StorageSkuVO;
import com.medusa.gruul.addon.supplier.model.vo.SupplierProductListVO;
import com.medusa.gruul.addon.supplier.modules.goods.service.QuerySupplierGoodsService;
import com.medusa.gruul.addon.supplier.mp.entity.SupplierGoods;
import com.medusa.gruul.addon.supplier.mp.service.ISupplierGoodsService;
import com.medusa.gruul.common.model.base.ActivityShopProductKey;
import com.medusa.gruul.common.model.base.ShopProductSkuKey;
import com.medusa.gruul.common.model.constant.CommonPool;
import com.medusa.gruul.common.model.enums.OrderType;
import com.medusa.gruul.common.mp.model.BaseEntity;
import com.medusa.gruul.common.security.resource.helper.ISecurity;
import com.medusa.gruul.goods.api.entity.Product;
import com.medusa.gruul.goods.api.model.CategoryLevel;
import com.medusa.gruul.goods.api.model.enums.ProductAuditStatus;
import com.medusa.gruul.goods.api.model.param.AuditProductParam;
import com.medusa.gruul.goods.api.model.vo.AuditProductVO;
import com.medusa.gruul.goods.api.model.vo.CategoryLevelName;
import com.medusa.gruul.goods.api.model.vo.LookProductVO;
import com.medusa.gruul.goods.api.model.vo.ProductVO;
import com.medusa.gruul.shop.api.model.vo.ShopInfoVO;
import com.medusa.gruul.shop.api.rpc.ShopRpcService;
import com.medusa.gruul.storage.api.entity.StorageSku;
import com.medusa.gruul.storage.api.enums.StockChangeType;
import com.medusa.gruul.storage.api.rpc.StorageRpcService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 查询供应商商品ServiceImpl
 *
 * @author xiaoq
 * @Description QuerySupplierGoodsServiceImpl.java
 * @date 2023-07-17 13:29
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class QuerySupplierGoodsServiceImpl implements QuerySupplierGoodsService {
    private final ISupplierGoodsService supplierGoodsService;

    private final StorageRpcService storageRpcService;


    private final SupplierAddonSupporter supplierAddonSupporter;
    private final ShopRpcService shopRpcService;

    @Override
    public IPage<SupplierProductListVO> getSupplierProductList(SupplierProductParam supplierProductParam) {
        IPage<SupplierProductListVO> page = supplierGoodsService.getSupplierProductList(supplierProductParam);
        List<SupplierProductListVO> records = page.getRecords();
        if (CollUtil.isEmpty(records)) {
            return page;
        }
        for (SupplierProductListVO bean : records) {
            //价格
            if (bean.getSalePrices().size() == CommonPool.NUMBER_ONE) {
                bean.setSalePrice(bean.getSalePrices().get(0));
            } else {
                //最高 最低价格
                bean.setMaxPrice(bean.getSalePrices().get(bean.getSalePrices().size() - 1));
                bean.setMinPrice(bean.getSalePrices().get(0));
            }
        }
        //库存查询
        stockInquiry(records, null);
        return page;
    }

    @Override
    public ProductVO getSupplierProductById(Long shopId, Long id) {
        ProductVO productVO = new ProductVO();
        SupplierGoods supplierGoods = supplierGoodsService.lambdaQuery().eq(SupplierGoods::getShopId, shopId).eq(BaseEntity::getId, id).one();
        SupplierError.PRODUCT_NOT_EXIST.trueThrow(supplierGoods == null);
        assert supplierGoods != null;
        BeanUtil.copyProperties(supplierGoods.getProduct(), productVO);

        productVO.setId(supplierGoods.getId());
        return productVO;
    }

    @Override
    public Map<Long,SupplierGoods> getSupplierProductListByIds(Set<Long> ids) {
        List<SupplierGoods> supplierGoods = supplierGoodsService.lambdaQuery()
                .in(BaseEntity::getId, ids).list();
        if (CollectionUtil.isEmpty(supplierGoods)) {
            return Maps.newHashMap();
        }
        Map<Long, SupplierGoods> supplierGoodsMap = supplierGoods.stream()
                .collect(Collectors.toMap(SupplierGoods::getId, Function.identity()));
        return supplierGoodsMap;

    }


    /**
     * 店铺获取货源信息 byPlatformCategory
     *
     * @param supplyListParam 检索param
     * @return IPage<SupplierProductListVO>
     */
    @Override
    public IPage<SupplierProductListVO> getSupplyListByPlatformCategory(SupplyListParam supplyListParam) {
        // 店铺id
        Long shopId = ISecurity.userMust().getShopId();
        // 获取店铺签约的平台二级类目
        Set<Long> signingCategoryIds = supplierAddonSupporter.getSigningCategoryIds(shopId);

        // 店铺签约类目为null得时候
        SupplierError.QUERY_CATEGORY_UNSIGNED.trueThrow(((CollUtil.isEmpty(signingCategoryIds))));


        Long platformCategoryParentId = supplyListParam.getPlatformCategoryParentId();


        if (platformCategoryParentId != null && !signingCategoryIds.contains(platformCategoryParentId)) {
            return new Page<>();
        }

        // 将platformCategoryParentId添加到签约类目集合中，但前提是它不为null
        if (platformCategoryParentId != null) {
            signingCategoryIds.add(platformCategoryParentId);
        }

        supplyListParam.setPlatformCategoryParentIds(signingCategoryIds);
        supplyListParam.setShopId(shopId);
        IPage<SupplierProductListVO> supplierProductList = supplierGoodsService.getSupplyListByPlatformCategory(supplyListParam);
        List<SupplierProductListVO> records = supplierProductList.getRecords();
        if (CollUtil.isEmpty(records)) {
            return supplierProductList;
        }
        Set<Long> shopIds = records.stream().map(SupplierProductListVO::getShopId).filter(Objects::nonNull)
                .collect(Collectors.toSet());
        if (CollectionUtil.isNotEmpty(shopIds)) {
            List<ShopInfoVO> shopInfoByShopIdList = shopRpcService.getShopInfoByShopIdList(shopIds);
            Map<Long, ShopInfoVO> shopInfoVOMap = shopInfoByShopIdList.stream()
                    .collect(Collectors.toMap(ShopInfoVO::getId, Function.identity()));
            records.forEach(bean -> {
                if (shopInfoVOMap.containsKey(bean.getShopId())) {
                    bean.setSupplierContractNumber(shopInfoVOMap.get(bean.getShopId()).getContractNumber());
                }
            });
        }


        //库存渲染
        stockInquiry(records, shopId);

        return supplierProductList;
    }

    /**
     * 获取商品库存基础信息(包含以删除信息)
     *
     * @param supplierProductParam 检索条件
     * @return IPage<SupplierProductListVO> 包含以删除商品
     */
    @Override
    public IPage<SupplierProductListVO> getProductStockBaseList(SupplierProductParam supplierProductParam) {
        filledExcludeProductIds(supplierProductParam);
        IPage<SupplierProductListVO> productStockBaseList = supplierGoodsService.getProductStockBaseList(supplierProductParam);
        List<SupplierProductListVO> records = productStockBaseList.getRecords();
        if (CollUtil.isEmpty(records)) {
            return productStockBaseList;
        }
        stockInquiry(records, null);
        return productStockBaseList;
    }

    /**
     * 填充要排除的商品ids
     *
     * @param supplierProductParam
     */
    private void filledExcludeProductIds(SupplierProductParam supplierProductParam) {
        List<Long> excludeProductIds = Lists.newArrayList();
        if (Objects.nonNull(supplierProductParam.getShopId())) {
            ActivityShopProductKey activityKey = new ActivityShopProductKey();
            activityKey.setShopId(supplierProductParam.getShopId()).setActivityType(OrderType.COMMON).setActivityId((long) CommonPool.NUMBER_ZERO);
            if (Lists.newArrayList(StockChangeType.ALLOCATION_OUTBOUND, StockChangeType.OTHER_OUTBOUND)
                    .contains(supplierProductParam.getStockChangeType())) {
                //调拨出库、其它出库 过滤掉库存为0(有限库存)的商品 过滤掉无限库存的商品
                excludeProductIds = storageRpcService.getShopEmptyStockProIds(activityKey, Boolean.TRUE, Boolean.TRUE);

            }
            if (Lists.newArrayList(StockChangeType.ALLOCATION_INBOUND,
                            StockChangeType.OTHER_INBOUND)
                    .contains(supplierProductParam.getStockChangeType())) {
                //调拨入库 其它入库  将【无限库存】的商品从列表中剔除掉
                excludeProductIds = storageRpcService.getShopEmptyStockProIds(activityKey, Boolean.FALSE, Boolean.TRUE);
            }
            //新增盘点
            if (supplierProductParam.getAddInventory()) {
                //新增盘点 过滤掉无限库存的商品
                excludeProductIds = storageRpcService.getShopEmptyStockProIds(activityKey, Boolean.FALSE, Boolean.TRUE);
            }
        }
        log.info("需要排除的商品ids : {}", excludeProductIds);
        supplierProductParam.setProductIdList(excludeProductIds.stream().map(String::valueOf).toArray(String[]::new));
    }


    @Override
    public LookProductVO getLookProductInfo(Long id, Long shopId) {
        LookProductVO lookProduct = new LookProductVO();
        SupplierGoods supplierGoods = supplierGoodsService.lambdaQuery()
                .eq(SupplierGoods::getShopId, shopId)
                .eq(BaseEntity::getId, id).one();
        SupplierError.PRODUCT_NOT_EXIST.trueThrow(supplierGoods == null);
        Product product = supplierGoods.getProduct();

        CategoryLevel platformCategory = product.getExtra().getPlatformCategory();
        CategoryLevelName platformCategoryLevelName = supplierAddonSupporter.getPlatformCategoryLevelName(platformCategory);
        lookProduct
                .setName(product.getName())
                .setDetail(product.getDetail())
                .setId(product.getId())
                .setSellType(product.getSellType())
                .setPlatformCategoryName(platformCategoryLevelName)
                .setCollectionUrl(product.getCollectionUrl());
        ActivityShopProductKey activityKey = (ActivityShopProductKey) new ActivityShopProductKey().setProductId(supplierGoods.getId()).setShopId(shopId).setActivityType(OrderType.COMMON).setActivityId(0L);
        Map<ActivityShopProductKey, List<StorageSku>> shopProductKeyListMap = storageRpcService.productSkuStockBatch(Collections.singleton(activityKey));
        lookProduct.setStorageSkus(shopProductKeyListMap.get(activityKey));
        return lookProduct;
    }

    /**
     * 供应商审核商品列表
     *
     * @param auditProductParam 审核商品列表查询param
     * @return IPage<AuditProductVO>
     */
    @Override
    public IPage<AuditProductVO> getSupplierAuditProductList(AuditProductParam auditProductParam) {
        IPage<AuditProductVO> supplierAuditProduct = supplierGoodsService.getSupplierAuditProduct(auditProductParam);
        List<AuditProductVO> records = supplierAuditProduct.getRecords();
        if (CollUtil.isEmpty(records)) {
            return supplierAuditProduct;
        }
        records.forEach(bean -> {
            switch (bean.getStatus()) {
                case UNDER_REVIEW -> bean.setAuditStatus(ProductAuditStatus.UNDER_REVIEW);
                case REFUSE -> bean.setAuditStatus(ProductAuditStatus.REFUSE);
                default -> bean.setAuditStatus(ProductAuditStatus.ALREADY_PASSED);
            }

        });
        return supplierAuditProduct;
    }

    @Override
    public Integer illegalCount(SupplierProductParamNoPage supplierProductParam) {
        return supplierGoodsService.illegalCount(supplierProductParam);

    }


    /**
     * 库存查询
     *
     * @param records     供应商商品信息
     * @param extraShopId 获取货源的店铺id
     */
    private void stockInquiry(List<SupplierProductListVO> records, Long extraShopId) {
        Map<ActivityShopProductKey, SupplierProductListVO> shopProductKeyMap = new HashMap<>(records.size());
        boolean notEmptExtraShopId = extraShopId != null;
        records.forEach(
                record -> {
                    ActivityShopProductKey supplierKey = new ActivityShopProductKey();
                    supplierKey.setProductId(record.getId()).setShopId(record.getShopId()).setActivityType(OrderType.COMMON).setActivityId(0L);
                    shopProductKeyMap.put(supplierKey, record);
                    if (notEmptExtraShopId) {
                        ActivityShopProductKey shopKey = new ActivityShopProductKey();
                        shopKey.setProductId(record.getId()).setShopId(extraShopId).setActivityType(OrderType.COMMON).setActivityId(0L);
                        shopProductKeyMap.put(shopKey, record);
                    }
                }
        );
        Map<ActivityShopProductKey, List<StorageSku>> shopProductKeyListMap = storageRpcService.productSkuStockBatch(shopProductKeyMap.keySet());
        Map<ShopProductSkuKey, StorageSkuVO> skuVoMap = this.skuVOMap(shopProductKeyListMap.values());
        shopProductKeyMap
                .entrySet()
                .stream()
                .filter(entry -> !entry.getKey().getShopId().equals(extraShopId))
                .forEach(
                        entry -> {
                            List<StorageSku> storageSkus = shopProductKeyListMap.get(entry.getKey());
                            if (CollUtil.isEmpty(storageSkus)) {
                                return;
                            }
                            Set<ShopProductSkuKey> skuKeys = storageSkus.stream()
                                    .map(sku -> new ShopProductSkuKey().setShopId(sku.getShopId()).setProductId(sku.getProductId()).setSkuId(sku.getId()))
                                    .collect(Collectors.toSet());
                            entry.getValue()
                                    .setStorageSkus(
                                            skuKeys.stream()
                                                    .map(key -> {
                                                        StorageSkuVO storageSkuVO = skuVoMap.get(key);
                                                        if (notEmptExtraShopId) {
                                                            storageSkuVO.setShopOwnProductStockNum(
                                                                    skuVoMap.getOrDefault(key.setShopId(extraShopId), new StorageSkuVO().setStock(0L)).getStock()
                                                            );
                                                        }
                                                        return storageSkuVO;
                                                    }).toList()
                                    );
                        });
    }

    public Map<ShopProductSkuKey, StorageSkuVO> skuVOMap(Collection<List<StorageSku>> skus) {
        return skus.stream()
                .flatMap(List::stream)
                .map(StorageSkuVO::fromStorageSku)
                .collect(
                        Collectors.toMap(
                                skuVo -> new ShopProductSkuKey().setShopId(skuVo.getShopId()).setProductId(skuVo.getProductId()).setSkuId(skuVo.getId()),
                                skuVo -> skuVo
                        )
                );
    }
}
