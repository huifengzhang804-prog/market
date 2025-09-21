package com.medusa.gruul.search.service.model.vo;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

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
public class MaterialSearchSuggestVO implements Serializable {

    /**
     * 可选素材所属分类名称
     */
    private List<String> categoryName;

    /**
     * 可选素材所属分类id
     */
    private List<String> categoryId;

    /**
     * 可选素材尺寸列表
     */
    private List<String> format;

    /**
     * 可选素材尺寸列表
     */
    private List<String> size;

    /**
     * 分类信息
     */
    private List<MaterialSearchSuggestDestailVO> category;
}
