package com.medusa.gruul.addon.matching.treasure.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.IdUtil;
import com.alibaba.fastjson2.JSON;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.medusa.gruul.addon.matching.treasure.constant.MatchingTreasureConstant;
import com.medusa.gruul.addon.matching.treasure.model.dto.*;
import com.medusa.gruul.addon.matching.treasure.model.enums.ProductAttributes;
import com.medusa.gruul.addon.matching.treasure.model.enums.SetMealStatus;
import com.medusa.gruul.addon.matching.treasure.model.vo.SetMealProductVO;
import com.medusa.gruul.addon.matching.treasure.model.vo.SetMealVO;
import com.medusa.gruul.addon.matching.treasure.mp.entity.SetMeal;
import com.medusa.gruul.addon.matching.treasure.mp.entity.SetMealProduct;
import com.medusa.gruul.addon.matching.treasure.mp.service.ISetMealPaymentInfoService;
import com.medusa.gruul.addon.matching.treasure.mp.service.ISetMealProductService;
import com.medusa.gruul.addon.matching.treasure.mp.service.ISetMealService;
import com.medusa.gruul.addon.matching.treasure.service.SetMealManageService;
import com.medusa.gruul.common.model.base.ActivityShopProductSkuKey;
import com.medusa.gruul.common.model.base.ShopProductKey;
import com.medusa.gruul.common.model.constant.CommonPool;
import com.medusa.gruul.common.model.enums.DistributionMode;
import com.medusa.gruul.common.model.enums.OrderType;
import com.medusa.gruul.common.model.enums.SellType;
import com.medusa.gruul.common.model.resp.SystemCode;
import com.medusa.gruul.common.module.app.shop.ShopMode;
import com.medusa.gruul.common.redis.util.RedisUtil;
import com.medusa.gruul.common.security.model.enums.Roles;
import com.medusa.gruul.common.security.resource.helper.ISecurity;
import com.medusa.gruul.common.system.model.ISystem;
import com.medusa.gruul.common.system.model.model.ClientType;
import com.medusa.gruul.common.xxl.job.CronHelper;
import com.medusa.gruul.common.xxl.job.XxlJobProperties;
import com.medusa.gruul.common.xxl.job.model.XxlJobInfo;
import com.medusa.gruul.common.xxl.job.service.JobService;
import com.medusa.gruul.global.model.exception.GlobalException;
import com.medusa.gruul.goods.api.entity.Product;
import com.medusa.gruul.goods.api.rpc.GoodsRpcService;
import com.medusa.gruul.shop.api.model.vo.ShopInfoVO;
import com.medusa.gruul.shop.api.rpc.ShopRpcService;
import com.medusa.gruul.storage.api.dto.activity.ActivityCloseKey;
import com.medusa.gruul.storage.api.dto.activity.ActivityCreateDTO;
import com.medusa.gruul.storage.api.dto.activity.ActivitySkuDTO;
import com.medusa.gruul.storage.api.entity.StorageSku;
import com.medusa.gruul.storage.api.rpc.StorageActivityRpcService;
import com.medusa.gruul.storage.api.rpc.StorageRpcService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
public class SetMealManageServiceImpl implements SetMealManageService {


    private final ISetMealService setMealService;
    private final ISetMealProductService setMealProductService;
    private final StorageActivityRpcService storageActivityRpcService;
    private final StorageRpcService storageRpcService;
    private final JobService jobService;
    private final XxlJobProperties xxlJobProperties;
    private final RedisTemplate<String, Object> redisTemplate = RedisUtil.getRedisTemplate();

    private final ShopRpcService shopRpcService;
    private final GoodsRpcService goodsRpcService;

    private final ISetMealPaymentInfoService setMealPaymentInfoService;

    /**
     * 新增/编辑套餐
     *
     * @param shopId          店铺id
     * @param setMealActivity 套餐活动
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void addSetMeal(Long shopId, SetMealDTO setMealActivity) {
        long setMealId = IdUtil.getSnowflakeNextId();
        ShopInfoVO shopInfo = shopRpcService.getShopInfoByShopId(shopId);
        ShopMode shopMode = shopInfo.getShopMode();
        if (shopMode == ShopMode.O2O) {
            if (setMealActivity.getDistributionMode() == DistributionMode.EXPRESS
                    || setMealActivity.getDistributionMode() == DistributionMode.VIRTUAL) {
                throw new GlobalException("O2O店铺不支持 快递或无需物流配送");
            }
        }
        // 套餐活动新增
        SetMeal setMeal = setMealActivity.newSetMeal(shopId, setMealId);
        boolean save = setMealService.save(setMeal);
        if (!save) {
            throw new GlobalException(SystemCode.DATA_ADD_FAILED_CODE, "套餐活动保存失败");
        }
        // 保存套餐商品
        saveSetProducts(shopId, setMealActivity, setMealId);
        Set<Long> productIds = setMealActivity.getMainProduct().stream().map(SetMealProductDTO::getProductId).collect(Collectors.toSet());
        // 定时任务
        int jobId = jobService.add(
                new XxlJobInfo()
                        .setJobDesc(setMealActivity.getSetMealName())
                        .setAuthor("gruul")
                        .setJobGroup(xxlJobProperties.getExecutor().getId())
                        .setScheduleConf(CronHelper.toCron(setMealActivity.getEndTime()))
                        .setExecutorHandler(MatchingTreasureConstant.SET_MEAL_XXL_JOB_HANDLER)
                        .setExecutorParam(JSON.toJSONString(new SetMealKeyDTO()
                                .setSetMealId(setMealId)
                                .setShopId(shopId)
                                .setProductIds(productIds))
                        )
        );
        try {
            ActivityCreateDTO activityCreate = new ActivityCreateDTO()
                    .setActivityId(setMealId)
                    .setShopId(shopId)
                    .setActivityType(OrderType.PACKAGE)
                    .setStartTime(setMealActivity.getStartTime())
                    .setEndTime(setMealActivity.getEndTime())
                    .setSkus(setMealActivity.getMainProduct()
                            .stream()
                            .map(setMealProduct ->
                                    new ActivitySkuDTO()
                                            .setProductId(setMealProduct.getProductId())
                                            .setSkuId(setMealProduct.getSkuId())
                                            .setStock(setMealProduct.getMatchingStock().intValue())
                                            .setSalePrice(setMealProduct.getMatchingPrice())
                            ).collect(Collectors.toList()));
            // 扣库存
            storageActivityRpcService.activityCreate(activityCreate);
        } catch (Exception ex) {
            jobService.remove(jobId);
            throw ex;
        }
    }

    private void saveSetProducts(Long shopId, SetMealDTO setMealActivity, long setMealId) {
        List<SetMealProduct> setMealProducts = setMealActivity.getMainProduct()
                .stream()
                .map(setMealProduct -> new SetMealProduct()
                        .setSetMealId(setMealId)
                        .setShopId(shopId)
                        .setProductId(setMealProduct.getProductId())
                        .setProductName(setMealProduct.getProductName())
                        .setProductPic(setMealProduct.getProductPic())
                        .setSkuId(setMealProduct.getSkuId())
                        .setStockType(setMealProduct.getStockType())
                        .setSkuName(setMealProduct.getSkuName())
                        .setSkuPrice(setMealProduct.getSkuPrice())
                        .setSkuStock(setMealProduct.getSkuStock())
                        .setProductAttributes(setMealProduct.getProductAttributes())
                        .setMatchingPrice(setMealProduct.getMatchingPrice())
                        .setMatchingStock(setMealProduct.getMatchingStock())
                ).toList();

        boolean status = setMealProductService.saveBatch(setMealProducts);
        if (!status) {
            throw new GlobalException(SystemCode.DATA_ADD_FAILED_CODE, "套餐活动保存失败");
        }
    }


    @Override
    @Transactional(rollbackFor = Exception.class)
    public void editSetMeal(Long shopId, SetMealDTO setMealDTO) {
        Long setMealId = setMealDTO.getSetMealId();
        SetMeal setMeal = setMealDTO.newSetMeal(shopId, setMealId);
        RedisUtil.doubleDeletion(
                () -> {
                    setMealService.updateById(setMeal);
                    setMealProductService.lambdaUpdate()
                            .eq(SetMealProduct::getSetMealId, setMealId)
                            .eq(SetMealProduct::getShopId, shopId)
                            .remove();
                    saveSetProducts(shopId, setMealDTO, setMealId);
                },
                () -> deleteSetMealExistKey(shopId)
        );

    }


    /**
     * 删除套餐活动
     *
     * @param shopId 店铺id
     */
    private void deleteSetMealExistKey(Long shopId) {
        redisTemplate.delete(RedisUtil.key(MatchingTreasureConstant.SET_MEAL_EXIST_KEY, shopId));
    }

    /**
     * 查询套餐活动
     *
     * @param setMealQuery 查询参数
     * @return 分页结果
     */
    @Override
    public IPage<SetMealVO> pageSetMeal(SetMealQueryDTO setMealQuery) {
        if (ISecurity.matcher()
                .anyRole(Roles.ADMIN, Roles.CUSTOM_ADMIN)
                .match()) {
            setMealQuery.setShopId(ISecurity.userMust().getShopId());
        }
        IPage<SetMealVO> setMealVOIPage = setMealService.pageSetMeal(setMealQuery);
        List<SetMealVO> records = setMealVOIPage.getRecords();
        if (CollectionUtil.isNotEmpty(records)) {
            Set<Long> setMealIds = records.stream().map(SetMealVO::getId).collect(Collectors.toSet());
            //查询指定活动参与的商品数
            Map<Long, Integer> setMealProductCountMap = setMealProductService.querySetMealProductCount(setMealIds);
            Map<Long, Integer> setMealOrderCountMap = setMealPaymentInfoService.querySetMealOrderCount(setMealIds);
            for (SetMealVO record : records) {
                record.setProductCount(setMealProductCountMap.get(record.getId()));
                record.setOrderCount(setMealOrderCountMap.getOrDefault(record.getId(), 0));
            }

        }
        return setMealVOIPage;

    }

    /**
     * 查询套餐活动详情
     *
     * @param shopId    店铺id
     * @param setMealId 套餐id
     * @return 套餐详情
     */
    @Override
    public SetMealVO getSetMealDetailById(Long shopId, Long setMealId) {
        SetMeal setMeal = setMealService.lambdaQuery()
                .eq(SetMeal::getId, setMealId)
                .eq(SetMeal::getShopId, shopId)
                .one();
        if (setMeal == null) {
            throw new GlobalException("套餐活动不存在");
        }
        List<SetMealProduct> setMealProducts = setMealProductService.lambdaQuery()
                .eq(SetMealProduct::getSetMealId, setMealId)
                .eq(SetMealProduct::getShopId, shopId)
                .list();
        if (CollUtil.isEmpty(setMealProducts)) {
            throw new GlobalException("套餐商品不存在");
        }
        //查询商品信息
        Set<ShopProductKey> productKeys = setMealProducts.stream()
                .map(product -> new ShopProductKey(product.getShopId(), product.getProductId()))
                .collect(Collectors.toSet());
        Map<ShopProductKey, Product> productMap = goodsRpcService.getProductBatch(productKeys);
        //套餐商品对应的店铺id 代销商品设置为供应商 id
        Map<ShopProductKey, Long> productSupplierIdMap = new HashMap<>(productMap.size());
//        productMap.forEach((key, value) -> productSupplierIdMap.put(key,
//                SellType.CONSIGNMENT == value.getSellType() ? value.getSupplierId() : value.getShopId()));
        productMap.forEach((key, value) -> productSupplierIdMap.put(key, value.getShopId()));
        Set<ActivityShopProductSkuKey> skuKeys = setMealProducts.stream()
                .map(
                        product -> {
                            ActivityShopProductSkuKey skuKey = new ActivityShopProductSkuKey();
                            skuKey.setSkuId(product.getSkuId())
                                    .setProductId(product.getProductId())
                                    .setShopId(product.getShopId())
                                    .setActivityType(OrderType.PACKAGE)
                                    .setActivityId(setMealId);
                            Long supplierId = productSupplierIdMap.get(new ShopProductKey(product.getShopId(), product.getProductId()));
                            if (supplierId != null) {
                                skuKey.setShopId(supplierId);
                            }
                            return skuKey;
                        }
                )
                .collect(Collectors.toSet());
        //批量查询活动商品库存
        Map<ActivityShopProductSkuKey, StorageSku> skuMap = storageRpcService.skuBatch(skuKeys);

        List<SetMealProductVO> mainProduct = new ArrayList<>();
        List<SetMealProductVO> matchingProducts = new ArrayList<>();
        for (SetMealProduct setMealProduct : setMealProducts) {
            SetMealProductVO product = new SetMealProductVO()
                    .setSetMealId(setMealId)
                    .setShopId(shopId)
                    .setProductId(setMealProduct.getProductId())
                    .setProductPic(setMealProduct.getProductPic())
                    .setProductName(setMealProduct.getProductName())
                    .setProductAttributes(setMealProduct.getProductAttributes())
                    .setSkuId(setMealProduct.getSkuId())
                    .setStockType(setMealProduct.getStockType())
                    .setSkuName(setMealProduct.getSkuName())
                    .setSkuStock(setMealProduct.getSkuStock())
                    .setSkuPrice(setMealProduct.getSkuPrice())
                    .setMatchingPrice(setMealProduct.getMatchingPrice())
                    .setMatchingStock(setMealProduct.getMatchingStock());
            //sku Key
            ActivityShopProductSkuKey skuKey = new ActivityShopProductSkuKey();
            skuKey.setSkuId(product.getSkuId())
                    .setProductId(product.getProductId())
                    .setShopId(product.getShopId())
                    .setActivityType(OrderType.PACKAGE)
                    .setActivityId(setMealId);
            //如果是代销商品，设置为供应商id
            Long supplierId = productSupplierIdMap.get(new ShopProductKey(shopId, setMealProduct.getProductId()));
            if (supplierId != null) {
                skuKey.setShopId(supplierId);
            }
            //更新库存
            StorageSku storageSku = skuMap.get(skuKey);
            product.setMatchingStock(storageSku == null ? CommonPool.NUMBER_ZERO : storageSku.getStock());
            if (setMealProduct.getProductAttributes() == ProductAttributes.MATCHING_PRODUCTS) {
                matchingProducts.add(product);
            } else {
                mainProduct.add(product);
            }
        }
        SetMealVO setMealVO = new SetMealVO()
                .setId(setMealId)
                .setShopId(shopId)
                .setShopName(setMeal.getShopName())
                .setSetMealType(setMeal.getSetMealType())
                .setSetMealMainPicture(setMeal.getSetMealMainPicture())
                .setSetMealName(setMeal.getSetMealName())
                .setSetMealDescription(setMeal.getSetMealDescription())
                .setStartTime(setMeal.getStartTime())
                .setEndTime(setMeal.getEndTime())
                .setStackable(setMeal.getStackable())
                .setDistributionMode(setMeal.getDistributionMode())
                .setMainProduct(mainProduct)
                .setMatchingProducts(matchingProducts);
        if (ISystem.clientTypeMust() == ClientType.PLATFORM_CONSOLE) {
            // 增加店铺类型
            ShopInfoVO shopInfoVO = shopRpcService.getShopInfoByShopId(shopId);
            setMealVO.setShopMode(shopInfoVO.getShopMode());
        }
        return setMealVO;


    }

    /**
     * 删除套餐活动
     *
     * @param shopId    店铺id
     * @param setMealId 套餐id
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteSetMeal(Long shopId, Long setMealId) {
        try {
            SetMeal setMeal = setMealService.lambdaQuery()
                    .eq(SetMeal::getId, setMealId)
                    .eq(SetMeal::getShopId, shopId)
                    .one();
            // 活动为空，或已下架
            if (setMeal == null
                    || setMeal.getSetMealStatus() == SetMealStatus.ILLEGAL_SELL_OFF
                    || setMeal.getSetMealStatus() == SetMealStatus.MERCHANT_SELL_OFF) {
                return;
            }
            LocalDateTime now = LocalDateTime.now();
            boolean processing = now.isAfter(setMeal.getStartTime()) && now.isBefore(setMeal.getEndTime());
            // 活动不在进行中
            if (!processing) {
                return;
            }
            // 归还库存
            returnSetMealSku(shopId, setMealId);
        } finally {
            // 删除套餐活动和套餐商品、缓存key
            RedisUtil.doubleDeletion(
                    () -> deleteSetMealWithProduct(shopId, setMealId),
                    RedisUtil.key(MatchingTreasureConstant.SET_MEAL_EXIST_KEY, shopId)
            );

        }

    }


    private void deleteSetMealWithProduct(Long shopId, Long setMealId) {
        setMealService.lambdaUpdate()
                .eq(SetMeal::getId, setMealId)
                .eq(SetMeal::getShopId, shopId)
                .remove();
        setMealProductService.lambdaUpdate()
                .eq(SetMealProduct::getId, setMealId)
                .eq(SetMealProduct::getShopId, shopId)
                .remove();
    }

    /**
     * 批量删除套餐活动
     *
     * @param shopSetMealIds 店铺、套餐id集合
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteBatchSetMeal(List<ShopSetMealIdDTO> shopSetMealIds) {
        Set<Long> shopIds = null;
        Set<Long> setMealIds = null;
        try {
            setMealIds = new HashSet<>();
            Map<Long, Set<Long>> setMealMap = new HashMap<>();
            for (ShopSetMealIdDTO shopSetMealId : shopSetMealIds) {
                Set<Long> set = setMealMap.computeIfAbsent(shopSetMealId.getShopId(), (key) -> new HashSet<>());
                set.add(shopSetMealId.getSetMealId());
                setMealIds.add(shopSetMealId.getSetMealId());
            }
            // shopId,1,[1,2,3]
            // shopId 2,[4,5,6]
            shopIds = setMealMap.keySet();
            List<SetMeal> setMeals = setMealService.lambdaQuery()
                    .in(SetMeal::getId, setMealIds)
                    .in(SetMeal::getShopId, shopIds)
                    .list();
            if (CollectionUtils.isEmpty(setMeals)) {
                return;
            }
            LocalDateTime now = LocalDateTime.now();
            // 进行中的套餐活动
            List<SetMeal> processingSetMeals = setMeals.stream()
                    .filter(setMeal ->
                            setMeal.getSetMealStatus() != SetMealStatus.ILLEGAL_SELL_OFF &&
                                    (setMeal.getStartTime().isAfter(now) && setMeal.getEndTime().isBefore(now)))
                    .toList();
            if (CollectionUtils.isEmpty(processingSetMeals)) {
                return;
            }
            //活动商品处理
            Map<Long, List<SetMealProduct>> setMealProductMap = getMealProductMap(setMealIds, shopIds);
            if (CollUtil.isEmpty(setMealProductMap)) {
                return;
            }
            storageActivityRpcService.activityClose(
                    shopSetMealIds.stream()
                            .map(shopSetMealId -> new ActivityCloseKey()
                                    .setActivityType(OrderType.PACKAGE)
                                    .setShopId(shopSetMealId.getShopId())
                                    .setActivityId(shopSetMealId.getSetMealId())
                            ).collect(Collectors.toSet())
            );

        } catch (Exception e) {
            setMealIds = null;
            throw new GlobalException("批量删除套餐活动失败");
        } finally {
            if (setMealIds != null) {
                Set<Long> finalSetMealIds = setMealIds;
                Set<Long> finalShopIds = shopIds;
                RedisUtil.doubleDeletion(
                        () -> {
                            setMealService.lambdaUpdate()
                                    .in(SetMeal::getId, finalSetMealIds)
                                    .in(SetMeal::getShopId, finalShopIds)
                                    .remove();
                            setMealProductService.lambdaUpdate()
                                    .in(SetMealProduct::getId, finalSetMealIds)
                                    .in(SetMealProduct::getShopId, finalShopIds)
                                    .remove();
                        },
                        () -> finalShopIds.forEach(this::deleteSetMealExistKey)
                );

            }
        }
    }

    private Map<Long, List<SetMealProduct>> getMealProductMap(Set<Long> setMealIds, Set<Long> shopIds) {
        // 套餐商品
        List<SetMealProduct> setMealProducts = setMealProductService.lambdaQuery()
                .select(SetMealProduct::getProductId, SetMealProduct::getShopId)
                .in(SetMealProduct::getSetMealId, setMealIds)
                .in(SetMealProduct::getShopId, shopIds)
                .list();
        return setMealProducts.stream().collect(Collectors.groupingBy(SetMealProduct::getShopId));


    }


    /**
     * 下架套餐活动
     *
     * @param shopId    店铺id
     * @param setMealId 套餐id
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void sellOffSetMeal(Long shopId, Long setMealId, String violationExplain) {
        SetMealStatus setMealStatus;
        SetMeal setMeal = setMealService.lambdaQuery()
                .eq(SetMeal::getId, setMealId)
                .eq(SetMeal::getShopId, shopId)
                .one();
        //是否是平台下架 否则是店铺下架
        boolean isPlatform = ISecurity.anyRole(Roles.SUPER_ADMIN, Roles.SUPER_CUSTOM_ADMIN);
        if (isPlatform) {
            setMealStatus = SetMealStatus.ILLEGAL_SELL_OFF;
            // 设置违规说明
            setMeal.setViolationExplain(violationExplain);
        } else {
            setMealStatus = SetMealStatus.MERCHANT_SELL_OFF;
        }
        // 只有未开始、进行中的套餐可以进行下架操作
        if (setMeal == null
                || (SetMealStatus.NOT_STARTED != setMeal.getSetMealStatus() && SetMealStatus.PROCESSING != setMeal.getSetMealStatus())
                || setMeal.getEndTime().isBefore(LocalDateTime.now())) {
            return;
        }

        RedisUtil.doubleDeletion(
                () -> setMealService.lambdaUpdate()
                        .set(SetMeal::getSetMealStatus, setMealStatus)
                        .set(isPlatform, SetMeal::getViolationExplain, violationExplain)
                        .eq(SetMeal::getId, setMealId)
                        .eq(SetMeal::getShopId, shopId)
                        .update(),
                () -> RedisUtil.key(MatchingTreasureConstant.SET_MEAL_EXIST_KEY, shopId)
        );
        // 活动进行中,归还套餐库存
        returnSetMealSku(shopId, setMealId);

    }

    /**
     * 归还活动库存
     *
     * @param shopId    店铺id
     * @param setMealId 套餐id
     */

    private void returnSetMealSku(Long shopId, Long setMealId) {
        // 套餐商品
        Set<ActivityCloseKey> activityCloseKeys =
                CollUtil.newHashSet(
                        new ActivityCloseKey()
                                .setShopId(shopId)
                                .setActivityId(setMealId)
                                .setActivityType(OrderType.PACKAGE)
                );
        storageActivityRpcService.activityClose(activityCloseKeys);
    }


}
