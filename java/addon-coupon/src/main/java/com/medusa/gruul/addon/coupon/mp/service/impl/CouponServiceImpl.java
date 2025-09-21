package com.medusa.gruul.addon.coupon.mp.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.medusa.gruul.addon.coupon.model.dto.ConsumerCouponQueryDTO;
import com.medusa.gruul.addon.coupon.model.dto.CouponQueryDTO;
import com.medusa.gruul.addon.coupon.model.dto.OrderCouponPageDTO;
import com.medusa.gruul.addon.coupon.model.dto.ProductCouponPageDTO;
import com.medusa.gruul.addon.coupon.model.vo.CouponVO;
import com.medusa.gruul.addon.coupon.mp.entity.Coupon;
import com.medusa.gruul.addon.coupon.mp.mapper.CouponMapper;
import com.medusa.gruul.addon.coupon.mp.service.ICouponService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

/**
 * 优惠券 服务实现类
 *
 * @author 张治保
 * @since 2022-11-02
 */
@Service
public class CouponServiceImpl extends ServiceImpl<CouponMapper, Coupon> implements ICouponService {


    @Override
    public IPage<Coupon> couponPage(Long shopId, CouponQueryDTO queryParam) {
        return baseMapper.couponPage(queryParam, shopId);
    }

    @Override
    public IPage<CouponVO> consumerCouponPage(Long userId, ConsumerCouponQueryDTO query) {
        return baseMapper.consumerCouponPage(query, userId);
    }

    @Override
    public IPage<CouponVO> orderShopCouponPage(String bid, Long userId, OrderCouponPageDTO orderCouponPage) {
        return baseMapper.orderShopCouponPage(orderCouponPage, bid, userId);
    }

    @Override
    public IPage<CouponVO> productShopCouponPage(ProductCouponPageDTO query, Long userId) {
        return baseMapper.productShopCouponPage(query, userId);
    }

    /**
     * 获取店铺前3个优先级高的优惠券规则
     *
     * @param shopIds 店铺id
     * @return CouponVO
     */
    @Override
    public List<CouponVO> getCouponList(Set<Long> shopIds) {
        return baseMapper.getCouponList(shopIds);
    }

}
