package com.medusa.gruul.goods.api.model.dto;

import com.medusa.gruul.goods.api.model.enums.FeaturesType;
import jakarta.validation.Valid;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.io.Serial;
import java.io.Serializable;

/**
 * ProductFeaturesCreateDTO
 *
 * @author Andy.Yan
 */
@Getter
@Setter
@ToString
@Accessors(chain = true)
@EqualsAndHashCode
public class ProductFeaturesCreateDTO implements Serializable {
    @Serial
    private static final long serialVersionUID = -271162497502393713L;

    /**
     * 店铺id
     */
    private Long shopId;

    /**
     * 特性type
     */
    private FeaturesType featuresType;

    /**
     * 排序值
     */
    private Integer sort;

    /**
     * 业务处理字段
     */
    @Valid
    private ProductFeaturesValueCreateDTO featuresValue;

}
