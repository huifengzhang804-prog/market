package com.medusa.gruul.addon.coupon.service;

import com.medusa.gruul.addon.coupon.model.vo.CouponVO;
import com.medusa.gruul.addon.coupon.mp.entity.Coupon;
import com.medusa.gruul.addon.coupon.mp.entity.CouponUser;
import io.vavr.control.Option;
import jakarta.validation.constraints.Min;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Supplier;

/**
 * @author 张治保
 * date 2022/11/3
 */
public interface CouponPlusService {
    /**
     * 获取优惠券详情
     *
     * @param shopId   店铺id
     * @param couponId 优惠券id
     * @return 优惠券详情
     */
    Option<Coupon> getCoupon(Long shopId, Long couponId);

    /**
     * 优惠券减库存（先redis 扣减成功后 异步操作数据库）
     *
     * @param shopId   店铺id
     * @param couponId 优惠券id
     * @param num      扣减数量
     * @return 扣减成功后的优惠券详情
     */
    Coupon couponStockReduce(Long shopId, Long couponId, @Min(1) Integer num);

    /**
     * 优惠券减库存（数据库乐观锁）
     *
     * @param shopId   店铺id
     * @param couponId 优惠券id
     * @param num      扣减数量
     * @return 扣减成功后的优惠券详情
     */
    Coupon couponStockReduceDb(Long shopId, Long couponId, @Min(1) Integer num);


    /**
     * 获取优惠券 影响的商品id列表
     *
     * @param shopId   店铺id
     * @param couponId 优惠券id
     * @return 商品id 列表
     */
    Set<Long> getProductIds(Long shopId, Long couponId);

    /**
     * 获取待使用的用户的优惠券
     *
     * @param userId       用户id
     * @param shopId       店铺id
     * @param couponUserId 用户领取的优惠券id
     * @return 用户优惠券
     */
    Option<CouponUser> getCouponUserForUse(Long userId, Long shopId, Long couponUserId);


    /**
     * 批量锁定用户的优惠券，然后再执行任务，然后统一释放锁 并删除 缓存里的数据
     *
     * @param userId            用户id
     * @param shopIdCouponIdMap 用户id 优惠券id
     * @param supplier          全部锁定成功后的任务
     * @param orderNo           订单编号(使用优惠券的订单)
     * @param orderShopIdsMap   以优惠券用券记录Id为key, 当前订单一组店铺Id为value; 仅当订单中使用平台券时有值, 平台券即'shopId=0',
     * @param <T>               任务结果类型
     * @return 任务执行结果
     */
    <T> T lockUserCouponsBatch(Long userId, Map<Long, Long> shopIdCouponIdMap, Supplier<T> supplier, String orderNo, HashMap<Long, Set<Long>> orderShopIdsMap);


    /**
     * 解锁 用户优惠券 然后执行任务
     *
     * @param userId            用户id
     * @param shopIdCouponIdMap 店铺ID与优惠券id
     * @param task              结算完成后需要执行的任务
     */
    void unlockUserCouponBatch(Long userId, Map<Long, Long> shopIdCouponIdMap, Runnable task);

    /**
     * 获取店铺前3个优先级高的优惠券规则
     *
     * @param shopIds 店铺id
     * @return CouponVO
     */
    List<CouponVO> getCouponList(Set<Long> shopIds);
}

