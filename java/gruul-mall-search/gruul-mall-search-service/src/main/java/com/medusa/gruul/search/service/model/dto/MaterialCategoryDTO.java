package com.medusa.gruul.search.service.model.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * @author 张治保
 * @since 2023/9/23
 */
@Getter
@Setter
@ToString
@Accessors(chain = true)
public class MaterialCategoryDTO implements Serializable {

    /**
     * 父级分类 id,一级分类的这个值为空
     */
    private String parentId;

    /**
     * 分类名称 长度为 8
     */
    @NotBlank
    @Size(min = 1, max = 8)
    private String name;

}
