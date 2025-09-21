package com.medusa.gruul.addon.bargain.service.impl;

import com.alibaba.fastjson2.JSON;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.medusa.gruul.addon.bargain.constant.BargainConstant;
import com.medusa.gruul.addon.bargain.model.BargainKey;
import com.medusa.gruul.addon.bargain.model.dto.BargainDTO;
import com.medusa.gruul.addon.bargain.model.dto.BargainQueryDTO;
import com.medusa.gruul.addon.bargain.model.dto.BargainShopIdDTO;
import com.medusa.gruul.addon.bargain.model.enums.ActivityStatus;
import com.medusa.gruul.addon.bargain.model.enums.BargainErrorEnums;
import com.medusa.gruul.addon.bargain.model.enums.BargainSponsorSkuStatus;
import com.medusa.gruul.addon.bargain.model.enums.BargainStatus;
import com.medusa.gruul.addon.bargain.model.vo.BargainActivityProductVO;
import com.medusa.gruul.addon.bargain.model.vo.BargainDetailVO;
import com.medusa.gruul.addon.bargain.model.vo.BargainInfoVO;
import com.medusa.gruul.addon.bargain.mp.entity.Bargain;
import com.medusa.gruul.addon.bargain.mp.entity.BargainHelpPeople;
import com.medusa.gruul.addon.bargain.mp.entity.BargainOrder;
import com.medusa.gruul.addon.bargain.mp.entity.BargainProduct;
import com.medusa.gruul.addon.bargain.mp.service.IBargainHelpPeopleService;
import com.medusa.gruul.addon.bargain.mp.service.IBargainOrderService;
import com.medusa.gruul.addon.bargain.mp.service.IBargainProductService;
import com.medusa.gruul.addon.bargain.mp.service.IBargainService;
import com.medusa.gruul.addon.bargain.service.BargainManageService;
import com.medusa.gruul.addon.bargain.utils.BargainRedisUtils;
import com.medusa.gruul.addon.bargain.vo.BargainSponsorProductSkuVO;
import com.medusa.gruul.addon.bargain.vo.BargainVo;
import com.medusa.gruul.common.model.base.ActivityShopProductSkuKey;
import com.medusa.gruul.common.model.base.ShopProductKey;
import com.medusa.gruul.common.model.constant.CommonPool;
import com.medusa.gruul.common.model.enums.OrderType;
import com.medusa.gruul.common.model.enums.SellType;
import com.medusa.gruul.common.model.resp.SystemCode;
import com.medusa.gruul.common.redis.util.RedisUtil;
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
import com.medusa.gruul.storage.api.dto.activity.ActivityCloseKey;
import com.medusa.gruul.storage.api.dto.activity.ActivityCreateDTO;
import com.medusa.gruul.storage.api.dto.activity.ActivitySkuDTO;
import com.medusa.gruul.storage.api.entity.StorageSku;
import com.medusa.gruul.storage.api.rpc.StorageActivityRpcService;
import com.medusa.gruul.storage.api.rpc.StorageRpcService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.time.Duration;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author wudi
 */
@Service
@RequiredArgsConstructor
public class BargainManageServiceImpl implements BargainManageService {


    private final IBargainService bargainService;
    private final IBargainProductService bargainProductService;
    private final IBargainOrderService bargainOrderService;
    private final IBargainHelpPeopleService bargainHelpPeopleService;
    private final StorageActivityRpcService storageActivityRpcService;
    private final StorageRpcService storageRpcService;
    private final JobService jobService;
    private final XxlJobProperties xxlJobProperties;
    private final GoodsRpcService goodsRpcService;

    private static void getBargainActivityProductVO(Long shopId, Long activityId, BargainProduct bargainProduct,
                                                    List<BargainActivityProductVO> bargainActivityProducts) {
        BargainActivityProductVO bargainActivityProductVO = new BargainActivityProductVO()
                .setShopId(shopId)
                .setProductId(bargainProduct.getProductId())
                .setProductName(bargainProduct.getProductName())
                .setProductPic(bargainProduct.getProductPic())
                .setActivityId(activityId)
                .setSkuId(bargainProduct.getSkuId())
                .setSkuName(bargainProduct.getSkuName())
                .setStock(bargainProduct.getStock())
                .setFloorPrice(bargainProduct.getFloorPrice())
                .setSkuPrice(bargainProduct.getSkuPrice())
                .setSkuStock(bargainProduct.getSkuStock())
                .setStockType(bargainProduct.getStockType());
        bargainActivityProducts.add(bargainActivityProductVO);
    }

    private static BargainDetailVO getBargainDetailVO(Bargain bargain,
                                                      List<BargainActivityProductVO> bargainActivityProducts) {
        return new BargainDetailVO()
                .setId(bargain.getId())
                .setShopId(bargain.getShopId())
                .setName(bargain.getName())
                .setStartTime(bargain.getStartTime())
                .setEndTime(bargain.getEndTime())
                .setBargainingPeople(bargain.getBargainingPeople())
                .setBargainValidityPeriod(bargain.getBargainValidityPeriod())
                .setIsSelfBargain(bargain.getIsSelfBargain())
//                .setUserType(bargain.getUserType())
//                .setActivityPreheat(bargain.getActivityPreheat())
                .setStackable(bargain.getStackable())
                .setStatus(bargain.getStatus())
                .setHelpCutAmount(bargain.getHelpCutAmount())
                .setBargainActivityProducts(bargainActivityProducts);
    }

    /**
     * 添加砍价活动
     *
     * @param bargainActivity 砍价活动
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void addBargain(BargainDTO bargainActivity) {
        Long shopId = ISecurity.userMust().getShopId();
        bargainActivity.validParam(shopId, bargainService, bargainProductService);
        // 保存砍价活动
        Bargain bargain = bargainActivity.newBargain(shopId);
        boolean save = bargainService.save(bargain);
        if (!save) {
            throw new GlobalException(SystemCode.DATA_ADD_FAILED_CODE, "添加砍价活动失败");
        }
        Long activityId = bargain.getId();
        List<BargainProduct> bargainProducts = bargainActivity.newBargainProduct(shopId, activityId);
        // 保存砍价活动商品
        boolean saveBatch = bargainProductService.saveBatch(bargainProducts);
        if (!saveBatch) {
            throw new GlobalException(SystemCode.DATA_ADD_FAILED_CODE, "添加砍价活动失败");
        }
        // 添加定时任务
        Set<Long> bargainProductIds = bargainProducts.stream().map(BargainProduct::getProductId).collect(Collectors.toSet());
        int jobId = saveJob(bargain, bargainProductIds);
        try {
            // 扣砍价活动库存
            activityCreateSkuStock(bargainActivity, shopId, activityId, bargainProducts);
        } catch (Exception ex) {
            //如果活动绑定失败 删除定时任务
            jobService.remove(jobId);
            throw ex;
        }
        // 删除砍价商品redis
        RedisUtil.delete(RedisUtil.keys(RedisUtil.key(BargainConstant.BARGAIN_PRODUCT_DETAIL_CACHE_KEY, shopId, "*")));
    }

    private void activityCreateSkuStock(BargainDTO bargainActivity, Long shopId, Long activityId,
                                        List<BargainProduct> bargainProducts) {
        storageActivityRpcService.activityCreate(
                new ActivityCreateDTO()
                        .setShopId(shopId)
                        .setActivityId(activityId)
                        .setActivityType(OrderType.BARGAIN)
                        .setSkus(bargainProducts.stream()
                                .map(bargainProduct -> new ActivitySkuDTO()
                                        .setProductId(bargainProduct.getProductId())
                                        .setSkuId(bargainProduct.getSkuId())
                                        .setStock(Math.toIntExact(bargainProduct.getStock()))
                                        .setSalePrice(bargainProduct.getFloorPrice())
                                ).collect(Collectors.toList())
                        ).setStartTime(bargainActivity.getStartTime())
                        .setEndTime(bargainActivity.getEndTime())
        );
    }

    private int saveJob(Bargain bargain, Set<Long> bargainProductIds) {
        return jobService.add(
                new XxlJobInfo()
                        .setJobDesc(bargain.getName())
                        .setAuthor("gruul")
                        .setJobGroup(xxlJobProperties.getExecutor().getId())
                        .setScheduleConf(CronHelper.toCron(bargain.getEndTime().minusMinutes(bargain.getBargainValidityPeriod())))
                        .setExecutorHandler(BargainConstant.BARGAIN_XXL_JOB_HANDLER)
                        .setExecutorParam(JSON.toJSONString(new BargainKey(bargain.getShopId(), bargain.getId(), bargainProductIds)))
        );
    }

    /**
     * 获取砍价活动详情
     *
     * @param shopId     店铺id
     * @param activityId 砍价活动id
     * @return 砍价活动详情
     */
    @Override
    public BargainDetailVO getBargainDetail(Long shopId, Long activityId) {

        Bargain bargain = bargainService.lambdaQuery()
                .eq(Bargain::getId, activityId)
                .eq(Bargain::getShopId, shopId)
                .one();
        if (bargain == null) {
            throw SystemCode.DATA_NOT_EXIST.exception();
        }
        List<BargainProduct> bargainProducts = bargainProductService.lambdaQuery()
                .eq(BargainProduct::getActivityId, activityId)
                .eq(BargainProduct::getShopId, shopId)
                .list();

        // 在活动时间内
        boolean processing = bargain.processing();
        List<BargainActivityProductVO> bargainActivityProducts = new ArrayList<>(bargainProducts.size());
        Set<ShopProductKey> shopProductKeys = new HashSet<>();
        List<ActivityShopProductSkuKey> activityShopProductKeys = new ArrayList<>();
        for (BargainProduct bargainProduct : bargainProducts) {
            getBargainActivityProductVO(shopId, activityId, bargainProduct, bargainActivityProducts);
            // 活动进行中
            if (processing) {
                shopProductKeys.add(
                        new ShopProductKey().setShopId(shopId).setProductId(bargainProduct.getProductId())
                );
                // 设置库存查询对象
                ActivityShopProductSkuKey activityShopProductSkuKey = new ActivityShopProductSkuKey();
                activityShopProductSkuKey.setSkuId(bargainProduct.getSkuId())
                        .setProductId(bargainProduct.getProductId())
                        .setShopId(shopId)
                        .setActivityId(activityId)
                        .setActivityType(OrderType.BARGAIN);
                activityShopProductKeys.add(activityShopProductSkuKey);
            }
        }
        //批量查询 是否代销商品
        Map<ShopProductKey, Product> productMap = goodsRpcService.getProductBatch(shopProductKeys);
        Set<ActivityShopProductSkuKey> activityShopProductSets = activityShopProductKeys.stream().peek(key -> {
            Product product = productMap.get(
                    new ShopProductKey()
                            .setProductId(key.getProductId())
                            .setShopId(shopId)
            );
            if (product != null && SellType.CONSIGNMENT.equals(product.getSellType())) {
                key.setShopId(product.getSupplierId());
            }
        }).collect(Collectors.toSet());
        //批量查询 库存
        Map<ActivityShopProductSkuKey, StorageSku> storageSkuMap = storageRpcService.skuBatch(
                activityShopProductSets
        );
        bargainActivityProducts.forEach(bargainActivityProductVO -> {
            ActivityShopProductSkuKey key = new ActivityShopProductSkuKey();
            key.setSkuId(bargainActivityProductVO.getSkuId())
                    .setProductId(bargainActivityProductVO.getProductId())
                    .setShopId(shopId)
                    .setActivityId(activityId)
                    .setActivityType(OrderType.BARGAIN);
            StorageSku storageSku = storageSkuMap.get(key);
            if (storageSku != null) {
                bargainActivityProductVO.setStock(storageSku.getStock());
            }

        });
        return getBargainDetailVO(bargain, bargainActivityProducts);

    }

    /**
     * 分页查询砍价活动
     *
     * @param bargainQuery 砍价活动查询条件
     * @return 砍价活动分页
     */
    @Override
    public IPage<BargainInfoVO> bargainInfoPage(BargainQueryDTO bargainQuery) {
        return bargainService.bargainInfoPage(bargainQuery);
    }


    /**
     * 批量删除砍价活动
     * todo 目前只有店铺能删除 平台无法删除
     *
     * @param bargainShopId 砍价活动id、店铺id集合
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteBatchBargain(List<BargainShopIdDTO> bargainShopId) {
        Set<Long> shopIds = new HashSet<>();
        Set<Long> activityIds = new HashSet<>();
        for (BargainShopIdDTO bargainShopIdDTO : bargainShopId) {
            shopIds.add(bargainShopIdDTO.getShopId());
            activityIds.add(bargainShopIdDTO.getActivityId());
        }
        List<Bargain> dbBargains = bargainService.getBaseMapper().selectBatchIds(activityIds);
        if (CollectionUtils.isEmpty(dbBargains)) {
            throw SystemCode.DATA_NOT_EXIST.exception();
        }
        dbBargains = dbBargains.stream().filter(bargain -> {

            if (ISystem.clientTypeMust().equals(ClientType.SHOP_CONSOLE)) {
                if (!bargain.getShopId().equals(ISystem.shopIdMust())) {
                    //店铺端删除 需要店铺id一致
                    return Boolean.FALSE;
                }
                //店铺端 删除：只有处于【未开始、已结束、违规下架、已下架】的活动才能删除
                if (bargain.notStarted() || bargain.finished() || bargain.illegalSellOff() || bargain.shopSellOff()) {
                    return Boolean.TRUE;
                }

            }
            if (ISystem.clientTypeMust().equals(ClientType.PLATFORM_CONSOLE)) {
                //平台端 删除：只有处于【已结束、违规下架、已下架】的活动才能删除
                if (bargain.finished() || bargain.illegalSellOff() || bargain.shopSellOff()) {
                    return Boolean.TRUE;
                }
            }


            return Boolean.FALSE;
        }).collect(Collectors.toList());
        //是否是店铺端操作
        boolean shopClientFlag = ClientType.SHOP_CONSOLE.equals(ISystem.clientTypeMust());
        if (CollectionUtils.isEmpty(dbBargains)) {
            if (ISystem.clientTypeMust().equals(ClientType.PLATFORM_CONSOLE)) {
                throw BargainErrorEnums.CAN_NOT_DELETE.exception();
            }
            if (ISystem.clientTypeMust().equals(ClientType.SHOP_CONSOLE)) {
                throw BargainErrorEnums.CAN_NOT_REMOVE.exception();
            }

        }
        activityIds = dbBargains.stream().map(Bargain::getId).collect(Collectors.toSet());
        try {

            if (shopClientFlag) {
                //如果是平台端的删除 只是平台自己看不了 但是对于店铺 用户依然是生效的 所以活动的这些下游的数据依然保存

                //只有店铺端删除 才会删除活动的下游数据
                Map<Long, Long> activityShopIds = dbBargains.stream()
                        .collect(Collectors.toMap(Bargain::getId, Bargain::getShopId));
                //查询砍价的商品
                List<BargainProduct> bargainProducts = bargainProductService.lambdaQuery()
                        .select(BargainProduct::getShopId, BargainProduct::getProductId)
                        .in(BargainProduct::getActivityId, activityShopIds.keySet())
                        .list();

                // 砍价商品sku详情缓存key
                deleteBargainProductDetailKey(
                        bargainProducts.stream()
                                .map(bargainProduct -> BargainRedisUtils.getBargainProductSkuDetailKey(
                                        bargainProduct.getShopId(),
                                        bargainProduct.getProductId()
                                )).collect(Collectors.toSet())
                );
                // 归还库存 过滤出正常的砍价信息 店铺下架 或者违规下架 库存已经归还 无需再次归还
                List<Bargain> normalStatusBargain = dbBargains.stream().filter(bargain -> {
                    if (bargain.getStatus().equals(ActivityStatus.ILLEGAL_SELL_OFF) ||
                            bargain.getStatus().equals(ActivityStatus.SHOP_SELL_OFF)) {
                        return Boolean.FALSE;
                    }
                    return Boolean.TRUE;

                }).toList();
                if (!CollectionUtils.isEmpty(normalStatusBargain)) {
                    storageActivityRpcService.activityClose(
                            normalStatusBargain.stream()
                                    .map(bargain -> new ActivityCloseKey()
                                            .setShopId(bargain.getShopId())
                                            .setActivityId(bargain.getId())
                                            .setActivityType(OrderType.BARGAIN)
                                    ).collect(Collectors.toSet())
                    );
                }

                // 砍价订单
                List<BargainOrder> bargainOrders = bargainOrderService.lambdaQuery()
                        .select(BargainOrder::getSponsorId, BargainOrder::getActivityId, BargainOrder::getShopId, BargainOrder::getProductId)
                        .in(BargainOrder::getActivityId, activityIds)
                        .in(BargainOrder::getShopId, shopIds)
                        .eq(BargainOrder::getBargainStatus, BargainStatus.BARGAINING)
                        .list();
                if (!CollectionUtils.isEmpty(bargainOrders)) {
                    doubleDeletion(bargainOrders);
                }
            }


        } finally {
            bargainService.lambdaUpdate()
                    .in(Bargain::getId, activityIds)
                    .remove();
            if (shopClientFlag) {
                bargainProductService.lambdaUpdate()
                        .in(BargainProduct::getActivityId, activityIds)
                        .in(BargainProduct::getShopId, shopIds)
                        .remove();
                bargainOrderService.lambdaUpdate()
                        .in(BargainOrder::getActivityId, activityIds)
                        .in(BargainOrder::getShopId, shopIds)
                        .remove();
                bargainHelpPeopleService.lambdaUpdate()
                        .in(BargainHelpPeople::getActivityId, activityIds)
                        .in(BargainHelpPeople::getShopId, shopIds)
                        .remove();
            }

        }

    }

    private void deleteBargainProductDetailKey(Set<String> bargainProductDetailKey) {
        RedisUtil.doubleDeletion(
                () -> bargainProductDetailKey,
                bargainProductDetailKey
        );
    }

    private void doubleDeletion(List<BargainOrder> bargainOrders) {
        RedisUtil.doubleDeletion(
                () -> deleteBargainCache(bargainOrders),
                () -> deleteBargainCache(bargainOrders)
        );
    }

    @SuppressWarnings({"unchecked"})
    private void deleteBargainCache(List<BargainOrder> bargainOrders) {
        RedisUtil.executePipelined(
                // 发起人砍价商品sku信息缓存key
                bargainSponsorOperations -> bargainOrders.forEach(bargainOrder -> {
                            Long sponsorId = bargainOrder.getSponsorId();
                            Long activityId = bargainOrder.getActivityId();
                            Long shopId = bargainOrder.getShopId();
                            Long productId = bargainOrder.getProductId();
                            // 发起人砍价商品sku信息缓存key
                            RedisUtil.setCacheMap(
                                    BargainRedisUtils.getBargainSponsorProductSkuKey(sponsorId, activityId, shopId,
                                            productId),
                                    new BargainSponsorProductSkuVO()
                                            .setBargainSponsorSkuStatus(BargainSponsorSkuStatus.END),
                                    Duration.ofMinutes(CommonPool.NUMBER_TEN)
                            );
                            // 砍价发起人砍价金额key
                            RedisUtil.delete(BargainRedisUtils.getBargainSponsorProductAmountKey(sponsorId,
                                    activityId, shopId, productId));
                            // 帮砍人信息key
                            RedisUtil.delete(BargainRedisUtils.getBargainHelpPeopleKey(activityId,
                                    bargainOrder.getId()));
                        }
                )

        );
    }


    /**
     * 平台违规下架砍价活动
     *
     * @param bargainVo 砍价活动信息
     */
    @Override
    @SuppressWarnings({"uncheck"})
    @Transactional(rollbackFor = Exception.class)
    public void sellOfBargain(BargainVo bargainVo) {
        Long activityId = bargainVo.getBargainId();
        Long shopId = bargainVo.getShopId();
        if (shopId == null) {
            throw SystemCode.PARAM_VALID_ERROR.exception();
        }
        Bargain dbBargain = bargainService.lambdaQuery()
                .eq(Bargain::getId, bargainVo.getBargainId())
                .eq(Bargain::getShopId, bargainVo.getShopId())
                .one();
        if (Objects.isNull(dbBargain)) {
            throw SystemCode.DATA_NOT_EXIST.exception();
        }
        if (!(dbBargain.notStarted() || dbBargain.processing())) {
            //违规下架  未开始 进行中的才可以进行违规下架
            throw BargainErrorEnums.BARGAIN_STATE_ERROR.exception();
        }

        // 砍价活动下架
        boolean update = bargainService.lambdaUpdate()
                .set(Bargain::getStatus, ActivityStatus.ILLEGAL_SELL_OFF)
                //违规下架的原因
                .set(Bargain::getViolationReason, bargainVo.getViolationReason())
                .eq(Bargain::getId, activityId)
                .update();
        if (!update) {
            throw new GlobalException(SystemCode.DATA_UPDATE_FAILED_CODE, "砍价活动下架失败");
        }

        // 活动已结束
        if (dbBargain.finished()) {
            return;
        }
        //这里是会对进行中状态的砍价活动进行操作
        Set<Long> productIds = bargainProductService.lambdaQuery()
                .select(BargainProduct::getProductId)
                .eq(BargainProduct::getActivityId, activityId)
                .eq(BargainProduct::getShopId, shopId)
                .list().stream()
                .map(BargainProduct::getProductId)
                .collect(Collectors.toSet());
        deleteBargainProductDetailKey(
                productIds.stream()
                        .map(productId -> BargainRedisUtils.getBargainProductSkuDetailKey(shopId, productId))
                        .collect(Collectors.toSet())
        );
        dealAfterOffShelf(shopId, activityId, productIds);
    }

    /**
     * 下架之后的处理
     *
     * @param shopId     商家id
     * @param activityId 活动id
     * @param productIds 产品id
     */
    private void dealAfterOffShelf(Long shopId, Long activityId, Set<Long> productIds) {
        // 归还砍价活动库存
        returnStorageSku(shopId, activityId, productIds);
        // 砍价中

        List<BargainOrder> bargainOrders = bargainOrderService.lambdaQuery()
                .select(BargainOrder::getSponsorId, BargainOrder::getActivityId, BargainOrder::getShopId,
                        BargainOrder::getProductId)
                .eq(BargainOrder::getActivityId, activityId)
                .eq(BargainOrder::getShopId, shopId)
                .eq(BargainOrder::getBargainStatus, BargainStatus.BARGAINING)
                .list();
        if (CollectionUtils.isEmpty(bargainOrders)) {
            return;
        }
        // 砍价活动下架，砍价订单状态改为砍价失败
        bargainOrderService.lambdaUpdate()
                .set(BargainOrder::getBargainStatus, BargainStatus.FAILED_TO_BARGAIN)
                .eq(BargainOrder::getActivityId, activityId)
                .eq(BargainOrder::getShopId, shopId)
                .update();
        //缓存中删除砍价中的订单信息
        deleteBargainCache(bargainOrders);
    }

    @Override
    public void returnStorageSku(Long shopId, Long activityId, Set<Long> productIds) {
        // 归还砍价活动库存
        storageActivityRpcService.activityClose(
                Set.of(
                        new ActivityCloseKey()
                                .setShopId(shopId)
                                .setActivityType(OrderType.BARGAIN)
                                .setActivityId(activityId)
                )
        );
    }

    @Override
    public void shopSellOfBargain(BargainVo bargainVo) {
        Bargain dbBargain = bargainService.lambdaQuery()
                .eq(Bargain::getId, bargainVo.getBargainId())
                .eq(Bargain::getShopId, bargainVo.getShopId())
                .one();
        if (Objects.isNull(dbBargain)) {
            throw SystemCode.DATA_NOT_EXIST.exception();
        }
        //店铺端 只有处于进行中的活动才可以进行下架操作
        if (!dbBargain.processing()) {
            throw BargainErrorEnums.BARGAIN_STATE_ERROR.exception();
        }
        // 砍价活动下架
        boolean update = bargainService.lambdaUpdate()
                //下架 店铺操作
                .set(Bargain::getStatus, ActivityStatus.SHOP_SELL_OFF)
                .eq(Bargain::getId, bargainVo.getBargainId())
                .update();
        if (!update) {
            throw SystemCode.DATA_UPDATE_FAILED.exception();
        }
        Set<Long> productIds = bargainProductService.lambdaQuery().select(BargainProduct::getProductId)
                .eq(BargainProduct::getActivityId, dbBargain.getId())
                .eq(BargainProduct::getShopId, dbBargain.getShopId())
                .list().stream().map(BargainProduct::getProductId)
                .collect(Collectors.toSet());
        deleteBargainProductDetailKey(
                productIds.stream()
                        .map(productId -> BargainRedisUtils.getBargainProductSkuDetailKey(bargainVo.getShopId(), productId))
                        .collect(Collectors.toSet())
        );
        dealAfterOffShelf(dbBargain.getShopId(), dbBargain.getId(), productIds);


    }

    /**
     * 查询砍价活动违规下架的原因
     *
     * @param bargainId
     * @return
     */
    @Override
    public String queryIllegalReason(Long bargainId) {
        Bargain bargain = bargainService.getBaseMapper().selectById(bargainId);
        if (Objects.isNull(bargain)) {
            throw SystemCode.DATA_NOT_EXIST.exception();
        }
        ClientType clientType = ISystem.clientTypeMust();
        if (ClientType.SHOP_CONSOLE.equals(clientType)) {
            if (!ISystem.shopIdMust().equals(bargain.getShopId())) {
                throw BargainErrorEnums.NO_PERMISSION.exception();
            }
        }
        if (!bargain.illegalSellOff()) {
            throw BargainErrorEnums.BARGAIN_STATE_ERROR.exception();
        }
        return bargain.getViolationReason();
    }

}
