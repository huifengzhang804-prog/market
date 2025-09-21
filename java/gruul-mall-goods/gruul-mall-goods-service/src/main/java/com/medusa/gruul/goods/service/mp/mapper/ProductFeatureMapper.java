package com.medusa.gruul.goods.service.mp.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.medusa.gruul.goods.api.entity.ProductFeatures;
import com.medusa.gruul.goods.api.model.vo.ProductFeaturesVO;
import com.medusa.gruul.goods.service.model.param.ProductFeaturesParam;
import org.apache.ibatis.annotations.Param;

/**
 * 产品特性持久层
 *
 * @author xiaoq
 * @Description ProductFeatureMapper.java
 * @date 2023-06-15 14:21
 */
public interface ProductFeatureMapper extends BaseMapper<ProductFeatures> {
    /**
     * 分页获取产品特性数据
     *
     * @param param 查询参数
     * @return  IPage<ProductFeaturesVO>
     */
    IPage<ProductFeaturesVO> getProductFeatureList(@Param("param") ProductFeaturesParam param);

}
