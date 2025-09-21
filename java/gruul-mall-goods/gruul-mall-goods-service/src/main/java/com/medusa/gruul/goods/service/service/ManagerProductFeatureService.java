package com.medusa.gruul.goods.service.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.medusa.gruul.goods.api.model.dto.ProductFeaturesCreateDTO;
import com.medusa.gruul.goods.api.model.dto.ProductFeaturesModifyDTO;
import com.medusa.gruul.goods.api.model.enums.FeaturesType;
import com.medusa.gruul.goods.api.model.vo.ProductFeaturesVO;
import com.medusa.gruul.goods.service.model.param.ProductFeaturesParam;

import java.util.List;

/**
 * 管理端产品特性数据层
 *
 * @author xiaoq
 * @Description ManagerProductFeatureService.java
 * @date 2023-06-15 14:10
 */
public interface ManagerProductFeatureService {
    /**
     * 产品特性保存
     *
     * @param dto 产品特性
     */
    void saveProductFeature(ProductFeaturesCreateDTO dto);

    /**
     * 产品特性修改
     * @param dto 产品特性
     */
    void updateProductFeature(ProductFeaturesModifyDTO dto);

    /**
     * 产品特性删除
     *
     * @param ids 产品特性ids
     * @param featuresType 产品特性类型
     */
    void delProductFeature(List<Long> ids, FeaturesType featuresType);

    /**
     * 商品特性List
     *
     * @param param 查询参数
     * @return IPage<ProductFeaturesVO>
     */
    IPage<ProductFeaturesVO> getProductFeatureList(ProductFeaturesParam param);

}
