package com.medusa.gruul.search.service.mp.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.medusa.gruul.common.custom.aggregation.classify.enums.CategoryLevel;
import com.medusa.gruul.common.mp.model.BaseEntity;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.util.List;

/**
 * @author : xiaoq
 * @description: 平台类目
 * @date : 2022-04-18 14:34
 */
@Getter
@Setter
@Accessors(chain = true)
@TableName("t_platform_category")
public class PlatformCategory extends BaseEntity {

    /**
     * 上机分类的编号：0表示一级分类
     */
    @TableField("parent_id")
    private Long parentId;

    /**
     * 分类名称
     */
    @NotBlank
    @Size(max = 64)
    @TableField("name")
    private String name;


    /**
     * 分类级别：0->1级；1->2级
     */
    @NotNull
    @TableField("level")
    private CategoryLevel level;

    /**
     * 分类排序
     */
    @TableField("sort")
    private Integer sort;

    /**
     * 三级分类图片
     */
    @TableField("category_img")
    private String categoryImg;


    @TableField(exist = false)
    private List<PlatformCategory> children;
}
