package com.medusa.gruul.search.service.model.vo;

import com.medusa.gruul.common.custom.aggregation.classify.enums.CategoryLevel;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.util.List;

@Getter
@Setter
@ToString
@Accessors(chain = true)
public class SearchBrandDetailVO {

    /**
     * 品牌id
     */
    private String id;

    /**
     * 品牌名称
     */
    private String brandName;

    /**
     * 品牌描述
     */
    private String brandDesc;

    /**
     * 品牌logo
     */
    private String brandLogo;

    /**
     * 关注人数
     */
    private Long followers;

    /**
     * 是否关注
     */
    private Boolean isFollow;


    /**
     *l 所属类目或分类
     */
    private List<CategoryVO> categoryVos;

    @Data
    public static class CategoryVO {

        private Long id;

        private Long parentId;

        private CategoryLevel categoryLevel;


    }
}
