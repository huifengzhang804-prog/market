package com.medusa.gruul.addon.coupon.mp.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.medusa.gruul.addon.coupon.model.dto.ConsumerCouponQueryDTO;
import com.medusa.gruul.addon.coupon.model.dto.CouponQueryDTO;
import com.medusa.gruul.addon.coupon.model.dto.OrderCouponPageDTO;
import com.medusa.gruul.addon.coupon.model.dto.ProductCouponPageDTO;
import com.medusa.gruul.addon.coupon.model.vo.CouponVO;
import com.medusa.gruul.addon.coupon.mp.entity.Coupon;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Set;

/**
 * 优惠券 Mapper 接口
 *
 * @author 张治保
 * @since 2022-11-02
 */
public interface CouponMapper extends BaseMapper<Coupon> {


    /**
     * 管理端 分页查询优惠券
     *
     * @param shopId 店铺id 为空则为 平台查询 否则为店铺查询
     * @param query  查询参数
     * @return 分页查询结果
     */
    IPage<Coupon> couponPage(@Param("query") CouponQueryDTO query, @Param("shopId") Long shopId);


    /**
     * 消费者端分页查询优惠券
     *
     * @param userId 用户id
     * @param query  查询条件
     * @return 分页查询结果
     */
    IPage<CouponVO> consumerCouponPage(@Param("query") ConsumerCouponQueryDTO query, @Param("userId") Long userId);


    /**
     * 结算选择优惠券
     *
     * @param bid             业务id
     * @param userId          用户id
     * @param orderCouponPage 分页查询条件  查询平台时 店铺id为0
     * @return 分页结果
     */
    IPage<CouponVO> orderShopCouponPage(@Param("query") OrderCouponPageDTO orderCouponPage, @Param("bid") String bid, @Param("userId") Long userId);


    /**
     * 商品详情优惠券优惠
     *
     * @param query  查询参数
     * @param userId yonghuid
     * @return 分页查询结果
     */
    IPage<CouponVO> productShopCouponPage(@Param("query") ProductCouponPageDTO query, @Param("userId") Long userId);

    /**
     * 获取店铺前3个优先级高的优惠券规则
     *
     * @param shopIds 店铺id
     * @return CouponVO
     */
    List<CouponVO> getCouponList(@Param("shopIds") Set<Long> shopIds);
}
