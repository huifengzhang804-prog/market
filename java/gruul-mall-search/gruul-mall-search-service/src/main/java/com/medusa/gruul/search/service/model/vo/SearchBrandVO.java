package com.medusa.gruul.search.service.model.vo;

import com.medusa.gruul.search.api.enums.BrandStatus;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;

@Getter
@Setter
@ToString
@Accessors(chain = true)
public class SearchBrandVO {

    /**
     * 品牌id
     */
    private Long id;


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

    /**
     * 创建时间
     */
    private LocalDateTime createTime;



}
