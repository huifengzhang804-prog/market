package com.medusa.gruul.search.service.model.vo;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * 素材搜索建议
 *
 * @author 张治保
 * @since 2023/9/25
 */
@Getter
@Setter
@ToString
@Accessors(chain = true)
public class MaterialSearchSuggestDestailVO implements Serializable {

    /**
     * 素材分类名称
     */
    private String categoryName;

    /**
     * 素材分类id
     */
    private String categoryId;
}
