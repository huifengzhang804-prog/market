package com.medusa.gruul.search.api.model;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * 分类 /平台类目/店铺分类
 *
 * @author 张治保
 * date 2022/12/22
 */
@Getter
@Setter
@Accessors(chain = true)
@ToString
public class NestedCategory implements Serializable {

    /**
     * 一级分类id
     */
    private Long one;

    /**
     * 二级分类id
     */
    private Long two;

    /**
     * 三级分类id
     */
    private Long three;
}
