package com.medusa.gruul.goods.service.mp.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.medusa.gruul.goods.api.entity.ProductFeatures;
import com.medusa.gruul.goods.api.model.vo.ProductFeaturesVO;
import com.medusa.gruul.goods.service.model.param.ProductFeaturesParam;

/**
 * 产品特性数据层
 *
 * @author xiaoq
 * @Description IProductFeatureService.java
 * @date 2023-06-15 14:19
 */
public interface IProductFeatureService extends IService<ProductFeatures> {
    /**
     *  分页获取产品特性
     *
     * @param param 查询参数
     * @return   IPage<ProductFeaturesVO>
     */
    IPage<ProductFeaturesVO> getProductFeatureList(ProductFeaturesParam param);

}
