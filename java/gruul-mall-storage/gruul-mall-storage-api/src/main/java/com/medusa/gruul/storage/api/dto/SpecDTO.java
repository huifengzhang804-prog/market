package com.medusa.gruul.storage.api.dto;


import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.validator.constraints.Length;

import java.io.Serializable;

/**
 * 规格信息
 *
 * @author 张治保
 */
@Getter
@Setter
@ToString
public class SpecDTO implements Serializable {
    /**
     * 规格名称
     */
    @NotBlank
    @Length(max = 64, message = "规格值长度不能超过28个字符")
    private String name;
    /**
     * 排序
     */
    private Integer order;
}