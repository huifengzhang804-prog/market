package com.medusa.gruul.goods.api.model.vo;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;

/**
 * 类目等级name
 *
 * @author xiaoq
 * @Description CategoryLevelName.java
 * @date 2023-09-26 14:05
 */

@Getter
@Setter
@ToString
public class CategoryLevelName implements Serializable {

    /**
     * 一级分类name
     */
    @NotNull
    private String oneName;

    /**
     * 二级分类name
     */
    @NotNull
    private String twoName;

    /**
     * 三级分类id
     */
    @NotNull
    private String threeName;
}
