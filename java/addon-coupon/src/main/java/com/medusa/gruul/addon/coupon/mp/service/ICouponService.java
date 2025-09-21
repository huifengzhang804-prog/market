package com.medusa.gruul.addon.coupon.mp.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.medusa.gruul.addon.coupon.model.dto.ConsumerCouponQueryDTO;
import com.medusa.gruul.addon.coupon.model.dto.CouponQueryDTO;
import com.medusa.gruul.addon.coupon.model.dto.OrderCouponPageDTO;
import com.medusa.gruul.addon.coupon.model.dto.ProductCouponPageDTO;
import com.medusa.gruul.addon.coupon.model.vo.CouponVO;
import com.medusa.gruul.addon.coupon.mp.entity.Coupon;

import java.util.List;
import java.util.Set;

/**
 * 优惠券 服务类
 *
 * @author 张治保
 * @since 2022-11-02
 */
public interface ICouponService extends IService<Coupon> {


    /**
     * 管理端分页查询优惠券
     *
     * @param shopId     店铺id 为空则为 平台查询 否则为店铺查询
     * @param queryParam 查询参数
     * @return 分页查询结果
     */
    IPage<Coupon> couponPage(Long shopId, CouponQueryDTO queryParam);


    /**
     * 消费者端分页查询优惠券
     *
     * @param userId 用户id
     * @param query  查询条件
     * @return 分页查询结果
     */
    IPage<CouponVO> consumerCouponPage(Long userId, ConsumerCouponQueryDTO query);


    /**
     * 结算选择优惠券
     *
     * @param bid             业务id
     * @param userId          用户id
     * @param orderCouponPage 分页查询条件  查询平台时 店铺id为0
     * @return 分页结果
     */
    IPage<CouponVO> orderShopCouponPage(String bid, Long userId, OrderCouponPageDTO orderCouponPage);

    /**
     * 商品详情优惠券优惠
     *
     * @param query  查询参数
     * @param userId 当前查询的用户id
     * @return 分页查询结果
     */
    IPage<CouponVO> productShopCouponPage(ProductCouponPageDTO query, Long userId);

    /**
     * 获取店铺前3个优先级高的优惠券规则
     *
     * @param shopIds 店铺id
     * @return CouponVO
     */
    List<CouponVO> getCouponList(Set<Long> shopIds);
}
