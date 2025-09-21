package com.medusa.gruul.addon.integral.service;

import com.medusa.gruul.addon.integral.model.dto.IntegralProductDTO;
import com.medusa.gruul.addon.integral.model.dto.IntegralProductFixDTO;
import com.medusa.gruul.addon.integral.model.enums.IntegralProductStatus;

import java.util.Set;

/**
 * 管理端积分商品数据层
 *
 * @author xiaoq
 * @Description 管理端积分商品数据层
 * @date 2023-02-01 09:26
 */
public interface ManageIntegralProductService {
    /**
     * 积分商品发布
     *
     * @param integralProduct 积分商品dto
     */
    void issueIntegralProduct(IntegralProductDTO integralProduct);

    /**
     * 积分商品信息删除 双删
     *
     * @param ids 积分商品ids
     */
    void delIntegralProduct(Set<Long> ids);

    /**
     * 积分商品修改
     *
     * @param integralProduct 积分商品dto
     */
    void updateIntegralProduct(IntegralProductDTO integralProduct);

    /**
     * 积分商品批量上下架
     *
     * @param ids    积分商品ids
     * @param status 积分商品修改状态
     */
    void updateIntegralProductStatus(Set<Long> ids, IntegralProductStatus status);

    /**
     *积分商品信息增量修改
     *
     * @param integralProductFix 修改内容
     */
    void updateProductIncrement(IntegralProductFixDTO integralProductFix);
}
