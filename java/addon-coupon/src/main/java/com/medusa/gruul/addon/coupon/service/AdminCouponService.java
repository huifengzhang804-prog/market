package com.medusa.gruul.addon.coupon.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.medusa.gruul.addon.coupon.model.dto.*;
import com.medusa.gruul.addon.coupon.mp.entity.Coupon;
import io.vavr.control.Option;

import java.util.List;
import java.util.Set;

/**
 * @author 张治保
 * date 2022/11/2
 */
public interface AdminCouponService {

    /**
     * 新增优惠券
     *
     * @param shopId 店铺id
     * @param coupon 优惠券信息
     */
    void newCoupon(Long shopId, CouponDTO coupon);

    /**
     * 分页查询优惠券
     *
     * @param shopIdOpt  可能为空的店铺id
     * @param queryParam 分页查询条件
     * @return 分页查询结果
     */
    IPage<Coupon> couponPage(Option<Long> shopIdOpt, CouponQueryDTO queryParam);

    /**
     * 编辑优惠券
     *
     * @param shopId   店铺id
     * @param couponId 优惠券id
     * @param coupon   优惠券编辑信息
     */
    void editCoupon(Long shopId, Long couponId, CouponDTO coupon);

    /**
     * 编辑已生效的商品
     *
     * @param shopId     店铺id
     * @param couponId   优惠券id
     * @param couponEdit 优惠券编辑
     */
    void editValidCoupon(Long shopId, Long couponId, CouponWorkingEditDTO couponEdit);


    /**
     * 商家端批量删除优惠券
     *
     * @param shopId    店铺id
     * @param couponIds 优惠券id列表
     */
    void deleteShopCouponBatch(Long shopId, Set<Long> couponIds);


    /**
     * 平台赠送优惠券给用户
     *
     * @param shopId      店铺id
     * @param giftsToUser 赠送优惠券详情
     */
    void giftsToUser(Long shopId, GiftsToUserDTO giftsToUser);

    /**
     * 平台端批量删除优惠券
     *
     * @param shopCoupons 店铺id与优惠券id列表
     */
//    void deleteCouponBatch(List<ShopCouponMapDTO> shopCoupons);

    /**
     * 平台端批量删除优惠券
     *
     * @param shopCoupons 店铺id与优惠券id列表
     */
    void banCouponBatch(List<ShopCouponMapDTO> shopCoupons);

    /**
     * 获取优惠券详情
     *
     * @param shopId   店铺id
     * @param couponId 优惠券id
     * @return 优惠券详情
     */
    Coupon coupon(Long shopId, Long couponId);

    /**
     * 店铺端下架优化
     *
     * @param shopCouponMapDTO 优惠券id与店铺id
     * @return 下架成功 true  下架失败 false
     */
    Boolean shopCouponOffShelf(ShopCouponMapDTO shopCouponMapDTO);

    /**
     * 查询优惠券活动的违规原因
     *
     * @param couponId 优惠券id
     * @return 违规原因
     */
    String queryIllegalReason(Long couponId);

    /**
     * 查詢优惠券列列表用于装修展示
     *
     * @param shopId
     * @return
     */
    List<Coupon> queryShopCouponForDecoration(Long shopId);
}
