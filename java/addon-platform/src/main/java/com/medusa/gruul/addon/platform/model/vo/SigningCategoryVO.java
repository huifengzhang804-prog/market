package com.medusa.gruul.addon.platform.model.vo;

import lombok.Data;

/**
 * 签约类目VO
 *
 * @author xiaoq
 * @Description SigningCategoryVO.java
 * @date 2023-05-15 14:02
 */
@Data
public class SigningCategoryVO {

    private Long id;

    /**
     * 上级类目id  平台一级类目
     */
    private Long parentId;

    /**
     * 平台一级类目名称
     */
    private String parentName;


    /**
     * 当前类目id 平台二级类目
     */
    private Long currentCategoryId;


    /**
     * 平台二级类目名称
     */
    private String currentCategoryName;
    /**
     * 店铺id
     */
    private Long shopId;

    /**
     * 自定义扣除比例
     */
    private Long customDeductionRatio;

    /**
     * 扣除比例
     */
    private Long deductionRatio;

    private String imageUrl;


}
