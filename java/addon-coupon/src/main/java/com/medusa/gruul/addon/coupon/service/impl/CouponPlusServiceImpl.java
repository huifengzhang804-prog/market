package com.medusa.gruul.addon.coupon.service.impl;

import cn.hutool.json.JSONUtil;
import com.medusa.gruul.addon.coupon.model.CouponConstant;
import com.medusa.gruul.addon.coupon.model.CouponErrorCode;
import com.medusa.gruul.addon.coupon.model.enums.CouponStatus;
import com.medusa.gruul.addon.coupon.model.vo.CouponVO;
import com.medusa.gruul.addon.coupon.mp.entity.Coupon;
import com.medusa.gruul.addon.coupon.mp.entity.CouponProduct;
import com.medusa.gruul.addon.coupon.mp.entity.CouponUser;
import com.medusa.gruul.addon.coupon.mp.service.ICouponProductService;
import com.medusa.gruul.addon.coupon.mp.service.ICouponService;
import com.medusa.gruul.addon.coupon.mp.service.ICouponUserService;
import com.medusa.gruul.addon.coupon.properties.CouponConfigurationProperties;
import com.medusa.gruul.addon.coupon.service.CouponPlusService;
import com.medusa.gruul.common.model.base.ShopProductSkuKey;
import com.medusa.gruul.common.model.constant.CommonPool;
import com.medusa.gruul.common.model.resp.SystemCode;
import com.medusa.gruul.common.redis.util.RedisUtil;
import com.medusa.gruul.global.model.constant.SecurityConst;
import com.medusa.gruul.global.model.exception.GlobalException;
import com.medusa.gruul.global.model.helper.CompletableTask;
import io.vavr.control.Option;
import lombok.RequiredArgsConstructor;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.util.*;
import java.util.concurrent.Executor;
import java.util.function.Supplier;
import java.util.stream.Collectors;

/**
 * @author 张治保
 * date 2022/11/3
 */
@Service
@RequiredArgsConstructor
public class CouponPlusServiceImpl implements CouponPlusService {

    private final ICouponService couponService;
    private final RedissonClient redissonClient;
    private final ICouponUserService couponUserService;
    private final ICouponProductService couponProductService;
    private final Executor couponCompletableTaskExecutor;
    private final CouponConfigurationProperties couponConfigurationProperties;

    @Override
    public Option<Coupon> getCoupon(Long shopId, Long couponId) {
//        ClientType clientType = ISystem.clientTypeMust();
        return Option.of(
                RedisUtil.getCacheMap(
                        Coupon.class,
                        () -> couponService.lambdaQuery()
//                                //平台端查询的时候 查询平台端的删除标记位
//                                .eq(ClientType.PLATFORM_CONSOLE.equals(clientType),Coupon::getPlatformDeleteFlag,Boolean.FALSE)
//                                //非平台端查询的时候 查询商铺删除的标记位
//                                .eq(!ClientType.PLATFORM_CONSOLE.equals(clientType),Coupon::getShopDeleteFlag,Boolean.FALSE)
                                .eq(Coupon::getShopId, shopId)
                                .eq(Coupon::getId, couponId)
                                .one(),
                        Duration.ofSeconds(couponConfigurationProperties.getCacheExpire().getCoupon()),
                        CouponConstant.COUPON_CACHE_KEY,
                        shopId,
                        couponId
                )
        );
    }

    @Override
    public Coupon couponStockReduce(Long shopId, Long couponId, Integer num) {
        Coupon coupon = this.getCoupon(shopId, couponId).getOrElseThrow(() -> new GlobalException(SystemCode.DATA_NOT_EXIST));
        if (coupon.getStatus() == CouponStatus.BANED||coupon.getStatus()==CouponStatus.SHOP_BANED) {
            throw new GlobalException(CouponErrorCode.COUPON_INVALID, "当前优惠券不可用");
        }
        Long stock = coupon.getStock();
        if (stock < 1) {
            throw new GlobalException(CouponErrorCode.COUPON_OUT_STOCK, "优惠券库存不足");
        }
        Long result = RedisUtil.getRedisTemplate().opsForHash().increment(
                RedisUtil.key(CouponConstant.COUPON_CACHE_KEY, shopId, couponId),
                CouponConstant.COUPON_STOCK_FIELD,
                -num
        );
        if (result < 0) {
            RedisUtil.getRedisTemplate().opsForHash().increment(
                    RedisUtil.key(CouponConstant.COUPON_CACHE_KEY, shopId, couponId),
                    CouponConstant.COUPON_STOCK_FIELD,
                    num
            );
            throw new GlobalException(CouponErrorCode.COUPON_OUT_STOCK, "优惠券库存不足");
        }
        CompletableTask.allOf(
                couponCompletableTaskExecutor,
                () -> this.couponStockReduceDb(shopId, couponId, num)
        );
        return coupon.setStock(result);
    }


    @Override
    public Coupon couponStockReduceDb(Long shopId, Long couponId, Integer num) {
        Coupon coupon = couponService.lambdaQuery()
                .eq(Coupon::getShopId, shopId)
                .eq(Coupon::getId, couponId)
                .ge(Coupon::getStock, num)
                .one();
        if (coupon == null) {
            throw new GlobalException(SystemCode.DATA_NOT_EXIST);
        }
        Long remainStock = coupon.getStock() - num;
        boolean update = couponService.lambdaUpdate()
                .set(Coupon::getStock, remainStock)
                .set(Coupon::getVersion,coupon.getVersion()+ CommonPool.NUMBER_ONE)
                .eq(Coupon::getShopId, shopId)
                .eq(Coupon::getId, couponId)
                .eq(Coupon::getVersion, coupon.getVersion())
                .update();
        if (!update) {
            this.couponStockReduceDb(shopId, couponId, num);
        }
        return coupon.setStock(remainStock);
    }


    @Override
    public Set<Long> getProductIds(Long shopId, Long couponId) {
        return couponProductService.lambdaQuery()
                .select(CouponProduct::getProductId)
                .eq(CouponProduct::getShopId, shopId)
                .eq(CouponProduct::getCouponId, couponId)
                .list()
                .stream()
                .map(CouponProduct::getProductId)
                .collect(Collectors.toSet());
    }

    @Override
    public Option<CouponUser> getCouponUserForUse(Long userId, Long shopId, Long couponUserId) {
        return Option.of(
                couponUserService.lambdaQuery()
                        .eq(CouponUser::getUserId, userId)
                        .eq(CouponUser::getShopId, shopId)
                        .eq(CouponUser::getId, couponUserId)
                        .one()
        );
    }

    @Override
    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRES_NEW)
    public <T> T lockUserCouponsBatch(Long userId, Map<Long, Long> shopIdCouponIdMap, Supplier<T> supplier, String orderNo, HashMap<Long, Set<Long>> orderShopIdsMap) {
        Set<String> allCouponCacheKeys = shopIdCouponIdMap.entrySet()
                .stream()
                .map(entry -> RedisUtil.key(CouponConstant.COUPON_USER_CACHE_KEY, userId, entry.getKey(), entry.getValue()))
                .collect(Collectors.toSet());
        return RedisUtil.doubleDeletion(
                () -> this.lockUserCouponsBatch(userId, shopIdCouponIdMap.entrySet().iterator(), supplier, orderNo, orderShopIdsMap),
                () -> RedisUtil.delete(allCouponCacheKeys)
        );
    }

    /**
     * 挨个锁定 每个优惠券 锁完之后执行任务 然后再释放锁 这里是递归
     */
    public <T> T lockUserCouponsBatch(Long userId, Iterator<Map.Entry<Long, Long>> shopIdCouponIdsIterator, Supplier<T> supplier, String orderNo, HashMap<Long, Set<Long>> orderShopIdsMap) {
        if (!shopIdCouponIdsIterator.hasNext()) {
            return supplier.get();
        }
        Map.Entry<Long, Long> shopIdCouponId = shopIdCouponIdsIterator.next();
        Long shopId = shopIdCouponId.getKey();
        Long couponUserId = shopIdCouponId.getValue();
        RLock lock = redissonClient.getLock(RedisUtil.key(CouponConstant.COUPON_USER_USE_LOCK, userId, shopId, couponUserId));
        boolean lockSuccess = lock.tryLock();
        if (!lockSuccess) {
            throw new GlobalException("当前优惠券正在被使用", CouponErrorCode.COUPON_IS_BEING_USED, new ShopProductSkuKey().setShopId(shopId));
        }

        // 当订单使用有平台券时集合不为空, 所以这里associatedShopIds有值的情况: map集合需有值, 且当前优惠券为平台券
        Set<Long> associatedShopIds = orderShopIdsMap.get(couponUserId);

        try {
            T result = lockUserCouponsBatch(userId, shopIdCouponIdsIterator, supplier, orderNo, orderShopIdsMap);
            boolean update = couponUserService.lambdaUpdate()
                    .set(CouponUser::getUsed, Boolean.TRUE)
                    .set(CouponUser::getAssociatedOrderNo, orderNo)
                    // 如果当前优惠券为平台券, 将用更新的一组店铺Id
                    .setSql(null != associatedShopIds && !associatedShopIds.isEmpty(), String.format("associated_shop_ids = \"%s\"", JSONUtil.toJsonStr(associatedShopIds)))
                    // .setSql(null != associatedShopIds && !associatedShopIds.isEmpty(), "associated_shop_ids = #{associatedShopIds}", "\"" + JSONUtil.toJsonStr(associatedShopIds) + "\"")
                    .eq(CouponUser::getUserId, userId)
                    .eq(CouponUser::getShopId, shopId)
                    .eq(CouponUser::getId, couponUserId)
                    .eq(CouponUser::getUsed, Boolean.FALSE)
                    .update();
            if (!update) {
                throw new GlobalException("优惠券已被使用", CouponErrorCode.COUPON_USED, new ShopProductSkuKey().setShopId(shopId));
            }
            return result;
        } finally {
            lock.unlock();
        }
    }


    @Override
    @Transactional(rollbackFor = Exception.class)
    public void unlockUserCouponBatch(Long userId, Map<Long, Long> shopIdCouponIdMap, Runnable task) {
        this.unlockUserCouponBatch(userId, shopIdCouponIdMap.entrySet().iterator(), task);
    }

    /**
     * 获取店铺前3个优先级高的优惠券规则
     *
     * @param shopIds 店铺id
     * @return CouponVO
     */
    @Override
    public List<CouponVO> getCouponList(Set<Long> shopIds) {
        return couponService.getCouponList(shopIds);
    }

    public void unlockUserCouponBatch(Long userId, Iterator<Map.Entry<Long, Long>> shopIdCouponIdIterator, Runnable task) {
        if (!shopIdCouponIdIterator.hasNext()) {
            task.run();
            return;
        }
        Map.Entry<Long, Long> shopIdCouponId = shopIdCouponIdIterator.next();
        Long shopId = shopIdCouponId.getKey();
        Long couponUserId = shopIdCouponId.getValue();
        RLock lock = redissonClient.getLock(RedisUtil.key(CouponConstant.COUPON_USER_USE_LOCK, userId, shopId, couponUserId));
        boolean lockSuccess = lock.tryLock();
        try {
            this.unlockUserCouponBatch(userId, shopIdCouponIdIterator, task);
            couponUserService.lambdaUpdate()
                    .set(CouponUser::getUsed, Boolean.FALSE)
                    .set(CouponUser::getAssociatedOrderNo, null)
                    // 如果当前优惠券为平台券(即shopId=0), 则需要将用券时更新的一组店铺Id更新回空, 但须保留平台代表的shopId(shopId=0)(平台端可以查看所有用券记录, 这里不添加也行)
                    .setSql(String.format("associated_shop_ids = \"%s\"", JSONUtil.toJsonStr(Set.of(SecurityConst.NO_SHOP_ID_CLIENT_DEFAULT_SHOP_ID))))
                    .eq(CouponUser::getUserId, userId)
                    .eq(CouponUser::getShopId, shopId)
                    .eq(CouponUser::getId, couponUserId)
                    .eq(CouponUser::getUsed, Boolean.TRUE)
                    .update();
        } finally {
            if (lockSuccess) {
                lock.unlock();
            }
        }

    }
}
