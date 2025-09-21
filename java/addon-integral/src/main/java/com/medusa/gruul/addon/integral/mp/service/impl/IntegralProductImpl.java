package com.medusa.gruul.addon.integral.mp.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.medusa.gruul.addon.integral.model.dto.IntegralProductFixDTO;
import com.medusa.gruul.addon.integral.model.param.IntegralProductParam;
import com.medusa.gruul.addon.integral.model.vo.IntegralProductListVO;
import com.medusa.gruul.addon.integral.mp.entity.IntegralProduct;
import com.medusa.gruul.addon.integral.mp.mapper.IntegralProductMapper;
import com.medusa.gruul.addon.integral.mp.service.IIntegralProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * @author xiaoq
 * @Description
 * @date 2023-01-31 15:09
 */
@Service
@RequiredArgsConstructor
public class IntegralProductImpl extends ServiceImpl<IntegralProductMapper, IntegralProduct> implements IIntegralProductService {

    private final IntegralProductMapper integralProductMapper;

    @Override
    public IPage<IntegralProductListVO> getIntegralProductList(IntegralProductParam integralProductParam) {
        return integralProductMapper.queryIntegralProductList(integralProductParam);
    }

    @Override
    public IntegralProduct getIntegralProductInfoById(Long id) {
        return integralProductMapper.queryIntegralProductInfoById(id);
    }

    @Override
    public void updateProductIncrement(IntegralProductFixDTO integralProductFix) {
        baseMapper.updateProductIncrement(integralProductFix);
    }
}
