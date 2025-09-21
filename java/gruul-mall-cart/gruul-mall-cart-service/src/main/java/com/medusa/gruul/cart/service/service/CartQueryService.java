package com.medusa.gruul.cart.service.service;

import com.medusa.gruul.cart.service.model.vo.CartVO;

/**
 * 用户查询购物车服务接口
 * @author 张治保
 * date 2022/5/16
 */
public interface CartQueryService {
    /**
     * 获取购物车数据
     *
     * @param shopId 店铺id
     * @return 购物车数据
     */
    CartVO myCart(Long shopId);

    /**
     * 购物车商品统计
     * @param shopId  店铺id
     * @return 统计结果
     */
    Integer cartCount(Long shopId);

    /**
     * 购物车金额计算
     *
     * @param shopId 店铺id
     * @return 购物车金额
     */
    Long cartTotalMoney(Long shopId);
}
