package com.medusa.gruul.goods.api.model.dto;

import cn.hutool.core.collection.CollUtil;
import com.medusa.gruul.common.model.constant.CommonPool;
import com.medusa.gruul.goods.api.model.enums.GoodsError;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 商品特性值DTO
 *
 * @author xiaoq
 * @Description ProductFeaturesValueDTO.java
 * @date 2023-06-15 09:16
 */
@Getter
@Setter
@ToString
@Accessors(chain = true)
@EqualsAndHashCode
public class ProductFeaturesValueCreateDTO implements Serializable {

    /**
     * 特性名称
     */
    @NotBlank(message = "特性名称不能为空")
    private String featureName;


    /**
     * 是否必选
     */
    @NotNull
    private Boolean isRequired;


    /**
     * 是否多选
     */
    @NotNull
    private Boolean isMultiSelect;

    /**
     * 特性值
     */
    @Valid
    private Set<FeatureValueDTO> featureValues;

    /**
     * 获取特性值map
     *
     * @return java.util.Map<java.lang.Long, com.medusa.gruul.goods.api.model.dto.FeatureValueDTO> 特性值map
     */
    public Map<Long, FeatureValueDTO> toFeatureValueMap() {
        return CollUtil.emptyIfNull(getFeatureValues())
                .stream()
                .collect(Collectors.toMap(FeatureValueDTO::getFeatureValueId, v -> v));
    }

    /**
     * 校验表单并获取属性特性值
     *
     * @param formIds 表单 id
     * @return java.util.Set<com.medusa.gruul.goods.api.model.dto.FeatureValueDTO> 属性特性值
     */
    public Set<FeatureValueDTO> validAndGetValues(Set<Long> formIds) {
        formIds = CollUtil.emptyIfNull(formIds);
        // 1. 是否必选   如果必选 size 应大于0
        if (getIsRequired() && CollUtil.isEmpty(formIds)) {
            throw GoodsError.PRODUCT_ATTRIBUTES_REQUIRED_CHECK_FAIL.exception(getFeatureName());
        }
        // 2. 是否多选  如果非多选 size 应小于等于1
        if ((!getIsMultiSelect() && formIds.size() > CommonPool.NUMBER_ONE)) {
            throw GoodsError.PRODUCT_ATTRIBUTES_RADIO_CHECK_FAIL.exception(getFeatureName());
        }
        Set<FeatureValueDTO> productValues = getFeatureValues();
        if (CollUtil.isEmpty(formIds) || CollUtil.isEmpty(productValues)) {
            return Set.of();
        }
        Map<Long, FeatureValueDTO> featureValueMap = toFeatureValueMap();
        return formIds.stream()
                .filter(featureValueMap::containsKey)
                .map(featureValueMap::get)
                .collect(Collectors.toSet());
    }

}
