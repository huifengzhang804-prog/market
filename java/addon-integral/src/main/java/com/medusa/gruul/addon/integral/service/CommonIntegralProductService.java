package com.medusa.gruul.addon.integral.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.medusa.gruul.addon.integral.model.param.IntegralProductParam;
import com.medusa.gruul.addon.integral.model.vo.IntegralProductListVO;
import com.medusa.gruul.addon.integral.mp.entity.IntegralProduct;

/**
 * 平台+用户端 通用积分商品数据层
 *
 * @author xiaoq
 * @Description 平台+用户端 通用积分商品数据层
 * @date 2023-02-01 10:41
 */
public interface CommonIntegralProductService {
    /**
     * 积分商品列表信息
     *
     * @param integralProductParam 积分商品查询参数
     * @return 分页数据
     */
    IPage<IntegralProductListVO> getIntegralProductList(IntegralProductParam integralProductParam);

    /**
     * 获取积分商品信息by id
     *
     * @param id 积分商品id
     * @return 积分商品信息
     */
    IntegralProduct getIntegralProductInfo(Long id);
}
