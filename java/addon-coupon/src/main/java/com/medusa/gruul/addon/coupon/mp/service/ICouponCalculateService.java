package com.medusa.gruul.addon.coupon.mp.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.medusa.gruul.addon.coupon.mp.entity.CouponCalculate;

import java.util.List;
import java.util.function.Function;

/**
 * 服务类
 *
 * @author 张治保
 * @since 2022-11-07
 */
public interface ICouponCalculateService extends IService<CouponCalculate> {


    /**
     * 执行计算操作
     *
     * @param couponCalculates 需要的计算的商品数据
     * @param dataFunc         接收一个bid参数作为查询条件
     * @param <T>              返回值类型
     * @return 计算结果
     */
    <T> T todo(List<CouponCalculate> couponCalculates, Function<String, T> dataFunc);

}
