package com.medusa.gruul.shop.api.model.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;

/**
 *  签约类目DTO
 *
 * @author xiaoq
 * @Description  SigningCategoryDTO.JAVA
 * @date 2023-05-13 09:30
 */
@Getter
@Setter
@ToString
public class SigningCategoryDTO implements Serializable {
    private static final long serialVersionUID = -5441749920695254908L;
    /**
     * 签约类目id
     */
    private Long id;

    /**
     * 上级类目id  平台一级类目
     */
    private Long parentId;


    /**
     * 当前类目id 平台二级类目
     */
    private Long currentCategoryId;



    /**
     * 自定义扣除比例
     */
    private Long customDeductionRatio;

}
