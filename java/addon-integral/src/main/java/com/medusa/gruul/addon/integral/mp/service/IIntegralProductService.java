package com.medusa.gruul.addon.integral.mp.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.medusa.gruul.addon.integral.model.dto.IntegralProductFixDTO;
import com.medusa.gruul.addon.integral.model.param.IntegralProductParam;
import com.medusa.gruul.addon.integral.model.vo.IntegralProductListVO;
import com.medusa.gruul.addon.integral.mp.entity.IntegralProduct;

/**
 * @author xiaoq
 * @Description
 * @date 2023-01-31 15:09
 */
public interface IIntegralProductService extends IService<IntegralProduct> {
    /**
     * 获取积分商品列表信息
     *
     * @param integralProductParam 查询数据
     * @return 分页数据
     */
    IPage<IntegralProductListVO> getIntegralProductList(IntegralProductParam integralProductParam);

    /**
     * 根据积分商品id 获取积分商品详细信息
     *
     * @param id 积分商品id
     * @return 积分商品详细信息
     */
    IntegralProduct getIntegralProductInfoById(Long id);

    void updateProductIncrement(IntegralProductFixDTO integralProductFix);
}
