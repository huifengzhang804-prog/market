package com.medusa.gruul.addon.coupon.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.medusa.gruul.addon.coupon.model.dto.*;
import com.medusa.gruul.addon.coupon.model.vo.CouponUserVO;
import com.medusa.gruul.addon.coupon.model.vo.CouponVO;
import io.vavr.control.Option;

import java.util.List;

/**
 * @author 张治保
 * date 2022/11/3
 */
public interface ConsumerCouponService {

    /**
     * 消费者端分页查询优惠券
     *
     * @param userIdOpt 可能为空的用户id 为空则表示匿名登陆获取可领取的所有优惠券
     * @param query     查询条件 仅当查询状态为待使用时才会查询 适用的商品id列表
     * @return 分页查询结果
     */
    IPage<CouponVO> consumerCouponPage(Option<Long> userIdOpt, ConsumerCouponQueryDTO query);

    /**
     * 领取优惠券
     *
     * @param userId   用户id
     * @param shopId   店铺id
     * @param couponId 优惠券id
     */
    Boolean collectCoupon(Long userId, Long shopId, Long couponId);


    /**
     * 获取用户平台可用优惠券列表
     *
     * @param userId 用户id
     * @return 平台优惠券列表
     */
    List<CouponVO> platformCouponAvailable(Long userId);

    /**
     * 结算选择优惠券
     *
     * @param userId          用户id
     * @param orderCouponPage 分页查询条件  查询平台时 店铺id为0
     * @return 分页结果
     */
    IPage<CouponVO> orderShopCouponPage(Long userId, OrderCouponPageDTO orderCouponPage);

    /**
     * 商品详情优惠券优惠
     *
     * @param query 查询参数
     * @return 分页查询结果
     */
    IPage<CouponVO> productShopCouponPage(ProductCouponPageDTO query);

    /**
     * 商品详情优惠券优惠
     *
     * @param query 查询参数
     * @return 分页查询结果
     */
    IPage<CouponUserVO> useRecord(CouponUserDTO query);

    /**
     * 用券记录列表导出
     *
     * @param query 用券记录查询参数
     * @return 导出id
     */
    Long export(CouponUserDTO query);


}
