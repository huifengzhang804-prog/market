package com.medusa.gruul.goods.service.model.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;


/**
 * @author 张治保
 * date 2022/4/24
 */
@Getter
@Setter
@ToString
public class CategoryNameImgDTO {

    /**
     * 分类名称
     */
    @NotBlank
    private String name;

    /**
     * 图片
     */
    private String categoryImg;
}
