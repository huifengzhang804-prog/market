package com.medusa.gruul.addon.integral.mp.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.medusa.gruul.addon.integral.model.dto.IntegralProductFixDTO;
import com.medusa.gruul.addon.integral.model.param.IntegralProductParam;
import com.medusa.gruul.addon.integral.model.vo.IntegralProductListVO;
import com.medusa.gruul.addon.integral.mp.entity.IntegralProduct;
import org.apache.ibatis.annotations.Param;

/**
 * @author xiaoq
 * @Description
 * @date 2023-01-31 15:08
 */
public interface IntegralProductMapper extends BaseMapper<IntegralProduct> {
    /**
     * 查询积分商品列表数据
     *
     * @param integralProductParam 积分商品查询数据
     * @return 分页数据信息
     */
    IPage<IntegralProductListVO> queryIntegralProductList(@Param("integralProductParam") IntegralProductParam integralProductParam);

    /**
     * 根据积分商品id 获取积分商品详细信息
     *
     * @param id 积分商品id
     * @return 积分商品详细信息
     */
    IntegralProduct queryIntegralProductInfoById(@Param("id") Long id);

    void updateProductIncrement(@Param("integralProductFix") IntegralProductFixDTO integralProductFix);
}
