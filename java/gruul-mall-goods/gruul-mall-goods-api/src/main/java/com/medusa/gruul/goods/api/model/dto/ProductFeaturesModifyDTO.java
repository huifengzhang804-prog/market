package com.medusa.gruul.goods.api.model.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.io.Serial;
import java.io.Serializable;

/**
 * ProductFeaturesModifyDTO
 *
 * @author Andy.Yan
 */
@Getter
@Setter
@ToString
@Accessors(chain = true)
@EqualsAndHashCode
public class ProductFeaturesModifyDTO extends ProductFeaturesCreateDTO implements Serializable {
    @Serial
    private static final long serialVersionUID = -271162497502393713L;

    @NotNull
    private Long id;

    /**
     * 业务处理字段
     */
    @Valid
    private ProductFeaturesValueModifyDTO featuresValue;

}
