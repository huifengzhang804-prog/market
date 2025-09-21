package com.medusa.gruul.addon.bargain.service.impl;

import static java.time.Duration.between;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.BooleanUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson2.TypeReference;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.google.common.collect.Lists;
import com.medusa.gruul.addon.bargain.constant.BargainConstant;
import com.medusa.gruul.addon.bargain.model.BargainErrorCode;
import com.medusa.gruul.addon.bargain.model.dto.BargainHelpPeopleDTO;
import com.medusa.gruul.addon.bargain.model.dto.BargainHelpPeopleQueryDTO;
import com.medusa.gruul.addon.bargain.model.dto.BargainSponsorDTO;
import com.medusa.gruul.addon.bargain.model.enums.ActivityStatus;
import com.medusa.gruul.addon.bargain.model.enums.BargainSponsorSkuStatus;
import com.medusa.gruul.addon.bargain.model.enums.BargainStatus;
import com.medusa.gruul.addon.bargain.model.enums.HelpCutAmountType;
import com.medusa.gruul.addon.bargain.model.vo.BargainHelpPeopleVO;
import com.medusa.gruul.addon.bargain.model.vo.BargainSponsorVO;
import com.medusa.gruul.addon.bargain.mp.entity.Bargain;
import com.medusa.gruul.addon.bargain.mp.entity.BargainHelpPeople;
import com.medusa.gruul.addon.bargain.mp.entity.BargainOrder;
import com.medusa.gruul.addon.bargain.mp.entity.BargainProduct;
import com.medusa.gruul.addon.bargain.mp.service.IBargainHelpPeopleService;
import com.medusa.gruul.addon.bargain.mp.service.IBargainOrderService;
import com.medusa.gruul.addon.bargain.mp.service.IBargainProductService;
import com.medusa.gruul.addon.bargain.mp.service.IBargainService;
import com.medusa.gruul.addon.bargain.mq.BargainRabbit;
import com.medusa.gruul.addon.bargain.service.BargainConsumerService;
import com.medusa.gruul.addon.bargain.utils.BargainRedisUtils;
import com.medusa.gruul.addon.bargain.vo.BargainProductDetailVO;
import com.medusa.gruul.addon.bargain.vo.BargainSponsorProductSkuVO;
import com.medusa.gruul.addon.bargain.vo.BargainSponsorProductVO;
import com.medusa.gruul.common.fastjson2.FastJson2;
import com.medusa.gruul.common.model.base.ActivityShopProductKey;
import com.medusa.gruul.common.model.base.ActivityShopProductSkuKey;
import com.medusa.gruul.common.model.constant.CommonPool;
import com.medusa.gruul.common.model.enums.OrderType;
import com.medusa.gruul.common.mp.IManualTransaction;
import com.medusa.gruul.common.redis.util.RedisUtil;
import com.medusa.gruul.common.security.resource.helper.ISecurity;
import com.medusa.gruul.global.model.exception.GlobalException;
import com.medusa.gruul.global.model.helper.AmountCalculateHelper;
import com.medusa.gruul.global.model.helper.CompletableTask;
import com.medusa.gruul.goods.api.entity.Product;
import com.medusa.gruul.goods.api.model.dto.FeatureValueDTO;
import com.medusa.gruul.goods.api.model.dto.ProductFeaturesValueDTO;
import com.medusa.gruul.goods.api.model.enums.ProductStatus;
import com.medusa.gruul.goods.api.rpc.GoodsRpcService;
import com.medusa.gruul.storage.api.entity.StorageSku;
import com.medusa.gruul.storage.api.rpc.StorageRpcService;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayDeque;
import java.util.Collection;
import java.util.Collections;
import java.util.Deque;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.data.redis.core.DefaultTypedTuple;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

/**
 * @author wudi
 */
@Service
@RequiredArgsConstructor
public class BargainConsumerServiceImpl implements BargainConsumerService {

    private final IBargainService bargainService;
    private final IBargainProductService bargainProductService;
    private final IBargainOrderService bargainOrderService;
    private final IBargainHelpPeopleService bargainHelpPeopleService;
    private final StorageRpcService storageRpcService;
    private final RabbitTemplate rabbitTemplate;
    private final RedissonClient redissonClient;
    private final Executor bargainExecutor;
    private final GoodsRpcService goodsRpcService;
    private final RedisTemplate<String, Object> redisTemplate = RedisUtil.getRedisTemplate();

    /**
     * 发起砍价
     *
     * @param bargainSponsor 砍价参数
     * @return 砍价结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public BargainSponsorVO bargainSponsor(BargainSponsorDTO bargainSponsor) {
        Long userId = ISecurity.userMust().getId();

        Long shopId = bargainSponsor.getShopId();
        Long productId = bargainSponsor.getProductId();
        Product productInfo = goodsRpcService.getProductInfo(shopId, productId);
        if (productInfo == null || ProductStatus.SELL_ON != productInfo.getStatus()) {
            throw new GlobalException(BargainErrorCode.BARGAIN_FAILED, "当前商品已下架");
        }
        long featureValueSum = 0;
        Set<ProductFeaturesValueDTO> productAttributes = bargainSponsor.getProductFeaturesValue();
        if (productAttributes != null) {
            try {
                productAttributes = productInfo.getExtra()
                        .checkProductAttributes(ProductFeaturesValueDTO.userSelectedForm(productAttributes));
                bargainSponsor.setProductFeaturesValue(productAttributes);
            } catch (Exception e) {
                throw new GlobalException(e.getMessage());
            }
            featureValueSum = productAttributes.stream()
                    .flatMap(productFeaturesValue -> productFeaturesValue.getFeatureValues().stream())
                    .mapToLong(FeatureValueDTO::getSecondValue)
                    .sum();
        }
        Long skuId = bargainSponsor.getSkuId();
        Long activityId = bargainSponsor.getActivityId();
        LocalDateTime now = LocalDateTime.now();
        // 判断当前商品是否是砍价商品
        BargainProduct bargainProduct = bargainProductService.lambdaQuery()
                .eq(BargainProduct::getShopId, shopId)
                .eq(BargainProduct::getProductId, productId)
                .eq(BargainProduct::getSkuId, skuId)
                .eq(BargainProduct::getActivityId, activityId)
                .one();
        if (bargainProduct == null) {
            throw new GlobalException(BargainErrorCode.BARGAIN_FAILED, "当前商品不是砍价商品");
        }
        // 判断当前用户是否已经发起过砍价
        BargainOrder bargainOrder = bargainOrderService.lambdaQuery()
                .select(BargainOrder::getBargainStatus)
                .eq(BargainOrder::getActivityId, activityId)
                .eq(BargainOrder::getShopId, shopId)
                .eq(BargainOrder::getProductId, productId)
                .eq(BargainOrder::getSponsorId, userId)
                .and(wrapper -> wrapper.eq(BargainOrder::getBargainStatus, BargainStatus.SUCCESSFUL_BARGAIN)
                        .or().eq(BargainOrder::getBargainStatus, BargainStatus.BARGAINING))
                .one();
        if (bargainOrder != null) {
            BargainStatus bargainStatus = bargainOrder.getBargainStatus();
            if (bargainStatus == BargainStatus.BARGAINING) {
                throw new GlobalException(BargainErrorCode.BARGAIN_INITIATED, "当前商品您已经发起过砍价了");
            }
            if (bargainStatus == BargainStatus.SUCCESSFUL_BARGAIN) {
                throw new GlobalException(BargainErrorCode.BARGAIN_SUCCESS, "您成功砍价过该商品，已达到砍价次数上限");
            }
        }
        // 判断当前商品是否在砍价活动中
        Bargain bargain = bargainService.lambdaQuery()
                .select(Bargain::getBargainingPeople, Bargain::getEndTime, Bargain::getBargainValidityPeriod,
                        Bargain::getHelpCutAmount, Bargain::getStackable, Bargain::getIsSelfBargain)
                .in(Bargain::getId, activityId)
//                .eq(ClientType.PLATFORM_CONSOLE.equals(ISystem.clientTypeMust()),Bargain::getPlatformDeleteFlag,Boolean.FALSE)
//                .eq(!ClientType.PLATFORM_CONSOLE.equals(ISystem.clientTypeMust()),Bargain::getShopDeleteFlag,Boolean.FALSE)
                .le(Bargain::getStartTime, now)
                .ge(Bargain::getEndTime, now)
                .notIn(Bargain::getStatus,
                        Lists.newArrayList(ActivityStatus.ILLEGAL_SELL_OFF, ActivityStatus.SHOP_SELL_OFF))
                .one();
        if (bargain == null) {
            throw new GlobalException(BargainErrorCode.BARGAIN_FAILED, "砍价活动不存在");
        }
        // 判断当前商品库存是否充足
        StorageSku storageSku = getStorageSku(
                shopId,
                productId, skuId, activityId
        );
        if (storageSku == null || storageSku.getStock() <= 0) {
            throw new GlobalException(BargainErrorCode.BARGAIN_FAILED, "当前商品库存不足");
        }
        bargainProduct.setSkuPrice(bargainProduct.getSkuPrice() + featureValueSum);
        Integer bargainingPeople = bargain.getBargainingPeople();
        // 获取砍价金额集合
        Deque<Long> bargains = bargain.getHelpCutAmount() == HelpCutAmountType.FIX_BARGAIN ?
                startFixedBargain(bargainingPeople, bargainProduct.getSkuPrice() - bargainProduct.getFloorPrice()) :
                startRandomBargain(bargainingPeople, bargainProduct.getSkuPrice() - bargainProduct.getFloorPrice());

        Long bargainOrderId = getBargainOrderId(bargainSponsor, userId, shopId, productId, activityId, now,
                bargainProduct, bargain, bargains);

        // 返回发起人信息
        return new BargainSponsorVO().setUserId(userId).setShopId(shopId).setProductId(productId).setSkuId(skuId)
                .setActivityId(activityId)
                .setBargainOrderId(bargainOrderId);
    }

    /**
     * 砍价订单id
     *
     * @param bargainSponsor 发起人
     * @param userId         用户id
     * @param shopId         店铺id
     * @param productId      商品id
     * @param activityId     活动id
     * @param now            当前时间
     * @param bargainProduct 砍价商品
     * @param bargain        砍价信息
     * @param bargains       砍价金额集合
     * @return 砍价订单id
     */

    private Long getBargainOrderId(BargainSponsorDTO bargainSponsor, Long userId, Long shopId, Long productId,
            Long activityId, LocalDateTime now, BargainProduct bargainProduct, Bargain bargain
            , Deque<Long> bargains) {

        Long bargainOrderId;
        //自我砍价
        if (BooleanUtil.isTrue(bargain.getIsSelfBargain())) {
            bargain.setSponsorBargainEndTime(now.plusMinutes(bargain.getBargainValidityPeriod()));
            Long helpAmount = bargains.pollLast();
            // 设置砍价缓存和砍价订单
            bargainOrderId = setCacheBargainProductOrder(bargainSponsor, userId, now, activityId, bargainProduct,
                    bargain, bargains);
            if (helpAmount != null) {
                IManualTransaction.afterCommit(
                        () -> bargainExecutor.execute(
                                () -> {
                                    redisTemplate.opsForHash().increment(
                                            BargainRedisUtils.getBargainSponsorProductSkuKey(userId, activityId, shopId,
                                                    productId), BargainConstant.BARGAIN_HELP_PEOPLE_COUNT, helpAmount);
                                    saveBargainHelpCut(
                                            new BargainHelpPeopleDTO()
                                                    .setSponsorId(userId)
                                                    .setShopId(shopId)
                                                    .setProductId(productId)
                                                    .setActivityId(activityId)
                                                    .setBargainOrderId(bargainOrderId)
                                                    .setUserNickName(bargainSponsor.getUserNickname())
                                                    .setUserHeadPortrait(bargainSponsor.getUserHeadPortrait())
                                            , userId, activityId, userId, now, bargain, helpAmount);
                                }
                        )
                );
            }
        } else {
            // 设置砍价缓存和砍价订单
            bargainOrderId = setCacheBargainProductOrder(bargainSponsor, userId, now, activityId, bargainProduct,
                    bargain, bargains);
        }
        return bargainOrderId;
    }

    private StorageSku getStorageSku(Long shopId, Long productId, Long skuId, Long activityId) {
        ActivityShopProductSkuKey activityShopProductSkuKey = new ActivityShopProductSkuKey();
        activityShopProductSkuKey.setSkuId(skuId)
                .setProductId(productId)
                .setShopId(shopId)
                .setActivityId(activityId)
                .setActivityType(OrderType.BARGAIN);
        return storageRpcService.getProductSku(activityShopProductSkuKey);
    }

    /**
     * 帮好友砍价
     *
     * @param bargainHelpPeople 砍价参数
     * @return 砍价结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long helpBargain(BargainHelpPeopleDTO bargainHelpPeople) {
        // 获取当前用户id
        Long userId = ISecurity.userMust().getId();
        Long activityId = bargainHelpPeople.getActivityId();
        Long sponsorId = bargainHelpPeople.getSponsorId();
        Long shopId = bargainHelpPeople.getShopId();
        Long productId = bargainHelpPeople.getProductId();
        LocalDateTime now = LocalDateTime.now();
        String key = BargainRedisUtils.getBargainSponsorProductAmountKey(sponsorId, activityId, shopId, productId);
        // 判断当前用户是否可以帮砍价
        Bargain bargain = validIsHelpCut(userId, activityId, sponsorId, shopId, productId, now, key);
        // 获取砍价金额
        Integer helpAmount = (Integer) redisTemplate.opsForList().rightPop(key);
        if (helpAmount == null) {
            throw new GlobalException(BargainErrorCode.BARGAIN_FAILED,
                    BargainSponsorSkuStatus.FAILED_TO_BARGAIN.getDesc());
        }
        redisTemplate.opsForHash()
                .increment(BargainRedisUtils.getBargainSponsorProductSkuKey(sponsorId, activityId, shopId, productId),
                        BargainConstant.BARGAIN_HELP_PEOPLE_COUNT, helpAmount);
        // 保存砍价帮砍信息
        Long amount = Long.valueOf(helpAmount);
        saveBargainHelpCut(bargainHelpPeople, userId, activityId, sponsorId, now, bargain, amount);
        return amount;
    }

    /**
     * 判断当前用户是否可以帮砍价
     *
     * @param userId     用户id
     * @param activityId 活动id
     * @param sponsorId  发起人id
     * @param shopId     店铺id
     * @param productId  商品id
     * @param now        当前时间
     * @param key        金额key
     * @return 砍价信息
     */
    private Bargain validIsHelpCut(Long userId, Long activityId, Long sponsorId, Long shopId, Long productId,
            LocalDateTime now, String key) {
        BargainSponsorProductSkuVO cacheBargainSponsorProductSku = BargainRedisUtils.getCacheBargainSponsorProductSku(
                sponsorId, activityId, shopId, productId);
        if (cacheBargainSponsorProductSku == null
                || BargainSponsorSkuStatus.END == cacheBargainSponsorProductSku.getBargainSponsorSkuStatus()) {
            throw new GlobalException(BargainErrorCode.BARGAIN_FAILED,
                    BargainSponsorSkuStatus.FAILED_TO_BARGAIN.getDesc());
        }
        if (Boolean.FALSE.equals(redisTemplate.hasKey(key))) {
            throw new GlobalException(BargainErrorCode.BARGAIN_FAILED, BargainSponsorSkuStatus.RESERVE_PRICE.getDesc());
        }
        Bargain bargain;
        RLock lock = redissonClient.getLock(RedisUtil.key(activityId, sponsorId, productId));
        lock.lock();
        try {
            // 判断当前用户是否已经帮砍过价
            boolean isHelpCut = bargainHelpPeopleService.lambdaQuery()
                    .eq(BargainHelpPeople::getUserId, userId)
                    .eq(BargainHelpPeople::getActivityId, activityId)
                    .eq(BargainHelpPeople::getSponsorId, sponsorId)
                    .eq(BargainHelpPeople::getProductId, productId)
                    .exists();
            if (isHelpCut) {
                throw new GlobalException(BargainErrorCode.BARGAIN_FAILED, BargainSponsorSkuStatus.HELPED.getDesc());
            }
            // 砍价订单是否存在并未过期
            BargainOrder bargainOrder = bargainOrderService.lambdaQuery()
                    .select(BargainOrder::getEndTime)
                    .eq(BargainOrder::getActivityId, activityId)
                    .eq(BargainOrder::getSponsorId, sponsorId)
                    .eq(BargainOrder::getProductId, productId)
                    .eq(BargainOrder::getBargainStatus, BargainStatus.BARGAINING)
                    .ge(BargainOrder::getEndTime, now)
                    .one();
            if (bargainOrder == null) {
                throw new GlobalException(BargainErrorCode.BARGAIN_FAILED, BargainSponsorSkuStatus.END.getDesc());
            }
            // 砍价活动是否进行中并未下架
            bargain = bargainService.lambdaQuery()

                    .select(Bargain::getIsSelfBargain,
                            Bargain::getShopId,
                            Bargain::getBargainValidityPeriod
                    )

                    .eq(Bargain::getId, activityId)
//                    .eq(ClientType.PLATFORM_CONSOLE.equals(ISystem.clientTypeMust()),Bargain::getPlatformDeleteFlag,Boolean.FALSE)
//                    .eq(!ClientType.PLATFORM_CONSOLE.equals(ISystem.clientTypeMust()),Bargain::getShopDeleteFlag,Boolean.FALSE)
                    .notIn(Bargain::getStatus,
                            Lists.newArrayList(ActivityStatus.ILLEGAL_SELL_OFF, ActivityStatus.SHOP_SELL_OFF))
                    .one();
            if (bargain == null) {
                throw new GlobalException(BargainErrorCode.BARGAIN_FAILED, BargainSponsorSkuStatus.END.getDesc());
            }
//            UserType userType = bargain.getUserType();
//            if (userType == UserType.NEW_USER) {
//                // 新用户砍价
//                boolean isUser = orderRpcService.isNewUser(userId, bargain.getShopId());
//                if (!isUser) {
//                    throw new GlobalException(BargainErrorCode.BARGAIN_FAILED, "您不是新用户");
//                }
//            }
            // 判断是否自我砍价
            Boolean isSelfBargain = bargain.getIsSelfBargain();
            if (!isSelfBargain && userId.equals(sponsorId)) {
                throw new GlobalException(BargainErrorCode.BARGAIN_FAILED, "不能帮自己砍价");
            }
            bargain.setSponsorBargainEndTime(bargainOrder.getEndTime());
        } finally {
            lock.unlock();
        }
        return bargain;
    }

    /**
     * 保存砍价帮砍信息
     *
     * @param bargainHelpPeople 砍价帮砍信息
     * @param userId            用户id
     * @param activityId        活动id
     * @param sponsorId         发起人id
     * @param now               当前时间
     * @param bargain           砍价活动
     * @param helpAmount        帮砍金额
     */
    private void saveBargainHelpCut(BargainHelpPeopleDTO bargainHelpPeople, Long userId, Long activityId,
            Long sponsorId, LocalDateTime now, Bargain bargain, Long helpAmount) {
        String userNickName = bargainHelpPeople.getUserNickName();
        String userHeadPortrait = bargainHelpPeople.getUserHeadPortrait();
        Long shopId = bargainHelpPeople.getShopId();
        Long productId = bargainHelpPeople.getProductId();
        Long bargainOrderId = bargainHelpPeople.getBargainOrderId();
        CompletableTask.getOrThrowException(
                CompletableTask.allOf(
                        bargainExecutor,
                        () -> {
                            Set<ZSetOperations.TypedTuple<BargainHelpPeopleVO>> tuples = new HashSet<>();
                            tuples.add(
                                    new DefaultTypedTuple<>(
                                            new BargainHelpPeopleVO()
                                                    .setActivityId(activityId)
                                                    .setHelpCutAmount(helpAmount)
                                                    .setHelpCutTime(now)
                                                    .setShopId(shopId)
                                                    .setUserId(userId)
                                                    .setUserNickName(userNickName)
                                                    .setUserHeadPortrait(userHeadPortrait)
                                                    .setShopId(shopId), helpAmount.doubleValue()
                                    )
                            );
                            String bargainHelpPeopleRedisKey = BargainRedisUtils.getBargainHelpPeopleKey(activityId,
                                    bargainOrderId);
                            // 缓存帮砍人信息
                            RedisUtil.setCacheZSet(bargainHelpPeopleRedisKey, tuples);
                            redisTemplate.expire(bargainHelpPeopleRedisKey,
                                    Duration.between(now, bargain.getSponsorBargainEndTime()));
                        },
                        () -> {
                            // 保存帮砍人信息
                            bargainHelpPeopleService.save(
                                    new BargainHelpPeople()
                                            .setUserId(userId)
                                            .setActivityId(activityId)
                                            .setProductId(bargainHelpPeople.getProductId())
                                            .setUserNickName(userNickName)
                                            .setUserHeadPortrait(userHeadPortrait)
                                            .setShopId(shopId)
                                            .setSponsorId(sponsorId)
                                            .setHelpCutTime(now)
                                            .setHelpCutAmount(helpAmount)
                                            .setBargainOrderId(bargainOrderId)
                            );
                            // 更新砍价订单砍价人数
                            bargainOrderService.lambdaUpdate()
                                    .setSql(StrUtil.format(BargainConstant.BARGAIN_HELP_PEOPLE_SQL_TEMPLATE,
                                            CommonPool.NUMBER_ONE))
                                    .eq(BargainOrder::getActivityId, activityId)
                                    .eq(BargainOrder::getProductId, productId)
                                    .eq(BargainOrder::getSponsorId, sponsorId)
                                    .eq(BargainOrder::getBargainStatus, BargainStatus.BARGAINING)
                                    .update();
                        }
                ));


    }

    /**
     * 获取砍价活动发起人商品sku信息
     *
     * @param sponsorId  发起人id
     * @param activityId 活动id
     * @param shopId     店铺id
     * @param productId  商品id
     * @param skuId      商品sku id
     * @return 砍价活动发起人商品sku信息
     */
    @Override
    public BargainSponsorProductSkuVO getBargainSponsorProductSku(Long sponsorId, Long activityId, Long shopId,
            Long productId, Long skuId, Long bargainOrderId) {
        BargainSponsorProductSkuVO sponsorProductSku = BargainRedisUtils.getBargainSponsorProductSku(
                RedisUtil.key(sponsorId, shopId, activityId),
                () -> BargainRedisUtils.getCacheBargainSponsorProductSku(sponsorId, activityId, shopId, productId),
                () -> getDbBargainSponsorProductSku(sponsorId, activityId, shopId, productId, bargainOrderId)
        );

        if (BargainSponsorSkuStatus.END == sponsorProductSku.getBargainSponsorSkuStatus()) {
            return sponsorProductSku;
        }
        //如果是进行中则检查库存是否充足 不充足标识为 砍价失败
        if (BargainSponsorSkuStatus.IN_PROGRESS == sponsorProductSku.getBargainSponsorSkuStatus()) {
            Product product = goodsRpcService.getProductInfo(shopId, productId);
            // 判断库存
            StorageSku storageSku = getStorageSku(
                    shopId,
                    productId, skuId, activityId);
            if (storageSku == null || storageSku.getStock() <= 0) {
                BargainRedisUtils.setCacheBargainSponsorProductSku(
                        BargainRedisUtils.getBargainSponsorProductSkuKey(sponsorId, activityId, shopId, productId),
                        sponsorProductSku.setBargainSponsorSkuStatus(BargainSponsorSkuStatus.FAILED_TO_BARGAIN));
            }
        }

        getFloorPrice(sponsorProductSku);
        return sponsorProductSku;
    }

    private void getFloorPrice(BargainSponsorProductSkuVO sponsorProductSku) {
        Set<ProductFeaturesValueDTO> productAttributes = sponsorProductSku.getProductAttributes();
        if (CollectionUtils.isEmpty(productAttributes)) {
            return;
        }
        sponsorProductSku.setFloorPrice(
                sponsorProductSku.getFloorPrice() +
                        productAttributes.stream()
                                .flatMap(productFeaturesValueDTO -> productFeaturesValueDTO.getFeatureValues().stream())
                                .mapToLong(FeatureValueDTO::getSecondValue)
                                .sum()
        );

    }

    /**
     * 获取砍价好友列表
     *
     * @param bargainHelpPeopleQuery 砍价好友列表查询参数
     * @return 砍价帮助列表
     */
    @Override
    public IPage<BargainHelpPeopleVO> bargainHelpPeoplePage(BargainHelpPeopleQueryDTO bargainHelpPeopleQuery) {
        String bargainHelpPeopleKey = BargainRedisUtils.getBargainHelpPeopleKey(bargainHelpPeopleQuery.getActivityId(),
                bargainHelpPeopleQuery.getBargainOrderId());
        long current = bargainHelpPeopleQuery.getCurrent();
        long size = bargainHelpPeopleQuery.getSize();
        long pageNum = current == CommonPool.NUMBER_ONE ? CommonPool.NUMBER_ZERO
                : (current - CommonPool.NUMBER_ONE) * size + CommonPool.NUMBER_ONE;
        List<BargainHelpPeopleVO> bargainHelpPeoples = RedisUtil.getCacheZSet(bargainHelpPeopleKey, pageNum,
                current * size, BargainHelpPeopleVO.class);
        Page<BargainHelpPeopleVO> bargainHelpPeoplePage = new Page<>();
        if (CollectionUtils.isEmpty(bargainHelpPeoples)) {
            return bargainHelpPeoplePage;
        }
        Long total = redisTemplate.opsForZSet().size(bargainHelpPeopleKey);
        total = total == null ? CommonPool.NUMBER_ZERO : total;
        return bargainHelpPeoplePage.setRecords(bargainHelpPeoples)
                .setCurrent(current)
                .setSize(size)
                .setPages((total + size - CommonPool.NUMBER_ONE) / size)
                .setTotal(total);
    }

    /**
     * 获取数据库已发起砍价商品sku信息
     *
     * @param sponsorId  发起人id
     * @param activityId 活动id
     * @param shopId     店铺id
     * @param productId  商品id
     * @return 已发起砍价商品sku信息
     */
    private BargainSponsorProductSkuVO getDbBargainSponsorProductSku(Long sponsorId, Long activityId, Long shopId,
            Long productId, Long bargainOrderId) {
        BargainSponsorProductSkuVO bargainSponsorProductSkuVO = new BargainSponsorProductSkuVO();
        // 获取砍价订单是否存在
        BargainOrder bargainOrder = bargainOrderService.lambdaQuery()
                .eq(BargainOrder::getActivityId, activityId)
                .eq(BargainOrder::getShopId, shopId)
                .eq(BargainOrder::getProductId, productId)
                .eq(BargainOrder::getId, bargainOrderId)
                .eq(BargainOrder::getSponsorId, sponsorId)
                .one();

        String cacheKey = BargainRedisUtils.getBargainSponsorProductSkuKey(sponsorId, activityId, shopId, productId);
        if (bargainOrder == null) {
            BargainSponsorProductSkuVO sponsorProductSku = bargainSponsorProductSkuVO.setBargainSponsorSkuStatus(
                    BargainSponsorSkuStatus.END);
            BargainRedisUtils.setCacheBargainSponsorProductSku(cacheKey, sponsorProductSku);
            return sponsorProductSku;
        }
        BargainHelpPeople bargainHelpPeople = bargainHelpPeopleService.query()
                .select("SUM(help_cut_amount) AS helpCutAmount")
                .eq("activity_id", activityId)
                .eq("sponsor_id", sponsorId)
                .one();
        Bargain bargain = bargainService.lambdaQuery()
                .select(Bargain::getIsSelfBargain)
                .eq(Bargain::getId, activityId)
                .eq(Bargain::getShopId, shopId)
                .one();
        bargainSponsorProductSkuVO.setShopId(shopId)
                .setActivityId(activityId)
                .setProductId(bargainOrder.getProductId())
                .setSkuId(bargainOrder.getSkuId())
                .setSkuName(bargainOrder.getProductName())
                .setSkuImage(bargainOrder.getProductPic())
                .setSkuPrice(bargainOrder.getSalePrice())
                .setBargainSponsorSkuStatus(
                        switch (bargainOrder.getBargainStatus()) {
                            case FAILED_TO_BARGAIN -> BargainSponsorSkuStatus.FAILED_TO_BARGAIN;
                            case SUCCESSFUL_BARGAIN -> BargainSponsorSkuStatus.SUCCESSFUL_BARGAIN;
                            default -> BargainSponsorSkuStatus.IN_PROGRESS;
                        }
                )
                .setAmountCut(bargainHelpPeople != null ? bargainHelpPeople.getHelpCutAmount() : 0L)
                .setIsSelfBargain(bargain.getIsSelfBargain())
                .setEndTime(bargainOrder.getEndTime())
                .setFloorPrice(bargainOrder.getFloorPrice())
                .setUserId(sponsorId)
                .setUserHeadPortrait(bargainOrder.getUserHeadPortrait())
                .setUserNickName(bargainOrder.getUserNickname())
                .setProductAttributes(bargainOrder.getProductAttributes());
        RedisUtil.setCacheMap(cacheKey, bargainSponsorProductSkuVO,
                Duration.between(LocalDateTime.now(), bargainOrder.getEndTime()));
        return bargainSponsorProductSkuVO;
    }

    @SuppressWarnings({"unchecked"})

    private Long setCacheBargainProductOrder(BargainSponsorDTO startBargain, Long userId, LocalDateTime now,
            Long activityId, BargainProduct bargainProduct, Bargain bargain,
            Deque<Long> bargains) {

        Long shopId = bargainProduct.getShopId();
        Long productId = bargainProduct.getProductId();
        Long skuId = bargainProduct.getSkuId();
        LocalDateTime endTime = now.plusMinutes(bargain.getBargainValidityPeriod());

        // 有效期
        Duration expireTime = Duration.ofMinutes(bargain.getBargainValidityPeriod());
        String sponsorProductAmountKey = BargainRedisUtils.getBargainSponsorProductAmountKey(userId, activityId, shopId,
                productId);
        RedisUtil.executePipelined(
                redisOperations -> {
                    // 砍价发起人商品sku砍价金额
                    redisOperations.opsForList().leftPushAll(sponsorProductAmountKey, bargains.toArray());
                    redisOperations.expire(sponsorProductAmountKey, expireTime);
                }
        );
        // 保存砍价订单信息
        BargainOrder bargainOrder = new BargainOrder()
                .setSponsorId(userId)
                .setUserHeadPortrait(startBargain.getUserHeadPortrait())
                .setUserNickname(startBargain.getUserNickname())
                .setActivityId(activityId)
                .setShopId(shopId)
                .setProductId(productId)
                .setSkuId(skuId)
                .setProductPic(startBargain.getSkuImage())
                .setProductName(bargainProduct.getProductName())
                .setSkuName(bargainProduct.getSkuName())
                .setSalePrice(bargainProduct.getSkuPrice())
                .setFloorPrice(bargainProduct.getFloorPrice())
                .setBargainingPeople(CommonPool.NUMBER_ZERO)
                .setPublishBargainingTime(now)
                .setEndTime(endTime)
                .setBargainStatus(BargainStatus.BARGAINING)
                .setProductAttributes(startBargain.getProductFeaturesValue());
        // 保存砍价订单信息
        bargainOrderService.save(bargainOrder);
        BargainSponsorProductSkuVO bargainSponsorProductSkuVO = new BargainSponsorProductSkuVO()
                .setShopId(shopId)
                .setActivityId(activityId)
                .setProductId(productId)
                .setSkuId(skuId)
                .setProductName(bargainProduct.getProductName())
                .setSkuName(bargainProduct.getSkuName())
                .setSkuPrice(bargainProduct.getSkuPrice())
                .setSkuImage(startBargain.getSkuImage())
                .setAmountCut(0L)
                .setEndTime(endTime)
                .setFloorPrice(bargainProduct.getFloorPrice())
                .setUserId(userId)
                .setUserHeadPortrait(startBargain.getUserHeadPortrait())
                .setBargainOrderId(bargainOrder.getId())
                .setIsSelfBargain(bargain.getIsSelfBargain())
                .setStackable(bargain.getStackable())
                .setUserNickName(startBargain.getUserNickname())
                .setProductAttributes(startBargain.getProductFeaturesValue())
                .setBargainSponsorSkuStatus(BargainSponsorSkuStatus.IN_PROGRESS);
        // 砍价发起人商品sku信息
        RedisUtil.setCacheMap(BargainRedisUtils.getBargainSponsorProductSkuKey(userId, activityId, shopId, productId),
                bargainSponsorProductSkuVO, expireTime);

        rabbitTemplate.convertAndSend(
                BargainRabbit.BARGAIN_START.exchange(),
                BargainRabbit.BARGAIN_START.routingKey(),
                bargainOrder,
                message -> {
                    message.getMessageProperties()
                            .setHeader(MessageProperties.X_DELAY, bargain.getBargainValidityPeriod() * 60 * 1000);
                    return message;
                }
        );
        return bargainOrder.getId();
    }

    /**
     * 随机金额砍价
     *
     * @param bargainingPeople 砍价人数
     * @param totalAmount      可砍金额
     * @return 砍价金额
     */
    private Deque<Long> startRandomBargain(Integer bargainingPeople, Long totalAmount) {
        ArrayDeque<Long> bargains = new ArrayDeque<>();
        Random random = new Random();
        long minAmount = 100L;
        //倍数
        for (int index = 0; index < bargainingPeople - 1; index++) {
            //余下金额的平均值
            long avgAmount = AmountCalculateHelper.avgAmount(totalAmount, bargainingPeople - index - 1,
                    RoundingMode.DOWN);
            //随机金额
            long amount = AmountCalculateHelper.getDiscountAmount(avgAmount, BigDecimal.valueOf(random.nextDouble()));
            amount = amount == 0L ? minAmount : amount;
            bargains.addLast(amount);
            totalAmount -= amount;
        }
        //最后一个人获取剩余的金额
        bargains.addLast(totalAmount);
        return bargains;
    }

    /**
     * 固定金额砍价
     *
     * @param bargainingPeople 砍价人数
     * @param totalAmount      可砍金额
     * @return 砍价金额
     */
    private Deque<Long> startFixedBargain(Integer bargainingPeople, Long totalAmount) {
        // 可砍金额 /砍价人数 平均
        long avgAmount = AmountCalculateHelper.avgAmount(totalAmount, bargainingPeople, RoundingMode.DOWN);

        ArrayDeque<Long> bargins = new ArrayDeque<>();
        //每个人砍价金额 为平均值
        for (int index = 1; index < bargainingPeople; index++) {
            bargins.addLast(avgAmount);
            totalAmount -= avgAmount;
        }
        //最后一个人砍价金额为剩余金额
        bargins.addLast(totalAmount);
        return bargins;
    }


    /**
     * 获取砍价商品sku详情和我的砍价信息
     *
     * @param shopId    店铺id
     * @param productId 商品id
     * @return 砍价商品sku详情
     */
    @Override
    public BargainSponsorProductVO getBargainProductSku(Long shopId, Long productId, Long userId) {
        // 获取砍价商品sku
        String bargainProductSkuDetailKey = BargainRedisUtils.getBargainProductSkuDetailKey(shopId, productId);
        List<BargainProductDetailVO> bargainProductDetailList = BargainRedisUtils.getRedisLock(
                // redis锁
                RedisUtil.key(BargainConstant.BARGAIN_PRODUCT_REDIS_LOCK_KEY, shopId, productId),
                // 从缓存中获取砍价商品sku
                () -> getCacheBargainProduct(bargainProductSkuDetailKey),
                // 从数据库中获取砍价商品sku
                () -> getDbBargainProduct(shopId, productId, bargainProductSkuDetailKey)
        );
        BargainSponsorProductVO bargainSponsorProduct = new BargainSponsorProductVO();
        // 判断砍价商品sku是否为空
        if (CollectionUtils.isEmpty(bargainProductDetailList)) {
            return bargainSponsorProduct;
        }
        BargainProductDetailVO bargainProductDetail = bargainProductDetailList.get(CommonPool.NUMBER_ZERO);
        LocalDateTime now = LocalDateTime.now();
        // 砍价活动进行中
        if (now.isAfter(bargainProductDetail.getStartTime()) && now.isBefore(bargainProductDetail.getEndTime())) {
            getInProgressBargain(bargainSponsorProduct, bargainProductDetail.getActivityId(), shopId, productId,
                    bargainProductDetailList, userId);
        }
        bargainSponsorProduct.setBargainProductDetails(bargainProductDetailList);
        return bargainSponsorProduct;


    }

    private List<BargainProductDetailVO> getDbBargainProduct(Long shopId, Long productId, String redisKey) {
        // 判断当前商品是否是砍价商品
        boolean isBargainProduct = true;
        boolean notExistBargainProduct = true;
        Set<Long> activityIds = null;
        try {
            // 判断当前商品是否是砍价商品
            List<BargainProduct> bargainProducts = bargainProductService.lambdaQuery()
                    .eq(BargainProduct::getShopId, shopId)
                    .eq(BargainProduct::getProductId, productId)
                    .list();
            if (CollectionUtils.isEmpty(bargainProducts)) {
                throw new GlobalException(BargainErrorCode.BARGAIN_PRODUCT_NOT_EXISTS, "当前商品不是砍价商品");
            }
            activityIds = bargainProducts.stream()
                    .map(BargainProduct::getActivityId)
                    .collect(Collectors.toSet());
            // 进行中的砍价活动
            LocalDateTime now = LocalDateTime.now();
            Bargain bargain = bargainService.lambdaQuery()
                    .select(Bargain::getId, Bargain::getStartTime, Bargain::getEndTime)
//                    .eq(ClientType.PLATFORM_CONSOLE.equals(ISystem.clientTypeMust()),Bargain::getPlatformDeleteFlag,Boolean.FALSE)
//                    .eq(!ClientType.PLATFORM_CONSOLE.equals(ISystem.clientTypeMust()),Bargain::getShopDeleteFlag,Boolean.FALSE)
                    .in(Bargain::getId, activityIds)
                    .le(Bargain::getStartTime, now)
                    .ge(Bargain::getEndTime, now)
                    .notIn(Bargain::getStatus,
                            Lists.newArrayList(ActivityStatus.ILLEGAL_SELL_OFF, ActivityStatus.SHOP_SELL_OFF))
                    .one();
            if (bargain == null) {
                // 获取预热中的砍价活动
                bargain = getPreheatBargain(activityIds);
            }
            // 获取砍价商品sku
            List<BargainProductDetailVO> bargainProductDetails = getBargainProduct(bargainProducts, bargain);
            redisTemplate.opsForValue().set(redisKey, bargainProductDetails, between(now, bargain.getEndTime()));
            notExistBargainProduct = false;
            return bargainProductDetails;
        } catch (GlobalException e) {
            // 当前商品不是砍价商品
            if (e.code() == BargainErrorCode.BARGAIN_PRODUCT_NOT_EXISTS) {
                isBargainProduct = false;
            }
            return Collections.emptyList();
        } finally {
            // 缓存为null
            if (notExistBargainProduct) {
                setCacheNullBargainProduct(isBargainProduct, activityIds, redisKey);
            }
        }
    }

    /**
     * 缓存为null
     *
     * @param isBargainProduct 是否是砍价商品
     * @param activityIds      砍价活动id
     * @param redisKey         缓存key
     */
    private void setCacheNullBargainProduct(boolean isBargainProduct, Set<Long> activityIds, String redisKey) {
        // 是砍价商品 存在预热未到预热时间 or 存在未开始的砍价活动
        long expireTime = 3600;
        if (isBargainProduct) {
            Bargain bargain = bargainService.lambdaQuery()
                    .select(Bargain::getStartTime)
                    .in(Bargain::getId, activityIds)
//                    .eq(ClientType.PLATFORM_CONSOLE.equals(ISystem.clientTypeMust()),Bargain::getPlatformDeleteFlag,Boolean.FALSE)
//                    .eq(!ClientType.PLATFORM_CONSOLE.equals(ISystem.clientTypeMust()),Bargain::getShopDeleteFlag,Boolean.FALSE)
                    .ge(Bargain::getStartTime, LocalDateTime.now())
                    .notIn(Bargain::getStatus,
                            Lists.newArrayList(ActivityStatus.ILLEGAL_SELL_OFF, ActivityStatus.SHOP_SELL_OFF))
                    .orderByAsc(Bargain::getStartTime)
                    .last("limit 1")
                    .one();
            if (bargain != null) {
                expireTime = Duration.between(LocalDateTime.now(), bargain.getStartTime()).getSeconds();
            }
        }
        redisTemplate.opsForValue().set(redisKey, BargainConstant.NOT_BARGAIN_PRODUCT, expireTime, TimeUnit.SECONDS);
    }


    private List<BargainProductDetailVO> getBargainProduct(List<BargainProduct> bargainProducts, Bargain bargain) {
        Long activityId = bargain.getId();
        return bargainProducts.stream()
                .filter(bargainProduct -> activityId.equals(bargainProduct.getActivityId()))
                .map(bargainProduct ->
                        new BargainProductDetailVO()
                                .setShopId(bargainProduct.getShopId())
                                .setProductId(bargainProduct.getProductId())
                                .setSkuId(bargainProduct.getSkuId())
                                .setActivityId(activityId)
                                .setFloorPrice(bargainProduct.getFloorPrice())
                                .setSkuStock(bargainProduct.getStock())
                                .setStartTime(bargain.getStartTime())
                                .setEndTime(bargain.getEndTime())
                ).toList();
    }

    private List<BargainProductDetailVO> getCacheBargainProduct(String redisKey) {
        Object obj = redisTemplate.opsForValue().get(redisKey);
        if (obj == null) {
            return null;
        }
        if (obj.equals(BargainConstant.NOT_BARGAIN_PRODUCT)) {
            return Collections.emptyList();
        }
        // 反序列化为list对象
        return FastJson2.convert(obj, new TypeReference<>() {
        });
    }


    /**
     * 获取进行中的砍价商品
     *
     * @param bargainSponsorProduct 返回数据
     * @param activityId            砍价活动id
     * @param shopId                店铺id
     * @param productId             商品id
     * @param bargainProducts       砍价商品
     * @param userId                登录用户id
     */
    private void getInProgressBargain(BargainSponsorProductVO bargainSponsorProduct, Long activityId, Long shopId,
            Long productId, List<BargainProductDetailVO> bargainProducts,
            Long userId) {
        ActivityShopProductKey activityShopProductKey = new ActivityShopProductKey();
        activityShopProductKey.setProductId(productId);
        activityShopProductKey.setShopId(shopId)
                .setActivityId(activityId)
                .setActivityType(OrderType.BARGAIN);
        Map<ActivityShopProductKey, List<StorageSku>> storageSkuMap = storageRpcService.productSkuStockBatch(
                CollectionUtil.newHashSet(
                        activityShopProductKey
                )
        );
        if (CollUtil.isNotEmpty(storageSkuMap)) {
            Map<Long, Long> skuIdMap = storageSkuMap.values()
                    .stream()
                    .flatMap(Collection::stream)
                    .toList()
                    .stream().collect(Collectors.toMap(StorageSku::getId, StorageSku::getStock));
            for (BargainProductDetailVO bargainProduct : bargainProducts) {
                bargainProduct.setSkuStock(skuIdMap.get(bargainProduct.getSkuId()));
            }
        }
        if (userId == null) {
            return;
        }
        bargainSponsorProduct.setSponsorProductSku(
                RedisUtil.getCacheMap(
                        BargainRedisUtils.getBargainSponsorProductSkuKey(userId, activityId, shopId, productId),
                        BargainSponsorProductSkuVO.class
                )
        );
    }

    /**
     * 获取预热中的砍价活动
     *
     * @param activityIds 砍价活动id
     * @return 砍价活动
     */
    private Bargain getPreheatBargain(Set<Long> activityIds) {
        // 预热中的砍价活动
        Bargain bargain = bargainService.query()

                .select(
//                        "IFNULL(DATE_ADD(NOW(), INTERVAL activity_preheat HOUR) >= start_time,0) AS activity_preheat," +
                        "id,start_time,end_time")

                .in("id", activityIds)
//                .eq(ClientType.PLATFORM_CONSOLE.equals(ISystem.clientTypeMust()),"platform_delete_flag",Boolean.FALSE)
//                .eq(!ClientType.PLATFORM_CONSOLE.equals(ISystem.clientTypeMust()),"shop_delete_flag",Boolean.FALSE)
                .ge("start_time", LocalDateTime.now())
                .notIn("status", Lists.newArrayList(ActivityStatus.ILLEGAL_SELL_OFF, ActivityStatus.SHOP_SELL_OFF))
                .orderByAsc("start_time")
                .last("limit 1")
                .one();
//        if (bargain != null
//                && bargain.getActivityPreheat().equals(CommonPool.NUMBER_ZERO)) {
//            throw new GlobalException(BargainErrorCode.BARGAIN_ACTIVITY_NOT_EXISTS, "砍价活动不存在");
//        }
        return bargain;
    }

}
