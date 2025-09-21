package com.medusa.gruul.goods.api.model;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;

/**
 * @author 张治保
 * date 2022/12/1
 */
@Getter
@Setter
@ToString
public class CategoryLevel implements Serializable {

    /**
     * 一级分类id
     */
    @NotNull
    private Long one;

    /**
     * 二级分类id
     */
    @NotNull
    private Long two;

    /**
     * 三级分类id
     */
    @NotNull
    private Long three;


    public boolean areFieldsNonNull() {
        return one == null || two == null || three == null;
    }
}
