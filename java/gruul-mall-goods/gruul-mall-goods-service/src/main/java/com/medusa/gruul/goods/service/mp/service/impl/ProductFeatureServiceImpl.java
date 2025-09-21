package com.medusa.gruul.goods.service.mp.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.medusa.gruul.goods.api.entity.ProductFeatures;
import com.medusa.gruul.goods.api.model.vo.ProductFeaturesVO;
import com.medusa.gruul.goods.service.model.param.ProductFeaturesParam;
import com.medusa.gruul.goods.service.mp.mapper.ProductFeatureMapper;
import com.medusa.gruul.goods.service.mp.service.IProductFeatureService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;


/**
 * 产品特性数据实现层
 *
 * @author xiaoq
 * @Description ProductFeatureServiceImpl.java
 * @date 2023-06-15 14:21
 */
@Service
@RequiredArgsConstructor
public class ProductFeatureServiceImpl extends ServiceImpl<ProductFeatureMapper, ProductFeatures> implements IProductFeatureService {

    @Override
    public IPage<ProductFeaturesVO> getProductFeatureList(ProductFeaturesParam param) {
        return this.baseMapper.getProductFeatureList(param);
    }
}
