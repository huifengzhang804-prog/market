package com.medusa.gruul.search.service.model.dto;

import com.alibaba.fastjson2.annotation.JSONField;
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
public class MaterialCategoryNameDTO implements Serializable {


    /**
     * 分类 id
     */
    @JSONField(serialize = false, deserialize = false)
    private String id;

    /**
     * 分类名称 长度为 8
     */
    @NotBlank
    @Size(min = 1, max = 8)
    private String name;

}
