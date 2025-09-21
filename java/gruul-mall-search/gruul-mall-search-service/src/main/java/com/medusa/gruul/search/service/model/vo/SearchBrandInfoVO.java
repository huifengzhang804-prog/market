package com.medusa.gruul.search.service.model.vo;

import com.medusa.gruul.search.api.enums.BrandStatus;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

/**
 * 品牌基本信息
 */
@Getter
@Setter
@ToString
@Accessors(chain = true)
public class SearchBrandInfoVO {

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
     * 状态(默认上架，0--下架 1--上架)
     */
    private BrandStatus status;

    /**
     * 是否关注
     */
    private Boolean isFollow;

}
