package com.medusa.gruul.addon.platform.model.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;


/**
 * @Description:
 * @Author: xiaoq
 * @Date : 2022-04-24 18:02
 */
@Getter
@Setter
@ToString
public class PlatformCategoryNameImgDTO {
    /**
     * 分类名称
     */
    @NotBlank(message = "分类名称不能为空")
    private String name;


    /**
     * 二级类目扣除比例
     */
    private Long deductionRatio;

    /**
     * 图片
     */
    private String categoryImg;
}
