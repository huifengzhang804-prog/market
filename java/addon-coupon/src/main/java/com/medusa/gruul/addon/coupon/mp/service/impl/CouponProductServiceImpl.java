package com.medusa.gruul.addon.coupon.mp.service.impl;

import com.medusa.gruul.addon.coupon.mp.entity.CouponProduct;
import com.medusa.gruul.addon.coupon.mp.mapper.CouponProductMapper;
import com.medusa.gruul.addon.coupon.mp.service.ICouponProductService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * 优惠券商品关联表 服务实现类
 *
 * @author 张治保
 * @since 2022-11-02
 */
@Service
public class CouponProductServiceImpl extends ServiceImpl<CouponProductMapper, CouponProduct> implements ICouponProductService {

}
