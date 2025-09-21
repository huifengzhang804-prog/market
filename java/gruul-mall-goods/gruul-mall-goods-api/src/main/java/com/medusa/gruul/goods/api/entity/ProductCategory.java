package com.medusa.gruul.goods.api.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.medusa.gruul.common.custom.aggregation.classify.enums.CategoryLevel;
import com.medusa.gruul.common.mp.model.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.util.List;

/**
 * 商品分类表
 *
 * @author xiaoq
 * @since 2022-03-04
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@TableName("t_product_category")
public class ProductCategory extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * 上级分类的编号：0表示一级分类
     */
    private Long parentId;

    /**
     * 分类名称
     */
    private String name;

    /**
     * 分类级别
     */
    private CategoryLevel level;

    /**
     * 排序
     */
    private Integer sort;
    /**
     * 图片  只有level为三级时 不为空
     */
    private String categoryImg;


    @TableField(exist = false)
    private List<ProductCategory> children;

    @TableField(exist = false)
    private Long productNumber;

}
