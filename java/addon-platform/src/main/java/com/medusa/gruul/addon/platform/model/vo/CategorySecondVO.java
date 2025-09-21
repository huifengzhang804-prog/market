package com.medusa.gruul.addon.platform.model.vo;

import com.medusa.gruul.common.custom.aggregation.classify.enums.CategoryLevel;
import lombok.Data;

import java.util.List;

/**
 * @Description: 类目二级分类Vo
 * @Author: xiaoq
 * @Date : 2022-04-18 14:49
 */
@Data
public class CategorySecondVO {

    private Long id;

    /**
     * 分类名称
     */
    private String name;

    /**
     * 上机分类的编号：0表示一级分类
     */
    private Long parentId;

    /**
     * 分类级别：0->1级；1->2级
     */
    private CategoryLevel level;

    /**
     * 分类id(数据传输用)
     */
    private Long categoryId;

    /**
     * 二级类目扣除占比
     */
    private Long deductionRatio;

    /**
     * 排序
     */
    private Integer sort;

    private String categoryImg;

    /**
     * 商品数量
     */
    private Long productNumber;

    /**
     * 二级分类
     */
    private List<CategoryThirdlyVO> categoryThirdlyVos;

}
