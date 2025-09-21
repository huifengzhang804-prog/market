package com.medusa.gruul.goods.service.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.medusa.gruul.common.mp.model.BaseEntity;
import com.medusa.gruul.goods.api.entity.ProductFeatures;
import com.medusa.gruul.goods.api.model.dto.ProductFeaturesCreateDTO;
import com.medusa.gruul.goods.api.model.dto.ProductFeaturesModifyDTO;
import com.medusa.gruul.goods.api.model.dto.ProductFeaturesValueDTO;
import com.medusa.gruul.goods.api.model.enums.FeaturesType;
import com.medusa.gruul.goods.api.model.enums.GoodsError;
import com.medusa.gruul.goods.api.model.vo.ProductFeaturesVO;
import com.medusa.gruul.goods.service.model.param.ProductFeaturesParam;
import com.medusa.gruul.goods.service.mp.service.IProductFeatureService;
import com.medusa.gruul.goods.service.service.ManagerProductFeatureService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 管理端产品特性数据实现层
 *
 * @author xiaoq
 * @Description ManagerProductFeatureServiceImpl.java
 * @date 2023-06-15 14:11
 */
@Service
@RequiredArgsConstructor
public class ManagerProductFeatureServiceImpl implements ManagerProductFeatureService {

    private final IProductFeatureService productFeatureService;

    @Override
    public void saveProductFeature(ProductFeaturesCreateDTO dto) {
        ProductFeatures productFeatures = new ProductFeatures();
        BeanUtil.copyProperties(dto, productFeatures);

        if (productFeatures.getFeaturesType() == FeaturesType.ATTRIBUTE) {
            checkProductFeature(productFeatures);
        }
        Long count = productFeatureService.lambdaQuery().eq(ProductFeatures::getFeaturesType, productFeatures.getFeaturesType()).count();
        count++;
        productFeatures.setSort(count.intValue());
        productFeatureService.save(productFeatures);
    }

    @Override
    public void updateProductFeature(ProductFeaturesModifyDTO dto) {
        ProductFeatures productFeatures = new ProductFeatures();
        BeanUtil.copyProperties(dto, productFeatures);
        checkProductFeature(productFeatures);
        ProductFeatures newProductFeatures = productFeatureService.lambdaQuery()
                .eq(ProductFeatures::getFeaturesType, productFeatures.getFeaturesType())
                .eq(BaseEntity::getId, productFeatures.getId()).one();
        GoodsError.REAL_DISTRIBUTION_CHECK_ERROR.trueThrow(newProductFeatures == null);
        productFeatureService.updateById(productFeatures);
    }

    /**
     * 删除产品特性信息
     *
     * @param ids          产品特性ids
     * @param featuresType 产品特性类型
     */
    @Override
    public void delProductFeature(List<Long> ids, FeaturesType featuresType) {
        boolean remove = productFeatureService.lambdaUpdate()
                .eq(ProductFeatures::getFeaturesType, featuresType)
                .in(BaseEntity::getId, ids)
                .remove();
        GoodsError.PRODUCT_FEATURE_DELETED_FAIL.falseThrow(remove);
    }

    @Override
    public IPage<ProductFeaturesVO> getProductFeatureList(ProductFeaturesParam param) {
        return productFeatureService.getProductFeatureList(param);
    }


    private void checkProductFeature(ProductFeatures productFeatures) {
        if (productFeatures.getFeaturesType() == FeaturesType.ATTRIBUTE) {
            ProductFeaturesValueDTO productFeaturesValue = productFeatures.getFeaturesValue();
            //校验数据
            Boolean isRequired = productFeaturesValue.getIsRequired();
            Boolean isMultiSelect = productFeaturesValue.getIsMultiSelect();
            GoodsError.PARAMETER_ERROR_CHECK_REQUIRED.trueThrow(isMultiSelect == null || isRequired == null);
            boolean flag = productFeaturesValue.getFeatureValues().stream().anyMatch(featureValue -> featureValue.getSecondValue() == null);
            GoodsError.PARAMETER_ERROR_CHECK_REQUIRED.trueThrow(flag);
        }
    }

}
