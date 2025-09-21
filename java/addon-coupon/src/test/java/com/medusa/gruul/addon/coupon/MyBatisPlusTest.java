package com.medusa.gruul.addon.coupon;


/**
 * @author jipeng
 * @since 2024/3/13
 */

import com.medusa.gruul.addon.coupon.mp.entity.Coupon;
import com.medusa.gruul.addon.coupon.mp.entity.CouponUser;
import com.medusa.gruul.addon.coupon.mp.service.ICouponService;
import com.medusa.gruul.addon.coupon.mp.service.ICouponUserService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

/**
 * 类描述
 *
 * @author jipeng
 * @since 2024/3/13
 */
@SpringBootTest
@Slf4j
public class MyBatisPlusTest {

    @Resource
    ICouponService couponService;
    @Resource
    ICouponUserService couponUserService;

    @Test
    public void testQuery() {
        List<Coupon> coupons = couponService.getBaseMapper().selectList(null);
        log.info("coupon:{}", coupons);

    }

    @Test
    public void testUpdate() {
        Coupon coupon = new Coupon();
        coupon.setId(1L);
        coupon.setName("测试编辑");
        couponService.updateById(coupon);

    }

    @Test
    public void testDelete() {
        int i = couponService.getBaseMapper().deleteById(1L);
        log.info("i:{}", i);
    }

    @Test
    public void queryCount() {
        Long count = couponUserService.lambdaQuery()
                .eq(CouponUser::getUserId, 11L)
                .eq(CouponUser::getShopId, 22L)
                .eq(CouponUser::getCouponId, 33L)
                .count();
        log.info("count:{}", count);
    }

}
