package com.medusa.gruul.goods.api.model.dto;

import jakarta.validation.constraints.NotNull;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.io.Serializable;

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
public class ProductFeaturesValueModifyDTO extends ProductFeaturesValueCreateDTO implements Serializable {

    //    @NotNull
    private Long id;
}
