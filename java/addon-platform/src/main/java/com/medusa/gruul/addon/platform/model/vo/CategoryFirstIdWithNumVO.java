package com.medusa.gruul.addon.platform.model.vo;

import lombok.Data;

/**
 * @author WuDi
 * date: 2022/11/4
 */
@Data
public class CategoryFirstIdWithNumVO {


    /**
     * 一级平台分类id
     */
    private Long platformCategoryFirstId;
    /**
     * 一级平台分类名称
     */
    private String platformCategoryFirstName;
    /**
     * 商品数量
     */
    private Integer productNum;

    /**
     * 销量
     */
    private Long salesCount;
}
