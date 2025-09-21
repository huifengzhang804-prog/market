package com.medusa.gruul.search.service.model.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * @author 张治保
 * @since 2023/9/25
 */
@Getter
@Setter
@ToString
@Accessors(chain = true)
public class MaterialSearchSuggestDTO implements Serializable {

    /**
     * 分类 id 为空时查询全部
     */
    private String categoryId;
}
