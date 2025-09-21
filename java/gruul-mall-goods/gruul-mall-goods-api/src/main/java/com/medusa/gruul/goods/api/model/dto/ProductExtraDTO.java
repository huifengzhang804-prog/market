package com.medusa.gruul.goods.api.model.dto;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.map.MapUtil;
import com.medusa.gruul.common.model.constant.CommonPool;
import com.medusa.gruul.goods.api.model.CategoryLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 产品额外数据DTO
 *
 * @author xiaoq
 * @Description ProductExtraDTO.java
 * @date 2023-06-15 15:26
 */
@Getter
@Setter
@ToString
@Accessors(chain = true)
public class ProductExtraDTO implements Serializable {

    /**
     * 店铺类目信息
     */
    private CategoryLevel shopCategory;


    /**
     * 平台类目信息
     */
    private CategoryLevel platformCategory;


    /**
     * 自定义折扣百分比
     */
    private Long customDeductionRatio;


    /**
     * 违规信息
     */
    private ProductViolationDTO productViolation;

    /**
     * 产品属性
     */
    private List<ProductFeaturesValueDTO> productAttributes;

    /**
     * 产品参数
     */
    private List<ProductFeaturesValueDTO> productParameters;

    /**
     * 价格设置
     */
    private ConsignmentPriceSettingDTO consignmentPriceSetting;


    /**
     * 供应商自定义折扣百分比
     */
    private Long supplierCustomDeductionRatio;


    /**
     * 提交时间
     */
    private LocalDateTime submitTime;

    /**
     * 审核时间
     */
    private LocalDateTime auditTime;

    //如果是代销商品的话 则存在铺货员信息
    /**
     * 铺货员ID
     */
    private Long deliveryUserId;
    /**
     * 铺货员名称
     */
    private String deliveryUserName;
    /**
     * 铺货员手机号
     */
    private String deliveryUserPhone;

    /**
     * 校验产品属性快照是否正常
     *
     * @param userSelectedForm 用户选择的属性表单
     * @return 产品关联快照
     */
    public Set<ProductFeaturesValueDTO> checkProductAttributes(Map<Long, Set<Long>> userSelectedForm) {
        List<ProductFeaturesValueDTO> productAttributes = getProductAttributes();
        if (CollUtil.isEmpty(productAttributes)) {
            return Set.of();
        }
        //result
        Set<ProductFeaturesValueDTO> result = CollUtil.newHashSet();
        // 用户选择的输入表单
        Map<Long, Set<Long>> finalUserSelectedForm = MapUtil.emptyIfNull(userSelectedForm);
        // 遍历产品属性
        for (ProductFeaturesValueDTO attribute : productAttributes) {
            Set<FeatureValueDTO> featureValues;
            // 如果属性为空或 者 选择的特性值为空
            if (attribute == null || CollUtil.isEmpty(featureValues = attribute.validAndGetValues(finalUserSelectedForm.get(attribute.getId())))) {
                continue;
            }
            result.add(
                    new ProductFeaturesValueDTO()
                            .setId(attribute.getId())
                            .setFeatureName(attribute.getFeatureName())
                            .setIsRequired(attribute.getIsRequired())
                            .setIsMultiSelect(attribute.getIsMultiSelect())
                            .setFeatureValues(featureValues)
            );
        }
        return result;
    }

    /**
     * 获取选择的商品属性的总额
     *
     * @param userSelectedForm 用户选择的商品属性
     * @return 选择的商品属性的总额
     */
    public long attributesAmount(Map<Long, Set<Long>> userSelectedForm) {
        Set<ProductFeaturesValueDTO> values = checkProductAttributes(userSelectedForm);
        if (CollUtil.isEmpty(values)) {
            return CommonPool.NUMBER_ZERO;
        }
        long result = CommonPool.NUMBER_ZERO;
        for (ProductFeaturesValueDTO value : values) {
            Set<FeatureValueDTO> featureValues = value.getFeatureValues();
            if (CollUtil.isEmpty(featureValues)) {
                continue;
            }
            for (FeatureValueDTO featureValue : featureValues) {
                result += featureValue.getSecondValue();
            }
        }
        return result < 0 ? CommonPool.NUMBER_ZERO : result;
    }


}
