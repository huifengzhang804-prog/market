package com.medusa.gruul.goods.api.model.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.*;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * 特性值
 *
 * @author xiaoq
 * @Description FeatureValueDTO.java
 * @date 2023-06-15 10:06
 */
@Getter
@Setter
@ToString
@Accessors(chain = true)
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
public class FeatureValueDTO implements Serializable {

    /**
     * 唯一值id
     */
    private Long featureValueId;

    /**
     * 第一个值 属性值 or 参数值
     */
    @NotBlank
    private String firstValue;

    /**
     * 第二个值 属性加价值
     */
    private Long secondValue;


}
