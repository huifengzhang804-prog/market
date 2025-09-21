package com.medusa.gruul.goods.service.model.param;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.medusa.gruul.goods.api.model.enums.FeaturesType;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;


/**
 * 产品特性 param
 *
 * @author xiaoq
 * @Description ProductFeaturesParam.java
 * @date 2023-06-15 18:38
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class ProductFeaturesParam extends Page<Object> {
    /**
     * 名称
     */
    private String name;

    /**
     * 产品特性type
     */
    @NotNull
    private FeaturesType featuresType;
}
