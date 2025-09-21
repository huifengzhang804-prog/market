package com.medusa.gruul.search.service.mp.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.medusa.gruul.common.mp.model.BaseEntity;

import com.medusa.gruul.search.api.enums.BrandStatus;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

/**
 * 品牌
 *
 * @author WuDi
 * @since 2023-02-02
 */
@Getter
@Setter
@TableName("t_search_brand")
@Accessors(chain = true)
public class SearchBrand extends BaseEntity {

    private static final long serialVersionUID = 1L;
    /**
     * 品牌名称
     */
    private String brandName;

    /**
     * 品牌logo
     */
    private String brandLogo;

    /**
     * 品牌描述
     */
    private String brandDesc;

    /**
     * 检索首字母
     */
    private String searchInitials;

    /**
     * 上级类目/上级分类
     */
    private Long parentCategoryId;

    /**
     * 关注人数
     */
    private Integer followers;

    /**
     * 状态(默认上架，0--下架 1--上架)
     */
    private BrandStatus status;

    /**
     * 排序
     */
    private Integer sort;
}
